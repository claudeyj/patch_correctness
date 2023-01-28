/*   0*/package com.fasterxml.jackson.databind.introspect;
/*   0*/
/*   0*/import com.fasterxml.jackson.databind.AnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.JavaType;
/*   0*/import com.fasterxml.jackson.databind.MapperFeature;
/*   0*/import com.fasterxml.jackson.databind.PropertyName;
/*   0*/import com.fasterxml.jackson.databind.PropertyNamingStrategy;
/*   0*/import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.util.BeanUtil;
/*   0*/import com.fasterxml.jackson.databind.util.ClassUtil;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.TreeMap;
/*   0*/
/*   0*/public class POJOPropertiesCollector {
/*   0*/  protected final MapperConfig<?> _config;
/*   0*/  
/*   0*/  protected final boolean _forSerialization;
/*   0*/  
/*   0*/  protected final boolean _stdBeanNaming;
/*   0*/  
/*   0*/  protected final JavaType _type;
/*   0*/  
/*   0*/  protected final AnnotatedClass _classDef;
/*   0*/  
/*   0*/  protected final VisibilityChecker<?> _visibilityChecker;
/*   0*/  
/*   0*/  protected final AnnotationIntrospector _annotationIntrospector;
/*   0*/  
/*   0*/  protected final String _mutatorPrefix;
/*   0*/  
/*  69*/  protected final LinkedHashMap<String, POJOPropertyBuilder> _properties = new LinkedHashMap<String, POJOPropertyBuilder>();
/*   0*/  
/*  72*/  protected LinkedList<POJOPropertyBuilder> _creatorProperties = null;
/*   0*/  
/*  74*/  protected LinkedList<AnnotatedMember> _anyGetters = null;
/*   0*/  
/*  76*/  protected LinkedList<AnnotatedMethod> _anySetters = null;
/*   0*/  
/*  81*/  protected LinkedList<AnnotatedMethod> _jsonValueGetters = null;
/*   0*/  
/*   0*/  protected HashSet<String> _ignoredPropertyNames;
/*   0*/  
/*   0*/  protected LinkedHashMap<Object, AnnotatedMember> _injectables;
/*   0*/  
/*   0*/  protected POJOPropertiesCollector(MapperConfig<?> config, boolean forSerialization, JavaType type, AnnotatedClass classDef, String mutatorPrefix) {
/* 106*/    this._config = config;
/* 107*/    this._stdBeanNaming = config.isEnabled(MapperFeature.USE_STD_BEAN_NAMING);
/* 108*/    this._forSerialization = forSerialization;
/* 109*/    this._type = type;
/* 110*/    this._classDef = classDef;
/* 111*/    this._mutatorPrefix = (mutatorPrefix == null) ? "set" : mutatorPrefix;
/* 112*/    this._annotationIntrospector = config.isAnnotationProcessingEnabled() ? this._config.getAnnotationIntrospector() : null;
/* 114*/    if (this._annotationIntrospector == null) {
/* 115*/      this._visibilityChecker = this._config.getDefaultVisibilityChecker();
/*   0*/    } else {
/* 117*/      this._visibilityChecker = this._annotationIntrospector.findAutoDetectVisibility(classDef, this._config.getDefaultVisibilityChecker());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public MapperConfig<?> getConfig() {
/* 129*/    return this._config;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType getType() {
/* 133*/    return this._type;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedClass getClassDef() {
/* 137*/    return this._classDef;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotationIntrospector getAnnotationIntrospector() {
/* 141*/    return this._annotationIntrospector;
/*   0*/  }
/*   0*/  
/*   0*/  public List<BeanPropertyDefinition> getProperties() {
/* 146*/    return new ArrayList<BeanPropertyDefinition>(this._properties.values());
/*   0*/  }
/*   0*/  
/*   0*/  public Map<Object, AnnotatedMember> getInjectables() {
/* 150*/    return this._injectables;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMethod getJsonValueMethod() {
/* 156*/    if (this._jsonValueGetters != null) {
/* 157*/      if (this._jsonValueGetters.size() > 1) {
/* 158*/          reportProblem("Multiple value properties defined (" + this._jsonValueGetters.get(0) + " vs " + this._jsonValueGetters.get(1) + ")"); 
/*   0*/         }
/* 162*/      return this._jsonValueGetters.get(0);
/*   0*/    } 
/* 164*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMember getAnyGetter() {
/* 169*/    if (this._anyGetters != null) {
/* 170*/      if (this._anyGetters.size() > 1) {
/* 171*/          reportProblem("Multiple 'any-getters' defined (" + this._anyGetters.get(0) + " vs " + this._anyGetters.get(1) + ")"); 
/*   0*/         }
/* 174*/      return this._anyGetters.getFirst();
/*   0*/    } 
/* 176*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMethod getAnySetterMethod() {
/* 181*/    if (this._anySetters != null) {
/* 182*/      if (this._anySetters.size() > 1) {
/* 183*/          reportProblem("Multiple 'any-setters' defined (" + this._anySetters.get(0) + " vs " + this._anySetters.get(1) + ")"); 
/*   0*/         }
/* 186*/      return this._anySetters.getFirst();
/*   0*/    } 
/* 188*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getIgnoredPropertyNames() {
/* 196*/    return this._ignoredPropertyNames;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectIdInfo getObjectIdInfo() {
/* 205*/    if (this._annotationIntrospector == null) {
/* 206*/        return null; 
/*   0*/       }
/* 208*/    ObjectIdInfo info = this._annotationIntrospector.findObjectIdInfo(this._classDef);
/* 209*/    if (info != null) {
/* 210*/        info = this._annotationIntrospector.findObjectReferenceInfo(this._classDef, info); 
/*   0*/       }
/* 212*/    return info;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findPOJOBuilderClass() {
/* 220*/    return this._annotationIntrospector.findPOJOBuilder(this._classDef);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map<String, POJOPropertyBuilder> getPropertyMap() {
/* 225*/    return this._properties;
/*   0*/  }
/*   0*/  
/*   0*/  public POJOPropertiesCollector collect() {
/* 240*/    this._properties.clear();
/* 243*/    _addFields();
/* 244*/    _addMethods();
/* 245*/    _addCreators();
/* 246*/    _addInjectables();
/* 249*/    _removeUnwantedProperties();
/* 252*/    _renameProperties();
/* 254*/    PropertyNamingStrategy naming = _findNamingStrategy();
/* 255*/    if (naming != null) {
/* 256*/        _renameUsing(naming); 
/*   0*/       }
/* 263*/    for (POJOPropertyBuilder property : this._properties.values()) {
/* 264*/        property.trimByVisibility(); 
/*   0*/       }
/* 268*/    for (POJOPropertyBuilder property : this._properties.values()) {
/* 269*/        property.mergeAnnotations(this._forSerialization); 
/*   0*/       }
/* 275*/    if (this._config.isEnabled(MapperFeature.USE_WRAPPER_NAME_AS_PROPERTY_NAME)) {
/* 276*/        _renameWithWrappers(); 
/*   0*/       }
/* 280*/    _sortProperties();
/* 281*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  protected void _sortProperties() {
/*   0*/    boolean sort;
/*   0*/    Map<String, POJOPropertyBuilder> all;
/* 296*/    AnnotationIntrospector intr = this._annotationIntrospector;
/* 298*/    Boolean alpha = (intr == null) ? null : intr.findSerializationSortAlphabetically((Annotated)this._classDef);
/* 300*/    if (alpha == null) {
/* 301*/      sort = this._config.shouldSortPropertiesAlphabetically();
/*   0*/    } else {
/* 303*/      sort = alpha;
/*   0*/    } 
/* 305*/    String[] propertyOrder = (intr == null) ? null : intr.findSerializationPropertyOrder(this._classDef);
/* 308*/    if (!sort && this._creatorProperties == null && propertyOrder == null) {
/*   0*/        return; 
/*   0*/       }
/* 311*/    int size = this._properties.size();
/* 314*/    if (sort) {
/* 315*/      all = new TreeMap<String, POJOPropertyBuilder>();
/*   0*/    } else {
/* 317*/      all = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
/*   0*/    } 
/* 320*/    for (POJOPropertyBuilder prop : this._properties.values()) {
/* 321*/        all.put(prop.getName(), prop); 
/*   0*/       }
/* 323*/    Map<String, POJOPropertyBuilder> ordered = new LinkedHashMap<String, POJOPropertyBuilder>(size + size);
/* 325*/    if (propertyOrder != null) {
/* 326*/        for (String name : propertyOrder) {
/* 327*/          POJOPropertyBuilder w = all.get(name);
/* 328*/          if (w == null) {
/* 329*/              for (POJOPropertyBuilder prop : this._properties.values()) {
/* 330*/                if (name.equals(prop.getInternalName())) {
/* 331*/                  w = prop;
/* 333*/                  name = prop.getName();
/*   0*/                  break;
/*   0*/                } 
/*   0*/              }  
/*   0*/             }
/* 338*/          if (w != null) {
/* 339*/              ordered.put(name, w); 
/*   0*/             }
/*   0*/        }  
/*   0*/       }
/* 344*/    if (this._creatorProperties != null) {
/*   0*/      Collection<POJOPropertyBuilder> cr;
/* 351*/      if (sort) {
/* 352*/        TreeMap<String, POJOPropertyBuilder> sorted = new TreeMap<String, POJOPropertyBuilder>();
/* 354*/        for (POJOPropertyBuilder prop : this._creatorProperties) {
/* 355*/            sorted.put(prop.getName(), prop); 
/*   0*/           }
/* 357*/        cr = sorted.values();
/*   0*/      } else {
/* 359*/        cr = this._creatorProperties;
/*   0*/      } 
/* 361*/      for (POJOPropertyBuilder prop : cr) {
/* 362*/          ordered.put(prop.getName(), prop); 
/*   0*/         }
/*   0*/    } 
/* 366*/    ordered.putAll(all);
/* 368*/    this._properties.clear();
/* 369*/    this._properties.putAll(ordered);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addFields() {
/* 383*/    AnnotationIntrospector ai = this._annotationIntrospector;
/* 388*/    boolean pruneFinalFields = (!this._forSerialization && !this._config.isEnabled(MapperFeature.ALLOW_FINAL_FIELDS_AS_MUTATORS));
/* 390*/    for (AnnotatedField f : this._classDef.fields()) {
/*   0*/      PropertyName pn;
/* 391*/      String implName = (ai == null) ? null : ai.findImplicitPropertyName(f);
/* 392*/      if (implName == null) {
/* 393*/          implName = f.getName(); 
/*   0*/         }
/* 398*/      if (ai == null) {
/* 399*/        pn = null;
/* 400*/      } else if (this._forSerialization) {
/* 406*/        pn = ai.findNameForSerialization(f);
/*   0*/      } else {
/* 408*/        pn = ai.findNameForDeserialization(f);
/*   0*/      } 
/* 410*/      boolean nameExplicit = (pn != null);
/* 412*/      if (nameExplicit && pn.isEmpty()) {
/* 413*/        pn = _propNameFromSimple(implName);
/* 414*/        nameExplicit = false;
/*   0*/      } 
/* 417*/      boolean visible = (pn != null);
/* 418*/      if (!visible) {
/* 419*/          visible = this._visibilityChecker.isFieldVisible(f); 
/*   0*/         }
/* 422*/      boolean ignored = (ai != null && ai.hasIgnoreMarker(f));
/* 428*/      if (pruneFinalFields && pn == null && !ignored && Modifier.isFinal(f.getModifiers())) {
/*   0*/          continue; 
/*   0*/         }
/* 431*/      _property(implName).addField(f, pn, nameExplicit, visible, ignored);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addCreators() {
/* 441*/    if (this._annotationIntrospector != null) {
/* 442*/      for (AnnotatedConstructor ctor : this._classDef.getConstructors()) {
/* 443*/        if (this._creatorProperties == null) {
/* 444*/            this._creatorProperties = new LinkedList<POJOPropertyBuilder>(); 
/*   0*/           }
/* 446*/        for (int i = 0, len = ctor.getParameterCount(); i < len; i++) {
/* 447*/            _addCreatorParam(ctor.getParameter(i)); 
/*   0*/           }
/*   0*/      } 
/* 450*/      for (AnnotatedMethod factory : this._classDef.getStaticMethods()) {
/* 451*/        if (this._creatorProperties == null) {
/* 452*/            this._creatorProperties = new LinkedList<POJOPropertyBuilder>(); 
/*   0*/           }
/* 454*/        for (int i = 0, len = factory.getParameterCount(); i < len; i++) {
/* 455*/            _addCreatorParam(factory.getParameter(i)); 
/*   0*/           }
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addCreatorParam(AnnotatedParameter param) {
/* 467*/    String impl = this._annotationIntrospector.findImplicitPropertyName(param);
/* 468*/    if (impl == null) {
/* 469*/        impl = ""; 
/*   0*/       }
/* 471*/    PropertyName pn = this._annotationIntrospector.findNameForDeserialization(param);
/* 472*/    boolean expl = (pn != null && !pn.isEmpty());
/* 473*/    if (!expl) {
/* 474*/      if (impl.isEmpty()) {
/*   0*/          return; 
/*   0*/         }
/* 482*/      if (!this._annotationIntrospector.hasCreatorAnnotation(param.getOwner())) {
/*   0*/          return; 
/*   0*/         }
/* 485*/      pn = new PropertyName(impl);
/*   0*/    } 
/* 495*/    POJOPropertyBuilder prop = (expl && impl.isEmpty()) ? _property(pn) : _property(impl);
/* 496*/    prop.addCtor(param, pn, expl, true, false);
/* 497*/    this._creatorProperties.add(prop);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addMethods() {
/* 505*/    AnnotationIntrospector ai = this._annotationIntrospector;
/* 507*/    for (AnnotatedMethod m : this._classDef.memberMethods()) {
/* 513*/      int argCount = m.getParameterCount();
/* 514*/      if (argCount == 0) {
/* 515*/        _addGetterMethod(m, ai);
/*   0*/        continue;
/*   0*/      } 
/* 516*/      if (argCount == 1) {
/* 517*/        _addSetterMethod(m, ai);
/*   0*/        continue;
/*   0*/      } 
/* 518*/      if (argCount == 2 && 
/* 519*/        ai != null && ai.hasAnySetterAnnotation(m)) {
/* 520*/        if (this._anySetters == null) {
/* 521*/            this._anySetters = new LinkedList<AnnotatedMethod>(); 
/*   0*/           }
/* 523*/        this._anySetters.add(m);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addGetterMethod(AnnotatedMethod m, AnnotationIntrospector ai) {
/*   0*/    String implName;
/*   0*/    boolean visible;
/* 532*/    if (!m.hasReturnType()) {
/*   0*/        return; 
/*   0*/       }
/* 537*/    if (ai != null) {
/* 538*/      if (ai.hasAnyGetterAnnotation(m)) {
/* 539*/        if (this._anyGetters == null) {
/* 540*/            this._anyGetters = new LinkedList<AnnotatedMember>(); 
/*   0*/           }
/* 542*/        this._anyGetters.add(m);
/*   0*/        return;
/*   0*/      } 
/* 546*/      if (ai.hasAsValueAnnotation(m)) {
/* 547*/        if (this._jsonValueGetters == null) {
/* 548*/            this._jsonValueGetters = new LinkedList<AnnotatedMethod>(); 
/*   0*/           }
/* 550*/        this._jsonValueGetters.add(m);
/*   0*/        return;
/*   0*/      } 
/*   0*/    } 
/* 557*/    PropertyName pn = (ai == null) ? null : ai.findNameForSerialization(m);
/* 558*/    boolean nameExplicit = (pn != null);
/* 560*/    if (!nameExplicit) {
/* 561*/      implName = (ai == null) ? null : ai.findImplicitPropertyName(m);
/* 562*/      if (implName == null) {
/* 563*/          implName = BeanUtil.okNameForRegularGetter(m, m.getName(), this._stdBeanNaming); 
/*   0*/         }
/* 565*/      if (implName == null) {
/* 566*/        implName = BeanUtil.okNameForIsGetter(m, m.getName(), this._stdBeanNaming);
/* 567*/        if (implName == null) {
/*   0*/            return; 
/*   0*/           }
/* 570*/        visible = this._visibilityChecker.isIsGetterVisible(m);
/*   0*/      } else {
/* 572*/        visible = this._visibilityChecker.isGetterVisible(m);
/*   0*/      } 
/*   0*/    } else {
/* 576*/      implName = (ai == null) ? null : ai.findImplicitPropertyName(m);
/* 577*/      if (implName == null) {
/* 578*/          implName = BeanUtil.okNameForGetter(m, this._stdBeanNaming); 
/*   0*/         }
/* 581*/      if (implName == null) {
/* 582*/          implName = m.getName(); 
/*   0*/         }
/* 584*/      if (pn.isEmpty()) {
/* 586*/        pn = _propNameFromSimple(implName);
/* 587*/        nameExplicit = false;
/*   0*/      } 
/* 589*/      visible = true;
/*   0*/    } 
/* 591*/    boolean ignore = (ai == null) ? false : ai.hasIgnoreMarker(m);
/* 592*/    _property(implName).addGetter(m, pn, nameExplicit, visible, ignore);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addSetterMethod(AnnotatedMethod m, AnnotationIntrospector ai) {
/*   0*/    String implName;
/*   0*/    boolean visible;
/* 599*/    PropertyName pn = (ai == null) ? null : ai.findNameForDeserialization(m);
/* 600*/    boolean nameExplicit = (pn != null);
/* 601*/    if (!nameExplicit) {
/* 602*/      implName = (ai == null) ? null : ai.findImplicitPropertyName(m);
/* 603*/      if (implName == null) {
/* 604*/          implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming); 
/*   0*/         }
/* 606*/      if (implName == null) {
/*   0*/          return; 
/*   0*/         }
/* 609*/      visible = this._visibilityChecker.isSetterVisible(m);
/*   0*/    } else {
/* 612*/      implName = (ai == null) ? null : ai.findImplicitPropertyName(m);
/* 613*/      if (implName == null) {
/* 614*/          implName = BeanUtil.okNameForMutator(m, this._mutatorPrefix, this._stdBeanNaming); 
/*   0*/         }
/* 617*/      if (implName == null) {
/* 618*/          implName = m.getName(); 
/*   0*/         }
/* 620*/      if (pn.isEmpty()) {
/* 622*/        pn = _propNameFromSimple(implName);
/* 623*/        nameExplicit = false;
/*   0*/      } 
/* 625*/      visible = true;
/*   0*/    } 
/* 627*/    boolean ignore = (ai == null) ? false : ai.hasIgnoreMarker(m);
/* 628*/    _property(implName).addSetter(m, pn, nameExplicit, visible, ignore);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _addInjectables() {
/* 633*/    AnnotationIntrospector ai = this._annotationIntrospector;
/* 634*/    if (ai == null) {
/*   0*/        return; 
/*   0*/       }
/* 639*/    for (AnnotatedField f : this._classDef.fields()) {
/* 640*/        _doAddInjectable(ai.findInjectableValueId(f), f); 
/*   0*/       }
/* 643*/    for (AnnotatedMethod m : this._classDef.memberMethods()) {
/* 647*/      if (m.getParameterCount() != 1) {
/*   0*/          continue; 
/*   0*/         }
/* 650*/      _doAddInjectable(ai.findInjectableValueId(m), m);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _doAddInjectable(Object id, AnnotatedMember m) {
/* 656*/    if (id == null) {
/*   0*/        return; 
/*   0*/       }
/* 659*/    if (this._injectables == null) {
/* 660*/        this._injectables = new LinkedHashMap<Object, AnnotatedMember>(); 
/*   0*/       }
/* 662*/    AnnotatedMember prev = this._injectables.put(id, m);
/* 663*/    if (prev != null) {
/* 664*/      String type = id.getClass().getName();
/* 665*/      throw new IllegalArgumentException("Duplicate injectable value with id '" + String.valueOf(id) + "' (of type " + type + ")");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private PropertyName _propNameFromSimple(String simpleName) {
/* 671*/    return PropertyName.construct(simpleName, null);
/*   0*/  }
/*   0*/  
/*   0*/  protected void _removeUnwantedProperties() {
/* 686*/    Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
/* 687*/    boolean forceNonVisibleRemoval = !this._config.isEnabled(MapperFeature.INFER_PROPERTY_MUTATORS);
/* 689*/    while (it.hasNext()) {
/* 690*/      Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/* 691*/      POJOPropertyBuilder prop = entry.getValue();
/* 694*/      if (!prop.anyVisible()) {
/* 695*/        it.remove();
/*   0*/        continue;
/*   0*/      } 
/* 699*/      if (prop.anyIgnorals()) {
/* 701*/        if (!prop.isExplicitlyIncluded()) {
/* 702*/          it.remove();
/* 703*/          _addIgnored(prop.getName());
/*   0*/          continue;
/*   0*/        } 
/* 707*/        prop.removeIgnored();
/* 708*/        if (!this._forSerialization && !prop.couldDeserialize()) {
/* 709*/            _addIgnored(prop.getName()); 
/*   0*/           }
/*   0*/      } 
/* 713*/      prop.removeNonVisible(forceNonVisibleRemoval);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void _addIgnored(String name) {
/* 719*/    if (!this._forSerialization) {
/* 720*/      if (this._ignoredPropertyNames == null) {
/* 721*/          this._ignoredPropertyNames = new HashSet<String>(); 
/*   0*/         }
/* 723*/      this._ignoredPropertyNames.add(name);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _renameProperties() {
/* 736*/    Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
/* 737*/    LinkedList<POJOPropertyBuilder> renamed = null;
/* 738*/    while (it.hasNext()) {
/* 739*/      Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/* 740*/      POJOPropertyBuilder prop = entry.getValue();
/* 742*/      Collection<PropertyName> l = prop.findExplicitNames();
/* 745*/      if (l.isEmpty()) {
/*   0*/          continue; 
/*   0*/         }
/* 748*/      it.remove();
/* 749*/      if (renamed == null) {
/* 750*/          renamed = new LinkedList<POJOPropertyBuilder>(); 
/*   0*/         }
/* 753*/      if (l.size() == 1) {
/* 754*/        PropertyName n = l.iterator().next();
/* 755*/        renamed.add(prop.withName(n));
/*   0*/        continue;
/*   0*/      } 
/* 759*/      renamed.addAll(prop.explode(l));
/*   0*/    } 
/* 775*/    if (renamed != null) {
/* 776*/        for (POJOPropertyBuilder prop : renamed) {
/* 777*/          String name = prop.getName();
/* 778*/          POJOPropertyBuilder old = this._properties.get(name);
/* 779*/          if (old == null) {
/* 780*/            this._properties.put(name, prop);
/*   0*/          } else {
/* 782*/            old.addAll(prop);
/*   0*/          } 
/* 785*/          _updateCreatorProperty(prop, this._creatorProperties);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void _renameUsing(PropertyNamingStrategy naming) {
/* 792*/    POJOPropertyBuilder[] props = (POJOPropertyBuilder[])this._properties.values().toArray((Object[])new POJOPropertyBuilder[this._properties.size()]);
/* 793*/    this._properties.clear();
/* 794*/    for (POJOPropertyBuilder prop : props) {
/*   0*/      String simpleName;
/* 795*/      PropertyName fullName = prop.getFullName();
/* 796*/      String rename = null;
/* 799*/      if (!prop.isExplicitlyNamed()) {
/* 800*/          if (this._forSerialization) {
/* 801*/            if (prop.hasGetter()) {
/* 802*/              rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/* 803*/            } else if (prop.hasField()) {
/* 804*/              rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
/*   0*/            } 
/* 807*/          } else if (prop.hasSetter()) {
/* 808*/            rename = naming.nameForSetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/* 809*/          } else if (prop.hasConstructorParameter()) {
/* 810*/            rename = naming.nameForConstructorParameter(this._config, prop.getConstructorParameter(), fullName.getSimpleName());
/* 811*/          } else if (prop.hasField()) {
/* 812*/            rename = naming.nameForField(this._config, prop.getField(), fullName.getSimpleName());
/* 813*/          } else if (prop.hasGetter()) {
/* 817*/            rename = naming.nameForGetterMethod(this._config, prop.getGetter(), fullName.getSimpleName());
/*   0*/          }  
/*   0*/         }
/* 822*/      if (rename != null && !fullName.hasSimpleName(rename)) {
/* 823*/        prop = prop.withSimpleName(rename);
/* 824*/        simpleName = rename;
/*   0*/      } else {
/* 826*/        simpleName = fullName.getSimpleName();
/*   0*/      } 
/* 831*/      POJOPropertyBuilder old = this._properties.get(simpleName);
/* 832*/      if (old == null) {
/* 833*/        this._properties.put(simpleName, prop);
/*   0*/      } else {
/* 835*/        old.addAll(prop);
/*   0*/      } 
/* 838*/      _updateCreatorProperty(prop, this._creatorProperties);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void _renameWithWrappers() {
/* 847*/    Iterator<Map.Entry<String, POJOPropertyBuilder>> it = this._properties.entrySet().iterator();
/* 848*/    LinkedList<POJOPropertyBuilder> renamed = null;
/* 849*/    while (it.hasNext()) {
/* 850*/      Map.Entry<String, POJOPropertyBuilder> entry = it.next();
/* 851*/      POJOPropertyBuilder prop = entry.getValue();
/* 852*/      AnnotatedMember member = prop.getPrimaryMember();
/* 853*/      if (member == null) {
/*   0*/          continue; 
/*   0*/         }
/* 856*/      PropertyName wrapperName = this._annotationIntrospector.findWrapperName(member);
/* 860*/      if (wrapperName == null || !wrapperName.hasSimpleName()) {
/*   0*/          continue; 
/*   0*/         }
/* 863*/      if (!wrapperName.equals(prop.getFullName())) {
/* 864*/        if (renamed == null) {
/* 865*/            renamed = new LinkedList<POJOPropertyBuilder>(); 
/*   0*/           }
/* 867*/        prop = prop.withName(wrapperName);
/* 868*/        renamed.add(prop);
/* 869*/        it.remove();
/*   0*/      } 
/*   0*/    } 
/* 873*/    if (renamed != null) {
/* 874*/        for (POJOPropertyBuilder prop : renamed) {
/* 875*/          String name = prop.getName();
/* 876*/          POJOPropertyBuilder old = this._properties.get(name);
/* 877*/          if (old == null) {
/* 878*/            this._properties.put(name, prop);
/*   0*/            continue;
/*   0*/          } 
/* 880*/          old.addAll(prop);
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/  
/*   0*/  protected void reportProblem(String msg) {
/* 894*/    throw new IllegalArgumentException("Problem with definition of " + this._classDef + ": " + msg);
/*   0*/  }
/*   0*/  
/*   0*/  protected POJOPropertyBuilder _property(PropertyName name) {
/* 898*/    return _property(name.getSimpleName());
/*   0*/  }
/*   0*/  
/*   0*/  protected POJOPropertyBuilder _property(String implName) {
/* 904*/    POJOPropertyBuilder prop = this._properties.get(implName);
/* 905*/    if (prop == null) {
/* 906*/      prop = new POJOPropertyBuilder(new PropertyName(implName), this._annotationIntrospector, this._forSerialization);
/* 908*/      this._properties.put(implName, prop);
/*   0*/    } 
/* 910*/    return prop;
/*   0*/  }
/*   0*/  
/*   0*/  private PropertyNamingStrategy _findNamingStrategy() {
/* 915*/    Object namingDef = (this._annotationIntrospector == null) ? null : this._annotationIntrospector.findNamingStrategy(this._classDef);
/* 917*/    if (namingDef == null) {
/* 918*/        return this._config.getPropertyNamingStrategy(); 
/*   0*/       }
/* 920*/    if (namingDef instanceof PropertyNamingStrategy) {
/* 921*/        return (PropertyNamingStrategy)namingDef; 
/*   0*/       }
/* 926*/    if (!(namingDef instanceof Class)) {
/* 927*/        throw new IllegalStateException("AnnotationIntrospector returned PropertyNamingStrategy definition of type " + namingDef.getClass().getName() + "; expected type PropertyNamingStrategy or Class<PropertyNamingStrategy> instead"); 
/*   0*/       }
/* 930*/    Class<?> namingClass = (Class)namingDef;
/* 931*/    if (!PropertyNamingStrategy.class.isAssignableFrom(namingClass)) {
/* 932*/        throw new IllegalStateException("AnnotationIntrospector returned Class " + namingClass.getName() + "; expected Class<PropertyNamingStrategy>"); 
/*   0*/       }
/* 935*/    HandlerInstantiator hi = this._config.getHandlerInstantiator();
/* 936*/    if (hi != null) {
/* 937*/      PropertyNamingStrategy pns = hi.namingStrategyInstance(this._config, this._classDef, namingClass);
/* 938*/      if (pns != null) {
/* 939*/          return pns; 
/*   0*/         }
/*   0*/    } 
/* 942*/    return (PropertyNamingStrategy)ClassUtil.createInstance(namingClass, this._config.canOverrideAccessModifiers());
/*   0*/  }
/*   0*/  
/*   0*/  protected void _updateCreatorProperty(POJOPropertyBuilder prop, List<POJOPropertyBuilder> creatorProperties) {
/* 947*/    if (creatorProperties != null) {
/* 948*/        for (int i = 0, len = creatorProperties.size(); i < len; i++) {
/* 949*/          if (((POJOPropertyBuilder)creatorProperties.get(i)).getInternalName().equals(prop.getInternalName())) {
/* 950*/            creatorProperties.set(i, prop);
/*   0*/            break;
/*   0*/          } 
/*   0*/        }  
/*   0*/       }
/*   0*/  }
/*   0*/}
