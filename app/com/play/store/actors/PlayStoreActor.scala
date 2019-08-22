package com.play.store.actors

import java.util.UUID
import akka.actor.{Actor, ActorLogging}
import com.play.store.actors.PlayStoreActor.{AvailableProducts, DeleteReservation, LoadProducts, ReserveProduct}
import com.play.store.dao.ProductDAO
import com.play.store.errors.Errors.CantReserveProductError
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import com.play.store.models.{Id, Product, ReservationOrder}
import scala.concurrent.Future

object PlayStoreActor {
  case class ReserveProduct(id: Id[Product])
  case class DeleteReservation(id: Id[Product])
  case object LoadProducts
  case object AvailableProducts

}

class PlayStoreActor @Inject()(
  productDAO: ProductDAO,
  protected val dbConfigProvider: DatabaseConfigProvider) extends Actor with ActorLogging with HasDatabaseConfigProvider[JdbcProfile] {

  import context.dispatcher

  private var currentProducts: Map[Id[Product], Product] = Map.empty[Id[Product], Product]

  def receive = {
    case LoadProducts => loadProducts()
    case AvailableProducts => Future.successful(currentProducts.values.filter(_.quantity > 0))
    case ReserveProduct(id) => reserveProduct(id)
    case DeleteReservation(id) => deleteReservation(id)
  }

  private def deleteReservation(id: Id[Product]) = {
    log.info(s"Expiring reservation for product ${id.toString}")
    val product = currentProducts(id)
    currentProducts + (product.id -> product.copy(quantity = product.quantity + 1))
  }

  private def loadProducts() = {
    db.run(productDAO.getAllAvailable())
      .foreach {products =>
        currentProducts = Map(products.map(p => p.id -> p): _*)
      }
  }


  private def reserveProduct(id: Id[Product]) = {
    currentProducts.get(id) match {
      case Some(product) => if (product.quantity > 0) {
        val reservedProduct = product.copy(quantity = product.quantity - 1)
        currentProducts + (reservedProduct.id -> reservedProduct)
        val reservationCode = UUID.randomUUID().toString
        context.actorOf(ShopkeeperActor.props(id), reservationCode)
        Right(ReservationOrder(reservationCode, reservedProduct.price))
      } else {
        Left(CantReserveProductError(id))
      }
      case None => Left(CantReserveProductError(id))
    }
  }


  override def preStart(): Unit = {
    self ! LoadProducts
  }

}
