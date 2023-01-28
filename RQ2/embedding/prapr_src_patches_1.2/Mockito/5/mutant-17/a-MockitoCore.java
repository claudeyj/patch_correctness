/*   0*/package org.mockito.internal;
/*   0*/
/*   0*/import java.util.Arrays;
/*   0*/import java.util.List;
/*   0*/import org.mockito.InOrder;
/*   0*/import org.mockito.MockSettings;
/*   0*/import org.mockito.MockingDetails;
/*   0*/import org.mockito.exceptions.Reporter;
/*   0*/import org.mockito.exceptions.misusing.NotAMockException;
/*   0*/import org.mockito.internal.creation.MockSettingsImpl;
/*   0*/import org.mockito.internal.invocation.finder.VerifiableInvocationsFinder;
/*   0*/import org.mockito.internal.progress.IOngoingStubbing;
/*   0*/import org.mockito.internal.progress.MockingProgress;
/*   0*/import org.mockito.internal.progress.ThreadSafeMockingProgress;
/*   0*/import org.mockito.internal.stubbing.InvocationContainer;
/*   0*/import org.mockito.internal.stubbing.OngoingStubbingImpl;
/*   0*/import org.mockito.internal.stubbing.StubberImpl;
/*   0*/import org.mockito.internal.util.DefaultMockingDetails;
/*   0*/import org.mockito.internal.util.MockUtil;
/*   0*/import org.mockito.internal.verification.MockAwareVerificationMode;
/*   0*/import org.mockito.internal.verification.VerificationDataImpl;
/*   0*/import org.mockito.internal.verification.VerificationModeFactory;
/*   0*/import org.mockito.internal.verification.api.InOrderContext;
/*   0*/import org.mockito.internal.verification.api.VerificationDataInOrder;
/*   0*/import org.mockito.internal.verification.api.VerificationDataInOrderImpl;
/*   0*/import org.mockito.invocation.Invocation;
/*   0*/import org.mockito.mock.MockCreationSettings;
/*   0*/import org.mockito.stubbing.Answer;
/*   0*/import org.mockito.stubbing.DeprecatedOngoingStubbing;
/*   0*/import org.mockito.stubbing.OngoingStubbing;
/*   0*/import org.mockito.stubbing.Stubber;
/*   0*/import org.mockito.stubbing.VoidMethodStubbable;
/*   0*/import org.mockito.verification.VerificationMode;
/*   0*/
/*   0*/public class MockitoCore {
/*  43*/  private final Reporter reporter = new Reporter();
/*   0*/  
/*  44*/  private final MockUtil mockUtil = new MockUtil();
/*   0*/  
/*  45*/  private final MockingProgress mockingProgress = new ThreadSafeMockingProgress();
/*   0*/  
/*   0*/  public boolean isTypeMockable(Class<?> typeToMock) {
/*  48*/    return this.mockUtil.isTypeMockable(typeToMock);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T mock(Class<T> typeToMock, MockSettings settings) {
/*  52*/    if (!MockSettingsImpl.class.isInstance(settings))
/*  53*/      throw new IllegalArgumentException("Unexpected implementation of '" + settings.getClass().getCanonicalName() + "'\n" + "At the moment, you cannot provide your own implementations that class."); 
/*  57*/    MockSettingsImpl<T> impl = MockSettingsImpl.class.cast(settings);
/*  58*/    MockCreationSettings<T> creationSettings = impl.confirm(typeToMock);
/*  59*/    T mock = this.mockUtil.createMock(creationSettings);
/*  60*/    this.mockingProgress.mockingStarted(mock, typeToMock);
/*  61*/    return mock;
/*   0*/  }
/*   0*/  
/*   0*/  public IOngoingStubbing stub() {
/*  65*/    IOngoingStubbing stubbing = this.mockingProgress.pullOngoingStubbing();
/*  66*/    if (stubbing == null) {
/*  67*/      this.mockingProgress.reset();
/*  68*/      this.reporter.missingMethodInvocation();
/*   0*/    } 
/*  70*/    return stubbing;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> DeprecatedOngoingStubbing<T> stub(T methodCall) {
/*  74*/    this.mockingProgress.stubbingStarted();
/*  75*/    return (DeprecatedOngoingStubbing<T>)stub();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> OngoingStubbing<T> when(T methodCall) {
/*  79*/    this.mockingProgress.stubbingStarted();
/*  80*/    return (OngoingStubbing<T>)stub();
/*   0*/  }
/*   0*/  
/*   0*/  public <T> T verify(T mock, VerificationMode mode) {
/*  84*/    if (mock == null) {
/*  85*/      this.reporter.nullPassedToVerify();
/*  86*/    } else if (!this.mockUtil.isMock(mock)) {
/*  87*/      this.reporter.notAMockPassedToVerify(mock.getClass());
/*   0*/    } 
/*  89*/    this.mockingProgress.verificationStarted(new MockAwareVerificationMode(mock, mode));
/*  90*/    return mock;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> void reset(T... mocks) {
/*  94*/    this.mockingProgress.validateState();
/*  95*/    this.mockingProgress.reset();
/*  96*/    this.mockingProgress.resetOngoingStubbing();
/*  98*/    for (T m : mocks)
/*  99*/      this.mockUtil.resetMock(m); 
/*   0*/  }
/*   0*/  
/*   0*/  public void verifyNoMoreInteractions(Object... mocks) {
/* 104*/    assertMocksNotEmpty(mocks);
/* 105*/    this.mockingProgress.validateState();
/* 106*/    for (Object mock : mocks) {
/*   0*/      try {
/* 108*/        if (mock == null)
/* 109*/          this.reporter.nullPassedToVerifyNoMoreInteractions(); 
/* 111*/        InvocationContainer invocations = this.mockUtil.<Object>getMockHandler(mock).getInvocationContainer();
/* 112*/        VerificationDataImpl data = new VerificationDataImpl(invocations, null);
/* 113*/        VerificationModeFactory.noMoreInteractions().verify(data);
/* 114*/      } catch (NotAMockException e) {
/* 115*/        this.reporter.notAMockPassedToVerifyNoMoreInteractions();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void verifyNoMoreInteractionsInOrder(List<Object> mocks, InOrderContext inOrderContext) {
/* 121*/    this.mockingProgress.validateState();
/* 122*/    VerifiableInvocationsFinder finder = new VerifiableInvocationsFinder();
/* 123*/    VerificationDataInOrder data = new VerificationDataInOrderImpl(inOrderContext, finder.find(mocks), null);
/* 124*/    VerificationModeFactory.noMoreInteractions().verifyInOrder(data);
/*   0*/  }
/*   0*/  
/*   0*/  private void assertMocksNotEmpty(Object[] mocks) {
/* 128*/    if (mocks == null || mocks.length == 0)
/* 129*/      this.reporter.mocksHaveToBePassedToVerifyNoMoreInteractions(); 
/*   0*/  }
/*   0*/  
/*   0*/  public InOrder inOrder(Object... mocks) {
/* 134*/    if (mocks == null || mocks.length == 0)
/* 135*/      this.reporter.mocksHaveToBePassedWhenCreatingInOrder(); 
/* 137*/    for (Object mock : mocks) {
/* 138*/      if (mock == null) {
/* 139*/        this.reporter.nullPassedWhenCreatingInOrder();
/* 140*/      } else if (!this.mockUtil.isMock(mock)) {
/* 141*/        this.reporter.notAMockPassedWhenCreatingInOrder();
/*   0*/      } 
/*   0*/    } 
/* 144*/    return new InOrderImpl(Arrays.asList(mocks));
/*   0*/  }
/*   0*/  
/*   0*/  public Stubber doAnswer(Answer answer) {
/* 148*/    this.mockingProgress.stubbingStarted();
/* 149*/    this.mockingProgress.resetOngoingStubbing();
/* 150*/    return new StubberImpl().doAnswer(answer);
/*   0*/  }
/*   0*/  
/*   0*/  public <T> VoidMethodStubbable<T> stubVoid(T mock) {
/* 154*/    InternalMockHandler<T> handler = this.mockUtil.getMockHandler(mock);
/* 155*/    this.mockingProgress.stubbingStarted();
/* 156*/    return handler.voidMethodStubbable(mock);
/*   0*/  }
/*   0*/  
/*   0*/  public void validateMockitoUsage() {
/* 160*/    this.mockingProgress.validateState();
/*   0*/  }
/*   0*/  
/*   0*/  public Invocation getLastInvocation() {
/* 168*/    OngoingStubbingImpl ongoingStubbing = (OngoingStubbingImpl)this.mockingProgress.pullOngoingStubbing();
/* 169*/    List<Invocation> allInvocations = ongoingStubbing.getRegisteredInvocations();
/* 170*/    return allInvocations.get(allInvocations.size() - 1);
/*   0*/  }
/*   0*/  
/*   0*/  public Object[] ignoreStubs(Object... mocks) {
/* 174*/    for (Object m : mocks) {
/* 175*/      InvocationContainer invocationContainer = new MockUtil().<Object>getMockHandler(m).getInvocationContainer();
/* 176*/      List<Invocation> ins = invocationContainer.getInvocations();
/* 177*/      for (Invocation in : ins) {
/* 178*/        if (in.stubInfo() != null)
/* 179*/          in.ignoreForVerification(); 
/*   0*/      } 
/*   0*/    } 
/* 183*/    return mocks;
/*   0*/  }
/*   0*/  
/*   0*/  public MockingDetails mockingDetails(Object toInspect) {
/* 187*/    return new DefaultMockingDetails(toInspect, new MockUtil());
/*   0*/  }
/*   0*/}
