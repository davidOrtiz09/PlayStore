package com.play.store.actors

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import com.play.store.actors.PlayStoreActor.DeleteReservation
import com.play.store.actors.ShopkeeperActor.{CancelOrder, FinishReservation, StartTimer}
import com.play.store.models.{Id, Product}
import scala.concurrent.duration._


object ShopkeeperActor {
  case object StartTimer
  case object CancelOrder
  case object FinishReservation

  def props(productId: Id[Product]) = Props(new ShopkeeperActor(productId))
}
class ShopkeeperActor(productId: Id[Product]) extends Actor with ActorLogging {
  private val ttl = 30.seconds
  private val reservationCode = self.path.name

  import context.dispatcher

  def receive: Receive = {
    case StartTimer => startTime()
    case CancelOrder => cancelOrder()
    case FinishReservation =>
      log.info(s"Reservation for product ${productId.toString} has been completed")
      self ! PoisonPill
  }

  private def cancelOrder() = {
    log.info(s"Shopkeeper $reservationCode is cancelling order for product ${productId.toString} because ttl has been completed")
    context.parent ! DeleteReservation(productId)
    self ! PoisonPill
  }

  private def startTime() = {
    log.info(s"Shopkeeper's $reservationCode timer started at $ttl seconds")
    context.system.scheduler.scheduleOnce(ttl, self, CancelOrder)
  }

  override def preStart(): Unit = {
    log.info(s"Shopkeeper $reservationCode is alive")
    self ! StartTimer
  }


}
