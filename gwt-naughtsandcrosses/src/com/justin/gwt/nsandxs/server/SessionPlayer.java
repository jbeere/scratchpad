package com.justin.gwt.nsandxs.server;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author justin
 */
public class SessionPlayer implements HttpSessionListener {

   public static final String SESSION_PLAYER_ATTRIBUTE = "player";

   private int id;

   public void sessionCreated(HttpSessionEvent event) {
      Player player = new Player(getNextId());
      event.getSession().setAttribute(SESSION_PLAYER_ATTRIBUTE, player);
   }

   public void sessionDestroyed(HttpSessionEvent event) {
      
   }

   private synchronized int getNextId() {
      return id++;
   }
}
