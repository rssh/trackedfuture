package trackedfuture.runtime

import java.util.concurrent.TimeoutException

import trackedfuture.util.ThreadLocalIterator

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

/**
  * Created by ovoievodin on 14.06.2016.
  */

object TrackedAwait {
  def ready[T](awaitOriginal: Await.type, awaitable: Awaitable[T], atMost: Duration): awaitable.type = {
    try {
      awaitOriginal.ready(awaitable, atMost)
    } catch {
      case ex: TimeoutException => {
        ThreadTrace.setPrev(threadLocalTrace(awaitable))
        ex.setStackTrace(ThreadTrace.mergeWithPrev(ex.getStackTrace))
        throw ex
      }
    }
  }

  private def threadLocalTrace[T](awaitable: Awaitable[T]): StackTraces = {
    val trace = new ThreadLocalIterator[StackTraces](classOf[StackTraces]).iterator.find(_.getCurrentFuture == awaitable)
    if (trace == null) new StackTraces(Array()) else trace.get
  }

  def result[T](awaitOriginal: Await.type, awaitable: Awaitable[T], atMost: Duration): T = {
    try {
      awaitOriginal.result(awaitable, atMost)
    } catch {
      case ex: TimeoutException => {
        ThreadTrace.setPrev(threadLocalTrace(awaitable))
        ex.setStackTrace(ThreadTrace.mergeWithPrev(ex.getStackTrace))
        throw ex
      }
    }
  }
}
