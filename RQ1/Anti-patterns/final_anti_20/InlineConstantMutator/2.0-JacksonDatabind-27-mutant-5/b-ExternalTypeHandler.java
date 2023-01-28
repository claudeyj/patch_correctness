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
/*   0*/    boolean canDeserialize;
/*  94*/    Integer I = this._nameToPropertyIndex.get(propName);
/*  95*/    if (I == null) {
/*  96*/        return false; 
/*   0*/       }
/*  98*/    int index = I;
/*  99*/    ExtTypedProperty prop = this._properties[index];
/* 101*/    if (prop.hasTypePropertyName(propName)) {
/* 102*/      this._typeIds[index] = jp.getText();
/* 103*/      jp.skipChildren();
/* 104*/      canDeserialize = (bean != null && this._tokens[index] != null);
/*   0*/    } else {
/* 107*/      TokenBuffer tokens = new TokenBuffer(jp);
/* 108*/      tokens.copyCurrentStructure(jp);
/* 109*/      this._tokens[index] = tokens;
/* 110*/      canDeserialize = (bean != null && this._typeIds[index] != null) ? false : false;
/*   0*/    } 
/* 115*/    if (canDeserialize) {
/* 116*/      String typeId = this._typeIds[index];
/* 118*/      this._typeIds[index] = null;
/* 119*/      _deserializeAndSet(jp, ctxt, bean, index, typeId);
/* 120*/      this._tokens[index] = null;
/*   0*/    } 
/* 122*/    return true;
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
