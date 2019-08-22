package com.play.store.actors

import akka.actor.Actor
import com.play.store.actors.PlayStoreActor.{AvailableProducts, LoadProducts}
import com.play.store.dao.ProductDAO
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import com.play.store.models.Product
import scala.concurrent.Future

object PlayStoreActor {
  case object CreateShopkeeper
  case object DeleteReservation
  case object LoadProducts
  case object AvailableProducts

}

class PlayStoreActor @Inject()(productDAO: ProductDAO, protected val dbConfigProvider: DatabaseConfigProvider) extends Actor with HasDatabaseConfigProvider[JdbcProfile] {

  private var currentProducts: Seq[Product] = Seq.empty[Product]

  def receive = {
    case LoadProducts => db.run(productDAO.getAllAvailable()).foreach(products => currentProducts = products)
    case AvailableProducts => Future.successful(currentProducts.filter(_.quantity > 0))
  }


  override def preStart(): Unit = {
    self ! LoadProducts
  }

}
