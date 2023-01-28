/*   0*/package org.mockito.internal.matchers;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.hamcrest.Description;
/*   0*/import org.mockito.ArgumentMatcher;
/*   0*/
/*   0*/public class Same extends ArgumentMatcher<Object> implements Serializable {
/*   0*/  private static final long serialVersionUID = -1226959355938572597L;
/*   0*/  
/*   0*/  private final Object wanted;
/*   0*/  
/*   0*/  public Same(Object wanted) {
/*  19*/    this.wanted = wanted;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean matches(Object actual) {
/*  23*/    return (this.wanted == actual);
/*   0*/  }
/*   0*/  
/*   0*/  public void describeTo(Description description) {
/*  27*/    description.appendText("same(");
/*  28*/    appendQuoting(description);
/*  29*/    if (this.wanted == null)
/*   0*/      return; 
/*  29*/    description.appendText(this.wanted.toString());
/*  30*/    appendQuoting(description);
/*  31*/    description.appendText(")");
/*   0*/  }
/*   0*/  
/*   0*/  private void appendQuoting(Description description) {
/*  35*/    if (this.wanted instanceof String) {
/*  36*/      description.appendText("\"");
/*  37*/    } else if (this.wanted instanceof Character) {
/*  38*/      description.appendText("'");
/*   0*/    } 
/*   0*/  }
/*   0*/}
