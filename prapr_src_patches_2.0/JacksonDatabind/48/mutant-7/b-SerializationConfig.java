/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JsonAutoDetect;
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.annotation.JsonInclude;
/*   0*/import com.fasterxml.jackson.annotation.PropertyAccessor;
/*   0*/import com.fasterxml.jackson.core.Base64Variant;
/*   0*/import com.fasterxml.jackson.core.FormatFeature;
/*   0*/import com.fasterxml.jackson.core.JsonFactory;
/*   0*/import com.fasterxml.jackson.core.JsonGenerator;
/*   0*/import com.fasterxml.jackson.core.PrettyPrinter;
/*   0*/import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
/*   0*/import com.fasterxml.jackson.core.util.Instantiatable;
/*   0*/import com.fasterxml.jackson.databind.cfg.BaseSettings;
/*   0*/import com.fasterxml.jackson.databind.cfg.ContextAttributes;
/*   0*/import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfigBase;
/*   0*/import com.fasterxml.jackson.databind.introspect.ClassIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.SimpleMixInResolver;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.ser.FilterProvider;
/*   0*/import com.fasterxml.jackson.databind.type.TypeFactory;
/*   0*/import com.fasterxml.jackson.databind.util.RootNameLookup;
/*   0*/import java.io.Serializable;
/*   0*/import java.text.DateFormat;
/*   0*/import java.util.Locale;
/*   0*/import java.util.TimeZone;
/*   0*/
/*   0*/public final class SerializationConfig extends MapperConfigBase<SerializationFeature, SerializationConfig> implements Serializable {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  44*/  protected static final PrettyPrinter DEFAULT_PRETTY_PRINTER = (PrettyPrinter)new DefaultPrettyPrinter();
/*   0*/  
/*  48*/  protected static final JsonInclude.Value DEFAULT_INCLUSION = JsonInclude.Value.empty();
/*   0*/  
/*   0*/  protected final FilterProvider _filterProvider;
/*   0*/  
/*   0*/  protected final PrettyPrinter _defaultPrettyPrinter;
/*   0*/  
/*   0*/  protected final int _serFeatures;
/*   0*/  
/*   0*/  protected final int _generatorFeatures;
/*   0*/  
/*   0*/  protected final int _generatorFeaturesToChange;
/*   0*/  
/*   0*/  protected final int _formatWriteFeatures;
/*   0*/  
/*   0*/  protected final int _formatWriteFeaturesToChange;
/*   0*/  
/*   0*/  protected final JsonInclude.Value _serializationInclusion;
/*   0*/  
/*   0*/  public SerializationConfig(BaseSettings base, SubtypeResolver str, SimpleMixInResolver mixins, RootNameLookup rootNames) {
/* 139*/    super(base, str, mixins, rootNames);
/* 140*/    this._serFeatures = collectFeatureDefaults(SerializationFeature.class);
/* 141*/    this._filterProvider = null;
/* 142*/    this._defaultPrettyPrinter = DEFAULT_PRETTY_PRINTER;
/* 143*/    this._generatorFeatures = 0;
/* 144*/    this._generatorFeaturesToChange = 0;
/* 145*/    this._formatWriteFeatures = 0;
/* 146*/    this._formatWriteFeaturesToChange = 0;
/* 147*/    this._serializationInclusion = DEFAULT_INCLUSION;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, SubtypeResolver str) {
/* 152*/    super(src, str);
/* 153*/    this._serFeatures = src._serFeatures;
/* 154*/    this._serializationInclusion = src._serializationInclusion;
/* 155*/    this._filterProvider = src._filterProvider;
/* 156*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 157*/    this._generatorFeatures = src._generatorFeatures;
/* 158*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 159*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 160*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, int mapperFeatures, int serFeatures, int generatorFeatures, int generatorFeatureMask, int formatFeatures, int formatFeaturesMask) {
/* 168*/    super(src, mapperFeatures);
/* 169*/    this._serFeatures = serFeatures;
/* 170*/    this._serializationInclusion = src._serializationInclusion;
/* 171*/    this._filterProvider = src._filterProvider;
/* 172*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 173*/    this._generatorFeatures = generatorFeatures;
/* 174*/    this._generatorFeaturesToChange = generatorFeatureMask;
/* 175*/    this._formatWriteFeatures = formatFeatures;
/* 176*/    this._formatWriteFeaturesToChange = formatFeaturesMask;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, BaseSettings base) {
/* 181*/    super(src, base);
/* 182*/    this._serFeatures = src._serFeatures;
/* 183*/    this._serializationInclusion = src._serializationInclusion;
/* 184*/    this._filterProvider = src._filterProvider;
/* 185*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 186*/    this._generatorFeatures = src._generatorFeatures;
/* 187*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 188*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 189*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, FilterProvider filters) {
/* 194*/    super(src);
/* 195*/    this._serFeatures = src._serFeatures;
/* 196*/    this._serializationInclusion = src._serializationInclusion;
/* 197*/    this._filterProvider = filters;
/* 198*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 199*/    this._generatorFeatures = src._generatorFeatures;
/* 200*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 201*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 202*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, Class<?> view) {
/* 207*/    super(src, view);
/* 208*/    this._serFeatures = src._serFeatures;
/* 209*/    this._serializationInclusion = src._serializationInclusion;
/* 210*/    this._filterProvider = src._filterProvider;
/* 211*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 212*/    this._generatorFeatures = src._generatorFeatures;
/* 213*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 214*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 215*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, JsonInclude.Value incl) {
/* 220*/    super(src);
/* 221*/    this._serFeatures = src._serFeatures;
/* 222*/    this._serializationInclusion = incl;
/* 223*/    this._filterProvider = src._filterProvider;
/* 224*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 225*/    this._generatorFeatures = src._generatorFeatures;
/* 226*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 227*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 228*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  private SerializationConfig(SerializationConfig src, PropertyName rootName) {
/* 233*/    super(src, rootName);
/* 234*/    this._serFeatures = src._serFeatures;
/* 235*/    this._serializationInclusion = src._serializationInclusion;
/* 236*/    this._filterProvider = src._filterProvider;
/* 237*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 238*/    this._generatorFeatures = src._generatorFeatures;
/* 239*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 240*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 241*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected SerializationConfig(SerializationConfig src, ContextAttributes attrs) {
/* 249*/    super(src, attrs);
/* 250*/    this._serFeatures = src._serFeatures;
/* 251*/    this._serializationInclusion = src._serializationInclusion;
/* 252*/    this._filterProvider = src._filterProvider;
/* 253*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 254*/    this._generatorFeatures = src._generatorFeatures;
/* 255*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 256*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 257*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins) {
/* 265*/    super(src, mixins);
/* 266*/    this._serFeatures = src._serFeatures;
/* 267*/    this._serializationInclusion = src._serializationInclusion;
/* 268*/    this._filterProvider = src._filterProvider;
/* 269*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 270*/    this._generatorFeatures = src._generatorFeatures;
/* 271*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 272*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 273*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected SerializationConfig(SerializationConfig src, PrettyPrinter defaultPP) {
/* 281*/    super(src);
/* 282*/    this._serFeatures = src._serFeatures;
/* 283*/    this._serializationInclusion = src._serializationInclusion;
/* 284*/    this._filterProvider = src._filterProvider;
/* 285*/    this._defaultPrettyPrinter = defaultPP;
/* 286*/    this._generatorFeatures = src._generatorFeatures;
/* 287*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 288*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 289*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  protected SerializationConfig(SerializationConfig src, SimpleMixInResolver mixins, RootNameLookup rootNames) {
/* 301*/    super(src, mixins, rootNames);
/* 302*/    this._serFeatures = src._serFeatures;
/* 303*/    this._serializationInclusion = src._serializationInclusion;
/* 304*/    this._filterProvider = src._filterProvider;
/* 305*/    this._defaultPrettyPrinter = src._defaultPrettyPrinter;
/* 306*/    this._generatorFeatures = src._generatorFeatures;
/* 307*/    this._generatorFeaturesToChange = src._generatorFeaturesToChange;
/* 308*/    this._formatWriteFeatures = src._formatWriteFeatures;
/* 309*/    this._formatWriteFeaturesToChange = src._formatWriteFeaturesToChange;
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(MapperFeature... features) {
/* 325*/    int newMapperFlags = this._mapperFeatures;
/* 326*/    for (MapperFeature f : features) {
/* 327*/        newMapperFlags |= f.getMask(); 
/*   0*/       }
/* 329*/    return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig without(MapperFeature... features) {
/* 342*/    int newMapperFlags = this._mapperFeatures;
/* 343*/    for (MapperFeature f : features) {
/* 344*/        newMapperFlags &= f.getMask() ^ 0xFFFFFFFF; 
/*   0*/       }
/* 346*/    return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(MapperFeature feature, boolean state) {
/*   0*/    int newMapperFlags;
/* 356*/    if (state) {
/* 357*/      newMapperFlags = this._mapperFeatures | feature.getMask();
/*   0*/    } else {
/* 359*/      newMapperFlags = this._mapperFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/*   0*/    } 
/* 361*/    return (newMapperFlags == this._mapperFeatures) ? this : new SerializationConfig(this, newMapperFlags, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(AnnotationIntrospector ai) {
/* 369*/    return _withBase(this._base.withAnnotationIntrospector(ai));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 374*/    return _withBase(this._base.withAppendedAnnotationIntrospector(ai));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
/* 379*/    return _withBase(this._base.withInsertedAnnotationIntrospector(ai));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(ClassIntrospector ci) {
/* 384*/    return _withBase(this._base.withClassIntrospector(ci));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(DateFormat df) {
/* 394*/    SerializationConfig cfg = new SerializationConfig(this, this._base.withDateFormat(df));
/* 396*/    if (df == null) {
/* 397*/      cfg = cfg.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*   0*/    } else {
/* 399*/      cfg = cfg.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
/*   0*/    } 
/* 401*/    return cfg;
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(HandlerInstantiator hi) {
/* 406*/    return _withBase(this._base.withHandlerInstantiator(hi));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(PropertyNamingStrategy pns) {
/* 411*/    return _withBase(this._base.withPropertyNamingStrategy(pns));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withRootName(PropertyName rootName) {
/* 416*/    if (rootName == null) {
/* 417*/      if (this._rootName == null) {
/* 418*/          return this; 
/*   0*/         }
/* 420*/    } else if (rootName.equals(this._rootName)) {
/* 421*/      return this;
/*   0*/    } 
/* 423*/    return new SerializationConfig(this, rootName);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(SubtypeResolver str) {
/* 428*/    return (str == this._subtypeResolver) ? this : new SerializationConfig(this, str);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(TypeFactory tf) {
/* 433*/    return _withBase(this._base.withTypeFactory(tf));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(TypeResolverBuilder<?> trb) {
/* 438*/    return _withBase(this._base.withTypeResolverBuilder(trb));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withView(Class<?> view) {
/* 443*/    return (this._view == view) ? this : new SerializationConfig(this, view);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(VisibilityChecker<?> vc) {
/* 448*/    return _withBase(this._base.withVisibilityChecker(vc));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withVisibility(PropertyAccessor forMethod, JsonAutoDetect.Visibility visibility) {
/* 453*/    return _withBase(this._base.withVisibility(forMethod, visibility));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(Locale l) {
/* 458*/    return _withBase(this._base.with(l));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(TimeZone tz) {
/* 463*/    return _withBase(this._base.with(tz));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(Base64Variant base64) {
/* 468*/    return _withBase(this._base.with(base64));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(ContextAttributes attrs) {
/* 473*/    return (attrs == this._attributes) ? this : new SerializationConfig(this, attrs);
/*   0*/  }
/*   0*/  
/*   0*/  private final SerializationConfig _withBase(BaseSettings newBase) {
/* 477*/    return (this._base == newBase) ? this : new SerializationConfig(this, newBase);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(SerializationFeature feature) {
/* 492*/    int newSerFeatures = this._serFeatures | feature.getMask();
/* 493*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(SerializationFeature first, SerializationFeature... features) {
/* 505*/    int newSerFeatures = this._serFeatures | first.getMask();
/* 506*/    for (SerializationFeature f : features) {
/* 507*/        newSerFeatures |= f.getMask(); 
/*   0*/       }
/* 509*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withFeatures(SerializationFeature... features) {
/* 521*/    int newSerFeatures = this._serFeatures;
/* 522*/    for (SerializationFeature f : features) {
/* 523*/        newSerFeatures |= f.getMask(); 
/*   0*/       }
/* 525*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig without(SerializationFeature feature) {
/* 537*/    int newSerFeatures = this._serFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 538*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig without(SerializationFeature first, SerializationFeature... features) {
/* 550*/    int newSerFeatures = this._serFeatures & (first.getMask() ^ 0xFFFFFFFF);
/* 551*/    for (SerializationFeature f : features) {
/* 552*/        newSerFeatures &= f.getMask() ^ 0xFFFFFFFF; 
/*   0*/       }
/* 554*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withoutFeatures(SerializationFeature... features) {
/* 566*/    int newSerFeatures = this._serFeatures;
/* 567*/    for (SerializationFeature f : features) {
/* 568*/        newSerFeatures &= f.getMask() ^ 0xFFFFFFFF; 
/*   0*/       }
/* 570*/    return (newSerFeatures == this._serFeatures) ? this : new SerializationConfig(this, this._mapperFeatures, newSerFeatures, this._generatorFeatures, this._generatorFeaturesToChange, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(JsonGenerator.Feature feature) {
/* 589*/    int newSet = this._generatorFeatures | feature.getMask();
/* 590*/    int newMask = this._generatorFeaturesToChange | feature.getMask();
/* 591*/    return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withFeatures(JsonGenerator.Feature... features) {
/* 605*/    int newSet = this._generatorFeatures;
/* 606*/    int newMask = this._generatorFeaturesToChange;
/* 607*/    for (JsonGenerator.Feature f : features) {
/* 608*/      int mask = f.getMask();
/* 609*/      newSet |= mask;
/* 610*/      newMask |= mask;
/*   0*/    } 
/* 612*/    return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig without(JsonGenerator.Feature feature) {
/* 626*/    int newSet = this._generatorFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 627*/    int newMask = this._generatorFeaturesToChange | feature.getMask();
/* 628*/    return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withoutFeatures(JsonGenerator.Feature... features) {
/* 642*/    int newSet = this._generatorFeatures;
/* 643*/    int newMask = this._generatorFeaturesToChange;
/* 644*/    for (JsonGenerator.Feature f : features) {
/* 645*/      int mask = f.getMask();
/* 646*/      newSet &= mask ^ 0xFFFFFFFF;
/* 647*/      newMask |= mask;
/*   0*/    } 
/* 649*/    return (this._generatorFeatures == newSet && this._generatorFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, newSet, newMask, this._formatWriteFeatures, this._formatWriteFeaturesToChange);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig with(FormatFeature feature) {
/* 668*/    int newSet = this._formatWriteFeatures | feature.getMask();
/* 669*/    int newMask = this._formatWriteFeaturesToChange | feature.getMask();
/* 670*/    return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withFeatures(FormatFeature... features) {
/* 684*/    int newSet = this._formatWriteFeatures;
/* 685*/    int newMask = this._formatWriteFeaturesToChange;
/* 686*/    for (FormatFeature f : features) {
/* 687*/      int mask = f.getMask();
/* 688*/      newSet |= mask;
/* 689*/      newMask |= mask;
/*   0*/    } 
/* 691*/    return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig without(FormatFeature feature) {
/* 705*/    int newSet = this._formatWriteFeatures & (feature.getMask() ^ 0xFFFFFFFF);
/* 706*/    int newMask = this._formatWriteFeaturesToChange | feature.getMask();
/* 707*/    return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withoutFeatures(FormatFeature... features) {
/* 721*/    int newSet = this._formatWriteFeatures;
/* 722*/    int newMask = this._formatWriteFeaturesToChange;
/* 723*/    for (FormatFeature f : features) {
/* 724*/      int mask = f.getMask();
/* 725*/      newSet &= mask ^ 0xFFFFFFFF;
/* 726*/      newMask |= mask;
/*   0*/    } 
/* 728*/    return (this._formatWriteFeatures == newSet && this._formatWriteFeaturesToChange == newMask) ? this : new SerializationConfig(this, this._mapperFeatures, this._serFeatures, this._generatorFeatures, this._generatorFeaturesToChange, newSet, newMask);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withFilters(FilterProvider filterProvider) {
/* 741*/    return (filterProvider == this._filterProvider) ? this : new SerializationConfig(this, filterProvider);
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public SerializationConfig withSerializationInclusion(JsonInclude.Include incl) {
/* 749*/    return withPropertyInclusion(DEFAULT_INCLUSION.withValueInclusion(incl));
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withPropertyInclusion(JsonInclude.Value incl) {
/* 756*/    if (this._serializationInclusion.equals(incl)) {
/* 757*/        return this; 
/*   0*/       }
/* 759*/    return new SerializationConfig(this, incl);
/*   0*/  }
/*   0*/  
/*   0*/  public SerializationConfig withDefaultPrettyPrinter(PrettyPrinter pp) {
/* 766*/    return (this._defaultPrettyPrinter == pp) ? this : new SerializationConfig(this, pp);
/*   0*/  }
/*   0*/  
/*   0*/  public PrettyPrinter constructDefaultPrettyPrinter() {
/* 776*/    PrettyPrinter pp = this._defaultPrettyPrinter;
/* 777*/    if (pp instanceof Instantiatable) {
/* 778*/        pp = (PrettyPrinter)((Instantiatable)pp).createInstance(); 
/*   0*/       }
/* 780*/    return pp;
/*   0*/  }
/*   0*/  
/*   0*/  public void initialize(JsonGenerator g) {
/* 798*/    if (SerializationFeature.INDENT_OUTPUT.enabledIn(this._serFeatures)) {
/* 800*/        if (g.getPrettyPrinter() == null) {
/* 801*/          PrettyPrinter pp = constructDefaultPrettyPrinter();
/* 802*/          if (pp != null) {
/* 803*/              g.setPrettyPrinter(pp); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 808*/    boolean useBigDec = SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN.enabledIn(this._serFeatures);
/* 810*/    int mask = this._generatorFeaturesToChange;
/* 811*/    if (mask != 0 || useBigDec) {
/* 812*/      int newFlags = this._generatorFeatures;
/* 814*/      if (useBigDec) {
/* 815*/        int f = JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN.getMask();
/* 816*/        newFlags |= f;
/* 817*/        mask |= f;
/*   0*/      } 
/* 819*/      g.overrideStdFeatures(newFlags, mask);
/*   0*/    } 
/* 821*/    if (this._formatWriteFeaturesToChange != 0) {
/* 822*/        g.overrideFormatFeatures(this._formatWriteFeatures, this._formatWriteFeaturesToChange); 
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotationIntrospector getAnnotationIntrospector() {
/* 835*/    if (isEnabled(MapperFeature.USE_ANNOTATIONS)) {
/* 836*/        return super.getAnnotationIntrospector(); 
/*   0*/       }
/* 838*/    return AnnotationIntrospector.nopInstance();
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDescription introspectClassAnnotations(JavaType type) {
/* 847*/    return getClassIntrospector().forClassAnnotations(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public BeanDescription introspectDirectClassAnnotations(JavaType type) {
/* 857*/    return getClassIntrospector().forDirectClassAnnotations(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public VisibilityChecker<?> getDefaultVisibilityChecker() {
/* 863*/    VisibilityChecker<?> vchecker = super.getDefaultVisibilityChecker();
/* 865*/    if (!isEnabled(MapperFeature.AUTO_DETECT_GETTERS)) {
/* 866*/        vchecker = (VisibilityChecker<?>)vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE); 
/*   0*/       }
/* 868*/    if (!isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS)) {
/* 869*/        vchecker = (VisibilityChecker<?>)vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE); 
/*   0*/       }
/* 871*/    if (!isEnabled(MapperFeature.AUTO_DETECT_FIELDS)) {
/* 872*/        vchecker = (VisibilityChecker<?>)vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE); 
/*   0*/       }
/* 874*/    return vchecker;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonInclude.Include getSerializationInclusion() {
/* 889*/    JsonInclude.Include incl = this._serializationInclusion.getValueInclusion();
/* 890*/    return (incl == JsonInclude.Include.USE_DEFAULTS) ? JsonInclude.Include.ALWAYS : incl;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonInclude.Value getDefaultPropertyInclusion() {
/* 895*/    return this._serializationInclusion;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonInclude.Value getDefaultPropertyInclusion(Class<?> baseType) {
/* 901*/    return this._serializationInclusion;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFormat.Value getDefaultPropertyFormat(Class<?> baseType) {
/* 907*/    return EMPTY_FORMAT;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean useRootWrapping() {
/* 919*/    if (this._rootName != null) {
/* 920*/        return !this._rootName.isEmpty(); 
/*   0*/       }
/* 922*/    return isEnabled(SerializationFeature.WRAP_ROOT_VALUE);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(SerializationFeature f) {
/* 926*/    return ((this._serFeatures & f.getMask()) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean isEnabled(JsonGenerator.Feature f, JsonFactory factory) {
/* 937*/    int mask = f.getMask();
/* 938*/    if ((this._generatorFeaturesToChange & mask) != 0) {
/* 939*/        return ((this._generatorFeatures & f.getMask()) != 0); 
/*   0*/       }
/* 941*/    return factory.isEnabled(f);
/*   0*/  }
/*   0*/  
/*   0*/  public final boolean hasSerializationFeatures(int featureMask) {
/* 951*/    return ((this._serFeatures & featureMask) == featureMask);
/*   0*/  }
/*   0*/  
/*   0*/  public final int getSerializationFeatures() {
/* 955*/    return this._serFeatures;
/*   0*/  }
/*   0*/  
/*   0*/  public FilterProvider getFilterProvider() {
/* 965*/    return this._filterProvider;
/*   0*/  }
/*   0*/  
/*   0*/  public PrettyPrinter getDefaultPrettyPrinter() {
/* 979*/    return this._defaultPrettyPrinter;
/*   0*/  }
/*   0*/  
/*   0*/  public <T extends BeanDescription> T introspect(JavaType type) {
/* 994*/    return (T)getClassIntrospector().forSerialization(this, type, this);
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/*1005*/    return "[SerializationConfig: flags=0x" + Integer.toHexString(this._serFeatures) + "]";
/*   0*/  }
/*   0*/}
