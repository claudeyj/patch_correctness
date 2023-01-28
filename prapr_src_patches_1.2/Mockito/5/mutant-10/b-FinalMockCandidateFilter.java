/*   0*/package org.mockito.internal.configuration.injection.filter;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.util.Collection;
/*   0*/import org.mockito.exceptions.Reporter;
/*   0*/import org.mockito.internal.util.reflection.BeanPropertySetter;
/*   0*/import org.mockito.internal.util.reflection.FieldSetter;
/*   0*/
/*   0*/public class FinalMockCandidateFilter implements MockCandidateFilter {
/*   0*/  public OngoingInjecter filterCandidate(Collection<Object> mocks, final Field field, final Object fieldInstance) {
/*  24*/    if (mocks.size() == 1) {
/*  25*/      final Object matchingMock = mocks.iterator().next();
/*  27*/      return new OngoingInjecter() {
/*   0*/          public Object thenInject() {
/*   0*/            try {
/*  30*/              if (!new BeanPropertySetter(fieldInstance, field).set(matchingMock))
/*  31*/                new FieldSetter(fieldInstance, field).set(matchingMock); 
/*  33*/            } catch (Exception e) {
/*  34*/              new Reporter().cannotInjectDependency(field, matchingMock, e);
/*   0*/            } 
/*  36*/            return matchingMock;
/*   0*/          }
/*   0*/        };
/*   0*/    } 
/*  41*/    return new OngoingInjecter() {
/*   0*/        public Object thenInject() {
/*  43*/          return null;
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/}
