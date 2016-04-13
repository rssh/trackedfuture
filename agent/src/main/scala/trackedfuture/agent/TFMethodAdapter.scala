package trackedfuture.agent

import org.objectweb.asm._

class TFMethodAdapter(up: MethodVisitor) extends MethodVisitor(Opcodes.ASM5, up)
{
  
  override def visitMethodInsn(opcode: Int, owner: String, name: String, desc: String, itf: Boolean): Unit =
  {
    if (opcode == Opcodes.INVOKEVIRTUAL &&
        owner.equals("scala/concurrent/Future$") &&
        name.equals("apply") &&
        desc.equals("(Lscala/Function0;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;")
       ) { 
         up.visitMethodInsn(Opcodes.INVOKESTATIC,
                          "trackedfuture/runtime/TrackedFuture",
                          "rapply",
                          "(Lscala/concurrent/Future$;Lscala/Function0;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;", false)
    } else if (opcode == Opcodes.INVOKEINTERFACE &&
        owner.equals("scala/concurrent/Future")) {
        if ((name == "map") &&
            (desc == "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;")
           ) {
          up.visitMethodInsn(Opcodes.INVOKESTATIC,
                          "trackedfuture/runtime/TrackedFuture",
                          "rmap",
                          "(Lscala/concurrent/Future;Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;", false)
        } else if ((name == "flatMap") &&
                   (desc == "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;")
                   ) {
          up.visitMethodInsn(Opcodes.INVOKESTATIC,
                          "trackedfuture/runtime/TrackedFuture",
                          "rFlatMap",
                          "(Lscala/concurrent/Future;Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;", false)
        } else if ((name == "filter") &&
                   (desc == "(Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;")
                  ) {
          up.visitMethodInsn(Opcodes.INVOKESTATIC,
                          "trackedfuture/runtime/TrackedFuture",
                          "rFilter",
                          "(Lscala/concurrent/Future;Lscala/Function1;Lscala/concurrent/ExecutionContext;)Lscala/concurrent/Future;", false)
        } else
          up.visitMethodInsn(opcode, owner, name, desc, itf)
    } else {
       up.visitMethodInsn(opcode, owner, name, desc, itf)
    }
  }

}
