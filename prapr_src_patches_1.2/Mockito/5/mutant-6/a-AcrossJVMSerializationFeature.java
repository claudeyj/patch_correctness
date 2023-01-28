/*   0*/package org.mockito.internal.creation.cglib;
/*   0*/
/*   0*/import java.io.ByteArrayInputStream;
/*   0*/import java.io.ByteArrayOutputStream;
/*   0*/import java.io.IOException;
/*   0*/import java.io.InputStream;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.ObjectStreamClass;
/*   0*/import java.io.ObjectStreamException;
/*   0*/import java.io.Serializable;
/*   0*/import java.lang.reflect.Field;
/*   0*/import java.lang.reflect.Method;
/*   0*/import java.util.Set;
/*   0*/import java.util.concurrent.locks.Lock;
/*   0*/import java.util.concurrent.locks.ReentrantLock;
/*   0*/import org.mockito.Incubating;
/*   0*/import org.mockito.exceptions.base.MockitoSerializationIssue;
/*   0*/import org.mockito.internal.creation.instance.InstantiatorProvider;
/*   0*/import org.mockito.internal.creation.settings.CreationSettings;
/*   0*/import org.mockito.internal.util.MockUtil;
/*   0*/import org.mockito.internal.util.StringJoiner;
/*   0*/import org.mockito.internal.util.reflection.FieldSetter;
/*   0*/import org.mockito.mock.MockCreationSettings;
/*   0*/import org.mockito.mock.MockName;
/*   0*/import org.mockito.mock.SerializableMode;
/*   0*/
/*   0*/@Incubating
/*   0*/class AcrossJVMSerializationFeature implements Serializable {
/*   0*/  private static final long serialVersionUID = 7411152578314420778L;
/*   0*/  
/*   0*/  private static final String MOCKITO_PROXY_MARKER = "MockitoProxyMarker";
/*   0*/  
/*   0*/  private boolean instanceLocalCurrentlySerializingFlag = false;
/*   0*/  
/*  61*/  private final Lock mutex = new ReentrantLock();
/*   0*/  
/*   0*/  public boolean isWriteReplace(Method method) {
/*  64*/    return (method.getReturnType() == Object.class && (method.getParameterTypes()).length == 0 && method.getName().equals("writeReplace"));
/*   0*/  }
/*   0*/  
/*   0*/  public Object writeReplace(Object mockitoMock) throws ObjectStreamException {
/*   0*/    try {
/* 115*/      this.mutex.lock();
/* 118*/      if (mockIsCurrentlyBeingReplaced())
/* 119*/        return mockitoMock; 
/* 121*/      mockReplacementStarted();
/* 123*/      return new AcrossJVMMockSerializationProxy(mockitoMock);
/* 124*/    } catch (IOException ioe) {
/* 125*/      MockUtil mockUtil = new MockUtil();
/* 126*/      MockName mockName = mockUtil.getMockName(mockitoMock);
/* 127*/      String mockedType = mockUtil.getMockSettings(mockitoMock).getTypeToMock().getCanonicalName();
/* 128*/      throw new MockitoSerializationIssue(StringJoiner.join(new Object[] { "The mock '" + mockName + "' of type '" + mockedType + "'", "The Java Standard Serialization reported an '" + ioe.getClass().getSimpleName() + "' saying :", "  " + ioe.getMessage() }), ioe);
/*   0*/    } finally {
/* 135*/      mockReplacementCompleted();
/* 136*/      this.mutex.unlock();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void mockReplacementCompleted() {
/* 142*/    this.instanceLocalCurrentlySerializingFlag = false;
/*   0*/  }
/*   0*/  
/*   0*/  private void mockReplacementStarted() {
/* 147*/    this.instanceLocalCurrentlySerializingFlag = true;
/*   0*/  }
/*   0*/  
/*   0*/  private boolean mockIsCurrentlyBeingReplaced() {
/* 152*/    return this.instanceLocalCurrentlySerializingFlag;
/*   0*/  }
/*   0*/  
/*   0*/  public <T> void enableSerializationAcrossJVM(MockCreationSettings<T> settings) {
/* 166*/    if (settings.getSerializableMode() == SerializableMode.ACROSS_CLASSLOADERS)
/* 169*/      settings.getExtraInterfaces().add(AcrossJVMMockitoMockSerializable.class); 
/*   0*/  }
/*   0*/  
/*   0*/  public static class AcrossJVMMockSerializationProxy implements Serializable {
/*   0*/    private static final long serialVersionUID = -7600267929109286514L;
/*   0*/    
/*   0*/    private final byte[] serializedMock;
/*   0*/    
/*   0*/    private final Class typeToMock;
/*   0*/    
/*   0*/    private final Set<Class> extraInterfaces;
/*   0*/    
/*   0*/    public AcrossJVMMockSerializationProxy(Object mockitoMock) throws IOException {
/* 204*/      ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 205*/      ObjectOutputStream objectOutputStream = new AcrossJVMSerializationFeature.MockitoMockObjectOutputStream(out);
/* 207*/      objectOutputStream.writeObject(mockitoMock);
/* 209*/      objectOutputStream.close();
/* 210*/      out.close();
/* 212*/      MockCreationSettings mockSettings = new MockUtil().getMockSettings(mockitoMock);
/* 213*/      this.serializedMock = out.toByteArray();
/* 214*/      this.typeToMock = mockSettings.getTypeToMock();
/* 215*/      this.extraInterfaces = mockSettings.getExtraInterfaces();
/*   0*/    }
/*   0*/    
/*   0*/    private Object readResolve() throws ObjectStreamException {
/*   0*/      try {
/* 228*/        ByteArrayInputStream bis = new ByteArrayInputStream(this.serializedMock);
/* 229*/        ObjectInputStream objectInputStream = new AcrossJVMSerializationFeature.MockitoMockObjectInputStream(bis, this.typeToMock, this.extraInterfaces);
/* 231*/        Object deserializedMock = objectInputStream.readObject();
/* 233*/        bis.close();
/* 234*/        objectInputStream.close();
/* 236*/        return deserializedMock;
/* 237*/      } catch (IOException ioe) {
/* 238*/        throw new MockitoSerializationIssue(StringJoiner.join(new Object[] { "Mockito mock cannot be deserialized to a mock of '" + this.typeToMock.getCanonicalName() + "'. The error was :", "  " + ioe.getMessage(), "If you are unsure what is the reason of this exception, feel free to contact us on the mailing list." }), ioe);
/* 243*/      } catch (ClassNotFoundException cce) {
/* 244*/        throw new MockitoSerializationIssue(StringJoiner.join(new Object[] { "A class couldn't be found while deserializing a Mockito mock, you should check your classpath. The error was :", "  " + cce.getMessage(), "If you are still unsure what is the reason of this exception, feel free to contact us on the mailing list." }), cce);
/*   0*/      } 
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static class MockitoMockObjectInputStream extends ObjectInputStream {
/*   0*/    private final Class typeToMock;
/*   0*/    
/*   0*/    private final Set<Class> extraInterfaces;
/*   0*/    
/*   0*/    public MockitoMockObjectInputStream(InputStream in, Class typeToMock, Set<Class<?>> extraInterfaces) throws IOException {
/* 279*/      super(in);
/* 280*/      this.typeToMock = typeToMock;
/* 281*/      this.extraInterfaces = extraInterfaces;
/* 282*/      enableResolveObject(true);
/*   0*/    }
/*   0*/    
/*   0*/    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
/* 299*/      if (notMarkedAsAMockitoMock(readObject()))
/* 300*/        return super.resolveClass(desc); 
/* 308*/      ClassImposterizer imposterizer = new ClassImposterizer(new InstantiatorProvider().getInstantiator(new CreationSettings()));
/* 309*/      imposterizer.setConstructorsAccessible(this.typeToMock, true);
/* 310*/      Class<?> proxyClass = imposterizer.createProxyClass(this.typeToMock, this.extraInterfaces.<Class<?>>toArray(new Class<?>[this.extraInterfaces.size()]));
/* 315*/      hackClassNameToMatchNewlyCreatedClass(desc, proxyClass);
/* 317*/      return proxyClass;
/*   0*/    }
/*   0*/    
/*   0*/    private void hackClassNameToMatchNewlyCreatedClass(ObjectStreamClass descInstance, Class<?> proxyClass) throws ObjectStreamException {
/*   0*/      try {
/* 341*/        Field classNameField = descInstance.getClass().getDeclaredField("name");
/* 342*/        new FieldSetter(descInstance, classNameField).set(proxyClass.getCanonicalName());
/* 343*/      } catch (NoSuchFieldException nsfe) {
/* 345*/        throw new MockitoSerializationIssue(StringJoiner.join(new Object[] { "Wow, the class 'ObjectStreamClass' in the JDK don't have the field 'name',", "this is definitely a bug in our code as it means the JDK team changed a few internal things.", "", "Please report an issue with the JDK used, a code sample and a link to download the JDK would be welcome." }), nsfe);
/*   0*/      } 
/*   0*/    }
/*   0*/    
/*   0*/    private boolean notMarkedAsAMockitoMock(Object marker) throws IOException, ClassNotFoundException {
/* 363*/      return !"MockitoProxyMarker".equals(marker);
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  private static class MockitoMockObjectOutputStream extends ObjectOutputStream {
/*   0*/    private static final String NOTHING = "";
/*   0*/    
/*   0*/    public MockitoMockObjectOutputStream(ByteArrayOutputStream out) throws IOException {
/* 384*/      super(out);
/*   0*/    }
/*   0*/    
/*   0*/    protected void annotateClass(Class<?> cl) throws IOException {
/* 395*/      writeObject(mockitoProxyClassMarker(cl));
/*   0*/    }
/*   0*/    
/*   0*/    private String mockitoProxyClassMarker(Class<?> cl) {
/* 406*/      if (AcrossJVMSerializationFeature.AcrossJVMMockitoMockSerializable.class.isAssignableFrom(cl))
/* 407*/        return "MockitoProxyMarker"; 
/* 409*/      return "";
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public static interface AcrossJVMMockitoMockSerializable {
/*   0*/    Object writeReplace() throws ObjectStreamException;
/*   0*/  }
/*   0*/}
