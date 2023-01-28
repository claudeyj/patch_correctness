/*   0*/package org.jfree.data.statistics;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/
/*   0*/public class MeanAndStandardDeviation implements Serializable {
/*   0*/  private static final long serialVersionUID = 7413468697315721515L;
/*   0*/  
/*   0*/  private Number mean;
/*   0*/  
/*   0*/  private Number standardDeviation;
/*   0*/  
/*   0*/  public MeanAndStandardDeviation(double mean, double standardDeviation) {
/*  74*/    this(new Double(mean), new Double(standardDeviation));
/*   0*/  }
/*   0*/  
/*   0*/  public MeanAndStandardDeviation(Number mean, Number standardDeviation) {
/*  85*/    this.mean = mean;
/*  86*/    this.standardDeviation = standardDeviation;
/*   0*/  }
/*   0*/  
/*   0*/  public Number getMean() {
/*  95*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public Number getStandardDeviation() {
/* 104*/    return this.standardDeviation;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 115*/    if (obj == this)
/* 116*/      return true; 
/* 118*/    if (!(obj instanceof MeanAndStandardDeviation))
/* 119*/      return false; 
/* 121*/    MeanAndStandardDeviation that = (MeanAndStandardDeviation)obj;
/* 122*/    if (!ObjectUtilities.equal(this.mean, that.mean))
/* 123*/      return false; 
/* 125*/    if (!ObjectUtilities.equal(this.standardDeviation, that.standardDeviation))
/* 128*/      return false; 
/* 130*/    return true;
/*   0*/  }
/*   0*/}
