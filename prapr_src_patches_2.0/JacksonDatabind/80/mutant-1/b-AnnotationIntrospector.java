/*   0*/package com.fasterxml.jackson.databind;
/*   0*/
/*   0*/import com.fasterxml.jackson.annotation.JacksonInject;
/*   0*/import com.fasterxml.jackson.annotation.JsonCreator;
/*   0*/import com.fasterxml.jackson.annotation.JsonFormat;
/*   0*/import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
/*   0*/import com.fasterxml.jackson.annotation.JsonInclude;
/*   0*/import com.fasterxml.jackson.annotation.JsonProperty;
/*   0*/import com.fasterxml.jackson.annotation.JsonSetter;
/*   0*/import com.fasterxml.jackson.core.Version;
/*   0*/import com.fasterxml.jackson.core.Versioned;
/*   0*/import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
/*   0*/import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/*   0*/import com.fasterxml.jackson.databind.cfg.MapperConfig;
/*   0*/import com.fasterxml.jackson.databind.introspect.Annotated;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
/*   0*/import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
/*   0*/import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
/*   0*/import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
/*   0*/import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
/*   0*/import com.fasterxml.jackson.databind.jsontype.NamedType;
/*   0*/import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
/*   0*/import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
/*   0*/import com.fasterxml.jackson.databind.util.NameTransformer;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.annotation.Annotation;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.List;
/*   0*/
/*   0*/public abstract class AnnotationIntrospector implements Versioned, Serializable {
/*   0*/  public static class ReferenceProperty {
/*   0*/    private final Type _type;
/*   0*/    
/*   0*/    private final String _name;
/*   0*/    
/*   0*/    public enum Type {
/*  64*/      MANAGED_REFERENCE, BACK_REFERENCE;
/*   0*/    }
/*   0*/    
/*   0*/    public ReferenceProperty(Type t, String n) {
/*  80*/      this._type = t;
/*  81*/      this._name = n;
/*   0*/    }
/*   0*/    
/*   0*/    public static ReferenceProperty managed(String name) {
/*  84*/      return new ReferenceProperty(Type.MANAGED_REFERENCE, name);
/*   0*/    }
/*   0*/    
/*   0*/    public static ReferenceProperty back(String name) {
/*  85*/      return new ReferenceProperty(Type.BACK_REFERENCE, name);
/*   0*/    }
/*   0*/    
/*   0*/    public Type getType() {
/*  87*/      return this._type;
/*   0*/    }
/*   0*/    
/*   0*/    public String getName() {
/*  88*/      return this._name;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isManagedReference() {
/*  90*/      return (this._type == Type.MANAGED_REFERENCE);
/*   0*/    }
/*   0*/    
/*   0*/    public boolean isBackReference() {
/*  91*/      return (this._type == Type.BACK_REFERENCE);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotationIntrospector nopInstance() {
/* 106*/    return NopAnnotationIntrospector.instance;
/*   0*/  }
/*   0*/  
/*   0*/  public static AnnotationIntrospector pair(AnnotationIntrospector a1, AnnotationIntrospector a2) {
/* 110*/    return new AnnotationIntrospectorPair(a1, a2);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<AnnotationIntrospector> allIntrospectors() {
/* 131*/    return Collections.singletonList(this);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection<AnnotationIntrospector> allIntrospectors(Collection<AnnotationIntrospector> result) {
/* 145*/    result.add(this);
/* 146*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract Version version();
/*   0*/  
/*   0*/  public boolean isAnnotationBundle(Annotation ann) {
/* 172*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectIdInfo findObjectIdInfo(Annotated ann) {
/* 192*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectIdInfo findObjectReferenceInfo(Annotated ann, ObjectIdInfo objectIdInfo) {
/* 201*/    return objectIdInfo;
/*   0*/  }
/*   0*/  
/*   0*/  public PropertyName findRootName(AnnotatedClass ac) {
/* 221*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonIgnoreProperties.Value findPropertyIgnorals(Annotated ac) {
/* 238*/    return JsonIgnoreProperties.Value.empty();
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean isIgnorableType(AnnotatedClass ac) {
/* 252*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findFilterId(Annotated ann) {
/* 261*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findNamingStrategy(AnnotatedClass ac) {
/* 274*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String findClassDescription(AnnotatedClass ac) {
/* 287*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public String[] findPropertiesToIgnore(Annotated ac, boolean forSerialization) {
/* 299*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public String[] findPropertiesToIgnore(Annotated ac) {
/* 307*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Boolean findIgnoreUnknownProperties(AnnotatedClass ac) {
/* 316*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public VisibilityChecker<?> findAutoDetectVisibility(AnnotatedClass ac, VisibilityChecker<?> checker) {
/* 332*/    return checker;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeResolverBuilder<?> findTypeResolver(MapperConfig<?> config, AnnotatedClass ac, JavaType baseType) {
/* 357*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeResolverBuilder<?> findPropertyTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType baseType) {
/* 377*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public TypeResolverBuilder<?> findPropertyContentTypeResolver(MapperConfig<?> config, AnnotatedMember am, JavaType containerType) {
/* 399*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public List<NamedType> findSubtypes(Annotated a) {
/* 411*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String findTypeName(AnnotatedClass ac) {
/* 418*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean isTypeId(AnnotatedMember member) {
/* 425*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ReferenceProperty findReferenceType(AnnotatedMember member) {
/* 437*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public NameTransformer findUnwrappingNameTransformer(AnnotatedMember member) {
/* 447*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasIgnoreMarker(AnnotatedMember m) {
/* 456*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public JacksonInject.Value findInjectableValue(AnnotatedMember m) {
/* 476*/    Object id = findInjectableValueId(m);
/* 477*/    if (id != null) {
/* 478*/        return JacksonInject.Value.forId(id); 
/*   0*/       }
/* 480*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean hasRequiredMarker(AnnotatedMember m) {
/* 488*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?>[] findViews(Annotated a) {
/* 506*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonFormat.Value findFormat(Annotated memberOrClass) {
/* 517*/    return JsonFormat.Value.empty();
/*   0*/  }
/*   0*/  
/*   0*/  public PropertyName findWrapperName(Annotated ann) {
/* 531*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String findPropertyDefaultValue(Annotated ann) {
/* 541*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String findPropertyDescription(Annotated ann) {
/* 553*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Integer findPropertyIndex(Annotated ann) {
/* 566*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String findImplicitPropertyName(AnnotatedMember member) {
/* 581*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public List<PropertyName> findPropertyAliases(Annotated ann) {
/* 591*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonProperty.Access findPropertyAccess(Annotated ann) {
/* 602*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public AnnotatedMethod resolveSetterConflict(MapperConfig<?> config, AnnotatedMethod setter1, AnnotatedMethod setter2) {
/* 614*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Object findInjectableValueId(AnnotatedMember m) {
/* 622*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findSerializer(Annotated am) {
/* 639*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findKeySerializer(Annotated am) {
/* 650*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findContentSerializer(Annotated am) {
/* 662*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findNullSerializer(Annotated am) {
/* 672*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonSerialize.Typing findSerializationTyping(Annotated a) {
/* 684*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findSerializationConverter(Annotated a) {
/* 709*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findSerializationContentConverter(AnnotatedMember a) {
/* 731*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonInclude.Value findPropertyInclusion(Annotated a) {
/* 743*/    return JsonInclude.Value.empty();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonInclude.Include findSerializationInclusion(Annotated a, JsonInclude.Include defValue) {
/* 765*/    return defValue;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonInclude.Include findSerializationInclusionForContent(Annotated a, JsonInclude.Include defValue) {
/* 779*/    return defValue;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType refineSerializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/* 798*/    return baseType;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findSerializationType(Annotated a) {
/* 806*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findSerializationKeyType(Annotated am, JavaType baseType) {
/* 814*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findSerializationContentType(Annotated am, JavaType baseType) {
/* 822*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] findSerializationPropertyOrder(AnnotatedClass ac) {
/* 836*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean findSerializationSortAlphabetically(Annotated ann) {
/* 845*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void findAndAddVirtualProperties(MapperConfig<?> config, AnnotatedClass ac, List<BeanPropertyWriter> properties) {}
/*   0*/  
/*   0*/  public PropertyName findNameForSerialization(Annotated a) {
/* 878*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean hasAsValue(Annotated a) {
/* 896*/    if (a instanceof AnnotatedMethod && 
/* 897*/      hasAsValueAnnotation((AnnotatedMethod)a)) {
/* 898*/        return true; 
/*   0*/       }
/* 901*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean hasAnyGetter(Annotated a) {
/* 918*/    if (a instanceof AnnotatedMethod && 
/* 919*/      hasAnyGetterAnnotation((AnnotatedMethod)a)) {
/* 920*/        return true; 
/*   0*/       }
/* 923*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public String[] findEnumValues(Class<?> enumType, Enum<?>[] enumValues, String[] names) {
/* 939*/    return names;
/*   0*/  }
/*   0*/  
/*   0*/  public Enum<?> findDefaultEnumValue(Class<Enum<?>> enumCls) {
/* 951*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public String findEnumValue(Enum<?> value) {
/* 968*/    return value.name();
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public boolean hasAsValueAnnotation(AnnotatedMethod am) {
/* 976*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public boolean hasAnyGetterAnnotation(AnnotatedMethod am) {
/* 984*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findDeserializer(Annotated am) {
/*1002*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findKeyDeserializer(Annotated am) {
/*1014*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findContentDeserializer(Annotated am) {
/*1027*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findDeserializationConverter(Annotated a) {
/*1053*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findDeserializationContentConverter(AnnotatedMember a) {
/*1075*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JavaType refineDeserializationType(MapperConfig<?> config, Annotated a, JavaType baseType) throws JsonMappingException {
/*1093*/    return baseType;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findDeserializationType(Annotated am, JavaType baseType) {
/*1111*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findDeserializationKeyType(Annotated am, JavaType baseKeyType) {
/*1128*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public Class<?> findDeserializationContentType(Annotated am, JavaType baseContentType) {
/*1146*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Object findValueInstantiator(AnnotatedClass ac) {
/*1161*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> findPOJOBuilder(AnnotatedClass ac) {
/*1178*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonPOJOBuilder.Value findPOJOBuilderConfig(AnnotatedClass ac) {
/*1185*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public PropertyName findNameForDeserialization(Annotated a) {
/*1209*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean hasAnySetter(Annotated a) {
/*1224*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonSetter.Value findSetterInfo(Annotated a) {
/*1234*/    return JsonSetter.Value.empty();
/*   0*/  }
/*   0*/  
/*   0*/  public Boolean findMergeInfo(Annotated a) {
/*1243*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JsonCreator.Mode findCreatorAnnotation(MapperConfig<?> config, Annotated a) {
/*1264*/    if (hasCreatorAnnotation(a)) {
/*1265*/      JsonCreator.Mode mode = findCreatorBinding(a);
/*1266*/      if (mode == null) {
/*1267*/          mode = JsonCreator.Mode.DEFAULT; 
/*   0*/         }
/*1269*/      return mode;
/*   0*/    } 
/*1271*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public boolean hasCreatorAnnotation(Annotated a) {
/*1288*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public JsonCreator.Mode findCreatorBinding(Annotated a) {
/*1302*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  @Deprecated
/*   0*/  public boolean hasAnySetterAnnotation(AnnotatedMethod am) {
/*1310*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  protected <A extends Annotation> A _findAnnotation(Annotated annotated, Class<A> annoClass) {
/*1336*/    Class<A> clazz = annoClass;
/*1336*/    return (annotated == null) ? null : annotated.getAnnotation(clazz);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasAnnotation(Annotated annotated, Class<? extends Annotation> annoClass) {
/*1353*/    return annotated.hasAnnotation(annoClass);
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean _hasOneOf(Annotated annotated, Class<? extends Annotation>[] annoClasses) {
/*1363*/    return annotated.hasOneOf(annoClasses);
/*   0*/  }
/*   0*/}
