/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*   0*/import com.fasterxml.jackson.annotation.JsonInclude;
/*   0*/import com.fasterxml.jackson.annotation.JsonTypeInfo;
/*   0*/import com.fasterxml.jackson.annotation.PropertyAccessor;
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.Base64Variants;
/*   0*/import com.fasterxml.jackson.core.FormatSchema;
/*   0*/import com.fasterxml.jackson.core.JsonEncoding;
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.JsonGenerationException;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.JsonParseException;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.core.JsonProcessingException;
/*   0*/import com.fasterxml.jackson.core.JsonToken;
/*   0*/import com.fasterxml.jackson.core.ObjectCodec;
/*   0*/import com.fasterxml.jackson.core.PrettyPrinter;
/*   0*/import com.fasterxml.jackson.core.TreeNode;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.Versioned;
/*   0*/import com.fasterxml.jackson.core.io.CharacterEscapes;
/*   0*/import com.fasterxml.jackson.core.io.SegmentedStringWriter;
/*   0*/import com.fasterxml.jackson.core.type.ResolvedType;
/*   0*/import com.fasterxml.jackson.core.type.TypeReference;
/*   0*/import com.fasterxml.jackson.core.util.ByteArrayBuilder;
/*   0*/import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*   0*/import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*   0*/import com.fasterxml.jackson.databind.cfg.PackageVersion;
/*   0*/import com.fasterxml.jackson.databind.deser.BeanDeserializerFactory;
/*   0*/import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
/*   0*/import com.fasterxml.jackson.databind.deser.DefaultDeserializationContext;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializerFactory;
/*   0*/import com.fasterxml.jackson.databind.deser.Deserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.KeyDeserializers;
/*   0*/import com.fasterxml.jackson.databind.deser.ValueInstantiators;
/*   0*/import com.fasterxml.jackson.databind.introspect.BasicClassIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
/*   0*/import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.impl.StdSubtypeResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.node.ArrayNode;
/*   0*/import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*   0*/import com.fasterxml.jackson.databind.node.NullNode;
/*   0*/import com.fasterxml.jackson.databind.node.ObjectNode;
/*   0*/import com.fasterxml.jackson.databind.node.TreeTraversingParser;
/*   0*/import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
/*   0*/import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
/*   0*/import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
/*   0*/import com.fasterxml.jackson.databind.ser.FilterProvider;
/*   0*/import com.fasterxml.jackson.databind.ser.SerializerFactory;
/*   0*/import com.fasterxml.jackson.databind.ser.Serializers;
/*   0*/import com.fasterxml.jackson.databind.type.ClassKey;
/*   0*/import com.fasterxml.jackson.databind.type.SimpleType;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.type.TypeModifier;
/*   0*/import com.fasterxml.jackson.databind.util.RootNameLookup;
/*   0*/import com.fasterxml.jackson.databind.util.StdDateFormat;
/*   0*/import com.fasterxml.jackson.databind.util.TokenBuffer;
/*   0*/import java.io.Closeable;
/*   0*/import java.io.File;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.OutputStream;
/*   0*/import java.io.Reader;
/*   0*/import java.io.Serializable;
/*   0*/import java.io.Writer;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.net.URL;
/*   0*/import java.text.DateFormat;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Locale;
/*   0*/import java.util.Map;
/*   0*/import java.util.ServiceLoader;
/*   0*/import java.util.TimeZone;
/*   0*/import java.util.concurrent.ConcurrentHashMap;
/*   0*/import java.util.concurrent.atomic.AtomicReference;
/*   0*/
/*   0*/public class ObjectMapper extends ObjectCodec implements Versioned, Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  public enum DefaultTyping {
/*  87*/    JAVA_LANG_OBJECT, OBJECT_AND_NON_CONCRETE, NON_CONCRETE_AND_ARRAYS, NON_FINAL;
/*   0*/  }
/*   0*/  
/*   0*/  public static class DefaultTypeResolverBuilder extends StdTypeResolverBuilder implements Serializable {
/*   0*/    private static final long serialVersionUID = 1L;
/*   0*/    
/*   0*/    protected final ObjectMapper.DefaultTyping _appliesFor;
/*   0*/    
/*   0*/    public DefaultTypeResolverBuilder(ObjectMapper.DefaultTyping t) {
/* 142*/      this._appliesFor = t;
/*   0*/    }
/*   0*/    
/*   0*/    public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/* 149*/      return useForType(baseType) ? super.buildTypeDeserializer(config, baseType, subtypes) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public TypeSerializer buildTypeSerializer(SerializationConfig config, JavaType baseType, Collection<NamedType> subtypes) {
/* 156*/      return useForType(baseType) ? super.buildTypeSerializer(config, baseType, subtypes) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean useForType(JavaType t) {
/* 169*/      switch (this._appliesFor) {
/*   0*/        case NON_CONCRETE_AND_ARRAYS:
/* 171*/          while (t.isArrayType()) {
/* 172*/              t = t.getContentType(); 
/*   0*/             }
/*   0*/        case OBJECT_AND_NON_CONCRETE:
/* 176*/          return (t.getRawClass() == Object.class || !t.isConcrete() || TreeNode.class.isAssignableFrom(t.getRawClass()));
/*   0*/        case NON_FINAL:
/* 180*/          while (t.isArrayType()) {
/* 181*/              t = t.getContentType(); 
/*   0*/             }
/* 184*/          return (!t.isFinal() && !TreeNode.class.isAssignableFrom(t.getRawClass()));
/*   0*/      } 
/* 187*/      return (t.getRawClass() == Object.class);
/*   0*/    }
/*   0*/  }
/*   0*/  
/* 199*/  private static final JavaType JSON_NODE_TYPE = SimpleType.constructUnsafe(JsonNode.class);
/*   0*/  
/* 204*/  protected static final ClassIntrospector DEFAULT_INTROSPECTOR = BasicClassIntrospector.instance;
/*   0*/  
/* 207*/  protected static final AnnotationIntrospector DEFAULT_ANNOTATION_INTROSPECTOR = new JacksonAnnotationIntrospector();
/*   0*/  
/* 209*/  protected static final VisibilityChecker<?> STD_VISIBILITY_CHECKER = VisibilityChecker.Std.defaultInstance();
/*   0*/  
/* 211*/  protected static final PrettyPrinter _defaultPrettyPrinter = (PrettyPrinter)new DefaultPrettyPrinter();
/*   0*/  
/* 217*/  protected static final BaseSettings DEFAULT_BASE = new BaseSettings(DEFAULT_INTROSPECTOR, DEFAULT_ANNOTATION_INTROSPECTOR, STD_VISIBILITY_CHECKER, null, TypeFactory.defaultInstance(), null, StdDateFormat.instance, null, Locale.getDefault(), TimeZone.getTimeZone("GMT"), Base64Variants.getDefaultVariant());
/*   0*/  
/*   0*/  protected final JsonFactory _jsonFactory;
/*   0*/  
/*   0*/  protected TypeFactory _typeFactory;
/*   0*/  
/*   0*/  protected InjectableValues _injectableValues;
/*   0*/  
/*   0*/  protected SubtypeResolver _subtypeResolver;
/*   0*/  
/*   0*/  protected final RootNameLookup _rootNames;
/*   0*/  
/*   0*/  protected final HashMap<ClassKey, Class<?>> _mixInAnnotations;
/*   0*/  
/*   0*/  protected SerializationConfig _serializationConfig;
/*   0*/  
/*   0*/  protected DefaultSerializerProvider _serializerProvider;
/*   0*/  
/*   0*/  protected SerializerFactory _serializerFactory;
/*   0*/  
/*   0*/  protected DeserializationConfig _deserializationConfig;
/*   0*/  
/*   0*/  protected DefaultDeserializationContext _deserializationContext;
/*   0*/  
/* 356*/  protected final ConcurrentHashMap<JavaType, JsonDeserializer<Object>> _rootDeserializers = new ConcurrentHashMap<JavaType, JsonDeserializer<Object>>(64, 0.6F, 2);
/*   0*/  
/*   0*/  public ObjectMapper() {
/* 379*/    this(null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper(JsonFactory jf) {
/* 389*/    this(jf, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected ObjectMapper(ObjectMapper src) {
/* 399*/    this._jsonFactory = src._jsonFactory.copy();
/* 400*/    this._jsonFactory.setCodec(this);
/* 401*/    this._subtypeResolver = src._subtypeResolver;
/* 402*/    this._rootNames = new RootNameLookup();
/* 403*/    this._typeFactory = src._typeFactory;
/* 404*/    this._serializationConfig = src._serializationConfig;
/* 405*/    HashMap<ClassKey, Class<?>> mixins = new HashMap<ClassKey, Class<?>>(src._mixInAnnotations);
/* 406*/    this._mixInAnnotations = mixins;
/* 407*/    this._serializationConfig = new SerializationConfig(src._serializationConfig, mixins);
/* 408*/    this._deserializationConfig = new DeserializationConfig(src._deserializationConfig, mixins);
/* 409*/    this._serializerProvider = src._serializerProvider;
/* 410*/    this._deserializationContext = src._deserializationContext;
/* 413*/    this._serializerFactory = src._serializerFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper(JsonFactory jf, DefaultSerializerProvider sp, DefaultDeserializationContext dc) {
/* 437*/    if (jf == null) {
/* 438*/      this._jsonFactory = new MappingJsonFactory(this);
/*   0*/    } else {
/* 440*/      this._jsonFactory = jf;
/* 441*/      if (jf.getCodec() == null) {
/* 442*/          this._jsonFactory.setCodec(this); 
/*   0*/         }
/*   0*/    } 
/* 445*/    this._subtypeResolver = new StdSubtypeResolver();
/* 446*/    this._rootNames = new RootNameLookup();
/* 448*/    this._typeFactory = TypeFactory.defaultInstance();
/* 450*/    HashMap<ClassKey, Class<?>> mixins = new HashMap<ClassKey, Class<?>>();
/* 451*/    this._mixInAnnotations = mixins;
/* 452*/    this._serializationConfig = new SerializationConfig(DEFAULT_BASE, this._subtypeResolver, mixins);
/* 454*/    this._deserializationConfig = new DeserializationConfig(DEFAULT_BASE, this._subtypeResolver, mixins);
/* 458*/    boolean needOrder = this._jsonFactory.requiresPropertyOrdering();
/* 459*/    if (needOrder ^ this._serializationConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)) {
/* 460*/        configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, needOrder); 
/*   0*/       }
/* 463*/    this._serializerProvider = (sp == null) ? new DefaultSerializerProvider.Impl() : sp;
/* 464*/    this._deserializationContext = (dc == null) ? new DefaultDeserializationContext.Impl(BeanDeserializerFactory.instance) : dc;
/* 468*/    this._serializerFactory = BeanSerializerFactory.instance;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper copy() {
/* 488*/    _checkInvalidCopy(ObjectMapper.class);
/* 489*/    return new ObjectMapper(this);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _checkInvalidCopy(Class<?> exp) {
/* 498*/    if (getClass() != exp) {
/* 499*/        throw new IllegalStateException("Failed copy(): " + getClass().getName() + " (version: " + version() + ") does not override copy(); it has to"); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public Version version() {
/* 516*/    return PackageVersion.VERSION;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper registerModule(Module module) {
/* 538*/    String name = module.getModuleName();
/* 539*/    if (name == null) {
/* 540*/        throw new IllegalArgumentException("Module without defined name"); 
/*   0*/       }
/* 542*/    Version version = module.version();
/* 543*/    if (version == null) {
/* 544*/        throw new IllegalArgumentException("Module without defined version"); 
/*   0*/       }
/* 547*/    final ObjectMapper mapper = this;
/* 550*/    module.setupModule(new Module.SetupContext() {
/*   0*/          public Version getMapperVersion() {
/* 556*/            return ObjectMapper.this.version();
/*   0*/          }
/*   0*/          
/*   0*/          public <C extends ObjectCodec> C getOwner() {
/* 563*/            return (C)mapper;
/*   0*/          }
/*   0*/          
/*   0*/          public TypeFactory getTypeFactory() {
/* 568*/            return ObjectMapper.this._typeFactory;
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(MapperFeature f) {
/* 573*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(DeserializationFeature f) {
/* 578*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(SerializationFeature f) {
/* 583*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(JsonFactory.Feature f) {
/* 588*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(JsonParser.Feature f) {
/* 593*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public boolean isEnabled(JsonGenerator.Feature f) {
/* 598*/            return mapper.isEnabled(f);
/*   0*/          }
/*   0*/          
/*   0*/          public void addDeserializers(Deserializers d) {
/* 605*/            DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalDeserializers(d);
/* 606*/            mapper._deserializationContext = mapper._deserializationContext.with(df);
/*   0*/          }
/*   0*/          
/*   0*/          public void addKeyDeserializers(KeyDeserializers d) {
/* 611*/            DeserializerFactory df = mapper._deserializationContext._factory.withAdditionalKeyDeserializers(d);
/* 612*/            mapper._deserializationContext = mapper._deserializationContext.with(df);
/*   0*/          }
/*   0*/          
/*   0*/          public void addBeanDeserializerModifier(BeanDeserializerModifier modifier) {
/* 617*/            DeserializerFactory df = mapper._deserializationContext._factory.withDeserializerModifier(modifier);
/* 618*/            mapper._deserializationContext = mapper._deserializationContext.with(df);
/*   0*/          }
/*   0*/          
/*   0*/          public void addSerializers(Serializers s) {
/* 625*/            mapper._serializerFactory = mapper._serializerFactory.withAdditionalSerializers(s);
/*   0*/          }
/*   0*/          
/*   0*/          public void addKeySerializers(Serializers s) {
/* 630*/            mapper._serializerFactory = mapper._serializerFactory.withAdditionalKeySerializers(s);
/*   0*/          }
/*   0*/          
/*   0*/          public void addBeanSerializerModifier(BeanSerializerModifier modifier) {
/* 635*/            mapper._serializerFactory = mapper._serializerFactory.withSerializerModifier(modifier);
/*   0*/          }
/*   0*/          
/*   0*/          public void addAbstractTypeResolver(AbstractTypeResolver resolver) {
/* 642*/            DeserializerFactory df = mapper._deserializationContext._factory.withAbstractTypeResolver(resolver);
/* 643*/            mapper._deserializationContext = mapper._deserializationContext.with(df);
/*   0*/          }
/*   0*/          
/*   0*/          public void addTypeModifier(TypeModifier modifier) {
/* 648*/            TypeFactory f = mapper._typeFactory;
/* 649*/            f = f.withModifier(modifier);
/* 650*/            mapper.setTypeFactory(f);
/*   0*/          }
/*   0*/          
/*   0*/          public void addValueInstantiators(ValueInstantiators instantiators) {
/* 655*/            DeserializerFactory df = mapper._deserializationContext._factory.withValueInstantiators(instantiators);
/* 656*/            mapper._deserializationContext = mapper._deserializationContext.with(df);
/*   0*/          }
/*   0*/          
/*   0*/          public void setClassIntrospector(ClassIntrospector ci) {
/* 661*/            mapper._deserializationConfig = mapper._deserializationConfig.with(ci);
/* 662*/            mapper._serializationConfig = mapper._serializationConfig.with(ci);
/*   0*/          }
/*   0*/          
/*   0*/          public void insertAnnotationIntrospector(AnnotationIntrospector ai) {
/* 667*/            mapper._deserializationConfig = mapper._deserializationConfig.withInsertedAnnotationIntrospector(ai);
/* 668*/            mapper._serializationConfig = mapper._serializationConfig.withInsertedAnnotationIntrospector(ai);
/*   0*/          }
/*   0*/          
/*   0*/          public void appendAnnotationIntrospector(AnnotationIntrospector ai) {
/* 673*/            mapper._deserializationConfig = mapper._deserializationConfig.withAppendedAnnotationIntrospector(ai);
/* 674*/            mapper._serializationConfig = mapper._serializationConfig.withAppendedAnnotationIntrospector(ai);
/*   0*/          }
/*   0*/          
/*   0*/          public void registerSubtypes(Class<?>... subtypes) {
/* 679*/            mapper.registerSubtypes(subtypes);
/*   0*/          }
/*   0*/          
/*   0*/          public void registerSubtypes(NamedType... subtypes) {
/* 684*/            mapper.registerSubtypes(subtypes);
/*   0*/          }
/*   0*/          
/*   0*/          public void setMixInAnnotations(Class<?> target, Class<?> mixinSource) {
/* 689*/            mapper.addMixInAnnotations(target, mixinSource);
/*   0*/          }
/*   0*/          
/*   0*/          public void addDeserializationProblemHandler(DeserializationProblemHandler handler) {
/* 694*/            mapper.addHandler(handler);
/*   0*/          }
/*   0*/          
/*   0*/          public void setNamingStrategy(PropertyNamingStrategy naming) {
/* 699*/            mapper.setPropertyNamingStrategy(naming);
/*   0*/          }
/*   0*/        });
/* 702*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper registerModules(Module... modules) {
/* 718*/    for (Module module : modules) {
/* 719*/        registerModule(module); 
/*   0*/       }
/* 721*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper registerModules(Iterable<Module> modules) {
/* 737*/    for (Module module : modules) {
/* 738*/        registerModule(module); 
/*   0*/       }
/* 740*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Module> findModules() {
/* 753*/    return findModules(null);
/*   0*/  }
/*   0*/  
/*   0*/  public static List<Module> findModules(ClassLoader classLoader) {
/* 767*/    ArrayList<Module> modules = new ArrayList<Module>();
/* 768*/    ServiceLoader<Module> loader = (classLoader == null) ? ServiceLoader.<Module>load(Module.class) : ServiceLoader.<Module>load(Module.class, classLoader);
/* 770*/    for (Module module : loader) {
/* 771*/        modules.add(module); 
/*   0*/       }
/* 773*/    return modules;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper findAndRegisterModules() {
/* 789*/    return registerModules(findModules());
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig getSerializationConfig() {
/* 807*/    return this._serializationConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig getDeserializationConfig() {
/* 820*/    return this._deserializationConfig;
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationContext getDeserializationContext() {
/* 831*/    return this._deserializationContext;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setSerializerFactory(SerializerFactory f) {
/* 845*/    this._serializerFactory = f;
/* 846*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public SerializerFactory getSerializerFactory() {
/* 857*/    return this._serializerFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setSerializerProvider(DefaultSerializerProvider p) {
/* 865*/    this._serializerProvider = p;
/* 866*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public SerializerProvider getSerializerProvider() {
/* 870*/    return this._serializerProvider;
/*   0*/  }
/*   0*/  
/*   0*/  public final void setMixInAnnotations(Map<Class<?>, Class<?>> sourceMixins) {
/* 893*/    this._mixInAnnotations.clear();
/* 894*/    if (sourceMixins != null && sourceMixins.size() > 0) {
/* 895*/        for (Map.Entry<Class<?>, Class<?>> en : sourceMixins.entrySet()) {
/* 896*/            this._mixInAnnotations.put(new ClassKey(en.getKey()), en.getValue()); 
/*   0*/           } 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public final void addMixInAnnotations(Class<?> target, Class<?> mixinSource) {
/* 913*/    this._mixInAnnotations.put(new ClassKey(target), mixinSource);
/*   0*/  }
/*   0*/  
/*   0*/  public final Class<?> findMixInClassFor(Class<?> cls) {
/* 917*/    return (this._mixInAnnotations == null) ? null : this._mixInAnnotations.get(new ClassKey(cls));
/*   0*/  }
/*   0*/  
/*   0*/  public final int mixInCount() {
/* 921*/    return (this._mixInAnnotations == null) ? 0 : this._mixInAnnotations.size();
/*   0*/  }
/*   0*/  
/*   0*/  public VisibilityChecker<?> getVisibilityChecker() {
/* 936*/    return this._serializationConfig.getDefaultVisibilityChecker();
/*   0*/  }
/*   0*/  
/*   0*/  public void setVisibilityChecker(VisibilityChecker<?> vc) {
/* 947*/    this._deserializationConfig = this._deserializationConfig.with(vc);
/* 948*/    this._serializationConfig = this._serializationConfig.with(vc);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
/* 977*/    this._deserializationConfig = this._deserializationConfig.withVisibility(forMethod, visibility);
/* 978*/    this._serializationConfig = this._serializationConfig.withVisibility(forMethod, visibility);
/* 979*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public SubtypeResolver getSubtypeResolver() {
/* 986*/    return this._subtypeResolver;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setSubtypeResolver(SubtypeResolver str) {
/* 993*/    this._subtypeResolver = str;
/* 994*/    this._deserializationConfig = this._deserializationConfig.with(str);
/* 995*/    this._serializationConfig = this._serializationConfig.with(str);
/* 996*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setAnnotationIntrospector(AnnotationIntrospector ai) {
/*1004*/    this._serializationConfig = this._serializationConfig.with(ai);
/*1005*/    this._deserializationConfig = this._deserializationConfig.with(ai);
/*1006*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setAnnotationIntrospectors(AnnotationIntrospector serializerAI, AnnotationIntrospector deserializerAI) {
/*1024*/    this._serializationConfig = this._serializationConfig.with(serializerAI);
/*1025*/    this._deserializationConfig = this._deserializationConfig.with(deserializerAI);
/*1026*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setPropertyNamingStrategy(PropertyNamingStrategy s) {
/*1033*/    this._serializationConfig = this._serializationConfig.with(s);
/*1034*/    this._deserializationConfig = this._deserializationConfig.with(s);
/*1035*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setSerializationInclusion(JsonInclude.Include incl) {
/*1042*/    this._serializationConfig = this._serializationConfig.withSerializationInclusion(incl);
/*1043*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enableDefaultTyping() {
/*1059*/    return enableDefaultTyping(DefaultTyping.OBJECT_AND_NON_CONCRETE);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enableDefaultTyping(DefaultTyping dti) {
/*1069*/    return enableDefaultTyping(dti, JsonTypeInfo.As.WRAPPER_ARRAY);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enableDefaultTyping(DefaultTyping applicability, JsonTypeInfo.As includeAs) {
/*1082*/    TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
/*1084*/    typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
/*1085*/    typer = (TypeResolverBuilder<?>)typer.inclusion(includeAs);
/*1086*/    return setDefaultTyping(typer);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enableDefaultTypingAsProperty(DefaultTyping applicability, String propertyName) {
/*1099*/    TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(applicability);
/*1101*/    typer = (TypeResolverBuilder<?>)typer.init(JsonTypeInfo.Id.CLASS, null);
/*1102*/    typer = (TypeResolverBuilder<?>)typer.inclusion(JsonTypeInfo.As.PROPERTY);
/*1103*/    typer = (TypeResolverBuilder<?>)typer.typeProperty(propertyName);
/*1104*/    return setDefaultTyping(typer);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disableDefaultTyping() {
/*1114*/    return setDefaultTyping(null);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setDefaultTyping(TypeResolverBuilder<?> typer) {
/*1125*/    this._deserializationConfig = this._deserializationConfig.with(typer);
/*1126*/    this._serializationConfig = this._serializationConfig.with(typer);
/*1127*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void registerSubtypes(Class<?>... classes) {
/*1138*/    getSubtypeResolver().registerSubtypes(classes);
/*   0*/  }
/*   0*/  
/*   0*/  public void registerSubtypes(NamedType... types) {
/*1150*/    getSubtypeResolver().registerSubtypes(types);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeFactory getTypeFactory() {
/*1163*/    return this._typeFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setTypeFactory(TypeFactory f) {
/*1175*/    this._typeFactory = f;
/*1176*/    this._deserializationConfig = this._deserializationConfig.with(f);
/*1177*/    this._serializationConfig = this._serializationConfig.with(f);
/*1178*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType constructType(Type t) {
/*1187*/    return this._typeFactory.constructType(t);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setNodeFactory(JsonNodeFactory f) {
/*1202*/    this._deserializationConfig = this._deserializationConfig.with(f);
/*1203*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper addHandler(DeserializationProblemHandler h) {
/*1211*/    this._deserializationConfig = this._deserializationConfig.withHandler(h);
/*1212*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper clearProblemHandlers() {
/*1220*/    this._deserializationConfig = this._deserializationConfig.withNoProblemHandlers();
/*1221*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setConfig(DeserializationConfig config) {
/*1239*/    this._deserializationConfig = config;
/*1240*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFilters(FilterProvider filterProvider) {
/*1261*/    this._serializationConfig = this._serializationConfig.withFilters(filterProvider);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setBase64Variant(Base64Variant v) {
/*1275*/    this._serializationConfig = this._serializationConfig.with(v);
/*1276*/    this._deserializationConfig = this._deserializationConfig.with(v);
/*1277*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setConfig(SerializationConfig config) {
/*1295*/    this._serializationConfig = config;
/*1296*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFactory getFactory() {
/*1314*/    return this._jsonFactory;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonFactory getJsonFactory() {
/*1321*/    return getFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setDateFormat(DateFormat dateFormat) {
/*1335*/    this._deserializationConfig = this._deserializationConfig.with(dateFormat);
/*1336*/    this._serializationConfig = this._serializationConfig.with(dateFormat);
/*1337*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public Object setHandlerInstantiator(HandlerInstantiator hi) {
/*1349*/    this._deserializationConfig = this._deserializationConfig.with(hi);
/*1350*/    this._serializationConfig = this._serializationConfig.with(hi);
/*1351*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setInjectableValues(InjectableValues injectableValues) {
/*1359*/    this._injectableValues = injectableValues;
/*1360*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setLocale(Locale l) {
/*1368*/    this._deserializationConfig = this._deserializationConfig.with(l);
/*1369*/    this._serializationConfig = this._serializationConfig.with(l);
/*1370*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper setTimeZone(TimeZone tz) {
/*1378*/    this._deserializationConfig = this._deserializationConfig.with(tz);
/*1379*/    this._serializationConfig = this._serializationConfig.with(tz);
/*1380*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper configure(MapperFeature f, boolean state) {
/*1394*/    this._serializationConfig = state ? this._serializationConfig.with(new MapperFeature[] { f }) : this._serializationConfig.without(new MapperFeature[] { f });
/*1396*/    this._deserializationConfig = state ? this._deserializationConfig.with(new MapperFeature[] { f }) : this._deserializationConfig.without(new MapperFeature[] { f });
/*1398*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper configure(SerializationFeature f, boolean state) {
/*1406*/    this._serializationConfig = state ? this._serializationConfig.with(f) : this._serializationConfig.without(f);
/*1408*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper configure(DeserializationFeature f, boolean state) {
/*1416*/    this._deserializationConfig = state ? this._deserializationConfig.with(f) : this._deserializationConfig.without(f);
/*1418*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper configure(JsonParser.Feature f, boolean state) {
/*1431*/    this._jsonFactory.configure(f, state);
/*1432*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper configure(JsonGenerator.Feature f, boolean state) {
/*1445*/    this._jsonFactory.configure(f, state);
/*1446*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enable(MapperFeature... f) {
/*1454*/    this._deserializationConfig = this._deserializationConfig.with(f);
/*1455*/    this._serializationConfig = this._serializationConfig.with(f);
/*1456*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disable(MapperFeature... f) {
/*1464*/    this._deserializationConfig = this._deserializationConfig.without(f);
/*1465*/    this._serializationConfig = this._serializationConfig.without(f);
/*1466*/    return copy();
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enable(DeserializationFeature feature) {
/*1474*/    this._deserializationConfig = this._deserializationConfig.with(feature);
/*1475*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enable(DeserializationFeature first, DeserializationFeature... f) {
/*1484*/    this._deserializationConfig = this._deserializationConfig.with(first, f);
/*1485*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disable(DeserializationFeature feature) {
/*1493*/    this._deserializationConfig = this._deserializationConfig.without(feature);
/*1494*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disable(DeserializationFeature first, DeserializationFeature... f) {
/*1503*/    this._deserializationConfig = this._deserializationConfig.without(first, f);
/*1504*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enable(SerializationFeature f) {
/*1512*/    this._serializationConfig = this._serializationConfig.with(f);
/*1513*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper enable(SerializationFeature first, SerializationFeature... f) {
/*1522*/    this._serializationConfig = this._serializationConfig.with(first, f);
/*1523*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disable(SerializationFeature f) {
/*1531*/    this._serializationConfig = this._serializationConfig.without(f);
/*1532*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectMapper disable(SerializationFeature first, SerializationFeature... f) {
/*1541*/    this._serializationConfig = this._serializationConfig.without(first, f);
/*1542*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(MapperFeature f) {
/*1551*/    return this._serializationConfig.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(SerializationFeature f) {
/*1559*/    return this._serializationConfig.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(DeserializationFeature f) {
/*1567*/    return this._deserializationConfig.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonFactory.Feature f) {
/*1577*/    return this._jsonFactory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonParser.Feature f) {
/*1587*/    return this._jsonFactory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEnabled(JsonGenerator.Feature f) {
/*1597*/    return this._jsonFactory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNodeFactory getNodeFactory() {
/*1611*/    return this._deserializationConfig.getNodeFactory();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*1637*/    return (T)_readValue(getDeserializationConfig(), jp, this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*1652*/    return (T)_readValue(getDeserializationConfig(), jp, this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public final <T> T readValue(JsonParser jp, ResolvedType valueType) throws IOException, JsonParseException, JsonMappingException {
/*1666*/    return (T)_readValue(getDeserializationConfig(), jp, (JavaType)valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*1676*/    return (T)_readValue(getDeserializationConfig(), jp, valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends TreeNode> T readTree(JsonParser jp) throws IOException, JsonProcessingException {
/*1697*/    DeserializationConfig cfg = getDeserializationConfig();
/*1698*/    JsonToken t = jp.getCurrentToken();
/*1699*/    if (t == null) {
/*1700*/      t = jp.nextToken();
/*1701*/      if (t == null) {
/*1702*/          return null; 
/*   0*/         }
/*   0*/    } 
/*1705*/    JsonNode n = (JsonNode)_readValue(cfg, jp, JSON_NODE_TYPE);
/*1706*/    if (n == null) {
/*1707*/        n = getNodeFactory().nullNode(); 
/*   0*/       }
/*1710*/    return (T)n;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser jp, ResolvedType valueType) throws IOException, JsonProcessingException {
/*1730*/    return readValues(jp, (JavaType)valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser jp, JavaType valueType) throws IOException, JsonProcessingException {
/*1739*/    DeserializationConfig config = getDeserializationConfig();
/*1740*/    DeserializationContext ctxt = createDeserializationContext(jp, config);
/*1741*/    JsonDeserializer<?> deser = _findRootDeserializer(ctxt, valueType);
/*1743*/    return new MappingIterator<T>(valueType, jp, ctxt, deser, false, null);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser jp, Class<T> valueType) throws IOException, JsonProcessingException {
/*1754*/    return readValues(jp, this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> MappingIterator<T> readValues(JsonParser jp, TypeReference<?> valueTypeRef) throws IOException, JsonProcessingException {
/*1764*/    return readValues(jp, this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(InputStream in) throws IOException, JsonProcessingException {
/*1787*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(in), JSON_NODE_TYPE);
/*1788*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(Reader r) throws IOException, JsonProcessingException {
/*1804*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(r), JSON_NODE_TYPE);
/*1805*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(String content) throws IOException, JsonProcessingException {
/*1818*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(content), JSON_NODE_TYPE);
/*1819*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(byte[] content) throws IOException, JsonProcessingException {
/*1832*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(content), JSON_NODE_TYPE);
/*1833*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(File file) throws IOException, JsonProcessingException {
/*1846*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(file), JSON_NODE_TYPE);
/*1847*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonNode readTree(URL source) throws IOException, JsonProcessingException {
/*1860*/    JsonNode n = (JsonNode)_readMapAndClose(this._jsonFactory.createParser(source), JSON_NODE_TYPE);
/*1861*/    return (n == null) ? NullNode.instance : n;
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(JsonGenerator jgen, Object value) throws IOException, JsonGenerationException, JsonMappingException {
/*1879*/    SerializationConfig config = getSerializationConfig();
/*1881*/    if (config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
/*1882*/        jgen.useDefaultPrettyPrinter(); 
/*   0*/       }
/*1884*/    if (config.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/*1885*/      _writeCloseableValue(jgen, value, config);
/*   0*/    } else {
/*1887*/      _serializerProvider(config).serializeValue(jgen, value);
/*1888*/      if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/*1889*/          jgen.flush(); 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(JsonGenerator jgen, TreeNode rootNode) throws IOException, JsonProcessingException {
/*1904*/    SerializationConfig config = getSerializationConfig();
/*1905*/    _serializerProvider(config).serializeValue(jgen, rootNode);
/*1906*/    if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/*1907*/        jgen.flush(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public void writeTree(JsonGenerator jgen, JsonNode rootNode) throws IOException, JsonProcessingException {
/*1918*/    SerializationConfig config = getSerializationConfig();
/*1919*/    _serializerProvider(config).serializeValue(jgen, rootNode);
/*1920*/    if (config.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/*1921*/        jgen.flush(); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectNode createObjectNode() {
/*1934*/    return this._deserializationConfig.getNodeFactory().objectNode();
/*   0*/  }
/*   0*/  
/*   0*/  public ArrayNode createArrayNode() {
/*1946*/    return this._deserializationConfig.getNodeFactory().arrayNode();
/*   0*/  }
/*   0*/  
/*   0*/  public JsonParser treeAsTokens(TreeNode n) {
/*1958*/    return (JsonParser)new TreeTraversingParser((JsonNode)n, this);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T treeToValue(TreeNode n, Class<T> valueType) throws JsonProcessingException {
/*   0*/    try {
/*1978*/      if (valueType != Object.class && valueType.isAssignableFrom(n.getClass())) {
/*1979*/          return (T)n; 
/*   0*/         }
/*1981*/      return readValue(treeAsTokens(n), valueType);
/*1982*/    } catch (JsonProcessingException e) {
/*1983*/      throw e;
/*1984*/    } catch (IOException e) {
/*1985*/      throw new IllegalArgumentException(e.getMessage(), e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends JsonNode> T valueToTree(Object fromValue) throws IllegalArgumentException {
/*   0*/    JsonNode result;
/*2011*/    if (fromValue == null) {
/*2011*/        return null; 
/*   0*/       }
/*2012*/    TokenBuffer buf = new TokenBuffer(this, false);
/*   0*/    try {
/*2015*/      writeValue(buf, fromValue);
/*2016*/      JsonParser jp = buf.asParser();
/*2017*/      result = readTree(jp);
/*2018*/      jp.close();
/*2019*/    } catch (IOException e) {
/*2020*/      throw new IllegalArgumentException(e.getMessage(), e);
/*   0*/    } 
/*2022*/    return (T)result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canSerialize(Class<?> type) {
/*2046*/    return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canSerialize(Class<?> type, AtomicReference<Throwable> cause) {
/*2057*/    return _serializerProvider(getSerializationConfig()).hasSerializerFor(type, cause);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canDeserialize(JavaType type) {
/*2072*/    return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canDeserialize(JavaType type, AtomicReference<Throwable> cause) {
/*2085*/    return createDeserializationContext(null, getDeserializationConfig()).hasValueDeserializerFor(type, cause);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(File src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2100*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(File src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2107*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(File src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2114*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(URL src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2123*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(URL src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2130*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(URL src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2137*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(String content, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2146*/    return (T)_readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(String content, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2153*/    return (T)_readMapAndClose(this._jsonFactory.createParser(content), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(String content, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2160*/    return (T)_readMapAndClose(this._jsonFactory.createParser(content), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(Reader src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2169*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(Reader src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2176*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(Reader src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2183*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(InputStream src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2192*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(InputStream src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2199*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(InputStream src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2206*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2215*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
/*2225*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2232*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, int offset, int len, TypeReference valueTypeRef) throws IOException, JsonParseException, JsonMappingException {
/*2240*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), this._typeFactory.constructType(valueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2247*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T readValue(byte[] src, int offset, int len, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*2255*/    return (T)_readMapAndClose(this._jsonFactory.createParser(src, offset, len), valueType);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(File resultFile, Object value) throws IOException, JsonGenerationException, JsonMappingException {
/*2272*/    _configAndWriteValue(this._jsonFactory.createGenerator(resultFile, JsonEncoding.UTF8), value);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(OutputStream out, Object value) throws IOException, JsonGenerationException, JsonMappingException {
/*2289*/    _configAndWriteValue(this._jsonFactory.createGenerator(out, JsonEncoding.UTF8), value);
/*   0*/  }
/*   0*/  
/*   0*/  public void writeValue(Writer w, Object value) throws IOException, JsonGenerationException, JsonMappingException {
/*2305*/    _configAndWriteValue(this._jsonFactory.createGenerator(w), value);
/*   0*/  }
/*   0*/  
/*   0*/  public String writeValueAsString(Object value) throws JsonProcessingException {
/*2321*/    SegmentedStringWriter sw = new SegmentedStringWriter(this._jsonFactory._getBufferRecycler());
/*   0*/    try {
/*2323*/      _configAndWriteValue(this._jsonFactory.createGenerator((Writer)sw), value);
/*2324*/    } catch (JsonProcessingException e) {
/*2325*/      throw e;
/*2326*/    } catch (IOException e) {
/*2327*/      throw JsonMappingException.fromUnexpectedIOE(e);
/*   0*/    } 
/*2329*/    return sw.getAndClear();
/*   0*/  }
/*   0*/  
/*   0*/  public byte[] writeValueAsBytes(Object value) throws JsonProcessingException {
/*2345*/    ByteArrayBuilder bb = new ByteArrayBuilder(this._jsonFactory._getBufferRecycler());
/*   0*/    try {
/*2347*/      _configAndWriteValue(this._jsonFactory.createGenerator((OutputStream)bb, JsonEncoding.UTF8), value);
/*2348*/    } catch (JsonProcessingException e) {
/*2349*/      throw e;
/*2350*/    } catch (IOException e) {
/*2351*/      throw JsonMappingException.fromUnexpectedIOE(e);
/*   0*/    } 
/*2353*/    byte[] result = bb.toByteArray();
/*2354*/    bb.release();
/*2355*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer() {
/*2370*/    return new ObjectWriter(this, getSerializationConfig());
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(SerializationFeature feature) {
/*2379*/    return new ObjectWriter(this, getSerializationConfig().with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(SerializationFeature first, SerializationFeature... other) {
/*2389*/    return new ObjectWriter(this, getSerializationConfig().with(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(DateFormat df) {
/*2398*/    return new ObjectWriter(this, getSerializationConfig().with(df));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writerWithView(Class<?> serializationView) {
/*2406*/    return new ObjectWriter(this, getSerializationConfig().withView(serializationView));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writerWithType(Class<?> rootType) {
/*2416*/    return new ObjectWriter(this, getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writerWithType(TypeReference<?> rootType) {
/*2428*/    return new ObjectWriter(this, getSerializationConfig(), (rootType == null) ? null : this._typeFactory.constructType(rootType), null);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writerWithType(JavaType rootType) {
/*2440*/    return new ObjectWriter(this, getSerializationConfig(), rootType, null);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(PrettyPrinter pp) {
/*2449*/    if (pp == null) {
/*2450*/        pp = ObjectWriter.NULL_PRETTY_PRINTER; 
/*   0*/       }
/*2452*/    return new ObjectWriter(this, getSerializationConfig(), null, pp);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writerWithDefaultPrettyPrinter() {
/*2460*/    return new ObjectWriter(this, getSerializationConfig(), null, _defaultPrettyPrinter());
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(FilterProvider filterProvider) {
/*2469*/    return new ObjectWriter(this, getSerializationConfig().withFilters(filterProvider));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(FormatSchema schema) {
/*2481*/    _verifySchemaType(schema);
/*2482*/    return new ObjectWriter(this, getSerializationConfig(), schema);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(Base64Variant defaultBase64) {
/*2492*/    return new ObjectWriter(this, getSerializationConfig().with(defaultBase64));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(CharacterEscapes escapes) {
/*2502*/    return writer().with(escapes);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectWriter writer(ContextAttributes attrs) {
/*2512*/    return new ObjectWriter(this, getSerializationConfig().with(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader() {
/*2528*/    return new ObjectReader(this, getDeserializationConfig()).with(this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(DeserializationFeature feature) {
/*2540*/    return new ObjectReader(this, getDeserializationConfig().with(feature));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(DeserializationFeature first, DeserializationFeature... other) {
/*2552*/    return new ObjectReader(this, getDeserializationConfig().with(first, other));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader readerForUpdating(Object valueToUpdate) {
/*2567*/    JavaType t = this._typeFactory.constructType(valueToUpdate.getClass());
/*2568*/    return new ObjectReader(this, getDeserializationConfig(), t, valueToUpdate, null, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(JavaType type) {
/*2578*/    return new ObjectReader(this, getDeserializationConfig(), type, null, null, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(Class<?> type) {
/*2588*/    return reader(this._typeFactory.constructType(type));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(TypeReference<?> type) {
/*2597*/    return reader(this._typeFactory.constructType(type));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(JsonNodeFactory f) {
/*2606*/    return new ObjectReader(this, getDeserializationConfig()).with(f);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(FormatSchema schema) {
/*2617*/    _verifySchemaType(schema);
/*2618*/    return new ObjectReader(this, getDeserializationConfig(), null, null, schema, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(InjectableValues injectableValues) {
/*2629*/    return new ObjectReader(this, getDeserializationConfig(), null, null, null, injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader readerWithView(Class<?> view) {
/*2638*/    return new ObjectReader(this, getDeserializationConfig().withView(view));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(Base64Variant defaultBase64) {
/*2648*/    return new ObjectReader(this, getDeserializationConfig().with(defaultBase64));
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectReader reader(ContextAttributes attrs) {
/*2658*/    return new ObjectReader(this, getDeserializationConfig().with(attrs));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
/*2684*/    if (fromValue == null) {
/*2684*/        return null; 
/*   0*/       }
/*2685*/    return (T)_convert(fromValue, this._typeFactory.constructType(toValueType));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T convertValue(Object fromValue, TypeReference<?> toValueTypeRef) throws IllegalArgumentException {
/*2692*/    return convertValue(fromValue, this._typeFactory.constructType(toValueTypeRef));
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T convertValue(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
/*2700*/    if (fromValue == null) {
/*2700*/        return null; 
/*   0*/       }
/*2701*/    return (T)_convert(fromValue, toValueType);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _convert(Object fromValue, JavaType toValueType) throws IllegalArgumentException {
/*2721*/    Class<?> targetType = toValueType.getRawClass();
/*2722*/    if (targetType != Object.class && !toValueType.hasGenericTypes() && targetType.isAssignableFrom(fromValue.getClass())) {
/*2725*/        return fromValue; 
/*   0*/       }
/*2731*/    TokenBuffer buf = new TokenBuffer(this, false);
/*   0*/    try {
/*   0*/      Object result;
/*2735*/      SerializationConfig config = getSerializationConfig().without(SerializationFeature.WRAP_ROOT_VALUE);
/*2737*/      _serializerProvider(config).serializeValue(buf, fromValue);
/*2740*/      JsonParser jp = buf.asParser();
/*2743*/      DeserializationConfig deserConfig = getDeserializationConfig();
/*2744*/      JsonToken t = _initForReading(jp);
/*2745*/      if (t == JsonToken.VALUE_NULL) {
/*2746*/        DeserializationContext ctxt = createDeserializationContext(jp, deserConfig);
/*2747*/        result = _findRootDeserializer(ctxt, toValueType).getNullValue();
/*2748*/      } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*2749*/        result = null;
/*   0*/      } else {
/*2751*/        DeserializationContext ctxt = createDeserializationContext(jp, deserConfig);
/*2752*/        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, toValueType);
/*2754*/        result = deser.deserialize(jp, ctxt);
/*   0*/      } 
/*2756*/      jp.close();
/*2757*/      return result;
/*2758*/    } catch (IOException e) {
/*2759*/      throw new IllegalArgumentException(e.getMessage(), e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public JsonSchema generateJsonSchema(Class<?> t) throws JsonMappingException {
/*2779*/    return _serializerProvider(getSerializationConfig()).generateJsonSchema(t);
/*   0*/  }
/*   0*/  
/*   0*/  public void acceptJsonFormatVisitor(Class<?> type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/*2796*/    acceptJsonFormatVisitor(this._typeFactory.constructType(type), visitor);
/*   0*/  }
/*   0*/  
/*   0*/  public void acceptJsonFormatVisitor(JavaType type, JsonFormatVisitorWrapper visitor) throws JsonMappingException {
/*2814*/    if (type == null) {
/*2815*/        throw new IllegalArgumentException("type must be provided"); 
/*   0*/       }
/*2817*/    _serializerProvider(getSerializationConfig()).acceptJsonFormatVisitor(type, visitor);
/*   0*/  }
/*   0*/  
/*   0*/  protected DefaultSerializerProvider _serializerProvider(SerializationConfig config) {
/*2831*/    return this._serializerProvider.createInstance(config, this._serializerFactory);
/*   0*/  }
/*   0*/  
/*   0*/  protected PrettyPrinter _defaultPrettyPrinter() {
/*2840*/    return _defaultPrettyPrinter;
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _configAndWriteValue(JsonGenerator jgen, Object value) throws IOException, JsonGenerationException, JsonMappingException {
/*2850*/    SerializationConfig cfg = getSerializationConfig();
/*2852*/    if (cfg.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
/*2853*/        jgen.useDefaultPrettyPrinter(); 
/*   0*/       }
/*2856*/    if (cfg.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)) {
/*2857*/        jgen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN); 
/*   0*/       }
/*2860*/    if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/*2861*/      _configAndWriteCloseable(jgen, value, cfg);
/*   0*/      return;
/*   0*/    } 
/*   0*/    boolean closed = false;
/*   0*/    try {
/*2866*/      _serializerProvider(cfg).serializeValue(jgen, value);
/*2867*/      closed = true;
/*2868*/      jgen.close();
/*   0*/    } finally {
/*2873*/      if (!closed) {
/*2877*/        jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*   0*/        try {
/*2879*/          jgen.close();
/*2880*/        } catch (IOException iOException) {}
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected final void _configAndWriteValue(JsonGenerator jgen, Object value, Class<?> viewClass) throws IOException, JsonGenerationException, JsonMappingException {
/*2888*/    SerializationConfig cfg = getSerializationConfig().withView(viewClass);
/*2889*/    if (cfg.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
/*2890*/        jgen.useDefaultPrettyPrinter(); 
/*   0*/       }
/*2893*/    if (cfg.isEnabled(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)) {
/*2894*/        jgen.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN); 
/*   0*/       }
/*2898*/    if (cfg.isEnabled(SerializationFeature.CLOSE_CLOSEABLE) && value instanceof Closeable) {
/*2899*/      _configAndWriteCloseable(jgen, value, cfg);
/*   0*/      return;
/*   0*/    } 
/*   0*/    boolean closed = false;
/*   0*/    try {
/*2904*/      _serializerProvider(cfg).serializeValue(jgen, value);
/*2905*/      closed = true;
/*2906*/      jgen.close();
/*   0*/    } finally {
/*2908*/      if (!closed) {
/*2911*/        jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*   0*/        try {
/*2913*/          jgen.close();
/*2914*/        } catch (IOException iOException) {}
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _configAndWriteCloseable(JsonGenerator jgen, Object value, SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
/*2926*/    Closeable toClose = (Closeable)value;
/*   0*/    try {
/*2928*/      _serializerProvider(cfg).serializeValue(jgen, value);
/*2929*/      JsonGenerator tmpJgen = jgen;
/*2930*/      jgen = null;
/*2931*/      tmpJgen.close();
/*2932*/      Closeable tmpToClose = toClose;
/*2933*/      toClose = null;
/*2934*/      tmpToClose.close();
/*   0*/    } finally {
/*2939*/      if (jgen != null) {
/*2942*/        jgen.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
/*   0*/        try {
/*2944*/          jgen.close();
/*2945*/        } catch (IOException iOException) {}
/*   0*/      } 
/*2947*/      if (toClose != null) {
/*   0*/          try {
/*2949*/            toClose.close();
/*2950*/          } catch (IOException iOException) {} 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private final void _writeCloseableValue(JsonGenerator jgen, Object value, SerializationConfig cfg) throws IOException, JsonGenerationException, JsonMappingException {
/*2962*/    Closeable toClose = (Closeable)value;
/*   0*/    try {
/*2964*/      _serializerProvider(cfg).serializeValue(jgen, value);
/*2965*/      if (cfg.isEnabled(SerializationFeature.FLUSH_AFTER_WRITE_VALUE)) {
/*2966*/          jgen.flush(); 
/*   0*/         }
/*2968*/      Closeable tmpToClose = toClose;
/*2969*/      toClose = null;
/*2970*/      tmpToClose.close();
/*   0*/    } finally {
/*2972*/      if (toClose != null) {
/*   0*/          try {
/*2974*/            toClose.close();
/*2975*/          } catch (IOException iOException) {} 
/*   0*/         }
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected DefaultDeserializationContext createDeserializationContext(JsonParser jp, DeserializationConfig cfg) {
/*2994*/    return this._deserializationContext.createInstance(cfg, jp, this._injectableValues);
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _readValue(DeserializationConfig cfg, JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*   0*/    Object result;
/*3009*/    JsonToken t = _initForReading(jp);
/*3010*/    if (t == JsonToken.VALUE_NULL) {
/*3012*/      DeserializationContext ctxt = createDeserializationContext(jp, cfg);
/*3013*/      result = _findRootDeserializer(ctxt, valueType).getNullValue();
/*3014*/    } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*3015*/      result = null;
/*   0*/    } else {
/*3017*/      DeserializationContext ctxt = createDeserializationContext(jp, cfg);
/*3018*/      JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
/*3020*/      if (cfg.useRootWrapping()) {
/*3021*/        result = _unwrapAndDeserialize(jp, ctxt, cfg, valueType, deser);
/*   0*/      } else {
/*3023*/        result = deser.deserialize(jp, ctxt);
/*   0*/      } 
/*   0*/    } 
/*3027*/    jp.clearCurrentToken();
/*3028*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _readMapAndClose(JsonParser jp, JavaType valueType) throws IOException, JsonParseException, JsonMappingException {
/*   0*/    try {
/*   0*/      Object result;
/*3036*/      JsonToken t = _initForReading(jp);
/*3037*/      if (t == JsonToken.VALUE_NULL) {
/*3039*/        DeserializationContext ctxt = createDeserializationContext(jp, getDeserializationConfig());
/*3041*/        result = _findRootDeserializer(ctxt, valueType).getNullValue();
/*3042*/      } else if (t == JsonToken.END_ARRAY || t == JsonToken.END_OBJECT) {
/*3043*/        result = null;
/*   0*/      } else {
/*3045*/        DeserializationConfig cfg = getDeserializationConfig();
/*3046*/        DeserializationContext ctxt = createDeserializationContext(jp, cfg);
/*3047*/        JsonDeserializer<Object> deser = _findRootDeserializer(ctxt, valueType);
/*3048*/        if (cfg.useRootWrapping()) {
/*3049*/          result = _unwrapAndDeserialize(jp, ctxt, cfg, valueType, deser);
/*   0*/        } else {
/*3051*/          result = deser.deserialize(jp, ctxt);
/*   0*/        } 
/*3053*/        ctxt.checkUnresolvedObjectId();
/*   0*/      } 
/*3056*/      jp.clearCurrentToken();
/*3057*/      return result;
/*   0*/    } finally {
/*   0*/      try {
/*3060*/        jp.close();
/*3061*/      } catch (IOException iOException) {}
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonToken _initForReading(JsonParser jp) throws IOException, JsonParseException, JsonMappingException {
/*3087*/    JsonToken t = jp.getCurrentToken();
/*3088*/    if (t == null) {
/*3090*/      t = jp.nextToken();
/*3091*/      if (t == null) {
/*3095*/          throw JsonMappingException.from(jp, "No content to map due to end-of-input"); 
/*   0*/         }
/*   0*/    } 
/*3098*/    return t;
/*   0*/  }
/*   0*/  
/*   0*/  protected Object _unwrapAndDeserialize(JsonParser jp, DeserializationContext ctxt, DeserializationConfig config, JavaType rootType, JsonDeserializer<Object> deser) throws IOException, JsonParseException, JsonMappingException {
/*3106*/    String expName = config.getRootName();
/*3107*/    if (expName == null) {
/*3108*/      PropertyName pname = this._rootNames.findRootName(rootType, config);
/*3109*/      expName = pname.getSimpleName();
/*   0*/    } 
/*3111*/    if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
/*3112*/        throw JsonMappingException.from(jp, "Current token not START_OBJECT (needed to unwrap root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*3115*/    if (jp.nextToken() != JsonToken.FIELD_NAME) {
/*3116*/        throw JsonMappingException.from(jp, "Current token not FIELD_NAME (to contain expected root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*3119*/    String actualName = jp.getCurrentName();
/*3120*/    if (!expName.equals(actualName)) {
/*3121*/        throw JsonMappingException.from(jp, "Root name '" + actualName + "' does not match expected ('" + expName + "') for type " + rootType); 
/*   0*/       }
/*3125*/    jp.nextToken();
/*3126*/    Object result = deser.deserialize(jp, ctxt);
/*3128*/    if (jp.nextToken() != JsonToken.END_OBJECT) {
/*3129*/        throw JsonMappingException.from(jp, "Current token not END_OBJECT (to match wrapper object with root name '" + expName + "'), but " + jp.getCurrentToken()); 
/*   0*/       }
/*3132*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected JsonDeserializer<Object> _findRootDeserializer(DeserializationContext ctxt, JavaType valueType) throws JsonMappingException {
/*3149*/    JsonDeserializer<Object> deser = this._rootDeserializers.get(valueType);
/*3150*/    if (deser != null) {
/*3151*/        return deser; 
/*   0*/       }
/*3154*/    deser = ctxt.findRootValueDeserializer(valueType);
/*3155*/    if (deser == null) {
/*3156*/        throw new JsonMappingException("Can not find a deserializer for type " + valueType); 
/*   0*/       }
/*3158*/    this._rootDeserializers.put(valueType, deser);
/*3159*/    return deser;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _verifySchemaType(FormatSchema schema) {
/*3167*/    if (schema != null && 
/*3168*/      !this._jsonFactory.canUseSchema(schema)) {
/*3169*/        throw new IllegalArgumentException("Can not use FormatSchema of type " + schema.getClass().getName() + " for format " + this._jsonFactory.getFormatName()); 
/*   0*/       }
/*   0*/  }
/*   0*/}
