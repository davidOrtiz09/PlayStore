package com.play.store.services

import com.google.inject.ImplementedBy
import com.play.store.dao.ProductDAO
import com.play.store.models._
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.Future

@ImplementedBy(classOf[ProductServiceImpl])
trait ProductService {

  def loadAllProducts(): Future[Seq[Product]]

}

class ProductServiceImpl @Inject()(
  productDAO: ProductDAO,
  protected val dbConfigProvider: DatabaseConfigProvider) extends ProductService with HasDatabaseConfigProvider[JdbcProfile] {

  def loadAllProducts() = {
    db.run(productDAO.getAllAvailable())
  }

}
