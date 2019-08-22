package com.play.store.services

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.ImplementedBy
import com.play.store.actors.PlayStoreActor.{AvailableProducts, ReserveProduct}
import com.play.store.dao.ProductDAO
import com.play.store.errors.Errors.{CantBuyProductError, CurrencyNotSupportedError, SuperStoreError}
import com.play.store.models.Currency.Currency
import com.play.store.models._
import javax.inject.{Inject, Named}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import com.play.store.actors.ShopkeeperActor.FinishReservation

@ImplementedBy(classOf[StoreServiceImpl])
trait StoreService {

  def getAllAvailableProducts(currency: Option[String]): Future[Seq[Product]]

  def reserveProduct(productId: Id[Product]): Future[ReservationOrder]

  def buyProduct(order: ReservationOrder) : Future[Id[Product]]

}

class StoreServiceImpl @Inject()(
  actorSystem: ActorSystem,
  productDAO: ProductDAO,
  exchangeService: ExchangeService,
  @Named("play-store-actor") playStoreActor: ActorRef,
  protected val dbConfigProvider: DatabaseConfigProvider)
  (implicit ec: ExecutionContext) extends StoreService with HasDatabaseConfigProvider[JdbcProfile] {

  implicit val timeout: Timeout = 5.seconds

  override def getAllAvailableProducts(currency: Option[String]) = {

    currency match {
      case Some(c) => for {
        newCurrency <- validateCurrency(c)
        products <- (playStoreActor ? AvailableProducts).mapTo[Seq[Product]]
        newProducts <- calculateNewPrices(products, newCurrency)
      } yield newProducts
      case None => (playStoreActor ? AvailableProducts).mapTo[Seq[Product]]
    }
  }

  override def reserveProduct(productId: Id[Product]) = {
    (playStoreActor ? ReserveProduct(productId)).mapTo[Either[SuperStoreError, ReservationOrder]].flatMap {
      case Right(reservation) => Future.successful(reservation)
      case Left(error) => Future.failed(error)
    }
  }

  override def buyProduct(order: ReservationOrder) = {
    actorSystem.actorSelection(s"/user/${playStoreActor.path.name}/${order.code}").resolveOne().flatMap { ref =>
      (ref ? FinishReservation).mapTo[Id[Product]]
    }.recoverWith {
      case _: Exception => Future.failed(CantBuyProductError(order.code))
    }
  }

  private def calculateNewPrices(products: Seq[Product], newCurrency: Currency) = {
    Future.traverse(products) { p =>
      exchangeService.convertAmount(p.price, newCurrency).map { newPrice =>
        p.copy(price = newPrice)
      }
    }
  }

  private def validateCurrency(currency: String) ={
    Try(Currency.withName(currency.toUpperCase)) match {
      case Success(c) => Future.successful(c)
      case Failure(_) => Future.failed(CurrencyNotSupportedError(currency))
    }
  }

}
