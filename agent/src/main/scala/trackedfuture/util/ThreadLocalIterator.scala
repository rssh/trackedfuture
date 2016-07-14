package trackedfuture.util

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer

class ThreadLocalIterator[A](clazz: Class[A]) extends Iterable[A]{


  override def iterator: Iterator[A] = getInternalMap

  private def getInternalMap: Iterator[A] = {

    val bufferList = new ListBuffer[A]
    val threadField = classOf[Thread].getDeclaredField("inheritableThreadLocals")
    threadField.setAccessible(true)

    val threadLocalMapClass = Class.forName("java.lang.ThreadLocal$ThreadLocalMap")
    val tableField = threadLocalMapClass.getDeclaredField("table")
    tableField.setAccessible(true)

    for ((thread, stacks) <- Thread.getAllStackTraces.asScala.mapValues(_.toSet);
         tf = threadField.get(thread)
         if tf != null) {
      val table = tableField.get(tf)

      for (i <- 0 until java.lang.reflect.Array.getLength(table);
           entry = java.lang.reflect.Array.get(table, i)
           if entry != null) {
        val valueField = entry.getClass.getDeclaredField("value")
        valueField.setAccessible(true)
        val value = valueField.get(entry)

        if (value != null && value.getClass.isAssignableFrom(clazz)) {
          bufferList += value.asInstanceOf[A]
        }
      }
    }

    bufferList.toList.iterator
  }
}
