/*   0*/package org.mockito.internal.runners;
/*   0*/
/*   0*/import org.junit.runner.Description;
/*   0*/import org.junit.runner.manipulation.Filter;
/*   0*/import org.junit.runner.manipulation.NoTestsRemainException;
/*   0*/import org.junit.runner.notification.RunNotifier;
/*   0*/import org.junit.runners.BlockJUnit4ClassRunner;
/*   0*/import org.junit.runners.model.FrameworkMethod;
/*   0*/import org.junit.runners.model.InitializationError;
/*   0*/import org.junit.runners.model.Statement;
/*   0*/import org.mockito.MockitoAnnotations;
/*   0*/import org.mockito.internal.runners.util.FrameworkUsageValidator;
/*   0*/
/*   0*/public class JUnit45AndHigherRunnerImpl implements RunnerImpl {
/*   0*/  private BlockJUnit4ClassRunner runner;
/*   0*/  
/*   0*/  public JUnit45AndHigherRunnerImpl(Class<?> klass) throws InitializationError {
/*  23*/    this.runner = new BlockJUnit4ClassRunner(klass) {
/*   0*/        protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
/*  27*/          MockitoAnnotations.initMocks(target);
/*  28*/          return super.withBefores(method, target, statement);
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  public void run(RunNotifier notifier) {
/*  35*/    notifier.addListener(new FrameworkUsageValidator(notifier));
/*  37*/    this.runner.run(notifier);
/*   0*/  }
/*   0*/  
/*   0*/  public Description getDescription() {
/*  41*/    return this.runner.getDescription();
/*   0*/  }
/*   0*/  
/*   0*/  public void filter(Filter filter) throws NoTestsRemainException {
/*  45*/    this.runner.filter(filter);
/*   0*/  }
/*   0*/}
