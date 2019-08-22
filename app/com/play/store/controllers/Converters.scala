package com.play.store.controllers

import com.play.store.errors.Errors.SuperStoreError
import com.play.store.models.Currency.Currency
import com.play.store.models.{Id, Money, Product}
import play.api.libs.json._
import play.api.libs.functional.syntax._

object Converters {

  implicit val idWriter: Writes[Id[Product]] = Writes { id =>
    JsNumber(id.id)
  }

  implicit val currencyWriter: Writes[Currency] = Writes { currency =>
    JsString(currency.toString)
  }

  implicit val moneyWrites: Writes[Money] = (
    (JsPath \ "amount").write[BigDecimal] and
    (JsPath \ "currency").write[Currency]
    )(unlift(Money.unapply))

  implicit val productWrites: Writes[Product] = (
    (JsPath \ "id").write[Id[Product]] and
    (JsPath \ "name").write[String] and
    (JsPath \ "price").write[Money] and
    (JsPath \ "quantity").write[Int]
    )(unlift(Product.unapply))


  implicit val errorWrites = new Writes[SuperStoreError] {
    override def writes(error: SuperStoreError) = {
      Json.obj("message" -> error.message, "code" -> error.code)
    }
  }

}
