package trackedfuture.runtime

import scala.concurrent._
import scala.util._
import scala.util.control._

object TrackedFuture
{

  def apply[T](body: =>T)(implicit executor: ExecutionContext):Future[T]=
  {
    //inline to avoid extra stack frame.
    //val prevTrace = ThreadTrace.retrieveCurrent()
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    val promise = Promise[T]()
    val runnable = new Runnable() {
           override def run(): Unit =
           {
            ThreadTrace.setPrev(prevTrace)
            try {
              val r = body
              promise success r
            } catch {
              case NonFatal(ex) =>
                       ex.setStackTrace(ThreadTrace.mergeWithPrev(ex.getStackTrace()))
                       promise failure ex
            }
           }
    }   
    executor.execute(runnable)
    promise.future
  }

  /**
   * this method generate static method in TrackedFuture which later can be substitutued
   * instead Future.apply in bytecode by agent.
   **/
  def rapply[T](unused: Future.type, body: =>T, executor: ExecutionContext):Future[T]=
        apply(body)(executor)

  def onComplete[T,U](future: Future[T], f: Try[T] => U)(implicit executor: ExecutionContext): Unit =
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.onComplete( x => trackedCall(f(x),prevTrace) )
  }

  def foreach[T](future: Future[T], f: T => Unit)(implicit executor: ExecutionContext): Unit =
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.foreach( x => trackedCall(f(x),prevTrace) )
  }

  def transform[T,S](future: Future[T],
                   s: T => S, f: Throwable => Throwable)(implicit executor: ExecutionContext): Future[S] = {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    //
    // note, that changign x => trackedCall(x,t) to trackedCall(_,t) change bytecode
    future.transform( x => trackedCall(s(x),prevTrace), x => trackedCall(f(x),prevTrace) )(executor)
  }
    


  def rmap[A,B](future: Future[A], function: A => B, executor: ExecutionContext):Future[B]=
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.map { a => trackedCall(function(a),prevTrace) }(executor)
  }

  def rFlatMap[A,B](future: Future[A], function: A => Future[B], executor: ExecutionContext):Future[B]=
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.flatMap{ a => trackedCall(function(a),prevTrace) }(executor)
  }

  def rFilter[A](future: Future[A], function: A => Boolean, executor: ExecutionContext):Future[A]=
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.map { a => trackedCall(
                        if (function(a)) a else 
                          throw new NoSuchElementException("Future.filter predicate is not satisfied")
                        ,
                        prevTrace) 
               }(executor)
  }

  def collect[A,B](future: Future[A], pf: PartialFunction[A,B], executor: ExecutionContext):Future[B]=
  {
    val trace = Thread.currentThread.getStackTrace()
    val prevTrace = new StackTraces(trace, ThreadTrace.prevTraces.value)
    future.map { a => trackedCall({
          pf.applyOrElse(a, (t: A) => throw new NoSuchElementException("Future.collect partial function is not defined at: " + t)) 
    }, prevTrace) }(executor)
  }



  private def trackedCall[A](body: =>A, prevTrace:StackTraces):A =
  {
    ThreadTrace.setPrev(prevTrace)
    try {
      body
    }catch{
      case NonFatal(ex) =>
        ex.setStackTrace(ThreadTrace.mergeWithPrev(ex.getStackTrace()))
        throw ex
    }
  }


}
