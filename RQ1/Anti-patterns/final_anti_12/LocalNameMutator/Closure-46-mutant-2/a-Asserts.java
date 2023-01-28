/*   0*/package com.google.javascript.rhino.testing;
/*   0*/
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.StaticScope;
/*   0*/import junit.framework.Assert;
/*   0*/
/*   0*/public class Asserts {
/*   0*/  public static JSType assertResolvesToSame(JSType type) {
/*  56*/    Assert.assertSame(type, assertValidResolve(type));
/*  57*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  public static JSType assertValidResolve(JSType type) {
/*  62*/    return assertValidResolve(type, MapBasedScope.emptyScope());
/*   0*/  }
/*   0*/  
/*   0*/  public static JSType assertValidResolve(JSType type, StaticScope<JSType> scope) {
/*  68*/    ErrorReporter t = TestErrorReporter.forNoExpectedReports();
/*  69*/    JSType resolvedType = type.resolve(t, scope);
/*  70*/    assertTypeEquals("JSType#resolve should not affect object equality", type, resolvedType);
/*  72*/    Assert.assertEquals("JSType#resolve should not affect hash codes", type.hashCode(), resolvedType.hashCode());
/*  74*/    return resolvedType;
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeNotEquals(JSType a, JSType b) {
/*  78*/    assertTypeNotEquals("", a, b);
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeNotEquals(String message, JSType a, JSType b) {
/*  82*/    Assert.assertFalse(message + (message.isEmpty() ? "" : "\n") + "Type: " + b + "\n", a.isEquivalentTo(b));
/*  87*/    Assert.assertFalse(message + " Equals is not symmetric.\n" + "Type: " + b + "\n", b.isEquivalentTo(a));
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeEquals(JSType a, JSType b) {
/*  95*/    assertTypeEquals("", a, b);
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeEquals(String message, JSType a, JSType b) {
/*  99*/    Assert.assertTrue(message + (message.isEmpty() ? "" : "\n") + "Expected: " + a + "\n" + "Actual  : " + b, a.isEquivalentTo(b));
/* 105*/    Assert.assertTrue(message + " Equals is not symmetric.\n" + "Expected: " + b + "\n" + "Actual  : " + a, b.isEquivalentTo(a));
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertEquivalenceOperations(JSType a, JSType b) {
/* 118*/    Assert.assertTrue(a.isEquivalentTo(b));
/* 119*/    Assert.assertTrue(a.isEquivalentTo(a));
/* 120*/    Assert.assertTrue(b.isEquivalentTo(b));
/* 121*/    Assert.assertTrue(b.isEquivalentTo(a));
/* 123*/    Assert.assertTrue(a.isSubtype(b));
/* 124*/    Assert.assertTrue(a.isSubtype(a));
/* 125*/    Assert.assertTrue(b.isSubtype(b));
/* 126*/    Assert.assertTrue(b.isSubtype(a));
/* 128*/    assertTypeEquals(a, a.getGreatestSubtype(b));
/* 129*/    assertTypeEquals(a, a.getGreatestSubtype(a));
/* 130*/    assertTypeEquals(a, b.getGreatestSubtype(b));
/* 131*/    assertTypeEquals(a, b.getGreatestSubtype(a));
/* 133*/    assertTypeEquals(a, a.getLeastSupertype(b));
/* 134*/    assertTypeEquals(a, a.getLeastSupertype(a));
/* 135*/    assertTypeEquals(a, b.getLeastSupertype(b));
/* 136*/    assertTypeEquals(a, b.getLeastSupertype(a));
/*   0*/  }
/*   0*/}
