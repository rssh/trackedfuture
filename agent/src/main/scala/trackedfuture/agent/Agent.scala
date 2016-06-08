package trackedfuture.agent

import java.lang.instrument._

object Agent
{

  def premain(args: String, instr: Instrumentation):Unit =
  {
    System.err.println("class transformer added")
    instr.addTransformer(new TrackedFutureTransformer())
  }
 

}
