package com.justin.gwt.nsandxs.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * jeb 11 Feb 2010 12:51:08 PM
 */
public class GameModel {

   private Symbol[] matrix;

   private final List<GameModelListener> listeners;

   public GameModel() {
      this(3);
   }

   public GameModel(int size) {
      this.matrix = new Symbol[size * size];
      this.listeners = new ArrayList<GameModelListener>(1);

      buildMatrix();
   }

   private void buildMatrix() {
      for (int i = 0; i < matrix.length; i++) {
         matrix[i] = null;
      }
   }

   public void place(Symbol symbol, int pos) {
      matrix[pos] = symbol;
      fireUpdateEvent();
   }

   public int getSize() {
      return (int) Math.sqrt(matrix.length);
   }

   public Symbol getSymbol(int pos) {
      return matrix[pos];
   }

   public void addListener(GameModelListener listener) {
      listeners.add(listener);
   }

   public void removeListener(GameModelListener listener) {
      listeners.remove(listener);
   }

   private void fireUpdateEvent() {
      for (GameModelListener listener : listeners) {
         listener.update(this);
      }
   }
}
