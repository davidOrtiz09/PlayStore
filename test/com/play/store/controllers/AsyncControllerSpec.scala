package com.play.store.controllers

import com.play.store.models.{Currency, Id, Money, Product}
import com.play.store.services.StoreService
import org.scalatest.mockito.MockitoSugar
import scala.concurrent.Future
import org.scalatestplus.play._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.mockito.Mockito.when
import play.api.libs.json.Json
import scala.concurrent.ExecutionContext.Implicits.global


class AsyncControllerSpec extends PlaySpec with Results with MockitoSugar {

  val storeService = mock[StoreService]

  "AsyncController" should {
    "return all the available products" in {
      val product = Product(Id[Product](1), "jacket", Money(25000, Currency.COP), 5)
      when(storeService.getAllAvailableProducts(None))
        .thenReturn(Future.successful(Seq(product)))
      val expectedJson = Json.arr(Json.obj("id" -> 1, "name" -> "jacket",
        "price" -> Json.obj("amount" -> 25000,"currency"-> "COP"),
        "quantity" -> 5))
      val controller = new AsyncController(Helpers.stubControllerComponents(), storeService)
      val result: Future[Result] = controller.getAllAvailableProducts(None).apply(FakeRequest())
      val jsonResult = contentAsJson(result)
      jsonResult mustBe expectedJson
    }
  }

}
