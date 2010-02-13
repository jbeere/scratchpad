package com.justin.gwt.nsandxs.server;

import com.justin.gwt.nsandxs.client.event.Event;
import org.junit.Test;

import static org.junit.Assert.*;
/**
 * @author justin
 */

public class GameTest {

   @Test
   public void testGame() {
      Player p1 = new Player(1);
      Player p2 = new Player(2);

      p1.setOutput(new TestEventOutput(1));
      p2.setOutput(new TestEventOutput(2));

      Server server = new Server();

      Game game1 = server.join(p1);
      Game game2 = server.join(p2);

      game1.move(p1, 0);
      game1.move(p2, 6);
      game1.move(p1, 1);
      game1.move(p2, 3);
      game1.move(p1, 2);

      assertEquals("Should be the same game!", game1, game2);

   }

   private class TestEventOutput implements EventOutput {

      private int pNo;

      public TestEventOutput(int pNo) {
         this.pNo = pNo;
      }

      public void write(Event event) {
         System.out.printf("%d> %s%n", pNo, event);
      }
   }
}
