package com.example.holdsafetyadmin;

import java.util.HashMap;

class HashMapInteger<K> extends HashMap<K,Integer> {
    public void increment(K key) {
        if(super.containsKey(key))
            super.put(key,super.get(key)+1);
        else
            super.put(key,new Integer(1));
    }

    public void increment(K key, int val) {
        if(super.containsKey(key))
            super.put(key,super.get(key)+val);
        else
            super.put(key,new Integer(val));
    }
}
