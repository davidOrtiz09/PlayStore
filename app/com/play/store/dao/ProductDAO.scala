package com.play.store.dao

import com.google.inject.ImplementedBy
import javax.inject._
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO
import scala.concurrent.ExecutionContext
import com.play.store.models.Product


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