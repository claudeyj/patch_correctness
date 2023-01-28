/*   0*/package com.fasterxml.jackson.databind.util;
/*   0*/
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/
/*   0*/public class LRUMap<K, V> implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final transient int _maxEntries;
/*   0*/  
/*   0*/  protected final transient ConcurrentHashMap<K, V> _map;
/*   0*/  
/*   0*/  protected transient int _jdkSerializeMaxEntries;
/*   0*/  
/*   0*/  public LRUMap(int initialEntries, int maxEntries) {
/*  35*/    this._map = new ConcurrentHashMap<>(initialEntries, 0.8F, 4);
/*  36*/    this._maxEntries = maxEntries;
/*   0*/  }
/*   0*/  
/*   0*/  public V put(K key, V value) {
/*  40*/    if (this._map.size() >= this._maxEntries) {
/*  42*/        synchronized (this) {
/*  43*/          if (this._map.size() >= this._maxEntries) {
/*  44*/              clear(); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/*  48*/    return this._map.put(key, value);
/*   0*/  }
/*   0*/  
/*   0*/  public V putIfAbsent(K key, V value) {
/*  57*/    if (this._map.size() >= this._maxEntries) {
/*  58*/        synchronized (this) {
/*  59*/          if (this._map.size() >= this._maxEntries) {
/*  60*/              clear(); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/*  64*/    return this._map.putIfAbsent(key, value);
/*   0*/  }
/*   0*/  
/*   0*/  public V get(Object key) {
/*  68*/    this._map.get(key);
/*  68*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void clear() {
/*  70*/    this._map.clear();
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/*  71*/    return this._map.size();
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream in) throws IOException {
/*  88*/    this._jdkSerializeMaxEntries = in.readInt();
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream out) throws IOException {
/*  92*/    out.writeInt(this._jdkSerializeMaxEntries);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object readResolve() {
/*  96*/    return new LRUMap(this._jdkSerializeMaxEntries, this._jdkSerializeMaxEntries);
/*   0*/  }
/*   0*/}
