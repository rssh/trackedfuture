package trackedfuture.runtime

class StackTraces(elements: Array[StackTraceElement], prev: StackTraces = null)
{
 
  def  depth(): Int =
  {
    val prevDepth = if (prev eq null) 0 else prev.depth()
    prevDepth + elements.length
  }

  def  toTrace(): Array[StackTraceElement] =
  {
    val trace = new Array[StackTraceElement](depth())
    fill(trace,0)
    trace
  }

  def  fill(trace: Array[StackTraceElement], startIndex:Int):Int =
  {
    val nextIndex = startIndex + elements.length
    System.arraycopy(elements,0,trace,startIndex,elements.length)
    //TODO: think - how it's needed,
    //val inserted = createSeparatorStackTraceElement();
    if (prev eq null) {
               nextIndex 
    } else {
               prev.fill(trace, nextIndex)
    }
  }

}


