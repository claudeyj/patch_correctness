/*   0*/package org.mockito.internal.verification.argumentmatching;
/*   0*/
/*   0*/import java.util.LinkedList;
/*   0*/import java.util.List;
/*   0*/import org.hamcrest.Matcher;
/*   0*/import org.hamcrest.SelfDescribing;
/*   0*/import org.hamcrest.StringDescription;
/*   0*/import org.mockito.internal.matchers.ContainsExtraTypeInformation;
/*   0*/
/*   0*/public class ArgumentMatchingTool {
/*   0*/  public Integer[] getSuspiciouslyNotMatchingArgsIndexes(List<Matcher> matchers, Object[] arguments) {
/*  21*/    if (matchers.size() != arguments.length)
/*  22*/      return new Integer[0]; 
/*  25*/    List<Integer> suspicious = new LinkedList<Integer>();
/*  26*/    int i = 0;
/*  27*/    for (Matcher m : matchers) {
/*  28*/      if (m instanceof ContainsExtraTypeInformation && !safelyMatches(m, arguments[i]) && toStringEquals(m, arguments[i]) && !((ContainsExtraTypeInformation)m).typeMatches(arguments[i]))
/*  32*/        suspicious.add(i); 
/*  34*/      i++;
/*   0*/    } 
/*  36*/    return suspicious.<Integer>toArray(new Integer[0]);
/*   0*/  }
/*   0*/  
/*   0*/  private boolean safelyMatches(Matcher m, Object arg) {
/*   0*/    try {
/*  41*/      return m.matches(arg);
/*  42*/    } catch (Throwable t) {
/*  43*/      return false;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private boolean toStringEquals(Matcher m, Object arg) {
/*  48*/    return StringDescription.toString((SelfDescribing)m).equals(arg.toString());
/*   0*/  }
/*   0*/}
