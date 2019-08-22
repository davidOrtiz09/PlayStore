package com.play.store.controllers

import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import Converters._
import com.play.store.errors.Errors.SuperStoreError
import com.play.store.services.StoreService
import play.api.libs.json.Json

class AsyncController @Inject()(cc: ControllerComponents, storeService: StoreService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllAvailableProducts(currency: Option[String]) = Action.async {
    storeService.getAllAvailableProducts(currency).map(products => Ok(Json.toJson(products)))
      .recover {
        case e: SuperStoreError => BadRequest(Json.toJson(e))
        case _ => InternalServerError(Json.toJson("Something wrong happened"))
      }
  }


}
