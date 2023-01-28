/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.lang.reflect.ParameterizedType;
/*   0*/import java.lang.reflect.Type;
/*   0*/import java.lang.reflect.TypeVariable;
/*   0*/import java.lang.reflect.WildcardType;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.LinkedHashMap;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.util.Checks;
/*   0*/
/*   0*/public abstract class GenericMetadataSupport {
/*  63*/  protected Map<TypeVariable, Type> contextualActualTypeParameters = new HashMap<TypeVariable, Type>();
/*   0*/  
/*   0*/  protected void registerTypeVariablesOn(Type classType) {
/*  67*/    if (!(classType instanceof ParameterizedType))
/*   0*/      return; 
/*  70*/    ParameterizedType parameterizedType = (ParameterizedType)classType;
/*  71*/    TypeVariable[] typeParameters = ((Class)parameterizedType.getRawType()).getTypeParameters();
/*  72*/    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
/*  73*/    for (int i = 0; i < actualTypeArguments.length; i++) {
/*  74*/      TypeVariable typeParameter = typeParameters[i];
/*  75*/      Type actualTypeArgument = actualTypeArguments[i];
/*  77*/      if (actualTypeArgument instanceof WildcardType) {
/*  78*/        this.contextualActualTypeParameters.put(typeParameter, boundsOf((WildcardType)actualTypeArgument));
/*   0*/      } else {
/*  80*/        this.contextualActualTypeParameters.put(typeParameter, actualTypeArgument);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void registerTypeParametersOn(TypeVariable[] typeParameters) {
/*  87*/    for (TypeVariable typeVariable : typeParameters)
/*  88*/      registerTypeVariableIfNotPresent(typeVariable); 
/*   0*/  }
/*   0*/  
/*   0*/  private void registerTypeVariableIfNotPresent(TypeVariable typeVariable) {
/*  93*/    if (!this.contextualActualTypeParameters.containsKey(typeVariable))
/*  94*/      this.contextualActualTypeParameters.put(typeVariable, boundsOf(typeVariable)); 
/*   0*/  }
/*   0*/  
/*   0*/  private BoundedType boundsOf(TypeVariable typeParameter) {
/* 105*/    if (typeParameter.getBounds()[0] instanceof TypeVariable)
/* 106*/      return boundsOf((TypeVariable)typeParameter.getBounds()[0]); 
/* 108*/    return new TypeVarBoundedType(typeParameter);
/*   0*/  }
/*   0*/  
/*   0*/  private BoundedType boundsOf(WildcardType wildCard) {
/* 123*/    WildCardBoundedType wildCardBoundedType = new WildCardBoundedType(wildCard);
/* 124*/    if (wildCardBoundedType.firstBound() instanceof TypeVariable)
/* 125*/      return boundsOf((TypeVariable)wildCardBoundedType.firstBound()); 
/* 128*/    return wildCardBoundedType;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract Class<?> rawType();
/*   0*/  
/*   0*/  public List<Type> extraInterfaces() {
/* 144*/    return Collections.emptyList();
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?>[] rawExtraInterfaces() {
/* 151*/    return new Class<?>[0];
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasRawExtraInterfaces() {
/* 158*/    return ((rawExtraInterfaces()).length > 0);
/*   0*/  }
/*   0*/  
/*   0*/  public Map<TypeVariable, Type> actualTypeArguments() {
/* 167*/    TypeVariable[] typeParameters = (TypeVariable[])rawType().getTypeParameters();
/* 168*/    LinkedHashMap<TypeVariable, Type> actualTypeArguments = new LinkedHashMap<TypeVariable, Type>();
/* 170*/    for (TypeVariable typeParameter : typeParameters) {
/* 172*/      Type actualType = getActualTypeArgumentFor(typeParameter);
/* 174*/      actualTypeArguments.put(typeParameter, actualType);
/*   0*/    } 
/* 178*/    return actualTypeArguments;
/*   0*/  }
/*   0*/  
/*   0*/  protected Type getActualTypeArgumentFor(TypeVariable typeParameter) {
/* 182*/    Type type = this.contextualActualTypeParameters.get(typeParameter);
/* 183*/    if (type instanceof TypeVariable) {
/* 184*/      TypeVariable typeVariable = (TypeVariable)type;
/* 185*/      return getActualTypeArgumentFor(typeVariable);
/*   0*/    } 
/* 188*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  public GenericMetadataSupport resolveGenericReturnType(Method method) {
/* 200*/    Type genericReturnType = method.getGenericReturnType();
/* 203*/    if (genericReturnType instanceof Class)
/* 204*/      return new NotGenericReturnTypeSupport(genericReturnType); 
/* 206*/    if (genericReturnType instanceof ParameterizedType)
/* 207*/      return new ParameterizedReturnType(this, (TypeVariable[])method.getTypeParameters(), (ParameterizedType)method.getGenericReturnType()); 
/* 209*/    if (genericReturnType instanceof TypeVariable)
/* 210*/      return new TypeVariableReturnType(this, (TypeVariable[])method.getTypeParameters(), (TypeVariable)genericReturnType); 
/* 213*/    throw new MockitoException("Ouch, it shouldn't happen, type '" + genericReturnType.getClass().getCanonicalName() + "' on method : '" + method.toGenericString() + "' is not supported : " + genericReturnType);
/*   0*/  }
/*   0*/  
/*   0*/  public static GenericMetadataSupport inferFrom(Type type) {
/* 229*/    Checks.checkNotNull(type, "type");
/* 230*/    if (type instanceof Class)
/* 231*/      return new FromClassGenericMetadataSupport((Class)type); 
/* 233*/    if (type instanceof ParameterizedType)
/* 234*/      return new FromParameterizedTypeGenericMetadataSupport((ParameterizedType)type); 
/* 237*/    throw new MockitoException("Type meta-data for this Type (" + type.getClass().getCanonicalName() + ") is not supported : " + type);
/*   0*/  }
/*   0*/  
/*   0*/  private static class FromClassGenericMetadataSupport extends GenericMetadataSupport {
/*   0*/    private final Class<?> clazz;
/*   0*/    
/*   0*/    public FromClassGenericMetadataSupport(Class<?> clazz) {
/* 255*/      this.clazz = clazz;
/* 257*/      Class<?> currentExploredClass = clazz;
/* 258*/      for (; currentExploredClass != null && currentExploredClass != Object.class; 
/* 259*/        currentExploredClass = superClassOf(currentExploredClass))
/* 261*/        readActualTypeParametersOnDeclaringClass(currentExploredClass); 
/*   0*/    }
/*   0*/    
/*   0*/    private Class superClassOf(Class currentExploredClass) {
/* 266*/      Type genericSuperclass = currentExploredClass.getGenericSuperclass();
/* 267*/      if (genericSuperclass instanceof ParameterizedType) {
/* 268*/        Type rawType = ((ParameterizedType)genericSuperclass).getRawType();
/* 269*/        return (Class)rawType;
/*   0*/      } 
/* 271*/      return (Class)genericSuperclass;
/*   0*/    }
/*   0*/    
/*   0*/    private void readActualTypeParametersOnDeclaringClass(Class<?> clazz) {
/* 275*/      registerTypeParametersOn((TypeVariable[])clazz.getTypeParameters());
/* 276*/      registerTypeVariablesOn(clazz.getGenericSuperclass());
/* 277*/      for (Type genericInterface : clazz.getGenericInterfaces())
/* 278*/        registerTypeVariablesOn(genericInterface); 
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> rawType() {
/* 284*/      return this.clazz;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class FromParameterizedTypeGenericMetadataSupport extends GenericMetadataSupport {
/*   0*/    private final ParameterizedType parameterizedType;
/*   0*/    
/*   0*/    public FromParameterizedTypeGenericMetadataSupport(ParameterizedType parameterizedType) {
/* 304*/      this.parameterizedType = parameterizedType;
/* 305*/      readActualTypeParameters();
/*   0*/    }
/*   0*/    
/*   0*/    private void readActualTypeParameters() {
/* 309*/      registerTypeVariablesOn(this.parameterizedType.getRawType());
/* 310*/      registerTypeVariablesOn(this.parameterizedType);
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> rawType() {
/* 315*/      return (Class)this.parameterizedType.getRawType();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class ParameterizedReturnType extends GenericMetadataSupport {
/*   0*/    private final ParameterizedType parameterizedType;
/*   0*/    
/*   0*/    private final TypeVariable[] typeParameters;
/*   0*/    
/*   0*/    public ParameterizedReturnType(GenericMetadataSupport source, TypeVariable[] typeParameters, ParameterizedType parameterizedType) {
/* 328*/      this.parameterizedType = parameterizedType;
/* 329*/      this.typeParameters = typeParameters;
/* 330*/      this.contextualActualTypeParameters = source.contextualActualTypeParameters;
/* 332*/      readTypeParameters();
/* 333*/      readTypeVariables();
/*   0*/    }
/*   0*/    
/*   0*/    private void readTypeParameters() {
/* 337*/      registerTypeParametersOn(this.typeParameters);
/*   0*/    }
/*   0*/    
/*   0*/    private void readTypeVariables() {
/* 341*/      registerTypeVariablesOn(this.parameterizedType);
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> rawType() {
/* 346*/      return (Class)this.parameterizedType.getRawType();
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class TypeVariableReturnType extends GenericMetadataSupport {
/*   0*/    private final TypeVariable typeVariable;
/*   0*/    
/*   0*/    private final TypeVariable[] typeParameters;
/*   0*/    
/*   0*/    private Class<?> rawType;
/*   0*/    
/*   0*/    public TypeVariableReturnType(GenericMetadataSupport source, TypeVariable[] typeParameters, TypeVariable typeVariable) {
/* 363*/      this.typeParameters = typeParameters;
/* 364*/      this.typeVariable = typeVariable;
/* 365*/      this.contextualActualTypeParameters = source.contextualActualTypeParameters;
/* 367*/      readTypeParameters();
/* 368*/      readTypeVariables();
/*   0*/    }
/*   0*/    
/*   0*/    private void readTypeParameters() {
/* 372*/      registerTypeParametersOn(this.typeParameters);
/*   0*/    }
/*   0*/    
/*   0*/    private void readTypeVariables() {
/*   0*/      Type[] arr$;
/*   0*/      int len$, i$;
/* 376*/      for (arr$ = this.typeVariable.getBounds(), len$ = arr$.length, i$ = 1; i$ < len$; ) {
/* 376*/        Type type = arr$[i$];
/* 377*/        registerTypeVariablesOn(type);
/*   0*/        i$++;
/*   0*/      } 
/* 379*/      registerTypeVariablesOn(getActualTypeArgumentFor(this.typeVariable));
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> rawType() {
/* 384*/      if (this.rawType == null)
/* 385*/        this.rawType = extractRawTypeOf(this.typeVariable); 
/* 387*/      return this.rawType;
/*   0*/    }
/*   0*/    
/*   0*/    private Class<?> extractRawTypeOf(Type type) {
/* 391*/      if (type instanceof Class)
/* 392*/        return (Class)type; 
/* 394*/      if (type instanceof ParameterizedType)
/* 395*/        return (Class)((ParameterizedType)type).getRawType(); 
/* 397*/      if (type instanceof GenericMetadataSupport.BoundedType)
/* 398*/        return extractRawTypeOf(((GenericMetadataSupport.BoundedType)type).firstBound()); 
/* 400*/      if (type instanceof TypeVariable)
/* 405*/        return extractRawTypeOf(this.contextualActualTypeParameters.get(type)); 
/* 407*/      throw new MockitoException("Raw extraction not supported for : '" + type + "'");
/*   0*/    }
/*   0*/    
/*   0*/    public List<Type> extraInterfaces() {
/* 412*/      Type type = extractActualBoundedTypeOf(this.typeVariable);
/* 413*/      if (type instanceof GenericMetadataSupport.BoundedType)
/* 414*/        return Arrays.asList(((GenericMetadataSupport.BoundedType)type).interfaceBounds()); 
/* 416*/      if (type instanceof ParameterizedType)
/* 417*/        return Collections.singletonList(type); 
/* 419*/      if (type instanceof Class)
/* 420*/        return Collections.emptyList(); 
/* 422*/      throw new MockitoException("Cannot extract extra-interfaces from '" + this.typeVariable + "' : '" + type + "'");
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?>[] rawExtraInterfaces() {
/* 430*/      List<Type> extraInterfaces = extraInterfaces();
/* 431*/      List<Class<?>> rawExtraInterfaces = new ArrayList<Class<?>>();
/* 432*/      for (Type extraInterface : extraInterfaces) {
/* 433*/        Class<?> rawInterface = extractRawTypeOf(extraInterface);
/* 435*/        if (!rawType().equals(rawInterface))
/* 436*/          rawExtraInterfaces.add(rawInterface); 
/*   0*/      } 
/* 439*/      return rawExtraInterfaces.<Class<?>>toArray(new Class<?>[rawExtraInterfaces.size()]);
/*   0*/    }
/*   0*/    
/*   0*/    private Type extractActualBoundedTypeOf(Type type) {
/* 443*/      if (type instanceof TypeVariable)
/* 448*/        return extractActualBoundedTypeOf(this.contextualActualTypeParameters.get(type)); 
/* 450*/      if (type instanceof GenericMetadataSupport.BoundedType) {
/* 451*/        Type actualFirstBound = extractActualBoundedTypeOf(((GenericMetadataSupport.BoundedType)type).firstBound());
/* 452*/        if (!(actualFirstBound instanceof GenericMetadataSupport.BoundedType))
/* 453*/          return type; 
/* 455*/        return actualFirstBound;
/*   0*/      } 
/* 457*/      return type;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class NotGenericReturnTypeSupport extends GenericMetadataSupport {
/*   0*/    private final Class<?> returnType;
/*   0*/    
/*   0*/    public NotGenericReturnTypeSupport(Type genericReturnType) {
/* 470*/      this.returnType = (Class)genericReturnType;
/*   0*/    }
/*   0*/    
/*   0*/    public Class<?> rawType() {
/* 475*/      return this.returnType;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static interface BoundedType extends Type {
/*   0*/    Type firstBound();
/*   0*/    
/*   0*/    Type[] interfaceBounds();
/*   0*/  }
/*   0*/  
/*   0*/  public static class TypeVarBoundedType implements BoundedType {
/*   0*/    private final TypeVariable typeVariable;
/*   0*/    
/*   0*/    public TypeVarBoundedType(TypeVariable typeVariable) {
/* 520*/      this.typeVariable = typeVariable;
/*   0*/    }
/*   0*/    
/*   0*/    public Type firstBound() {
/* 527*/      return this.typeVariable.getBounds()[0];
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] interfaceBounds() {
/* 538*/      Type[] interfaceBounds = new Type[(this.typeVariable.getBounds()).length - 1];
/* 539*/      System.arraycopy(this.typeVariable.getBounds(), 1, interfaceBounds, 0, (this.typeVariable.getBounds()).length - 1);
/* 540*/      return interfaceBounds;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object o) {
/* 545*/      if (this == o)
/* 545*/        return true; 
/* 546*/      if (o == null || getClass() != o.getClass())
/* 546*/        return false; 
/* 548*/      return this.typeVariable.equals(((TypeVarBoundedType)o).typeVariable);
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 554*/      return this.typeVariable.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 559*/      return "{firstBound=" + firstBound() + ", interfaceBounds=" + Arrays.deepToString((Object[])interfaceBounds()) + '}';
/*   0*/    }
/*   0*/    
/*   0*/    public TypeVariable typeVariable() {
/* 563*/      return this.typeVariable;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static class WildCardBoundedType implements BoundedType {
/*   0*/    private final WildcardType wildcard;
/*   0*/    
/*   0*/    public WildCardBoundedType(WildcardType wildcard) {
/* 580*/      this.wildcard = wildcard;
/*   0*/    }
/*   0*/    
/*   0*/    public Type firstBound() {
/* 587*/      Type[] lowerBounds = this.wildcard.getLowerBounds();
/* 588*/      Type[] upperBounds = this.wildcard.getUpperBounds();
/* 590*/      return (lowerBounds.length != 0) ? lowerBounds[0] : upperBounds[0];
/*   0*/    }
/*   0*/    
/*   0*/    public Type[] interfaceBounds() {
/* 597*/      return new Type[0];
/*   0*/    }
/*   0*/    
/*   0*/    public boolean equals(Object o) {
/* 602*/      if (this == o)
/* 602*/        return true; 
/* 603*/      if (o == null || getClass() != o.getClass())
/* 603*/        return false; 
/* 605*/      return this.wildcard.equals(((GenericMetadataSupport.TypeVarBoundedType)o).typeVariable);
/*   0*/    }
/*   0*/    
/*   0*/    public int hashCode() {
/* 611*/      return this.wildcard.hashCode();
/*   0*/    }
/*   0*/    
/*   0*/    public String toString() {
/* 616*/      return "{firstBound=" + firstBound() + ", interfaceBounds=[]}";
/*   0*/    }
/*   0*/    
/*   0*/    public WildcardType wildCard() {
/* 620*/      return this.wildcard;
/*   0*/    }
/*   0*/  }
/*   0*/}
