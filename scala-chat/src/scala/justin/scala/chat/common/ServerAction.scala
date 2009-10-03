package justin.scala.chat.common

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

abstract class ServerAction

// pass a chat id to the client
case class Registered(id: Int) extends ServerAction

// broadcast when someone joins
case class Joined(id: Int, name: String) extends ServerAction

// broadcast when someone leaves
case class Left(id: Int) extends ServerAction

// broadcast when a message is sent
case class Sent(id: Int, message: String) extends ServerAction

// respond list of clients
case class ClientsResponse(clients: Map[Int, String]) extends ServerAction
