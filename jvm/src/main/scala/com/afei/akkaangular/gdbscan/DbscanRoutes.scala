package com.afei.akkaangular.gdbscan

import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directive.{addByNameNullaryApply, addDirectiveApply}
import akka.http.scaladsl.server.Directives._
import com.afei.akkaangular.api.RoutesSupport
import com.afei.model.{Location, Param}
import com.simple.rest.DataService
import com.typesafe.scalalogging.StrictLogging
import upickle.default.{macroR, read, write}

trait DbscanRoutes extends RoutesSupport with StrictLogging {

  // to keep increased points
  var dataCache: Seq[(Double, Double)] = DataService.getOrginalData
  var eps: Int = 5
  var minP: Int = 15

  val gdbscanRoute = pathPrefix("gdbscan") {
    path("getClusters") {
      get {
        complete {
          write(new DataService(dataCache, eps, minP).getClustersAsLocations)
        }
      }
    } ~
      path("getAll") {
        get {
          complete {
            write(new DataService(dataCache, eps, minP).getAll())
          }
        }
      } ~
      path("getNoise") {
        get {
          complete {
            write(new DataService(dataCache, eps, minP).getNoise())
          }
        }
      } ~
      path("addLocation") {
        post {
          entity(as[String]) { newLocString =>
            {
              val newLoc = read[Location](newLocString)
              dataCache = dataCache :+ (newLoc.x, newLoc.y)
              complete {
                println("all data number: " + dataCache.size)
                """{"message": "create new point successfully"}"""
              }
            }
          }
        }
      } ~
      path("updatePara") {
        post {
          entity(as[String]) { newParamString =>
            {
              val newParam = read[Param](newParamString)
              eps = newParam.eps
              minP = newParam.minP

              println("new parameter eps: " + eps + " minP: " + minP)
              complete {
                """{"message": "parameter update successfully"}"""
              }
            }
          }
        }
      }
  }

}