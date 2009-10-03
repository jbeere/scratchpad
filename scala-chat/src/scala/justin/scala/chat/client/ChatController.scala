package justin.scala.chat.client


import common.Leave
import swing.Reactor

/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatController(chat: ChatClientRemote, model: ChatModel, ui: ChatUI) extends Reactor {

  chat.addObserver(new Observer[ChatClient] {
    def update(subject: ChatClient, modifier: Any) {
      modifier match {
        case ChatJoined(id, name) => model.addClient(id, name)
        case ChatLeft(id) => model.removeClient(id)
        case ChatReceived(id, message) => model.addMessage(id, message)
        case ChatSent(message) => ui.messageSend(message)
        case ChatConnected => ui.setMode(ModeConnected)
        case ChatDisconnected => ui.setMode(ModeDisconnected)
        case ChatClientList(clients) => model.addClients(clients)
      }
    }
  })

  model.addObserver(new Observer[ChatModel] {
    def update(subject: ChatModel, modifier: Any) {
      modifier match {
        case MessageAdded(message) => ui.addMessage(message)
        case ClientAdded(client) => ui.addClient(client)
        case ClientRemoved(client) => ui.removeClient(client)
      }
    }
  })

  listenTo(ui)

  reactions += {
    case UISend => chat ! ui.message.text
    case UILeave => chat ! Leave(0)
    case UIJoin => chat.connect(ui.connect.text)
  }
}