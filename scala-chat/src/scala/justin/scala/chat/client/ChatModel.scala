package justin.scala.chat.client

import java.util.Date

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatModel extends Subject[ChatModel] {

  var clients = Map[Int, Client]()
  var messages = Map[Int, Message]()

  var messageId = 1

  def addMessage(id: Int, message: String) {
    val msg = new Message(messageId, getClient(id), message, new Date)
    messages = messages + Tuple2(messageId, msg)
    messageId = messageId + 1
    fireMessage(msg)
  }

  def addClients(newClients: Map[Int, String]) {
    // remove all the existing clients
    clients.foreach(t => removeClient(t._1))
    // add all the new clients
    newClients.foreach(t => addClient(t._1, t._2))
  }

  def addClient(id: Int, name: String) = synchronized {
    if (!clients.contains(id)) {
      val client = new Client(id, name)
      clients = clients + Tuple2(id, client)
      fireClientAdded(client)
    }
  }

  def removeClient(id: Int) = synchronized {
    if (clients.contains(id)) {
      val client = getClient(id)
      clients = clients - id
      fireClientRemoved(client)
    }
  }

  def getClient(id: Int) = clients.getOrElse(id, DefaultClient)

  object DefaultClient extends Client(0, "default")

  def fireMessage(message: Message) = notifyObservers(new MessageAdded(message))

  def fireClientAdded(client: Client) = notifyObservers(new ClientAdded(client))

  def fireClientRemoved(client: Client) = notifyObservers(new ClientRemoved(client))
}

class Message(val id: Int, val from: Client, val message: String, val received: Date)

class Client(val id: Int, val name: String)

abstract class ChatModelEvent

case class MessageAdded(message: Message) extends ChatModelEvent
case class ClientAdded(client: Client) extends ChatModelEvent
case class ClientRemoved(client: Client) extends ChatModelEvent