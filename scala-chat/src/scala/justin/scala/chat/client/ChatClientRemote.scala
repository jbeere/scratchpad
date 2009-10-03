package justin.scala.chat.client

import actors.remote.{Node, RemoteActor}
import actors.remote.RemoteActor._
import common.{Join, Leave, Send, Constants}

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatClientRemote extends ChatClient {

  RemoteActor.classLoader = getClass().getClassLoader()

  override def act() {
    // not sure what this does...
    link(server)
    // continue as usual
    super.act()
  }

  def connect(connect: String) {

    name = connect.split("@")(0)
    val host = connect.split("@")(1).split(":")(0)
    val port = connect.split("@")(1).split(":")(1).toInt

    Console.println("connecting to %s@%s:%d".format(name, host, port));

    // define the peer node to connect to
    val peer = Node(host, port)
    // get the server stub from the peer node
    server = select(peer, Constants.chat)

    start
  }
}

