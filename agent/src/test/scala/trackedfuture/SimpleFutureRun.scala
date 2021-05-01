package trackedfuture

import java.util.concurrent.Executors

import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

object SimpleFutureRun
{

  def main(args:Array[String]):Unit =
  {

    val ec = ExecutionContext.fromExecutor(
      Executors.newFixedThreadPool(1)
    )

    val f = Future {
      Thread.sleep(10*1000)
    }

    System.out.println("Here")
    Await.ready(f, 1000 millis)

  }
}
