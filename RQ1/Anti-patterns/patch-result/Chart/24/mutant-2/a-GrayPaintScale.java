/*   0*/package org.jfree.chart.renderer;
/*   0*/
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Paint;
/*   0*/import java.io.Serializable;
/*   0*/import org.jfree.chart.util.PublicCloneable;
/*   0*/
/*   0*/public class GrayPaintScale implements PaintScale, PublicCloneable, Serializable {
/*   0*/  private double lowerBound;
/*   0*/  
/*   0*/  private double upperBound;
/*   0*/  
/*   0*/  public GrayPaintScale() {
/*  72*/    this(0.0D, 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public GrayPaintScale(double lowerBound, double upperBound) {
/*  85*/    if (lowerBound >= upperBound)
/*  86*/      throw new IllegalArgumentException("Requires lowerBound < upperBound."); 
/*  89*/    this.lowerBound = lowerBound;
/*  90*/    this.upperBound = upperBound;
/*   0*/  }
/*   0*/  
/*   0*/  public double getLowerBound() {
/* 101*/    return this.lowerBound;
/*   0*/  }
/*   0*/  
/*   0*/  public double getUpperBound() {
/* 112*/    return this.upperBound;
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getPaint(double value) {
/* 124*/    double v = Math.max(value, this.lowerBound);
/* 125*/    v = Math.min(v, this.upperBound);
/* 126*/    int g = (int)((value - this.lowerBound) / (this.upperBound - this.lowerBound) * 255.0D);
/* 128*/    return new Color(g, g, g);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 145*/    if (obj == this)
/* 146*/      return true; 
/* 148*/    if (!(obj instanceof GrayPaintScale))
/* 149*/      return false; 
/* 151*/    GrayPaintScale that = (GrayPaintScale)obj;
/* 152*/    if (this.lowerBound != that.lowerBound)
/* 153*/      return false; 
/* 155*/    if (this.upperBound != that.upperBound)
/* 156*/      return false; 
/* 158*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/* 170*/    return super.clone();
/*   0*/  }
/*   0*/}
