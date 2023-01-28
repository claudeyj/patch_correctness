/*   0*/package org.mockito.internal.matchers;
/*   0*/
/*   0*/import org.hamcrest.Description;
/*   0*/import org.hamcrest.SelfDescribing;
/*   0*/import org.mockito.ArgumentMatcher;
/*   0*/
/*   0*/public class Equals extends ArgumentMatcher<Object> implements ContainsExtraTypeInformation {
/*   0*/  private final Object wanted;
/*   0*/  
/*   0*/  public Equals(Object wanted) {
/*  16*/    this.wanted = wanted;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matches(Object actual) {
/*  20*/    this;
/*  20*/    if (actual == null)
/*  21*/      return (actual == null); 
/*  23*/    return this.wanted.equals(actual);
/*   0*/  }
/*   0*/  
/*   0*/  public void describeTo(Description description) {
/*  27*/    description.appendText(describe(this.wanted));
/*   0*/  }
/*   0*/  
/*   0*/  public String describe(Object object) {
/*  31*/    String text = quoting();
/*  32*/    if (object == null) {
/*  33*/      text = text + "null";
/*   0*/    } else {
/*  35*/      text = text + object.toString();
/*   0*/    } 
/*  37*/    text = text + quoting();
/*  38*/    return text;
/*   0*/  }
/*   0*/  
/*   0*/  private String quoting() {
/*  42*/    if (this.wanted instanceof String)
/*  43*/      return "\""; 
/*  44*/    if (this.wanted instanceof Character)
/*  45*/      return "'"; 
/*  47*/    return "";
/*   0*/  }
/*   0*/  
/*   0*/  protected final Object getWanted() {
/*  52*/    return this.wanted;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object o) {
/*  57*/    if (o == null || !getClass().equals(o.getClass()))
/*  58*/      return false; 
/*  60*/    Equals other = (Equals)o;
/*  61*/    return ((this.wanted == null && other.wanted == null) || (this.wanted != null && this.wanted.equals(other.wanted)));
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*  66*/    throw new UnsupportedOperationException("hashCode() is not supported");
/*   0*/  }
/*   0*/  
/*   0*/  public SelfDescribing withExtraTypeInfo() {
/*  70*/    return new SelfDescribing() {
/*   0*/        public void describeTo(Description description) {
/*  72*/          description.appendText(Equals.this.describe("(" + Equals.this.wanted.getClass().getSimpleName() + ") " + Equals.this.wanted));
/*   0*/        }
/*   0*/      };
/*   0*/  }
/*   0*/  
/*   0*/  public boolean typeMatches(Object object) {
/*  77*/    return (this.wanted != null && object != null && object.getClass() == this.wanted.getClass());
/*   0*/  }
/*   0*/}
