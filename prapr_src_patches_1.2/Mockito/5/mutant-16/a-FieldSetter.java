/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/
/*   0*/public class FieldSetter {
/*   0*/  private final Object target;
/*   0*/  
/*   0*/  private final Field field;
/*   0*/  
/*   0*/  public FieldSetter(Object target, Field field) {
/*  15*/    this.target = target;
/*  16*/    this.field = field;
/*   0*/  }
/*   0*/  
/*   0*/  public void set(Object value) {
/*  20*/    AccessibilityChanger changer = new AccessibilityChanger();
/*  21*/    changer.enableAccess(this.field);
/*   0*/    try {
/*  23*/      this.field.set(this.target, value);
/*  24*/    } catch (IllegalAccessException e) {
/*  25*/      throw new RuntimeException("Access not authorized on field '" + this.field + "' of object '" + this.target + "' with value: '" + value + "'", e);
/*  26*/    } catch (IllegalArgumentException e) {
/*  27*/      throw new RuntimeException("Wrong argument on field '" + this.field + "' of object '" + this.target + "' with value: '" + value + "', \n" + "reason : " + e.getMessage(), e);
/*   0*/    } 
/*  30*/    changer.safelyDisableAccess(this.field);
/*   0*/  }
/*   0*/}
