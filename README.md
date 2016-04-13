# Tracked Future

##  Overview


  Contains agent, which substitute in bytecode calls of 
  ```Future.apply''' ```map```, ```flatMap``` ```filter``` to tracked versions, which save origin caller stack.

   Ie. tracked version collect stack trace of origin thread when appropriative construction is created and then,
  when handle exception, merge one with stack trace of this exception; 

 Useful for debugging. 

## Usage

1.  publishLocal  tracked-future to you local repository

2.  when debug, enable agent 
~~~scala
fork := true
javaOptions += s"""-javaagent:${System.getProperty("user.home")}/.ivy2/local/com.github.rssh/trackedfuture_2.11/0.2/jars/trackedfuture_2.11.jar"""
~~~ scala

##  Results 

Let's look at the next code:
~~~scala
~~~

With tracked future agent enabled, instead traces, which ends in top-level executor:

~~~
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
 
If you want a version with more wrappend methods and frames cleanup - don't hesitate to submit pull request ;)

