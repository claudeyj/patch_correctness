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
/* 127*/      p.nextToken();
/* 128*/      if (this._objectIdReader != null) {
/* 129*/          return deserializeWithObjectId(p, ctxt); 
/*   0*/         }
/* 131*/      return deserializeFromObject(p, ctxt);
/*   0*/    } 
/* 133*/    JsonToken t = p.getCurrentToken();
/* 134*/    return _deserializeOther(p, ctxt, t);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 141*/    switch (t) {
/*   0*/      case VALUE_STRING:
/* 143*/        return deserializeFromString(p, ctxt);
/*   0*/      case VALUE_NUMBER_INT:
/* 145*/        return deserializeFromNumber(p, ctxt);
/*   0*/      case VALUE_NUMBER_FLOAT:
/* 147*/        return deserializeFromDouble(p, ctxt);
/*   0*/      case VALUE_EMBEDDED_OBJECT:
/* 149*/        return deserializeFromEmbedded(p, ctxt);
/*   0*/      case VALUE_TRUE:
/*   0*/      case VALUE_FALSE:
/* 152*/        return deserializeFromBoolean(p, ctxt);
/*   0*/      case START_ARRAY:
/* 155*/        return deserializeFromArray(p, ctxt);
/*   0*/      case FIELD_NAME:
/*   0*/      case END_OBJECT:
/* 158*/        if (this._vanillaProcessing) {
/* 159*/            return vanillaDeserialize(p, ctxt, t); 
/*   0*/           }
/* 161*/        if (this._objectIdReader != null) {
/* 162*/            return deserializeWithObjectId(p, ctxt); 
/*   0*/           }
/* 164*/        return deserializeFromObject(p, ctxt);
/*   0*/    } 
/* 166*/    throw ctxt.mappingException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 171*/    throw ctxt.endOfInputException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*   0*/    String propName;
/* 183*/    p.setCurrentValue(bean);
/* 184*/    if (this._injectables != null) {
/* 185*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 187*/    if (this._unwrappedPropertyHandler != null) {
/* 188*/        return deserializeWithUnwrapped(p, ctxt, bean); 
/*   0*/       }
/* 190*/    if (this._externalTypeIdHandler != null) {
/* 191*/        return deserializeWithExternalTypeId(p, ctxt, bean); 
/*   0*/       }
/* 196*/    if (p.isExpectedStartObjectToken()) {
/* 197*/      propName = p.nextFieldName();
/* 198*/      if (propName == null) {
/* 199*/          return bean; 
/*   0*/         }
/* 202*/    } else if (p.hasTokenId(5)) {
/* 203*/      propName = p.getCurrentName();
/*   0*/    } else {
/* 205*/      return bean;
/*   0*/    } 
/* 208*/    if (this._needViewProcesing) {
/* 209*/      Class<?> view = ctxt.getActiveView();
/* 210*/      if (view != null) {
/* 211*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/*   0*/    while (true) {
/* 215*/      p.nextToken();
/* 216*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 218*/      if (prop != null) {
/*   0*/        try {
/* 220*/          prop.deserializeAndSet(p, ctxt, bean);
/* 221*/        } catch (Exception e) {
/* 222*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 226*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/* 227*/      if ((propName = p.nextFieldName()) == null) {
/* 228*/          return bean; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 245*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 247*/    p.setCurrentValue(bean);
/* 248*/    if (p.hasTokenId(5)) {
/* 249*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 251*/        p.nextToken();
/* 252*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 254*/        if (prop != null) {
/*   0*/          try {
/* 256*/            prop.deserializeAndSet(p, ctxt, bean);
/* 257*/          } catch (Exception e) {
/* 258*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 262*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 263*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 265*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 281*/    if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && 
/* 282*/      p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
/* 284*/        return deserializeFromObjectId(p, ctxt); 
/*   0*/       }
/* 287*/    if (this._nonStandardCreation) {
/* 288*/      if (this._unwrappedPropertyHandler != null) {
/* 289*/          return deserializeWithUnwrapped(p, ctxt); 
/*   0*/         }
/* 291*/      if (this._externalTypeIdHandler != null) {
/* 292*/          return deserializeWithExternalTypeId(p, ctxt); 
/*   0*/         }
/* 294*/      Object object = deserializeFromObjectUsingNonDefault(p, ctxt);
/* 295*/      if (this._injectables != null) {
/* 296*/          injectValues(ctxt, object); 
/*   0*/         }
/* 310*/      return object;
/*   0*/    } 
/* 312*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 314*/    p.setCurrentValue(bean);
/* 315*/    if (p.canReadObjectId()) {
/* 316*/      Object id = p.getObjectId();
/* 317*/      if (id != null) {
/* 318*/          _handleTypedObjectId(p, ctxt, bean, id); 
/*   0*/         }
/*   0*/    } 
/* 321*/    if (this._injectables != null) {
/* 322*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 324*/    if (this._needViewProcesing) {
/* 325*/      Class<?> view = ctxt.getActiveView();
/* 326*/      if (view != null) {
/* 327*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/* 330*/    if (p.hasTokenId(5)) {
/* 331*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 333*/        p.nextToken();
/* 334*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 335*/        if (prop != null) {
/*   0*/          try {
/* 337*/            prop.deserializeAndSet(p, ctxt, bean);
/* 338*/          } catch (Exception e) {
/* 339*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 343*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 344*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 346*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 362*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 363*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 366*/    TokenBuffer unknown = null;
/* 368*/    JsonToken t = p.getCurrentToken();
/* 369*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 370*/      String propName = p.getCurrentName();
/* 371*/      p.nextToken();
/* 373*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 374*/      if (creatorProp != null) {
/* 376*/        if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/          Object object;
/* 378*/          p.nextToken();
/*   0*/          try {
/* 381*/            object = creator.build(ctxt, buffer);
/* 382*/          } catch (Exception e) {
/* 383*/            wrapInstantiationProblem(e, ctxt);
/* 384*/            object = null;
/*   0*/          } 
/* 386*/          if (object == null) {
/* 387*/              throw ctxt.instantiationException(this._beanType.getRawClass(), "JSON Creator returned null"); 
/*   0*/             }
/* 390*/          p.setCurrentValue(object);
/* 393*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 394*/              return handlePolymorphic(p, ctxt, object, unknown); 
/*   0*/             }
/* 396*/          if (unknown != null) {
/* 397*/              object = handleUnknownProperties(ctxt, object, unknown); 
/*   0*/             }
/* 400*/          return deserialize(p, ctxt, object);
/*   0*/        } 
/* 405*/      } else if (!buffer.readIdProperty(propName)) {
/* 409*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 410*/        if (prop != null) {
/* 411*/          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 416*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 417*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 421*/        } else if (this._anySetter != null) {
/*   0*/          try {
/* 423*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/* 424*/          } catch (Exception e) {
/* 425*/            wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 430*/          if (unknown == null) {
/* 431*/              unknown = new TokenBuffer(p); 
/*   0*/             }
/* 433*/          unknown.writeFieldName(propName);
/* 434*/          unknown.copyCurrentStructure(p);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 440*/      bean = creator.build(ctxt, buffer);
/* 441*/    } catch (Exception e) {
/* 442*/      wrapInstantiationProblem(e, ctxt);
/* 443*/      bean = null;
/*   0*/    } 
/* 445*/    if (unknown != null) {
/* 447*/      if (bean.getClass() != this._beanType.getRawClass()) {
/* 448*/          return handlePolymorphic(null, ctxt, bean, unknown); 
/*   0*/         }
/* 451*/      return handleUnknownProperties(ctxt, bean, unknown);
/*   0*/    } 
/* 453*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
/*   0*/    try {
/* 461*/      return prop.deserialize(p, ctxt);
/* 462*/    } catch (Exception e) {
/* 463*/      wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
/* 465*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
/* 479*/    if (p.hasTokenId(5)) {
/* 480*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 482*/        p.nextToken();
/* 484*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 485*/        if (prop != null) {
/* 486*/          if (!prop.visibleInView(activeView)) {
/* 487*/            p.skipChildren();
/*   0*/          } else {
/*   0*/            try {
/* 491*/              prop.deserializeAndSet(p, ctxt, bean);
/* 492*/            } catch (Exception e) {
/* 493*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/* 497*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 498*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 500*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 517*/    if (this._delegateDeserializer != null) {
/* 518*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 520*/    if (this._propertyBasedCreator != null) {
/* 521*/        return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt); 
/*   0*/       }
/* 523*/    TokenBuffer tokens = new TokenBuffer(p);
/* 524*/    tokens.writeStartObject();
/* 525*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 528*/    p.setCurrentValue(bean);
/* 530*/    if (this._injectables != null) {
/* 531*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 533*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 534*/    String propName = p.hasTokenId(5) ? p.getCurrentName() : null;
/* 536*/    for (; propName != null; propName = p.nextFieldName()) {
/* 537*/      p.nextToken();
/* 538*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 539*/      if (prop != null) {
/* 540*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 541*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 545*/            prop.deserializeAndSet(p, ctxt, bean);
/* 546*/          } catch (Exception e) {
/* 547*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 552*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 553*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 557*/        tokens.writeFieldName(propName);
/* 558*/        tokens.copyCurrentStructure(p);
/* 560*/        if (this._anySetter != null) {
/*   0*/            try {
/* 562*/              this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 563*/            } catch (Exception e) {
/* 564*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            }  
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 569*/    tokens.writeEndObject();
/* 570*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 571*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 578*/    JsonToken t = p.getCurrentToken();
/* 579*/    if (t == JsonToken.START_OBJECT) {
/* 580*/        t = p.nextToken(); 
/*   0*/       }
/* 582*/    TokenBuffer tokens = new TokenBuffer(p);
/* 583*/    tokens.writeStartObject();
/* 584*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 585*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 586*/      String propName = p.getCurrentName();
/* 587*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 588*/      p.nextToken();
/* 589*/      if (prop != null) {
/* 590*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 591*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 595*/            prop.deserializeAndSet(p, ctxt, bean);
/* 596*/          } catch (Exception e) {
/* 597*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 601*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 602*/        handleIgnoredProperty(p, ctxt, bean, propName);
/*   0*/      } else {
/* 606*/        tokens.writeFieldName(propName);
/* 607*/        tokens.copyCurrentStructure(p);
/* 609*/        if (this._anySetter != null) {
/* 610*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 613*/    tokens.writeEndObject();
/* 614*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 615*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 622*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 623*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 625*/    TokenBuffer tokens = new TokenBuffer(p);
/* 626*/    tokens.writeStartObject();
/* 628*/    JsonToken t = p.getCurrentToken();
/* 629*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 630*/      String propName = p.getCurrentName();
/* 631*/      p.nextToken();
/* 633*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 634*/      if (creatorProp != null) {
/* 636*/        if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/          Object object;
/* 637*/          t = p.nextToken();
/*   0*/          try {
/* 640*/            object = creator.build(ctxt, buffer);
/* 641*/          } catch (Exception e) {
/* 642*/            wrapInstantiationProblem(e, ctxt);
/*   0*/          } 
/* 646*/          p.setCurrentValue(object);
/* 648*/          while (t == JsonToken.FIELD_NAME) {
/* 649*/            p.nextToken();
/* 650*/            tokens.copyCurrentStructure(p);
/* 651*/            t = p.nextToken();
/*   0*/          } 
/* 653*/          tokens.writeEndObject();
/* 654*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 657*/            tokens.close();
/* 658*/            throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
/*   0*/          } 
/* 660*/          return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, object, tokens);
/*   0*/        } 
/* 665*/      } else if (!buffer.readIdProperty(propName)) {
/* 669*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 670*/        if (prop != null) {
/* 671*/          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 678*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 679*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/*   0*/        } else {
/* 682*/          tokens.writeFieldName(propName);
/* 683*/          tokens.copyCurrentStructure(p);
/* 685*/          if (this._anySetter != null) {
/*   0*/              try {
/* 687*/                buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/* 688*/              } catch (Exception e) {
/* 689*/                wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/              }  
/*   0*/             }
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 697*/      bean = creator.build(ctxt, buffer);
/* 698*/    } catch (Exception e) {
/* 699*/      wrapInstantiationProblem(e, ctxt);
/* 700*/      return null;
/*   0*/    } 
/* 702*/    return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 715*/    if (this._propertyBasedCreator != null) {
/* 716*/        return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt); 
/*   0*/       }
/* 718*/    return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 725*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 726*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 728*/    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 729*/      String propName = p.getCurrentName();
/* 730*/      t = p.nextToken();
/* 731*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 732*/      if (prop != null) {
/* 734*/        if (t.isScalarValue()) {
/* 735*/            ext.handleTypePropertyValue(p, ctxt, propName, bean); 
/*   0*/           }
/* 737*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 738*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 742*/            prop.deserializeAndSet(p, ctxt, bean);
/* 743*/          } catch (Exception e) {
/* 744*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 749*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 750*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 754*/      } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/* 758*/        if (this._anySetter != null) {
/*   0*/          try {
/* 760*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 761*/          } catch (Exception e) {
/* 762*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 767*/          handleUnknownProperty(p, ctxt, bean, propName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 770*/    return ext.complete(p, ctxt, bean);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 777*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 778*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 779*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 781*/    TokenBuffer tokens = new TokenBuffer(p);
/* 782*/    tokens.writeStartObject();
/* 784*/    JsonToken t = p.getCurrentToken();
/* 785*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 786*/      String propName = p.getCurrentName();
/* 787*/      p.nextToken();
/* 789*/      SettableBeanProperty creatorProp = null;
/* 790*/      if (creatorProp != null) {
/* 794*/        if (!ext.handlePropertyValue(p, ctxt, propName, buffer)) {
/* 798*/            if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/              Object bean;
/* 799*/              t = p.nextToken();
/*   0*/              try {
/* 802*/                bean = creator.build(ctxt, buffer);
/* 803*/              } catch (Exception e) {
/* 804*/                wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/              } 
/* 808*/              while (t == JsonToken.FIELD_NAME) {
/* 809*/                p.nextToken();
/* 810*/                tokens.copyCurrentStructure(p);
/* 811*/                t = p.nextToken();
/*   0*/              } 
/* 813*/              if (bean.getClass() != this._beanType.getRawClass()) {
/* 816*/                  throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values"); 
/*   0*/                 }
/* 818*/              return ext.complete(p, ctxt, bean);
/*   0*/            }  
/*   0*/           }
/* 824*/      } else if (!buffer.readIdProperty(propName)) {
/* 828*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 829*/        if (prop != null) {
/* 830*/          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/* 834*/        } else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/* 840*/          if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 841*/            handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 845*/          } else if (this._anySetter != null) {
/* 846*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 852*/      return ext.complete(p, ctxt, buffer, creator);
/* 853*/    } catch (Exception e) {
/* 854*/      wrapInstantiationProblem(e, ctxt);
/* 855*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/}
