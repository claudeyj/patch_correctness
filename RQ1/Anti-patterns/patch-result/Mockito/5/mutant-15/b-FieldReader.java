/*   0*/package org.mockito.internal.util.reflection;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/
/*   0*/public class FieldReader {
/*   0*/  final Object target;
/*   0*/  
/*   0*/  final Field field;
/*   0*/  
/*  15*/  final AccessibilityChanger changer = new AccessibilityChanger();
/*   0*/  
/*   0*/  public FieldReader(Object target, Field field) {
/*  18*/    this.target = target;
/*  19*/    this.field = field;
/*  20*/    this.changer.enableAccess(field);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNull() {
/*  24*/    return (read() == null);
/*   0*/  }
/*   0*/  
/*   0*/  public Object read() {
/*   0*/    try {
/*  29*/      return this.field.get(this.target);
/*  30*/    } catch (Throwable e) {
/*  31*/      throw new MockitoException("Cannot read state from field: " + this.field + ", on instance: " + this.target);
/*   0*/    } 
/*   0*/  }
/*   0*/}
