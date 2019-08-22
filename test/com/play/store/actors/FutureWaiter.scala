package com.play.store.actors

import java.util.concurrent.TimeUnit
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

trait FutureWaiter {
  def waitForIt[T](f: Future[T], seconds: Int = 10): T = {
    Await.result(f, Duration(seconds, TimeUnit.SECONDS))
  }
}
