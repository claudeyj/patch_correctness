/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.core.FormatFeature;
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.JsonParser;
/*   0*/import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*   0*/import com.fasterxml.jackson.databind.cfg.ConfigOverrides;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
/*   0*/import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.node.JsonNodeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.LinkedNode;
/*   0*/import com.fasterxml.jackson.databind.util.RootNameLookup;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Collection;
/*   0*/
/*   0*/public final class DeserializationConfig extends MapperConfigBase<DeserializationFeature, DeserializationConfig> implements Serializable {
/*   0*/  private static final long serialVersionUID = 2L;
/*   0*/  
/*   0*/  protected final LinkedNode<DeserializationProblemHandler> _problemHandlers;
/*   0*/  
/*   0*/  protected final JsonNodeFactory _nodeFactory;
/*   0*/  
/*   0*/  protected final int _deserFeatures;
/*   0*/  
/*   0*/  protected final int _parserFeatures;
/*   0*/  
/*   0*/  protected final int _parserFeaturesToChange;
/*   0*/  
/*   0*/  protected final int _formatReadFeatures;
/*   0*/  
/*   0*/  protected final int _formatReadFeaturesToChange;
/*   0*/  
/*   0*/  public DeserializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 103*/    super(base, str, mixins, rootNames, configOverrides);
/* 104*/    this._deserFeatures = collectFeatureDefaults(DeserializationFeature.class);
/* 105*/    this._nodeFactory = JsonNodeFactory.instance;
/* 106*/    this._problemHandlers = null;
/* 107*/    this._parserFeatures = 0;
/* 108*/    this._parserFeaturesToChange = 0;
/* 109*/    this._formatReadFeatures = 0;
/* 110*/    this._formatReadFeaturesToChange = 0;
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames, ConfigOverrides configOverrides) {
/* 122*/    super(src, mixins, rootNames, configOverrides);
/* 123*/    this._deserFeatures = src._deserFeatures;
/* 124*/    this._problemHandlers = src._problemHandlers;
/* 125*/    this._nodeFactory = src._nodeFactory;
/* 126*/    this._parserFeatures = src._parserFeatures;
/* 127*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 128*/    this._formatReadFeatures = src._formatReadFeatures;
/* 129*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, int mapperFeatures, int deserFeatures, int parserFeatures, int parserFeatureMask, int formatFeatures, int formatFeatureMask) {
/* 144*/    super(src, mapperFeatures);
/* 145*/    this._deserFeatures = deserFeatures;
/* 146*/    this._nodeFactory = src._nodeFactory;
/* 147*/    this._problemHandlers = src._problemHandlers;
/* 148*/    this._parserFeatures = parserFeatures;
/* 149*/    this._parserFeaturesToChange = parserFeatureMask;
/* 150*/    this._formatReadFeatures = formatFeatures;
/* 151*/    this._formatReadFeaturesToChange = formatFeatureMask;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, SubtypeResolver str) {
/* 160*/    super(src, str);
/* 161*/    this._deserFeatures = src._deserFeatures;
/* 162*/    this._nodeFactory = src._nodeFactory;
/* 163*/    this._problemHandlers = src._problemHandlers;
/* 164*/    this._parserFeatures = src._parserFeatures;
/* 165*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 166*/    this._formatReadFeatures = src._formatReadFeatures;
/* 167*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, BaseSettings base) {
/* 172*/    super(src, base);
/* 173*/    src._deserFeatures = src._deserFeatures;
/* 174*/    this._nodeFactory = src._nodeFactory;
/* 175*/    this._problemHandlers = src._problemHandlers;
/* 176*/    this._parserFeatures = src._parserFeatures;
/* 177*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 178*/    this._formatReadFeatures = src._formatReadFeatures;
/* 179*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, JsonNodeFactory f) {
/* 184*/    super(src);
/* 185*/    this._deserFeatures = src._deserFeatures;
/* 186*/    this._problemHandlers = src._problemHandlers;
/* 187*/    this._nodeFactory = f;
/* 188*/    this._parserFeatures = src._parserFeatures;
/* 189*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 190*/    this._formatReadFeatures = src._formatReadFeatures;
/* 191*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, LinkedNode<DeserializationProblemHandler> problemHandlers) {
/* 197*/    super(src);
/* 198*/    this._deserFeatures = src._deserFeatures;
/* 199*/    this._problemHandlers = problemHandlers;
/* 200*/    this._nodeFactory = src._nodeFactory;
/* 201*/    this._parserFeatures = src._parserFeatures;
/* 202*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 203*/    this._formatReadFeatures = src._formatReadFeatures;
/* 204*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, PropertyName rootName) {
/* 209*/    super(src, rootName);
/* 210*/    this._deserFeatures = src._deserFeatures;
/* 211*/    this._problemHandlers = src._problemHandlers;
/* 212*/    this._nodeFactory = src._nodeFactory;
/* 213*/    this._parserFeatures = src._parserFeatures;
/* 214*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 215*/    this._formatReadFeatures = src._formatReadFeatures;
/* 216*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private DeserializationConfig(DeserializationConfig src, Class<?> view) {
/* 221*/    super(src, view);
/* 222*/    this._deserFeatures = src._deserFeatures;
/* 223*/    this._problemHandlers = src._problemHandlers;
/* 224*/    this._nodeFactory = src._nodeFactory;
/* 225*/    this._parserFeatures = src._parserFeatures;
/* 226*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 227*/    this._formatReadFeatures = src._formatReadFeatures;
/* 228*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationConfig(DeserializationConfig src, ContextAttributes attrs) {
/* 233*/    super(src, attrs);
/* 234*/    this._deserFeatures = src._deserFeatures;
/* 235*/    this._problemHandlers = src._problemHandlers;
/* 236*/    this._nodeFactory = src._nodeFactory;
/* 237*/    this._parserFeatures = src._parserFeatures;
/* 238*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 239*/    this._formatReadFeatures = src._formatReadFeatures;
/* 240*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected DeserializationConfig(DeserializationConfig src, SimpleMixInResolver mixins) {
/* 245*/    super(src, mixins);
/* 246*/    this._deserFeatures = src._deserFeatures;
/* 247*/    this._problemHandlers = src._problemHandlers;
/* 248*/    this._nodeFactory = src._nodeFactory;
/* 249*/    this._parserFeatures = src._parserFeatures;
/* 250*/    this._parserFeaturesToChange = src._parserFeaturesToChange;
/* 251*/    this._formatReadFeatures = src._formatReadFeatures;
/* 252*/    this._formatReadFeaturesToChange = src._formatReadFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected BaseSettings getBaseSettings() {
/* 256*/    return this._base;
/*   0*/  }
/*   0*/  
/*   0*/  protected final DeserializationConfig _withBase(BaseSettings newBase) {
/* 266*/    return (this._base == newBase) ? this : new DeserializationConfig(this, newBase);
/*   0*/  }
/*   0*/  
/*   0*/  protected final DeserializationConfig _withMapperFeatures(int mapperFeatures) {
/* 271*/    return new DeserializationConfig(this, mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(SubtypeResolver str) {
/* 284*/    return (this._subtypeResolver == str) ? this : new DeserializationConfig(this, str);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withRootName(PropertyName rootName) {
/* 289*/    if (rootName == null) {
/* 290*/      if (this._rootName == null) {
/* 291*/          return this; 
/*   0*/         }
/* 293*/    } else if (rootName.equals(this._rootName)) {
/* 294*/      return this;
/*   0*/    } 
/* 296*/    return new DeserializationConfig(this, rootName);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withView(Class<?> view) {
/* 301*/    return (this._view == view) ? this : new DeserializationConfig(this, view);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(ContextAttributes attrs) {
/* 306*/    return (attrs == this._attributes) ? this : new DeserializationConfig(this, attrs);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(DeserializationFeature feature) {
/* 321*/    int newDeserFeatures = this._deserFeatures | feature.getMask();
/* 322*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(DeserializationFeature first, DeserializationFeature... features) {
/* 335*/    int newDeserFeatures = this._deserFeatures | first.getMask();
/* 336*/    for (DeserializationFeature f : features) {
/* 337*/        newDeserFeatures |= f.getMask(); 
/*   0*/       }
/* 339*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withFeatures(DeserializationFeature... features) {
/* 351*/    int newDeserFeatures = this._deserFeatures;
/* 352*/    for (DeserializationFeature f : features) {
/* 353*/        newDeserFeatures |= f.getMask(); 
/*   0*/       }
/* 355*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig without(DeserializationFeature feature) {
/* 367*/    int newDeserFeatures = this._deserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 368*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig without(DeserializationFeature first, DeserializationFeature... features) {
/* 381*/    int newDeserFeatures = this._deserFeatures & (first.getMask() ^ 0xFFFFFFFF);
/* 382*/    for (DeserializationFeature f : features) {
/* 383*/        newDeserFeatures &= f.getMask() ^ 0xFFFFFFFF; 
/*   0*/       }
/* 385*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withoutFeatures(DeserializationFeature... features) {
/* 397*/    int newDeserFeatures = this._deserFeatures;
/* 398*/    for (DeserializationFeature f : features) {
/* 399*/        newDeserFeatures &= f.getMask() ^ 0xFFFFFFFF; 
/*   0*/       }
/* 401*/    return (newDeserFeatures == this._deserFeatures) ? this : new DeserializationConfig(this, this._mapperFeatures, newDeserFeatures, this._parserFeatures, this._parserFeaturesToChange, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(JsonParser.Feature feature) {
/* 421*/    int newSet = this._parserFeatures | feature.getMask();
/* 422*/    int newMask = this._parserFeaturesToChange | feature.getMask();
/* 423*/    return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withFeatures(JsonParser.Feature... features) {
/* 437*/    int newSet = this._parserFeatures;
/* 438*/    int newMask = this._parserFeaturesToChange;
/* 439*/    for (JsonParser.Feature f : features) {
/* 440*/      int mask = f.getMask();
/* 441*/      newSet |= mask;
/* 442*/      newMask |= mask;
/*   0*/    } 
/* 444*/    return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig without(JsonParser.Feature feature) {
/* 458*/    int newSet = this._parserFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 459*/    int newMask = this._parserFeaturesToChange | feature.getMask();
/* 460*/    return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withoutFeatures(JsonParser.Feature... features) {
/* 474*/    int newSet = this._parserFeatures;
/* 475*/    int newMask = this._parserFeaturesToChange;
/* 476*/    for (JsonParser.Feature f : features) {
/* 477*/      int mask = f.getMask();
/* 478*/      newSet &= mask ^ 0xFFFFFFFF;
/* 479*/      newMask |= mask;
/*   0*/    } 
/* 481*/    return (this._parserFeatures == newSet && this._parserFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, newSet, newMask, this._formatReadFeatures, this._formatReadFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(FormatFeature feature) {
/* 501*/    int newSet = this._formatReadFeatures | feature.getMask();
/* 502*/    int newMask = this._formatReadFeaturesToChange | feature.getMask();
/* 503*/    return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withFeatures(FormatFeature... features) {
/* 517*/    int newSet = this._formatReadFeatures;
/* 518*/    int newMask = this._formatReadFeaturesToChange;
/* 519*/    for (FormatFeature f : features) {
/* 520*/      int mask = f.getMask();
/* 521*/      newSet |= mask;
/* 522*/      newMask |= mask;
/*   0*/    } 
/* 524*/    return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig without(FormatFeature feature) {
/* 538*/    int newSet = this._formatReadFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 539*/    int newMask = this._formatReadFeaturesToChange | feature.getMask();
/* 540*/    return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withoutFeatures(FormatFeature... features) {
/* 554*/    int newSet = this._formatReadFeatures;
/* 555*/    int newMask = this._formatReadFeaturesToChange;
/* 556*/    for (FormatFeature f : features) {
/* 557*/      int mask = f.getMask();
/* 558*/      newSet &= mask ^ 0xFFFFFFFF;
/* 559*/      newMask |= mask;
/*   0*/    } 
/* 561*/    return (this._formatReadFeatures == newSet && this._formatReadFeaturesToChange == newMask) ? this : new DeserializationConfig(this, this._mapperFeatures, this._deserFeatures, this._parserFeatures, this._parserFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig with(JsonNodeFactory f) {
/* 578*/    if (this._nodeFactory == f) {
/* 579*/        return this; 
/*   0*/       }
/* 581*/    return new DeserializationConfig(this, f);
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withHandler(DeserializationProblemHandler h) {
/* 591*/    if (LinkedNode.contains(this._problemHandlers, h)) {
/* 592*/        return this; 
/*   0*/       }
/* 594*/    return new DeserializationConfig(this, new LinkedNode<>(h, this._problemHandlers));
/*   0*/  }
/*   0*/  
/*   0*/  public DeserializationConfig withNoProblemHandlers() {
/* 603*/    if (this._problemHandlers == null) {
/* 604*/        return this; 
/*   0*/       }
/* 606*/    return new DeserializationConfig(this, (LinkedNode<DeserializationProblemHandler>)null);
/*   0*/  }
/*   0*/  
/*   0*/  public void initialize(JsonParser p) {
/* 624*/    if (this._parserFeaturesToChange != 0) {
/* 625*/        p.overrideStdFeatures(this._parserFeatures, this._parserFeaturesToChange); 
/*   0*/       }
/* 627*/    if (this._formatReadFeaturesToChange != 0) {
/* 628*/        p.overrideFormatFeatures(this._formatReadFeatures, this._formatReadFeaturesToChange); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public boolean useRootWrapping() {
/* 641*/    if (this._rootName != null) {
/* 642*/        return !this._rootName.isEmpty(); 
/*   0*/       }
/* 644*/    return isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(DeserializationFeature f) {
/* 648*/    return ((this._deserFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonParser.Feature f, JsonFactory factory) {
/* 652*/    int mask = f.getMask();
/* 653*/    if ((this._parserFeaturesToChange & mask) != 0) {
/* 654*/        return ((this._parserFeatures & f.getMask()) != 0); 
/*   0*/       }
/* 656*/    return factory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasDeserializationFeatures(int featureMask) {
/* 666*/    return ((this._deserFeatures & featureMask) == featureMask);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasSomeOfFeatures(int featureMask) {
/* 676*/    return ((this._deserFeatures & featureMask) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final int getDeserializationFeatures() {
/* 684*/    return this._deserFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean requiresFullValue() {
/* 696*/    return DeserializationFeature.FAIL_ON_TRAILING_TOKENS.enabledIn(this._deserFeatures);
/*   0*/  }
/*   0*/  
/*   0*/  public LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
/* 710*/    return this._problemHandlers;
/*   0*/  }
/*   0*/  
/*   0*/  public final JsonNodeFactory getNodeFactory() {
/* 714*/    return this._nodeFactory;
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends BeanDescription> T introspect(JavaType type) {
/* 731*/    return (T)getClassIntrospector().forDeserialization(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends BeanDescription> T introspectForCreation(JavaType type) {
/* 740*/    return (T)getClassIntrospector().forCreation(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends BeanDescription> T introspectForBuilder(JavaType type) {
/* 748*/    return (T)getClassIntrospector().forDeserializationWithBuilder(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public TypeDeserializer findTypeDeserializer(JavaType baseType) throws JsonMappingException {
/* 767*/    BeanDescription bean = introspectClassAnnotations(baseType.getRawClass());
/* 768*/    AnnotatedClass ac = bean.getClassInfo();
/* 769*/    TypeResolverBuilder<?> b = getAnnotationIntrospector().findTypeResolver(this, ac, baseType);
/* 774*/    Collection<NamedType> subtypes = null;
/* 775*/    if (b == null) {
/* 776*/      b = getDefaultTyper(baseType);
/* 777*/      if (b == null) {
/* 778*/          return null; 
/*   0*/         }
/*   0*/    } else {
/* 781*/      subtypes = getSubtypeResolver().collectAndResolveSubtypesByTypeId(this, ac);
/*   0*/    } 
/* 783*/    return b.buildTypeDeserializer(this, baseType, subtypes);
/*   0*/  }
/*   0*/}
