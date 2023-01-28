/*   0*/package org.jfree.chart.util;
/*   0*/
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.Serializable;
/*   0*/
/*   0*/public class RectangleInsets implements Serializable {
/*   0*/  private static final long serialVersionUID = 1902273207559319996L;
/*   0*/  
/*  42*/  public static final RectangleInsets ZERO_INSETS = new RectangleInsets(UnitType.ABSOLUTE, 0.0D, 0.0D, 0.0D, 0.0D);
/*   0*/  
/*   0*/  private UnitType unitType;
/*   0*/  
/*   0*/  private double top;
/*   0*/  
/*   0*/  private double left;
/*   0*/  
/*   0*/  private double bottom;
/*   0*/  
/*   0*/  private double right;
/*   0*/  
/*   0*/  public RectangleInsets() {
/*  66*/    this(1.0D, 1.0D, 1.0D, 1.0D);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets(double top, double left, double bottom, double right) {
/*  79*/    this(UnitType.ABSOLUTE, top, left, bottom, right);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets(UnitType unitType, double top, double left, double bottom, double right) {
/*  94*/    if (unitType == null)
/*  95*/      throw new IllegalArgumentException("Null 'unitType' argument."); 
/*  97*/    this.unitType = unitType;
/*  98*/    this.top = top;
/*  99*/    this.bottom = bottom;
/* 100*/    this.left = left;
/* 101*/    this.right = right;
/*   0*/  }
/*   0*/  
/*   0*/  public UnitType getUnitType() {
/* 111*/    return this.unitType;
/*   0*/  }
/*   0*/  
/*   0*/  public double getTop() {
/* 120*/    return this.top;
/*   0*/  }
/*   0*/  
/*   0*/  public double getBottom() {
/* 129*/    return this.bottom;
/*   0*/  }
/*   0*/  
/*   0*/  public double getLeft() {
/* 138*/    return this.left;
/*   0*/  }
/*   0*/  
/*   0*/  public double getRight() {
/* 147*/    return this.right;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 158*/    if (obj == this)
/* 159*/      return true; 
/* 161*/    if (!(obj instanceof RectangleInsets))
/* 162*/      return false; 
/* 164*/    RectangleInsets that = (RectangleInsets)obj;
/* 165*/    if (that.unitType != this.unitType)
/* 166*/      return false; 
/* 168*/    if (this.left != that.left)
/* 169*/      return false; 
/* 171*/    if (this.right != that.right)
/* 172*/      return false; 
/* 174*/    if (this.top != that.top)
/* 175*/      return false; 
/* 177*/    if (this.bottom != that.bottom)
/* 178*/      return false; 
/* 180*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/* 191*/    int result = (this.unitType != null) ? this.unitType.hashCode() : 0;
/* 192*/    long temp = (this.top != 0.0D) ? Double.doubleToLongBits(this.top) : 0L;
/* 193*/    result = 29 * result + (int)(temp ^ temp >>> 32L);
/* 194*/    temp = (this.bottom != 0.0D) ? Double.doubleToLongBits(this.bottom) : 0L;
/* 195*/    result = 29 * result + (int)(temp ^ temp >>> 32L);
/* 196*/    temp = (this.left != 0.0D) ? Double.doubleToLongBits(this.left) : 0L;
/* 197*/    result = 29 * result + (int)(temp ^ temp >>> 32L);
/* 198*/    temp = (this.right != 0.0D) ? Double.doubleToLongBits(this.right) : 0L;
/* 199*/    result = 29 * result + (int)(temp ^ temp >>> 32L);
/* 200*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public String toString() {
/* 210*/    return "RectangleInsets[t=" + this.top + ",l=" + this.left + ",b=" + this.bottom + ",r=" + this.right + "]";
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createAdjustedRectangle(Rectangle2D base, LengthAdjustmentType horizontal, LengthAdjustmentType vertical) {
/* 229*/    if (base == null)
/* 230*/      throw new IllegalArgumentException("Null 'base' argument."); 
/* 232*/    double x = base.getX();
/* 233*/    double y = base.getY();
/* 234*/    double w = base.getWidth();
/* 235*/    double h = base.getHeight();
/* 236*/    if (horizontal == LengthAdjustmentType.EXPAND) {
/* 237*/      double leftOutset = calculateLeftOutset(w);
/* 238*/      x -= leftOutset;
/* 239*/      w = w + leftOutset + calculateRightOutset(w);
/* 241*/    } else if (horizontal == LengthAdjustmentType.CONTRACT) {
/* 242*/      double leftMargin = calculateLeftInset(w);
/* 243*/      x += leftMargin;
/* 244*/      w = w - leftMargin - calculateRightInset(w);
/*   0*/    } 
/* 246*/    if (vertical == LengthAdjustmentType.EXPAND) {
/* 247*/      double topMargin = calculateTopOutset(h);
/* 248*/      y -= topMargin;
/* 249*/      h = h + topMargin + calculateBottomOutset(h);
/* 251*/    } else if (vertical == LengthAdjustmentType.CONTRACT) {
/* 252*/      double topMargin = calculateTopInset(h);
/* 253*/      y += topMargin;
/* 254*/      h = h - topMargin - calculateBottomInset(h);
/*   0*/    } 
/* 256*/    return new Rectangle2D.Double(x, y, w, h);
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createInsetRectangle(Rectangle2D base) {
/* 267*/    return createInsetRectangle(base, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createInsetRectangle(Rectangle2D base, boolean horizontal, boolean vertical) {
/* 281*/    if (base == null)
/* 282*/      throw new IllegalArgumentException("Null 'base' argument."); 
/* 284*/    double topMargin = 0.0D;
/* 285*/    double bottomMargin = 0.0D;
/* 286*/    if (vertical) {
/* 287*/      topMargin = calculateTopInset(base.getHeight());
/* 288*/      bottomMargin = calculateBottomInset(base.getHeight());
/*   0*/    } 
/* 290*/    double leftMargin = 0.0D;
/* 291*/    double rightMargin = 0.0D;
/* 292*/    if (horizontal) {
/* 293*/      leftMargin = calculateLeftInset(base.getWidth());
/* 294*/      rightMargin = calculateRightInset(base.getWidth());
/*   0*/    } 
/* 296*/    return new Rectangle2D.Double(base.getX() + leftMargin, base.getY() + topMargin, base.getWidth() - leftMargin - rightMargin, base.getHeight() - topMargin - bottomMargin);
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createOutsetRectangle(Rectangle2D base) {
/* 312*/    return createOutsetRectangle(base, true, true);
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createOutsetRectangle(Rectangle2D base, boolean horizontal, boolean vertical) {
/* 327*/    if (base == null)
/* 328*/      throw new IllegalArgumentException("Null 'base' argument."); 
/* 330*/    double topMargin = 0.0D;
/* 331*/    double bottomMargin = 0.0D;
/* 332*/    if (vertical) {
/* 333*/      topMargin = calculateTopOutset(base.getHeight());
/* 334*/      bottomMargin = calculateBottomOutset(base.getHeight());
/*   0*/    } 
/* 336*/    double leftMargin = 0.0D;
/* 337*/    double rightMargin = 0.0D;
/* 338*/    if (horizontal) {
/* 339*/      leftMargin = calculateLeftOutset(base.getWidth());
/* 340*/      rightMargin = calculateRightOutset(base.getWidth());
/*   0*/    } 
/* 342*/    return new Rectangle2D.Double(base.getX() - leftMargin, base.getY() - topMargin, base.getWidth() + leftMargin + rightMargin, base.getHeight() + topMargin + bottomMargin);
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateTopInset(double height) {
/* 358*/    double result = this.top;
/* 359*/    if (this.unitType == UnitType.RELATIVE)
/* 360*/      result = this.top * height; 
/* 362*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateTopOutset(double height) {
/* 373*/    double result = this.top;
/* 374*/    if (this.unitType == UnitType.RELATIVE)
/* 375*/      result = height / (1.0D - this.top - this.bottom) * this.top; 
/* 377*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateBottomInset(double height) {
/* 388*/    double result = this.bottom;
/* 389*/    if (this.unitType == UnitType.RELATIVE)
/* 390*/      result = this.bottom * height; 
/* 392*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateBottomOutset(double height) {
/* 403*/    double result = this.bottom;
/* 404*/    if (this.unitType == UnitType.RELATIVE)
/* 405*/      result = height / (1.0D - this.top - this.bottom) * this.bottom; 
/* 407*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateLeftInset(double width) {
/* 418*/    double result = this.left;
/* 419*/    if (this.unitType == UnitType.RELATIVE)
/* 420*/      result = this.left * width; 
/* 422*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateLeftOutset(double width) {
/* 433*/    double result = this.left;
/* 434*/    if (this.unitType == UnitType.RELATIVE)
/* 435*/      result = width / (1.0D - this.left - this.right) * this.left; 
/* 437*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateRightInset(double width) {
/* 448*/    double result = this.right;
/* 449*/    if (this.unitType == UnitType.RELATIVE)
/* 450*/      result = this.right * width; 
/* 452*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double calculateRightOutset(double width) {
/* 463*/    double result = this.right;
/* 464*/    if (this.unitType == UnitType.RELATIVE)
/* 465*/      result = width / (1.0D - this.left - this.right) * this.right; 
/* 467*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public double trimWidth(double width) {
/* 478*/    return width - calculateLeftInset(width) - calculateRightInset(width);
/*   0*/  }
/*   0*/  
/*   0*/  public double extendWidth(double width) {
/* 489*/    return width + calculateLeftOutset(width) + calculateRightOutset(width);
/*   0*/  }
/*   0*/  
/*   0*/  public double trimHeight(double height) {
/* 500*/    return height - calculateTopInset(height) - calculateBottomInset(height);
/*   0*/  }
/*   0*/  
/*   0*/  public double extendHeight(double height) {
/* 512*/    return height + calculateTopOutset(height) + calculateBottomOutset(height);
/*   0*/  }
/*   0*/  
/*   0*/  public void trim(Rectangle2D area) {
/* 522*/    double w = area.getWidth();
/* 523*/    double h = area.getHeight();
/* 524*/    double l = calculateLeftInset(w);
/* 525*/    double r = calculateRightInset(w);
/* 526*/    double t = calculateTopInset(h);
/* 527*/    double b = calculateBottomInset(h);
/* 528*/    area.setRect(area.getX() + l, area.getY() + t, w - l - r, h - t - w);
/*   0*/  }
/*   0*/}
