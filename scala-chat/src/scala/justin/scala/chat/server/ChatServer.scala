package justin.scala.chat.server

import actors.Actor._
import actors.Actor
import common._

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatServer extends Actor {

  // the map of clients will be maintained during the act lifecycle
  var clients = Map[Int, Client]();
  var clientsString = Map[Int, String]();

  def act() {
    var nextId = 1
    loop {
      react {
        case Join(name) =>
          // a client wants to join the chat
          Console.println("%s: Joining".format(name))
          // add him to the list of clients
          val id = nextId
          clients = clients + Tuple(id, new Client(id, name, sender.receiver))
          clientsString = clientsString + Tuple(id, name)
          // give him his chat id
          sender ! Registered(nextId)
          // for the next guy
          nextId = nextId + 1
          // let everyone know about their new friend
          notifyClients(Joined(id, name))
        case Leave(id) =>
          // a client wants to leave the chat
          Console.println("%s: Leaving".format(name(id)))
          val left = Left(id)
          // remove him from the client list
          clients = clients - id
          clientsString = clientsString - id
          // let everyone know
          notifyClients(left)
        case Send(id, message) =>
          // a client is sending a message
          Console.println("%s: Sending".format(name(id)))
          // let everyone know
          notifyClients(Sent(id, message))
        case ClientsRequest =>
          sender ! ClientsResponse(clientsString)
      }
    }
  }

  // retrieves the name of a client
  def name(id: Int) = clients.getOrElse(id, DefaultClient).name

  // notifies all the clients of an event
  def notifyClients(event: ServerAction) = clients.foreach(r => r._2.actor ! event)

  // the local local definition of a client
  class Client(val id: Int, val name: String, val actor: Actor)

  // the default client for those edge cases (should not be necessary)
  object DefaultClient extends Client(0, "Default", ChatServer.this)
}