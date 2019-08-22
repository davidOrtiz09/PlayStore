package com.play.store.models

import com.play.store.models.Currency.Currency

case class RequestOrder(productId: Id[Product], maybeCurrency: Option[Currency])
