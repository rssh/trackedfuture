package trackedfuture.agent

import java.lang.instrument._

object Agent
{

  def premain(args: String, instr: Instrumentation):Unit =
  {
    System.err.println("add  transformer")
    instr.addTransformer(new TrackedFutureTransformer())
  }
 

}
