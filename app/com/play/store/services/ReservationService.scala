package com.play.store.services

import com.play.store.dao.ProductDAO
import javax.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import scala.concurrent.ExecutionContext

trait ReservationService {

}

class ReservationServiceImpl @Inject()(
  ProductDAO: ProductDAO,
  protected val dbConfigProvider: DatabaseConfigProvider)
  (implicit ec: ExecutionContext) extends ReservationService with HasDatabaseConfigProvider[JdbcProfile] {

}
