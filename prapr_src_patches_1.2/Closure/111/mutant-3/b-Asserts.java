/*   0*/package com.google.javascript.rhino.testing;
/*   0*/
/*   0*/import com.google.common.collect.Iterables;
/*   0*/import com.google.javascript.rhino.ErrorReporter;
/*   0*/import com.google.javascript.rhino.jstype.JSType;
/*   0*/import com.google.javascript.rhino.jstype.StaticScope;
/*   0*/import java.util.Iterator;
/*   0*/import junit.framework.Assert;
/*   0*/
/*   0*/public class Asserts {
/*   0*/  public static JSType assertResolvesToSame(JSType type) {
/*  59*/    Assert.assertSame(type, assertValidResolve(type));
/*  60*/    return type;
/*   0*/  }
/*   0*/  
/*   0*/  public static JSType assertValidResolve(JSType type) {
/*  65*/    return assertValidResolve(type, MapBasedScope.emptyScope());
/*   0*/  }
/*   0*/  
/*   0*/  public static JSType assertValidResolve(JSType type, StaticScope<JSType> scope) {
/*  71*/    ErrorReporter t = TestErrorReporter.forNoExpectedReports();
/*  72*/    JSType resolvedType = type.resolve(t, scope);
/*  73*/    assertTypeEquals("JSType#resolve should not affect object equality", type, resolvedType);
/*  75*/    return resolvedType;
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeNotEquals(JSType a, JSType b) {
/*  79*/    assertTypeNotEquals("", a, b);
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeNotEquals(String message, JSType a, JSType b) {
/*  83*/    Assert.assertFalse(message + (message.isEmpty() ? "" : "\n") + "Type: " + b + "\n", a.isEquivalentTo(b));
/*  88*/    Assert.assertFalse(message + " Equals is not symmetric.\n" + "Type: " + b + "\n", b.isEquivalentTo(a));
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeEquals(JSType a, JSType b) {
/*  96*/    "";
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertTypeEquals(String message, JSType a, JSType b) {
/* 100*/    Assert.assertTrue("Both types must be null, or both must be non-null " + a + "," + b, (((a == null) ? true : false) == ((b == null) ? true : false)));
/* 103*/    if (a == null)
/*   0*/      return; 
/* 106*/    Assert.assertTrue(message + (message.isEmpty() ? "" : "\n") + "Expected: " + a + "\n" + "Actual  : " + b, a.isEquivalentTo(b));
/* 112*/    Assert.assertTrue(message + " Equals is not symmetric.\n" + "Expected: " + b + "\n" + "Actual  : " + a, b.isEquivalentTo(a));
/*   0*/  }
/*   0*/  
/*   0*/  public static <T extends JSType, S extends JSType> void assertTypeCollectionEquals(Iterable<T> a, Iterable<S> b) {
/* 122*/    Assert.assertEquals(Iterables.size(a), Iterables.size(b));
/* 123*/    Iterator<T> aIterator = a.iterator();
/* 124*/    Iterator<S> bIterator = b.iterator();
/* 125*/    while (aIterator.hasNext())
/* 126*/      assertTypeEquals((JSType)aIterator.next(), (JSType)bIterator.next()); 
/*   0*/  }
/*   0*/  
/*   0*/  public static void assertEquivalenceOperations(JSType a, JSType b) {
/* 135*/    Assert.assertTrue(a.isEquivalentTo(b));
/* 136*/    Assert.assertTrue(a.isEquivalentTo(a));
/* 137*/    Assert.assertTrue(b.isEquivalentTo(b));
/* 138*/    Assert.assertTrue(b.isEquivalentTo(a));
/* 140*/    Assert.assertTrue(a.isSubtype(b));
/* 141*/    Assert.assertTrue(a.isSubtype(a));
/* 142*/    Assert.assertTrue(b.isSubtype(b));
/* 143*/    Assert.assertTrue(b.isSubtype(a));
/* 145*/    assertTypeEquals(a, a.getGreatestSubtype(b));
/* 146*/    assertTypeEquals(a, a.getGreatestSubtype(a));
/* 147*/    assertTypeEquals(a, b.getGreatestSubtype(b));
/* 148*/    assertTypeEquals(a, b.getGreatestSubtype(a));
/* 150*/    assertTypeEquals(a, a.getLeastSupertype(b));
/* 151*/    assertTypeEquals(a, a.getLeastSupertype(a));
/* 152*/    assertTypeEquals(a, b.getLeastSupertype(b));
/* 153*/    assertTypeEquals(a, b.getLeastSupertype(a));
/* 155*/    Assert.assertTrue(a.canCastTo(b));
/* 156*/    Assert.assertTrue(a.canCastTo(a));
/* 157*/    Assert.assertTrue(b.canCastTo(b));
/* 158*/    Assert.assertTrue(b.canCastTo(a));
/*   0*/  }
/*   0*/}
