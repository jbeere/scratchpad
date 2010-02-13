package com.justin.gwt.nsandxs.server;

import com.justin.gwt.nsandxs.server.Game;
import com.justin.gwt.nsandxs.server.Player;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author justin
 */
public class Server {

   private final Queue<Game> gamesWaiting;
   private final Queue<Game> gamesBusy;

   private int gameId = 0;

   public Server() {
      gamesWaiting = new LinkedList<Game>();
      gamesBusy = new LinkedList<Game>();
   }

   public Game join(Player player) {
      Game game = gamesWaiting.poll();
      if (game == null) {
         game = new Game(getNextGameId(), player);
         gamesWaiting.add(game);
      } else {
         game.setPlayer2(player);
         gamesBusy.add(game);
      }
      return game;
   }

   private synchronized int getNextGameId() {
      return gameId++;
   }
}
