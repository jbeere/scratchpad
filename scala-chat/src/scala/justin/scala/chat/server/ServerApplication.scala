package justin.scala.chat.server

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

object ServerApplication extends Application {

  // default port is 10101
  new ChatServerRemote(10101).start
}