package com.play.store.dao

import com.play.store.models.Currency.Currency
import com.play.store.models.{Id, ProductDTO}

trait ProductTable extends DatabaseProfile {

  import profile.api._

  val productQuery = TableQuery[ProductsTable]

  class ProductsTable(tag: Tag) extends Table[ProductDTO](tag, "products")  {

    def id = column[Id[ProductDTO]]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def priceAmount = column[BigDecimal]("price_amount")
    def priceCurrency = column[Currency]("price_currency")
    def quantity = column[Int]("quantity")

    def * = (id, name, priceAmount, priceCurrency, quantity) <> (ProductDTO.tupled, ProductDTO.unapply)
  }

}
