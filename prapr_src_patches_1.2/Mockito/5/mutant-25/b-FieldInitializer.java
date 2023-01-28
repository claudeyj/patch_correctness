/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import java.lang.reflect.Modifier;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.Collections;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.List;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.util.MockUtil;
/*   0*/
/*   0*/public class FieldInitializer {
/*   0*/  private final Object fieldOwner;
/*   0*/  
/*   0*/  private final Field field;
/*   0*/  
/*   0*/  private final ConstructorInstantiator instantiator;
/*   0*/  
/*   0*/  public FieldInitializer(Object fieldOwner, Field field) {
/*  46*/    this(fieldOwner, field, new NoArgConstructorInstantiator(fieldOwner, field));
/*   0*/  }
/*   0*/  
/*   0*/  public FieldInitializer(Object fieldOwner, Field field, ConstructorArgumentResolver argResolver) {
/*  61*/    this(fieldOwner, field, new ParameterizedConstructorInstantiator(fieldOwner, field, argResolver));
/*   0*/  }
/*   0*/  
/*   0*/  private FieldInitializer(Object fieldOwner, Field field, ConstructorInstantiator instantiator) {
/*  65*/    if (new FieldReader(fieldOwner, field).isNull()) {
/*  66*/      checkNotLocal(field);
/*  67*/      checkNotInner(field);
/*  68*/      checkNotInterface(field);
/*  69*/      checkNotAbstract(field);
/*   0*/    } 
/*  71*/    this.fieldOwner = fieldOwner;
/*  72*/    this.field = field;
/*  73*/    this.instantiator = instantiator;
/*   0*/  }
/*   0*/  
/*   0*/  public FieldInitializationReport initialize() {
/*  82*/    AccessibilityChanger changer = new AccessibilityChanger();
/*  83*/    changer.enableAccess(this.field);
/*   0*/    try {
/*  86*/      return acquireFieldInstance();
/*  87*/    } catch (IllegalAccessException e) {
/*  88*/      throw new MockitoException("Problems initializing field '" + this.field.getName() + "' of type '" + this.field.getType().getSimpleName() + "'", e);
/*   0*/    } finally {
/*  90*/      changer.safelyDisableAccess(this.field);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNotLocal(Field field) {
/*  95*/    if (field.getType().isLocalClass())
/*  96*/      throw new MockitoException("the type '" + field.getType().getSimpleName() + "' is a local class."); 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNotInner(Field field) {
/* 101*/    if (field.getType().isMemberClass() && !Modifier.isStatic(field.getType().getModifiers()))
/* 102*/      throw new MockitoException("the type '" + field.getType().getSimpleName() + "' is an inner class."); 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNotInterface(Field field) {
/* 107*/    if (field.getType().isInterface())
/* 108*/      throw new MockitoException("the type '" + field.getType().getSimpleName() + "' is an interface."); 
/*   0*/  }
/*   0*/  
/*   0*/  private void checkNotAbstract(Field field) {
/* 113*/    if (Modifier.isAbstract(field.getType().getModifiers()))
/* 114*/      throw new MockitoException("the type '" + field.getType().getSimpleName() + " is an abstract class."); 
/*   0*/  }
/*   0*/  
/*   0*/  private FieldInitializationReport acquireFieldInstance() throws IllegalAccessException {
/* 119*/    Object fieldInstance = this.field.get(this.fieldOwner);
/* 120*/    if (fieldInstance != null)
/* 121*/      return new FieldInitializationReport(fieldInstance, false, false); 
/* 124*/    return this.instantiator.instantiate();
/*   0*/  }
/*   0*/  
/*   0*/  public static interface ConstructorArgumentResolver {
/*   0*/    Object[] resolveTypeInstances(Class<?>... param1VarArgs);
/*   0*/  }
/*   0*/  
/*   0*/  private static interface ConstructorInstantiator {
/*   0*/    FieldInitializationReport instantiate();
/*   0*/  }
/*   0*/  
/*   0*/  static class NoArgConstructorInstantiator implements ConstructorInstantiator {
/*   0*/    private final Object testClass;
/*   0*/    
/*   0*/    private final Field field;
/*   0*/    
/*   0*/    NoArgConstructorInstantiator(Object testClass, Field field) {
/* 170*/      this.testClass = testClass;
/* 171*/      this.field = field;
/*   0*/    }
/*   0*/    
/*   0*/    public FieldInitializationReport instantiate() {
/* 175*/      AccessibilityChanger changer = new AccessibilityChanger();
/* 176*/      Constructor<?> constructor = null;
/*   0*/      try {
/* 178*/        constructor = this.field.getType().getDeclaredConstructor(new Class<?>[0]);
/* 179*/        changer.enableAccess(constructor);
/* 181*/        Object[] noArg = new Object[0];
/* 182*/        Object newFieldInstance = constructor.newInstance(noArg);
/* 183*/        new FieldSetter(this.testClass, this.field).set(newFieldInstance);
/* 185*/        return new FieldInitializationReport(this.field.get(this.testClass), true, false);
/* 186*/      } catch (ReflectiveOperationException e) {
/* 187*/        throw new MockitoException("the type '" + this.field.getType().getSimpleName() + "' has no default constructor", e);
/* 188*/      } catch (InvocationTargetException e) {
/* 189*/        throw new MockitoException("the default constructor of type '" + this.field.getType().getSimpleName() + "' has raised an exception (see the stack trace for cause): " + e.getTargetException().toString(), e);
/* 190*/      } catch (InstantiationException e) {
/* 191*/        throw new MockitoException("InstantiationException (see the stack trace for cause): " + e.toString(), e);
/* 192*/      } catch (IllegalAccessException e) {
/* 193*/        throw new MockitoException("IllegalAccessException (see the stack trace for cause): " + e.toString(), e);
/*   0*/      } finally {
/* 195*/        if (constructor != null)
/* 196*/          changer.safelyDisableAccess(constructor); 
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  static class ParameterizedConstructorInstantiator implements ConstructorInstantiator {
/*   0*/    private final Object testClass;
/*   0*/    
/*   0*/    private final Field field;
/*   0*/    
/*   0*/    private final FieldInitializer.ConstructorArgumentResolver argResolver;
/*   0*/    
/* 216*/    private final MockUtil mockUtil = new MockUtil();
/*   0*/    
/* 217*/    private final Comparator<Constructor<?>> byParameterNumber = new Comparator<Constructor<?>>() {
/*   0*/        public int compare(Constructor<?> constructorA, Constructor<?> constructorB) {
/* 219*/          int argLengths = (constructorB.getParameterTypes()).length - (constructorA.getParameterTypes()).length;
/* 220*/          if (argLengths == 0) {
/* 221*/            int constructorAMockableParamsSize = countMockableParams(constructorA);
/* 222*/            int constructorBMockableParamsSize = countMockableParams(constructorB);
/* 223*/            return constructorBMockableParamsSize - constructorAMockableParamsSize;
/*   0*/          } 
/* 225*/          return argLengths;
/*   0*/        }
/*   0*/        
/*   0*/        private int countMockableParams(Constructor<?> constructor) {
/* 229*/          int constructorMockableParamsSize = 0;
/* 230*/          for (Class<?> aClass : constructor.getParameterTypes()) {
/* 231*/            if (FieldInitializer.ParameterizedConstructorInstantiator.this.mockUtil.isTypeMockable(aClass))
/* 232*/              constructorMockableParamsSize++; 
/*   0*/          } 
/* 235*/          return constructorMockableParamsSize;
/*   0*/        }
/*   0*/      };
/*   0*/    
/*   0*/    ParameterizedConstructorInstantiator(Object testClass, Field field, FieldInitializer.ConstructorArgumentResolver argumentResolver) {
/* 244*/      this.testClass = testClass;
/* 245*/      this.field = field;
/* 246*/      this.argResolver = argumentResolver;
/*   0*/    }
/*   0*/    
/*   0*/    public FieldInitializationReport instantiate() {
/* 250*/      AccessibilityChanger changer = new AccessibilityChanger();
/* 251*/      Constructor<?> constructor = null;
/*   0*/      try {
/* 253*/        constructor = biggestConstructor(this.field.getType());
/* 254*/        changer.enableAccess(constructor);
/* 256*/        Object[] args = this.argResolver.resolveTypeInstances(constructor.getParameterTypes());
/* 257*/        Object newFieldInstance = constructor.newInstance(args);
/* 258*/        new FieldSetter(this.testClass, this.field).set(newFieldInstance);
/* 260*/        return new FieldInitializationReport(this.field.get(this.testClass), false, true);
/* 261*/      } catch (IllegalArgumentException e) {
/* 262*/        throw new MockitoException("internal error : argResolver provided incorrect types for constructor " + constructor + " of type " + this.field.getType().getSimpleName(), e);
/* 263*/      } catch (InvocationTargetException e) {
/* 264*/        throw new MockitoException("the constructor of type '" + this.field.getType().getSimpleName() + "' has raised an exception (see the stack trace for cause): " + e.getTargetException().toString(), e);
/* 265*/      } catch (InstantiationException e) {
/* 266*/        throw new MockitoException("InstantiationException (see the stack trace for cause): " + e.toString(), e);
/* 267*/      } catch (IllegalAccessException e) {
/* 268*/        throw new MockitoException("IllegalAccessException (see the stack trace for cause): " + e.toString(), e);
/*   0*/      } finally {
/* 270*/        if (constructor != null)
/* 271*/          changer.safelyDisableAccess(constructor); 
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private void checkParameterized(Constructor<?> constructor, Field field) {
/* 277*/      if ((constructor.getParameterTypes()).length == 0)
/* 278*/        throw new MockitoException("the field " + field.getName() + " of type " + field.getType() + " has no parameterized constructor"); 
/*   0*/    }
/*   0*/    
/*   0*/    private Constructor<?> biggestConstructor(Class<?> clazz) {
/* 283*/      List<Constructor<?>> constructors = Arrays.asList(clazz.getDeclaredConstructors());
/* 284*/      Collections.sort(constructors, this.byParameterNumber);
/* 286*/      Constructor<?> constructor = constructors.get(0);
/* 287*/      checkParameterized(constructor, this.field);
/* 288*/      return constructor;
/*   0*/    }
/*   0*/  }
/*   0*/}
