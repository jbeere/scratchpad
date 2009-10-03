package justin.scala.chat.client

import common.Leave
import swing._
import event.{WindowClosing, ButtonClicked}
/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */
object ClientApplication extends SimpleGUIApplication {

  val top = new Frame {
    val ui = new ChatUI
    val chat = new ChatClientRemote
    val model = new ChatModel
    val controller = new ChatController(chat, model, ui)

    contents = ui
    title = "Scala Chat Client"

    // default connect string
    ui.connect.text = "yourname@junk:10101"

    reactions += {
      case WindowClosing(source) =>
        // leave the chat before exit
        chat ! Leave(0)
        System.exit(1)
    }
  }
}