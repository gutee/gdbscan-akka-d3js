package com.simple

import scala.scalajs.js.JSApp

import com.greencatsoft.angularjs.Angular
import com.greencatsoft.angularjs.injectable
import com.simple.controller.D3Controller

object GdbscanClient extends JSApp {
  def main(): Unit = {

    val module = Angular.module("d3App", Seq("nvd3"))

    module.controller[D3Controller]
  }
}