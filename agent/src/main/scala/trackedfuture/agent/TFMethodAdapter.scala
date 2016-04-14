package trackedfuture.agent

import org.objectweb.asm._

class TFMethodAdapter(up: MethodVisitor) extends MethodVisitor(Opcodes.ASM5, up)
{
  
  
  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean): Unit =
  {
    substitutions get (opcode,owner,name,desc) match {
      case Some(trackedFutureMethod) => 
              up.visitMethodInsn(Opcodes.INVOKESTATIC,
                                 "trackedfuture/runtime/TrackedFuture",
                                 trackedFutureMethod,
                                 appendParam(owner,desc),
                                 false)
      case None =>
              up.visitMethodInsn(opcode, owner, name, desc, itf)
    }
  }

  val substitutions = Map(
        (Opcodes.INVOKEVIRTUAL,
          "scala/concurrent/Future$", "apply", 
          "(Lscala/Function0;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
        ) -> "rapply",
        (Opcodes.INVOKEINTERFACE,
          "scala/concurrent/Future", "map", 
          "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
        ) -> "rmap",
        (Opcodes.INVOKEINTERFACE,
          "scala/concurrent/Future", "flatMap", 
          "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
        ) -> "rFlatMap",
        (Opcodes.INVOKEINTERFACE,
          "scala/concurrent/Future", "filter", 
          "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
        ) -> "rFilter",
        (Opcodes.INVOKEINTERFACE,
          "scala/concurrent/Future", "withFilter", 
          "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
        ) -> "rFilter"
  )

  def appendParam(ownerType: String, desc: String): String =
    s"(L${ownerType};${desc.substring(1)}"

}
