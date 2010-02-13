package com.justin.gwt.nsandxs.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;
import com.justin.gwt.nsandxs.client.model.GameModel;
import com.justin.gwt.nsandxs.client.model.GameModelListener;
import com.justin.gwt.nsandxs.client.model.Symbol;

import java.util.ArrayList;
import java.util.List;

/**
 * jeb 12 Feb 2010 8:01:28 AM
 */
public class GameView extends FocusPanel {

   private final GWTCanvas canvas;
   private final GameModel model;

   private volatile int mousePosX = 0;
   private volatile int mousePosY = 0;

   private final List<GameViewListener> listeners;

   public GameView(GameModel model) {
      this.canvas = new GWTCanvas(400, 400);
      this.model = model;
      this.listeners = new ArrayList<GameViewListener>(0);
      init();
   }

   public void addListener(GameViewListener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(GameViewListener listener) {
      this.listeners.remove(listener);
   }

   private void init() {
      model.addListener(new GameModelListener() {
         public void update(GameModel nsandxs) {
            redraw();
         }
      });

      addMouseMoveHandler(new MouseMoveHandler() {
         public void onMouseMove(MouseMoveEvent event) {
            mousePosX = event.getX();
            mousePosY = event.getY();
         }
      });

      addClickHandler(new ClickHandler() {
         public void onClick(ClickEvent event) {
            select();
         }
      });

      setWidget(canvas);
      redraw();
   }

   private void select() {
      int width = canvas.getCoordWidth();
      int height = canvas.getCoordHeight();
      int size = model.getSize();

      int col = 0, row = 0;

      double blockSizeX = width / size;
      double colPos = mousePosX;
      for (int i = 0; i < size; i++) {
         double testPos = blockSizeX * (i + 1);
         if (colPos < testPos) {
            col = i;
            break;
         }
      }

      double blockSizeY = height / size;
      double rowPos = mousePosY;
      for (int i = 0; i < size; i++) {
         double testPos = (blockSizeY * (i + 1));
         if (rowPos < testPos) {
            row = i;
            break;
         }
      }

      int pos = (row * size) + col;
      fireSelected(pos);
   }

   private void fireSelected(int pos) {
      for (GameViewListener listener : listeners) {
         listener.boxSelected(pos);
      }
   }


   private void redraw() {
      int width = canvas.getCoordWidth();
      int height = canvas.getCoordHeight();
      int size = model.getSize();

      double pixelSize = width < height ? width : height;
      double boxSize = (pixelSize / size) - 1;

      canvas.clear();

      canvas.setLineWidth(pixelSize / 100D);
      canvas.setStrokeStyle(Color.BLACK);

      // draw the rows and columns
      canvas.beginPath();
      for (int i = 1; i < size; i++) {

         // column
         canvas.moveTo(boxSize * i, height);
         canvas.lineTo(boxSize * i, 0);

         // row
         canvas.moveTo(0, boxSize * i);
         canvas.lineTo(width, boxSize * i);
      }
      canvas.stroke();

      // draw the symbols
      for (int row = 0; row < size; row++) {
         for (int col = 0; col < size; col++) {
            Symbol symbol = model.getSymbol((row * size) + col);
            if (symbol != null) {
               canvas.saveContext();
               canvas.translate(boxSize * col, boxSize * row);
               drawSymbol(symbol, boxSize);
               canvas.restoreContext();
            }
         }
      }
   }

   private void drawSymbol(Symbol symbol, double boxSize) {
      double scale = 0.4D;
      canvas.saveContext();
      canvas.scale(1 - scale, 1 - scale);
      canvas.translate(boxSize * scale, boxSize * scale);
      canvas.setLineWidth(boxSize / 16);
      canvas.beginPath();
      switch (symbol) {
         case NAUGHT:
            canvas.arc(boxSize / 2, boxSize / 2, boxSize / 2, 0, Math.PI * 2, false);
            break;
         case CROSS:
            canvas.moveTo(0, boxSize);
            canvas.lineTo(boxSize, 0);
            canvas.moveTo(0, 0);
            canvas.lineTo(boxSize, boxSize);
            break;
      }
      canvas.stroke();
      canvas.restoreContext();
   }
}
