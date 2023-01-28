/*   0*/package org.mockito.internal.configuration.injection;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.util.Set;
/*   0*/import org.mockito.Mockito;
/*   0*/import org.mockito.Spy;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.util.MockUtil;
/*   0*/import org.mockito.internal.util.reflection.FieldReader;
/*   0*/import org.mockito.internal.util.reflection.FieldSetter;
/*   0*/
/*   0*/public class SpyOnInjectedFieldsHandler extends MockInjectionStrategy {
/*   0*/  protected boolean processInjection(Field field, Object fieldOwner, Set<Object> mockCandidates) {
/*  32*/    FieldReader fieldReader = new FieldReader(fieldOwner, field);
/*  35*/    if (!fieldReader.isNull() && field.isAnnotationPresent((Class)Spy.class))
/*   0*/      try {
/*  37*/        Object instance = fieldReader.read();
/*  38*/        if (new MockUtil().isMock(instance)) {
/*  41*/          Mockito.reset(new Object[] { instance });
/*   0*/        } else {
/*  43*/          new FieldSetter(fieldOwner, field).set(Mockito.mock(instance.getClass(), Mockito.withSettings().spiedInstance(instance).defaultAnswer(Mockito.CALLS_REAL_METHODS).name(field.getName())));
/*   0*/        } 
/*  50*/      } catch (Throwable e) {
/*  51*/        throw new MockitoException("Problems initiating spied field " + field.getName(), e);
/*   0*/      }  
/*  55*/    return false;
/*   0*/  }
/*   0*/}
