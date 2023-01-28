/*   0*/package com.google.javascript.rhino.jstype;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.common.collect.ImmutableList;
/*   0*/import com.google.common.collect.Maps;
/*   0*/import com.google.common.collect.Sets;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.JSDocInfo;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import java.util.Map;
/*   0*/import java.util.Set;
/*   0*/
/*   0*/class PrototypeObjectType extends ObjectType {
/*   0*/  private static final long serialVersionUID = 1L;
/*   0*/  
/*   0*/  private final String className;
/*   0*/  
/*   0*/  private final Map<String, ObjectType.Property> properties;
/*   0*/  
/*   0*/  private final boolean nativeType;
/*   0*/  
/*   0*/  private ObjectType implicitPrototypeFallback;
/*   0*/  
/*  85*/  private FunctionType ownerFunction = null;
/*   0*/  
/*   0*/  private boolean prettyPrint = false;
/*   0*/  
/*   0*/  private static final int MAX_PRETTY_PRINTED_PROPERTIES = 4;
/*   0*/  
/*   0*/  PrototypeObjectType(JSTypeRegistry registry, String className, ObjectType implicitPrototype) {
/* 106*/    this(registry, className, implicitPrototype, false);
/*   0*/  }
/*   0*/  
/*   0*/  PrototypeObjectType(JSTypeRegistry registry, String className, ObjectType implicitPrototype, boolean nativeType) {
/* 115*/    super(registry);
/* 116*/    this.properties = Maps.newTreeMap();
/* 117*/    this.className = className;
/* 118*/    this.nativeType = nativeType;
/* 119*/    if (nativeType || implicitPrototype != null) {
/* 120*/      setImplicitPrototype(implicitPrototype);
/*   0*/    } else {
/* 122*/      setImplicitPrototype(registry.getNativeObjectType(JSTypeNative.OBJECT_TYPE));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectType.Property getSlot(String name) {
/* 129*/    if (this.properties.containsKey(name))
/* 130*/      return this.properties.get(name); 
/* 132*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 133*/    if (implicitPrototype != null) {
/* 134*/      ObjectType.Property prop = implicitPrototype.getSlot(name);
/* 135*/      if (prop != null)
/* 136*/        return prop; 
/*   0*/    } 
/* 139*/    for (ObjectType interfaceType : getCtorExtendedInterfaces()) {
/* 140*/      ObjectType.Property prop = interfaceType.getSlot(name);
/* 141*/      if (prop != null)
/* 142*/        return prop; 
/*   0*/    } 
/* 145*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public int getPropertiesCount() {
/* 153*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 154*/    if (implicitPrototype == null)
/* 155*/      return this.properties.size(); 
/* 157*/    int localCount = 0;
/* 158*/    for (String property : this.properties.keySet()) {
/* 159*/      if (!implicitPrototype.hasProperty(property))
/* 160*/        localCount++; 
/*   0*/    } 
/* 163*/    return implicitPrototype.getPropertiesCount() + localCount;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasProperty(String propertyName) {
/* 169*/    return (isUnknownType() || getSlot(propertyName) != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasOwnProperty(String propertyName) {
/* 174*/    return (this.properties.get(propertyName) != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Set<String> getOwnPropertyNames() {
/* 179*/    return this.properties.keySet();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPropertyTypeDeclared(String property) {
/* 184*/    StaticSlot<JSType> slot = getSlot(property);
/* 185*/    if (slot == null)
/* 186*/      return false; 
/* 188*/    return !slot.isTypeInferred();
/*   0*/  }
/*   0*/  
/*   0*/  void collectPropertyNames(Set<String> props) {
/* 193*/    for (String prop : this.properties.keySet())
/* 194*/      props.add(prop); 
/* 196*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 197*/    if (implicitPrototype != null)
/* 198*/      implicitPrototype.collectPropertyNames(props); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPropertyTypeInferred(String property) {
/* 204*/    StaticSlot<JSType> slot = getSlot(property);
/* 205*/    if (slot == null)
/* 206*/      return false; 
/* 208*/    return slot.isTypeInferred();
/*   0*/  }
/*   0*/  
/*   0*/  public JSType getPropertyType(String property) {
/* 213*/    StaticSlot<JSType> slot = getSlot(property);
/* 214*/    if (slot == null)
/* 215*/      return getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/* 217*/    return slot.getType();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isPropertyInExterns(String propertyName) {
/* 222*/    ObjectType.Property p = this.properties.get(propertyName);
/* 223*/    if (p != null)
/* 224*/      return p.isFromExterns(); 
/* 226*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 227*/    if (implicitPrototype != null)
/* 228*/      return implicitPrototype.isPropertyInExterns(propertyName); 
/* 230*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  boolean defineProperty(String name, JSType type, boolean inferred, Node propertyNode) {
/* 236*/    if (hasOwnDeclaredProperty(name))
/* 237*/      return false; 
/* 239*/    ObjectType.Property newProp = new ObjectType.Property(name, type, inferred, propertyNode);
/* 241*/    ObjectType.Property oldProp = this.properties.get(name);
/* 242*/    if (oldProp != null)
/* 245*/      newProp.setJSDocInfo(oldProp.getJSDocInfo()); 
/* 247*/    this.properties.put(name, newProp);
/* 248*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeProperty(String name) {
/* 253*/    return (this.properties.remove(name) != null);
/*   0*/  }
/*   0*/  
/*   0*/  public Node getPropertyNode(String propertyName) {
/* 258*/    ObjectType.Property p = this.properties.get(propertyName);
/* 259*/    if (p != null)
/* 260*/      return p.getNode(); 
/* 262*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 263*/    if (implicitPrototype != null)
/* 264*/      return implicitPrototype.getPropertyNode(propertyName); 
/* 266*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public JSDocInfo getOwnPropertyJSDocInfo(String propertyName) {
/* 271*/    ObjectType.Property p = this.properties.get(propertyName);
/* 272*/    if (p != null)
/* 273*/      return p.getJSDocInfo(); 
/* 275*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPropertyJSDocInfo(String propertyName, JSDocInfo info) {
/* 280*/    if (info != null) {
/* 281*/      if (!this.properties.containsKey(propertyName))
/* 285*/        defineInferredProperty(propertyName, getPropertyType(propertyName), null); 
/* 291*/      ObjectType.Property property = this.properties.get(propertyName);
/* 292*/      if (property != null)
/* 293*/        property.setJSDocInfo(info); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matchesNumberContext() {
/* 300*/    return (isNumberObjectType() || isDateType() || isBooleanObjectType() || isStringObjectType() || hasOverridenNativeProperty("valueOf"));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matchesStringContext() {
/* 306*/    return (isTheObjectType() || isStringObjectType() || isDateType() || isRegexpType() || isArrayType() || isNumberObjectType() || isBooleanObjectType() || hasOverridenNativeProperty("toString"));
/*   0*/  }
/*   0*/  
/*   0*/  private boolean hasOverridenNativeProperty(String propertyName) {
/* 316*/    if (isNativeObjectType())
/* 317*/      return false; 
/* 320*/    JSType propertyType = getPropertyType(propertyName);
/* 321*/    ObjectType nativeType = isFunctionType() ? this.registry.getNativeObjectType(JSTypeNative.FUNCTION_PROTOTYPE) : this.registry.getNativeObjectType(JSTypeNative.OBJECT_PROTOTYPE);
/* 325*/    JSType nativePropertyType = nativeType.getPropertyType(propertyName);
/* 326*/    return (propertyType != nativePropertyType);
/*   0*/  }
/*   0*/  
/*   0*/  public JSType unboxesTo() {
/* 331*/    if (isStringObjectType())
/* 332*/      return getNativeType(JSTypeNative.STRING_TYPE); 
/* 333*/    if (isBooleanObjectType())
/* 334*/      return getNativeType(JSTypeNative.BOOLEAN_TYPE); 
/* 335*/    if (isNumberObjectType())
/* 336*/      return getNativeType(JSTypeNative.NUMBER_TYPE); 
/* 338*/    return super.unboxesTo();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matchesObjectContext() {
/* 344*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canBeCalled() {
/* 349*/    return isRegexpType();
/*   0*/  }
/*   0*/  
/*   0*/  String toStringHelper(boolean forAnnotations) {
/* 354*/    if (hasReferenceName())
/* 355*/      return getReferenceName(); 
/* 356*/    if (this.prettyPrint) {
/* 358*/      this.prettyPrint = false;
/* 361*/      Set<String> propertyNames = Sets.newTreeSet();
/* 362*/      ObjectType current = this;
/* 363*/      for (; current != null && !current.isNativeObjectType() && propertyNames.size() <= 4; 
/*   0*/        
/* 365*/        current = current.getImplicitPrototype())
/* 366*/        propertyNames.addAll(current.getOwnPropertyNames()); 
/* 369*/      StringBuilder sb = new StringBuilder();
/* 370*/      sb.append("{");
/* 372*/      int i = 0;
/* 373*/      for (String property : propertyNames) {
/* 374*/        if (i > 0)
/* 375*/          sb.append(", "); 
/* 378*/        sb.append(property);
/* 379*/        sb.append(": ");
/* 380*/        sb.append(getPropertyType(property).toStringHelper(forAnnotations));
/* 382*/        i++;
/* 383*/        if (!forAnnotations && i == 4) {
/* 384*/          sb.append(", ...");
/*   0*/          break;
/*   0*/        } 
/*   0*/      } 
/* 389*/      sb.append("}");
/* 391*/      this.prettyPrint = true;
/* 392*/      return sb.toString();
/*   0*/    } 
/* 394*/    return forAnnotations ? "?" : "{...}";
/*   0*/  }
/*   0*/  
/*   0*/  void setPrettyPrint(boolean prettyPrint) {
/* 399*/    this.prettyPrint = prettyPrint;
/*   0*/  }
/*   0*/  
/*   0*/  boolean isPrettyPrint() {
/* 403*/    return this.prettyPrint;
/*   0*/  }
/*   0*/  
/*   0*/  public FunctionType getConstructor() {
/* 408*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public ObjectType getImplicitPrototype() {
/* 413*/    return this.implicitPrototypeFallback;
/*   0*/  }
/*   0*/  
/*   0*/  final void setImplicitPrototype(ObjectType implicitPrototype) {
/* 423*/    Preconditions.checkState(!hasCachedValues());
/* 424*/    this.implicitPrototypeFallback = implicitPrototype;
/*   0*/  }
/*   0*/  
/*   0*/  public String getReferenceName() {
/* 429*/    if (this.className != null)
/* 430*/      return this.className; 
/* 431*/    if (this.ownerFunction != null)
/* 432*/      return this.ownerFunction.getReferenceName() + ".prototype"; 
/* 434*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasReferenceName() {
/* 440*/    return (this.className != null || this.ownerFunction != null);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isSubtype(JSType that) {
/* 445*/    if (JSType.isSubtypeHelper(this, that))
/* 446*/      return true; 
/* 450*/    if (that.isUnionType())
/* 453*/      return false; 
/* 457*/    if (that.isRecordType())
/* 458*/      return RecordType.isSubtype(this, that.toMaybeRecordType()); 
/* 464*/    ObjectType thatObj = that.toObjectType();
/* 465*/    ObjectType thatCtor = (thatObj == null) ? null : thatObj.getConstructor();
/* 466*/    if (thatCtor != null && thatCtor.isInterface()) {
/* 467*/      Iterable<ObjectType> thisInterfaces = getCtorImplementedInterfaces();
/* 468*/      for (ObjectType thisInterface : thisInterfaces) {
/* 469*/        if (thisInterface.isSubtype(that))
/* 470*/          return true; 
/*   0*/      } 
/*   0*/    } 
/* 475*/    if (getConstructor() != null && getConstructor().isInterface())
/* 476*/      for (ObjectType thisInterface : getCtorExtendedInterfaces()) {
/* 477*/        if (thisInterface.isSubtype(that))
/* 478*/          return true; 
/*   0*/      }  
/* 484*/    if (isUnknownType() || implicitPrototypeChainIsUnknown())
/* 488*/      return true; 
/* 490*/    return isImplicitPrototype(thatObj);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean implicitPrototypeChainIsUnknown() {
/* 494*/    ObjectType p = getImplicitPrototype();
/* 495*/    while (p != null) {
/* 496*/      if (p.isUnknownType())
/* 497*/        return true; 
/* 499*/      p = p.getImplicitPrototype();
/*   0*/    } 
/* 501*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasCachedValues() {
/* 506*/    return super.hasCachedValues();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNativeObjectType() {
/* 512*/    return this.nativeType;
/*   0*/  }
/*   0*/  
/*   0*/  void setOwnerFunction(FunctionType type) {
/* 516*/    Preconditions.checkState((this.ownerFunction == null || type == null));
/* 517*/    this.ownerFunction = type;
/*   0*/  }
/*   0*/  
/*   0*/  public FunctionType getOwnerFunction() {
/* 522*/    return this.ownerFunction;
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<ObjectType> getCtorImplementedInterfaces() {
/* 527*/    return isFunctionPrototypeType() ? getOwnerFunction().getImplementedInterfaces() : (Iterable<ObjectType>)ImmutableList.of();
/*   0*/  }
/*   0*/  
/*   0*/  public Iterable<ObjectType> getCtorExtendedInterfaces() {
/* 534*/    return isFunctionPrototypeType() ? getOwnerFunction().getExtendedInterfaces() : (Iterable<ObjectType>)ImmutableList.of();
/*   0*/  }
/*   0*/  
/*   0*/  JSType resolveInternal(ErrorReporter t, StaticScope<JSType> scope) {
/* 541*/    setResolvedTypeInternal(this);
/* 543*/    ObjectType implicitPrototype = getImplicitPrototype();
/* 544*/    if (implicitPrototype != null)
/* 545*/      this.implicitPrototypeFallback = (ObjectType)implicitPrototype.resolve(t, scope); 
/* 548*/    for (ObjectType.Property prop : this.properties.values())
/* 549*/      prop.setType(safeResolve(prop.getType(), t, scope)); 
/* 551*/    return this;
/*   0*/  }
/*   0*/  
/*   0*/  public void matchConstraint(ObjectType constraintObj) {
/* 567*/    if (constraintObj.isRecordType())
/* 568*/      for (String prop : constraintObj.getOwnPropertyNames()) {
/* 569*/        JSType propType = constraintObj.getPropertyType(prop);
/* 570*/        if (!isPropertyTypeDeclared(prop)) {
/* 571*/          JSType typeToInfer = propType;
/* 572*/          if (!hasProperty(prop))
/* 573*/            typeToInfer = getNativeType(JSTypeNative.VOID_TYPE).getLeastSupertype(propType); 
/* 576*/          defineInferredProperty(prop, typeToInfer, null);
/*   0*/        } 
/*   0*/      }  
/*   0*/  }
/*   0*/}
