package justin.scala.chat.client

import javax.swing.ScrollPaneConstants._
import javax.swing.table.AbstractTableModel
import swing._
import event._
/**
 * @author <a href="mailto:justin@guruhut.com>justin</a>
 */

class ChatUI extends BorderPanel with Publisher {
  val connect = new TextField
  val join = new Button("join")
  val leave = new Button("leave")

  val message = new TextField
  val send = new Button("send")

  val connectPanel = new BorderPanel {
    add(connect, BorderPanel.Position.Center)
    add(new BoxPanel(Orientation.Horizontal) {
      contents += join
      contents += leave
    }, BorderPanel.Position.East)
  }

  val messagePanel = new BorderPanel {
    add(message, BorderPanel.Position.Center)
    add(new BoxPanel(Orientation.Horizontal) {
      contents += send
    }, BorderPanel.Position.East)
  }

  val clientsModel = new AbstractTableModel {

    var clients = List[Client]()

    override def getColumnName(columnIndex: Int) = "Folks"

    override def getColumnCount = 1

    override def getRowCount = clients.size

    def getValueAt(rowIndex: Int, columnIndex: Int) = clients(rowIndex).name

    def +(client: Client) {
      clients = clients ::: List(client)
      fireTableDataChanged
    }

    def -(client: Client) {
      clients = clients.remove(v => v.id.equals(client.id))
      fireTableDataChanged
    }
  }

  val messagesModel = new AbstractTableModel {

    var messages = List[Message]()

    override def getColumnName(columnIndex: Int) = "Words"

    override def getColumnCount = 1

    override def getRowCount = messages.size

    def getValueAt(rowIndex: Int, columnIndex: Int) = messages(rowIndex).from.name + ": " + messages(rowIndex).message

    def +(message: Message) {
      messages = List(message) ::: messages
      fireTableDataChanged
    }
  }

  val clientsTable = new Table(0, 0) {
    
    model = clientsModel
  }

  val messagesTable = new Table(0, 0) {

    model = messagesModel
  }

  add(new ScrollPane(clientsTable) {
    preferredSize = (120, 400)
    peer.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS)
    peer.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER)
  }, BorderPanel.Position.West)

  add(new BorderPanel {
    add(new ScrollPane(messagesTable) {
      preferredSize = (400, 400)
      peer.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS)
      peer.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER)
    }, BorderPanel.Position.Center)

    add(messagePanel, BorderPanel.Position.North)
    add(connectPanel, BorderPanel.Position.South)
  }, BorderPanel.Position.Center)

  def addClient(client: Client) {
    clientsModel + client
  }

  def removeClient(client: Client) {
    clientsModel - client
  }

  def addMessage(message: Message) {
    messagesModel + message
  }

  def messageSend(msg: String) {
    message.text = ""
    message.requestFocus
  }

  listenTo(leave)
  listenTo(send)
  listenTo(join)
  listenTo(message)
  listenTo(connect)

  reactions += {
    case ButtonClicked(source) =>
      if (source == join) fireJoin
      if (source == leave) fireLeave
      if (source == send) fireSend
    case EditDone(source) =>
      if (source == message) fireSend
      if (source == connect) fireJoin
  }

  def fireSend {
    publish(UISend)
  }

  def fireLeave {
    publish(UILeave)
  }

  def fireJoin {
    publish(UIJoin)
  }

  def setMode(mode: Mode) {
    message.enabled = mode == ModeConnected
    send.enabled = mode == ModeConnected

    connect.enabled = mode == ModeDisconnected
    join.enabled = mode == ModeDisconnected
    leave.enabled = mode == ModeConnected

    if (mode == ModeConnected) {
      connect.selectAll
      connect.requestFocus
    }
    if (mode == ModeDisconnected) {
      message.selectAll
      message.requestFocus
    }
  }

  setMode(ModeDisconnected)
}

abstract class Mode

case object ModeDisconnected extends Mode
case object ModeConnected extends Mode

case object UIJoin extends Event
case object UILeave extends Event
case object UISend extends Event