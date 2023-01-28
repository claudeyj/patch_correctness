/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanAsArrayDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class BeanDeserializer extends BeanDeserializerBase implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  protected transient Exception _nullFromCreator;
/*   0*/  
/*   0*/  private volatile transient NameTransformer _currentlyTransforming;
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/*  64*/    super(builder, beanDesc, properties, backRefs, ignorableProps, ignoreAllUnknown, hasViews);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src) {
/*  73*/    super(src, src._ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/*  77*/    super(src, ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializer(BeanDeserializerBase src, NameTransformer unwrapper) {
/*  81*/    super(src, unwrapper);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBase src, ObjectIdReader oir) {
/*  85*/    super(src, oir);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBase src, Set<String> ignorableProps) {
/*  89*/    super(src, ignorableProps);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer(BeanDeserializerBase src, BeanPropertyMap props) {
/*  93*/    super(src, props);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> unwrappingDeserializer(NameTransformer transformer) {
/* 101*/    if (getClass() != BeanDeserializer.class) {
/* 102*/        return this; 
/*   0*/       }
/* 106*/    if (this._currentlyTransforming == transformer) {
/* 107*/        return this; 
/*   0*/       }
/* 109*/    this._currentlyTransforming = transformer;
/*   0*/    try {
/* 111*/      return new BeanDeserializer(this, transformer);
/*   0*/    } finally {
/* 112*/      this._currentlyTransforming = null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer withObjectIdReader(ObjectIdReader oir) {
/* 117*/    return new BeanDeserializer(this, oir);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializer withIgnorableProperties(Set<String> ignorableProps) {
/* 122*/    return new BeanDeserializer(this, ignorableProps);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase withBeanProperties(BeanPropertyMap props) {
/* 127*/    return new BeanDeserializer(this, props);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase asArrayDeserializer() {
/* 132*/    SettableBeanProperty[] props = this._beanProperties.getPropertiesInInsertionOrder();
/* 133*/    return new BeanAsArrayDeserializer(this, props);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 149*/    if (p.isExpectedStartObjectToken()) {
/* 150*/      if (this._vanillaProcessing) {
/* 151*/          return vanillaDeserialize(p, ctxt, p.nextToken()); 
/*   0*/         }
/* 155*/      p.nextToken();
/* 156*/      if (this._objectIdReader != null) {
/* 157*/          return deserializeWithObjectId(p, ctxt); 
/*   0*/         }
/* 159*/      return deserializeFromObject(p, ctxt);
/*   0*/    } 
/* 161*/    return _deserializeOther(p, ctxt, p.getCurrentToken());
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeOther(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 168*/    if (t != null) {
/* 169*/        switch (t) {
/*   0*/          case VALUE_STRING:
/* 171*/            return deserializeFromString(p, ctxt);
/*   0*/          case VALUE_NUMBER_INT:
/* 173*/            return deserializeFromNumber(p, ctxt);
/*   0*/          case VALUE_NUMBER_FLOAT:
/* 175*/            return deserializeFromDouble(p, ctxt);
/*   0*/          case VALUE_EMBEDDED_OBJECT:
/* 177*/            return deserializeFromEmbedded(p, ctxt);
/*   0*/          case VALUE_TRUE:
/*   0*/          case VALUE_FALSE:
/* 180*/            return deserializeFromBoolean(p, ctxt);
/*   0*/          case VALUE_NULL:
/* 182*/            return deserializeFromNull(p, ctxt);
/*   0*/          case START_ARRAY:
/* 185*/            return deserializeFromArray(p, ctxt);
/*   0*/          case FIELD_NAME:
/*   0*/          case END_OBJECT:
/* 188*/            if (this._vanillaProcessing) {
/* 189*/                return vanillaDeserialize(p, ctxt, t); 
/*   0*/               }
/* 191*/            if (this._objectIdReader != null) {
/* 192*/                return deserializeWithObjectId(p, ctxt); 
/*   0*/               }
/* 194*/            return deserializeFromObject(p, ctxt);
/*   0*/        }  
/*   0*/       }
/* 198*/    return ctxt.handleUnexpectedToken(handledType(), p);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  protected Object _missingToken(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 203*/    throw ctxt.endOfInputException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserialize(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/*   0*/    String propName;
/* 215*/    p.setCurrentValue(bean);
/* 216*/    if (this._injectables != null) {
/* 217*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 219*/    if (this._unwrappedPropertyHandler != null) {
/* 220*/        return deserializeWithUnwrapped(p, ctxt, bean); 
/*   0*/       }
/* 222*/    if (this._externalTypeIdHandler != null) {
/* 223*/        return deserializeWithExternalTypeId(p, ctxt, bean); 
/*   0*/       }
/* 228*/    if (p.isExpectedStartObjectToken()) {
/* 229*/      propName = p.nextFieldName();
/* 230*/      if (propName == null) {
/* 231*/          return bean; 
/*   0*/         }
/* 234*/    } else if (p.hasTokenId(5)) {
/* 235*/      propName = p.getCurrentName();
/*   0*/    } else {
/* 237*/      return bean;
/*   0*/    } 
/* 240*/    if (this._needViewProcesing) {
/* 241*/      Class<?> view = ctxt.getActiveView();
/* 242*/      if (view != null) {
/* 243*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/*   0*/    while (true) {
/* 247*/      p.nextToken();
/* 248*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 250*/      if (prop != null) {
/*   0*/        try {
/* 252*/          prop.deserializeAndSet(p, ctxt, bean);
/* 253*/        } catch (Exception e) {
/* 254*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } else {
/* 258*/        handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/      } 
/* 259*/      if ((propName = p.nextFieldName()) == null) {
/* 260*/          return bean; 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final Object vanillaDeserialize(JsonParser p, DeserializationContext ctxt, JsonToken t) throws IOException {
/* 277*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 279*/    p.setCurrentValue(bean);
/* 280*/    if (p.hasTokenId(5)) {
/* 281*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 283*/        p.nextToken();
/* 284*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 286*/        if (prop != null) {
/*   0*/          try {
/* 288*/            prop.deserializeAndSet(p, ctxt, bean);
/* 289*/          } catch (Exception e) {
/* 290*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 294*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 295*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 297*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromObject(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 313*/    if (this._objectIdReader != null && this._objectIdReader.maySerializeAsObject() && 
/* 314*/      p.hasTokenId(5) && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
/* 316*/        return deserializeFromObjectId(p, ctxt); 
/*   0*/       }
/* 319*/    if (this._nonStandardCreation) {
/* 320*/      if (this._unwrappedPropertyHandler != null) {
/* 321*/          return deserializeWithUnwrapped(p, ctxt); 
/*   0*/         }
/* 323*/      if (this._externalTypeIdHandler != null) {
/* 324*/          return deserializeWithExternalTypeId(p, ctxt); 
/*   0*/         }
/* 326*/      Object object = deserializeFromObjectUsingNonDefault(p, ctxt);
/* 327*/      if (this._injectables != null) {
/* 328*/          injectValues(ctxt, object); 
/*   0*/         }
/* 342*/      return object;
/*   0*/    } 
/* 344*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 346*/    p.setCurrentValue(bean);
/* 347*/    if (p.canReadObjectId()) {
/* 348*/      Object id = p.getObjectId();
/* 349*/      if (id != null) {
/* 350*/          _handleTypedObjectId(p, ctxt, bean, id); 
/*   0*/         }
/*   0*/    } 
/* 353*/    if (this._injectables != null) {
/* 354*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 356*/    if (this._needViewProcesing) {
/* 357*/      Class<?> view = ctxt.getActiveView();
/* 358*/      if (view != null) {
/* 359*/          return deserializeWithView(p, ctxt, bean, view); 
/*   0*/         }
/*   0*/    } 
/* 362*/    if (p.hasTokenId(5)) {
/* 363*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 365*/        p.nextToken();
/* 366*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 367*/        if (prop != null) {
/*   0*/          try {
/* 369*/            prop.deserializeAndSet(p, ctxt, bean);
/* 370*/          } catch (Exception e) {
/* 371*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 375*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 376*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 378*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _deserializeUsingPropertyBased(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 394*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 395*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 396*/    TokenBuffer unknown = null;
/* 397*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 399*/    JsonToken t = p.getCurrentToken();
/* 400*/    List<BeanReferring> referrings = null;
/* 401*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 402*/      String propName = p.getCurrentName();
/* 403*/      p.nextToken();
/* 405*/      if (!buffer.readIdProperty(propName)) {
/* 409*/        SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 410*/        if (creatorProp != null) {
/* 413*/          if (activeView != null && !creatorProp.visibleInView(activeView)) {
/* 414*/            p.skipChildren();
/*   0*/          } else {
/* 417*/            Object value = _deserializeWithErrorWrapping(p, ctxt, creatorProp);
/* 418*/            if (buffer.assignParameter(creatorProp, value)) {
/*   0*/              Object object;
/* 419*/              p.nextToken();
/*   0*/              try {
/* 422*/                object = creator.build(ctxt, buffer);
/* 423*/              } catch (Exception e) {
/* 424*/                object = wrapInstantiationProblem(e, ctxt);
/*   0*/              } 
/* 426*/              if (object == null) {
/* 427*/                  return ctxt.handleInstantiationProblem(handledType(), null, _creatorReturnedNullException()); 
/*   0*/                 }
/* 431*/              p.setCurrentValue(object);
/* 434*/              if (object.getClass() != this._beanType.getRawClass()) {
/* 435*/                  return handlePolymorphic(p, ctxt, object, unknown); 
/*   0*/                 }
/* 437*/              if (unknown != null) {
/* 438*/                  object = handleUnknownProperties(ctxt, object, unknown); 
/*   0*/                 }
/* 441*/              return deserialize(p, ctxt, object);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/* 446*/          SettableBeanProperty prop = this._beanProperties.find(propName);
/* 447*/          if (prop != null) {
/*   0*/            try {
/* 449*/              buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 450*/            } catch (UnresolvedForwardReference reference) {
/* 454*/              BeanReferring referring = handleUnresolvedReference(ctxt, prop, buffer, reference);
/* 456*/              if (referrings == null) {
/* 457*/                  referrings = new ArrayList<>(); 
/*   0*/                 }
/* 459*/              referrings.add(referring);
/*   0*/            } 
/* 464*/          } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 465*/            handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 469*/          } else if (this._anySetter != null) {
/*   0*/            try {
/* 471*/              buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/* 472*/            } catch (Exception e) {
/* 473*/              wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/            } 
/*   0*/          } else {
/* 478*/            if (unknown == null) {
/* 479*/                unknown = new TokenBuffer(p, ctxt); 
/*   0*/               }
/* 481*/            unknown.writeFieldName(propName);
/* 482*/            unknown.copyCurrentStructure(p);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 488*/      bean = creator.build(ctxt, buffer);
/* 489*/    } catch (Exception e) {
/* 490*/      wrapInstantiationProblem(e, ctxt);
/* 491*/      bean = null;
/*   0*/    } 
/* 493*/    if (referrings != null) {
/* 494*/        for (BeanReferring referring : referrings) {
/* 495*/            referring.setBean(bean); 
/*   0*/           } 
/*   0*/       }
/* 498*/    if (unknown != null) {
/* 500*/      if (bean.getClass() != this._beanType.getRawClass()) {
/* 501*/          return handlePolymorphic(null, ctxt, bean, unknown); 
/*   0*/         }
/* 504*/      return handleUnknownProperties(ctxt, bean, unknown);
/*   0*/    } 
/* 506*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  private BeanReferring handleUnresolvedReference(DeserializationContext ctxt, SettableBeanProperty prop, PropertyValueBuffer buffer, UnresolvedForwardReference reference) throws JsonMappingException {
/* 517*/    BeanReferring referring = new BeanReferring(ctxt, reference, prop.getType(), buffer, prop);
/* 519*/    reference.getRoid().appendReferring(referring);
/* 520*/    return referring;
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object _deserializeWithErrorWrapping(JsonParser p, DeserializationContext ctxt, SettableBeanProperty prop) throws IOException {
/*   0*/    try {
/* 528*/      return prop.deserialize(p, ctxt);
/* 529*/    } catch (Exception e) {
/* 530*/      wrapAndThrow(e, this._beanType.getRawClass(), prop.getName(), ctxt);
/* 532*/      return null;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeFromNull(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 550*/    if (p.requiresCustomCodec()) {
/* 552*/      TokenBuffer tb = new TokenBuffer(p, ctxt);
/* 553*/      tb.writeEndObject();
/* 554*/      JsonParser p2 = tb.asParser(p);
/* 555*/      p2.nextToken();
/* 557*/      Object ob = this._vanillaProcessing ? vanillaDeserialize(p2, ctxt, JsonToken.END_OBJECT) : deserializeFromObject(p2, ctxt);
/* 559*/      p2.close();
/* 560*/      return ob;
/*   0*/    } 
/* 562*/    return ctxt.handleUnexpectedToken(handledType(), p);
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object deserializeWithView(JsonParser p, DeserializationContext ctxt, Object bean, Class<?> activeView) throws IOException {
/* 575*/    if (p.hasTokenId(5)) {
/* 576*/      String propName = p.getCurrentName();
/*   0*/      do {
/* 578*/        p.nextToken();
/* 580*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 581*/        if (prop != null) {
/* 582*/          if (!prop.visibleInView(activeView)) {
/* 583*/            p.skipChildren();
/*   0*/          } else {
/*   0*/            try {
/* 587*/              prop.deserializeAndSet(p, ctxt, bean);
/* 588*/            } catch (Exception e) {
/* 589*/              wrapAndThrow(e, bean, propName, ctxt);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/* 593*/          handleUnknownVanilla(p, ctxt, bean, propName);
/*   0*/        } 
/* 594*/      } while ((propName = p.nextFieldName()) != null);
/*   0*/    } 
/* 596*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 613*/    if (this._delegateDeserializer != null) {
/* 614*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 616*/    if (this._propertyBasedCreator != null) {
/* 617*/        return deserializeUsingPropertyBasedWithUnwrapped(p, ctxt); 
/*   0*/       }
/* 619*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 620*/    tokens.writeStartObject();
/* 621*/    Object bean = this._valueInstantiator.createUsingDefault(ctxt);
/* 624*/    p.setCurrentValue(bean);
/* 626*/    if (this._injectables != null) {
/* 627*/        injectValues(ctxt, bean); 
/*   0*/       }
/* 629*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 630*/    String propName = p.hasTokenId(5) ? p.getCurrentName() : null;
/* 632*/    for (; propName != null; propName = p.nextFieldName()) {
/* 633*/      p.nextToken();
/* 634*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 635*/      if (prop != null) {
/* 636*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 637*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 641*/            prop.deserializeAndSet(p, ctxt, bean);
/* 642*/          } catch (Exception e) {
/* 643*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 648*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 649*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 656*/      } else if (this._anySetter == null) {
/* 658*/        tokens.writeFieldName(propName);
/* 659*/        tokens.copyCurrentStructure(p);
/*   0*/      } else {
/* 663*/        TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
/* 664*/        tokens.writeFieldName(propName);
/* 665*/        tokens.append(b2);
/*   0*/        try {
/* 667*/          this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
/* 668*/        } catch (Exception e) {
/* 669*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 672*/    tokens.writeEndObject();
/* 673*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 674*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithUnwrapped(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 682*/    JsonToken t = p.getCurrentToken();
/* 683*/    if (t == JsonToken.START_OBJECT) {
/* 684*/        t = p.nextToken(); 
/*   0*/       }
/* 686*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 687*/    tokens.writeStartObject();
/* 688*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 689*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 690*/      String propName = p.getCurrentName();
/* 691*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 692*/      p.nextToken();
/* 693*/      if (prop != null) {
/* 694*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 695*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 699*/            prop.deserializeAndSet(p, ctxt, bean);
/* 700*/          } catch (Exception e) {
/* 701*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 705*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 706*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 713*/      } else if (this._anySetter == null) {
/* 715*/        tokens.writeFieldName(propName);
/* 716*/        tokens.copyCurrentStructure(p);
/*   0*/      } else {
/* 719*/        TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
/* 720*/        tokens.writeFieldName(propName);
/* 721*/        tokens.append(b2);
/*   0*/        try {
/* 723*/          this._anySetter.deserializeAndSet(b2.asParserOnFirstToken(), ctxt, bean, propName);
/* 724*/        } catch (Exception e) {
/* 725*/          wrapAndThrow(e, bean, propName, ctxt);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 730*/    tokens.writeEndObject();
/* 731*/    this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/* 732*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser p, DeserializationContext ctxt) throws IOException {
/*   0*/    Object bean;
/* 743*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 744*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 746*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 747*/    tokens.writeStartObject();
/* 749*/    JsonToken t = p.getCurrentToken();
/* 750*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 751*/      String propName = p.getCurrentName();
/* 752*/      p.nextToken();
/* 754*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 755*/      if (creatorProp != null) {
/* 757*/        if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/          Object object;
/* 759*/          t = p.nextToken();
/*   0*/          try {
/* 762*/            object = creator.build(ctxt, buffer);
/* 763*/          } catch (Exception e) {
/* 764*/            object = wrapInstantiationProblem(e, ctxt);
/*   0*/          } 
/* 767*/          p.setCurrentValue(object);
/* 769*/          while (t == JsonToken.FIELD_NAME) {
/* 770*/            p.nextToken();
/* 771*/            tokens.copyCurrentStructure(p);
/* 772*/            t = p.nextToken();
/*   0*/          } 
/* 774*/          tokens.writeEndObject();
/* 775*/          if (object.getClass() != this._beanType.getRawClass()) {
/* 778*/            ctxt.reportInputMismatch(creatorProp, "Cannot create polymorphic instances with unwrapped values", new Object[0]);
/* 780*/            return null;
/*   0*/          } 
/* 782*/          return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, object, tokens);
/*   0*/        } 
/* 787*/      } else if (!buffer.readIdProperty(propName)) {
/* 791*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 792*/        if (prop != null) {
/* 793*/          buffer.bufferProperty(prop, _deserializeWithErrorWrapping(p, ctxt, prop));
/* 797*/        } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 798*/          handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 805*/        } else if (this._anySetter == null) {
/* 807*/          tokens.writeFieldName(propName);
/* 808*/          tokens.copyCurrentStructure(p);
/*   0*/        } else {
/* 811*/          TokenBuffer b2 = TokenBuffer.asCopyOfValue(p);
/* 812*/          tokens.writeFieldName(propName);
/* 813*/          tokens.append(b2);
/*   0*/          try {
/* 815*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(b2.asParserOnFirstToken(), ctxt));
/* 817*/          } catch (Exception e) {
/* 818*/            wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/    try {
/* 827*/      bean = creator.build(ctxt, buffer);
/* 828*/    } catch (Exception e) {
/* 829*/      wrapInstantiationProblem(e, ctxt);
/* 830*/      return null;
/*   0*/    } 
/* 832*/    return this._unwrappedPropertyHandler.processUnwrapped(p, ctxt, bean, tokens);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 845*/    if (this._propertyBasedCreator != null) {
/* 846*/        return deserializeUsingPropertyBasedWithExternalTypeId(p, ctxt); 
/*   0*/       }
/* 848*/    if (this._delegateDeserializer != null) {
/* 854*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/* 858*/    return deserializeWithExternalTypeId(p, ctxt, this._valueInstantiator.createUsingDefault(ctxt));
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithExternalTypeId(JsonParser p, DeserializationContext ctxt, Object bean) throws IOException {
/* 865*/    Class<?> activeView = this._needViewProcesing ? ctxt.getActiveView() : null;
/* 866*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 868*/    for (JsonToken t = p.getCurrentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 869*/      String propName = p.getCurrentName();
/* 870*/      t = p.nextToken();
/* 871*/      SettableBeanProperty prop = this._beanProperties.find(propName);
/* 872*/      if (prop != null) {
/* 874*/        if (t.isScalarValue()) {
/* 875*/            ext.handleTypePropertyValue(p, ctxt, propName, bean); 
/*   0*/           }
/* 877*/        if (activeView != null && !prop.visibleInView(activeView)) {
/* 878*/          p.skipChildren();
/*   0*/        } else {
/*   0*/          try {
/* 882*/            prop.deserializeAndSet(p, ctxt, bean);
/* 883*/          } catch (Exception e) {
/* 884*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } 
/* 889*/      } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 890*/        handleIgnoredProperty(p, ctxt, bean, propName);
/* 894*/      } else if (!ext.handlePropertyValue(p, ctxt, propName, bean)) {
/* 898*/        if (this._anySetter != null) {
/*   0*/          try {
/* 900*/            this._anySetter.deserializeAndSet(p, ctxt, bean, propName);
/* 901*/          } catch (Exception e) {
/* 902*/            wrapAndThrow(e, bean, propName, ctxt);
/*   0*/          } 
/*   0*/        } else {
/* 907*/          handleUnknownProperty(p, ctxt, bean, propName);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 910*/    return ext.complete(p, ctxt, bean);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser p, DeserializationContext ctxt) throws IOException {
/* 917*/    ExternalTypeHandler ext = this._externalTypeIdHandler.start();
/* 918*/    PropertyBasedCreator creator = this._propertyBasedCreator;
/* 919*/    PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, this._objectIdReader);
/* 921*/    TokenBuffer tokens = new TokenBuffer(p, ctxt);
/* 922*/    tokens.writeStartObject();
/* 924*/    JsonToken t = p.getCurrentToken();
/* 925*/    for (; t == JsonToken.FIELD_NAME; t = p.nextToken()) {
/* 926*/      String propName = p.getCurrentName();
/* 927*/      p.nextToken();
/* 929*/      SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
/* 930*/      if (creatorProp != null) {
/* 934*/        if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/* 938*/            if (buffer.assignParameter(creatorProp, _deserializeWithErrorWrapping(p, ctxt, creatorProp))) {
/*   0*/              Object bean;
/* 939*/              t = p.nextToken();
/*   0*/              try {
/* 942*/                bean = creator.build(ctxt, buffer);
/* 943*/              } catch (Exception e) {
/* 944*/                wrapAndThrow(e, this._beanType.getRawClass(), propName, ctxt);
/*   0*/              } 
/* 948*/              while (t == JsonToken.FIELD_NAME) {
/* 949*/                p.nextToken();
/* 950*/                tokens.copyCurrentStructure(p);
/* 951*/                t = p.nextToken();
/*   0*/              } 
/* 953*/              if (bean.getClass() != this._beanType.getRawClass()) {
/* 956*/                  return ctxt.reportBadDefinition(this._beanType, String.format("Cannot create polymorphic instances with external type ids (%s -> %s)", new Object[] { this._beanType, bean.getClass() })); 
/*   0*/                 }
/* 960*/              return ext.complete(p, ctxt, bean);
/*   0*/            }  
/*   0*/           }
/* 966*/      } else if (!buffer.readIdProperty(propName)) {
/* 970*/        SettableBeanProperty prop = this._beanProperties.find(propName);
/* 971*/        if (prop != null) {
/* 972*/          buffer.bufferProperty(prop, prop.deserialize(p, ctxt));
/* 976*/        } else if (!ext.handlePropertyValue(p, ctxt, propName, null)) {
/* 980*/          if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/* 981*/            handleIgnoredProperty(p, ctxt, handledType(), propName);
/* 985*/          } else if (this._anySetter != null) {
/* 986*/            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(p, ctxt));
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 990*/    tokens.writeEndObject();
/*   0*/    try {
/* 994*/      return ext.complete(p, ctxt, buffer, creator);
/* 995*/    } catch (Exception e) {
/* 996*/      return wrapInstantiationProblem(e, ctxt);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Exception _creatorReturnedNullException() {
/*1007*/    if (this._nullFromCreator == null) {
/*1008*/        this._nullFromCreator = new NullPointerException("JSON Creator returned null"); 
/*   0*/       }
/*1010*/    return this._nullFromCreator;
/*   0*/  }
/*   0*/  
/*   0*/  static class BeanReferring extends ReadableObjectId.Referring {
/*   0*/    private final DeserializationContext _context;
/*   0*/    
/*   0*/    private final SettableBeanProperty _prop;
/*   0*/    
/*   0*/    private Object _bean;
/*   0*/    
/*   0*/    BeanReferring(DeserializationContext ctxt, UnresolvedForwardReference ref, JavaType valueType, PropertyValueBuffer buffer, SettableBeanProperty prop) {
/*1025*/      super(ref, valueType);
/*1026*/      this._context = ctxt;
/*1027*/      this._prop = prop;
/*   0*/    }
/*   0*/    
/*   0*/    public void setBean(Object bean) {
/*1031*/      this._bean = bean;
/*   0*/    }
/*   0*/    
/*   0*/    public void handleResolvedForwardReference(Object id, Object value) throws IOException {
/*1037*/      if (this._bean == null) {
/*1038*/          this._context.reportInputMismatch(this._prop, "Cannot resolve ObjectId forward reference using property '%s' (of type %s): Bean not yet resolved", new Object[] { this._prop.getName(), this._prop.getDeclaringClass().getName() }); 
/*   0*/         }
/*1042*/      this._prop.set(this._bean, value);
/*   0*/    }
/*   0*/  }
/*   0*/}
