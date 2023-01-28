/*   0*/package org.mockito.internal.runners.util;
/*   0*/
/*   0*/import java.lang.reflect.Constructor;
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import org.mockito.internal.runners.RunnerImpl;
/*   0*/
/*   0*/public class RunnerProvider {
/*   0*/  private static boolean hasJUnit45OrHigher;
/*   0*/  
/*   0*/  static {
/*   0*/    try {
/*  18*/      Class.forName("org.junit.runners.BlockJUnit4ClassRunner");
/*  19*/      hasJUnit45OrHigher = true;
/*  20*/    } catch (Throwable t) {
/*  21*/      hasJUnit45OrHigher = false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isJUnit45OrHigherAvailable() {
/*  26*/    return hasJUnit45OrHigher;
/*   0*/  }
/*   0*/  
/*   0*/  public RunnerImpl newInstance(String runnerClassName, Class<?> constructorParam) throws Exception {
/*   0*/    Constructor<?> constructor;
/*   0*/    try {
/*  32*/      Class<?> runnerClass = Class.forName(runnerClassName);
/*  33*/      constructor = runnerClass.getConstructor(new Class<?>[] { Class.class.getClass() });
/*  34*/    } catch (Exception e) {
/*  35*/      throw new RuntimeException(e);
/*   0*/    } 
/*   0*/    try {
/*  39*/      return (RunnerImpl)constructor.newInstance(new Object[] { constructorParam });
/*  40*/    } catch (InvocationTargetException e) {
/*  41*/      throw e;
/*  42*/    } catch (Throwable e) {
/*  43*/      throw new RuntimeException(e);
/*   0*/    } 
/*   0*/  }
/*   0*/}
