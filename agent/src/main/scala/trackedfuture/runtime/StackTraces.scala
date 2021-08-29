package trackedfuture.runtime

import scala.concurrent.Future

class StackTraces(elements: Array[StackTraceElement], prev: StackTraces = null) {

  private var currentFuture: Future[Unit] = _

  def getCurrentFuture[T]: Future[Unit] = currentFuture

  def setCurrentFuture(f: Future[Unit]): Unit = {
    currentFuture = f
  }

  def depth(): Int = {
    val prevDepth = if (prev eq null) 0 else prev.depth()
    prevDepth + elements.length
  }

  def toTrace: Array[StackTraceElement] = {
    val trace = new Array[StackTraceElement](depth())
    fill(trace, 0)
    trace
  }

  def fill(trace: Array[StackTraceElement], startIndex: Int): Int = {
    val nextIndex = startIndex + elements.length
    System.arraycopy(elements, 0, trace, startIndex, elements.length)
    //TODO: think - how it's needed,
    //val inserted = createSeparatorStackTraceElement();
    if (prev eq null) {
      nextIndex
    } else {
      prev.fill(trace, nextIndex)
    }
  }

}


