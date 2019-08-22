package com.play.store.controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import Converters._
import com.play.store.errors.Errors.SuperStoreError
import com.play.store.services.StoreService
import com.play.store.models._
import play.api.libs.json.Json

class AsyncController @Inject()(cc: ControllerComponents, storeService: StoreService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllAvailableProducts(currency: Option[String]) = Action.async {
    storeService.getAllAvailableProducts(currency).map(products => Ok(Json.toJson(products)))
      .recover {
        case e: SuperStoreError => BadRequest(Json.toJson(e))
        case e: Exception =>
          e.printStackTrace()
          InternalServerError(Json.toJson("Something wrong happened"))
      }
  }

  def reserveProduct = Action.async(parse.json[RequestOrder]) { implicit request =>
    storeService.reserveProduct(request.body.productId).map(reservation => Ok(Json.toJson(reservation)))
      .recover {
        case e: SuperStoreError => BadRequest(Json.toJson(e))
        case e: Exception =>
           e.printStackTrace()
           InternalServerError(Json.toJson("Can't reserve this product at the moment"))
      }
  }

  def buyProduct = Action.async(parse.json[ReservationOrder]) { implicit request =>
    storeService.buyProduct(request.body).map(_ => Ok(Json.toJson("Your product has been bought")))
      .recover {
        case e: SuperStoreError => BadRequest(Json.toJson(e))
        case e: Exception =>
          e.printStackTrace()
          InternalServerError(Json.toJson("Can't buy this product at the moment"))
      }
  }


}
