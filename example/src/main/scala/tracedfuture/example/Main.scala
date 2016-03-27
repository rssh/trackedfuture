package trackedfuture.example

import scala.concurrent._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Main
{

  def main(args: Array[String]):Unit =
  {
    val f = f0("222")
    try {
       val r = Await.result(f,10 seconds)
    } catch {
       // will print with f0 when agent is enabled
       case ex: Throwable => ex.printStackTrace
    }
  }

  def f0(x:String): Future[Unit] =
  {
    System.err.print("f0:");
    f1(x)
  }

  def f1(x: String): Future[Unit] =
   Future{
     System.err.println(s"f1(${x})")
     throw new RuntimeException("AAA");
   }

}
