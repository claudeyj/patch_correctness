/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public class BeanDeserializer extends BeanDeserializerBase implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*  47*/    super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src) {
/*  56*/    super(src, src._ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/*  60*/    super(src, ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
/*  64*/    super(src, unwrapper);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
/*  68*/    super(src, oir);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBase src, HashSet<String> ignorableProps) {
/*  72*/    super(src, ignorableProps);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer unwrapper) {
/*  81*/    if (getClass() != BeanDeserializer.class) {
/*  82*/        return this; 
/*   0*/       }
/*  88*/    return new BeanDeserializer(this, unwrapper);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
/*  93*/    return new BeanDeserializer(this, oir);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer withIgnorableProperties(HashSet<String> ignorableProps) {
/*  98*/    return new BeanDeserializer(this, ignorableProps);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase asArrayDeserializer() {
/* 103*/    SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/* 104*/    return new BeanAsArrayDeserializer(this, props);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 123*/    if (p.isExpectedStartObjectToken()) {
/* 124*/      if (this._vanillaProcessing) {
/* 125*/          return vanillaDeserialize(p, ctxt, p.nextToken()); 
/*   0*/         }
/* 129*/      p.nextToken();
/* 130*/      if (this._objectIdReader != null) {
/* 131*/          return deserializeWithObjectId(p, ctxt); 
/*   0*/         }
/* 133*/      return deserializeFromObject(p, ctxt);
/*   0*/    } 
/* 135*/    return _deserializeOther(p, ctxt, p.getCurrentToken());
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 142*/    switch (t) {
/*   0*/      case VALUE_STRING:
/* 144*/        return deserializeFromString(p, ctxt);
/*   0*/      case VALUE_NUMBER_INT:
/* 146*/        return deserializeFromNumber(p, ctxt);
/*   0*/      case VALUE_NUMBER_FLOAT:
/* 148*/        return deserializeFromDouble(p, ctxt);
/*   0*/      case VALUE_EMBEDDED_OBJECT:
/* 150*/        return deserializeFromEmbedded(p, ctxt);
/*   0*/      case VALUE_TRUE:
/*   0*/      case VALUE_FALSE:
/* 153*/        return deserializeFromBoolean(p, ctxt);
/*   0*/      case VALUE_NULL:
/* 156*/        return deserializeFromNull(p, ctxt);
/*   0*/      case START_ARRAY:
/* 159*/        return deserializeFromArray(p, ctxt);
/*   0*/      case FIELD_NAME:
/*   0*/      case END_OBJECT:
/* 162*/        if (this._vanillaProcessing) {
/* 163*/            return vanillaDeserialize(p, ctxt, t); 
/*   0*/           }
/* 165*/        if (this._objectIdReader != null) {
/* 166*/            return deserializeWithObjectId(p, ctxt); 
/*   0*/           }
/* 168*/        return deserializeFromObject(p, ctxt);
/*   0*/    } 
/* 171*/    throw ctxt.mappingException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 175*/    throw ctxt.endOfInputException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*   0*/    String propName;
/* 187*/    p.setCurrentValue(bean);
/* 188*/    if (this._injectables != null) {
/* 189*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 191*/    if (this._unwrappedPropertyHandler != null) {
/* 192*/        return deserializeWithUnwrapped(p, ctxt, bean); 
/*   0*/       }
/* 194*/    if (this._externalTypeIdHandler != null) {
/* 195*/        return deserializeWithExternalTypeId(p, ctxt, bean); 
/*   0*/       }
/* 200*/    if (p.isExpectedStartObjectToken()) {
/* 201*/      propName = p.nextFieldName();
/* 202*/      if (propName == null) {
/* 203*/          return bean; 
/*   0*/         }
/* 206*/    } else if (p.hasTokenId(5)) {
/* 207*/      propName = p.getCurrentName();
/*   0*/    } else {
/* 209*/      return bean;
/*   0*/    } 
/* 212*/    if (this._needViewProcesing) {
/* 213*/      Class<?> view = ctxt.getActiveView();
/* 214*/      if (view != null) {
/* 215*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/*   0*/    while (true) {
/* 219*/      p.nextToken();
/* 220*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 222*/      if (prop != null) {
/*   0*/        try {
/* 224*/          prop.deserializeAndSet(p, ctxt, bean);
/* 225*/        } catch (Exception e) {
/* 226*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 230*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/* 231*/      if ((propName = p.nextFieldName()) == null) {
/* 232*/          return bean; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 249*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 251*/    p.setCurrentValue(bean);
/* 252*/    if (p.hasTokenId(5)) {
/* 253*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 255*/        p.nextToken();
/* 256*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 258*/        if (prop != null) {
/*   0*/          try {
/* 260*/            prop.deserializeAndSet(p, ctxt, bean);
/* 261*/          } catch (Exception e) {
/* 262*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 266*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 267*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 269*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 285*/    if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && 
/* 286*/      p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
/* 288*/        return deserializeFromObjectId(p, ctxt); 
/*   0*/       }
/* 291*/    if (this._nonStandardCreation) {
/* 292*/      if (this._unwrappedPropertyHandler != null) {
/* 293*/          return deserializeWithUnwrapped(p, ctxt); 
/*   0*/         }
/* 295*/      if (this._externalTypeIdHandler != null) {
/* 296*/          return deserializeWithExternalTypeId(p, ctxt); 
/*   0*/         }
/* 298*/      Object object = deserializeFromObjectUsingNonDefault(p, ctxt);
/* 299*/      if (this._injectables != null) {
/* 300*/          injectValues(ctxt, object); 
/*   0*/         }
/* 314*/      return object;
/*   0*/    } 
/* 316*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 318*/    p.setCurrentValue(bean);
/* 319*/    if (p.canReadObjectId()) {
/* 320*/      Object id = p.getObjectId();
/* 321*/      if (id != null) {
/* 322*/          _handleTypedObjectId(p, ctxt, bean, id); 
/*   0*/         }
/*   0*/    } 
/* 325*/    if (this._injectables != null) {
/* 326*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 328*/    if (this._needViewProcesing) {
/* 329*/      Class<?> view = ctxt.getActiveView();
/* 330*/      if (view != null) {
/* 331*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/* 334*/    if (p.hasTokenId(5)) {
/* 335*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 337*/        p.nextToken();
/* 338*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 339*/        if (prop != null) {
/*   0*/          try {
/* 341*/            prop.deserializeAndSet(p, ctxt, bean);
/* 342*/          } catch (Exception e) {
/* 343*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 347*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 348*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 350*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 366*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 367*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 370*/    TokenBuffer unknown = null;
/* 372*/    JsonToken t = p.getCurrentToken();
/* 373*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 374*/      String propName = p.getCurrentName();
/* 375*/      p.nextToken();
/* 377*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 378*/      if (creatorProp != null) {
/* 380*/        if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/          Object object;
/* 382*/          p.nextToken();
/*   0*/          try {
/* 385*/            object = creator.build(ctxt, buffer);
/* 386*/          } catch (Exception e) {
/* 387*/            wrapInstantiationProblem(e, ctxt);
/* 388*/            object = null;
/*   0*/          } 
/* 390*/          if (object == null) {
/* 391*/              throw ctxt.instantiationException(this._beanType.getRawClass(), "JSON Creator returned null"); 
/*   0*/             }
/* 394*/          p.setCurrentValue(object);
/* 397*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 398*/              return handlePolymorphic(p, ctxt, object, unknown); 
/*   0*/             }
/* 400*/          if (unknown != null) {
/* 401*/              object = handleUnknownProperties(ctxt, object, unknown); 
/*   0*/             }
/* 404*/          return deserialize(p, ctxt, object);
/*   0*/        } 
/* 409*/      } else if (!buffer.readIdProperty(propName)) {
/* 413*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 414*/        if (prop != null) {
/* 415*/          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 420*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 421*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 425*/        } else if (this._anySetter != null) {
/*   0*/          try {
/* 427*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/* 428*/          } catch (Exception e) {
/* 429*/            wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 434*/          if (unknown == null) {
/* 435*/              unknown = new TokenBuffer(p, ctxt); 
/*   0*/             }
/* 437*/          unknown.writeFieldName(propName);
/* 438*/          unknown.copyCurrentStructure(p);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 444*/      bean = creator.build(ctxt, buffer);
/* 445*/    } catch (Exception e) {
/* 446*/      wrapInstantiationProblem(e, ctxt);
/* 447*/      bean = null;
/*   0*/    } 
/* 449*/    if (unknown != null) {
/* 451*/      if (bean.getClass() != this._beanType.getRawClass()) {
/* 452*/          return handlePolymorphic(null, ctxt, bean, unknown); 
/*   0*/         }
/* 455*/      return handleUnknownProperties(ctxt, bean, unknown);
/*   0*/    } 
/* 457*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
/*   0*/    try {
/* 465*/      return prop.deserialize(p, ctxt);
/* 466*/    } catch (Exception e) {
/* 467*/      wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
/* 469*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 487*/    if (p.requiresCustomCodec()) {
/* 489*/      TokenBuffer tb = new TokenBuffer(p, ctxt);
/* 490*/      tb.writeEndObject();
/* 491*/      JsonParser p2 = tb.asParser(p);
/* 492*/      p2.nextToken();
/* 494*/      Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
/* 496*/      p2.close();
/* 497*/      return ob;
/*   0*/    } 
/* 499*/    throw ctxt.mappingException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
/* 512*/    if (p.hasTokenId(5)) {
/* 513*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 515*/        p.nextToken();
/* 517*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 518*/        if (prop != null) {
/* 519*/          if (!prop.visibleInView(activeView)) {
/* 520*/            p.skipChildren();
/*   0*/          } else {
/*   0*/            try {
/* 524*/              prop.deserializeAndSet(p, ctxt, bean);
/* 525*/            } catch (Exception e) {
/* 526*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/* 530*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 531*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 533*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 550*/    if (this._delegateDeserializer != null) {
/* 551*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 553*/    if (this._propertyBasedCreator != null) {
/* 554*/        return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt); 
/*   0*/       }
/* 556*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 557*/    tokens.writeStartObject();
/* 558*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 561*/    p.setCurrentValue(bean);
/* 563*/    if (this._injectables != null) {
/* 564*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 566*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 567*/    String propName = p.hasTokenId(5) ? p.getCurrentName() : null;
/* 569*/    for (; propName != null; propName = p.nextFieldName()) {
/* 570*/      p.nextToken();
/* 571*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 572*/      if (prop != null) {
/* 573*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 574*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 578*/            prop.deserializeAndSet(p, ctxt, bean);
/* 579*/          } catch (Exception e) {
/* 580*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 585*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 586*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 590*/        tokens.writeFieldName(propName);
/* 591*/        tokens.copyCurrentStructure(p);
/* 593*/        if (this._anySetter != null) {
/*   0*/            try {
/* 595*/              this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 596*/            } catch (Exception e) {
/* 597*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            }  
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 602*/    tokens.writeEndObject();
/* 603*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 604*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 611*/    JsonToken t = p.getCurrentToken();
/* 612*/    if (t == JsonToken.START_OBJECT) {
/* 613*/        t = p.nextToken(); 
/*   0*/       }
/* 615*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 616*/    tokens.writeStartObject();
/* 617*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 618*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 619*/      String propName = p.getCurrentName();
/* 620*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 621*/      p.nextToken();
/* 622*/      if (prop != null) {
/* 623*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 624*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 628*/            prop.deserializeAndSet(p, ctxt, bean);
/* 629*/          } catch (Exception e) {
/* 630*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 634*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 635*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 639*/        tokens.writeFieldName(propName);
/* 640*/        tokens.copyCurrentStructure(p);
/* 642*/        if (this._anySetter != null) {
/* 643*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 646*/    tokens.writeEndObject();
/* 647*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 648*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 655*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 656*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 658*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 659*/    tokens.writeStartObject();
/* 661*/    JsonToken t = p.getCurrentToken();
/* 662*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 663*/      String propName = p.getCurrentName();
/* 664*/      p.nextToken();
/* 666*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 667*/      if (creatorProp != null) {
/* 669*/        if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/          Object object;
/* 670*/          t = p.nextToken();
/*   0*/          try {
/* 673*/            object = creator.build(ctxt, buffer);
/* 674*/          } catch (Exception e) {
/* 675*/            wrapInstantiationProblem(e, ctxt);
/*   0*/          } 
/* 679*/          p.setCurrentValue(object);
/* 681*/          while (t == JsonToken.FIELD_NAME) {
/* 682*/            p.nextToken();
/* 683*/            tokens.copyCurrentStructure(p);
/* 684*/            t = p.nextToken();
/*   0*/          } 
/* 686*/          tokens.writeEndObject();
/* 687*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 690*/            tokens.close();
/* 691*/            throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
/*   0*/          } 
/* 693*/          return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, object, tokens);
/*   0*/        } 
/* 698*/      } else if (!buffer.readIdProperty(propName)) {
/* 702*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 703*/        if (prop != null) {
/* 704*/          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 708*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 709*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/*   0*/        } else {
/* 712*/          tokens.writeFieldName(propName);
/* 713*/          tokens.copyCurrentStructure(p);
/* 715*/          if (this._anySetter != null) {
/*   0*/              try {
/* 717*/                buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/* 718*/              } catch (Exception e) {
/* 719*/                wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/              }  
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 727*/      bean = creator.build(ctxt, buffer);
/* 728*/    } catch (Exception e) {
/* 729*/      wrapInstantiationProblem(e, ctxt);
/* 730*/      return null;
/*   0*/    } 
/* 732*/    return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 745*/    if (this._propertyBasedCreator != null) {
/* 746*/        return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt); 
/*   0*/       }
/* 748*/    if (this._delegateDeserializer != null) {
/* 754*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 758*/    return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 765*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 766*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 768*/    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 769*/      String propName = p.getCurrentName();
/* 770*/      t = p.nextToken();
/* 771*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 772*/      if (prop != null) {
/* 774*/        if (t.isScalarValue()) {
/* 775*/            ext.handleTypePropertyValue(p, ctxt, propName, bean); 
/*   0*/           }
/* 777*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 778*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 782*/            prop.deserializeAndSet(p, ctxt, bean);
/* 783*/          } catch (Exception e) {
/* 784*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 789*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 790*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 794*/      } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/* 798*/        if (this._anySetter != null) {
/*   0*/          try {
/* 800*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 801*/          } catch (Exception e) {
/* 802*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 807*/          handleUnknownProperty(p, ctxt, bean, propName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 810*/    return ext.complete(p, ctxt, bean);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 817*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 818*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 819*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 821*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 822*/    tokens.writeStartObject();
/* 824*/    JsonToken t = p.getCurrentToken();
/* 825*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 826*/      String propName = p.getCurrentName();
/* 827*/      p.nextToken();
/* 829*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 830*/      if (creatorProp != null) {
/* 834*/        if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/* 838*/            if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/              Object bean;
/* 839*/              t = p.nextToken();
/*   0*/              try {
/* 842*/                bean = creator.build(ctxt, buffer);
/* 843*/              } catch (Exception e) {
/* 844*/                wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/              } 
/* 848*/              while (t == JsonToken.FIELD_NAME) {
/* 849*/                p.nextToken();
/* 850*/                tokens.copyCurrentStructure(p);
/* 851*/                t = p.nextToken();
/*   0*/              } 
/* 853*/              if (bean.getClass() != this._beanType.getRawClass()) {
/* 856*/                  throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values"); 
/*   0*/                 }
/* 858*/              return ext.complete(p, ctxt, bean);
/*   0*/            }  
/*   0*/           }
/* 864*/      } else if (!buffer.readIdProperty(propName)) {
/* 868*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 869*/        if (prop != null) {
/* 870*/          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/* 874*/        } else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/* 878*/          if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 879*/            handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 883*/          } else if (this._anySetter != null) {
/* 884*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 890*/      return ext.complete(p, ctxt, buffer, creator);
/* 891*/    } catch (Exception e) {
/* 892*/      wrapInstantiationProblem(e, ctxt);
/* 893*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
