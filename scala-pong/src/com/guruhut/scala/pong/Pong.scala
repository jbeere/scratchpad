package com.guruhut.scala.pong

import actors.Actor
import actors.Actor._

import java.awt.event._
import java.awt.geom.{Rectangle2D, Ellipse2D}
import java.util.{Timer, TimerTask}


/**
 * @author justin
 */
object Pong extends Application {
  // setup to run this game ...
  val model = new PongModel
  val player1 = new Player("Bricktop")
  val player2 = new Player("Turkish")
  val game = new PongGame(model, player1)
  val view = new PongView

  // todo a plain old Java frame, replace with the scala swing stuff
  val frame = new javax.swing.JFrame("Scala Pong")
  frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE)
  frame.setContentPane(view)
  frame.setSize(400, 400)
  frame.addKeyListener(new KeyPlayerMover(player1, KeyEvent.VK_A, KeyEvent.VK_Z))
  frame.addKeyListener(new KeyPlayerMover(player2, KeyEvent.VK_UP, KeyEvent.VK_DOWN))
  frame.show

  // add the view as an observer to the model
  model + view

  player1.start
  player2.start
  game.start

  game ! PlayerJoin(player2)
}

// messages from the game to the player
abstract class GameEvent
case class GameStart(n: Int, g: PongGame) extends GameEvent
case object GameOver extends GameEvent
case class Score(playerId: Int) extends GameEvent

// messages from the player to the game
abstract class PlayerEvent
case object PlayerQuit extends PlayerEvent
case class PlayerJoin(p: Player) extends PlayerEvent
case class PlayerMove(p: Player, direction: Int) extends PlayerEvent

// represents an instance of a pong game
class PongGame(model: PongModel, player1: Player) extends Actor {
  // todo is there a scala way to schedule tasks?
  var timer = new Timer
  var player2: Player = null
  var engine = new PongEngine(this, model)

  def act {

    def startGame {
      player1 ! GameStart(1, this)
      player2 ! GameStart(2, this)
      timer.scheduleAtFixedRate(engine, 0, 15)
      Console.println("Pong: Game started!");
    }

    def playerJoined(player: Player) {
      player2 = player
      Console.println("Pong: " + player.name + " Joined!");
    }

    def endGame {
      player1 ! GameOver
      player2 ! GameOver
      timer.cancel
      exit
    }

    def playerMoved(player: Player, direction: Int): Unit = {
      if (player == player1) {
        model.paddle1.direction = direction
      }
      if (player == player2) {
        model.paddle2.direction = direction
      }
    }

    def playerScored(playerId: Int): Unit = {
      var score = Score(playerId)
      player1 ! score
      player2 ! score
    }

    loop {
      react {
        case PlayerJoin(player: Player) =>
          playerJoined(player)
          startGame
        case PlayerQuit =>
          endGame
        case PlayerMove(player: Player, direction: Int) =>
          playerMoved(player, direction)
        case Score(playerId: Int) =>
          playerScored(playerId)
      }
    }
  }
}

// represents a player of the game
class Player(var name: String) extends Actor {
  var id: Int = 0
  var game: PongGame = null

  def move(direction: Int): Unit = {
    if (game != null) {
      game ! PlayerMove(this, direction)
    }
  }
  
  def act {
    
    def gameStarted(n: Int, g: PongGame) {
      id = n
      game = g
      Console.println(name + " : Game started!");
    }

    def gameEnded {
      Console.println(name + " : Game Over")
      exit
    }

    def scored(playerId: Int) {
      if (playerId == id) {
        Console.println(name + " : Yeah!")
      } else {
        Console.println(name + " : Darn!")
      }
    }
    
    loop {
      react {
        case GameStart(n: Int, g: PongGame) =>
          gameStarted(n, g)
        case GameOver =>
          gameEnded
        case Score(playerId: Int) =>
          scored(playerId)
      }
    }
  }
}

// something that needs to observe the model should have this trait
trait PongModelObserver {
  // something in the model has changed
  def updated(model: PongModel): Unit
}

// the model representing the state of the game
class PongModel {
  var observers = List[PongModelObserver]()
  val space = new SpaceModel(this)
  val paddle1 = new PlayerModel(this, -1)
  val paddle2 = new PlayerModel(this, 1)
  val ball = new BallModel(this)

  // add an observer
  def +(obs: PongModelObserver) {
    // lists are immutable
    observers = observers ::: List(obs)
    // initial notification of current status
    notifyObserver(obs)
  }

  def notifyObservers {
    // foreach executes notifyObserver ... nifty
    observers.foreach(notifyObserver)
  }

  def notifyObserver(observer: PongModelObserver) = {
    observer.updated(this)
  }
}

