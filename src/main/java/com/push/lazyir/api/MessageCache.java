package com.push.lazyir.api;

import java.util.HashMap;

class MessageCache {
     private HashMap<String,String> cache = new HashMap<>();

    public void warm() {
        cache.put("GETINFO","{ \"multipleVids\": \"false\", \"command\": \"getInfo\" }");
    }

     public String getCachedMessage(String type) {
       return cache.get(type);
     }
 }
