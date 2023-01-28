/*   0*/package org.mockito.internal.verification;
/*   0*/
/*   0*/import org.mockito.exceptions.base.MockitoAssertionError;
/*   0*/import org.mockito.exceptions.verification.junit.ArgumentsAreDifferent;
/*   0*/import org.mockito.internal.util.Timer;
/*   0*/import org.mockito.internal.verification.api.VerificationData;
/*   0*/import org.mockito.verification.VerificationMode;
/*   0*/
/*   0*/public class VerificationOverTimeImpl implements VerificationMode {
/*   0*/  private final long pollingPeriodMillis;
/*   0*/  
/*   0*/  private final long durationMillis;
/*   0*/  
/*   0*/  private final VerificationMode delegate;
/*   0*/  
/*   0*/  private final boolean returnOnSuccess;
/*   0*/  
/*   0*/  private final Timer timer;
/*   0*/  
/*   0*/  public VerificationOverTimeImpl(long pollingPeriodMillis, long durationMillis, VerificationMode delegate, boolean returnOnSuccess) {
/*  37*/    this(pollingPeriodMillis, durationMillis, delegate, returnOnSuccess, new Timer(durationMillis));
/*   0*/  }
/*   0*/  
/*   0*/  public VerificationOverTimeImpl(long pollingPeriodMillis, long durationMillis, VerificationMode delegate, boolean returnOnSuccess, Timer timer) {
/*  53*/    this.pollingPeriodMillis = pollingPeriodMillis;
/*  54*/    this.durationMillis = durationMillis;
/*  55*/    this.delegate = delegate;
/*  56*/    this.returnOnSuccess = returnOnSuccess;
/*  57*/    this.timer = timer;
/*   0*/  }
/*   0*/  
/*   0*/  public void verify(VerificationData data) {
/*  76*/    AssertionError error = null;
/*  78*/    this.timer.start();
/*  79*/    while (this.timer.isCounting()) {
/*   0*/      try {
/*  81*/        this.delegate.verify(data);
/*  83*/        if (this.returnOnSuccess)
/*   0*/          return; 
/*  86*/        error = null;
/*  88*/      } catch (MockitoAssertionError e) {
/*  89*/        error = handleVerifyException(e);
/*  91*/      } catch (ArgumentsAreDifferent e) {
/*  92*/        error = handleVerifyException((AssertionError)e);
/*   0*/      } 
/*   0*/    } 
/*  96*/    if (error != null)
/*  97*/      throw error; 
/*   0*/  }
/*   0*/  
/*   0*/  private AssertionError handleVerifyException(AssertionError e) {
/* 102*/    if (canRecoverFromFailure(this.delegate)) {
/* 103*/      sleep(this.pollingPeriodMillis);
/* 104*/      return e;
/*   0*/    } 
/* 106*/    throw e;
/*   0*/  }
/*   0*/  
/*   0*/  protected boolean canRecoverFromFailure(VerificationMode verificationMode) {
/* 111*/    return (!(verificationMode instanceof AtMost) && !(verificationMode instanceof NoMoreInteractions));
/*   0*/  }
/*   0*/  
/*   0*/  private void sleep(long sleep) {
/*   0*/    try {
/* 116*/      Thread.sleep(sleep);
/* 117*/    } catch (InterruptedException interruptedException) {}
/*   0*/  }
/*   0*/  
/*   0*/  public long getPollingPeriod() {
/* 123*/    return this.pollingPeriodMillis;
/*   0*/  }
/*   0*/  
/*   0*/  public long getDuration() {
/* 127*/    return this.durationMillis;
/*   0*/  }
/*   0*/  
/*   0*/  public VerificationMode getDelegate() {
/* 131*/    return this.delegate;
/*   0*/  }
/*   0*/}