// represents the state of a player
class PlayerModel(val model: PongModel, side: Int) extends Bounds {
  var score = 0
  var posX = if (side == -1) 0.1f else 0.9f
  var posY = 0.5f
  var direction = 0

  val width = 0.01f
  val height = 0.2f

  def right = posX + (width / 2)
  def left = posX - (width / 2)
  def top = posY - (height / 2)
  def bottom = posY + (height / 2)

  def notifyObservers = model.notifyObservers
}

// represents the state of the ball
class BallModel(val model: PongModel) extends Bounds {
  var pX = 0.5f
  var pY = 0.5f
  var directionX = 1
  var directionY = 1

  val radius = 0.02f
  val width = radius * 2
  val height = radius * 2

  def right = pX + radius
  def left = pX - radius
  def top = pY - radius
  def bottom = pY + radius

  def notifyObservers = model.notifyObservers
}

// the space that the game is played in
class SpaceModel(val model: PongModel) extends Bounds {
  val width = 1.1f // oversized, so that the ball goes out of the screen
  val height = 0.9f
  val right = 1 - ((1 - width) / 2)
  val left = (1 - width) / 2
  val top = (1 - height) / 2
  val bottom = 1 - ((1 - height) / 2)
}

// a class that the other models extend, provides basic
// collision detection functionality
abstract class Bounds {
  def right: Float
  def left: Float
  def top: Float
  def bottom: Float

  def width: Float
  def height: Float

  def within(that: Bounds) = withinH(that) && withinV(that)

  def withinH(that: Bounds) = withinTop(that) && withinBottom(that)
  def withinV(that: Bounds) = withinLeft(that) && withinRight(that)

  def withinRight(that: Bounds) = right < that.right
  def withinLeft(that: Bounds) = left > that.left
  def withinTop(that: Bounds) = top > that.top
  def withinBottom(that: Bounds) = bottom < that.bottom

  def intersects(that: Bounds) = intersectsH(that) && intersectsV(that)

  def intersectsH(that: Bounds) = intersectsTop(that) && intersectsBottom(that)
  def intersectsV(that: Bounds) =  intersectsLeft(that) && intersectsRight(that)

  def intersectsRight(that: Bounds) = left < that.right// && left > that.left
  def intersectsLeft(that: Bounds) = right > that.left// && right < that.right
  def intersectsTop(that: Bounds) = bottom > that.top// && bottom < that.bottom
  def intersectsBottom(that: Bounds) = top < that.bottom// && top > that.top
}

// the core game engine, modifies the model appropriately
class PongEngine(game: PongGame, model: PongModel) extends TimerTask {

  // the distance the ball / paddle travels in each iteration
  val xInc = 0.002f
  val yInc = 0.003f

  // moves the ball forward by x increments
  def moveBall(x: Int, y: Int): Unit = {
    model.ball.pX += (xInc * model.ball.directionX * x)
    model.ball.pY += (yInc * model.ball.directionY * x)
  }

  // moves the ball forward 1 increment
  def moveBall: Unit = {
    moveBall(1, 1)
  }

  // moves the paddle within the game boundaries
  def movePaddle(paddle: PlayerModel) {
    if (paddle.direction != 0) {
      if (paddle.direction < 0 && paddle.withinTop(model.space)) {
        paddle.posY += (xInc * paddle.direction)
      } else if (paddle.direction > 0 && paddle.withinBottom(model.space)) {
        paddle.posY += (xInc * paddle.direction)
      }
    }
  }

  // check if the ball has collided with anything
  def detectCollisions: (Boolean, Boolean) = {
    // todo better collision detection
    var xCollision = false
    var yCollision = false

    // check if the ball has hit one of the paddles
    if (model.ball.intersects(model.paddle1)) {
      if (model.ball.intersectsV(model.paddle1)) {
        xCollision = true
      }
    } else if (model.ball.intersects(model.paddle2)) {
      if (model.ball.intersectsV(model.paddle2)) {
        xCollision = true
      }
    }

    if (!yCollision && !xCollision) {
      // the ball hasn't hit a paddle
      if (!model.ball.withinRight(model.space)) {
        // the ball has hit the right wall, so player 1 scores
        model.paddle1.score = model.paddle1.score + 1
        game ! Score(1)
        xCollision = true
      } else if (!model.ball.withinLeft(model.space)) {
        // the ball has hit the left wall, so player 2 scores
        model.paddle2.score = model.paddle2.score + 1
        game ! Score(2)
        xCollision = true
      }

      // check if the ball has hit the top or bottom
      if (!model.ball.withinH(model.space)) {
        yCollision = true
      }
    }

    // return the collision results
    (xCollision, yCollision)
  }

  var lastCollisionX = 0
  var lastCollisionY = 0

