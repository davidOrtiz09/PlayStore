package com.play.store.dao

import com.play.store.models.Currency.Currency
import com.play.store.models.{Currency, Id}
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile

trait DatabaseProfile extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  implicit def idMapper[M]: BaseColumnType[Id[M]] = MappedColumnType.base[Id[M], Long](_.id, Id[M])

  implicit val currencyColumnType: BaseColumnType[Currency] = MappedColumnType.base[Currency, Int](_.id, Currency.apply)
}
