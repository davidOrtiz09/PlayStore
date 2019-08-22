package com.play.store.services

import com.google.inject.ImplementedBy
import com.play.store.errors.Errors.ExchangeError
import com.play.store.models.Currency.Currency
import com.play.store.models.Money
import javax.inject.Inject
import play.api.Configuration
import play.api.libs.ws.{WSClient, WSResponse}
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.json._
import scala.util.{Failure, Success, Try}

@ImplementedBy(classOf[ExchangeServiceImpl])
trait ExchangeService {

  def convertAmount(fromValue: Money, to: Currency): Future[Money]

}

class ExchangeServiceImpl @Inject()(ws: WSClient, config: Configuration)(implicit ec: ExecutionContext) extends ExchangeService {

  private val API_KEY = config.get[String]("oanda.api.key")
  private val URL = "https://www1.oanda.com/rates/api/v2/rates/spot.json"

  override def convertAmount(fromValue: Money, to: Currency) = {

    val futureResponse = ws.url(URL).addQueryStringParameters("api_key" -> API_KEY,
      "base" -> fromValue.currency.toString,
      "quote" -> to.toString).get()

    futureResponse.flatMap { response =>
      validateConvertAmountResponse(fromValue, to, response) match {
        case Success(newAmount) => Future.successful(newAmount)
        case Failure(error) => Future.failed(ExchangeError(error.getMessage))
      }
    }
  }

  private def validateConvertAmountResponse(fromValue: Money, to: Currency, response: WSResponse) = {
    Try{
      val quoteResponse = (response.json \ "quotes")(0)
      val midpointRate = (quoteResponse \ "midpoint").as[BigDecimal]
      val newRate = fromValue.amount * midpointRate
      Money(newRate, to)
    }
  }

}
