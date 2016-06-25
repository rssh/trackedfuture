package trackedfuture.agent

import org.objectweb.asm._

class TFClassAdapter(up:ClassVisitor) extends ClassVisitor(Opcodes.ASM5,up)
{


  override def visitMethod(access: Int, name: String, desc: String, 
                           signature: String, exceptions: Array[String]): MethodVisitor =
  {
    val mv = up.visitMethod(access,name,desc,signature,exceptions)
    if (! (mv eq null) ) {
       new TFMethodAdapter(mv)
    } else {
       mv
    }
  }

}
