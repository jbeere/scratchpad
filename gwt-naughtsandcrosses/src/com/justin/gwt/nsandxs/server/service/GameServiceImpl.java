package com.justin.gwt.nsandxs.server.service;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.justin.gwt.nsandxs.client.service.GameService;
import com.justin.gwt.nsandxs.server.GameServerBootstrap;
import com.justin.gwt.nsandxs.server.Player;
import com.justin.gwt.nsandxs.server.Server;

import javax.servlet.ServletException;

/**
 * @author justin
 */
public class GameServiceImpl extends RemoteServiceServlet implements GameService {

   private Server server;

   @Override
   public void init() throws ServletException {
      super.init();
      this.server = (Server) getServletContext().getAttribute(GameServerBootstrap.GAME_SERVER_INSTANCE);
   }

   public int join() {
      Player player = (Player) getThreadLocalRequest().getSession(true);
      return server.join(player).getId();
   }

   public void move(int gameId, int pos) {
      
   }
}