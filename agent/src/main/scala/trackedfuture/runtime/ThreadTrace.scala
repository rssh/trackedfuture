package trackedfuture.runtime

import scala.util._

object ThreadTrace {

  val prevTraces = new DynamicVariable[StackTraces](null)

  def retrieveCurrent(): StackTraces = {
    val trace = Thread.currentThread.getStackTrace
    new StackTraces(trace, prevTraces.value)
  }

  def setPrev(st: StackTraces): Unit = {
    prevTraces.value = st
  }

  def mergeWithPrev(trace: Array[StackTraceElement]): Array[StackTraceElement] = {
    if (prevTraces.value eq null) {
      trace
    } else {
      new StackTraces(trace, prevTraces.value).toTrace
    }
  }

}
