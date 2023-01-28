/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdGenerators;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.DeserializationFeature;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.PropertyMetadata;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.BeanPropertyMap;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ExternalTypeHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.InnerClassProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ManagedReferenceProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReferenceProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdValueProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.TypeWrappedDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.UnwrappedPropertyHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ValueInjector;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdDelegatingDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
/*   0*/import com.fasterxml.jackson.databind.exc.IgnoredPropertyException;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
/*   0*/import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.type.ClassKey;
/*   0*/import com.fasterxml.jackson.databind.util.Annotations;
/*   0*/import com.fasterxml.jackson.databind.util.ArrayBuilders;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.Converter;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.IOException;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/
/*   0*/public abstract class BeanDeserializerBase extends StdDeserializer<Object> implements ContextualDeserializer, ResolvableDeserializer, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  34*/  protected static final PropertyName TEMP_PROPERTY_NAME = new PropertyName("#temporary-name");
/*   0*/  
/*   0*/  private final transient Annotations _classAnnotations;
/*   0*/  
/*   0*/  protected final JavaType _beanType;
/*   0*/  
/*   0*/  protected final JsonFormat.Shape _serializationShape;
/*   0*/  
/*   0*/  protected final ValueInstantiator _valueInstantiator;
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _delegateDeserializer;
/*   0*/  
/*   0*/  protected PropertyBasedCreator _propertyBasedCreator;
/*   0*/  
/*   0*/  protected boolean _nonStandardCreation;
/*   0*/  
/*   0*/  protected boolean _vanillaProcessing;
/*   0*/  
/*   0*/  protected final BeanPropertyMap _beanProperties;
/*   0*/  
/*   0*/  protected final ValueInjector[] _injectables;
/*   0*/  
/*   0*/  protected SettableAnyProperty _anySetter;
/*   0*/  
/*   0*/  protected final HashSet<String> _ignorableProps;
/*   0*/  
/*   0*/  protected final boolean _ignoreAllUnknown;
/*   0*/  
/*   0*/  protected final boolean _needViewProcesing;
/*   0*/  
/*   0*/  protected final Map<String, SettableBeanProperty> _backRefs;
/*   0*/  
/*   0*/  protected transient HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
/*   0*/  
/*   0*/  protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
/*   0*/  
/*   0*/  protected ExternalTypeHandler _externalTypeIdHandler;
/*   0*/  
/*   0*/  protected final ObjectIdReader _objectIdReader;
/*   0*/  
/*   0*/  protected BeanDeserializerBase(BeanDeserializerBuilder builder, BeanDescription beanDesc, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, boolean hasViews) {
/* 205*/    super(beanDesc.getType());
/* 207*/    AnnotatedClass ac = beanDesc.getClassInfo();
/* 208*/    this._classAnnotations = ac.getAnnotations();
/* 209*/    this._beanType = beanDesc.getType();
/* 210*/    this._valueInstantiator = builder.getValueInstantiator();
/* 212*/    this._beanProperties = properties;
/* 213*/    this._backRefs = backRefs;
/* 214*/    this._ignorableProps = ignorableProps;
/* 215*/    this._ignoreAllUnknown = ignoreAllUnknown;
/* 217*/    this._anySetter = builder.getAnySetter();
/* 218*/    List<ValueInjector> injectables = builder.getInjectables();
/* 219*/    this._injectables = (injectables == null || injectables.isEmpty()) ? null : injectables.<ValueInjector>toArray(new ValueInjector[injectables.size()]);
/* 221*/    this._objectIdReader = builder.getObjectIdReader();
/* 222*/    this._nonStandardCreation = (this._unwrappedPropertyHandler != null || this._valueInstantiator.canCreateUsingDelegate() || this._valueInstantiator.canCreateFromObjectWith() || !this._valueInstantiator.canCreateUsingDefault());
/* 229*/    JsonFormat.Value format = beanDesc.findExpectedFormat(null);
/* 230*/    this._serializationShape = (format == null) ? null : format.getShape();
/* 232*/    this._needViewProcesing = hasViews;
/* 233*/    this._vanillaProcessing = (!this._nonStandardCreation && this._injectables == null && !this._needViewProcesing && this._objectIdReader == null);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase(BeanDeserializerBase src) {
/* 242*/    this(src, src._ignoreAllUnknown);
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase(BeanDeserializerBase src, boolean ignoreAllUnknown) {
/* 247*/    super(src._beanType);
/* 249*/    this._classAnnotations = src._classAnnotations;
/* 250*/    this._beanType = src._beanType;
/* 252*/    this._valueInstantiator = src._valueInstantiator;
/* 253*/    this._delegateDeserializer = src._delegateDeserializer;
/* 254*/    this._propertyBasedCreator = src._propertyBasedCreator;
/* 256*/    this._beanProperties = src._beanProperties;
/* 257*/    this._backRefs = src._backRefs;
/* 258*/    this._ignorableProps = src._ignorableProps;
/* 259*/    this._ignoreAllUnknown = ignoreAllUnknown;
/* 260*/    this._anySetter = src._anySetter;
/* 261*/    this._injectables = src._injectables;
/* 262*/    this._objectIdReader = src._objectIdReader;
/* 264*/    this._nonStandardCreation = src._nonStandardCreation;
/* 265*/    this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/* 266*/    this._needViewProcesing = src._needViewProcesing;
/* 267*/    this._serializationShape = src._serializationShape;
/* 269*/    this._vanillaProcessing = src._vanillaProcessing;
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBase(BeanDeserializerBase src, NameTransformer unwrapper) {
/* 274*/    super(src._beanType);
/* 276*/    this._classAnnotations = src._classAnnotations;
/* 277*/    this._beanType = src._beanType;
/* 279*/    this._valueInstantiator = src._valueInstantiator;
/* 280*/    this._delegateDeserializer = src._delegateDeserializer;
/* 281*/    this._propertyBasedCreator = src._propertyBasedCreator;
/* 283*/    this._backRefs = src._backRefs;
/* 284*/    this._ignorableProps = src._ignorableProps;
/* 285*/    this._ignoreAllUnknown = (unwrapper != null || src._ignoreAllUnknown);
/* 286*/    this._anySetter = src._anySetter;
/* 287*/    this._injectables = src._injectables;
/* 288*/    this._objectIdReader = src._objectIdReader;
/* 290*/    this._nonStandardCreation = src._nonStandardCreation;
/* 291*/    UnwrappedPropertyHandler uph = src._unwrappedPropertyHandler;
/* 293*/    if (unwrapper != null) {
/* 295*/      if (uph != null) {
/* 296*/          uph = uph.renameAll(unwrapper); 
/*   0*/         }
/* 299*/      this._beanProperties = src._beanProperties.renameAll(unwrapper);
/*   0*/    } else {
/* 301*/      this._beanProperties = src._beanProperties;
/*   0*/    } 
/* 303*/    this._unwrappedPropertyHandler = uph;
/* 304*/    this._needViewProcesing = src._needViewProcesing;
/* 305*/    this._serializationShape = src._serializationShape;
/* 308*/    this._vanillaProcessing = false;
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase(BeanDeserializerBase src, ObjectIdReader oir) {
/* 313*/    super(src._beanType);
/* 315*/    this._classAnnotations = src._classAnnotations;
/* 316*/    this._beanType = src._beanType;
/* 318*/    this._valueInstantiator = src._valueInstantiator;
/* 319*/    this._delegateDeserializer = src._delegateDeserializer;
/* 320*/    this._propertyBasedCreator = src._propertyBasedCreator;
/* 322*/    this._backRefs = src._backRefs;
/* 323*/    this._ignorableProps = src._ignorableProps;
/* 324*/    this._ignoreAllUnknown = src._ignoreAllUnknown;
/* 325*/    this._anySetter = src._anySetter;
/* 326*/    this._injectables = src._injectables;
/* 328*/    this._nonStandardCreation = src._nonStandardCreation;
/* 329*/    this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/* 330*/    this._needViewProcesing = src._needViewProcesing;
/* 331*/    this._serializationShape = src._serializationShape;
/* 334*/    this._objectIdReader = oir;
/* 336*/    if (oir == null) {
/* 337*/      this._beanProperties = src._beanProperties;
/* 338*/      this._vanillaProcessing = src._vanillaProcessing;
/*   0*/    } else {
/* 344*/      ObjectIdValueProperty idProp = new ObjectIdValueProperty(oir, PropertyMetadata.STD_REQUIRED);
/* 345*/      this._beanProperties = src._beanProperties.withProperty(idProp);
/* 346*/      this._vanillaProcessing = false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDeserializerBase(BeanDeserializerBase src, HashSet<String> ignorableProps) {
/* 352*/    super(src._beanType);
/* 354*/    this._classAnnotations = src._classAnnotations;
/* 355*/    this._beanType = src._beanType;
/* 357*/    this._valueInstantiator = src._valueInstantiator;
/* 358*/    this._delegateDeserializer = src._delegateDeserializer;
/* 359*/    this._propertyBasedCreator = src._propertyBasedCreator;
/* 361*/    this._backRefs = src._backRefs;
/* 362*/    this._ignorableProps = ignorableProps;
/* 363*/    this._ignoreAllUnknown = src._ignoreAllUnknown;
/* 364*/    this._anySetter = src._anySetter;
/* 365*/    this._injectables = src._injectables;
/* 367*/    this._nonStandardCreation = src._nonStandardCreation;
/* 368*/    this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
/* 369*/    this._needViewProcesing = src._needViewProcesing;
/* 370*/    this._serializationShape = src._serializationShape;
/* 372*/    this._vanillaProcessing = src._vanillaProcessing;
/* 373*/    this._objectIdReader = src._objectIdReader;
/* 374*/    this._beanProperties = src._beanProperties;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract JsonDeserializer<Object> unwrappingDeserializer(NameTransformer paramNameTransformer);
/*   0*/  
/*   0*/  public abstract BeanDeserializerBase withObjectIdReader(ObjectIdReader paramObjectIdReader);
/*   0*/  
/*   0*/  public abstract BeanDeserializerBase withIgnorableProperties(HashSet<String> paramHashSet);
/*   0*/  
/*   0*/  protected abstract BeanDeserializerBase asArrayDeserializer();
/*   0*/  
/*   0*/  public void resolve(DeserializationContext ctxt) throws JsonMappingException {
/* 408*/    ExternalTypeHandler.Builder extTypes = null;
/* 410*/    if (this._valueInstantiator.canCreateFromObjectWith()) {
/* 411*/      SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
/* 412*/      this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps);
/* 414*/      for (SettableBeanProperty prop : this._propertyBasedCreator.properties()) {
/* 415*/        if (prop.hasValueTypeDeserializer()) {
/* 416*/          TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
/* 417*/          if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/* 418*/            if (extTypes == null) {
/* 419*/                extTypes = new ExternalTypeHandler.Builder(); 
/*   0*/               }
/* 421*/            extTypes.addExternal(prop, typeDeser);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 427*/    UnwrappedPropertyHandler unwrapped = null;
/* 429*/    for (SettableBeanProperty origProp : (Iterable<SettableBeanProperty>)this._beanProperties) {
/* 430*/      SettableBeanProperty prop = origProp;
/* 432*/      if (!prop.hasValueDeserializer()) {
/* 434*/        JsonDeserializer<?> deser = findConvertingDeserializer(ctxt, prop);
/* 435*/        if (deser == null) {
/* 436*/            deser = findDeserializer(ctxt, prop.getType(), prop); 
/*   0*/           }
/* 438*/        prop = prop.withValueDeserializer(deser);
/*   0*/      } else {
/* 440*/        JsonDeserializer<Object> deser = prop.getValueDeserializer();
/* 444*/        JsonDeserializer<?> cd = ctxt.handlePrimaryContextualization(deser, prop, prop.getType());
/* 446*/        if (cd != deser) {
/* 447*/            prop = prop.withValueDeserializer(cd); 
/*   0*/           }
/*   0*/      } 
/* 451*/      prop = _resolveManagedReferenceProperty(ctxt, prop);
/* 454*/      if (!(prop instanceof ManagedReferenceProperty)) {
/* 455*/          prop = _resolvedObjectIdProperty(ctxt, prop); 
/*   0*/         }
/* 458*/      SettableBeanProperty u = _resolveUnwrappedProperty(ctxt, prop);
/* 459*/      if (u != null) {
/* 460*/        prop = u;
/* 461*/        if (unwrapped == null) {
/* 462*/            unwrapped = new UnwrappedPropertyHandler(); 
/*   0*/           }
/* 464*/        unwrapped.addProperty(prop);
/* 468*/        this._beanProperties.remove(prop);
/*   0*/        continue;
/*   0*/      } 
/* 472*/      prop = _resolveInnerClassValuedProperty(ctxt, prop);
/* 473*/      if (prop != origProp) {
/* 474*/          this._beanProperties.replace(prop); 
/*   0*/         }
/* 480*/      if (prop.hasValueTypeDeserializer()) {
/* 481*/        TypeDeserializer typeDeser = prop.getValueTypeDeserializer();
/* 482*/        if (typeDeser.getTypeInclusion() == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
/* 483*/          if (extTypes == null) {
/* 484*/              extTypes = new ExternalTypeHandler.Builder(); 
/*   0*/             }
/* 486*/          extTypes.addExternal(prop, typeDeser);
/* 488*/          this._beanProperties.remove(prop);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/* 495*/    if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
/* 496*/        this._anySetter = this._anySetter.withValueDeserializer(findDeserializer(ctxt, this._anySetter.getType(), this._anySetter.getProperty())); 
/*   0*/       }
/* 501*/    if (this._valueInstantiator.canCreateUsingDelegate()) {
/* 502*/      JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
/* 503*/      if (delegateType == null) {
/* 504*/          throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._beanType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'"); 
/*   0*/         }
/* 508*/      AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
/* 510*/      BeanProperty.Std property = new BeanProperty.Std(TEMP_PROPERTY_NAME, delegateType, null, this._classAnnotations, delegateCreator, PropertyMetadata.STD_OPTIONAL);
/* 514*/      TypeDeserializer td = delegateType.<TypeDeserializer>getTypeHandler();
/* 515*/      if (td == null) {
/* 516*/          td = ctxt.getConfig().findTypeDeserializer(delegateType); 
/*   0*/         }
/* 518*/      JsonDeserializer<Object> dd = findDeserializer(ctxt, delegateType, property);
/* 519*/      if (td != null) {
/* 520*/        td = td.forProperty(property);
/* 521*/        dd = new TypeWrappedDeserializer(td, dd);
/*   0*/      } 
/* 523*/      this._delegateDeserializer = dd;
/*   0*/    } 
/* 526*/    if (extTypes != null) {
/* 527*/      this._externalTypeIdHandler = extTypes.build();
/* 529*/      this._nonStandardCreation = true;
/*   0*/    } 
/* 532*/    this._unwrappedPropertyHandler = unwrapped;
/* 533*/    if (unwrapped != null) {
/* 534*/        this._nonStandardCreation = true; 
/*   0*/       }
/* 538*/    this._vanillaProcessing = (this._vanillaProcessing && !this._nonStandardCreation);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> findConvertingDeserializer(DeserializationContext ctxt, SettableBeanProperty prop) throws JsonMappingException {
/* 552*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 553*/    if (intr != null) {
/* 554*/      Object convDef = intr.findDeserializationConverter(prop.getMember());
/* 555*/      if (convDef != null) {
/* 556*/        Converter<Object, Object> conv = ctxt.converterInstance(prop.getMember(), convDef);
/* 557*/        JavaType delegateType = conv.getInputType(ctxt.getTypeFactory());
/* 558*/        JsonDeserializer<?> ser = ctxt.findContextualValueDeserializer(delegateType, prop);
/* 559*/        return new StdDelegatingDeserializer(conv, delegateType, ser);
/*   0*/      } 
/*   0*/    } 
/* 562*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
/* 576*/    ObjectIdReader oir = this._objectIdReader;
/* 579*/    AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
/* 580*/    AnnotatedMember accessor = (property == null || intr == null) ? null : property.getMember();
/* 582*/    if (accessor != null && intr != null) {
/* 583*/      ObjectIdInfo objectIdInfo = intr.findObjectIdInfo(accessor);
/* 584*/      if (objectIdInfo != null) {
/*   0*/        JavaType idType;
/*   0*/        SettableBeanProperty idProp;
/*   0*/        ObjectIdGenerator<?> idGen;
/* 586*/        objectIdInfo = intr.findObjectReferenceInfo(accessor, objectIdInfo);
/* 588*/        Class<?> implClass = objectIdInfo.getGeneratorType();
/* 593*/        ObjectIdResolver resolver = ctxt.objectIdResolverInstance(accessor, objectIdInfo);
/* 594*/        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 595*/          PropertyName propName = objectIdInfo.getPropertyName();
/* 596*/          idProp = findProperty(propName);
/* 597*/          if (idProp == null) {
/* 598*/              throw new IllegalArgumentException("Invalid Object Id definition for " + handledType().getName() + ": can not find property with name '" + propName + "'"); 
/*   0*/             }
/* 601*/          idType = idProp.getType();
/* 602*/          PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*   0*/        } else {
/* 604*/          JavaType type = ctxt.constructType(implClass);
/* 605*/          idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 606*/          idProp = null;
/* 607*/          idGen = ctxt.objectIdGeneratorInstance(accessor, objectIdInfo);
/*   0*/        } 
/* 609*/        JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/* 610*/        oir = ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), idGen, deser, idProp, resolver);
/*   0*/      } 
/*   0*/    } 
/* 615*/    BeanDeserializerBase contextual = this;
/* 616*/    if (oir != null && oir != this._objectIdReader) {
/* 617*/        contextual = contextual.withObjectIdReader(oir); 
/*   0*/       }
/* 620*/    if (accessor != null) {
/* 621*/      String[] ignorals = intr.findPropertiesToIgnore(accessor);
/* 622*/      if (ignorals != null && ignorals.length != 0) {
/* 623*/        HashSet<String> newIgnored = ArrayBuilders.setAndArray(contextual._ignorableProps, ignorals);
/* 624*/        contextual = contextual.withIgnorableProperties(newIgnored);
/*   0*/      } 
/*   0*/    } 
/* 629*/    JsonFormat.Shape shape = null;
/* 630*/    if (accessor != null) {
/* 631*/      JsonFormat.Value format = intr.findFormat(accessor);
/* 633*/      if (format != null) {
/* 634*/          shape = format.getShape(); 
/*   0*/         }
/*   0*/    } 
/* 637*/    if (shape == null) {
/* 638*/        shape = this._serializationShape; 
/*   0*/       }
/* 640*/    if (shape == JsonFormat.Shape.ARRAY) {
/* 641*/        contextual = contextual.asArrayDeserializer(); 
/*   0*/       }
/* 643*/    return contextual;
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
/* 653*/    String refName = prop.getManagedReferenceName();
/* 654*/    if (refName == null) {
/* 655*/        return prop; 
/*   0*/       }
/* 657*/    JsonDeserializer<?> valueDeser = prop.getValueDeserializer();
/* 658*/    SettableBeanProperty backProp = valueDeser.findBackReference(refName);
/* 659*/    if (backProp == null) {
/* 660*/        throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': no back reference property found from type " + prop.getType()); 
/*   0*/       }
/* 664*/    JavaType referredType = this._beanType;
/* 665*/    JavaType backRefType = backProp.getType();
/* 666*/    boolean isContainer = prop.getType().isContainerType();
/* 667*/    if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
/* 668*/        throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': back reference type (" + backRefType.getRawClass().getName() + ") not compatible with managed type (" + referredType.getRawClass().getName() + ")"); 
/*   0*/       }
/* 672*/    return new ManagedReferenceProperty(prop, refName, backProp, this._classAnnotations, isContainer);
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty _resolvedObjectIdProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
/* 682*/    ObjectIdInfo objectIdInfo = prop.getObjectIdInfo();
/* 683*/    JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
/* 684*/    ObjectIdReader objectIdReader = valueDeser.getObjectIdReader();
/* 685*/    if (objectIdInfo == null && objectIdReader == null) {
/* 686*/        return prop; 
/*   0*/       }
/* 689*/    return new ObjectIdReferenceProperty(prop, objectIdInfo);
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty _resolveUnwrappedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
/* 699*/    AnnotatedMember am = prop.getMember();
/* 700*/    if (am != null) {
/* 701*/      NameTransformer unwrapper = ctxt.getAnnotationIntrospector().findUnwrappingNameTransformer(am);
/* 702*/      if (unwrapper != null) {
/* 703*/        JsonDeserializer<Object> orig = prop.getValueDeserializer();
/* 704*/        JsonDeserializer<Object> unwrapping = orig.unwrappingDeserializer(unwrapper);
/* 705*/        if (unwrapping != orig && unwrapping != null) {
/* 707*/            return prop.withValueDeserializer(unwrapping); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 711*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationContext ctxt, SettableBeanProperty prop) {
/* 724*/    JsonDeserializer<Object> deser = prop.getValueDeserializer();
/* 726*/    if (deser instanceof BeanDeserializerBase) {
/* 727*/      BeanDeserializerBase bd = (BeanDeserializerBase)deser;
/* 728*/      ValueInstantiator vi = bd.getValueInstantiator();
/* 729*/      if (!vi.canCreateUsingDefault()) {
/* 730*/        Class<?> valueClass = prop.getType().getRawClass();
/* 731*/        Class<?> enclosing = ClassUtil.getOuterClass(valueClass);
/* 733*/        if (enclosing != null && enclosing == this._beanType.getRawClass()) {
/* 734*/            for (Constructor<?> ctor : valueClass.getConstructors()) {
/* 735*/              Class<?>[] paramTypes = ctor.getParameterTypes();
/* 736*/              if (paramTypes.length == 1 && paramTypes[0] == enclosing) {
/* 737*/                if (ctxt.getConfig().canOverrideAccessModifiers()) {
/* 738*/                    ClassUtil.checkAndFixAccess(ctor); 
/*   0*/                   }
/* 740*/                return new InnerClassProperty(prop, ctor);
/*   0*/              } 
/*   0*/            }  
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 746*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isCachable() {
/* 756*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> handledType() {
/* 760*/    return this._beanType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectIdReader getObjectIdReader() {
/* 770*/    return this._objectIdReader;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasProperty(String propertyName) {
/* 774*/    return (this._beanProperties.find(propertyName) != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasViews() {
/* 778*/    return this._needViewProcesing;
/*   0*/  }
/*   0*/  
/*   0*/  public int getPropertyCount() {
/* 785*/    return this._beanProperties.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<Object> getKnownPropertyNames() {
/* 790*/    ArrayList<Object> names = new ArrayList();
/* 791*/    for (SettableBeanProperty prop : (Iterable<SettableBeanProperty>)this._beanProperties) {
/* 792*/        names.add(prop.getName()); 
/*   0*/       }
/* 794*/    return names;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public final Class<?> getBeanClass() {
/* 801*/    return this._beanType.getRawClass();
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getValueType() {
/* 804*/    return this._beanType;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<SettableBeanProperty> properties() {
/* 815*/    if (this._beanProperties == null) {
/* 816*/        throw new IllegalStateException("Can only call after BeanDeserializer has been resolved"); 
/*   0*/       }
/* 818*/    return this._beanProperties.iterator();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterator<SettableBeanProperty> creatorProperties() {
/* 830*/    if (this._propertyBasedCreator == null) {
/* 831*/        return Collections.<SettableBeanProperty>emptyList().iterator(); 
/*   0*/       }
/* 833*/    return this._propertyBasedCreator.properties().iterator();
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty findProperty(PropertyName propertyName) {
/* 839*/    return findProperty(propertyName.getSimpleName());
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty findProperty(String propertyName) {
/* 851*/    SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyName);
/* 853*/    if (prop == null && this._propertyBasedCreator != null) {
/* 854*/        prop = this._propertyBasedCreator.findCreatorProperty(propertyName); 
/*   0*/       }
/* 856*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty findProperty(int propertyIndex) {
/* 871*/    SettableBeanProperty prop = (this._beanProperties == null) ? null : this._beanProperties.find(propertyIndex);
/* 873*/    if (prop == null && this._propertyBasedCreator != null) {
/* 874*/        prop = this._propertyBasedCreator.findCreatorProperty(propertyIndex); 
/*   0*/       }
/* 876*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  public SettableBeanProperty findBackReference(String logicalName) {
/* 886*/    if (this._backRefs == null) {
/* 887*/        return null; 
/*   0*/       }
/* 889*/    return this._backRefs.get(logicalName);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueInstantiator getValueInstantiator() {
/* 893*/    return this._valueInstantiator;
/*   0*/  }
/*   0*/  
/*   0*/  public void replaceProperty(SettableBeanProperty original, SettableBeanProperty replacement) {
/* 917*/    this._beanProperties.replace(replacement);
/*   0*/  }
/*   0*/  
/*   0*/  public abstract Object deserializeFromObject(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException;
/*   0*/  
/*   0*/  public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
/* 939*/    if (this._objectIdReader != null) {
/* 941*/      if (p.canReadObjectId()) {
/* 942*/        Object id = p.getObjectId();
/* 943*/        if (id != null) {
/* 944*/          Object ob = typeDeserializer.deserializeTypedFromObject(p, ctxt);
/* 945*/          return _handleTypedObjectId(p, ctxt, ob, id);
/*   0*/        } 
/*   0*/      } 
/* 949*/      JsonToken t = p.getCurrentToken();
/* 950*/      if (t != null) {
/* 952*/        if (t.isScalarValue()) {
/* 953*/            return deserializeFromObjectId(p, ctxt); 
/*   0*/           }
/* 956*/        if (t == JsonToken.START_ARRAY) {
/* 957*/            t = p.nextToken(); 
/*   0*/           }
/* 959*/        if (t == JsonToken.FIELD_NAME && this._objectIdReader.maySerializeAsObject() && this._objectIdReader.isValidReferencePropertyName(p.getCurrentName(), p)) {
/* 961*/            return deserializeFromObjectId(p, ctxt); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/* 966*/    return typeDeserializer.deserializeTypedFromObject(p, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _handleTypedObjectId(JsonParser jp, DeserializationContext ctxt, Object pojo, Object rawId) throws IOException {
/*   0*/    Object id;
/* 983*/    JsonDeserializer<Object> idDeser = this._objectIdReader.getDeserializer();
/* 987*/    if (idDeser.handledType() == rawId.getClass()) {
/* 989*/      id = rawId;
/*   0*/    } else {
/* 991*/      id = _convertObjectId(jp, ctxt, rawId, idDeser);
/*   0*/    } 
/* 994*/    ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/* 995*/    roid.bindItem(pojo);
/* 997*/    SettableBeanProperty idProp = this._objectIdReader.idProperty;
/* 998*/    if (idProp != null) {
/* 999*/        return idProp.setAndReturn(pojo, id); 
/*   0*/       }
/*1001*/    return pojo;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _convertObjectId(JsonParser jp, DeserializationContext ctxt, Object rawId, JsonDeserializer<Object> idDeser) throws IOException {
/*1017*/    TokenBuffer buf = new TokenBuffer(jp);
/*1018*/    if (rawId instanceof String) {
/*1019*/      buf.writeString((String)rawId);
/*1020*/    } else if (rawId instanceof Long) {
/*1021*/      buf.writeNumber((Long)rawId);
/*1022*/    } else if (rawId instanceof Integer) {
/*1023*/      buf.writeNumber((Integer)rawId);
/*   0*/    } else {
/*1028*/      buf.writeObject(rawId);
/*   0*/    } 
/*1030*/    JsonParser bufParser = buf.asParser();
/*1031*/    bufParser.nextToken();
/*1032*/    return idDeser.deserialize(bufParser, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeWithObjectId(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*1045*/    return deserializeFromObject(jp, ctxt);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeFromObjectId(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*1054*/    Object id = this._objectIdReader.readObjectReference(jp, ctxt);
/*1055*/    ReadableObjectId roid = ctxt.findObjectId(id, this._objectIdReader.generator, this._objectIdReader.resolver);
/*1057*/    Object pojo = roid.resolve();
/*1058*/    if (pojo == null) {
/*1059*/        throw new UnresolvedForwardReference("Could not resolve Object Id [" + id + "] (for " + this._beanType + ").", jp.getCurrentLocation(), roid); 
/*   0*/       }
/*1062*/    return pojo;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object deserializeFromObjectUsingNonDefault(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*1068*/    if (this._delegateDeserializer != null) {
/*1069*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt)); 
/*   0*/       }
/*1072*/    if (this._propertyBasedCreator != null) {
/*1073*/        return _deserializeUsingPropertyBased(jp, ctxt); 
/*   0*/       }
/*1076*/    if (this._beanType.isAbstract()) {
/*1077*/        throw JsonMappingException.from(jp, "Can not instantiate abstract type " + this._beanType + " (need to add/enable type information?)"); 
/*   0*/       }
/*1080*/    throw JsonMappingException.from(jp, "No suitable constructor found for type " + this._beanType + ": can not instantiate from JSON object (missing default constructor or creator, or perhaps need to add/enable type information?)");
/*   0*/  }
/*   0*/  
/*   0*/  protected abstract Object _deserializeUsingPropertyBased(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext) throws IOException, JsonProcessingException;
/*   0*/  
/*   0*/  public Object deserializeFromNumber(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*1092*/    if (this._objectIdReader != null) {
/*1093*/        return deserializeFromObjectId(jp, ctxt); 
/*   0*/       }
/*1096*/    switch (jp.getNumberType()) {
/*   0*/      case INT:
/*1098*/        if (this._delegateDeserializer != null && 
/*1099*/          !this._valueInstantiator.canCreateFromInt()) {
/*1100*/          Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
/*1101*/          if (this._injectables != null) {
/*1102*/              injectValues(ctxt, bean); 
/*   0*/             }
/*1104*/          return bean;
/*   0*/        } 
/*1107*/        return this._valueInstantiator.createFromInt(ctxt, jp.getIntValue());
/*   0*/      case LONG:
/*1109*/        if (this._delegateDeserializer != null && 
/*1110*/          !this._valueInstantiator.canCreateFromInt()) {
/*1111*/          Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
/*1112*/          if (this._injectables != null) {
/*1113*/              injectValues(ctxt, bean); 
/*   0*/             }
/*1115*/          return bean;
/*   0*/        } 
/*1118*/        return this._valueInstantiator.createFromLong(ctxt, jp.getLongValue());
/*   0*/    } 
/*1121*/    if (this._delegateDeserializer != null) {
/*1122*/      Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(jp, ctxt));
/*1123*/      if (this._injectables != null) {
/*1124*/          injectValues(ctxt, bean); 
/*   0*/         }
/*1126*/      return bean;
/*   0*/    } 
/*1128*/    throw ctxt.instantiationException(handledType(), "no suitable creator method found to deserialize from JSON integer number");
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromString(JsonParser p, DeserializationContext ctxt) throws IOException {
/*1134*/    if (this._objectIdReader != null) {
/*1135*/        return deserializeFromObjectId(p, ctxt); 
/*   0*/       }
/*1141*/    if (this._delegateDeserializer != null && 
/*1142*/      !this._valueInstantiator.canCreateFromString()) {
/*1143*/      Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*1144*/      if (this._injectables != null) {
/*1145*/          injectValues(ctxt, bean); 
/*   0*/         }
/*1147*/      return bean;
/*   0*/    } 
/*1150*/    return this._valueInstantiator.createFromString(ctxt, p.getText());
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromDouble(JsonParser p, DeserializationContext ctxt) throws IOException {
/*1160*/    switch (p.getNumberType()) {
/*   0*/      case FLOAT:
/*   0*/      case DOUBLE:
/*1163*/        if (this._delegateDeserializer != null && 
/*1164*/          !this._valueInstantiator.canCreateFromDouble()) {
/*1165*/          Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*1166*/          if (this._injectables != null) {
/*1167*/              injectValues(ctxt, bean); 
/*   0*/             }
/*1169*/          return bean;
/*   0*/        } 
/*1172*/        return this._valueInstantiator.createFromDouble(ctxt, p.getDoubleValue());
/*   0*/    } 
/*1175*/    if (this._delegateDeserializer != null) {
/*1176*/        return this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt)); 
/*   0*/       }
/*1178*/    throw ctxt.instantiationException(handledType(), "no suitable creator method found to deserialize from JSON floating-point number");
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromBoolean(JsonParser p, DeserializationContext ctxt) throws IOException {
/*1186*/    if (this._delegateDeserializer != null && 
/*1187*/      !this._valueInstantiator.canCreateFromBoolean()) {
/*1188*/      Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*1189*/      if (this._injectables != null) {
/*1190*/          injectValues(ctxt, bean); 
/*   0*/         }
/*1192*/      return bean;
/*   0*/    } 
/*1195*/    boolean value = (p.getCurrentToken() == JsonToken.VALUE_TRUE);
/*1196*/    return this._valueInstantiator.createFromBoolean(ctxt, value);
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromArray(JsonParser p, DeserializationContext ctxt) throws IOException {
/*1201*/    if (this._delegateDeserializer != null) {
/*   0*/      try {
/*1203*/        Object bean = this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
/*1204*/        if (this._injectables != null) {
/*1205*/            injectValues(ctxt, bean); 
/*   0*/           }
/*1207*/        return bean;
/*1208*/      } catch (Exception e) {
/*1209*/        wrapInstantiationProblem(e, ctxt);
/*   0*/      } 
/*   0*/    } else {
/*1211*/      if (ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
/*1212*/        JsonToken t = p.nextToken();
/*1213*/        if (t == JsonToken.END_ARRAY && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/*1214*/            return null; 
/*   0*/           }
/*1216*/        Object value = deserialize(p, ctxt);
/*1217*/        if (p.nextToken() != JsonToken.END_ARRAY) {
/*1218*/            throw ctxt.wrongTokenException(p, JsonToken.END_ARRAY, "Attempted to unwrap single value array for single '" + this._valueClass.getName() + "' value but there was more than a single value in the array"); 
/*   0*/           }
/*1221*/        return value;
/*   0*/      } 
/*1222*/      if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)) {
/*1223*/        JsonToken t = p.nextToken();
/*1224*/        if (t == JsonToken.END_ARRAY) {
/*1225*/            return null; 
/*   0*/           }
/*1227*/        throw ctxt.mappingException(handledType(), JsonToken.START_ARRAY);
/*   0*/      } 
/*   0*/    } 
/*1229*/    throw ctxt.mappingException(handledType());
/*   0*/  }
/*   0*/  
/*   0*/  public Object deserializeFromEmbedded(JsonParser jp, DeserializationContext ctxt) throws IOException {
/*1236*/    if (this._objectIdReader != null) {
/*1237*/        return deserializeFromObjectId(jp, ctxt); 
/*   0*/       }
/*1242*/    return jp.getEmbeddedObject();
/*   0*/  }
/*   0*/  
/*   0*/  protected void injectValues(DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
/*1254*/    for (ValueInjector injector : this._injectables) {
/*1255*/        injector.inject(ctxt, bean); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
/*1270*/    unknownTokens.writeEndObject();
/*1273*/    JsonParser bufferParser = unknownTokens.asParser();
/*1274*/    while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
/*1275*/      String propName = bufferParser.getCurrentName();
/*1277*/      bufferParser.nextToken();
/*1278*/      handleUnknownProperty(bufferParser, ctxt, bean, propName);
/*   0*/    } 
/*1280*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleUnknownVanilla(JsonParser jp, DeserializationContext ctxt, Object bean, String propName) throws IOException, JsonProcessingException {
/*1291*/    if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/*1292*/      handleIgnoredProperty(jp, ctxt, bean, propName);
/*1293*/    } else if (this._anySetter != null) {
/*   0*/      try {
/*1296*/        this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
/*1297*/      } catch (Exception e) {
/*1298*/        wrapAndThrow(e, bean, propName, ctxt);
/*   0*/      } 
/*   0*/    } else {
/*1302*/      handleUnknownProperty(jp, ctxt, bean, propName);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleUnknownProperty(JsonParser jp, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException, JsonProcessingException {
/*1315*/    if (this._ignoreAllUnknown) {
/*1316*/      jp.skipChildren();
/*   0*/      return;
/*   0*/    } 
/*1319*/    if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
/*1320*/        handleIgnoredProperty(jp, ctxt, beanOrClass, propName); 
/*   0*/       }
/*1324*/    super.handleUnknownProperty(jp, ctxt, beanOrClass, propName);
/*   0*/  }
/*   0*/  
/*   0*/  protected void handleIgnoredProperty(JsonParser jp, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException, JsonProcessingException {
/*1337*/    if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)) {
/*1338*/        throw IgnoredPropertyException.from(jp, beanOrClass, propName, getKnownPropertyNames()); 
/*   0*/       }
/*1340*/    jp.skipChildren();
/*   0*/  }
/*   0*/  
/*   0*/  protected Object handlePolymorphic(JsonParser jp, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
/*1360*/    JsonDeserializer<Object> subDeser = _findSubclassDeserializer(ctxt, bean, unknownTokens);
/*1361*/    if (subDeser != null) {
/*1362*/      if (unknownTokens != null) {
/*1364*/        unknownTokens.writeEndObject();
/*1365*/        JsonParser p2 = unknownTokens.asParser();
/*1366*/        p2.nextToken();
/*1367*/        bean = subDeser.deserialize(p2, ctxt, bean);
/*   0*/      } 
/*1370*/      if (jp != null) {
/*1371*/          bean = subDeser.deserialize(jp, ctxt, bean); 
/*   0*/         }
/*1373*/      return bean;
/*   0*/    } 
/*1376*/    if (unknownTokens != null) {
/*1377*/        bean = handleUnknownProperties(ctxt, bean, unknownTokens); 
/*   0*/       }
/*1380*/    if (jp != null) {
/*1381*/        bean = deserialize(jp, ctxt, bean); 
/*   0*/       }
/*1383*/    return bean;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
/*1397*/    synchronized (this) {
/*1398*/      subDeser = (this._subDeserializers == null) ? null : this._subDeserializers.get(new ClassKey(bean.getClass()));
/*   0*/    } 
/*1400*/    if (subDeser != null) {
/*1401*/        return subDeser; 
/*   0*/       }
/*1404*/    JavaType type = ctxt.constructType(bean.getClass());
/*1411*/    JsonDeserializer<Object> subDeser = ctxt.findRootValueDeserializer(type);
/*1413*/    if (subDeser != null) {
/*1414*/        synchronized (this) {
/*1415*/          if (this._subDeserializers == null) {
/*1416*/              this._subDeserializers = new HashMap<ClassKey, JsonDeserializer<Object>>(); 
/*   0*/             }
/*1418*/          this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
/*   0*/        }  
/*   0*/       }
/*1421*/    return subDeser;
/*   0*/  }
/*   0*/  
/*   0*/  public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
/*1446*/    throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, fieldName);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public void wrapAndThrow(Throwable t, Object bean, int index, DeserializationContext ctxt) throws IOException {
/*1452*/    throw JsonMappingException.wrapWithPath(throwOrReturnThrowable(t, ctxt), bean, index);
/*   0*/  }
/*   0*/  
/*   0*/  private Throwable throwOrReturnThrowable(Throwable t, DeserializationContext ctxt) throws IOException {
/*1462*/    while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/*1463*/        t = t.getCause(); 
/*   0*/       }
/*1466*/    if (t instanceof Error) {
/*1467*/        throw (Error)t; 
/*   0*/       }
/*1469*/    boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*1471*/    if (t instanceof IOException) {
/*1472*/      if (!wrap || !(t instanceof JsonProcessingException)) {
/*1473*/          throw (IOException)t; 
/*   0*/         }
/*1475*/    } else if (!wrap && 
/*1476*/      t instanceof RuntimeException) {
/*1477*/      throw (RuntimeException)t;
/*   0*/    } 
/*1480*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  protected void wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
/*1486*/    while (t instanceof java.lang.reflect.InvocationTargetException && t.getCause() != null) {
/*1487*/        t = t.getCause(); 
/*   0*/       }
/*1490*/    if (t instanceof Error) {
/*1491*/        throw (Error)t; 
/*   0*/       }
/*1493*/    boolean wrap = (ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS));
/*1494*/    if (t instanceof IOException) {
/*1496*/        throw (IOException)t; 
/*   0*/       }
/*1497*/    if (!wrap && 
/*1498*/      t instanceof RuntimeException) {
/*1499*/        throw (RuntimeException)t; 
/*   0*/       }
/*1502*/    throw ctxt.instantiationException(this._beanType.getRawClass(), t);
/*   0*/  }
/*   0*/}
