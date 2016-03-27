# Tracked Future

##  Overview

  Future, which when started - collect stack trace of origin thread and when handle exception - merge one with stack trace of this exception.

  Also contains agent, which substitute the call of ```Future.apply``` to the call of ```TrackedFuture.apply```  in bytecode.

 Useful for debugging. 

## Usage

  *  publishLocal  tracked-future to you local repository

  *  add to project which you debug, dependency:
~~~ scala
libraryDependencies += "com.github.rssh" %% "trackedfuture" % "0.1"
~~~

  *  when debug, enable agent 
~~~scala
fork := true
javaOptions += s"""-javaagent:${System.getProperty("user.home")}/.ivy2/local/com.github.rssh/trackedfuture_2.11/0.1/jars/trackedfuture_2.11.jar"""
~~~


##  Results 

Let's look at the next code:
~~~scala
object Main
{

  def main(args: Array[String]):Unit =
  {
    val f = f0("222")
    try {
       val r = Await.result(f,10 seconds)
    } catch {
       // will print with f0 when agent is enabled
       case ex: Throwable => ex.printStackTrace
    }
  }

  def f0(x:String): Future[Unit] =
  {
    System.err.print("f0:");
    f1(x)
  }

  def f1(x: String): Future[Unit] =
   Future{
     throw new RuntimeException("AAA");
   }

}

~~~

With tracked future agent enabled, instead traces, which ends in top-level executor:

~~~
f0:java.lang.RuntimeException: AAA
  at trackedfuture.example.Main$$anonfun$f1$1.apply(Main.scala:30)
  at trackedfuture.example.Main$$anonfun$f1$1.apply(Main.scala:30)
  at trackedfuture.runtime.TrackedFuture$$anon$1.run(TrackedFuture.scala:21)
  at scala.concurrent.impl.ExecutionContextImpl$AdaptedForkJoinTask.exec(ExecutionContextImpl.scala:121)
  at scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
  at scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
  at scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
  at scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
~~~

you will see traces wich include information: from where future was started:

~~~
f0:java.lang.RuntimeException: AAA
  at trackedfuture.example.Main$$anonfun$f1$1.apply(Main.scala:30)
  at trackedfuture.example.Main$$anonfun$f1$1.apply(Main.scala:30)
  at trackedfuture.runtime.TrackedFuture$$anon$1.run(TrackedFuture.scala:21)
  at scala.concurrent.impl.ExecutionContextImpl$AdaptedForkJoinTask.exec(ExecutionContextImpl.scala:121)
  at scala.concurrent.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
  at scala.concurrent.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
  at scala.concurrent.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
  at scala.concurrent.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
  at java.lang.Thread.getStackTrace(Thread.java:1552)
  at trackedfuture.runtime.TrackedFuture$.apply(TrackedFuture.scala:13)
  at trackedfuture.runtime.TrackedFuture$.rapply(TrackedFuture.scala:39)
  at trackedfuture.runtime.TrackedFuture.rapply(TrackedFuture.scala) 
  at trackedfuture.example.Main$.f1(Main.scala:29)
  at trackedfuture.example.Main$.f0(Main.scala:25)
  at trackedfuture.example.Main$.main(Main.scala:13)
  at trackedfuture.example.Main.main(Main.scala)
~~~

## Additional Notes
 
If you want 'right' version, which don't need dependency, package ```ASM``` inside agent jar and cleanup frames - don't hesitate to submit pull request ;)

