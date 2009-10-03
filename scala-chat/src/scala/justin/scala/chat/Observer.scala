package justin.scala.chat

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

trait Observer[S] {
  def update(subject: S, modifier: Any)
}

trait Subject[S] {
  this: S =>
  private var observers: List[Observer[S]] = Nil
  def addObserver(observer: Observer[S]) = observers = observer :: observers

  def notifyObservers(modifier: Any) = observers.foreach(_.update(this, modifier))
}