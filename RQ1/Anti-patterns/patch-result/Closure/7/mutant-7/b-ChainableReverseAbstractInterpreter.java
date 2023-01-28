/*   0*/package com.google.javascript.jscomp.type;
/*   0*/
/*   0*/import com.google.common.base.Preconditions;
/*   0*/import com.google.javascript.jscomp.CodingConvention;
/*   0*/import com.google.javascript.rhino.Node;
/*   0*/import com.google.javascript.rhino.jstype.EnumElementType;
/*   0*/import com.google.javascript.rhino.jstype.FunctionType;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeNative;
/*   0*/import com.google.javascript.rhino.jstype.JSTypeRegistry;
/*   0*/import com.google.javascript.rhino.jstype.ObjectType;
/*   0*/import com.google.javascript.rhino.jstype.ParameterizedType;
/*   0*/import com.google.javascript.rhino.jstype.StaticSlot;
/*   0*/import com.google.javascript.rhino.jstype.TemplateType;
/*   0*/import com.google.javascript.rhino.jstype.UnionType;
/*   0*/import com.google.javascript.rhino.jstype.Visitor;
/*   0*/
/*   0*/public abstract class ChainableReverseAbstractInterpreter implements ReverseAbstractInterpreter {
/*   0*/  protected final CodingConvention convention;
/*   0*/  
/*   0*/  final JSTypeRegistry typeRegistry;
/*   0*/  
/*   0*/  private ChainableReverseAbstractInterpreter firstLink;
/*   0*/  
/*   0*/  private ChainableReverseAbstractInterpreter nextLink;
/*   0*/  
/*   0*/  private final Visitor<JSType> restrictUndefinedVisitor;
/*   0*/  
/*   0*/  private final Visitor<JSType> restrictNullVisitor;
/*   0*/  
/*   0*/  public ChainableReverseAbstractInterpreter(CodingConvention convention, JSTypeRegistry typeRegistry) {
/* 186*/    this.restrictUndefinedVisitor = new Visitor<JSType>() {
/*   0*/        public JSType caseEnumElementType(EnumElementType enumElementType) {
/* 190*/          JSType type = enumElementType.getPrimitiveType().<JSType>visit(this);
/* 191*/          if (type != null && enumElementType.getPrimitiveType().isEquivalentTo(type))
/* 192*/            return enumElementType; 
/* 194*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseAllType() {
/* 200*/          return ChainableReverseAbstractInterpreter.this.typeRegistry.createUnionType(new JSTypeNative[] { JSTypeNative.OBJECT_TYPE, JSTypeNative.NUMBER_TYPE, JSTypeNative.STRING_TYPE, JSTypeNative.BOOLEAN_TYPE, JSTypeNative.NULL_TYPE });
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNoObjectType() {
/* 206*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_OBJECT_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNoType() {
/* 211*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseBooleanType() {
/* 216*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.BOOLEAN_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseFunctionType(FunctionType type) {
/* 221*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNullType() {
/* 226*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NULL_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNumberType() {
/* 231*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NUMBER_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseObjectType(ObjectType type) {
/* 236*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseStringType() {
/* 241*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.STRING_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseUnionType(UnionType type) {
/* 246*/          return type.getRestrictedUnion(ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.VOID_TYPE));
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseUnknownType() {
/* 251*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseVoidType() {
/* 256*/          return null;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseParameterizedType(ParameterizedType type) {
/* 261*/          return caseObjectType(type);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseTemplateType(TemplateType templateType) {
/* 266*/          return caseObjectType(templateType);
/*   0*/        }
/*   0*/      };
/* 274*/    this.restrictNullVisitor = new Visitor<JSType>() {
/*   0*/        public JSType caseEnumElementType(EnumElementType enumElementType) {
/* 278*/          JSType type = enumElementType.getPrimitiveType().<JSType>visit(this);
/* 279*/          if (type != null && enumElementType.getPrimitiveType().isEquivalentTo(type))
/* 281*/            return enumElementType; 
/* 283*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseAllType() {
/* 289*/          return ChainableReverseAbstractInterpreter.this.typeRegistry.createUnionType(new JSTypeNative[] { JSTypeNative.OBJECT_TYPE, JSTypeNative.NUMBER_TYPE, JSTypeNative.STRING_TYPE, JSTypeNative.BOOLEAN_TYPE, JSTypeNative.VOID_TYPE });
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNoObjectType() {
/* 295*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_OBJECT_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNoType() {
/* 300*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseBooleanType() {
/* 305*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.BOOLEAN_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseFunctionType(FunctionType type) {
/* 310*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNullType() {
/* 315*/          return null;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseNumberType() {
/* 320*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NUMBER_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseObjectType(ObjectType type) {
/* 325*/          return type;
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseStringType() {
/* 330*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.STRING_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseUnionType(UnionType type) {
/* 335*/          return type.getRestrictedUnion(ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NULL_TYPE));
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseUnknownType() {
/* 340*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.UNKNOWN_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseVoidType() {
/* 345*/          return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.VOID_TYPE);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseParameterizedType(ParameterizedType type) {
/* 350*/          return caseObjectType(type);
/*   0*/        }
/*   0*/        
/*   0*/        public JSType caseTemplateType(TemplateType templateType) {
/* 355*/          return caseObjectType(templateType);
/*   0*/        }
/*   0*/      };
/*   0*/    Preconditions.checkNotNull(convention);
/*   0*/    this.convention = convention;
/*   0*/    this.typeRegistry = typeRegistry;
/*   0*/    this.firstLink = this;
/*   0*/    this.nextLink = null;
/*   0*/  }
/*   0*/  
/*   0*/  public ChainableReverseAbstractInterpreter append(ChainableReverseAbstractInterpreter lastLink) {
/*   0*/    Preconditions.checkArgument((lastLink.nextLink == null));
/*   0*/    this.nextLink = lastLink;
/*   0*/    lastLink.firstLink = this.firstLink;
/*   0*/    return lastLink;
/*   0*/  }
/*   0*/  
/*   0*/  public ChainableReverseAbstractInterpreter getFirst() {
/*   0*/    return this.firstLink;
/*   0*/  }
/*   0*/  
/*   0*/  protected FlowScope firstPreciserScopeKnowingConditionOutcome(Node condition, FlowScope blindScope, boolean outcome) {
/*   0*/    return this.firstLink.getPreciserScopeKnowingConditionOutcome(condition, blindScope, outcome);
/*   0*/  }
/*   0*/  
/*   0*/  protected FlowScope nextPreciserScopeKnowingConditionOutcome(Node condition, FlowScope blindScope, boolean outcome) {
/*   0*/    return (this.nextLink != null) ? this.nextLink.getPreciserScopeKnowingConditionOutcome(condition, blindScope, outcome) : blindScope;
/*   0*/  }
/*   0*/  
/*   0*/  protected JSType getTypeIfRefinable(Node node, FlowScope scope) {
/*   0*/    StaticSlot<JSType> nameVar;
/*   0*/    String qualifiedName;
/*   0*/    StaticSlot<JSType> propVar;
/*   0*/    JSType propVarType;
/*   0*/    switch (node.getType()) {
/*   0*/      case 38:
/*   0*/        nameVar = scope.getSlot(node.getString());
/*   0*/        if (nameVar != null) {
/*   0*/          JSType nameVarType = nameVar.getType();
/*   0*/          if (nameVarType == null)
/*   0*/            nameVarType = node.getJSType(); 
/*   0*/          return nameVarType;
/*   0*/        } 
/*   0*/        return null;
/*   0*/      case 33:
/*   0*/        qualifiedName = node.getQualifiedName();
/*   0*/        if (qualifiedName == null)
/*   0*/          return null; 
/*   0*/        propVar = scope.getSlot(qualifiedName);
/*   0*/        propVarType = null;
/*   0*/        if (propVar != null)
/*   0*/          propVarType = propVar.getType(); 
/*   0*/        if (propVarType == null)
/*   0*/          propVarType = node.getJSType(); 
/*   0*/        if (propVarType == null)
/*   0*/          propVarType = getNativeType(JSTypeNative.UNKNOWN_TYPE); 
/*   0*/        return propVarType;
/*   0*/    } 
/*   0*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  protected void declareNameInScope(FlowScope scope, Node node, JSType type) {
/*   0*/    String qualifiedName;
/*   0*/    JSType origType;
/*   0*/    switch (node.getType()) {
/*   0*/      case 38:
/*   0*/        scope.inferSlotType(node.getString(), type);
/*   0*/        break;
/*   0*/      case 33:
/*   0*/        qualifiedName = node.getQualifiedName();
/*   0*/        Preconditions.checkNotNull(qualifiedName);
/*   0*/        origType = node.getJSType();
/*   0*/        origType = (origType == null) ? getNativeType(JSTypeNative.UNKNOWN_TYPE) : origType;
/*   0*/        scope.inferQualifiedSlot(node, qualifiedName, origType, type);
/*   0*/        break;
/*   0*/      case 42:
/*   0*/        break;
/*   0*/      default:
/*   0*/        throw new IllegalArgumentException("Node cannot be refined. \n" + node.toStringTree());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  abstract class RestrictByTypeOfResultVisitor implements Visitor<JSType> {
/*   0*/    public JSType caseAllType() {
/* 378*/      return caseTopType(ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.ALL_TYPE));
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseUnknownType() {
/* 383*/      return caseTopType(ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE));
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseUnionType(UnionType type) {
/* 388*/      JSType restricted = null;
/* 389*/      for (JSType alternate : type.getAlternates()) {
/* 390*/        JSType restrictedAlternate = alternate.<JSType>visit(this);
/* 391*/        if (restrictedAlternate != null) {
/* 392*/          if (restricted == null) {
/* 393*/            restricted = restrictedAlternate;
/*   0*/            continue;
/*   0*/          } 
/* 395*/          restricted = restrictedAlternate.getLeastSupertype(restricted);
/*   0*/        } 
/*   0*/      } 
/* 399*/      return restricted;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNoType() {
/* 404*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseEnumElementType(EnumElementType enumElementType) {
/* 418*/      JSType type = enumElementType.getPrimitiveType().<JSType>visit(this);
/* 419*/      if (type != null && enumElementType.getPrimitiveType().isEquivalentTo(type))
/* 421*/        return enumElementType; 
/* 423*/      return type;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseParameterizedType(ParameterizedType type) {
/* 429*/      return caseObjectType(type);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseTemplateType(TemplateType templateType) {
/* 434*/      return caseObjectType(templateType);
/*   0*/    }
/*   0*/    
/*   0*/    protected abstract JSType caseTopType(JSType param1JSType);
/*   0*/  }
/*   0*/  
/*   0*/  abstract class RestrictByTrueTypeOfResultVisitor extends RestrictByTypeOfResultVisitor {
/*   0*/    public JSType caseNoObjectType() {
/* 447*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseBooleanType() {
/* 452*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseFunctionType(FunctionType type) {
/* 457*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNullType() {
/* 462*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNumberType() {
/* 467*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseObjectType(ObjectType type) {
/* 472*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseStringType() {
/* 477*/      return null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseVoidType() {
/* 482*/      return null;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  abstract class RestrictByFalseTypeOfResultVisitor extends RestrictByTypeOfResultVisitor {
/*   0*/    protected JSType caseTopType(JSType topType) {
/* 495*/      return topType;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNoObjectType() {
/* 500*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_OBJECT_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseBooleanType() {
/* 505*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.BOOLEAN_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseFunctionType(FunctionType type) {
/* 510*/      return type;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNullType() {
/* 515*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NULL_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNumberType() {
/* 520*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NUMBER_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseObjectType(ObjectType type) {
/* 525*/      return type;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseStringType() {
/* 530*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.STRING_TYPE);
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseVoidType() {
/* 535*/      return ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.VOID_TYPE);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private class RestrictByOneTypeOfResultVisitor extends RestrictByTypeOfResultVisitor {
/*   0*/    private final String value;
/*   0*/    
/*   0*/    private final boolean resultEqualsValue;
/*   0*/    
/*   0*/    RestrictByOneTypeOfResultVisitor(String value, boolean resultEqualsValue) {
/* 558*/      this.value = value;
/* 559*/      this.resultEqualsValue = resultEqualsValue;
/*   0*/    }
/*   0*/    
/*   0*/    private boolean matchesExpectation(String result) {
/* 568*/      return (result.equals(this.value) == this.resultEqualsValue);
/*   0*/    }
/*   0*/    
/*   0*/    protected JSType caseTopType(JSType topType) {
/* 573*/      JSType result = topType;
/* 574*/      if (this.resultEqualsValue) {
/* 575*/        JSType typeByName = ChainableReverseAbstractInterpreter.this.getNativeTypeForTypeOf(this.value);
/* 576*/        if (typeByName != null)
/* 577*/          result = typeByName; 
/*   0*/      } 
/* 580*/      return result;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNoObjectType() {
/* 585*/      return (((this.value.equals("object") || this.value.equals("function"))) == this.resultEqualsValue) ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NO_OBJECT_TYPE) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseBooleanType() {
/* 591*/      return matchesExpectation("boolean") ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.BOOLEAN_TYPE) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseFunctionType(FunctionType type) {
/* 596*/      return matchesExpectation("function") ? type : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNullType() {
/* 601*/      return matchesExpectation("object") ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NULL_TYPE) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseNumberType() {
/* 606*/      return matchesExpectation("number") ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.NUMBER_TYPE) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseObjectType(ObjectType type) {
/* 611*/      "function";
/* 611*/      if (this.resultEqualsValue) {
/* 612*/        JSType ctorType = ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE);
/* 613*/        return (this.resultEqualsValue && ctorType.isSubtype(type)) ? ctorType : null;
/*   0*/      } 
/* 617*/      return matchesExpectation("object") ? type : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseStringType() {
/* 622*/      return matchesExpectation("string") ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.STRING_TYPE) : null;
/*   0*/    }
/*   0*/    
/*   0*/    public JSType caseVoidType() {
/* 627*/      return matchesExpectation("undefined") ? ChainableReverseAbstractInterpreter.this.getNativeType(JSTypeNative.VOID_TYPE) : null;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  protected final JSType getRestrictedWithoutUndefined(JSType type) {
/* 635*/    return (type == null) ? null : type.<JSType>visit(this.restrictUndefinedVisitor);
/*   0*/  }
/*   0*/  
/*   0*/  protected final JSType getRestrictedWithoutNull(JSType type) {
/* 642*/    return (type == null) ? null : type.<JSType>visit(this.restrictNullVisitor);
/*   0*/  }
/*   0*/  
/*   0*/  JSType getRestrictedByTypeOfResult(JSType type, String value, boolean resultEqualsValue) {
/* 674*/    if (type == null) {
/* 675*/      if (resultEqualsValue) {
/* 676*/        JSType result = getNativeTypeForTypeOf(value);
/* 677*/        return (result == null) ? getNativeType(JSTypeNative.CHECKED_UNKNOWN_TYPE) : result;
/*   0*/      } 
/* 679*/      return null;
/*   0*/    } 
/* 682*/    return type.<JSType>visit(new RestrictByOneTypeOfResultVisitor(value, resultEqualsValue));
/*   0*/  }
/*   0*/  
/*   0*/  JSType getNativeType(JSTypeNative typeId) {
/* 687*/    return this.typeRegistry.getNativeType(typeId);
/*   0*/  }
/*   0*/  
/*   0*/  private JSType getNativeTypeForTypeOf(String value) {
/* 700*/    if (value.equals("number"))
/* 701*/      return getNativeType(JSTypeNative.NUMBER_TYPE); 
/* 702*/    if (value.equals("boolean"))
/* 703*/      return getNativeType(JSTypeNative.BOOLEAN_TYPE); 
/* 704*/    if (value.equals("string"))
/* 705*/      return getNativeType(JSTypeNative.STRING_TYPE); 
/* 706*/    if (value.equals("undefined"))
/* 707*/      return getNativeType(JSTypeNative.VOID_TYPE); 
/* 708*/    if (value.equals("function"))
/* 709*/      return getNativeType(JSTypeNative.U2U_CONSTRUCTOR_TYPE); 
/* 711*/    return null;
/*   0*/  }
/*   0*/}
