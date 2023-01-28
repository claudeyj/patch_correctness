/*   0*/package org.apache.commons.csv;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public final class CSVRecord implements Serializable, Iterable<String> {
/*  35*/  private static final String[] EMPTY_STRING_ARRAY = new String[0];
/*   0*/  
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  private final String comment;
/*   0*/  
/*   0*/  private final Map<String, Integer> mapping;
/*   0*/  
/*   0*/  private final long recordNumber;
/*   0*/  
/*   0*/  private final String[] values;
/*   0*/  
/*   0*/  CSVRecord(String[] values, Map<String, Integer> mapping, String comment, long recordNumber) {
/*  53*/    this.recordNumber = recordNumber;
/*  54*/    this.values = (values != null) ? values : EMPTY_STRING_ARRAY;
/*  55*/    this.mapping = mapping;
/*  56*/    this.comment = comment;
/*   0*/  }
/*   0*/  
/*   0*/  public String get(Enum<?> e) {
/*  67*/    return get(e.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public String get(int i) {
/*  78*/    return this.values[i];
/*   0*/  }
/*   0*/  
/*   0*/  public String get(String name) {
/*  95*/    if (this.mapping == null) {
/*  96*/        throw new IllegalStateException("No header mapping was specified, the record values can't be accessed by name"); 
/*   0*/       }
/*  99*/    Integer index = this.mapping.get(name);
/* 100*/    if (index == null) {
/* 101*/        throw new IllegalArgumentException(String.format("Mapping for %s not found, expected one of %s", new Object[] { name, this.mapping.keySet() })); 
/*   0*/       }
/*   0*/    try {
/* 105*/      return this.values[index];
/* 106*/    } catch (ArrayIndexOutOfBoundsException e) {
/* 107*/      throw new IllegalArgumentException(String.format("Index for header '%s' is %d but CSVRecord only has %d values!", new Object[] { name, index, this.values.length }));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String getComment() {
/* 120*/    return this.comment;
/*   0*/  }
/*   0*/  
/*   0*/  public long getRecordNumber() {
/* 129*/    return this.recordNumber;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isConsistent() {
/* 139*/    return (this.mapping == null || this.mapping.size() == this.values.length);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isMapped(String name) {
/* 150*/    return (this.mapping != null && this.mapping.containsKey(name));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSet(String name) {
/* 161*/    return (isMapped(name) && (Integer)this.mapping.get(name) < this.values.length);
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<String> iterator() {
/* 170*/    return toList().iterator();
/*   0*/  }
/*   0*/  
/*   0*/  <M extends Map<String, String>> M putIn(M map) {
/* 180*/    for (Map.Entry<String, Integer> entry : this.mapping.entrySet()) {
/* 181*/      int col = (Integer)entry.getValue();
/* 182*/      if (col < this.values.length) {
/* 183*/          map.put(entry.getKey(), this.values[col]); 
/*   0*/         }
/*   0*/    } 
/* 186*/    return map;
/*   0*/  }
/*   0*/  
/*   0*/  public int size() {
/* 195*/    return this.values.length;
/*   0*/  }
/*   0*/  
/*   0*/  private List<String> toList() {
/* 205*/    return Arrays.asList(this.values);
/*   0*/  }
/*   0*/  
/*   0*/  public Map<String, String> toMap() {
/* 214*/    return putIn(new HashMap<String, String>(this.values.length));
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 219*/    return Arrays.toString((Object[])this.values);
/*   0*/  }
/*   0*/  
/*   0*/  String[] values() {
/* 223*/    return this.values;
/*   0*/  }
/*   0*/}
