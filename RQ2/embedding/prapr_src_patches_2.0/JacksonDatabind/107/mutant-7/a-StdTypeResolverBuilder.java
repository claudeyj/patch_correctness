/*   0*/package com.fasterxml.jackson.databind.jsontype.impl;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.MapperFeature;
/*   0*/import com.fasterxml.jackson.databind.SerializationConfig;
/*   0*/import com.fasterxml.jackson.databind.annotation.NoClass;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*   0*/import java.util.Collection;
/*   0*/
/*   0*/public class StdTypeResolverBuilder implements TypeResolverBuilder<StdTypeResolverBuilder> {
/*   0*/  protected JsonTypeInfo.Id _idType;
/*   0*/  
/*   0*/  protected JsonTypeInfo.As _includeAs;
/*   0*/  
/*   0*/  protected String _typeProperty;
/*   0*/  
/*   0*/  protected boolean _typeIdVisible = false;
/*   0*/  
/*   0*/  protected Class<?> _defaultImpl;
/*   0*/  
/*   0*/  protected TypeIdResolver _customIdResolver;
/*   0*/  
/*   0*/  protected StdTypeResolverBuilder(JsonTypeInfo.Id idType, JsonTypeInfo.As idAs, String propName) {
/*  54*/    this._idType = idType;
/*  55*/    this._includeAs = idAs;
/*  56*/    this._typeProperty = propName;
/*   0*/  }
/*   0*/  
/*   0*/  public static StdTypeResolverBuilder noTypeInfoBuilder() {
/*  60*/    return new StdTypeResolverBuilder().init(JsonTypeInfo.Id.NONE, null);
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder init(JsonTypeInfo.Id idType, TypeIdResolver idRes) {
/*  67*/    if (idType == null) {
/*  68*/        throw new IllegalArgumentException("idType cannot be null"); 
/*   0*/       }
/*  70*/    this._idType = idType;
/*  71*/    this._customIdResolver = idRes;
/*  73*/    this._typeProperty = idType.getDefaultPropertyName();
/*  74*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/*  81*/    if (this._idType == JsonTypeInfo.Id.NONE) {
/*  81*/        return null; 
/*   0*/       }
/*  84*/    if (baseType.isPrimitive()) {
/*  85*/        return null; 
/*   0*/       }
/*  87*/    TypeIdResolver idRes = idResolver(config, baseType, subtypes, true, false);
/*  88*/    switch (this._includeAs) {
/*   0*/      case WRAPPER_ARRAY:
/*  90*/        return new AsArrayTypeSerializer(idRes, null);
/*   0*/      case PROPERTY:
/*  92*/        return new AsPropertyTypeSerializer(idRes, null, this._typeProperty);
/*   0*/      case WRAPPER_OBJECT:
/*  94*/        return new AsWrapperTypeSerializer(idRes, null);
/*   0*/      case EXTERNAL_PROPERTY:
/*  96*/        return new AsExternalTypeSerializer(idRes, null, this._typeProperty);
/*   0*/      case EXISTING_PROPERTY:
/*  99*/        return new AsExistingPropertyTypeSerializer(idRes, null, this._typeProperty);
/*   0*/    } 
/* 101*/    throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/* 114*/    if (this._idType == JsonTypeInfo.Id.NONE) {
/* 114*/        return null; 
/*   0*/       }
/* 117*/    if (baseType.isPrimitive()) {
/* 118*/        return null; 
/*   0*/       }
/* 121*/    TypeIdResolver idRes = idResolver(config, baseType, subtypes, false, true);
/* 123*/    JavaType defaultImpl = defineDefaultImpl(config, baseType);
/* 126*/    switch (this._includeAs) {
/*   0*/      case WRAPPER_ARRAY:
/* 128*/        return new AsArrayTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*   0*/      case PROPERTY:
/*   0*/      case EXISTING_PROPERTY:
/* 132*/        return new AsPropertyTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl, this._includeAs);
/*   0*/      case WRAPPER_OBJECT:
/* 135*/        return new AsWrapperTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*   0*/      case EXTERNAL_PROPERTY:
/* 138*/        return new AsExternalTypeDeserializer(baseType, idRes, this._typeProperty, this._typeIdVisible, defaultImpl);
/*   0*/    } 
/* 141*/    throw new IllegalStateException("Do not know how to construct standard type serializer for inclusion type: " + this._includeAs);
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType defineDefaultImpl(DeserializationConfig config, JavaType baseType) {
/*   0*/    JavaType defaultImpl;
/* 146*/    if (this._defaultImpl == null) {
/* 148*/      if (config.isEnabled(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL) && !baseType.isAbstract()) {
/* 149*/        defaultImpl = baseType;
/*   0*/      } else {
/* 151*/        defaultImpl = null;
/*   0*/      } 
/* 160*/    } else if (this._defaultImpl == Void.class || this._defaultImpl == NoClass.class) {
/* 162*/      defaultImpl = config.getTypeFactory().constructType(this._defaultImpl);
/* 164*/    } else if (baseType.hasRawClass(this._defaultImpl)) {
/* 165*/      defaultImpl = baseType;
/* 166*/    } else if (baseType.isTypeOrSuperTypeOf(this._defaultImpl)) {
/* 168*/      defaultImpl = config.getTypeFactory().constructSpecializedType(baseType, this._defaultImpl);
/*   0*/    } else {
/* 181*/      defaultImpl = null;
/*   0*/    } 
/* 185*/    return defaultImpl;
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder inclusion(JsonTypeInfo.As includeAs) {
/* 196*/    if (includeAs == null) {
/* 197*/        throw new IllegalArgumentException("includeAs cannot be null"); 
/*   0*/       }
/* 199*/    this._includeAs = includeAs;
/* 200*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
/* 210*/    if (typeIdPropName == null || typeIdPropName.length() == 0) {
/* 211*/        typeIdPropName = this._idType.getDefaultPropertyName(); 
/*   0*/       }
/* 213*/    this._typeProperty = typeIdPropName;
/* 214*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder defaultImpl(Class<?> defaultImpl) {
/* 219*/    this._defaultImpl = defaultImpl;
/* 220*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder typeIdVisibility(boolean isVisible) {
/* 225*/    this._typeIdVisible = isVisible;
/* 226*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getDefaultImpl() {
/* 235*/    return this._defaultImpl;
/*   0*/  }
/*   0*/  
/*   0*/  public String getTypeProperty() {
/* 237*/    return this._typeProperty;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTypeIdVisible() {
/* 238*/    return this._typeIdVisible;
/*   0*/  }
/*   0*/  
/*   0*/  protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType, Collection<NamedType> subtypes, boolean forSer, boolean forDeser) {
/* 255*/    if (this._customIdResolver != null) {
/* 255*/        return this._customIdResolver; 
/*   0*/       }
/* 256*/    if (this._idType == null) {
/* 256*/        throw new IllegalStateException("Cannot build, 'init()' not yet called"); 
/*   0*/       }
/* 257*/    switch (this._idType) {
/*   0*/      case CLASS:
/* 259*/        return new ClassNameIdResolver(baseType, config.getTypeFactory());
/*   0*/      case MINIMAL_CLASS:
/* 261*/        return new MinimalClassNameIdResolver(baseType, config.getTypeFactory());
/*   0*/      case NAME:
/* 263*/        return TypeNameIdResolver.construct(config, baseType, subtypes, forSer, forDeser);
/*   0*/      case NONE:
/* 265*/        return null;
/*   0*/    } 
/* 268*/    throw new IllegalStateException("Do not know how to construct standard type id resolver for idType: " + this._idType);
/*   0*/  }
/*   0*/  
/*   0*/  public StdTypeResolverBuilder() {}
/*   0*/}
