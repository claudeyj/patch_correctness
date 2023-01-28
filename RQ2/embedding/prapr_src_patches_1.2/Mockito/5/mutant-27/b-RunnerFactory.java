/*   0*/package org.mockito.internal.runners;
/*   0*/
/*   0*/import java.lang.reflect.InvocationTargetException;
/*   0*/import org.mockito.exceptions.base.MockitoException;
/*   0*/import org.mockito.internal.runners.util.RunnerProvider;
/*   0*/import org.mockito.internal.runners.util.TestMethodsFinder;
/*   0*/
/*   0*/public class RunnerFactory {
/*   0*/  private final RunnerProvider runnerProvider;
/*   0*/  
/*   0*/  RunnerFactory(RunnerProvider runnerProvider) {
/*  18*/    this.runnerProvider = runnerProvider;
/*   0*/  }
/*   0*/  
/*   0*/  public RunnerFactory() {
/*  22*/    this(new RunnerProvider());
/*   0*/  }
/*   0*/  
/*   0*/  public RunnerImpl create(Class<?> klass) throws InvocationTargetException {
/*   0*/    try {
/*  27*/      if (this.runnerProvider.isJUnit45OrHigherAvailable())
/*  28*/        return this.runnerProvider.newInstance("org.mockito.internal.runners.JUnit45AndHigherRunnerImpl", klass); 
/*  30*/      return this.runnerProvider.newInstance("org.mockito.internal.runners.JUnit44RunnerImpl", klass);
/*  32*/    } catch (ReflectiveOperationException e) {
/*  33*/      if (!new TestMethodsFinder().hasTestMethods(klass))
/*  34*/        throw new MockitoException("\n\nNo tests found in " + klass.getSimpleName() + "\n" + "Haven't you forgot @Test annotation?\n", e); 
/*  41*/      throw e;
/*  42*/    } catch (Throwable t) {
/*  43*/      throw new MockitoException("\n\nMockitoRunner can only be used with JUnit 4.4 or higher.\nYou can upgrade your JUnit version or write your own Runner (please consider contributing your runner to the Mockito community).\nBear in mind that you can still enjoy all features of the framework without using runners (they are completely optional).\nIf you get this error despite using JUnit 4.4 or higher then please report this error to the mockito mailing list.\n", t);
/*   0*/    } 
/*   0*/  }
/*   0*/}
