package com.guruhut.scala.lift.controller

import actors.Actor
import actors.Actor._
import collection.mutable.HashSet
import java.util.{Timer, TimerTask}
// Messages
case class AddListener(listener: Actor)
case class RemoveListener(listener: Actor)

// Data Structures
case class Location(x: Float, y: Float)

object BallController extends Actor {

  val ball = new Ball
  val engine = new Engine
  val listeners = new HashSet[Actor]

  def notifyListeners = {
    listeners.foreach(_ ! Location(ball.xLocation, ball.yLocation))
  }

  def act {
    loop {
      react {
        case AddListener(listener: Actor) =>
          Console.println("Adding a listener....")
          listeners.incl(listener)
          reply(Location(ball.xLocation, ball.yLocation))
        case RemoveListener(listener: Actor) =>
          Console.println("Removing a listener....")
          listeners.excl(listener)
      }
    }
  }

  start

  new Timer().scheduleAtFixedRate(engine, 0, 100)
}

class Ball {
  var xLocation = 0.5f;
  var yLocation = 0.5f;
  var xSpeed = 0.01f;
  var ySpeed = 0.02f;
  var xDirection = 1;
  var yDirection = 1;

  def move {
    xLocation += xSpeed * xDirection
    yLocation += ySpeed * yDirection
    bounce
  }

  def bounce {
    if (xLocation >= 1f || xLocation <= 0f)
      xDirection *= -1
    if (yLocation >= 1f || yLocation <= 0f)
      yDirection *= -1
  }
}

class Engine extends TimerTask {

  def moveBall {
    BallController.ball.move
  }

  def run {
    moveBall
    BallController.notifyListeners
  }
}