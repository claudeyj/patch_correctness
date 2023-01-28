/*   0*/package org.mockito.internal.creation.cglib;
/*   0*/
/*   0*/import java.lang.reflect.Field;
/*   0*/import org.mockito.cglib.proxy.MethodProxy;
/*   0*/
/*   0*/class CGLIBHacker {
/*   0*/  public void setMockitoNamingPolicy(MethodProxy methodProxy) {
/*   0*/    try {
/*  16*/      Field createInfoField = reflectOnCreateInfo(methodProxy);
/*  17*/      createInfoField.setAccessible(true);
/*  18*/      Object createInfo = createInfoField.get(methodProxy);
/*  19*/      Field namingPolicyField = createInfo.getClass().getDeclaredField("namingPolicy");
/*  20*/      namingPolicyField.setAccessible(true);
/*  21*/      if (namingPolicyField.get(createInfo) == null)
/*  22*/        namingPolicyField.set(createInfo, MockitoNamingPolicy.INSTANCE); 
/*  24*/    } catch (Exception e) {
/*  25*/      throw new RuntimeException("Unable to set MockitoNamingPolicy on cglib generator which creates FastClasses", e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private Field reflectOnCreateInfo(MethodProxy methodProxy) throws SecurityException, NoSuchFieldException {
/*  33*/    Class<?> cglibMethodProxyClass = methodProxy.getClass();
/*  37*/    while (cglibMethodProxyClass != MethodProxy.class)
/*  38*/      cglibMethodProxyClass = methodProxy.getClass().getSuperclass(); 
/*  40*/    return cglibMethodProxyClass.getDeclaredField("createInfo");
/*   0*/  }
/*   0*/}
