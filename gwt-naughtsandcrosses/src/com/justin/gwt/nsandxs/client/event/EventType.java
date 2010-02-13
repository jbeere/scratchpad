package com.justin.gwt.nsandxs.client.event;

import java.util.Map;

/**
 * jeb 11 Feb 2010 12:47:48 PM
 */
public enum EventType {

   GAME,
   START,
   MOVE,
   WIN,
   LOSE,
   END;

   public Event createEvent(Map<String, String> properties) {
      return new Event(this, properties);
   }
}
