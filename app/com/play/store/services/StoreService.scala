package com.play.store.services

import java.util.UUID
import akka.actor.ActorRef
import com.google.inject.ImplementedBy
import com.play.store.actors.PlayStoreActor.AvailableProducts
import com.play.store.dao.ProductDAO
import com.play.store.errors.Errors.{CurrencyNotSupportedError, ProductNotAvailableError}
import com.play.store.models.Currency.Currency
import com.play.store.models.{Currency, Id, Money, Product}
import javax.inject.{Inject, Named}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.dbio.DBIOAction
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}
import scala.concurrent.duration._

import akka.pattern.ask
import akka.util.Timeout

@ImplementedBy(classOf[StoreServiceImpl])
trait StoreService {

  def getAllAvailableProducts(currency: Option[String]): Future[Seq[Product]]

  def reserveProduct(productId: Id[Product]): Future[String]

}

class StoreServiceImpl @Inject()(
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
      case None => db.run(productDAO.getAllAvailable())
    }
  }

  override def reserveProduct(productId: Id[Product]) = {

    UUID.randomUUID().toString
    ???
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
