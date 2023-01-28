/*   0*/package com.fasterxml.jackson.core;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.io.NumberInput;
/*   0*/
/*   0*/public class JsonPointer {
/*  26*/  protected static final JsonPointer EMPTY = new JsonPointer();
/*   0*/  
/*   0*/  protected final JsonPointer _nextSegment;
/*   0*/  
/*   0*/  protected final String _asString;
/*   0*/  
/*   0*/  protected final String _matchingPropertyName;
/*   0*/  
/*   0*/  protected final int _matchingElementIndex;
/*   0*/  
/*   0*/  protected JsonPointer() {
/*  56*/    this._nextSegment = null;
/*  57*/    this._matchingPropertyName = "";
/*  58*/    this._matchingElementIndex = -1;
/*  59*/    this._asString = "";
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonPointer(String fullString, String segment, JsonPointer next) {
/*  66*/    this._asString = fullString;
/*  67*/    this._nextSegment = next;
/*  69*/    this._matchingPropertyName = segment;
/*  70*/    this._matchingElementIndex = _parseIndex(segment);
/*   0*/  }
/*   0*/  
/*   0*/  public static JsonPointer compile(String input) throws IllegalArgumentException {
/*  91*/    if (input == null || input.length() == 0) {
/*  92*/        return EMPTY; 
/*   0*/       }
/*  95*/    if (input.charAt(0) != '/') {
/*  96*/        throw new IllegalArgumentException("Invalid input: JSON Pointer expression must start with '/': \"" + input + "\""); 
/*   0*/       }
/*  98*/    return _parseTail(input);
/*   0*/  }
/*   0*/  
/*   0*/  public static JsonPointer valueOf(String input) {
/* 105*/    return compile(input);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matches() {
/* 135*/    return (this._nextSegment == null);
/*   0*/  }
/*   0*/  
/*   0*/  public String getMatchingProperty() {
/* 136*/    return this._matchingPropertyName;
/*   0*/  }
/*   0*/  
/*   0*/  public int getMatchingIndex() {
/* 137*/    return this._matchingElementIndex;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean mayMatchProperty() {
/* 138*/    return (this._matchingPropertyName != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean mayMatchElement() {
/* 139*/    return (this._matchingElementIndex >= 0);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonPointer matchProperty(String name) {
/* 142*/    if (this._nextSegment == null || !this._matchingPropertyName.equals(name)) {
/* 143*/        return null; 
/*   0*/       }
/* 145*/    return this._nextSegment;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonPointer matchElement(int index) {
/* 149*/    if (index != this._matchingElementIndex || index < 0) {
/* 150*/        return null; 
/*   0*/       }
/* 152*/    return this._nextSegment;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonPointer tail() {
/* 160*/    return this._nextSegment;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 169*/    return this._asString;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 170*/    return this._asString.hashCode();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/* 173*/    if (o == this) {
/* 173*/        return true; 
/*   0*/       }
/* 174*/    if (o == null) {
/* 174*/        return false; 
/*   0*/       }
/* 175*/    if (!(o instanceof JsonPointer)) {
/* 175*/        return false; 
/*   0*/       }
/* 176*/    return this._asString.equals(((JsonPointer)o)._asString);
/*   0*/  }
/*   0*/  
/*   0*/  private static final int _parseIndex(String str) {
/* 186*/    int len = str.length();
/* 189*/    if (len == 0 || len > 10) {
/* 190*/        return -1; 
/*   0*/       }
/* 192*/    for (int i = 0; i < len; i++) {
/* 193*/      char c = str.charAt(i);
/* 194*/      if (c > '9' || c < '0') {
/* 195*/          return -1; 
/*   0*/         }
/*   0*/    } 
/* 198*/    if (len == 10) {
/* 199*/      long l = NumberInput.parseLong(str);
/* 200*/      if (l > 2147483647L) {
/* 201*/          return -1; 
/*   0*/         }
/*   0*/    } 
/* 204*/    return NumberInput.parseInt(str);
/*   0*/  }
/*   0*/  
/*   0*/  protected static JsonPointer _parseTail(String input) {
/* 208*/    int end = input.length();
/* 211*/    for (int i = 1; i < end; ) {
/* 212*/      char c = input.charAt(i);
/* 213*/      if (c == '/') {
/* 214*/          return new JsonPointer(input, input.substring(1, i), _parseTail(input.substring(i))); 
/*   0*/         }
/* 217*/      i++;
/* 219*/      if (c == '~' && i < end) {
/* 220*/          return _parseQuotedTail(input, i); 
/*   0*/         }
/*   0*/    } 
/* 225*/    return new JsonPointer(input, input.substring(1), EMPTY);
/*   0*/  }
/*   0*/  
/*   0*/  protected static JsonPointer _parseQuotedTail(String input, int i) {
/* 236*/    int end = input.length();
/* 237*/    StringBuilder sb = new StringBuilder(Math.max(16, end));
/* 238*/    if (i > 2) {
/* 239*/        sb.append(input, 1, i - 1); 
/*   0*/       }
/* 241*/    _appendEscape(sb, input.charAt(i++));
/* 242*/    while (i < end) {
/* 243*/      char c = input.charAt(i);
/* 244*/      if (c == '/') {
/* 245*/          return new JsonPointer(input, sb.toString(), _parseTail(input.substring(i))); 
/*   0*/         }
/* 248*/      i++;
/* 249*/      if (c == '~' && i < end) {
/* 250*/        _appendEscape(sb, input.charAt(i++));
/*   0*/        continue;
/*   0*/      } 
/* 253*/      sb.append(c);
/*   0*/    } 
/* 256*/    return new JsonPointer(input, sb.toString(), EMPTY);
/*   0*/  }
/*   0*/  
/*   0*/  private static void _appendEscape(StringBuilder sb, char c) {
/* 260*/    if (c == '0') {
/* 261*/      c = '~';
/* 262*/    } else if (c == '1') {
/* 263*/      c = '/';
/*   0*/    } else {
/* 265*/      sb.append('~');
/*   0*/    } 
/* 267*/    sb.append(c);
/*   0*/  }
/*   0*/}
