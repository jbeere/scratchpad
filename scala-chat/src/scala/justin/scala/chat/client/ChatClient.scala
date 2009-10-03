package justin.scala.chat.client

import actors.Actor._
import actors.{AbstractActor, Actor}
import common._

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatClient extends Actor with Subject[ChatClient] {

  // the user name
  var name: String = _
  
  // the server to whom we will chat
  var server: AbstractActor = _

  def act() {
    // request join from the server
    server ! Join(name)
    // wait for the server response
    react {
      // yes! we are officially in the chat
      case Registered(id) =>
        notifyObservers(ChatConnected)
        // ask the server for the current list of clients
        server ! ClientsRequest
        // wait for events from the server
        loop {
          react {
            case Joined(id, clientName) =>
              // someone joined
              notifyObservers(new ChatJoined(id, clientName))
            case Left(id) =>
              // someone left
              notifyObservers(new ChatLeft(id))
            case Sent(id, message) =>
              // someone sent a message
              notifyObservers(new ChatReceived(id, message))
            case message: String =>
              // a local wants us to send a message
              server ! Send(id, message)
              notifyObservers(new ChatSent(message))
            case Leave(0) =>
              // a local wants us to leave
              server ! Leave(id)
              notifyObservers(ChatDisconnected)
              exit()
            case ClientsResponse(clients) =>
              // the server has sent us the list of clients in the chat
              notifyObservers(new ChatClientList(clients))
          }
        }
    }
  }
}

abstract class ChatEvent

case class ChatJoined(id: Int, clientName: String)
case class ChatLeft(id: Int)
case class ChatReceived(id: Int, message: String)
case class ChatSent(message: String)
case object ChatConnected
case object ChatDisconnected
case class ChatClientList(clients: Map[Int, String])