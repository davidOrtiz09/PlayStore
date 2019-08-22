package com.play.store.models

import com.play.store.models.Currency.Currency

case class Money(amount: BigDecimal, currency: Currency)
