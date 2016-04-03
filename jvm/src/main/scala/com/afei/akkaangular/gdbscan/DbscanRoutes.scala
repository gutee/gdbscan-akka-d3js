package com.afei.akkaangular.gdbscan

import com.afei.akkaangular.api.RoutesSupport
import com.afei.model.Location
import com.afei.model.Param
import com.simple.rest.DataService
import com.typesafe.scalalogging.StrictLogging

import akka.http.scaladsl.marshalling.ToResponseMarshallable.apply
import akka.http.scaladsl.server.Directive.addByNameNullaryApply
import akka.http.scaladsl.server.Directive.addDirectiveApply
import akka.http.scaladsl.server.Directives.as
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.Directives.enhanceRouteWithConcatenation
import akka.http.scaladsl.server.Directives.entity
import akka.http.scaladsl.server.Directives.get
import akka.http.scaladsl.server.Directives.path
import akka.http.scaladsl.server.Directives.pathPrefix
import akka.http.scaladsl.server.Directives.post
import akka.http.scaladsl.server.Directives.segmentStringToPathMatcher
import upickle.default.SeqishW
import upickle.default.macroR
import upickle.default.read
import upickle.default.write

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