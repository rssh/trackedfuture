package trackedfuture.example

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

import org.scalatest._
import org.scalatest.concurrent._



class MainCallSpec extends FlatSpec with AsyncAssertions
{

  "MainCall" should "show origin method between future " in {
    val f = Main.f0("AAA")
    val w = new Waiter
    f onComplete {
       case Failure(ex) => 
                           val checked = checkMethod("f0",ex)
                           w{ assert(checked) }
                           w.dismiss()
       case _ => w{ assert(false) }
                 w.dismiss()
    }
    w.await{timeout(10 seconds)}
  }

  private def checkMethod(method:String, ex: Throwable): Boolean = {
    ex.printStackTrace()
    ex.getStackTrace.toSeq.find( _.getMethodName == method ).isDefined
  }


}
