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
    } else {
       up.visitMethodInsn(opcode, owner, name, desc, itf)
    }
  }

}
