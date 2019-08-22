package com.play.store.actors

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import akka.event.Logging
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
class ShopkeeperActor(productId: Id[Product]) extends Actor {
  private val logger = Logging(context.system, this)
  private val ttl = 30.seconds
  private val reservationCode = self.path.name

  import context.dispatcher

  def receive: Receive = {
    case StartTimer => startTime()
    case CancelOrder => cancelOrder()
    case FinishReservation => finishReservation()
  }

  private def finishReservation() = {
    logger.info(s"Reservation for product ${productId.toString} has been completed")
    self ! PoisonPill
    sender() ! productId
  }

  private def cancelOrder() = {
    logger.info(s"Shopkeeper $reservationCode is cancelling order for product ${productId.toString} because ttl has been completed")
    context.parent ! DeleteReservation(productId)
    self ! PoisonPill
  }

  private def startTime() = {
    logger.info(s"Shopkeeper's $reservationCode timer started at $ttl seconds")
    context.system.scheduler.scheduleOnce(ttl, self, CancelOrder)
  }

  override def preStart(): Unit = {
    logger.info(s"Shopkeeper $reservationCode is alive")
    self ! StartTimer
  }


}
