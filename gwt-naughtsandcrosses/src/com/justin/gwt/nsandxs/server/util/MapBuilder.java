package com.justin.gwt.nsandxs.server.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author justin
 */
public class MapBuilder<K, V> {

   private Map<K, V> map;

   public MapBuilder() {
      this(new HashMap<K, V>());
   }

   public MapBuilder(Map<K, V> map) {
      this.map = map;
   }

   public MapBuilder<K, V> put(K key, V value) {
      map.put(key, value);
      return this;
   }

   public Map<K, V> map() {
      return map;
   }
}
