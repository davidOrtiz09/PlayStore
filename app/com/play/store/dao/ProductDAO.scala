package com.play.store.dao

import com.google.inject.ImplementedBy
import com.play.store.errors.Errors.CantReserveProductError
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIO, DBIOAction}
import scala.concurrent.ExecutionContext
import com.play.store.models.{Id, Product, ProductDTO}


@ImplementedBy(classOf[ProductDAOImpl])
trait ProductDAO {

  def getAllAvailable(): DBIO[Seq[Product]]

}

class ProductDAOImpl @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider)
  (implicit ec: ExecutionContext) extends ProductDAO with ProductTable {

  import dbConfig.profile.api._

  override def getAllAvailable() = {
    productQuery
      .filter(_.quantity > 0)
      .result
      .map(_.map(Product.fromDB))
  }

}