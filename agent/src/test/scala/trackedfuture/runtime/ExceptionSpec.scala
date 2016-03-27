package trackedfuture.runtime

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util._

import org.scalatest._
import org.scalatest.concurrent._



class ExceptionSpec extends FlatSpec with AsyncAssertions
{

  "TrackedFuture" should "show origin thread between when trowing exception" in {
    val callCodeLine = 20; //!!! - point of code [TODO: implement __LINE__ as macro]
    def middleFun(): Future[Unit] = {
      val x = 1
      TrackedFuture { //!!! here is callCodeLine
        val y = 2
        if (true) {
          throw new Exception("qqq")
        }
      }
    }
    val w = new Waiter
    middleFun() onComplete {
       case Failure(ex) => 
                           val checked = checkFL("ExceptionSpec.scala",callCodeLine,ex)
                           w{ assert(checked) }
                           w.dismiss()
       case _ => w{ assert(false) }
                 w.dismiss()
    }
    w.await{timeout(10 seconds)}
  }

  private def checkFL(fname:String, line:Int, ex:Throwable): Boolean = {
    ex.printStackTrace()
    ex.getStackTrace.toSeq.find{ ste =>
      (ste.getFileName() == fname
       ) && (
       line == -1 || ste.getLineNumber()==line
      )
    }.isDefined
  }


}
