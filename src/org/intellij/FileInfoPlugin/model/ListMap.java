package org.intellij.FileInfoPlugin.model;

import java.util.*;

public class ListMap<K, V> extends HashMap<K, V> {

    List<K> keyList = new ArrayList<K>();

    public ListMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ListMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ListMap() {
    }

    public ListMap(Map<K, V> m) {
        super(m);
    }

    public V put(K key, V value) {
        if (!keyList.contains(key)) keyList.add(key);
        return super.put(key, value);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<? extends K> keyIt = m.keySet().iterator();
        while (keyIt.hasNext()) {
            K key = keyIt.next();
            if (!keyList.contains(key)) keyList.add(key);
        }
        super.putAll(m);
    }

    public V remove(Object key) {
        if (keyList.contains(key)) keyList.remove(key);
        return super.remove(key);
    }

    public void clear() {
        keyList.clear();
        super.clear();
    }

    public Set<K> keySet() {
        return new HashSet<K>(keyList);
    }

}
