package justin.scala.chat.common

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

abstract class ClientAction

// request server to join
case class Join(name: String) extends ClientAction

// request server to send this message
case class Send(id: Int, message: String) extends ClientAction

// request server to be excused
case class Leave(id: Int) extends ClientAction

// request the list of clients
case object ClientsRequest extends ClientAction