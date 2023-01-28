/*   0*/package org.jfree.chart.plot;
/*   0*/
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Stroke;
/*   0*/import org.jfree.chart.event.MarkerChangeEvent;
/*   0*/
/*   0*/public class ValueMarker extends Marker {
/*   0*/  private double value;
/*   0*/  
/*   0*/  public ValueMarker(double value) {
/*  69*/    this.value = value;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueMarker(double value, Paint paint, Stroke stroke) {
/*  80*/    this(value, paint, stroke, paint, stroke, 1.0F);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueMarker(double value, Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
/*  95*/    super(paint, stroke, paint, stroke, alpha);
/*  96*/    this.value = value;
/*   0*/  }
/*   0*/  
/*   0*/  public double getValue() {
/* 107*/    return this.value;
/*   0*/  }
/*   0*/  
/*   0*/  public void setValue(double value) {
/* 121*/    this.value = value;
/* 122*/    notifyListeners(new MarkerChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 141*/    if (obj == this)
/* 142*/      return true; 
/* 144*/    if (!super.equals(obj))
/* 145*/      return false; 
/* 147*/    if (!(obj instanceof ValueMarker))
/* 148*/      return false; 
/* 150*/    ValueMarker that = (ValueMarker)obj;
/* 151*/    if (this.value != that.value)
/* 152*/      return false; 
/* 154*/    return true;
/*   0*/  }
/*   0*/}
