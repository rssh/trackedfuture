package trackedfuture.example

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._
import java.util.concurrent.{Future => _,_}

import org.scalatest._
import org.scalatest.concurrent._



class MainCallSpec extends FlatSpec with AsyncAssertions
{

  val showException=false

  "MainCall" should "show origin method between future " in {
    callAndCheckMethod( Main.f0("AAA"), "f0")
  }

  "MainCall" should "show origin method with map " in {
    callAndCheckMethod( Main.f3("AAA"), "f3")
  }

  "MainCall" should "show origin method with flatMap " in {
    callAndCheckMethod( Main.fFlatMap0(), "fFlatMap0")
  }

  "MainCall" should "show origin method with filter " in {
    callAndCheckMethod( Main.fFilter0(), "fFilter0")
  }

  "MainCall" should "show origin method with withFilter " in {
    callAndCheckMethod( Main.withFilter0(), "withFilter0")
  }

  "MainCall" should "show origin method with collect " in {
    callAndCheckMethod( Main.fCollect0{case "bbb" => "ccc"}, "fCollect0")
  }

  "MainCall" should "show origin method with onComplete " in {
    var lastError: Option[Throwable] = None 
    val ec = ExecutionContext.fromExecutor(
                Executors.newFixedThreadPool(1),
                e=>lastError=Some(e)
             )
    Main.fOnComplete0(ec)
    Thread.sleep(100)
    assert(lastError.isDefined)
    assert(checkMethod("fOnComplete0",lastError.get))
  }

  "MainCall" should "show origin method with foreach" in {
    var lastError: Option[Throwable] = None 
    val ec = ExecutionContext.fromExecutor(
                Executors.newFixedThreadPool(1),
                e=>lastError=Some(e)
    )
    Main.fForeach0(ec)
    Thread.sleep(100)
    assert(lastError.isDefined)
    assert(checkMethod("fForeach0",lastError.get))
  }

  "MainCall" should "show origin method with transform " in {
    callAndCheckMethod( Main.fTransform0(), "fTransform0")
  }

  "MainCall" should "show origin method with reccover " in {
    callAndCheckMethod( Main.fRecover0(), "fRecover0")
  }


  private def callAndCheckMethod(body: =>Future[_],method:String): Unit = {
    val f = body
    val w = new Waiter
    f onComplete {
       case Failure(ex) => 
                           val checked = checkMethod(method,ex)
                           w{ assert(checked) }
                           w.dismiss()
       case _ => w{ assert(false) }
                 w.dismiss()
    }
    w.await{timeout(10 seconds)}
  }

  private def checkMethod(method:String, ex: Throwable): Boolean = {
    if (showException) ex.printStackTrace()
    ex.getStackTrace.toSeq.find( _.getMethodName == method ).isDefined
  }


}
