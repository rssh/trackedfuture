package trackedfuture

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

object SimpleFutureRun
{

  def main(args:Array[String]):Unit =
  {
    val f = Future{
     System.out.println("from future:")
     throw new RuntimeException("qqqq");
    }
    System.out.println("Here")
    
  }


}
