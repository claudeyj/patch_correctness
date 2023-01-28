/*   0*/package org.jfree.chart.util;
/*   0*/
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Polygon;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.geom.AffineTransform;
/*   0*/import java.awt.geom.Arc2D;
/*   0*/import java.awt.geom.Ellipse2D;
/*   0*/import java.awt.geom.GeneralPath;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.PathIterator;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.util.Arrays;
/*   0*/
/*   0*/public class ShapeUtilities {
/*   0*/  public static Shape clone(Shape shape) {
/* 103*/    if (shape instanceof Cloneable)
/*   0*/      try {
/* 105*/        return (Shape)ObjectUtilities.clone(shape);
/* 107*/      } catch (CloneNotSupportedException cloneNotSupportedException) {} 
/* 110*/    Shape result = null;
/* 111*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(Shape s1, Shape s2) {
/* 128*/    if (s1 instanceof Line2D && s2 instanceof Line2D)
/* 129*/      return equal((Line2D)s1, (Line2D)s2); 
/* 131*/    if (s1 instanceof Ellipse2D && s2 instanceof Ellipse2D)
/* 132*/      return equal((Ellipse2D)s1, (Ellipse2D)s2); 
/* 134*/    if (s1 instanceof Arc2D && s2 instanceof Arc2D)
/* 135*/      return equal((Arc2D)s1, (Arc2D)s2); 
/* 137*/    if (s1 instanceof Polygon && s2 instanceof Polygon)
/* 138*/      return equal((Polygon)s1, (Polygon)s2); 
/* 140*/    if (s1 instanceof GeneralPath && s2 instanceof GeneralPath)
/* 141*/      return equal((GeneralPath)s1, (GeneralPath)s2); 
/* 145*/    return ObjectUtilities.equal(s1, s2);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(Line2D l1, Line2D l2) {
/* 159*/    if (l1 == null)
/* 160*/      return (l2 == null); 
/* 162*/    if (l2 == null)
/* 163*/      return false; 
/* 165*/    if (!l1.getP1().equals(l2.getP1()))
/* 166*/      return false; 
/* 168*/    if (!l1.getP2().equals(l2.getP2()))
/* 169*/      return false; 
/* 171*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(Ellipse2D e1, Ellipse2D e2) {
/* 184*/    if (e1 == null)
/* 185*/      return (e2 == null); 
/* 187*/    if (e2 == null)
/* 188*/      return false; 
/* 190*/    if (!e1.getFrame().equals(e2.getFrame()))
/* 191*/      return false; 
/* 193*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(Arc2D a1, Arc2D a2) {
/* 206*/    if (a1 == null)
/* 207*/      return (a2 == null); 
/* 209*/    if (a2 == null)
/* 210*/      return false; 
/* 212*/    if (!a1.getFrame().equals(a2.getFrame()))
/* 213*/      return false; 
/* 215*/    if (a1.getAngleStart() != a2.getAngleStart())
/* 216*/      return false; 
/* 218*/    if (a1.getAngleExtent() != a2.getAngleExtent())
/* 219*/      return false; 
/* 221*/    if (a1.getArcType() != a2.getArcType())
/* 222*/      return false; 
/* 224*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(Polygon p1, Polygon p2) {
/* 237*/    if (p1 == null)
/* 238*/      return (p2 == null); 
/* 240*/    if (p2 == null)
/* 241*/      return false; 
/* 243*/    if (p1.npoints != p2.npoints)
/* 244*/      return false; 
/* 246*/    if (!Arrays.equals(p1.xpoints, p2.xpoints))
/* 247*/      return false; 
/* 249*/    if (!Arrays.equals(p1.ypoints, p2.ypoints))
/* 250*/      return false; 
/* 252*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean equal(GeneralPath p1, GeneralPath p2) {
/* 265*/    if (p1 == null)
/* 266*/      return (p2 == null); 
/* 268*/    if (p2 == null)
/* 269*/      return false; 
/* 271*/    if (p1.getWindingRule() != p2.getWindingRule())
/* 272*/      return false; 
/* 274*/    PathIterator iterator1 = p1.getPathIterator(null);
/* 275*/    PathIterator iterator2 = p1.getPathIterator(null);
/* 276*/    double[] d1 = new double[6];
/* 277*/    double[] d2 = new double[6];
/* 278*/    boolean done = (iterator1.isDone() && iterator2.isDone());
/* 279*/    while (!done) {
/* 280*/      if (iterator1.isDone() != iterator2.isDone())
/* 281*/        return false; 
/* 283*/      int seg1 = iterator1.currentSegment(d1);
/* 284*/      int seg2 = iterator2.currentSegment(d2);
/* 285*/      if (seg1 != seg2)
/* 286*/        return false; 
/* 288*/      if (!Arrays.equals(d1, d2))
/* 289*/        return false; 
/* 291*/      iterator1.next();
/* 292*/      iterator2.next();
/* 293*/      done = (iterator1.isDone() && iterator2.isDone());
/*   0*/    } 
/* 295*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createTranslatedShape(Shape shape, double transX, double transY) {
/* 310*/    if (shape == null)
/* 311*/      throw new IllegalArgumentException("Null 'shape' argument."); 
/* 313*/    AffineTransform transform = AffineTransform.getTranslateInstance(transX, transY);
/* 315*/    return transform.createTransformedShape(shape);
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createTranslatedShape(Shape shape, RectangleAnchor anchor, double locationX, double locationY) {
/* 334*/    if (shape == null)
/* 335*/      throw new IllegalArgumentException("Null 'shape' argument."); 
/* 337*/    if (anchor == null)
/* 338*/      throw new IllegalArgumentException("Null 'anchor' argument."); 
/* 340*/    Point2D anchorPoint = RectangleAnchor.coordinates(shape.getBounds2D(), anchor);
/* 342*/    AffineTransform transform = AffineTransform.getTranslateInstance(locationX - anchorPoint.getX(), locationY - anchorPoint.getY());
/* 344*/    return transform.createTransformedShape(shape);
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape rotateShape(Shape base, double angle, float x, float y) {
/* 360*/    if (base == null)
/* 361*/      return null; 
/* 363*/    AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
/* 364*/    Shape result = rotate.createTransformedShape(base);
/* 365*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static void drawRotatedShape(Graphics2D g2, Shape shape, double angle, float x, float y) {
/* 380*/    AffineTransform saved = g2.getTransform();
/* 381*/    AffineTransform rotate = AffineTransform.getRotateInstance(angle, x, y);
/* 382*/    g2.transform(rotate);
/* 383*/    g2.draw(shape);
/* 384*/    g2.setTransform(saved);
/*   0*/  }
/*   0*/  
/* 389*/  private static final float SQRT2 = (float)Math.pow(2.0D, 0.5D);
/*   0*/  
/*   0*/  public static Shape createDiagonalCross(float l, float t) {
/* 400*/    GeneralPath p0 = new GeneralPath();
/* 401*/    p0.moveTo(-l - t, -l + t);
/* 402*/    p0.lineTo(-l + t, -l - t);
/* 403*/    p0.lineTo(0.0F, -t * SQRT2);
/* 404*/    p0.lineTo(l - t, -l - t);
/* 405*/    p0.lineTo(l + t, -l + t);
/* 406*/    p0.lineTo(t * SQRT2, 0.0F);
/* 407*/    p0.lineTo(l + t, l - t);
/* 408*/    p0.lineTo(l - t, l + t);
/* 409*/    p0.lineTo(0.0F, t * SQRT2);
/* 410*/    p0.lineTo(-l + t, l + t);
/* 411*/    p0.lineTo(-l - t, l - t);
/* 412*/    p0.lineTo(-t * SQRT2, 0.0F);
/* 413*/    p0.closePath();
/* 414*/    return p0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createRegularCross(float l, float t) {
/* 426*/    GeneralPath p0 = new GeneralPath();
/* 427*/    p0.moveTo(-l, t);
/* 428*/    p0.lineTo(-t, t);
/* 429*/    p0.lineTo(-t, l);
/* 430*/    p0.lineTo(t, l);
/* 431*/    p0.lineTo(t, t);
/* 432*/    p0.lineTo(l, t);
/* 433*/    p0.lineTo(l, -t);
/* 434*/    p0.lineTo(t, -t);
/* 435*/    p0.lineTo(t, -l);
/* 436*/    p0.lineTo(-t, -l);
/* 437*/    p0.lineTo(-t, -t);
/* 438*/    p0.lineTo(-l, -t);
/* 439*/    p0.closePath();
/* 440*/    return p0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createDiamond(float s) {
/* 451*/    GeneralPath p0 = new GeneralPath();
/* 452*/    p0.moveTo(0.0F, -s);
/* 453*/    p0.lineTo(s, 0.0F);
/* 454*/    p0.lineTo(0.0F, s);
/* 455*/    p0.lineTo(-s, 0.0F);
/* 456*/    p0.closePath();
/* 457*/    return p0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createUpTriangle(float s) {
/* 468*/    GeneralPath p0 = new GeneralPath();
/* 469*/    p0.moveTo(0.0F, -s);
/* 470*/    p0.lineTo(s, s);
/* 471*/    p0.lineTo(-s, s);
/* 472*/    p0.closePath();
/* 473*/    return p0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createDownTriangle(float s) {
/* 484*/    GeneralPath p0 = new GeneralPath();
/* 485*/    p0.moveTo(0.0F, s);
/* 486*/    p0.lineTo(s, -s);
/* 487*/    p0.lineTo(-s, -s);
/* 488*/    p0.closePath();
/* 489*/    return p0;
/*   0*/  }
/*   0*/  
/*   0*/  public static Shape createLineRegion(Line2D line, float width) {
/* 503*/    GeneralPath result = new GeneralPath();
/* 504*/    float x1 = (float)line.getX1();
/* 505*/    float x2 = (float)line.getX2();
/* 506*/    float y1 = (float)line.getY1();
/* 507*/    float y2 = (float)line.getY2();
/* 508*/    if ((x2 - x1) != 0.0D) {
/* 509*/      double theta = Math.atan(((y2 - y1) / (x2 - x1)));
/* 510*/      float dx = (float)Math.sin(theta) * width;
/* 511*/      float dy = (float)Math.cos(theta) * width;
/* 512*/      result.moveTo(x1 - dx, y1 + dy);
/* 513*/      result.lineTo(x1 + dx, y1 - dy);
/* 514*/      result.lineTo(x2 + dx, y2 - dy);
/* 515*/      result.lineTo(x2 - dx, y2 + dy);
/* 516*/      result.closePath();
/*   0*/    } else {
/* 520*/      result.moveTo(x1 - width / 2.0F, y1);
/* 521*/      result.lineTo(x1 + width / 2.0F, y1);
/* 522*/      result.lineTo(x2 + width / 2.0F, y2);
/* 523*/      result.lineTo(x2 - width / 2.0F, y2);
/* 524*/      result.closePath();
/*   0*/    } 
/* 526*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public static Point2D getPointInRectangle(double x, double y, Rectangle2D area) {
/* 545*/    x = Math.max(area.getMinX(), Math.min(x, area.getMaxX()));
/* 546*/    y = Math.max(area.getMinY(), Math.min(y, area.getMaxY()));
/* 547*/    return new Point2D.Double(x, y);
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean contains(Rectangle2D rect1, Rectangle2D rect2) {
/* 562*/    double x0 = rect1.getX();
/* 563*/    double y0 = rect1.getY();
/* 564*/    double x = rect2.getX();
/* 565*/    double y = rect2.getY();
/* 566*/    double w = rect2.getWidth();
/* 567*/    double h = rect2.getHeight();
/* 569*/    return (x >= x0 && y >= y0 && x + w <= x0 + rect1.getWidth() && y + h <= y0 + rect1.getHeight());
/*   0*/  }
/*   0*/  
/*   0*/  public static boolean intersects(Rectangle2D rect1, Rectangle2D rect2) {
/* 587*/    double x0 = rect1.getX();
/* 588*/    double y0 = rect1.getY();
/* 590*/    double x = rect2.getX();
/* 591*/    double width = rect2.getWidth();
/* 592*/    double y = rect2.getY();
/* 593*/    double height = rect2.getHeight();
/* 594*/    return (x + width >= x0 && y + height >= y0 && x <= x0 + rect1.getWidth() && y <= y0 + rect1.getHeight());
/*   0*/  }
/*   0*/}
