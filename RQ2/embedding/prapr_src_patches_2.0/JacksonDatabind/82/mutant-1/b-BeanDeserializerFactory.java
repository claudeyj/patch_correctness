/*   0*/package com.fasterxml.jackson.databind.deser;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdGenerator;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdGenerators;
/*   0*/import com.fasterxml.jackson.annotation.ObjectIdResolver;
/*   0*/import com.fasterxml.jackson.databind.AbstractTypeResolver;
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.BeanDescription;
/*   0*/import com.fasterxml.jackson.databind.BeanProperty;
/*   0*/import com.fasterxml.jackson.databind.DeserializationConfig;
/*   0*/import com.fasterxml.jackson.databind.DeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.JsonDeserializer;
/*   0*/import com.fasterxml.jackson.databind.JsonMappingException;
/*   0*/import com.fasterxml.jackson.databind.MapperFeature;
/*   0*/import com.fasterxml.jackson.databind.PropertyMetadata;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*   0*/import com.fasterxml.jackson.databind.cfg.ConfigOverride;
/*   0*/import com.fasterxml.jackson.databind.cfg.DeserializerFactoryConfig;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ErrorThrowingDeserializer;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.FieldProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.MethodProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.PropertyBasedObjectIdGenerator;
/*   0*/import com.fasterxml.jackson.databind.deser.impl.SetterlessProperty;
/*   0*/import com.fasterxml.jackson.databind.deser.std.ThrowableDeserializer;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedField;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
/*   0*/import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
/*   0*/import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import com.fasterxml.jackson.databind.util.SimpleBeanPropertyDefinition;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/public class BeanDeserializerFactory extends BasicDeserializerFactory implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  38*/  private static final Class<?>[] INIT_CAUSE_PARAMS = new Class<?>[] { Throwable.class };
/*   0*/  
/*  40*/  private static final Class<?>[] NO_VIEWS = new Class<?>[0];
/*   0*/  
/*   0*/  protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
/*   0*/  
/*   0*/  static {
/*  50*/    Set<String> s = new HashSet<>();
/*  53*/    s.add("org.apache.commons.collections.functors.InvokerTransformer");
/*  54*/    s.add("org.apache.commons.collections.functors.InstantiateTransformer");
/*  55*/    s.add("org.apache.commons.collections4.functors.InvokerTransformer");
/*  56*/    s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
/*  57*/    s.add("org.codehaus.groovy.runtime.ConvertedClosure");
/*  58*/    s.add("org.codehaus.groovy.runtime.MethodClosure");
/*  59*/    s.add("org.springframework.beans.factory.ObjectFactory");
/*  60*/    s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
/*  61*/    s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
/*  62*/    DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
/*   0*/  }
/*   0*/  
/*  70*/  protected Set<String> _cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
/*   0*/  
/*  82*/  public static final BeanDeserializerFactory instance = new BeanDeserializerFactory(new DeserializerFactoryConfig());
/*   0*/  
/*   0*/  public BeanDeserializerFactory(DeserializerFactoryConfig config) {
/*  86*/    super(config);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializerFactory withConfig(DeserializerFactoryConfig config) {
/*  97*/    if (this._factoryConfig == config) {
/*  98*/        return this; 
/*   0*/       }
/* 106*/    if (getClass() != BeanDeserializerFactory.class) {
/* 107*/        throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with " + "additional deserializer definitions"); 
/*   0*/       }
/* 111*/    return new BeanDeserializerFactory(config);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> createBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 130*/    DeserializationConfig config = ctxt.getConfig();
/* 132*/    JsonDeserializer<Object> custom = _findCustomBeanDeserializer(type, config, beanDesc);
/* 133*/    if (custom != null) {
/* 134*/        return custom; 
/*   0*/       }
/* 140*/    if (type.isThrowable()) {
/* 141*/        return buildThrowableDeserializer(ctxt, type, beanDesc); 
/*   0*/       }
/* 148*/    if (type.isAbstract() && !type.isPrimitive() && !type.isEnumType()) {
/* 150*/      JavaType concreteType = materializeAbstractType(ctxt, type, beanDesc);
/* 151*/      if (concreteType != null) {
/* 155*/        beanDesc = config.introspect(concreteType);
/* 156*/        return buildBeanDeserializer(ctxt, concreteType, beanDesc);
/*   0*/      } 
/*   0*/    } 
/* 161*/    JsonDeserializer<Object> deser = (JsonDeserializer)findStdDeserializer(ctxt, type, beanDesc);
/* 162*/    if (deser != null) {
/* 163*/        return deser; 
/*   0*/       }
/* 167*/    if (!isPotentialBeanType(type.getRawClass())) {
/* 168*/        return null; 
/*   0*/       }
/* 171*/    checkIllegalTypes(ctxt, type, beanDesc);
/* 173*/    return buildBeanDeserializer(ctxt, type, beanDesc);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> createBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription beanDesc, Class<?> builderClass) throws JsonMappingException {
/* 183*/    JavaType builderType = ctxt.constructType(builderClass);
/* 184*/    BeanDescription builderDesc = ctxt.getConfig().introspectForBuilder(builderType);
/* 185*/    return buildBuilderBasedDeserializer(ctxt, valueType, builderDesc);
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<?> findStdDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 198*/    JsonDeserializer<?> deser = findDefaultDeserializer(ctxt, type, beanDesc);
/* 200*/    if (deser != null && 
/* 201*/      this._factoryConfig.hasDeserializerModifiers()) {
/* 202*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 203*/            deser = mod.modifyDeserializer(ctxt.getConfig(), beanDesc, deser); 
/*   0*/           } 
/*   0*/       }
/* 207*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected JavaType materializeAbstractType(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 215*/    for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
/* 216*/      JavaType concrete = r.resolveAbstractType(ctxt.getConfig(), beanDesc);
/* 217*/      if (concrete != null) {
/* 218*/          return concrete; 
/*   0*/         }
/*   0*/    } 
/* 221*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> buildBeanDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/*   0*/    ValueInstantiator valueInstantiator;
/*   0*/    JsonDeserializer<?> deserializer;
/*   0*/    try {
/* 251*/      valueInstantiator = findValueInstantiator(ctxt, beanDesc);
/* 252*/    } catch (NoClassDefFoundError error) {
/* 253*/      return new ErrorThrowingDeserializer(error);
/*   0*/    } 
/* 255*/    BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 256*/    builder.setValueInstantiator(valueInstantiator);
/* 258*/    addBeanProps(ctxt, beanDesc, builder);
/* 259*/    addObjectIdReader(ctxt, beanDesc, builder);
/* 262*/    addReferenceProperties(ctxt, beanDesc, builder);
/* 263*/    addInjectables(ctxt, beanDesc, builder);
/* 265*/    DeserializationConfig config = ctxt.getConfig();
/* 266*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 267*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 268*/            builder = mod.updateBuilder(config, beanDesc, builder); 
/*   0*/           } 
/*   0*/       }
/* 276*/    if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
/* 277*/      deserializer = builder.buildAbstract();
/*   0*/    } else {
/* 279*/      deserializer = builder.build();
/*   0*/    } 
/* 283*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 284*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 285*/            deserializer = mod.modifyDeserializer(config, beanDesc, deserializer); 
/*   0*/           } 
/*   0*/       }
/* 288*/    return (JsonDeserializer)deserializer;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> buildBuilderBasedDeserializer(DeserializationContext ctxt, JavaType valueType, BeanDescription builderDesc) throws JsonMappingException {
/* 304*/    ValueInstantiator valueInstantiator = findValueInstantiator(ctxt, builderDesc);
/* 305*/    DeserializationConfig config = ctxt.getConfig();
/* 306*/    BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, builderDesc);
/* 307*/    builder.setValueInstantiator(valueInstantiator);
/* 309*/    addBeanProps(ctxt, builderDesc, builder);
/* 310*/    addObjectIdReader(ctxt, builderDesc, builder);
/* 313*/    addReferenceProperties(ctxt, builderDesc, builder);
/* 314*/    addInjectables(ctxt, builderDesc, builder);
/* 316*/    JsonPOJOBuilder.Value builderConfig = builderDesc.findPOJOBuilderConfig();
/* 317*/    String buildMethodName = (builderConfig == null) ? "build" : builderConfig.buildMethodName;
/* 321*/    AnnotatedMethod buildMethod = builderDesc.findMethod(buildMethodName, null);
/* 322*/    if (buildMethod != null && 
/* 323*/      config.canOverrideAccessModifiers()) {
/* 324*/        ClassUtil.checkAndFixAccess(buildMethod.getMember(), config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS)); 
/*   0*/       }
/* 327*/    builder.setPOJOBuilder(buildMethod, builderConfig);
/* 329*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 330*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 331*/            builder = mod.updateBuilder(config, builderDesc, builder); 
/*   0*/           } 
/*   0*/       }
/* 334*/    JsonDeserializer<?> deserializer = builder.buildBuilderBased(valueType, buildMethodName);
/* 338*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 339*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 340*/            deserializer = mod.modifyDeserializer(config, builderDesc, deserializer); 
/*   0*/           } 
/*   0*/       }
/* 343*/    return (JsonDeserializer)deserializer;
/*   0*/  }
/*   0*/  
/*   0*/  protected void addObjectIdReader(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/*   0*/    JavaType idType;
/*   0*/    SettableBeanProperty idProp;
/*   0*/    ObjectIdGenerator<?> gen;
/* 350*/    ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
/* 351*/    if (objectIdInfo == null) {
/*   0*/        return; 
/*   0*/       }
/* 354*/    Class<?> implClass = objectIdInfo.getGeneratorType();
/* 359*/    ObjectIdResolver resolver = ctxt.objectIdResolverInstance(beanDesc.getClassInfo(), objectIdInfo);
/* 362*/    if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
/* 363*/      PropertyName propName = objectIdInfo.getPropertyName();
/* 364*/      idProp = builder.findProperty(propName);
/* 365*/      if (idProp == null) {
/* 366*/          throw new IllegalArgumentException("Invalid Object Id definition for " + beanDesc.getBeanClass().getName() + ": can not find property with name '" + propName + "'"); 
/*   0*/         }
/* 369*/      idType = idProp.getType();
/* 370*/      PropertyBasedObjectIdGenerator propertyBasedObjectIdGenerator = new PropertyBasedObjectIdGenerator(objectIdInfo.getScope());
/*   0*/    } else {
/* 372*/      JavaType type = ctxt.constructType(implClass);
/* 373*/      idType = ctxt.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
/* 374*/      idProp = null;
/* 375*/      gen = ctxt.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
/*   0*/    } 
/* 378*/    JsonDeserializer<?> deser = ctxt.findRootValueDeserializer(idType);
/* 379*/    builder.setObjectIdReader(ObjectIdReader.construct(idType, objectIdInfo.getPropertyName(), gen, deser, idProp, resolver));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 388*/    DeserializationConfig config = ctxt.getConfig();
/* 390*/    BeanDeserializerBuilder builder = constructBeanDeserializerBuilder(ctxt, beanDesc);
/* 391*/    builder.setValueInstantiator(findValueInstantiator(ctxt, beanDesc));
/* 393*/    addBeanProps(ctxt, beanDesc, builder);
/* 400*/    AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
/* 401*/    if (am != null) {
/* 402*/      SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), am, new PropertyName("cause"));
/* 404*/      SettableBeanProperty prop = constructSettableProperty(ctxt, beanDesc, propDef, am.getParameterType(0));
/* 406*/      if (prop != null) {
/* 411*/          builder.addOrReplaceProperty(prop, true); 
/*   0*/         }
/*   0*/    } 
/* 416*/    builder.addIgnorable("localizedMessage");
/* 418*/    builder.addIgnorable("suppressed");
/* 422*/    builder.addIgnorable("message");
/* 425*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 426*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 427*/            builder = mod.updateBuilder(config, beanDesc, builder); 
/*   0*/           } 
/*   0*/       }
/* 430*/    JsonDeserializer<?> deserializer = builder.build();
/* 435*/    if (deserializer instanceof BeanDeserializer) {
/* 436*/        deserializer = new ThrowableDeserializer((BeanDeserializer)deserializer); 
/*   0*/       }
/* 440*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 441*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 442*/            deserializer = mod.modifyDeserializer(config, beanDesc, deserializer); 
/*   0*/           } 
/*   0*/       }
/* 445*/    return (JsonDeserializer)deserializer;
/*   0*/  }
/*   0*/  
/*   0*/  protected BeanDeserializerBuilder constructBeanDeserializerBuilder(DeserializationContext ctxt, BeanDescription beanDesc) {
/* 462*/    return new BeanDeserializerBuilder(beanDesc, ctxt.getConfig());
/*   0*/  }
/*   0*/  
/*   0*/  protected void addBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/*   0*/    Set<String> ignored;
/* 476*/    boolean isConcrete = !beanDesc.getType().isAbstract();
/* 477*/    SettableBeanProperty[] creatorProps = isConcrete ? builder.getValueInstantiator().getFromObjectArguments(ctxt.getConfig()) : null;
/* 480*/    boolean hasCreatorProps = (creatorProps != null);
/* 486*/    JsonIgnoreProperties.Value ignorals = ctxt.getConfig().getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
/* 491*/    if (ignorals != null) {
/* 492*/      boolean ignoreAny = ignorals.getIgnoreUnknown();
/* 493*/      builder.setIgnoreUnknownProperties(ignoreAny);
/* 495*/      ignored = ignorals.findIgnoredForDeserialization();
/* 496*/      for (String propName : ignored) {
/* 497*/          builder.addIgnorable(propName); 
/*   0*/         }
/*   0*/    } else {
/* 500*/      ignored = Collections.emptySet();
/*   0*/    } 
/* 504*/    AnnotatedMethod anySetterMethod = beanDesc.findAnySetter();
/* 505*/    AnnotatedMember anySetterField = null;
/* 506*/    if (anySetterMethod != null) {
/* 507*/      builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterMethod));
/*   0*/    } else {
/* 510*/      anySetterField = beanDesc.findAnySetterField();
/* 511*/      if (anySetterField != null) {
/* 512*/          builder.setAnySetter(constructAnySetter(ctxt, beanDesc, anySetterField)); 
/*   0*/         }
/*   0*/    } 
/* 517*/    if (anySetterMethod == null && anySetterField == null) {
/* 518*/      Collection<String> ignored2 = beanDesc.getIgnoredPropertyNames();
/* 519*/      if (ignored2 != null) {
/* 520*/          for (String propName : ignored2) {
/* 523*/              builder.addIgnorable(propName); 
/*   0*/             } 
/*   0*/         }
/*   0*/    } 
/* 527*/    boolean useGettersAsSetters = (ctxt.isEnabled(MapperFeature.USE_GETTERS_AS_SETTERS) && ctxt.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
/* 531*/    List<BeanPropertyDefinition> propDefs = filterBeanProps(ctxt, beanDesc, builder, beanDesc.findProperties(), ignored);
/* 535*/    if (this._factoryConfig.hasDeserializerModifiers()) {
/* 536*/        for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
/* 537*/            propDefs = mod.updateProperties(ctxt.getConfig(), beanDesc, propDefs); 
/*   0*/           } 
/*   0*/       }
/* 542*/    for (BeanPropertyDefinition propDef : propDefs) {
/* 543*/      SettableBeanProperty prop = null;
/* 548*/      if (propDef.hasSetter()) {
/* 549*/        JavaType propertyType = propDef.getSetter().getParameterType(0);
/* 550*/        prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/* 551*/      } else if (propDef.hasField()) {
/* 552*/        JavaType propertyType = propDef.getField().getType();
/* 553*/        prop = constructSettableProperty(ctxt, beanDesc, propDef, propertyType);
/* 554*/      } else if (useGettersAsSetters && propDef.hasGetter()) {
/* 558*/        AnnotatedMethod getter = propDef.getGetter();
/* 560*/        Class<?> rawPropertyType = getter.getRawType();
/* 561*/        if (Collection.class.isAssignableFrom(rawPropertyType) || Map.class.isAssignableFrom(rawPropertyType)) {
/* 563*/            prop = constructSetterlessProperty(ctxt, beanDesc, propDef); 
/*   0*/           }
/*   0*/      } 
/* 568*/      if (hasCreatorProps && propDef.hasConstructorParameter()) {
/* 574*/        String name = propDef.getName();
/* 575*/        CreatorProperty cprop = null;
/* 576*/        if (creatorProps != null) {
/* 577*/            for (SettableBeanProperty cp : creatorProps) {
/* 578*/              if (name.equals(cp.getName()) && cp instanceof CreatorProperty) {
/* 579*/                cprop = (CreatorProperty)cp;
/*   0*/                break;
/*   0*/              } 
/*   0*/            }  
/*   0*/           }
/* 584*/        if (cprop == null) {
/* 585*/          List<String> n = new ArrayList<>();
/* 586*/          for (SettableBeanProperty cp : creatorProps) {
/* 587*/              n.add(cp.getName()); 
/*   0*/             }
/* 589*/          ctxt.reportBadPropertyDefinition(beanDesc, propDef, "Could not find creator property with name '%s' (known Creator properties: %s)", new Object[] { name, n });
/*   0*/          continue;
/*   0*/        } 
/* 594*/        if (prop != null) {
/* 595*/            cprop.setFallbackSetter(prop); 
/*   0*/           }
/* 597*/        prop = cprop;
/* 598*/        builder.addCreatorProperty(cprop);
/*   0*/        continue;
/*   0*/      } 
/* 602*/      if (prop != null) {
/* 603*/        Class<?>[] views = propDef.findViews();
/* 604*/        if (views == null) {
/* 606*/            if (!ctxt.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)) {
/* 607*/                views = NO_VIEWS; 
/*   0*/               } 
/*   0*/           }
/* 611*/        prop.setViews(views);
/* 612*/        builder.addProperty(prop);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected List<BeanPropertyDefinition> filterBeanProps(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder, List<BeanPropertyDefinition> propDefsIn, Set<String> ignored) throws JsonMappingException {
/* 629*/    ArrayList<BeanPropertyDefinition> result = new ArrayList<>(Math.max(4, propDefsIn.size()));
/* 631*/    HashMap<Class<?>, Boolean> ignoredTypes = new HashMap<>();
/* 633*/    for (BeanPropertyDefinition property : propDefsIn) {
/* 634*/      String name = property.getName();
/* 635*/      if (ignored.contains(name)) {
/*   0*/          continue; 
/*   0*/         }
/* 638*/      if (!property.hasConstructorParameter()) {
/* 639*/        Class<?> rawPropertyType = null;
/* 640*/        if (property.hasSetter()) {
/* 641*/          rawPropertyType = property.getSetter().getRawParameterType(0);
/* 642*/        } else if (property.hasField()) {
/* 643*/          rawPropertyType = property.getField().getRawType();
/*   0*/        } 
/* 647*/        if (rawPropertyType != null && isIgnorableType(ctxt.getConfig(), beanDesc, rawPropertyType, ignoredTypes)) {
/* 650*/          builder.addIgnorable(name);
/*   0*/          continue;
/*   0*/        } 
/*   0*/      } 
/* 654*/      result.add(property);
/*   0*/    } 
/* 656*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected void addReferenceProperties(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/* 668*/    Map<String, AnnotatedMember> refs = beanDesc.findBackReferenceProperties();
/* 669*/    if (refs != null) {
/* 670*/        for (Map.Entry<String, AnnotatedMember> en : refs.entrySet()) {
/*   0*/          JavaType type;
/* 671*/          String name = en.getKey();
/* 672*/          AnnotatedMember m = en.getValue();
/* 674*/          if (m instanceof AnnotatedMethod) {
/* 675*/            type = ((AnnotatedMethod)m).getParameterType(0);
/*   0*/          } else {
/* 677*/            type = m.getType();
/* 680*/            if (m instanceof AnnotatedParameter) {
/* 681*/                ctxt.reportBadTypeDefinition(beanDesc, "Can not bind back references as Creator parameters: type %s (reference '%s', parameter index #%d)", new Object[] { beanDesc.getBeanClass().getName(), name, ((AnnotatedParameter)m).getIndex() }); 
/*   0*/               }
/*   0*/          } 
/* 686*/          SimpleBeanPropertyDefinition propDef = SimpleBeanPropertyDefinition.construct(ctxt.getConfig(), m, PropertyName.construct(name));
/* 688*/          builder.addBackReferenceProperty(name, constructSettableProperty(ctxt, beanDesc, propDef, type));
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void addInjectables(DeserializationContext ctxt, BeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
/* 702*/    Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
/* 703*/    if (raw != null) {
/* 704*/        for (Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
/* 705*/          AnnotatedMember m = entry.getValue();
/* 706*/          builder.addInjectable(PropertyName.construct(m.getName()), m.getType(), beanDesc.getClassAnnotations(), m, entry.getKey());
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableAnyProperty constructAnySetter(DeserializationContext ctxt, BeanDescription beanDesc, AnnotatedMember mutator) throws JsonMappingException {
/* 727*/    JavaType type = null;
/* 728*/    if (mutator instanceof AnnotatedMethod) {
/* 730*/      type = ((AnnotatedMethod)mutator).getParameterType(1);
/* 731*/    } else if (mutator instanceof AnnotatedField) {
/* 733*/      type = ((AnnotatedField)mutator).getType().getContentType();
/*   0*/    } 
/* 737*/    type = resolveMemberAndTypeAnnotations(ctxt, mutator, type);
/* 738*/    BeanProperty.Std prop = new BeanProperty.Std(PropertyName.construct(mutator.getName()), type, null, beanDesc.getClassAnnotations(), mutator, PropertyMetadata.STD_OPTIONAL);
/* 742*/    JsonDeserializer<Object> deser = findDeserializerFromAnnotation(ctxt, mutator);
/* 743*/    if (deser == null) {
/* 744*/        deser = type.<JsonDeserializer<Object>>getValueHandler(); 
/*   0*/       }
/* 746*/    if (deser != null) {
/* 748*/        deser = (JsonDeserializer)ctxt.handlePrimaryContextualization(deser, prop, type); 
/*   0*/       }
/* 750*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 751*/    return new SettableAnyProperty(prop, mutator, type, deser, typeDeser);
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty constructSettableProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef, JavaType propType0) throws JsonMappingException {
/*   0*/    SettableBeanProperty prop;
/* 767*/    AnnotatedMember mutator = propDef.getNonConstructorMutator();
/* 771*/    if (mutator == null) {
/* 772*/        ctxt.reportBadPropertyDefinition(beanDesc, propDef, "No non-constructor mutator available", new Object[0]); 
/*   0*/       }
/* 774*/    JavaType type = resolveMemberAndTypeAnnotations(ctxt, mutator, propType0);
/* 776*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 778*/    if (mutator instanceof AnnotatedMethod) {
/* 779*/      prop = new MethodProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedMethod)mutator);
/*   0*/    } else {
/* 783*/      prop = new FieldProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), (AnnotatedField)mutator);
/*   0*/    } 
/* 786*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, mutator);
/* 787*/    if (deser == null) {
/* 788*/        deser = type.<JsonDeserializer>getValueHandler(); 
/*   0*/       }
/* 790*/    if (deser != null) {
/* 791*/      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 792*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/* 795*/    AnnotationIntrospector.ReferenceProperty ref = propDef.findReferenceType();
/* 796*/    if (ref != null && ref.isManagedReference()) {
/* 797*/        prop.setManagedReferenceName(ref.getName()); 
/*   0*/       }
/* 799*/    ObjectIdInfo objectIdInfo = propDef.findObjectIdInfo();
/* 800*/    if (objectIdInfo != null) {
/* 801*/        prop.setObjectIdInfo(objectIdInfo); 
/*   0*/       }
/* 803*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected SettableBeanProperty constructSetterlessProperty(DeserializationContext ctxt, BeanDescription beanDesc, BeanPropertyDefinition propDef) throws JsonMappingException {
/* 814*/    AnnotatedMethod getter = propDef.getGetter();
/* 815*/    JavaType type = resolveMemberAndTypeAnnotations(ctxt, getter, getter.getType());
/* 816*/    TypeDeserializer typeDeser = type.<TypeDeserializer>getTypeHandler();
/* 817*/    SettableBeanProperty prop = new SetterlessProperty(propDef, type, typeDeser, beanDesc.getClassAnnotations(), getter);
/* 819*/    JsonDeserializer<?> deser = findDeserializerFromAnnotation(ctxt, getter);
/* 820*/    if (deser == null) {
/* 821*/        deser = type.<JsonDeserializer>getValueHandler(); 
/*   0*/       }
/* 823*/    if (deser != null) {
/* 824*/      deser = ctxt.handlePrimaryContextualization(deser, prop, type);
/* 825*/      prop = prop.withValueDeserializer(deser);
/*   0*/    } 
/* 827*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isPotentialBeanType(Class<?> type) {
/* 846*/    String typeStr = ClassUtil.canBeABeanType(type);
/* 847*/    if (typeStr != null) {
/* 848*/        throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean"); 
/*   0*/       }
/* 850*/    if (ClassUtil.isProxyType(type)) {
/* 851*/        throw new IllegalArgumentException("Can not deserialize Proxy class " + type.getName() + " as a Bean"); 
/*   0*/       }
/* 856*/    typeStr = ClassUtil.isLocalType(type, true);
/* 857*/    if (typeStr != null) {
/* 858*/        throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean"); 
/*   0*/       }
/* 860*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean isIgnorableType(DeserializationConfig config, BeanDescription beanDesc, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
/* 870*/    Boolean status = ignoredTypes.get(type);
/* 871*/    if (status != null) {
/* 872*/        return status; 
/*   0*/       }
/* 875*/    ConfigOverride override = config.findConfigOverride(type);
/* 876*/    if (override != null) {
/* 877*/        status = override.getIsIgnoredType(); 
/*   0*/       }
/* 879*/    if (status == null) {
/* 880*/      BeanDescription desc = config.introspectClassAnnotations(type);
/* 881*/      status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
/* 883*/      if (status == null) {
/* 884*/          status = Boolean.FALSE; 
/*   0*/         }
/*   0*/    } 
/* 887*/    ignoredTypes.put(type, status);
/* 888*/    return status;
/*   0*/  }
/*   0*/  
/*   0*/  protected void checkIllegalTypes(DeserializationContext ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
/* 900*/    String full = type.getRawClass().getName();
/* 902*/    if (this._cfgIllegalClassNames.contains(full)) {
/* 903*/        ctxt.reportBadTypeDefinition(beanDesc, "Illegal type (%s) to deserialize: prevented for security reasons", new Object[] { full }); 
/*   0*/       }
/*   0*/  }
/*   0*/}
