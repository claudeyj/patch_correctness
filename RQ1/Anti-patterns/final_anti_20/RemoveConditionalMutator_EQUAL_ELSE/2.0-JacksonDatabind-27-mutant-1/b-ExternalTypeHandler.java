/*   0*/package com.fasterxml.jackson.databind.deser.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.HashMap;
/*   0*/
/*   0*/public class ExternalTypeHandler {
/*   0*/  private final ExtTypedProperty[] _properties;
/*   0*/  
/*   0*/  private final HashMap<String, Integer> _nameToPropertyIndex;
/*   0*/  
/*   0*/  private final String[] _typeIds;
/*   0*/  
/*   0*/  private final TokenBuffer[] _tokens;
/*   0*/  
/*   0*/  protected ExternalTypeHandler(ExtTypedProperty[] properties, HashMap<String, Integer> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens) {
/*  31*/    this._properties = properties;
/*  32*/    this._nameToPropertyIndex = nameToPropertyIndex;
/*  33*/    this._typeIds = typeIds;
/*  34*/    this._tokens = tokens;
/*   0*/  }
/*   0*/  
/*   0*/  protected ExternalTypeHandler(ExternalTypeHandler h) {
/*  39*/    this._properties = h._properties;
/*  40*/    this._nameToPropertyIndex = h._nameToPropertyIndex;
/*  41*/    int len = this._properties.length;
/*  42*/    this._typeIds = new String[len];
/*  43*/    this._tokens = new TokenBuffer[len];
/*   0*/  }
/*   0*/  
/*   0*/  public ExternalTypeHandler start() {
/*  47*/    return new ExternalTypeHandler(this);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean handleTypePropertyValue(JsonParser jp, DeserializationContext ctxt, String propName, Object bean) throws IOException {
/*  60*/    Integer I = this._nameToPropertyIndex.get(propName);
/*  61*/    if (I == null) {
/*  62*/        return false; 
/*   0*/       }
/*  64*/    int index = I;
/*  65*/    ExtTypedProperty prop = this._properties[index];
/*  66*/    if (!prop.hasTypePropertyName(propName)) {
/*  67*/        return false; 
/*   0*/       }
/*  69*/    String typeId = jp.getText();
/*  71*/    boolean canDeserialize = (bean != null && this._tokens[index] != null);
/*  73*/    if (canDeserialize) {
/*  74*/      _deserializeAndSet(jp, ctxt, bean, index, typeId);
/*  76*/      this._tokens[index] = null;
/*   0*/    } else {
/*  78*/      this._typeIds[index] = typeId;
/*   0*/    } 
/*  80*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean handlePropertyValue(JsonParser jp, DeserializationContext ctxt, String propName, Object bean) throws IOException {
/*   0*/    // Byte code:
/*   0*/    //   0: aload_0
/*   0*/    //   1: getfield _nameToPropertyIndex : Ljava/util/HashMap;
/*   0*/    //   4: aload_3
/*   0*/    //   5: invokevirtual get : (Ljava/lang/Object;)Ljava/lang/Object;
/*   0*/    //   8: checkcast java/lang/Integer
/*   0*/    //   11: astore #5
/*   0*/    //   13: aload #5
/*   0*/    //   15: ifnonnull -> 20
/*   0*/    //   18: iconst_0
/*   0*/    //   19: ireturn
/*   0*/    //   20: aload #5
/*   0*/    //   22: invokevirtual intValue : ()I
/*   0*/    //   25: istore #6
/*   0*/    //   27: aload_0
/*   0*/    //   28: getfield _properties : [Lcom/fasterxml/jackson/databind/deser/impl/ExternalTypeHandler$ExtTypedProperty;
/*   0*/    //   31: iload #6
/*   0*/    //   33: aaload
/*   0*/    //   34: astore #7
/*   0*/    //   36: aload #7
/*   0*/    //   38: aload_3
/*   0*/    //   39: invokevirtual hasTypePropertyName : (Ljava/lang/String;)Z
/*   0*/    //   42: ifeq -> 86
/*   0*/    //   45: aload_0
/*   0*/    //   46: getfield _typeIds : [Ljava/lang/String;
/*   0*/    //   49: iload #6
/*   0*/    //   51: aload_1
/*   0*/    //   52: invokevirtual getText : ()Ljava/lang/String;
/*   0*/    //   55: aastore
/*   0*/    //   56: aload_1
/*   0*/    //   57: invokevirtual skipChildren : ()Lcom/fasterxml/jackson/core/JsonParser;
/*   0*/    //   60: pop
/*   0*/    //   61: aload #4
/*   0*/    //   63: ifnull -> 80
/*   0*/    //   66: aload_0
/*   0*/    //   67: getfield _tokens : [Lcom/fasterxml/jackson/databind/util/TokenBuffer;
/*   0*/    //   70: iload #6
/*   0*/    //   72: aaload
/*   0*/    //   73: ifnull -> 80
/*   0*/    //   76: iconst_1
/*   0*/    //   77: goto -> 81
/*   0*/    //   80: iconst_0
/*   0*/    //   81: istore #8
/*   0*/    //   83: goto -> 134
/*   0*/    //   86: new com/fasterxml/jackson/databind/util/TokenBuffer
/*   0*/    //   89: dup
/*   0*/    //   90: aload_1
/*   0*/    //   91: invokespecial <init> : (Lcom/fasterxml/jackson/core/JsonParser;)V
/*   0*/    //   94: astore #9
/*   0*/    //   96: aload #9
/*   0*/    //   98: aload_1
/*   0*/    //   99: invokevirtual copyCurrentStructure : (Lcom/fasterxml/jackson/core/JsonParser;)V
/*   0*/    //   102: aload_0
/*   0*/    //   103: getfield _tokens : [Lcom/fasterxml/jackson/databind/util/TokenBuffer;
/*   0*/    //   106: iload #6
/*   0*/    //   108: aload #9
/*   0*/    //   110: aastore
/*   0*/    //   111: aload #4
/*   0*/    //   113: pop
/*   0*/    //   114: goto -> 131
/*   0*/    //   117: aload_0
/*   0*/    //   118: getfield _typeIds : [Ljava/lang/String;
/*   0*/    //   121: iload #6
/*   0*/    //   123: aaload
/*   0*/    //   124: ifnull -> 131
/*   0*/    //   127: iconst_1
/*   0*/    //   128: goto -> 132
/*   0*/    //   131: iconst_0
/*   0*/    //   132: istore #8
/*   0*/    //   134: iload #8
/*   0*/    //   136: ifeq -> 176
/*   0*/    //   139: aload_0
/*   0*/    //   140: getfield _typeIds : [Ljava/lang/String;
/*   0*/    //   143: iload #6
/*   0*/    //   145: aaload
/*   0*/    //   146: astore #9
/*   0*/    //   148: aload_0
/*   0*/    //   149: getfield _typeIds : [Ljava/lang/String;
/*   0*/    //   152: iload #6
/*   0*/    //   154: aconst_null
/*   0*/    //   155: aastore
/*   0*/    //   156: aload_0
/*   0*/    //   157: aload_1
/*   0*/    //   158: aload_2
/*   0*/    //   159: aload #4
/*   0*/    //   161: iload #6
/*   0*/    //   163: aload #9
/*   0*/    //   165: invokevirtual _deserializeAndSet : (Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;Ljava/lang/Object;ILjava/lang/String;)V
/*   0*/    //   168: aload_0
/*   0*/    //   169: getfield _tokens : [Lcom/fasterxml/jackson/databind/util/TokenBuffer;
/*   0*/    //   172: iload #6
/*   0*/    //   174: aconst_null
/*   0*/    //   175: aastore
/*   0*/    //   176: iconst_1
/*   0*/    //   177: ireturn
/*   0*/    // Line number table:
/*   0*/    //   Java source line number -> byte code offset
/*   0*/    //   #94	-> 0
/*   0*/    //   #95	-> 13
/*   0*/    //   #96	-> 18
/*   0*/    //   #98	-> 20
/*   0*/    //   #99	-> 27
/*   0*/    //   #101	-> 36
/*   0*/    //   #102	-> 45
/*   0*/    //   #103	-> 56
/*   0*/    //   #104	-> 61
/*   0*/    //   #107	-> 86
/*   0*/    //   #108	-> 96
/*   0*/    //   #109	-> 102
/*   0*/    //   #110	-> 111
/*   0*/    //   #115	-> 134
/*   0*/    //   #116	-> 139
/*   0*/    //   #118	-> 148
/*   0*/    //   #119	-> 156
/*   0*/    //   #120	-> 168
/*   0*/    //   #122	-> 176
/*   0*/    // Local variable table:
/*   0*/    //   start	length	slot	name	descriptor
/*   0*/    //   83	3	8	canDeserialize	Z
/*   0*/    //   96	38	9	tokens	Lcom/fasterxml/jackson/databind/util/TokenBuffer;
/*   0*/    //   148	28	9	typeId	Ljava/lang/String;
/*   0*/    //   0	178	0	this	Lcom/fasterxml/jackson/databind/deser/impl/ExternalTypeHandler;
/*   0*/    //   0	178	1	jp	Lcom/fasterxml/jackson/core/JsonParser;
/*   0*/    //   0	178	2	ctxt	Lcom/fasterxml/jackson/databind/DeserializationContext;
/*   0*/    //   0	178	3	propName	Ljava/lang/String;
/*   0*/    //   0	178	4	bean	Ljava/lang/Object;
/*   0*/    //   13	165	5	I	Ljava/lang/Integer;
/*   0*/    //   27	151	6	index	I
/*   0*/    //   36	142	7	prop	Lcom/fasterxml/jackson/databind/deser/impl/ExternalTypeHandler$ExtTypedProperty;
/*   0*/    //   134	44	8	canDeserialize	Z
/*   0*/  }
/*   0*/  
/*   0*/  public Object complete(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException {
/* 129*/    for (int i = 0, len = this._properties.length; i < len; i++) {
/* 130*/      String typeId = this._typeIds[i];
/* 131*/      if (typeId == null) {
/* 132*/        TokenBuffer tokens = this._tokens[i];
/* 135*/        if (tokens == null) {
/*   0*/            continue; 
/*   0*/           }
/* 141*/        JsonToken t = tokens.firstToken();
/* 142*/        if (t != null && t.isScalarValue()) {
/* 143*/          JsonParser buffered = tokens.asParser(jp);
/* 144*/          buffered.nextToken();
/* 145*/          SettableBeanProperty extProp = this._properties[i].getProperty();
/* 146*/          Object result = TypeDeserializer.deserializeIfNatural(buffered, ctxt, extProp.getType());
/* 147*/          if (result != null) {
/* 148*/            extProp.set(bean, result);
/*   0*/            continue;
/*   0*/          } 
/* 152*/          if (!this._properties[i].hasDefaultType()) {
/* 153*/              throw ctxt.mappingException("Missing external type id property '%s'", new Object[] { this._properties[i].getTypePropertyName() }); 
/*   0*/             }
/* 156*/          typeId = this._properties[i].getDefaultTypeId();
/*   0*/        } 
/* 158*/      } else if (this._tokens[i] == null) {
/* 159*/        SettableBeanProperty prop = this._properties[i].getProperty();
/* 160*/        throw ctxt.mappingException("Missing property '%s' for external type id '%s'", new Object[] { prop.getName(), this._properties[i].getTypePropertyName() });
/*   0*/      } 
/* 163*/      _deserializeAndSet(jp, ctxt, bean, i, typeId);
/*   0*/    } 
/* 165*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  public Object complete(JsonParser jp, DeserializationContext ctxt, PropertyValueBuffer buffer, PropertyBasedCreator creator) throws IOException {
/* 177*/    int len = this._properties.length;
/* 178*/    Object[] values = new Object[len];
/* 179*/    for (int i = 0; i < len; i++) {
/* 180*/      String typeId = this._typeIds[i];
/* 181*/      if (typeId == null) {
/* 183*/        if (this._tokens[i] == null) {
/*   0*/            continue; 
/*   0*/           }
/* 188*/        if (!this._properties[i].hasDefaultType()) {
/* 189*/            throw ctxt.mappingException("Missing external type id property '%s'", new Object[] { this._properties[i].getTypePropertyName() }); 
/*   0*/           }
/* 192*/        typeId = this._properties[i].getDefaultTypeId();
/* 193*/      } else if (this._tokens[i] == null) {
/* 194*/        SettableBeanProperty prop = this._properties[i].getProperty();
/* 195*/        throw ctxt.mappingException("Missing property '%s' for external type id '%s'", new Object[] { prop.getName(), this._properties[i].getTypePropertyName() });
/*   0*/      } 
/* 198*/      values[i] = _deserialize(jp, ctxt, i, typeId);
/*   0*/    } 
/* 201*/    for (int j = 0; j < len; j++) {
/* 202*/      SettableBeanProperty prop = this._properties[j].getProperty();
/* 203*/      if (creator.findCreatorProperty(prop.getName()) != null) {
/* 204*/          buffer.assignParameter(prop, values[j]); 
/*   0*/         }
/*   0*/    } 
/* 207*/    Object bean = creator.build(ctxt, buffer);
/* 209*/    for (int k = 0; k < len; k++) {
/* 210*/      SettableBeanProperty prop = this._properties[k].getProperty();
/* 211*/      if (creator.findCreatorProperty(prop.getName()) == null) {
/* 212*/          prop.set(bean, values[k]); 
/*   0*/         }
/*   0*/    } 
/* 215*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserialize(JsonParser jp, DeserializationContext ctxt, int index, String typeId) throws IOException {
/* 222*/    TokenBuffer merged = new TokenBuffer(jp);
/* 223*/    merged.writeStartArray();
/* 224*/    merged.writeString(typeId);
/* 225*/    JsonParser p2 = this._tokens[index].asParser(jp);
/* 226*/    p2.nextToken();
/* 227*/    merged.copyCurrentStructure(p2);
/* 228*/    merged.writeEndArray();
/* 231*/    p2 = merged.asParser(jp);
/* 232*/    p2.nextToken();
/* 233*/    return this._properties[index].getProperty().deserialize(p2, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _deserializeAndSet(JsonParser jp, DeserializationContext ctxt, Object bean, int index, String typeId) throws IOException {
/* 243*/    TokenBuffer merged = new TokenBuffer(jp);
/* 244*/    merged.writeStartArray();
/* 245*/    merged.writeString(typeId);
/* 246*/    JsonParser p2 = this._tokens[index].asParser(jp);
/* 247*/    p2.nextToken();
/* 248*/    merged.copyCurrentStructure(p2);
/* 249*/    merged.writeEndArray();
/* 251*/    p2 = merged.asParser(jp);
/* 252*/    p2.nextToken();
/* 253*/    this._properties[index].getProperty().deserializeAndSet(p2, ctxt, bean);
/*   0*/  }
/*   0*/  
/*   0*/  public static class Builder {
/* 264*/    private final ArrayList<ExternalTypeHandler.ExtTypedProperty> _properties = new ArrayList<ExternalTypeHandler.ExtTypedProperty>();
/*   0*/    
/* 265*/    private final HashMap<String, Integer> _nameToPropertyIndex = new HashMap<String, Integer>();
/*   0*/    
/*   0*/    public void addExternal(SettableBeanProperty property, TypeDeserializer typeDeser) {
/* 269*/      Integer index = this._properties.size();
/* 270*/      this._properties.add(new ExternalTypeHandler.ExtTypedProperty(property, typeDeser));
/* 271*/      this._nameToPropertyIndex.put(property.getName(), index);
/* 272*/      this._nameToPropertyIndex.put(typeDeser.getPropertyName(), index);
/*   0*/    }
/*   0*/    
/*   0*/    public ExternalTypeHandler build() {
/* 276*/      return new ExternalTypeHandler(this._properties.<ExternalTypeHandler.ExtTypedProperty>toArray(new ExternalTypeHandler.ExtTypedProperty[this._properties.size()]), this._nameToPropertyIndex, null, null);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static final class ExtTypedProperty {
/*   0*/    private final SettableBeanProperty _property;
/*   0*/    
/*   0*/    private final TypeDeserializer _typeDeserializer;
/*   0*/    
/*   0*/    private final String _typePropertyName;
/*   0*/    
/*   0*/    public ExtTypedProperty(SettableBeanProperty property, TypeDeserializer typeDeser) {
/* 289*/      this._property = property;
/* 290*/      this._typeDeserializer = typeDeser;
/* 291*/      this._typePropertyName = typeDeser.getPropertyName();
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasTypePropertyName(String n) {
/* 295*/      return n.equals(this._typePropertyName);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean hasDefaultType() {
/* 299*/      return (this._typeDeserializer.getDefaultImpl() != null);
/*   0*/    }
/*   0*/    
/*   0*/    public String getDefaultTypeId() {
/* 303*/      Class<?> defaultType = this._typeDeserializer.getDefaultImpl();
/* 304*/      if (defaultType == null) {
/* 305*/          return null; 
/*   0*/         }
/* 307*/      return this._typeDeserializer.getTypeIdResolver().idFromValueAndType(null, defaultType);
/*   0*/    }
/*   0*/    
/*   0*/    public String getTypePropertyName() {
/* 310*/      return this._typePropertyName;
/*   0*/    }
/*   0*/    
/*   0*/    public SettableBeanProperty getProperty() {
/* 313*/      return this._property;
/*   0*/    }
/*   0*/  }
/*   0*/}
