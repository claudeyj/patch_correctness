/*   0*/package com.fasterxml.jackson.databind.deser.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.NoSuchElementException;
/*   0*/
/*   0*/public class BeanPropertyMap implements Iterable<SettableBeanProperty>, Serializable {
/*   0*/  private static final long serialVersionUID = 2L;
/*   0*/  
/*   0*/  protected final boolean _caseInsensitive;
/*   0*/  
/*   0*/  private int _hashMask;
/*   0*/  
/*   0*/  private int _size;
/*   0*/  
/*   0*/  private int _spillCount;
/*   0*/  
/*   0*/  private Object[] _hashArea;
/*   0*/  
/*   0*/  private SettableBeanProperty[] _propsInOrder;
/*   0*/  
/*   0*/  public BeanPropertyMap(boolean caseInsensitive, Collection<SettableBeanProperty> props) {
/*  60*/    this._caseInsensitive = caseInsensitive;
/*  61*/    this._propsInOrder = props.<SettableBeanProperty>toArray(new SettableBeanProperty[props.size()]);
/*  62*/    init(props);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanPropertyMap(BeanPropertyMap base, boolean caseInsensitive) {
/*  70*/    this._caseInsensitive = caseInsensitive;
/*  74*/    this._propsInOrder = Arrays.<SettableBeanProperty>copyOf(base._propsInOrder, base._propsInOrder.length);
/*  75*/    init(Arrays.asList(this._propsInOrder));
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyMap withCaseInsensitivity(boolean state) {
/*  86*/    if (this._caseInsensitive == state) {
/*  87*/        return this; 
/*   0*/       }
/*  89*/    return new BeanPropertyMap(this, state);
/*   0*/  }
/*   0*/  
/*   0*/  protected void init(Collection<SettableBeanProperty> props) {
/*  94*/    this._size = props.size();
/*  97*/    int hashSize = findSize(this._size);
/*  98*/    this._hashMask = hashSize - 1;
/* 101*/    int alloc = (hashSize + (hashSize >> 1)) * 2;
/* 102*/    Object[] hashed = new Object[alloc];
/* 103*/    int spillCount = 0;
/* 105*/    for (SettableBeanProperty prop : props) {
/* 107*/      if (prop == null) {
/*   0*/          continue; 
/*   0*/         }
/* 111*/      String key = getPropertyName(prop);
/* 112*/      int slot = _hashCode(key);
/* 113*/      int ix = slot << 1;
/* 116*/      if (hashed[ix] != null) {
/* 118*/        ix = hashSize + (slot >> 1) << 1;
/* 119*/        if (hashed[ix] != null) {
/* 121*/          ix = (hashSize + (hashSize >> 1) << 1) + spillCount;
/* 122*/          spillCount += 2;
/* 123*/          if (ix >= hashed.length) {
/* 124*/              hashed = Arrays.copyOf(hashed, hashed.length + 4); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/* 129*/      hashed[ix] = key;
/* 130*/      hashed[ix + 1] = prop;
/*   0*/    } 
/* 137*/    this._hashArea = hashed;
/* 138*/    this._spillCount = spillCount;
/*   0*/  }
/*   0*/  
/*   0*/  private static final int findSize(int size) {
/* 143*/    if (size <= 5) {
/* 144*/        return 8; 
/*   0*/       }
/* 146*/    if (size <= 12) {
/* 147*/        return 16; 
/*   0*/       }
/* 149*/    int needed = size + (size >> 2);
/* 150*/    int result = 32;
/* 151*/    while (result < needed) {
/* 152*/        result += result; 
/*   0*/       }
/* 154*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static BeanPropertyMap construct(Collection<SettableBeanProperty> props, boolean caseInsensitive) {
/* 161*/    return new BeanPropertyMap(caseInsensitive, props);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyMap withProperty(SettableBeanProperty newProp) {
/* 174*/    String key = getPropertyName(newProp);
/* 176*/    for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 177*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 178*/      if (prop != null && prop.getName().equals(key)) {
/* 179*/        this._hashArea[i] = newProp;
/* 180*/        this._propsInOrder[_findFromOrdered(prop)] = newProp;
/* 181*/        return this;
/*   0*/      } 
/*   0*/    } 
/* 185*/    int slot = _hashCode(key);
/* 186*/    int hashSize = this._hashMask + 1;
/* 187*/    int ix = slot << 1;
/* 190*/    if (this._hashArea[ix] != null) {
/* 192*/      ix = hashSize + (slot >> 1) << 1;
/* 193*/      if (this._hashArea[ix] != null) {
/* 195*/        ix = (hashSize + (hashSize >> 1) << 1) + this._spillCount;
/* 196*/        this._spillCount += 2;
/* 197*/        if (ix >= this._hashArea.length) {
/* 198*/            this._hashArea = Arrays.copyOf(this._hashArea, this._hashArea.length + 4); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 212*/    this._hashArea[ix] = key;
/* 213*/    this._hashArea[ix + 1] = newProp;
/* 215*/    int last = this._propsInOrder.length;
/* 216*/    this._propsInOrder = Arrays.<SettableBeanProperty>copyOf(this._propsInOrder, last + 1);
/* 217*/    this._propsInOrder[last] = newProp;
/* 221*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyMap assignIndexes() {
/* 227*/    int index = 0;
/* 228*/    for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 229*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 230*/      if (prop != null) {
/* 231*/          prop.assignIndex(index++); 
/*   0*/         }
/*   0*/    } 
/* 234*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyMap renameAll(NameTransformer transformer) {
/* 243*/    if (transformer == null || transformer == NameTransformer.NOP) {
/* 244*/        return this; 
/*   0*/       }
/* 247*/    int len = this._propsInOrder.length;
/* 248*/    ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
/* 250*/    for (int i = 0; i < len; i++) {
/* 251*/      SettableBeanProperty prop = this._propsInOrder[i];
/* 254*/      if (prop == null) {
/* 255*/        newProps.add(prop);
/*   0*/      } else {
/* 258*/        newProps.add(_rename(prop, transformer));
/*   0*/      } 
/*   0*/    } 
/* 261*/    return new BeanPropertyMap(this._caseInsensitive, newProps);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanPropertyMap withoutProperties(Collection<String> toExclude) {
/* 273*/    if (toExclude.isEmpty()) {
/* 274*/        return this; 
/*   0*/       }
/* 276*/    int len = this._propsInOrder.length;
/* 277*/    ArrayList<SettableBeanProperty> newProps = new ArrayList<>(len);
/* 279*/    for (int i = 0; i < len; i++) {
/* 280*/      SettableBeanProperty prop = this._propsInOrder[i];
/* 284*/      if (prop != null && 
/* 285*/        !toExclude.contains(prop.getName())) {
/* 286*/          newProps.add(prop); 
/*   0*/         }
/*   0*/    } 
/* 291*/    return new BeanPropertyMap(this._caseInsensitive, newProps);
/*   0*/  }
/*   0*/  
/*   0*/  public void replace(SettableBeanProperty newProp) {
/* 301*/    String key = getPropertyName(newProp);
/* 302*/    int ix = _findIndexInHash(key);
/* 304*/    if (ix >= 0) {
/* 305*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[ix];
/* 306*/      this._hashArea[ix] = newProp;
/* 308*/      this._propsInOrder[_findFromOrdered(prop)] = newProp;
/*   0*/      return;
/*   0*/    } 
/* 312*/    throw new NoSuchElementException("No entry '" + key + "' found, can't replace");
/*   0*/  }
/*   0*/  
/*   0*/  private List<SettableBeanProperty> properties() {
/* 316*/    ArrayList<SettableBeanProperty> p = new ArrayList<>(this._size);
/* 317*/    for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 318*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 319*/      if (prop != null) {
/* 320*/          p.add(prop); 
/*   0*/         }
/*   0*/    } 
/* 323*/    return p;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<SettableBeanProperty> iterator() {
/* 331*/    return properties().iterator();
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty[] getPropertiesInInsertionOrder() {
/* 343*/    return this._propsInOrder;
/*   0*/  }
/*   0*/  
/*   0*/  protected final String getPropertyName(SettableBeanProperty prop) {
/* 349*/    return this._caseInsensitive ? prop.getName().toLowerCase() : prop.getName();
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty find(int index) {
/* 359*/    for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 360*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 361*/      if (prop != null && index == prop.getPropertyIndex()) {
/* 362*/          return prop; 
/*   0*/         }
/*   0*/    } 
/* 365*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty find(String key) {
/* 370*/    if (key == null) {
/* 371*/        throw new IllegalArgumentException("Can not pass null property name"); 
/*   0*/       }
/* 373*/    if (this._caseInsensitive) {
/* 374*/        key = key.toLowerCase(); 
/*   0*/       }
/* 378*/    int slot = key.hashCode() & this._hashMask;
/* 382*/    int ix = slot << 1;
/* 383*/    Object match = this._hashArea[ix];
/* 384*/    if (match == key || key.equals(match)) {
/* 385*/        return (SettableBeanProperty)this._hashArea[ix + 1]; 
/*   0*/       }
/* 387*/    return _find2(key, slot, match);
/*   0*/  }
/*   0*/  
/*   0*/  private final SettableBeanProperty _find2(String key, int slot, Object match) {
/* 392*/    if (match == null) {
/* 393*/        return null; 
/*   0*/       }
/* 396*/    int hashSize = this._hashMask + 1;
/* 397*/    int ix = hashSize + (slot >> 1) << 1;
/* 398*/    match = this._hashArea[ix];
/* 399*/    if (key.equals(match)) {
/* 400*/        return (SettableBeanProperty)this._hashArea[ix + 1]; 
/*   0*/       }
/* 402*/    if (match != null) {
/* 403*/      int i = hashSize + (hashSize >> 1) << 1;
/* 404*/      for (int end = i + this._spillCount; i < end; i += 2) {
/* 405*/        match = this._hashArea[i];
/* 406*/        if (match == key || key.equals(match)) {
/* 407*/            return (SettableBeanProperty)this._hashArea[i + 1]; 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 411*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 420*/    return this._size;
/*   0*/  }
/*   0*/  
/*   0*/  public void remove(SettableBeanProperty propToRm) {
/* 428*/    ArrayList<SettableBeanProperty> props = new ArrayList<>(this._size);
/* 429*/    String key = getPropertyName(propToRm);
/*   0*/    boolean found = false;
/* 432*/    for (int i = 1, end = this._hashArea.length; i < end; i += 2) {
/* 433*/      SettableBeanProperty prop = (SettableBeanProperty)this._hashArea[i];
/* 434*/      if (prop == null) {
/*   0*/          continue; 
/*   0*/         }
/* 437*/      if (!found) {
/* 440*/        found = key.equals(prop.getName());
/* 441*/        if (found) {
/* 443*/          this._propsInOrder[_findFromOrdered(prop)] = null;
/*   0*/          continue;
/*   0*/        } 
/*   0*/      } 
/* 447*/      props.add(prop);
/*   0*/    } 
/* 449*/    if (!found) {
/* 450*/        throw new NoSuchElementException("No entry '" + propToRm.getName() + "' found, can't remove"); 
/*   0*/       }
/* 452*/    init(props);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean findDeserializeAndSet(JsonParser p, DeserializationContext ctxt, Object bean, String key) throws IOException {
/* 467*/    SettableBeanProperty prop = find(key);
/* 468*/    if (prop == null) {
/* 469*/        return false; 
/*   0*/       }
/*   0*/    try {
/* 472*/      prop.deserializeAndSet(p, ctxt, bean);
/* 473*/    } catch (Exception e) {
/* 474*/      wrapAndThrow(e, bean, key, ctxt);
/*   0*/    } 
/* 476*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 482*/    StringBuilder sb = new StringBuilder();
/* 483*/    sb.append("Properties=[");
/* 484*/    int count = 0;
/* 486*/    Iterator<SettableBeanProperty> it = iterator();
/* 487*/    while (it.hasNext()) {
/* 488*/      SettableBeanProperty prop = it.next();
/* 489*/      if (count++ > 0) {
/* 490*/          sb.append(", "); 
/*   0*/         }
/* 492*/      sb.append(prop.getName());
/* 493*/      sb.append('(');
/* 494*/      sb.append(prop.getType());
/* 495*/      sb.append(')');
/*   0*/    } 
/* 497*/    sb.append(']');
/* 498*/    return sb.toString();
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty _rename(SettableBeanProperty prop, NameTransformer xf) {
/* 509*/    if (prop == null) {
/* 510*/        return prop; 
/*   0*/       }
/* 512*/    String newName = xf.transform(prop.getName());
/* 513*/    prop = prop.withSimpleName(newName);
/* 514*/    JsonDeserializer<?> deser = prop.getValueDeserializer();
/* 515*/    if (deser != null) {
/* 517*/      JsonDeserializer<Object> newDeser = (JsonDeserializer)deser.unwrappingDeserializer(xf);
/* 519*/      if (newDeser != deser) {
/* 520*/          prop = prop.withValueDeserializer(newDeser); 
/*   0*/         }
/*   0*/    } 
/* 523*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
/* 530*/    while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/* 531*/        t = t.getCause(); 
/*   0*/       }
/* 534*/    if (t instanceof Error) {
/* 535*/        throw (Error)t; 
/*   0*/       }
/* 538*/    boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/* 540*/    if (t instanceof IOException) {
/* 541*/      if (!wrap || !(t instanceof com.fasterxml.jackson.core.JsonProcessingException)) {
/* 542*/          throw (IOException)t; 
/*   0*/         }
/* 544*/    } else if (!wrap && 
/* 545*/      t instanceof RuntimeException) {
/* 546*/      throw (RuntimeException)t;
/*   0*/    } 
/* 549*/    throw JsonMappingException.wrapWithPath(t, bean, fieldName);
/*   0*/  }
/*   0*/  
/*   0*/  private final int _findIndexInHash(String key) {
/* 562*/    int slot = _hashCode(key);
/* 563*/    int ix = slot << 1;
/* 566*/    if (key.equals(this._hashArea[ix])) {
/* 567*/        return ix + 1; 
/*   0*/       }
/* 570*/    int hashSize = this._hashMask + 1;
/* 571*/    ix = hashSize + (slot >> 1) << 1;
/* 572*/    if (key.equals(this._hashArea[ix])) {
/* 573*/        return ix + 1; 
/*   0*/       }
/* 576*/    int i = hashSize + (hashSize >> 1) << 1;
/* 577*/    for (int end = i + this._spillCount; i < end; i += 2) {
/* 578*/      if (key.equals(this._hashArea[i])) {
/* 579*/          return i + 1; 
/*   0*/         }
/*   0*/    } 
/* 582*/    return -1;
/*   0*/  }
/*   0*/  
/*   0*/  private final int _findFromOrdered(SettableBeanProperty prop) {
/* 586*/    for (int i = 0, end = this._propsInOrder.length; i < end; i++) {
/* 587*/      if (this._propsInOrder[i] == prop) {
/* 588*/          return i; 
/*   0*/         }
/*   0*/    } 
/* 591*/    throw new IllegalStateException("Illegal state: property '" + prop.getName() + "' missing from _propsInOrder");
/*   0*/  }
/*   0*/  
/*   0*/  private final int _hashCode(String key) {
/* 605*/    return key.hashCode() & this._hashMask;
/*   0*/  }
/*   0*/}
