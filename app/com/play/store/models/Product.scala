package com.play.store.models

import com.play.store.models.Currency.Currency

case class Product(id: Id[Product], name: String, price: Money, quantity: Int)

object Product {
  def fromDB(dto: ProductDTO) = {
    Product(Id[Product](dto.id.id), dto.name, Money(dto.priceAmount, dto.priceCurrency), dto.quantity)
  }
}

case class ProductDTO(id: Id[ProductDTO], name: String, priceAmount: BigDecimal, priceCurrency: Currency, quantity: Int)
