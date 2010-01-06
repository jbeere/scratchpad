require 'rubygems'
require 'serialport'
require 'tk'

$speed = 40

class AnimatedGraph
   
   def initialize(frame)
      
      # initialise the serial port
      @sp = SerialPort.new "/dev/tty.usbmodem12341", 9600
      @sp.write "AT\r\n"
      
      # variables for managing the graph values
      @valueCt = 300
      @valuePos = 0
      @values = Array.new(@valueCt)
      
      # a frame at the bottom, with a 'Quit' button
      TkFrame.new(frame) {|f|
         TkButton.new(f, :command=>proc{ $graph.cleanExit }, 
               :text=>'Quit').pack(:side=>:left, :expand=>true)
      }.pack(:side=>:bottom, :fill=>:x, :pady=>'2m')
      
      # the panel the graph will reside in
      pane = TkPanedWindow.new(frame).pack(:fill=>:both, :expand=>true)   
 
      @graphFrame = TkLabelFrame.new(pane, :text=>'Animated Graph')
      
      # todo make these dynamic in case the user resizes
      @width = 720.0
      @height = 400.0
      
      # the canvas to draw the graph on
      @c = TkCanvas.new(@graphFrame, :width=>@width, :height=>@height, 
                           :background=>'white', :borderwidth=>2,
                           :relief=>:sunken)
      
      # variables for positioning of ui elements
      @xPad = 25
      @xWidth = @width - (@xPad * 2)
      @xInc = @xWidth / (@valueCt.to_f)
      
      # graph grid
      1.upto(19) { |i|
         y = @height - ((@height / 20) * i)
         line = TkcLine.new(@c, @xPad, y, @width - @xPad, y, :width=>1,
            :smooth=>true, :fill=>"grey90")
         if (i % 2 == 0) then
            TkcText.new(@c, 15, y, :text=>"#{i * 5}")
            TkcText.new(@c, @width - 15, y, :text=>"#{i * 5}")
            line.width = 2
            line.fill = "grey70"
         end
      }
      
      # graph path, the path elements will be positioned during animation
      @graph = Array.new(@valueCt) { |i|
         TkcLine.new(@c,0,0,0,0,:width=>2,:smooth=>true,:fill=>"red")
      }
      
      @c.pack(:fill=>:both, :expand=>true)
      
      Tk.update
      
      # add the graph's frame to the pane
      pane.add(@graphFrame)
      
      # use a timer to call the refresh method every 500 ms
      @timer = TkTimer.new($speed) { refresh }.start(500)
   end
   
   def nextValue
      @sp.putc(13)
      @sp.flush
      s = ''
      c = @sp.getc
      until c == 13
         s.concat(c)
         c = @sp.getc
      end
      return s.hex
   end
   
   def drawGraph
      pathId = 0
      # render the values from valuePos (excl) to the end
      (@valuePos + 1).upto(@valueCt - 1) { |valueId|
         updateGraphElement(pathId, valueId)
         pathId = pathId.next
      }
      # render the values from beginning to valuePos
      0.upto(@valuePos) { |valueId|
         updateGraphElement(pathId, valueId)
         pathId = pathId.next
      }
   end
   
   def updateGraphElement(pathId, valueId)
      # get the value for the current entry
      cv = @values[valueId]
      # get the index for the previous entry
      if valueId <= 0 then
         valueId = @valueCt - 1
      else
         valueId = valueId - 1
      end
      # get the value for the previous entry
      pv = @values[valueId]
      # only draw if there is something to draw
      if cv != nil && pv != nil && pathId > 0
         # the path to modify
         pathElem = @graph[pathId]
         # calculate the new coords for this path
         x1 = @xPad + (pathId * @xInc)
         x2 = @xPad + ((pathId + 1) * @xInc)
         y1 = @height - ((pv / 1024.0) * @height)
         y2 = @height - ((cv / 1024.0) * @height)
         # set the path to the correct coords
         pathElem.coords(x1, y1, x2, y2)
      end 
   end
   
   def refresh
      # update the values with the latest value
      @values[@valuePos] = nextValue
      # render the graph
      drawGraph
      # move the position in the value array along
      @valuePos = @valuePos.next
      # wrap around if necessary
      @valuePos = 0 unless @valuePos < @valueCt
   end
   
   def cleanExit
      @sp.flush
      @sp.close
      Tk.root.destroy
   end
end

$graph = AnimatedGraph.new(Tk.root)
Tk.mainloop

