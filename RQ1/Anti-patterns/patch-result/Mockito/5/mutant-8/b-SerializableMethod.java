/*   0*/package org.mockito.internal.invocation;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.Arrays;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/
/*   0*/public class SerializableMethod implements Serializable, MockitoMethod {
/*   0*/  private static final long serialVersionUID = 6005610965006048445L;
/*   0*/  
/*   0*/  private final Class<?> declaringClass;
/*   0*/  
/*   0*/  private final String methodName;
/*   0*/  
/*   0*/  private final Class<?>[] parameterTypes;
/*   0*/  
/*   0*/  private final Class<?> returnType;
/*   0*/  
/*   0*/  private final Class<?>[] exceptionTypes;
/*   0*/  
/*   0*/  private final boolean isVarArgs;
/*   0*/  
/*   0*/  private final boolean isAbstract;
/*   0*/  
/*   0*/  public SerializableMethod(Method method) {
/*  27*/    this.declaringClass = method.getDeclaringClass();
/*  28*/    this.methodName = method.getName();
/*  29*/    this.parameterTypes = method.getParameterTypes();
/*  30*/    this.returnType = method.getReturnType();
/*  31*/    this.exceptionTypes = method.getExceptionTypes();
/*  32*/    this.isVarArgs = method.isVarArgs();
/*  33*/    this.isAbstract = ((method.getModifiers() & 0x400) != 0);
/*   0*/  }
/*   0*/  
/*   0*/  public String getName() {
/*  37*/    return this.methodName;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?> getReturnType() {
/*  41*/    return this.returnType;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?>[] getParameterTypes() {
/*  45*/    return this.parameterTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public Class<?>[] getExceptionTypes() {
/*  49*/    return this.exceptionTypes;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVarArgs() {
/*  53*/    return this.isVarArgs;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAbstract() {
/*  57*/    return this.isAbstract;
/*   0*/  }
/*   0*/  
/*   0*/  public Method getJavaMethod() {
/*   0*/    try {
/*  62*/      return this.declaringClass.getDeclaredMethod(this.methodName, this.parameterTypes);
/*  63*/    } catch (RuntimeException e) {
/*  64*/      String message = String.format("The method %1$s.%2$s is probably private or protected and cannot be mocked.\nPlease report this as a defect with an example of how to reproduce it.", new Object[] { this.declaringClass, this.methodName });
/*  67*/      throw new MockitoException(message, e);
/*  68*/    } catch (NoSuchMethodException e) {
/*  69*/      String message = String.format("The method %1$s.%2$s does not exists and you should not get to this point.\nPlease report this as a defect with an example of how to reproduce it.", new Object[] { this.declaringClass, this.methodName });
/*  72*/      throw new MockitoException(message, e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*  78*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*  83*/    if (this == obj)
/*  84*/      return true; 
/*  85*/    if (obj == null)
/*  86*/      return false; 
/*  87*/    if (getClass() != obj.getClass())
/*  88*/      return false; 
/*  89*/    SerializableMethod other = (SerializableMethod)obj;
/*  90*/    if (this.declaringClass == null) {
/*  91*/      if (other.declaringClass != null)
/*  92*/        return false; 
/*  93*/    } else if (!this.declaringClass.equals(other.declaringClass)) {
/*  94*/      return false;
/*   0*/    } 
/*  95*/    if (this.methodName == null) {
/*  96*/      if (other.methodName != null)
/*  97*/        return false; 
/*  98*/    } else if (!this.methodName.equals(other.methodName)) {
/*  99*/      return false;
/*   0*/    } 
/* 100*/    if (!Arrays.equals((Object[])this.parameterTypes, (Object[])other.parameterTypes))
/* 101*/      return false; 
/* 102*/    if (this.returnType == null) {
/* 103*/      if (other.returnType != null)
/* 104*/        return false; 
/* 105*/    } else if (!this.returnType.equals(other.returnType)) {
/* 106*/      return false;
/*   0*/    } 
/* 107*/    return true;
/*   0*/  }
/*   0*/}
