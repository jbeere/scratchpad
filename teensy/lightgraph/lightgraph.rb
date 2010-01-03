require 'rubygems'
require 'serialport'
require 'tk'

msg = TkLabel.new {
   text 'Serial Light Meter!'
}
msg.pack(:side=>:top)

class BallAnimation
   
   def initialize(frame)
      # initialise the serial port
      @sp = SerialPort.new "/dev/tty.usbmodem12341", 9600
      @sp.write "AT\r\n"

      # an array containing the last few values received
      @history = Array.new
      @history_pos = 0
      @history_len = 1
      
      # main frame with a button
      TkFrame.new(frame) { |f|
         TkButton.new(f, :command=>proc{Tk.root.destroy},
                      :text=>'Dismiss').pack(:side=>:left, :expand=>true)
      }.pack(:side=>:bottom, :fill=>:x, :pady=>'2m')
      
      # window pane to contain the canvas
      pane = TkPanedWindow.new(frame).pack(:fill=>:both, :expand=>true)
      
      # a frame for the canvas
      @cframe = TkLabelFrame.new(pane, :text=>'Light Reading')
      
      # the canvas
      @c = TkCanvas.new(@cframe, :width=>640, :height=>200, :background=>'white',
                        :borderwidth=>2, :relief=>:sunken)
      
      # the ball
      @ball = TkcOval.new(@c, 155, 20, 165, 30, :fill=>'grey50', :outline=>'')
      
      @c.pack(:fill=>:both, :expand=>true)
      
      #some variables
      @x = 30
      @y = 0
      @d = 1
      
      animateBall
      
      # animation time
      @timer = TkTimer.new(15) { animateBall }
      
      # draw the canvas
      Tk.update
      pane.add(@cframe)
      
      @timer.start(500)
   end
   
   def showBall
      @ball.coords(@x-15, @y-15, @x+15, @y+15)
   end
   
   def moveBall
      v = @history[@history_pos].to_f
      if v != nil then
         @y = 200 - ((v / 1024.0) * 200.0).to_i
      end
   end
   
   def animateBall
      update_value(serial_read_int())
      moveBall
      showBall
   end
   
   # adds a value as the latest history entry
   def update_value(v)
      @history[@history_pos] = v
      @history_pos += 1
      # when history is full, overwrite from the beginning
      if @history_pos >= @history_len
         @history_pos = 0
      end
   end

   # gets a line from the serial input stream
   def serial_read_line()
      s = ""
      c = @sp.getc
      until c == 13 # collect until 13 (new line)
         s.concat(c)
         c = @sp.getc
      end
      return s
   end
   
   # gets an integer from the serial input stream
   def serial_read_int()
      return serial_read_line().hex
   end
end

@graph = BallAnimation.new(Tk.root)

Tk.mainloop
