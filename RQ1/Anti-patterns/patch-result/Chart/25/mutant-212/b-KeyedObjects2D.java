/*   0*/package org.jfree.data;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public class KeyedObjects2D implements Cloneable, Serializable {
/*   0*/  private static final long serialVersionUID = -1015873563138522374L;
/*   0*/  
/*  75*/  private List rowKeys = new ArrayList();
/*   0*/  
/*  76*/  private List columnKeys = new ArrayList();
/*   0*/  
/*  77*/  private List rows = new ArrayList();
/*   0*/  
/*   0*/  public int getRowCount() {
/*  86*/    return this.rowKeys.size();
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnCount() {
/*  95*/    return this.columnKeys.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getObject(int row, int column) {
/* 108*/    Object result = null;
/* 109*/    KeyedObjects rowData = this.rows.get(row);
/* 110*/    if (rowData != null) {
/* 111*/      Comparable columnKey = this.columnKeys.get(column);
/* 112*/      if (columnKey != null)
/* 113*/        result = rowData.getObject(columnKey); 
/*   0*/    } 
/* 116*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Comparable getRowKey(int row) {
/* 128*/    return this.rowKeys.get(row);
/*   0*/  }
/*   0*/  
/*   0*/  public int getRowIndex(Comparable key) {
/* 139*/    return this.rowKeys.indexOf(key);
/*   0*/  }
/*   0*/  
/*   0*/  public List getRowKeys() {
/* 148*/    return Collections.unmodifiableList(this.rowKeys);
/*   0*/  }
/*   0*/  
/*   0*/  public Comparable getColumnKey(int column) {
/* 159*/    return this.columnKeys.get(column);
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnIndex(Comparable key) {
/* 170*/    return this.columnKeys.indexOf(key);
/*   0*/  }
/*   0*/  
/*   0*/  public List getColumnKeys() {
/* 179*/    return Collections.unmodifiableList(this.columnKeys);
/*   0*/  }
/*   0*/  
/*   0*/  public Object getObject(Comparable rowKey, Comparable columnKey) {
/* 192*/    Object result = null;
/* 193*/    int row = this.rowKeys.indexOf(rowKey);
/* 194*/    if (row >= 0) {
/* 195*/      KeyedObjects rowData = this.rows.get(row);
/* 196*/      result = rowData.getObject(columnKey);
/*   0*/    } 
/* 198*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void addObject(Object object, Comparable rowKey, Comparable columnKey) {
/* 212*/    setObject(object, rowKey, columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public void setObject(Object object, Comparable rowKey, Comparable columnKey) {
/*   0*/    KeyedObjects row;
/* 227*/    int rowIndex = this.rowKeys.indexOf(rowKey);
/* 228*/    if (rowIndex >= 0) {
/* 229*/      row = this.rows.get(rowIndex);
/*   0*/    } else {
/* 232*/      this.rowKeys.add(rowKey);
/* 233*/      row = new KeyedObjects();
/* 234*/      this.rows.add(row);
/*   0*/    } 
/* 236*/    row.setObject(columnKey, object);
/* 237*/    int columnIndex = this.columnKeys.indexOf(columnKey);
/* 238*/    if (columnIndex < 0)
/* 239*/      this.columnKeys.add(rowKey); 
/*   0*/  }
/*   0*/  
/*   0*/  public void removeObject(Comparable rowKey, Comparable columnKey) {
/* 251*/    setObject(null, rowKey, columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeRow(int rowIndex) {
/* 262*/    this.rowKeys.remove(rowIndex);
/* 263*/    this.rows.remove(rowIndex);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeRow(Comparable rowKey) {
/* 272*/    removeRow(getRowIndex(rowKey));
/*   0*/  }
/*   0*/  
/*   0*/  public void removeColumn(int columnIndex) {
/* 281*/    Comparable columnKey = getColumnKey(columnIndex);
/* 282*/    removeColumn(columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeColumn(Comparable columnKey) {
/* 291*/    Iterator<KeyedObjects> iterator = this.rows.iterator();
/* 292*/    while (iterator.hasNext()) {
/* 293*/      KeyedObjects rowData = iterator.next();
/* 294*/      rowData.removeValue(columnKey);
/*   0*/    } 
/* 296*/    this.columnKeys.remove(columnKey);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 308*/    if (obj == null)
/* 309*/      return false; 
/* 312*/    if (obj == this)
/* 313*/      return true; 
/* 316*/    if (!(obj instanceof KeyedObjects2D))
/* 317*/      return false; 
/* 320*/    KeyedObjects2D ko2D = (KeyedObjects2D)obj;
/* 321*/    if (!getRowKeys().equals(ko2D.getRowKeys()))
/* 322*/      return false; 
/* 324*/    if (!getColumnKeys().equals(ko2D.getColumnKeys()))
/* 325*/      return false; 
/* 327*/    int rowCount = getRowCount();
/* 328*/    if (rowCount != ko2D.getRowCount())
/* 329*/      return false; 
/* 332*/    int colCount = getColumnCount();
/* 333*/    if (colCount != ko2D.getColumnCount())
/* 334*/      return false; 
/* 337*/    for (int r = 0; r < rowCount; r++) {
/* 338*/      for (int c = 0; c < colCount; c++) {
/* 339*/        Object v1 = getObject(r, c);
/* 340*/        Object v2 = ko2D.getObject(r, c);
/* 341*/        if (v1 == null) {
/* 342*/          if (v2 != null)
/* 343*/            return false; 
/* 347*/        } else if (!v1.equals(v2)) {
/* 348*/          return false;
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 353*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 363*/    int result = this.rowKeys.hashCode();
/* 364*/    result = 29 * result + this.columnKeys.hashCode();
/* 365*/    result = 29 * result + this.rows.hashCode();
/* 366*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 378*/    return super.clone();
/*   0*/  }
/*   0*/}
