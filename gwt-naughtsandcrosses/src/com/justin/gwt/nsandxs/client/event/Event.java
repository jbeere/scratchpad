package com.justin.gwt.nsandxs.client.event;

import java.io.Serializable;
import java.util.Map;

/**
 * jeb 11 Feb 2010 12:47:30 PM
 */
public class Event implements Serializable {

    private EventType type;
    private Map<String, String> properties;

    Event(EventType type, Map<String, String> properties) {
        this.type = type;
        this.properties = properties;
    }

    public EventType getType() {
        return type;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public String toString() {
       return String.format("%s: %s", type, properties);
    }
}
