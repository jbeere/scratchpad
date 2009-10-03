package justin.scala.chat.server

import actors.remote.RemoteActor
import actors.remote.RemoteActor._
import common.Constants

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatServerRemote(port: Int) extends ChatServer {

  RemoteActor.classLoader = getClass().getClassLoader()
  
  override def act() {
    // binds to localhost:port
    alive(port)
    // registers this listener onto the port
    register(Constants.chat, this)
    // start chatting
    super.act()
  }
}