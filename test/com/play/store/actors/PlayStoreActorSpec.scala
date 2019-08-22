package com.play.store.actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import akka.util.Timeout
import com.play.store.actors.PlayStoreActor.{AvailableProducts, DeleteReservation, LoadProducts, ReserveProduct}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import org.scalatest.mockito.MockitoSugar
import scala.concurrent.duration._
import akka.pattern.ask
import com.play.store.errors.Errors.{ProductNotAvailableError, SuperStoreError}
import com.play.store.models._
import com.play.store.services.ProductService
import org.mockito.Mockito.when
import scala.concurrent.Future


class PlayStoreActorSpec extends TestKit(ActorSystem("MySpec"))
  with WordSpecLike with Matchers with BeforeAndAfterAll with MockitoSugar with FutureWaiter {

  lazy val productService = mock[ProductService]

  implicit val timeout = Timeout(10.seconds)

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "PlayStoreActor" must {

    "get all the available products in memory" in {
      val product = Product(Id[Product](1), "jacket", Money(25000, Currency.COP), 5)
      when(productService.loadAllProducts()).thenReturn(Future.successful(Seq(product)))
      val storeActor = system.actorOf(Props(new PlayStoreActor(productService)))
      val _ = waitForIt((storeActor ? LoadProducts).mapTo[Unit])
      val result = waitForIt((storeActor ? AvailableProducts).mapTo[Seq[Product]])
      result.head shouldBe product
    }

    "Reserve an available product" in {
      val product = Product(Id[Product](1), "jacket", Money(25000, Currency.COP), 5)
      when(productService.loadAllProducts()).thenReturn(Future.successful(Seq(product)))
      val storeActor = system.actorOf(Props(new PlayStoreActor(productService)))
      val _ = waitForIt((storeActor ? LoadProducts).mapTo[Unit])
      val result = waitForIt((storeActor ? ReserveProduct(product.id)).mapTo[Either[SuperStoreError, ReservationOrder]])
      result.right.get.price shouldBe product.price
    }


    "Can't reserve product because is not available" in {
      val product = Product(Id[Product](1), "jacket", Money(25000, Currency.COP), 0)
      when(productService.loadAllProducts()).thenReturn(Future.successful(Seq(product)))
      val storeActor = system.actorOf(Props(new PlayStoreActor(productService)))
      val _ = waitForIt((storeActor ? LoadProducts).mapTo[Unit])
      val result = waitForIt((storeActor ? ReserveProduct(product.id)).mapTo[Either[SuperStoreError, ReservationOrder]])
      result.left.get shouldBe ProductNotAvailableError(product.id)
    }

    "Delete unused reservation" in {
      val product = Product(Id[Product](1), "jacket", Money(25000, Currency.COP), 5)
      when(productService.loadAllProducts()).thenReturn(Future.successful(Seq(product)))
      val storeActor = system.actorOf(Props(new PlayStoreActor(productService)))
      waitForIt((storeActor ? LoadProducts).mapTo[Unit])
      waitForIt((storeActor ? ReserveProduct(product.id)).mapTo[Either[SuperStoreError, ReservationOrder]])
      waitForIt((storeActor ? DeleteReservation(product.id)).mapTo[Unit])
      val newProducts = waitForIt((storeActor ? AvailableProducts).mapTo[Seq[Product]])
      newProducts.head shouldBe product
    }

  }


}
