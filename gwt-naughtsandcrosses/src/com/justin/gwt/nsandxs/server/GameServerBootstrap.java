package com.justin.gwt.nsandxs.server;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author justin
 */
public class GameServerBootstrap implements ServletContextListener {

   public static final String GAME_SERVER_INSTANCE = "game.server.instance";

   public void contextInitialized(ServletContextEvent event) {
      Server server = new Server();
      event.getServletContext().setAttribute(GAME_SERVER_INSTANCE, server);
   }

   public void contextDestroyed(ServletContextEvent event) {
   }
}
