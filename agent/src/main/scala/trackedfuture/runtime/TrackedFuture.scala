package trackedfuture.runtime

import scala.concurrent._
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

}
