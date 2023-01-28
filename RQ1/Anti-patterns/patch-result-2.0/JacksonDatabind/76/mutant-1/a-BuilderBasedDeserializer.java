/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayBuilderDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class BuilderBasedDeserializer extends BeanDeserializerBase {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected final AnnotatedMethod _buildMethod;
/*   0*/  
/*   0*/  public BuilderBasedDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, Set<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*  45*/    super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
/*  47*/    this._buildMethod = builder.getBuildMethod();
/*  49*/    if (this._objectIdReader != null) {
/*  50*/        throw new IllegalArgumentException("Can not use Object Id with Builder-based deserialization (type " + beanDesc.getType() + ")"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected BuilderBasedDeserializer(BuilderBasedDeserializer src) {
/*  61*/    this(src, src._ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BuilderBasedDeserializer(BuilderBasedDeserializer src, boolean ignoreAllUnknown) {
/*  66*/    super(src, ignoreAllUnknown);
/*  67*/    this._buildMethod = src._buildMethod;
/*   0*/  }
/*   0*/  
/*   0*/  protected BuilderBasedDeserializer(BuilderBasedDeserializer src, NameTransformer unwrapper) {
/*  71*/    super(src, unwrapper);
/*  72*/    this._buildMethod = src._buildMethod;
/*   0*/  }
/*   0*/  
/*   0*/  public BuilderBasedDeserializer(BuilderBasedDeserializer src, ObjectIdReader oir) {
/*  76*/    super(src, oir);
/*  77*/    this._buildMethod = src._buildMethod;
/*   0*/  }
/*   0*/  
/*   0*/  public BuilderBasedDeserializer(BuilderBasedDeserializer src, Set<String> ignorableProps) {
/*  81*/    super(src, ignorableProps);
/*  82*/    this._buildMethod = src._buildMethod;
/*   0*/  }
/*   0*/  
/*   0*/  public BuilderBasedDeserializer(BuilderBasedDeserializer src, BeanPropertyMap props) {
/*  86*/    super(src, props);
/*  87*/    this._buildMethod = src._buildMethod;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/*  97*/    return new BuilderBasedDeserializer(this, unwrapper);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase withObjectIdReader(ObjectIdReader oir) {
/* 102*/    return new BuilderBasedDeserializer(this, oir);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase withIgnorableProperties(Set<String> ignorableProps) {
/* 107*/    return new BuilderBasedDeserializer(this, ignorableProps);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/* 112*/    return new BuilderBasedDeserializer(this, props);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase asArrayDeserializer() {
/* 117*/    SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/* 118*/    return new BeanAsArrayBuilderDeserializer(this, props, this._buildMethod);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object finishBuild(DeserializationContext ctxt, Object builder) throws IOException {
/* 131*/    if (null == this._buildMethod) {
/* 132*/        return builder; 
/*   0*/       }
/*   0*/    try {
/* 135*/      return this._buildMethod.getMember().invoke(builder, new Object[0]);
/* 136*/    } catch (Exception e) {
/* 137*/      return wrapInstantiationProblem(e, ctxt);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public final Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 148*/    JsonToken t = p.getCurrentToken();
/* 151*/    if (t == JsonToken.START_OBJECT) {
/* 152*/      t = p.nextToken();
/* 153*/      if (this._vanillaProcessing) {
/* 154*/          return finishBuild(ctxt, vanillaDeserialize(p, ctxt, t)); 
/*   0*/         }
/* 156*/      Object builder = deserializeFromObject(p, ctxt);
/* 157*/      return finishBuild(ctxt, builder);
/*   0*/    } 
/* 160*/    if (t != null) {
/* 161*/        switch (t) {
/*   0*/          case VALUE_STRING:
/* 163*/            return finishBuild(ctxt, deserializeFromString(p, ctxt));
/*   0*/          case VALUE_NUMBER_INT:
/* 165*/            return finishBuild(ctxt, deserializeFromNumber(p, ctxt));
/*   0*/          case VALUE_NUMBER_FLOAT:
/* 167*/            return finishBuild(ctxt, deserializeFromDouble(p, ctxt));
/*   0*/          case VALUE_EMBEDDED_OBJECT:
/* 169*/            return p.getEmbeddedObject();
/*   0*/          case VALUE_TRUE:
/*   0*/          case VALUE_FALSE:
/* 172*/            return finishBuild(ctxt, deserializeFromBoolean(p, ctxt));
/*   0*/          case START_ARRAY:
/* 175*/            return finishBuild(ctxt, deserializeFromArray(p, ctxt));
/*   0*/          case FIELD_NAME:
/*   0*/          case END_OBJECT:
/* 178*/            return finishBuild(ctxt, deserializeFromObject(p, ctxt));
/*   0*/        }  
/*   0*/       }
/* 182*/    return ctxt.handleUnexpectedToken(handledType(), p);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException {
/* 198*/    return finishBuild(ctxt, _deserialize(p, ctxt, builder));
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserialize(JsonParser p, DeserializationContext ctxt, Object builder) throws IOException, JsonProcessingException {
/* 211*/    if (this._injectables != null) {
/* 212*/        injectValues(ctxt, builder); 
/*   0*/       }
/* 214*/    if (this._unwrappedPropertyHandler != null) {
/* 215*/        return deserializeWithUnwrapped(p, ctxt, builder); 
/*   0*/       }
/* 217*/    if (this._externalTypeIdHandler != null) {
/* 218*/        return deserializeWithExternalTypeId(p, ctxt, builder); 
/*   0*/       }
/* 220*/    if (this._needViewProcesing) {
/* 221*/      Class<?> view = ctxt.getActiveView();
/* 222*/      if (view != null) {
/* 223*/          return deserializeWithView(p, ctxt, builder, view); 
/*   0*/         }
/*   0*/    } 
/* 226*/    JsonToken t = p.getCurrentToken();
/* 228*/    if (t == JsonToken.START_OBJECT) {
/* 229*/        t = p.nextToken(); 
/*   0*/       }
/* 231*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 232*/      String propName = p.getCurrentName();
/* 234*/      p.nextToken();
/* 235*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 237*/      if (prop != null) {
/*   0*/        try {
/* 239*/          builder = prop.deserializeSetAndReturn(p, ctxt, builder);
/* 240*/        } catch (Exception e) {
/* 241*/          wrapAndThrow(e, builder, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 245*/        handleUnknownVanilla(p, ctxt, handledType(), propName);
/*   0*/      } 
/*   0*/    } 
/* 247*/    return builder;
/*   0*/  }
/*   0*/  
/*   0*/  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException, JsonProcessingException {
/* 258*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 259*/    for (; p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
/* 260*/      String propName = p.getCurrentName();
/* 262*/      p.nextToken();
/* 263*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 264*/      if (prop != null) {
/*   0*/        try {
/* 266*/          bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 267*/        } catch (Exception e) {
/* 268*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 271*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/*   0*/    } 
/* 274*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/* 285*/    if (this._nonStandardCreation) {
/* 286*/      if (this._unwrappedPropertyHandler != null) {
/* 287*/          return deserializeWithUnwrapped(p, ctxt); 
/*   0*/         }
/* 289*/      if (this._externalTypeIdHandler != null) {
/* 290*/          return deserializeWithExternalTypeId(p, ctxt); 
/*   0*/         }
/* 292*/      return deserializeFromObjectUsingNonDefault(p, ctxt);
/*   0*/    } 
/* 294*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 295*/    if (this._injectables != null) {
/* 296*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 298*/    if (this._needViewProcesing) {
/* 299*/      Class<?> view = ctxt.getActiveView();
/* 300*/      if (view != null) {
/* 301*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/* 304*/    for (; p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
/* 305*/      String propName = p.getCurrentName();
/* 307*/      p.nextToken();
/* 308*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 309*/      if (prop != null) {
/*   0*/        try {
/* 311*/          bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 312*/        } catch (Exception e) {
/* 313*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 317*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/*   0*/    } 
/* 319*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/*   0*/    Object bean;
/* 336*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 337*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 340*/    TokenBuffer unknown = null;
/* 342*/    JsonToken t = p.getCurrentToken();
/* 343*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 344*/      String propName = p.getCurrentName();
/* 345*/      p.nextToken();
/* 347*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 348*/      if (creatorProp != null) {
/* 350*/        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/*   0*/          Object object;
/* 351*/          p.nextToken();
/*   0*/          try {
/* 354*/            object = creator.build(ctxt, buffer);
/* 355*/          } catch (Exception e) {
/* 356*/            wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/          } 
/* 360*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 361*/              return handlePolymorphic(p, ctxt, object, unknown); 
/*   0*/             }
/* 363*/          if (unknown != null) {
/* 364*/              object = handleUnknownProperties(ctxt, object, unknown); 
/*   0*/             }
/* 367*/          return _deserialize(p, ctxt, object);
/*   0*/        } 
/* 372*/      } else if (!buffer.readIdProperty(propName)) {
/* 376*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 377*/        if (prop != null) {
/* 378*/          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/* 383*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 384*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 388*/        } else if (this._anySetter != null) {
/* 389*/          buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*   0*/        } else {
/* 393*/          if (unknown == null) {
/* 394*/              unknown = new TokenBuffer(p, ctxt); 
/*   0*/             }
/* 396*/          unknown.writeFieldName(propName);
/* 397*/          unknown.copyCurrentStructure(p);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 403*/      bean = creator.build(ctxt, buffer);
/* 404*/    } catch (Exception e) {
/* 405*/      bean = wrapInstantiationProblem(e, ctxt);
/*   0*/    } 
/* 407*/    if (unknown != null) {
/* 409*/      if (bean.getClass() != this._beanType.getRawClass()) {
/* 410*/          return handlePolymorphic(null, ctxt, bean, unknown); 
/*   0*/         }
/* 413*/      return handleUnknownProperties(ctxt, bean, unknown);
/*   0*/    } 
/* 415*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException, JsonProcessingException {
/* 428*/    JsonToken t = p.getCurrentToken();
/* 429*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 430*/      String propName = p.getCurrentName();
/* 432*/      p.nextToken();
/* 433*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 434*/      if (prop != null) {
/* 435*/        if (!prop.visibleInView(activeView)) {
/* 436*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 440*/            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 441*/          } catch (Exception e) {
/* 442*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } else {
/* 446*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/*   0*/    } 
/* 448*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/* 465*/    if (this._delegateDeserializer != null) {
/* 466*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 468*/    if (this._propertyBasedCreator != null) {
/* 469*/        return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt); 
/*   0*/       }
/* 471*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 472*/    tokens.writeStartObject();
/* 473*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 475*/    if (this._injectables != null) {
/* 476*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 479*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 481*/    for (; p.getCurrentToken() != JsonToken.END_OBJECT; p.nextToken()) {
/* 482*/      String propName = p.getCurrentName();
/* 483*/      p.nextToken();
/* 484*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 485*/      if (prop != null) {
/* 486*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 487*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 491*/            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 492*/          } catch (Exception e) {
/* 493*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 498*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 499*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 503*/        tokens.writeFieldName(propName);
/* 504*/        tokens.copyCurrentStructure(p);
/* 506*/        if (this._anySetter != null) {
/*   0*/            try {
/* 508*/              this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 509*/            } catch (Exception e) {
/* 510*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            }  
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 515*/    tokens.writeEndObject();
/* 516*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 517*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
/* 525*/    JsonToken t = p.getCurrentToken();
/* 526*/    if (t == JsonToken.START_OBJECT) {
/* 527*/        t = p.nextToken(); 
/*   0*/       }
/* 529*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 530*/    tokens.writeStartObject();
/* 531*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 532*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 533*/      String propName = p.getCurrentName();
/* 534*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 535*/      p.nextToken();
/* 536*/      if (prop != null) {
/* 537*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 538*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 542*/            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 543*/          } catch (Exception e) {
/* 544*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 548*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 549*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 553*/        tokens.writeFieldName(propName);
/* 554*/        tokens.copyCurrentStructure(p);
/* 556*/        if (this._anySetter != null) {
/* 557*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 560*/    tokens.writeEndObject();
/* 561*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 562*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/*   0*/    Object bean;
/* 570*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 571*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 573*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 574*/    tokens.writeStartObject();
/* 576*/    JsonToken t = p.getCurrentToken();
/* 577*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 578*/      String propName = p.getCurrentName();
/* 579*/      p.nextToken();
/* 581*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 582*/      if (creatorProp != null) {
/* 583*/        if (buffer.assignParameter(creatorProp, creatorProp.deserialize(p, ctxt))) {
/*   0*/          Object object;
/* 584*/          t = p.nextToken();
/*   0*/          try {
/* 587*/            object = creator.build(ctxt, buffer);
/* 588*/          } catch (Exception e) {
/* 589*/            wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/          } 
/* 592*/          while (t == JsonToken.FIELD_NAME) {
/* 593*/            p.nextToken();
/* 594*/            tokens.copyCurrentStructure(p);
/* 595*/            t = p.nextToken();
/*   0*/          } 
/* 597*/          tokens.writeEndObject();
/* 598*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 599*/            ctxt.reportMappingException("Can not create polymorphic instances with unwrapped values", new Object[0]);
/* 600*/            return null;
/*   0*/          } 
/* 602*/          return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, object, tokens);
/*   0*/        } 
/* 607*/      } else if (!buffer.readIdProperty(propName)) {
/* 611*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 612*/        if (prop != null) {
/* 613*/          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/* 616*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 617*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/*   0*/        } else {
/* 620*/          tokens.writeFieldName(propName);
/* 621*/          tokens.copyCurrentStructure(p);
/* 623*/          if (this._anySetter != null) {
/* 624*/              buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt)); 
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 632*/      bean = creator.build(ctxt, buffer);
/* 633*/    } catch (Exception e) {
/* 634*/      return wrapInstantiationProblem(e, ctxt);
/*   0*/    } 
/* 636*/    return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/* 649*/    if (this._propertyBasedCreator != null) {
/* 650*/        return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt); 
/*   0*/       }
/* 652*/    return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
/* 659*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 660*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 662*/    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 663*/      String propName = p.getCurrentName();
/* 664*/      t = p.nextToken();
/* 665*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 666*/      if (prop != null) {
/* 668*/        if (t.isScalarValue()) {
/* 669*/            ext.handleTypePropertyValue(p, ctxt, propName, bean); 
/*   0*/           }
/* 671*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 672*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 676*/            bean = prop.deserializeSetAndReturn(p, ctxt, bean);
/* 677*/          } catch (Exception e) {
/* 678*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 683*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 684*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 688*/      } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/* 692*/        if (this._anySetter != null) {
/*   0*/          try {
/* 694*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 695*/          } catch (Exception e) {
/* 696*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 700*/          handleUnknownProperty(p, ctxt, bean, propName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 704*/    return ext.complete(p, ctxt, bean);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
/* 712*/    throw new IllegalStateException("Deserialization with Builder, External type id, @JsonCreator not yet implemented");
/*   0*/  }
/*   0*/}