  def bounce(f: => (Boolean, Boolean)) {
    // todo better bouncing algorithm required
    val (x, y) = f
    if (x && lastCollisionX > 35) {
      model.ball.directionX *= -1
      lastCollisionX = 0
    }
    if (y && lastCollisionY > 35) {
      model.ball.directionY *= -1
      lastCollisionY = 0
    }
    lastCollisionX = lastCollisionX + 1
    lastCollisionY = lastCollisionY + 1
  }

  def run = {
    moveBall
    movePaddle(model.paddle1)
    movePaddle(model.paddle2)
    bounce(detectCollisions)
    model.notifyObservers
  }
}

// a keyboard listener for a player
class KeyPlayerMover(player: Player, upKey: Int, downKey: Int) extends KeyAdapter {

  override def keyReleased(e: KeyEvent) {
    if (e.getKeyCode == upKey || e.getKeyCode == downKey) {
      player.move(0)
    }
  }

  override def keyPressed(e: KeyEvent) {
    if (e.getKeyCode == upKey) {
      player.move(-1)
    } else if (e.getKeyCode == downKey) {
      player.move(1)
    }
  }
}

// a view for the pong game
class PongView extends javax.swing.JComponent with PongModelObserver with HierarchyBoundsListener {
  val bg = new Rectangle2D.Float
  val space = new Rectangle2D.Float
  val paddles = Array(new Rectangle2D.Float, new Rectangle2D.Float)
  val ball = new Ellipse2D.Float

  var w = 1f
  var h = 1f

  var score1 = "0"
  var score2 = "0"

  override def paintComponent(g: java.awt.Graphics) {
    super.paintComponent(g)

    val g2: java.awt.Graphics2D = g.asInstanceOf[java.awt.Graphics2D]
    g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
      java.awt.RenderingHints.VALUE_ANTIALIAS_ON)

    var paint = g2.getPaint // backup state
    g2.setPaint(java.awt.Color.BLACK)
    fillBackground(g2)
    g2.setPaint(java.awt.Color.WHITE)
    drawSpace(g2)
    drawScore1(g2)
    drawScore2(g2)
    fillPaddles(g2)
    fillBall(g2)
    g2.setPaint(paint) // reset state
  }

  // a bunch of functions for drawing the components of the ui
  def fillBackground(g: java.awt.Graphics2D) = fillShape(g, bg)
  def fillPaddles(g: java.awt.Graphics2D) = for (paddle <- paddles) fillPaddle(g, paddle)
  def fillPaddle(g: java.awt.Graphics2D, s: java.awt.Shape) = fillShape(g, s)
  def fillBall(g: java.awt.Graphics2D) = fillShape(g, ball)
  def drawSpace(g: java.awt.Graphics2D) = drawShape(g, space)
  def drawScore1(g: java.awt.Graphics2D) = drawScore(g, 0.25f, score1)
  def drawScore2(g: java.awt.Graphics2D) = drawScore(g, 0.75f, score2)
  def drawScore(g: java.awt.Graphics2D, x: Float, text: String) = g.drawString(text, w * x, h * 0.3f)
  def fillShape(g: java.awt.Graphics2D, s: java.awt.Shape) = fill(s, g)
  def drawShape(g: java.awt.Graphics2D, s: java.awt.Shape) = draw(s, g)
  def fill(s: java.awt.Shape, g: java.awt.Graphics2D) = g.fill(s)
  def draw(s: java.awt.Shape, g: java.awt.Graphics2D) = g.draw(s)

  def updated(model: PongModel) {
    // todo is AffineTransform for scaling more efficient than copying and recalculating the model?
    // update the positions of all the components accoridng to the model
    // all positions in the model are ratios, so multiply by the size of this component
    bg.setRect(0, 0, w, h)
    space.setRect(w * model.space.left, h * model.space.top, w * model.space.width, h * model.space.height)
    paddles(0).setRect(w * model.paddle1.left, h * model.paddle1.top, w * model.paddle1.width, h * model.paddle1.height)
    paddles(1).setRect(w * model.paddle2.left, h * model.paddle2.top, w * model.paddle2.width, h * model.paddle2.height)
    ball.setFrame(w * model.ball.left, h * model.ball.top, w * model.ball.width, h * model.ball.height)

    // update the scores
    score1 = model.paddle1.score.toString
    score2 = model.paddle2.score.toString

    // redraw this component (the paintComponent method will eventually be called)
    repaint()
  }

  def updateFrame(dimension: java.awt.Dimension) {
    w = dimension.getWidth.toFloat
    h = dimension.getHeight.toFloat
  }

  override def ancestorMoved(e: HierarchyEvent) { /* N/A */ }

  override def ancestorResized(e: HierarchyEvent) {
    // creates a new Dimension object every time, so use sparingly
    // ie only when the size actually changes
    updateFrame(getSize())
  }

  // initial size
  updateFrame(getSize())

  // need to know when this component is resized
  addHierarchyBoundsListener(this)
}