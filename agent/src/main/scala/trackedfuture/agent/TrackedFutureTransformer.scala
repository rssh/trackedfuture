package trackedfuture.agent

import java.lang.instrument._
import java.security._

import org.objectweb.asm._

class TrackedFutureTransformer extends ClassFileTransformer {

  override def transform(loader: ClassLoader, className: String, classBeingRedefined: Class[_],
                         protectionDomain: ProtectionDomain, classfileBuffer: Array[Byte]): Array[Byte] = {
    if (className.startsWith("java/")
      || className.startsWith("com/sun/")
      || className.startsWith("sun/")
      || className.startsWith("scala/")
      || className.startsWith("trackedfuture/runtime/")
    ) {
      //null to prevent memory leal
      classfileBuffer
    } else {
      //System.err.println("transforming class:"+className)
      val writer = new ClassWriter(0)
      val adapter = new TFClassAdapter(writer)
      val reader = new ClassReader(classfileBuffer)
      reader.accept(adapter, 0)
      writer.toByteArray()
    }
  }

}
