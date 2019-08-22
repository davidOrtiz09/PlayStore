package com.play.store.models

object Currency extends Enumeration {
  type Currency = Value

  val COP = Value(0, "COP")
  val USD = Value(1, "USD")
  val EUR = Value(2, "EUR")
}
