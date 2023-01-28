/*   0*/package org.jfree.chart.axis;
/*   0*/
/*   0*/import java.awt.BasicStroke;
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.FontMetrics;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.AffineTransform;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.Arrays;
/*   0*/import java.util.EventListener;
/*   0*/import java.util.List;
/*   0*/import javax.swing.event.EventListenerList;
/*   0*/import org.jfree.chart.ChartRenderingInfo;
/*   0*/import org.jfree.chart.entity.AxisLabelEntity;
/*   0*/import org.jfree.chart.entity.EntityCollection;
/*   0*/import org.jfree.chart.event.AxisChangeEvent;
/*   0*/import org.jfree.chart.event.AxisChangeListener;
/*   0*/import org.jfree.chart.plot.Plot;
/*   0*/import org.jfree.chart.plot.PlotRenderingInfo;
/*   0*/import org.jfree.chart.text.TextAnchor;
/*   0*/import org.jfree.chart.text.TextUtilities;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/
/*   0*/public abstract class Axis implements Cloneable, Serializable {
/*   0*/  private static final long serialVersionUID = 7719289504573298271L;
/*   0*/  
/*   0*/  public static final boolean DEFAULT_AXIS_VISIBLE = true;
/*   0*/  
/* 137*/  public static final Font DEFAULT_AXIS_LABEL_FONT = new Font("SansSerif", 0, 12);
/*   0*/  
/* 141*/  public static final Paint DEFAULT_AXIS_LABEL_PAINT = Color.black;
/*   0*/  
/* 144*/  public static final RectangleInsets DEFAULT_AXIS_LABEL_INSETS = new RectangleInsets(3.0D, 3.0D, 3.0D, 3.0D);
/*   0*/  
/* 148*/  public static final Paint DEFAULT_AXIS_LINE_PAINT = Color.gray;
/*   0*/  
/* 151*/  public static final Stroke DEFAULT_AXIS_LINE_STROKE = new BasicStroke(1.0F);
/*   0*/  
/*   0*/  public static final boolean DEFAULT_TICK_LABELS_VISIBLE = true;
/*   0*/  
/* 157*/  public static final Font DEFAULT_TICK_LABEL_FONT = new Font("SansSerif", 0, 10);
/*   0*/  
/* 161*/  public static final Paint DEFAULT_TICK_LABEL_PAINT = Color.black;
/*   0*/  
/* 164*/  public static final RectangleInsets DEFAULT_TICK_LABEL_INSETS = new RectangleInsets(2.0D, 4.0D, 2.0D, 4.0D);
/*   0*/  
/*   0*/  public static final boolean DEFAULT_TICK_MARKS_VISIBLE = true;
/*   0*/  
/* 171*/  public static final Stroke DEFAULT_TICK_MARK_STROKE = new BasicStroke(1.0F);
/*   0*/  
/* 174*/  public static final Paint DEFAULT_TICK_MARK_PAINT = Color.gray;
/*   0*/  
/*   0*/  public static final float DEFAULT_TICK_MARK_INSIDE_LENGTH = 0.0F;
/*   0*/  
/*   0*/  public static final float DEFAULT_TICK_MARK_OUTSIDE_LENGTH = 2.0F;
/*   0*/  
/*   0*/  private boolean visible;
/*   0*/  
/*   0*/  private String label;
/*   0*/  
/*   0*/  private Font labelFont;
/*   0*/  
/*   0*/  private transient Paint labelPaint;
/*   0*/  
/*   0*/  private RectangleInsets labelInsets;
/*   0*/  
/*   0*/  private double labelAngle;
/*   0*/  
/*   0*/  private String labelToolTip;
/*   0*/  
/*   0*/  private String labelURL;
/*   0*/  
/*   0*/  private boolean axisLineVisible;
/*   0*/  
/*   0*/  private transient Stroke axisLineStroke;
/*   0*/  
/*   0*/  private transient Paint axisLinePaint;
/*   0*/  
/*   0*/  private boolean tickLabelsVisible;
/*   0*/  
/*   0*/  private Font tickLabelFont;
/*   0*/  
/*   0*/  private transient Paint tickLabelPaint;
/*   0*/  
/*   0*/  private RectangleInsets tickLabelInsets;
/*   0*/  
/*   0*/  private boolean tickMarksVisible;
/*   0*/  
/*   0*/  private float tickMarkInsideLength;
/*   0*/  
/*   0*/  private float tickMarkOutsideLength;
/*   0*/  
/*   0*/  private transient Stroke tickMarkStroke;
/*   0*/  
/*   0*/  private transient Paint tickMarkPaint;
/*   0*/  
/*   0*/  private double fixedDimension;
/*   0*/  
/*   0*/  private transient Plot plot;
/*   0*/  
/*   0*/  private transient EventListenerList listenerList;
/*   0*/  
/*   0*/  protected Axis(String label) {
/* 275*/    this.label = label;
/* 276*/    this.visible = true;
/* 277*/    this.labelFont = DEFAULT_AXIS_LABEL_FONT;
/* 278*/    this.labelPaint = DEFAULT_AXIS_LABEL_PAINT;
/* 279*/    this.labelInsets = DEFAULT_AXIS_LABEL_INSETS;
/* 280*/    this.labelAngle = 0.0D;
/* 281*/    this.labelToolTip = null;
/* 282*/    this.labelURL = null;
/* 284*/    this.axisLineVisible = true;
/* 285*/    this.axisLinePaint = DEFAULT_AXIS_LINE_PAINT;
/* 286*/    this.axisLineStroke = DEFAULT_AXIS_LINE_STROKE;
/* 288*/    this.tickLabelsVisible = true;
/* 289*/    this.tickLabelFont = DEFAULT_TICK_LABEL_FONT;
/* 290*/    this.tickLabelPaint = DEFAULT_TICK_LABEL_PAINT;
/* 291*/    this.tickLabelInsets = DEFAULT_TICK_LABEL_INSETS;
/* 293*/    this.tickMarksVisible = true;
/* 294*/    this.tickMarkStroke = DEFAULT_TICK_MARK_STROKE;
/* 295*/    this.tickMarkPaint = DEFAULT_TICK_MARK_PAINT;
/* 296*/    this.tickMarkInsideLength = 0.0F;
/* 297*/    this.tickMarkOutsideLength = 2.0F;
/* 299*/    this.plot = null;
/* 301*/    this.listenerList = new EventListenerList();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isVisible() {
/* 314*/    return this.visible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setVisible(boolean flag) {
/* 326*/    if (flag != this.visible) {
/* 327*/      this.visible = flag;
/* 328*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public String getLabel() {
/* 342*/    return this.label;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabel(String label) {
/* 357*/    String existing = this.label;
/* 358*/    if (existing != null) {
/* 359*/      if (!existing.equals(label)) {
/* 360*/        this.label = label;
/* 361*/        notifyListeners(new AxisChangeEvent(this));
/*   0*/      } 
/* 365*/    } else if (label != null) {
/* 366*/      this.label = label;
/* 367*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Font getLabelFont() {
/* 381*/    return this.labelFont;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelFont(Font font) {
/* 393*/    if (font == null)
/* 394*/      throw new IllegalArgumentException("Null 'font' argument."); 
/* 396*/    if (!this.labelFont.equals(font)) {
/* 397*/      this.labelFont = font;
/* 398*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getLabelPaint() {
/* 410*/    return this.labelPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelPaint(Paint paint) {
/* 422*/    if (paint == null)
/* 423*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/* 425*/    this.labelPaint = paint;
/* 426*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getLabelInsets() {
/* 438*/    return this.labelInsets;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelInsets(RectangleInsets insets) {
/* 450*/    if (insets == null)
/* 451*/      throw new IllegalArgumentException("Null 'insets' argument."); 
/* 453*/    if (!insets.equals(this.labelInsets)) {
/* 454*/      this.labelInsets = insets;
/* 455*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getLabelAngle() {
/* 467*/    return this.labelAngle;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelAngle(double angle) {
/* 479*/    this.labelAngle = angle;
/* 480*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public String getLabelToolTip() {
/* 493*/    return this.labelToolTip;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelToolTip(String text) {
/* 507*/    this.labelToolTip = text;
/* 508*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public String getLabelURL() {
/* 521*/    return this.labelURL;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLabelURL(String url) {
/* 535*/    this.labelURL = url;
/* 536*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isAxisLineVisible() {
/* 549*/    return this.axisLineVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisLineVisible(boolean visible) {
/* 563*/    this.axisLineVisible = visible;
/* 564*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getAxisLinePaint() {
/* 575*/    return this.axisLinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisLinePaint(Paint paint) {
/* 587*/    if (paint == null)
/* 588*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/* 590*/    this.axisLinePaint = paint;
/* 591*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getAxisLineStroke() {
/* 602*/    return this.axisLineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisLineStroke(Stroke stroke) {
/* 614*/    if (stroke == null)
/* 615*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/* 617*/    this.axisLineStroke = stroke;
/* 618*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTickLabelsVisible() {
/* 631*/    return this.tickLabelsVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickLabelsVisible(boolean flag) {
/* 647*/    if (flag != this.tickLabelsVisible) {
/* 648*/      this.tickLabelsVisible = flag;
/* 649*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Font getTickLabelFont() {
/* 662*/    return this.tickLabelFont;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickLabelFont(Font font) {
/* 675*/    if (font == null)
/* 676*/      throw new IllegalArgumentException("Null 'font' argument."); 
/* 679*/    if (!this.tickLabelFont.equals(font)) {
/* 680*/      this.tickLabelFont = font;
/* 681*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getTickLabelPaint() {
/* 694*/    return this.tickLabelPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickLabelPaint(Paint paint) {
/* 706*/    if (paint == null)
/* 707*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/* 709*/    this.tickLabelPaint = paint;
/* 710*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getTickLabelInsets() {
/* 721*/    return this.tickLabelInsets;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickLabelInsets(RectangleInsets insets) {
/* 733*/    if (insets == null)
/* 734*/      throw new IllegalArgumentException("Null 'insets' argument."); 
/* 736*/    if (!this.tickLabelInsets.equals(insets)) {
/* 737*/      this.tickLabelInsets = insets;
/* 738*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isTickMarksVisible() {
/* 752*/    return this.tickMarksVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickMarksVisible(boolean flag) {
/* 764*/    if (flag != this.tickMarksVisible) {
/* 765*/      this.tickMarksVisible = flag;
/* 766*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public float getTickMarkInsideLength() {
/* 779*/    return this.tickMarkInsideLength;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickMarkInsideLength(float length) {
/* 791*/    this.tickMarkInsideLength = length;
/* 792*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public float getTickMarkOutsideLength() {
/* 804*/    return this.tickMarkOutsideLength;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickMarkOutsideLength(float length) {
/* 816*/    this.tickMarkOutsideLength = length;
/* 817*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getTickMarkStroke() {
/* 828*/    return this.tickMarkStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickMarkStroke(Stroke stroke) {
/* 840*/    if (stroke == null)
/* 841*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/* 843*/    if (!this.tickMarkStroke.equals(stroke)) {
/* 844*/      this.tickMarkStroke = stroke;
/* 845*/      notifyListeners(new AxisChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getTickMarkPaint() {
/* 857*/    return this.tickMarkPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTickMarkPaint(Paint paint) {
/* 869*/    if (paint == null)
/* 870*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/* 872*/    this.tickMarkPaint = paint;
/* 873*/    notifyListeners(new AxisChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Plot getPlot() {
/* 886*/    return this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPlot(Plot plot) {
/* 899*/    this.plot = plot;
/* 900*/    configure();
/*   0*/  }
/*   0*/  
/*   0*/  public double getFixedDimension() {
/* 911*/    return this.fixedDimension;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedDimension(double dimension) {
/* 928*/    this.fixedDimension = dimension;
/*   0*/  }
/*   0*/  
/*   0*/  public abstract void configure();
/*   0*/  
/*   0*/  public abstract AxisSpace reserveSpace(Graphics2D paramGraphics2D, Plot paramPlot, Rectangle2D paramRectangle2D, RectangleEdge paramRectangleEdge, AxisSpace paramAxisSpace);
/*   0*/  
/*   0*/  public abstract AxisState draw(Graphics2D paramGraphics2D, double paramDouble, Rectangle2D paramRectangle2D1, Rectangle2D paramRectangle2D2, RectangleEdge paramRectangleEdge, PlotRenderingInfo paramPlotRenderingInfo);
/*   0*/  
/*   0*/  public abstract List refreshTicks(Graphics2D paramGraphics2D, AxisState paramAxisState, Rectangle2D paramRectangle2D, RectangleEdge paramRectangleEdge);
/*   0*/  
/*   0*/  public void addChangeListener(AxisChangeListener listener) {
/*1000*/    this.listenerList.add(AxisChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChangeListener(AxisChangeListener listener) {
/*1011*/    this.listenerList.remove(AxisChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hasListener(EventListener listener) {
/*1024*/    List list = Arrays.asList(this.listenerList.getListenerList());
/*1025*/    return list.contains(listener);
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(AxisChangeEvent event) {
/*1036*/    Object[] listeners = this.listenerList.getListenerList();
/*1037*/    for (int i = listeners.length - 2; i >= 0; i -= 2) {
/*1038*/      if (listeners[i] == AxisChangeListener.class)
/*1039*/        ((AxisChangeListener)listeners[i + 1]).axisChanged(event); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Rectangle2D getLabelEnclosure(Graphics2D g2, RectangleEdge edge) {
/*1056*/    Rectangle2D result = new Rectangle2D.Double();
/*1057*/    String axisLabel = getLabel();
/*1058*/    if (axisLabel != null && !axisLabel.equals("")) {
/*1059*/      FontMetrics fm = g2.getFontMetrics(getLabelFont());
/*1060*/      Rectangle2D bounds = TextUtilities.getTextBounds(axisLabel, g2, fm);
/*1061*/      RectangleInsets insets = getLabelInsets();
/*1062*/      bounds = insets.createOutsetRectangle(bounds);
/*1063*/      double angle = getLabelAngle();
/*1064*/      if (edge == RectangleEdge.LEFT || edge == RectangleEdge.RIGHT)
/*1065*/        angle -= 1.5707963267948966D; 
/*1067*/      double x = bounds.getCenterX();
/*1068*/      double y = bounds.getCenterY();
/*1069*/      AffineTransform transformer = AffineTransform.getRotateInstance(angle, x, y);
/*1071*/      Shape labelBounds = transformer.createTransformedShape(bounds);
/*1072*/      result = labelBounds.getBounds2D();
/*   0*/    } 
/*1075*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisState drawLabel(String label, Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
/*1097*/    if (state == null)
/*1098*/      throw new IllegalArgumentException("Null 'state' argument."); 
/*1101*/    if (label == null || label.equals(""))
/*1102*/      return state; 
/*1105*/    Font font = getLabelFont();
/*1106*/    RectangleInsets insets = getLabelInsets();
/*1107*/    g2.setFont(font);
/*1108*/    g2.setPaint(getLabelPaint());
/*1109*/    FontMetrics fm = g2.getFontMetrics();
/*1110*/    Rectangle2D labelBounds = TextUtilities.getTextBounds(label, g2, fm);
/*1111*/    Shape hotspot = null;
/*1113*/    if (edge == RectangleEdge.TOP) {
/*1114*/      AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
/*1117*/      Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
/*1118*/      labelBounds = rotatedLabelBounds.getBounds2D();
/*1119*/      float w = (float)labelBounds.getWidth();
/*1120*/      float h = (float)labelBounds.getHeight();
/*1121*/      float labelx = (float)dataArea.getCenterX();
/*1122*/      float labely = (float)(state.getCursor() - insets.getBottom() - h / 2.0D);
/*1124*/      TextUtilities.drawRotatedString(label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
/*1126*/      hotspot = new Rectangle2D.Float(labelx - w / 2.0F, labely - h / 2.0F, w, h);
/*1128*/      state.cursorUp(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
/*1131*/    } else if (edge == RectangleEdge.BOTTOM) {
/*1132*/      AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle(), labelBounds.getCenterX(), labelBounds.getCenterY());
/*1135*/      Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
/*1136*/      labelBounds = rotatedLabelBounds.getBounds2D();
/*1137*/      float w = (float)labelBounds.getWidth();
/*1138*/      float h = (float)labelBounds.getHeight();
/*1139*/      float labelx = (float)dataArea.getCenterX();
/*1140*/      float labely = (float)(state.getCursor() + insets.getTop() + h / 2.0D);
/*1142*/      TextUtilities.drawRotatedString(label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle(), TextAnchor.CENTER);
/*1144*/      hotspot = new Rectangle2D.Float(labelx - w / 2.0F, labely - h / 2.0F, w, h);
/*1146*/      state.cursorDown(insets.getTop() + labelBounds.getHeight() + insets.getBottom());
/*1149*/    } else if (edge == RectangleEdge.LEFT) {
/*1150*/      AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() - 1.5707963267948966D, labelBounds.getCenterX(), labelBounds.getCenterY());
/*1153*/      Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
/*1154*/      labelBounds = rotatedLabelBounds.getBounds2D();
/*1155*/      float w = (float)labelBounds.getWidth();
/*1156*/      float h = (float)labelBounds.getHeight();
/*1157*/      float labelx = (float)(state.getCursor() - insets.getRight() - w / 2.0D);
/*1159*/      float labely = (float)dataArea.getCenterY();
/*1160*/      TextUtilities.drawRotatedString(label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle() - 1.5707963267948966D, TextAnchor.CENTER);
/*1163*/      hotspot = new Rectangle2D.Float(labelx - w / 2.0F, labely - h / 2.0F, w, h);
/*1165*/      state.cursorLeft(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
/*1168*/    } else if (edge == RectangleEdge.RIGHT) {
/*1170*/      AffineTransform t = AffineTransform.getRotateInstance(getLabelAngle() + 1.5707963267948966D, labelBounds.getCenterX(), labelBounds.getCenterY());
/*1173*/      Shape rotatedLabelBounds = t.createTransformedShape(labelBounds);
/*1174*/      labelBounds = rotatedLabelBounds.getBounds2D();
/*1175*/      float w = (float)labelBounds.getWidth();
/*1176*/      float h = (float)labelBounds.getHeight();
/*1177*/      float labelx = (float)(state.getCursor() + insets.getLeft() + w / 2.0D);
/*1179*/      float labely = (float)(dataArea.getY() + dataArea.getHeight() / 2.0D);
/*1181*/      TextUtilities.drawRotatedString(label, g2, labelx, labely, TextAnchor.CENTER, getLabelAngle() + 1.5707963267948966D, TextAnchor.CENTER);
/*1184*/      hotspot = new Rectangle2D.Float(labelx - w / 2.0F, labely - h / 2.0F, w, h);
/*1186*/      state.cursorRight(insets.getLeft() + labelBounds.getWidth() + insets.getRight());
/*   0*/    } 
/*1190*/    if (plotState != null && hotspot != null) {
/*1191*/      ChartRenderingInfo owner = plotState.getOwner();
/*1192*/      if (owner == null)
/*1192*/        return state; 
/*1192*/      EntityCollection entities = owner.getEntityCollection();
/*1193*/      if (entities != null)
/*1194*/        entities.add(new AxisLabelEntity(this, hotspot, this.labelToolTip, this.labelURL)); 
/*   0*/    } 
/*1198*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawAxisLine(Graphics2D g2, double cursor, Rectangle2D dataArea, RectangleEdge edge) {
/*1213*/    Line2D axisLine = null;
/*1214*/    if (edge == RectangleEdge.TOP) {
/*1215*/      axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
/*1218*/    } else if (edge == RectangleEdge.BOTTOM) {
/*1219*/      axisLine = new Line2D.Double(dataArea.getX(), cursor, dataArea.getMaxX(), cursor);
/*1222*/    } else if (edge == RectangleEdge.LEFT) {
/*1223*/      axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
/*1226*/    } else if (edge == RectangleEdge.RIGHT) {
/*1227*/      axisLine = new Line2D.Double(cursor, dataArea.getY(), cursor, dataArea.getMaxY());
/*   0*/    } 
/*1230*/    g2.setPaint(this.axisLinePaint);
/*1231*/    g2.setStroke(this.axisLineStroke);
/*1232*/    g2.draw(axisLine);
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1245*/    Axis clone = (Axis)super.clone();
/*1247*/    clone.plot = null;
/*1248*/    clone.listenerList = new EventListenerList();
/*1249*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1260*/    if (obj == this)
/*1261*/      return true; 
/*1263*/    if (!(obj instanceof Axis))
/*1264*/      return false; 
/*1266*/    Axis that = (Axis)obj;
/*1267*/    if (this.visible != that.visible)
/*1268*/      return false; 
/*1270*/    if (!ObjectUtilities.equal(this.label, that.label))
/*1271*/      return false; 
/*1273*/    if (!ObjectUtilities.equal(this.labelFont, that.labelFont))
/*1274*/      return false; 
/*1276*/    if (!PaintUtilities.equal(this.labelPaint, that.labelPaint))
/*1277*/      return false; 
/*1279*/    if (!ObjectUtilities.equal(this.labelInsets, that.labelInsets))
/*1280*/      return false; 
/*1282*/    if (this.labelAngle != that.labelAngle)
/*1283*/      return false; 
/*1285*/    if (!ObjectUtilities.equal(this.labelToolTip, that.labelToolTip))
/*1286*/      return false; 
/*1288*/    if (!ObjectUtilities.equal(this.labelURL, that.labelURL))
/*1289*/      return false; 
/*1291*/    if (this.axisLineVisible != that.axisLineVisible)
/*1292*/      return false; 
/*1294*/    if (!ObjectUtilities.equal(this.axisLineStroke, that.axisLineStroke))
/*1295*/      return false; 
/*1297*/    if (!PaintUtilities.equal(this.axisLinePaint, that.axisLinePaint))
/*1298*/      return false; 
/*1300*/    if (this.tickLabelsVisible != that.tickLabelsVisible)
/*1301*/      return false; 
/*1303*/    if (!ObjectUtilities.equal(this.tickLabelFont, that.tickLabelFont))
/*1304*/      return false; 
/*1306*/    if (!PaintUtilities.equal(this.tickLabelPaint, that.tickLabelPaint))
/*1307*/      return false; 
/*1309*/    if (!ObjectUtilities.equal(this.tickLabelInsets, that.tickLabelInsets))
/*1312*/      return false; 
/*1314*/    if (this.tickMarksVisible != that.tickMarksVisible)
/*1315*/      return false; 
/*1317*/    if (this.tickMarkInsideLength != that.tickMarkInsideLength)
/*1318*/      return false; 
/*1320*/    if (this.tickMarkOutsideLength != that.tickMarkOutsideLength)
/*1321*/      return false; 
/*1323*/    if (!PaintUtilities.equal(this.tickMarkPaint, that.tickMarkPaint))
/*1324*/      return false; 
/*1326*/    if (!ObjectUtilities.equal(this.tickMarkStroke, that.tickMarkStroke))
/*1327*/      return false; 
/*1329*/    if (this.fixedDimension != that.fixedDimension)
/*1330*/      return false; 
/*1332*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*1343*/    stream.defaultWriteObject();
/*1344*/    SerialUtilities.writePaint(this.labelPaint, stream);
/*1345*/    SerialUtilities.writePaint(this.tickLabelPaint, stream);
/*1346*/    SerialUtilities.writeStroke(this.axisLineStroke, stream);
/*1347*/    SerialUtilities.writePaint(this.axisLinePaint, stream);
/*1348*/    SerialUtilities.writeStroke(this.tickMarkStroke, stream);
/*1349*/    SerialUtilities.writePaint(this.tickMarkPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*1362*/    stream.defaultReadObject();
/*1363*/    this.labelPaint = SerialUtilities.readPaint(stream);
/*1364*/    this.tickLabelPaint = SerialUtilities.readPaint(stream);
/*1365*/    this.axisLineStroke = SerialUtilities.readStroke(stream);
/*1366*/    this.axisLinePaint = SerialUtilities.readPaint(stream);
/*1367*/    this.tickMarkStroke = SerialUtilities.readStroke(stream);
/*1368*/    this.tickMarkPaint = SerialUtilities.readPaint(stream);
/*1369*/    this.listenerList = new EventListenerList();
/*   0*/  }
/*   0*/}
