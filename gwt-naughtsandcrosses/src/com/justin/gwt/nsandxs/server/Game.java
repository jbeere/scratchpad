package com.justin.gwt.nsandxs.server;

/**
 * @author justin
 */
public class Game {

   private static final int[][] combos = new int[][]{
         new int[]{0, 1, 2},
         new int[]{3, 4, 5},
         new int[]{6, 7, 8},
         new int[]{0, 3, 6},
         new int[]{1, 4, 7},
         new int[]{2, 5, 8},
         new int[]{0, 4, 8},
         new int[]{2, 4, 6}
   };

   private Integer id;

   private Player p1;
   private Player p2;

   private int[] matrix;

   public Game(Integer id, Player p1) {
      this.id = id;
      this.p1 = p1;
      this.matrix = new int[9];
      for(int i = 0; i < matrix.length; i++) {
         matrix[i] = 0;
      }
   }

   public Integer getId() {
      return id;
   }

   public void setPlayer2(Player p2) {
      this.p2 = p2;
      this.p1.start(id, 1, p2.getName());
      this.p2.start(id, 2, p1.getName());
   }

   public void move(Player player, int pos) {
      if (matrix[pos] != 0) {
         return;
      }
      
      int pNo = -1;
      if (player.equals(p1)) {
         pNo = 1;
      } else if (player.equals(p2)) {
         pNo = 2;
      }
      matrix[pos] = pNo;
      p1.move(id, pNo, pos);
      p2.move(id, pNo, pos);
      if (updateStatus()) {
         p1.end(id);
         p2.end(id);
      }
   }

   private boolean updateStatus() {
      int prev;
      for (int[] combo : combos) {
         prev = -1;
         for (int i = 0; i < combo.length; i++) {
            if (matrix[i] == 0) {
               break;
            }
            if (prev != -1 && matrix[i] != prev) {
               break;
            }
            if (i == combo.length - 1) {
               Player winner = prev == 1 ? p1 : p2;
               Player loser = prev == 1 ? p2 : p1;

               winner.win(id);
               loser.lose(id);
               
               return true;
            }
            prev = matrix[i];
         }
      }
      return false;
   }

   public int hashCode() {
      return id.hashCode();
   }

   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }
      if (!(obj instanceof Player)) {
         return false;
      }
      return id.equals(((Player) obj).getId());
   }

   public String toString() {
      return String.format("Game #%d", id);
   }
}
