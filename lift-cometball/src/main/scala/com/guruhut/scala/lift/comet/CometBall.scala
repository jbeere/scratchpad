package com.guruhut.scala.lift.comet


import controller._
import net.liftweb._
import http.js.JE.{JsRaw}
import http.js.{JsExp, JsCmd, JsCmds}
import http.{SHtml, CometActor}
import JsCmds._ // For implicits
import util.Full

class CometBall extends CometActor {
  var x = 0.5f
  var y = 0.5f

  override def defaultPrefix = Full("ball")


  def render = {
    bind("view" -> <span>{func}</span>)
  }

  def set : JsCmd = JsRaw(func)

  def func = String.format("set(%f, %f)", x.asInstanceOf[Object], y.asInstanceOf[Object])

  override def localSetup {
    BallController !? AddListener(this) match {
      case Location(x: Float, y: Float) => this.x = x; this.y = y
    }
  }

  override def localShutdown {
    BallController ! RemoveListener(this)
  }

  override def highPriority: PartialFunction[Any, Unit] = {
    case Location(x: Float, y: Float) =>
      this.x = x
      this.y = y
      partialUpdate(set)
  }
}