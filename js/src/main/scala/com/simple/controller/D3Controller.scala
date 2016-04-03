package com.simple.controller

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport

import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.raw.XMLHttpRequest

import com.afei.model.Location
import com.afei.model.Param
import com.greencatsoft.angularjs.AbstractController
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.injectable

import upickle.default.SeqishR
import upickle.default.SeqishW
import upickle.default.read
import upickle.default.write

@js.native
trait D3Scope extends Scope {
  var ref: js.Dynamic
  var content: js.Dynamic
  var myapi: js.Dynamic

  var myFormEps: String
  var myFormMinP: String
}

case class ElementData(x: Double, y: Double, size: Double, shape: String)
case class Group(key: String, values: Seq[ElementData])

@JSExport
@injectable("d3Ctrl")
class D3Controller(scope: D3Scope) extends AbstractController[D3Scope](scope) {

  val optionsText = """
{
    "chart": {
        "type": "scatterChart", 
        "height": 900, 
        "width": 900, 
        "margin": {
            "top": 20, 
            "right": 20, 
            "bottom": 60, 
            "left": 55
        }, 
        "showValues": true, 
        "transitionDuration": 350, 
        "xAxis": {
            "axisLabel": "X Axis"
        }, 
        "yAxis": {
            "axisLabel": "Y Axis"
        },
        
    "title": {
        "enable": true,
        "text": "GDBSCAN DIGRAM"
    }
            
    }
}
        """
  scope.ref = JSON.parse(optionsText)

  Ajax.get("api/gdbscan/getClusters")
    .onSuccess {
      case response => dealResult(response)
    }
  
  def dealResult(response: XMLHttpRequest) = {
    val clusters = read[Seq[Seq[Location]]](response.responseText)
    val comboRes = ((1 to clusters.size) zip clusters)
      .map { combo => Group("group:" + combo._1, combo._2.map(e => ElementData(e.x, e.y, 4, "circle"))) }

    Ajax.get("api/gdbscan/getNoise")
      .onSuccess {
        case response => {
          val noises = read[Seq[Location]](response.responseText)
          val allRes = comboRes :+ Group("group:" + (clusters.size + 1), noises.map { e => ElementData(e.x, e.y, 3, "circle") })

          scope.content = JSON.parse(write(allRes))
        }
      }
  }
  
  

  @JSExport
  def submit() = {
    
    val pa = Param(scope.myFormEps.toInt, scope.myFormMinP.toInt)

    // NOTE: if you write “Accept” part in the map, will cause 406 (Not Acceptable)
    Ajax.post("api/gdbscan/updatePara", write(pa), 0, Map("Content-Type" -> "application/json"))
      .onSuccess {
        case response => {
          js.Dynamic.global.window.location.reload()
        }
      }

      
  }

}

