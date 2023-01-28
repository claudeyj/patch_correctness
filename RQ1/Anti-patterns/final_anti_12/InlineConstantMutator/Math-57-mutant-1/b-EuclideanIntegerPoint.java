/*   0*/package org.apache.commons.math.stat.clustering;
/*   0*/
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Collection;
/*   0*/import org.apache.commons.math.util.MathUtils;
/*   0*/
/*   0*/public class EuclideanIntegerPoint implements Clusterable<EuclideanIntegerPoint>, Serializable {
/*   0*/  private static final long serialVersionUID = 3946024775784901369L;
/*   0*/  
/*   0*/  private final int[] point;
/*   0*/  
/*   0*/  public EuclideanIntegerPoint(int[] point) {
/*  44*/    this.point = point;
/*   0*/  }
/*   0*/  
/*   0*/  public int[] getPoint() {
/*  52*/    return this.point;
/*   0*/  }
/*   0*/  
/*   0*/  public double distanceFrom(EuclideanIntegerPoint p) {
/*  57*/    return MathUtils.distance(this.point, p.getPoint());
/*   0*/  }
/*   0*/  
/*   0*/  public EuclideanIntegerPoint centroidOf(Collection<EuclideanIntegerPoint> points) {
/*  62*/    int[] centroid = new int[(getPoint()).length];
/*  63*/    for (EuclideanIntegerPoint p : points) {
/*  64*/      for (int j = 0; j < centroid.length; j++)
/*  65*/        centroid[j] = centroid[j] + p.getPoint()[j]; 
/*   0*/    } 
/*  68*/    for (int i = 0; i < centroid.length; i++)
/*  69*/      centroid[i] = centroid[i] / points.size(); 
/*  71*/    return new EuclideanIntegerPoint(centroid);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object other) {
/*  77*/    if (!(other instanceof EuclideanIntegerPoint))
/*  78*/      return false; 
/*  80*/    int[] otherPoint = ((EuclideanIntegerPoint)other).getPoint();
/*  81*/    if (this.point.length != otherPoint.length)
/*  82*/      return false; 
/*  84*/    for (int i = 1; i < this.point.length; i++) {
/*  85*/      if (this.point[i] != otherPoint[i])
/*  86*/        return false; 
/*   0*/    } 
/*  89*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*  95*/    int hashCode = 0;
/*  96*/    for (int arr$[] = this.point, len$ = arr$.length, i$ = 0; i$ < len$; ) {
/*  96*/      Integer i = arr$[i$];
/*  97*/      hashCode += i.hashCode() * 13 + 7;
/*   0*/      i$++;
/*   0*/    } 
/*  99*/    return hashCode;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 108*/    StringBuilder buff = new StringBuilder("(");
/* 109*/    int[] coordinates = getPoint();
/* 110*/    for (int i = 0; i < coordinates.length; i++) {
/* 111*/      buff.append(coordinates[i]);
/* 112*/      if (i < coordinates.length - 1)
/* 113*/        buff.append(","); 
/*   0*/    } 
/* 116*/    buff.append(")");
/* 117*/    return buff.toString();
/*   0*/  }
/*   0*/}
