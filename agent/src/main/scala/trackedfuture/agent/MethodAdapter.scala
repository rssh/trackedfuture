package trackedfuture.agent

import org.objectweb.asm._

class MethodAdapter(up: MethodVisitor) extends MethodVisitor(Opcodes.ASM5, up) {


  val methodMapping = Map(
    (Opcodes.INVOKEVIRTUAL,
      "scala/concurrent/Future$", "apply",
      "(Lscala/Function0;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "rapply"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "map",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "rmap"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "flatMap",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "rFlatMap"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "filter",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "rFilter"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "withFilter",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "rFilter"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "collect",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "collect"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "onComplete",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)V"
      ) ->("trackedfuture/runtime/TrackedFuture", "onComplete"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "foreach",
      "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)V"
      ) ->("trackedfuture/runtime/TrackedFuture", "foreach"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "transform",
      "(Lscala/Function1;Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "transform"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "recover",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "recover"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "recoverWith",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;"
      ) ->("trackedfuture/runtime/TrackedFuture", "recoverWith"),
    (Opcodes.INVOKEINTERFACE,
      "scala/concurrent/Future", "andThen",
      "(Lscala/PartialFunction;Lscala/concurrent/ExecutionContext;)V"
      ) ->("trackedfuture/runtime/TrackedFuture", "andThen"),
    (Opcodes.INVOKEVIRTUAL,
      "scala/concurrent/Await$", "ready",
      "(Lscala/concurrent/Awaitable;Lscala/concurrent/duration/Duration;)Lscala/concurrent/Awaitable;"
      ) ->("trackedfuture/runtime/TrackedAwait", "ready"),
    (Opcodes.INVOKEVIRTUAL,
      "scala/concurrent/Await$", "result",
      "(Lscala/concurrent/Awaitable;Lscala/concurrent/duration/Duration;)Ljava/lang/Object;"
      ) ->("trackedfuture/runtime/TrackedAwait", "result")
  )

  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean): Unit = {
    methodMapping get(opcode, owner, name, desc) match {
      case Some(classMethodTuple) =>
        up.visitMethodInsn(Opcodes.INVOKESTATIC,
          classMethodTuple._1,
          classMethodTuple._2,
          appendParam(owner, desc),
          false)
      case None =>
        up.visitMethodInsn(opcode, owner, name, desc, itf)
    }
  }

  def appendParam(ownerType: String, desc: String): String =
    s"(L${ownerType};${desc.substring(1)}"

}
