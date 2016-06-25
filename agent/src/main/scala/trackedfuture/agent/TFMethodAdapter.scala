package trackedfuture.agent

import org.objectweb.asm._

class TFMethodAdapter(up: MethodVisitor) extends MethodVisitor(Opcodes.ASM5, up) {


  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean): Unit = {
    substitutions get(opcode, owner, name, desc) match {
      case Some(trackedFutureMethod) =>
        up.visitMethodInsn(Opcodes.INVOKESTATIC,
          "trackedfuture/runtime/TrackedFuture",
          trackedFutureMethod,
          appendParam(owner, desc),
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
      ) -> "rFilter",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "collect",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) -> "collect",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "onComplete",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)V"
      ) -> "onComplete",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "foreach",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)V"
      ) -> "foreach",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "transform",
      "(Lscala/Function1;Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) -> "transform",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "recover",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) -> "recover",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "recoverWith",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) -> "recoverWith",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "onSuccess",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)V"
      ) -> "onSuccess",
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "onFailure",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)V"
      ) -> "onFailure"
  )

  def appendParam(ownerType: String, desc: String): String =
    s"(L${ownerType};${desc.substring(1)}"

}
