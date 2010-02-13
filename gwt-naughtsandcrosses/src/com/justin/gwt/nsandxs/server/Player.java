package com.justin.gwt.nsandxs.server;

import com.justin.gwt.nsandxs.client.event.Event;
import com.justin.gwt.nsandxs.client.event.EventType;
import com.justin.gwt.nsandxs.server.util.MapBuilder;

import java.util.Map;
import java.util.Observable;

/**
 * @author justin
 */
public class Player extends Observable {

   private Integer id;
   private String name;

   private volatile EventOutput output;

   public Player(Integer id) {
      this.id = id;
      this.name = "Player " + id;
   }

   public Integer getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setOutput(EventOutput output) {
      this.output = output;
   }

   public void win(int gameId) {
      writeEvent(EventType.WIN, new MapBuilder<String, String>()
            .put("game.id", String.valueOf(gameId))
            .map());
   }

   public void lose(int gameId) {
      writeEvent(EventType.LOSE, new MapBuilder<String, String>()
            .put("game.id", String.valueOf(gameId))
            .map());
   }

   public void start(int gameId, int pNo, String opponentName) {
      writeEvent(EventType.START, new MapBuilder<String, String>()
            .put("game.id", String.valueOf(gameId))
            .put("player.number", String.valueOf(pNo))
            .put("opponent.name", opponentName)
            .map());
   }

   public void move(int gameId, int player, int pos) {
      writeEvent(EventType.MOVE, new MapBuilder<String, String>()
            .put("game.id", String.valueOf(gameId))
            .put("player.no", String.valueOf(player))
            .put("move.position", String.valueOf(pos))
            .map());
   }

   public void end(int gameId) {
      writeEvent(EventType.END, new MapBuilder<String, String>()
            .put("game.id", String.valueOf(gameId))
            .map());
   }

   private void writeEvent(EventType type, Map<String, String> properties) {
      Event event = type.createEvent(properties);
      output.write(event);
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
}
