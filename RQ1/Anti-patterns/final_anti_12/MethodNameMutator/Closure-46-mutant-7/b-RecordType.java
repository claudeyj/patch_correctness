/*   0*/package com.google.javascript.rhino.jstype;
/*   0*/
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/import java.util.SortedMap;
/*   0*/
/*   0*/class RecordType extends PrototypeObjectType {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*  67*/  private final SortedMap<String, JSType> properties = Maps.newTreeMap();
/*   0*/  
/*   0*/  private boolean isFrozen = false;
/*   0*/  
/*   0*/  RecordType(JSTypeRegistry registry, Map<String, RecordTypeBuilder.RecordProperty> properties) {
/*  79*/    super(registry, null, null);
/*  80*/    setPrettyPrint(true);
/*  82*/    for (String property : properties.keySet()) {
/*  83*/      RecordTypeBuilder.RecordProperty prop = properties.get(property);
/*  84*/      if (prop == null)
/*  85*/        throw new IllegalStateException("RecordProperty associated with a property should not be null!"); 
/*  88*/      defineDeclaredProperty(property, prop.getType(), prop.getPropertyNode());
/*   0*/    } 
/*  92*/    this.isFrozen = true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEquivalentTo(JSType other) {
/*  97*/    if (!other.isRecordType())
/*  98*/      return false; 
/* 102*/    RecordType otherRecord = other.toMaybeRecordType();
/* 103*/    if (otherRecord == this)
/* 104*/      return true; 
/* 107*/    Set<String> keySet = this.properties.keySet();
/* 108*/    Map<String, JSType> otherProps = otherRecord.properties;
/* 109*/    if (!otherProps.keySet().equals(keySet))
/* 110*/      return false; 
/* 112*/    for (String key : keySet) {
/* 113*/      if (!((JSType)otherProps.get(key)).isEquivalentTo(this.properties.get(key)))
/* 114*/        return false; 
/*   0*/    } 
/* 117*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectType getImplicitPrototype() {
/* 122*/    return this.registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE);
/*   0*/  }
/*   0*/  
/*   0*/  boolean defineProperty(String propertyName, JSType type, boolean inferred, Node propertyNode) {
/* 128*/    if (this.isFrozen)
/* 129*/      return false; 
/* 132*/    if (!inferred)
/* 133*/      this.properties.put(propertyName, type); 
/* 136*/    return super.defineProperty(propertyName, type, inferred, propertyNode);
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getLeastSupertype(JSType that) {
/* 142*/    if (!that.isNumberObjectType())
/* 143*/      return super.getLeastSupertype(that); 
/* 145*/    RecordTypeBuilder builder = new RecordTypeBuilder(this.registry);
/* 146*/    for (String property : this.properties.keySet()) {
/* 147*/      if (that.toMaybeRecordType().hasProperty(property) && that.toMaybeRecordType().getPropertyType(property).isEquivalentTo(getPropertyType(property)))
/* 150*/        builder.addProperty(property, getPropertyType(property), getPropertyNode(property)); 
/*   0*/    } 
/* 154*/    return builder.build();
/*   0*/  }
/*   0*/  
/*   0*/  JSType getGreatestSubtypeHelper(JSType that) {
/* 157*/    if (that.isRecordType()) {
/* 158*/      RecordType thatRecord = that.toMaybeRecordType();
/* 159*/      RecordTypeBuilder builder = new RecordTypeBuilder(this.registry);
/* 164*/      for (String property : this.properties.keySet()) {
/* 165*/        if (thatRecord.hasProperty(property) && !thatRecord.getPropertyType(property).isEquivalentTo(getPropertyType(property)))
/* 168*/          return this.registry.getNativeObjectType(JSTypeNative.NO_TYPE); 
/* 171*/        builder.addProperty(property, getPropertyType(property), getPropertyNode(property));
/*   0*/      } 
/* 175*/      for (String property : thatRecord.properties.keySet()) {
/* 176*/        if (!hasProperty(property))
/* 177*/          builder.addProperty(property, thatRecord.getPropertyType(property), thatRecord.getPropertyNode(property)); 
/*   0*/      } 
/* 182*/      return builder.build();
/*   0*/    } 
/* 185*/    JSType greatestSubtype = this.registry.getNativeType(JSTypeNative.NO_OBJECT_TYPE);
/* 187*/    JSType thatRestrictedToObj = this.registry.getNativeType(JSTypeNative.OBJECT_TYPE).getGreatestSubtype(that);
/* 190*/    if (!thatRestrictedToObj.isEmptyType())
/* 197*/      for (Map.Entry<String, JSType> entry : this.properties.entrySet()) {
/* 198*/        String propName = entry.getKey();
/* 199*/        JSType propType = entry.getValue();
/* 200*/        UnionTypeBuilder builder = new UnionTypeBuilder(this.registry);
/* 202*/        for (ObjectType alt : this.registry.getEachReferenceTypeWithProperty(propName)) {
/* 203*/          JSType altPropType = alt.getPropertyType(propName);
/* 204*/          if (altPropType != null && !alt.isEquivalentTo(this) && alt.isSubtype(that) && (propType.isUnknownType() || altPropType.isUnknownType() || altPropType.isEquivalentTo(propType)))
/* 208*/            builder.addAlternate(alt); 
/*   0*/        } 
/* 211*/        greatestSubtype = greatestSubtype.getLeastSupertype(builder.build());
/*   0*/      }  
/* 214*/    return greatestSubtype;
/*   0*/  }
/*   0*/  
/*   0*/  RecordType toMaybeRecordType() {
/* 219*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSubtype(JSType that) {
/* 224*/    if (JSType.isSubtypeHelper(this, that))
/* 225*/      return true; 
/* 229*/    if (this.registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE).isSubtype(that))
/* 231*/      return true; 
/* 237*/    if (!that.isRecordType())
/* 238*/      return false; 
/* 241*/    return isSubtype(this, that.toMaybeRecordType());
/*   0*/  }
/*   0*/  
/*   0*/  static boolean isSubtype(ObjectType typeA, RecordType typeB) {
/* 263*/    for (String property : typeB.properties.keySet()) {
/* 264*/      if (!typeA.hasProperty(property))
/* 265*/        return false; 
/* 268*/      JSType propA = typeA.getPropertyType(property);
/* 269*/      JSType propB = typeB.getPropertyType(property);
/* 270*/      if (!propA.isUnknownType() && !propB.isUnknownType()) {
/* 271*/        if (typeA.isPropertyTypeDeclared(property)) {
/* 272*/          if (!propA.isEquivalentTo(propB))
/* 273*/            return false; 
/*   0*/          continue;
/*   0*/        } 
/* 276*/        if (!propA.isSubtype(propB))
/* 277*/          return false; 
/*   0*/      } 
/*   0*/    } 
/* 283*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  JSType resolveInternal(ErrorReporter t, StaticScope<JSType> scope) {
/* 288*/    for (Map.Entry<String, JSType> entry : this.properties.entrySet()) {
/* 289*/      JSType type = entry.getValue();
/* 290*/      JSType resolvedType = type.resolve(t, scope);
/* 291*/      if (type != resolvedType)
/* 292*/        this.properties.put(entry.getKey(), resolvedType); 
/*   0*/    } 
/* 295*/    return super.resolveInternal(t, scope);
/*   0*/  }
/*   0*/}
