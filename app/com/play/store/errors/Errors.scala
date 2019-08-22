package com.play.store.errors

import com.play.store.models.{Id, Product}

object Errors {

  sealed abstract class SuperStoreError(val message: String, val code: Int) extends Exception(message)

  case class CurrencyNotSupportedError(currency: String) extends SuperStoreError(
    s"Currently we don't support $currency as a Currency",
    1001
  )

  case class ExchangeError(override val message: String) extends SuperStoreError(
    message,
    1002
  )

  case class ProductNotAvailableError(id: Id[Product]) extends SuperStoreError(
    s"The product with the following Id ${id.id} is not available or doesn't exist",
    1003
  )

  case class CantReserveProductError(id: Id[Product]) extends SuperStoreError(
    s"The product with the following Id ${id.id} couldn't be reserved",
    1004
  )


}
