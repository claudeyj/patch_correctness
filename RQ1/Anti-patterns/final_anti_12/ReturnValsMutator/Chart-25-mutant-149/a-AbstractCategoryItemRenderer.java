/*   0*/package org.jfree.chart.renderer.category;
/*   0*/
/*   0*/import java.awt.AlphaComposite;
/*   0*/import java.awt.Composite;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.GradientPaint;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.jfree.chart.LegendItem;
/*   0*/import org.jfree.chart.LegendItemCollection;
/*   0*/import org.jfree.chart.annotations.CategoryAnnotation;
/*   0*/import org.jfree.chart.axis.CategoryAxis;
/*   0*/import org.jfree.chart.axis.ValueAxis;
/*   0*/import org.jfree.chart.entity.CategoryItemEntity;
/*   0*/import org.jfree.chart.entity.EntityCollection;
/*   0*/import org.jfree.chart.event.RendererChangeEvent;
/*   0*/import org.jfree.chart.labels.CategoryItemLabelGenerator;
/*   0*/import org.jfree.chart.labels.CategorySeriesLabelGenerator;
/*   0*/import org.jfree.chart.labels.CategoryToolTipGenerator;
/*   0*/import org.jfree.chart.labels.ItemLabelPosition;
/*   0*/import org.jfree.chart.labels.StandardCategorySeriesLabelGenerator;
/*   0*/import org.jfree.chart.plot.CategoryMarker;
/*   0*/import org.jfree.chart.plot.CategoryPlot;
/*   0*/import org.jfree.chart.plot.DrawingSupplier;
/*   0*/import org.jfree.chart.plot.IntervalMarker;
/*   0*/import org.jfree.chart.plot.Marker;
/*   0*/import org.jfree.chart.plot.PlotOrientation;
/*   0*/import org.jfree.chart.plot.PlotRenderingInfo;
/*   0*/import org.jfree.chart.plot.ValueMarker;
/*   0*/import org.jfree.chart.renderer.AbstractRenderer;
/*   0*/import org.jfree.chart.text.TextUtilities;
/*   0*/import org.jfree.chart.urls.CategoryURLGenerator;
/*   0*/import org.jfree.chart.util.GradientPaintTransformer;
/*   0*/import org.jfree.chart.util.Layer;
/*   0*/import org.jfree.chart.util.LengthAdjustmentType;
/*   0*/import org.jfree.chart.util.ObjectList;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PublicCloneable;
/*   0*/import org.jfree.chart.util.RectangleAnchor;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.general.DatasetUtilities;
/*   0*/
/*   0*/public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {
/*   0*/  private static final long serialVersionUID = 1247553218442497391L;
/*   0*/  
/*   0*/  private CategoryPlot plot;
/*   0*/  
/* 231*/  private ObjectList itemLabelGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryItemLabelGenerator baseItemLabelGenerator;
/*   0*/  
/* 232*/  private ObjectList toolTipGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryToolTipGenerator baseToolTipGenerator;
/*   0*/  
/* 233*/  private ObjectList urlGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryURLGenerator baseURLGenerator;
/*   0*/  
/* 234*/  private CategorySeriesLabelGenerator legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
/*   0*/  
/*   0*/  private CategorySeriesLabelGenerator legendItemToolTipGenerator;
/*   0*/  
/*   0*/  private CategorySeriesLabelGenerator legendItemURLGenerator;
/*   0*/  
/* 236*/  private List backgroundAnnotations = new ArrayList();
/*   0*/  
/* 237*/  private List foregroundAnnotations = new ArrayList();
/*   0*/  
/*   0*/  private transient int rowCount;
/*   0*/  
/*   0*/  private transient int columnCount;
/*   0*/  
/*   0*/  public int getPassCount() {
/* 248*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot getPlot() {
/* 261*/    return this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPlot(CategoryPlot plot) {
/* 274*/    if (plot == null)
/* 275*/      throw new IllegalArgumentException("Null 'plot' argument."); 
/* 277*/    this.plot = plot;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column) {
/* 295*/    CategoryItemLabelGenerator generator = (CategoryItemLabelGenerator)this.itemLabelGeneratorList.get(row);
/* 297*/    if (generator == null)
/* 298*/      generator = this.baseItemLabelGenerator; 
/* 300*/    return generator;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {
/* 313*/    return (CategoryItemLabelGenerator)this.itemLabelGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
/* 328*/    setSeriesItemLabelGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify) {
/* 345*/    this.itemLabelGeneratorList.set(series, generator);
/* 346*/    if (notify)
/* 347*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
/* 359*/    return this.baseItemLabelGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator) {
/* 372*/    setBaseItemLabelGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify) {
/* 388*/    this.baseItemLabelGenerator = generator;
/* 389*/    if (notify)
/* 390*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getToolTipGenerator(int row, int column) {
/* 410*/    CategoryToolTipGenerator result = null;
/* 411*/    result = getSeriesToolTipGenerator(row);
/* 412*/    if (result == null)
/* 413*/      result = this.baseToolTipGenerator; 
/* 415*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
/* 429*/    return (CategoryToolTipGenerator)this.toolTipGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator) {
/* 444*/    setSeriesToolTipGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify) {
/* 462*/    this.toolTipGeneratorList.set(series, generator);
/* 463*/    if (notify)
/* 464*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getBaseToolTipGenerator() {
/* 476*/    return this.baseToolTipGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseToolTipGenerator(CategoryToolTipGenerator generator) {
/* 488*/    setBaseToolTipGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseToolTipGenerator(CategoryToolTipGenerator generator, boolean notify) {
/* 504*/    this.baseToolTipGenerator = generator;
/* 505*/    if (notify)
/* 506*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getURLGenerator(int row, int column) {
/* 521*/    CategoryURLGenerator generator = (CategoryURLGenerator)this.urlGeneratorList.get(row);
/* 523*/    if (generator == null)
/* 524*/      generator = this.baseURLGenerator; 
/* 526*/    return generator;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getSeriesURLGenerator(int series) {
/* 539*/    return (CategoryURLGenerator)this.urlGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesURLGenerator(int series, CategoryURLGenerator generator) {
/* 553*/    setSeriesURLGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesURLGenerator(int series, CategoryURLGenerator generator, boolean notify) {
/* 570*/    this.urlGeneratorList.set(series, generator);
/* 571*/    if (notify)
/* 572*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getBaseURLGenerator() {
/* 584*/    return this.baseURLGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseURLGenerator(CategoryURLGenerator generator) {
/* 595*/    setBaseURLGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseURLGenerator(CategoryURLGenerator generator, boolean notify) {
/* 610*/    this.baseURLGenerator = generator;
/* 611*/    if (notify)
/* 612*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation) {
/* 629*/    addAnnotation(annotation, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation, Layer layer) {
/* 641*/    if (annotation == null)
/* 642*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/* 644*/    if (layer.equals(Layer.FOREGROUND)) {
/* 645*/      this.foregroundAnnotations.add(annotation);
/* 646*/      notifyListeners(new RendererChangeEvent(this));
/* 648*/    } else if (layer.equals(Layer.BACKGROUND)) {
/* 649*/      this.backgroundAnnotations.add(annotation);
/* 650*/      notifyListeners(new RendererChangeEvent(this));
/*   0*/    } else {
/* 654*/      throw new RuntimeException("Unknown layer.");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(CategoryAnnotation annotation) {
/* 670*/    boolean removed = this.foregroundAnnotations.remove(annotation);
/* 671*/    removed &= this.backgroundAnnotations.remove(annotation);
/* 672*/    notifyListeners(new RendererChangeEvent(this));
/* 673*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAnnotations() {
/* 683*/    this.foregroundAnnotations.clear();
/* 684*/    this.backgroundAnnotations.clear();
/* 685*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public int getRowCount() {
/* 695*/    return this.rowCount;
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnCount() {
/* 705*/    return this.columnCount;
/*   0*/  }
/*   0*/  
/*   0*/  protected CategoryItemRendererState createState(PlotRenderingInfo info) {
/* 721*/    return new CategoryItemRendererState(info);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, int rendererIndex, PlotRenderingInfo info) {
/* 745*/    setPlot(plot);
/* 746*/    CategoryDataset data = plot.getDataset(rendererIndex);
/* 747*/    if (data != null) {
/* 748*/      this.rowCount = data.getRowCount();
/* 749*/      this.columnCount = data.getColumnCount();
/*   0*/    } else {
/* 752*/      this.rowCount = 0;
/* 753*/      this.columnCount = 0;
/*   0*/    } 
/* 755*/    return createState(info);
/*   0*/  }
/*   0*/  
/*   0*/  public Range findRangeBounds(CategoryDataset dataset) {
/* 769*/    return DatasetUtilities.findRangeBounds(dataset);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
/* 785*/    plot.drawBackground(g2, dataArea);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
/* 802*/    plot.drawOutline(g2, dataArea);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawDomainGridline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value) {
/* 827*/    Line2D line = null;
/* 828*/    PlotOrientation orientation = plot.getOrientation();
/* 830*/    if (orientation == PlotOrientation.HORIZONTAL) {
/* 831*/      line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
/* 834*/    } else if (orientation == PlotOrientation.VERTICAL) {
/* 835*/      line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
/*   0*/    } 
/* 839*/    Paint paint = plot.getDomainGridlinePaint();
/* 840*/    if (paint == null)
/* 841*/      paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT; 
/* 843*/    g2.setPaint(paint);
/* 845*/    Stroke stroke = plot.getDomainGridlineStroke();
/* 846*/    if (stroke == null)
/* 847*/      stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE; 
/* 849*/    g2.setStroke(stroke);
/* 851*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawRangeGridline(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value) {
/* 874*/    Range range = axis.getRange();
/* 875*/    if (!range.contains(value))
/*   0*/      return; 
/* 879*/    PlotOrientation orientation = plot.getOrientation();
/* 880*/    double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
/* 881*/    Line2D line = null;
/* 882*/    if (orientation == PlotOrientation.HORIZONTAL) {
/* 883*/      line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/* 886*/    } else if (orientation == PlotOrientation.VERTICAL) {
/* 887*/      line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/*   0*/    } 
/* 891*/    Paint paint = plot.getRangeGridlinePaint();
/* 892*/    if (paint == null)
/* 893*/      paint = CategoryPlot.DEFAULT_GRIDLINE_PAINT; 
/* 895*/    g2.setPaint(paint);
/* 897*/    Stroke stroke = plot.getRangeGridlineStroke();
/* 898*/    if (stroke == null)
/* 899*/      stroke = CategoryPlot.DEFAULT_GRIDLINE_STROKE; 
/* 901*/    g2.setStroke(stroke);
/* 903*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea) {
/* 925*/    Comparable category = marker.getKey();
/* 926*/    CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
/* 927*/    int columnIndex = dataset.getColumnIndex(category);
/* 928*/    if (columnIndex < 0)
/*   0*/      return; 
/* 932*/    Composite savedComposite = g2.getComposite();
/* 933*/    g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/* 936*/    PlotOrientation orientation = plot.getOrientation();
/* 937*/    Rectangle2D bounds = null;
/* 938*/    if (marker.getDrawAsLine()) {
/* 939*/      double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/* 942*/      Line2D line = null;
/* 943*/      if (orientation == PlotOrientation.HORIZONTAL) {
/* 944*/        line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/* 947*/      } else if (orientation == PlotOrientation.VERTICAL) {
/* 948*/        line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/*   0*/      } 
/* 951*/      g2.setPaint(marker.getPaint());
/* 952*/      g2.setStroke(marker.getStroke());
/* 953*/      g2.draw(line);
/* 954*/      bounds = line.getBounds2D();
/*   0*/    } else {
/* 957*/      double v0 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/* 960*/      double v1 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/* 963*/      Rectangle2D area = null;
/* 964*/      if (orientation == PlotOrientation.HORIZONTAL) {
/* 965*/        area = new Rectangle2D.Double(dataArea.getMinX(), v0, dataArea.getWidth(), v1 - v0);
/* 968*/      } else if (orientation == PlotOrientation.VERTICAL) {
/* 969*/        area = new Rectangle2D.Double(v0, dataArea.getMinY(), v1 - v0, dataArea.getHeight());
/*   0*/      } 
/* 972*/      g2.setPaint(marker.getPaint());
/* 973*/      g2.fill(area);
/* 974*/      bounds = area;
/*   0*/    } 
/* 977*/    String label = marker.getLabel();
/* 978*/    RectangleAnchor anchor = marker.getLabelAnchor();
/* 979*/    if (label != null) {
/* 980*/      Font labelFont = marker.getLabelFont();
/* 981*/      g2.setFont(labelFont);
/* 982*/      g2.setPaint(marker.getLabelPaint());
/* 983*/      Point2D coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
/* 986*/      TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/    } 
/* 990*/    g2.setComposite(savedComposite);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
/*1011*/    if (marker instanceof ValueMarker) {
/*1012*/      ValueMarker vm = (ValueMarker)marker;
/*1013*/      double value = vm.getValue();
/*1014*/      Range range = axis.getRange();
/*1016*/      if (!range.contains(value))
/*   0*/        return; 
/*1020*/      Composite savedComposite = g2.getComposite();
/*1021*/      g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/*1024*/      PlotOrientation orientation = plot.getOrientation();
/*1025*/      double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
/*1027*/      Line2D line = null;
/*1028*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1029*/        line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/*1032*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1033*/        line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/*   0*/      } 
/*1037*/      g2.setPaint(marker.getPaint());
/*1038*/      g2.setStroke(marker.getStroke());
/*1039*/      g2.draw(line);
/*1041*/      String label = marker.getLabel();
/*1042*/      RectangleAnchor anchor = marker.getLabelAnchor();
/*1043*/      if (label != null) {
/*1044*/        Font labelFont = marker.getLabelFont();
/*1045*/        g2.setFont(labelFont);
/*1046*/        g2.setPaint(marker.getLabelPaint());
/*1047*/        Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
/*1051*/        TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/      } 
/*1055*/      g2.setComposite(savedComposite);
/*1057*/    } else if (marker instanceof IntervalMarker) {
/*1058*/      IntervalMarker im = (IntervalMarker)marker;
/*1059*/      double start = im.getStartValue();
/*1060*/      double end = im.getEndValue();
/*1061*/      Range range = axis.getRange();
/*1062*/      if (!range.intersects(start, end))
/*   0*/        return; 
/*1066*/      Composite savedComposite = g2.getComposite();
/*1067*/      g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/*1070*/      double start2d = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
/*1072*/      double end2d = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
/*1074*/      double low = Math.min(start2d, end2d);
/*1075*/      double high = Math.max(start2d, end2d);
/*1077*/      PlotOrientation orientation = plot.getOrientation();
/*1078*/      Rectangle2D rect = null;
/*1079*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1081*/        low = Math.max(low, dataArea.getMinX());
/*1082*/        high = Math.min(high, dataArea.getMaxX());
/*1083*/        rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
/*1087*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1089*/        low = Math.max(low, dataArea.getMinY());
/*1090*/        high = Math.min(high, dataArea.getMaxY());
/*1091*/        rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
/*   0*/      } 
/*1095*/      Paint p = marker.getPaint();
/*1096*/      if (p instanceof GradientPaint) {
/*1097*/        GradientPaint gp = (GradientPaint)p;
/*1098*/        GradientPaintTransformer t = im.getGradientPaintTransformer();
/*1099*/        if (t != null)
/*1100*/          gp = t.transform(gp, rect); 
/*1102*/        g2.setPaint(gp);
/*   0*/      } else {
/*1105*/        g2.setPaint(p);
/*   0*/      } 
/*1107*/      g2.fill(rect);
/*1110*/      if (im.getOutlinePaint() != null && im.getOutlineStroke() != null)
/*1111*/        if (orientation == PlotOrientation.VERTICAL) {
/*1112*/          Line2D line = new Line2D.Double();
/*1113*/          double x0 = dataArea.getMinX();
/*1114*/          double x1 = dataArea.getMaxX();
/*1115*/          g2.setPaint(im.getOutlinePaint());
/*1116*/          g2.setStroke(im.getOutlineStroke());
/*1117*/          if (range.contains(start)) {
/*1118*/            line.setLine(x0, start2d, x1, start2d);
/*1119*/            g2.draw(line);
/*   0*/          } 
/*1121*/          if (range.contains(end)) {
/*1122*/            line.setLine(x0, end2d, x1, end2d);
/*1123*/            g2.draw(line);
/*   0*/          } 
/*   0*/        } else {
/*1127*/          Line2D line = new Line2D.Double();
/*1128*/          double y0 = dataArea.getMinY();
/*1129*/          double y1 = dataArea.getMaxY();
/*1130*/          g2.setPaint(im.getOutlinePaint());
/*1131*/          g2.setStroke(im.getOutlineStroke());
/*1132*/          if (range.contains(start)) {
/*1133*/            line.setLine(start2d, y0, start2d, y1);
/*1134*/            g2.draw(line);
/*   0*/          } 
/*1136*/          if (range.contains(end)) {
/*1137*/            line.setLine(end2d, y0, end2d, y1);
/*1138*/            g2.draw(line);
/*   0*/          } 
/*   0*/        }  
/*1143*/      String label = marker.getLabel();
/*1144*/      RectangleAnchor anchor = marker.getLabelAnchor();
/*1145*/      if (label != null) {
/*1146*/        Font labelFont = marker.getLabelFont();
/*1147*/        g2.setFont(labelFont);
/*1148*/        g2.setPaint(marker.getLabelPaint());
/*1149*/        Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
/*1153*/        TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/      } 
/*1157*/      g2.setComposite(savedComposite);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
/*1183*/    Rectangle2D anchorRect = null;
/*1184*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1185*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
/*1188*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1189*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
/*   0*/    } 
/*1192*/    return RectangleAnchor.coordinates(anchorRect, anchor);
/*   0*/  }
/*   0*/  
/*   0*/  protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
/*1217*/    Rectangle2D anchorRect = null;
/*1218*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1219*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
/*1222*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1223*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
/*   0*/    } 
/*1226*/    return RectangleAnchor.coordinates(anchorRect, anchor);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItem getLegendItem(int datasetIndex, int series) {
/*1244*/    CategoryPlot p = getPlot();
/*1245*/    if (p == null)
/*1246*/      return null; 
/*1250*/    if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series))
/*1251*/      return null; 
/*1254*/    CategoryDataset dataset = p.getDataset(datasetIndex);
/*1255*/    String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
/*1257*/    String description = label;
/*1258*/    String toolTipText = null;
/*1259*/    if (this.legendItemToolTipGenerator != null)
/*1260*/      toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series); 
/*1263*/    String urlText = null;
/*1264*/    if (this.legendItemURLGenerator != null)
/*1265*/      urlText = this.legendItemURLGenerator.generateLabel(dataset, series); 
/*1268*/    Shape shape = lookupSeriesShape(series);
/*1269*/    Paint paint = lookupSeriesPaint(series);
/*1270*/    Paint outlinePaint = lookupSeriesOutlinePaint(series);
/*1271*/    Stroke outlineStroke = lookupSeriesOutlineStroke(series);
/*1273*/    LegendItem item = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
/*1275*/    item.setSeriesKey(dataset.getRowKey(series));
/*1276*/    item.setSeriesIndex(series);
/*1277*/    item.setDataset(dataset);
/*1278*/    item.setDatasetIndex(datasetIndex);
/*1279*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1291*/    if (obj == this)
/*1292*/      return true; 
/*1294*/    if (!(obj instanceof AbstractCategoryItemRenderer))
/*1295*/      return false; 
/*1297*/    AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer)obj;
/*1299*/    if (!ObjectUtilities.equal(this.itemLabelGeneratorList, that.itemLabelGeneratorList))
/*1301*/      return false; 
/*1303*/    if (!ObjectUtilities.equal(this.baseItemLabelGenerator, that.baseItemLabelGenerator))
/*1305*/      return false; 
/*1307*/    if (!ObjectUtilities.equal(this.toolTipGeneratorList, that.toolTipGeneratorList))
/*1309*/      return false; 
/*1311*/    if (!ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator))
/*1313*/      return false; 
/*1315*/    if (!ObjectUtilities.equal(this.urlGeneratorList, that.urlGeneratorList))
/*1317*/      return false; 
/*1319*/    if (!ObjectUtilities.equal(this.baseURLGenerator, that.baseURLGenerator))
/*1321*/      return false; 
/*1323*/    if (!ObjectUtilities.equal(this.legendItemLabelGenerator, that.legendItemLabelGenerator))
/*1325*/      return false; 
/*1327*/    if (!ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator))
/*1329*/      return false; 
/*1331*/    if (!ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator))
/*1333*/      return false; 
/*1335*/    if (!ObjectUtilities.equal(this.backgroundAnnotations, that.backgroundAnnotations))
/*1337*/      return false; 
/*1339*/    if (!ObjectUtilities.equal(this.foregroundAnnotations, that.foregroundAnnotations))
/*1341*/      return false; 
/*1343*/    return super.equals(obj);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1352*/    int result = super.hashCode();
/*1353*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public DrawingSupplier getDrawingSupplier() {
/*1362*/    DrawingSupplier result = null;
/*1363*/    CategoryPlot cp = getPlot();
/*1364*/    if (cp != null)
/*1365*/      result = cp.getDrawingSupplier(); 
/*1367*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, CategoryDataset dataset, int row, int column, double x, double y, boolean negative) {
/*1390*/    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
/*1392*/    if (generator != null) {
/*1393*/      Font labelFont = getItemLabelFont(row, column);
/*1394*/      Paint paint = getItemLabelPaint(row, column);
/*1395*/      g2.setFont(labelFont);
/*1396*/      g2.setPaint(paint);
/*1397*/      String label = generator.generateLabel(dataset, row, column);
/*1398*/      ItemLabelPosition position = null;
/*1399*/      if (!negative) {
/*1400*/        position = getPositiveItemLabelPosition(row, column);
/*   0*/      } else {
/*1403*/        position = getNegativeItemLabelPosition(row, column);
/*   0*/      } 
/*1405*/      Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
/*1407*/      TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, CategoryAxis domainAxis, ValueAxis rangeAxis, Layer layer, PlotRenderingInfo info) {
/*1431*/    Iterator<CategoryAnnotation> iterator = null;
/*1432*/    if (layer.equals(Layer.FOREGROUND)) {
/*1433*/      iterator = this.foregroundAnnotations.iterator();
/*1435*/    } else if (layer.equals(Layer.BACKGROUND)) {
/*1436*/      iterator = this.backgroundAnnotations.iterator();
/*   0*/    } else {
/*1440*/      throw new RuntimeException("Unknown layer.");
/*   0*/    } 
/*1442*/    while (iterator.hasNext()) {
/*1443*/      CategoryAnnotation annotation = iterator.next();
/*1444*/      annotation.draw(g2, this.plot, dataArea, domainAxis, rangeAxis, 0, info);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1462*/    AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer)super.clone();
/*1466*/    if (this.itemLabelGeneratorList != null)
/*1467*/      clone.itemLabelGeneratorList = (ObjectList)this.itemLabelGeneratorList.clone(); 
/*1471*/    if (this.baseItemLabelGenerator != null)
/*1472*/      if (this.baseItemLabelGenerator instanceof PublicCloneable) {
/*1473*/        PublicCloneable pc = (PublicCloneable)this.baseItemLabelGenerator;
/*1475*/        clone.baseItemLabelGenerator = (CategoryItemLabelGenerator)pc.clone();
/*   0*/      } else {
/*1479*/        throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
/*   0*/      }  
/*1484*/    if (this.toolTipGeneratorList != null)
/*1485*/      clone.toolTipGeneratorList = (ObjectList)this.toolTipGeneratorList.clone(); 
/*1489*/    if (this.baseToolTipGenerator != null)
/*1490*/      if (this.baseToolTipGenerator instanceof PublicCloneable) {
/*1491*/        PublicCloneable pc = (PublicCloneable)this.baseToolTipGenerator;
/*1493*/        clone.baseToolTipGenerator = (CategoryToolTipGenerator)pc.clone();
/*   0*/      } else {
/*1497*/        throw new CloneNotSupportedException("Base tool tip generator not cloneable.");
/*   0*/      }  
/*1502*/    if (this.urlGeneratorList != null)
/*1503*/      clone.urlGeneratorList = (ObjectList)this.urlGeneratorList.clone(); 
/*1506*/    if (this.baseURLGenerator != null)
/*1507*/      if (this.baseURLGenerator instanceof PublicCloneable) {
/*1508*/        PublicCloneable pc = (PublicCloneable)this.baseURLGenerator;
/*1509*/        clone.baseURLGenerator = (CategoryURLGenerator)pc.clone();
/*   0*/      } else {
/*1512*/        throw new CloneNotSupportedException("Base item URL generator not cloneable.");
/*   0*/      }  
/*1517*/    if (this.legendItemLabelGenerator instanceof PublicCloneable)
/*1518*/      clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemLabelGenerator); 
/*1521*/    if (this.legendItemToolTipGenerator instanceof PublicCloneable)
/*1522*/      clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemToolTipGenerator); 
/*1525*/    if (this.legendItemURLGenerator instanceof PublicCloneable)
/*1526*/      clone.legendItemURLGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemURLGenerator); 
/*1529*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  protected CategoryAxis getDomainAxis(CategoryPlot plot, int index) {
/*1541*/    CategoryAxis result = plot.getDomainAxis(index);
/*1542*/    if (result == null)
/*1543*/      result = plot.getDomainAxis(); 
/*1545*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
/*1557*/    ValueAxis result = plot.getRangeAxis(index);
/*1558*/    if (result == null)
/*1559*/      result = plot.getRangeAxis(); 
/*1561*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/*1573*/    if (this.plot == null)
/*1574*/      return new LegendItemCollection(); 
/*1576*/    LegendItemCollection result = new LegendItemCollection();
/*1577*/    int index = this.plot.getIndexOf(this);
/*1578*/    CategoryDataset dataset = this.plot.getDataset(index);
/*1579*/    if (dataset != null) {
/*1580*/      int seriesCount = dataset.getRowCount();
/*1581*/      for (int i = 0; i < seriesCount; i++) {
/*1582*/        if (isSeriesVisibleInLegend(i)) {
/*1583*/          LegendItem item = getLegendItem(index, i);
/*1584*/          if (item != null)
/*1585*/            result.add(item); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1591*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
/*1602*/    return this.legendItemLabelGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemLabelGenerator(CategorySeriesLabelGenerator generator) {
/*1615*/    if (generator == null)
/*1616*/      throw new IllegalArgumentException("Null 'generator' argument."); 
/*1618*/    this.legendItemLabelGenerator = generator;
/*1619*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
/*1630*/    return this.legendItemToolTipGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemToolTipGenerator(CategorySeriesLabelGenerator generator) {
/*1643*/    this.legendItemToolTipGenerator = generator;
/*1644*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
/*1655*/    return this.legendItemURLGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemURLGenerator(CategorySeriesLabelGenerator generator) {
/*1668*/    this.legendItemURLGenerator = generator;
/*1669*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  protected void addItemEntity(EntityCollection entities, CategoryDataset dataset, int row, int column, Shape hotspot) {
/*1685*/    String tip = null;
/*1686*/    CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
/*1687*/    if (tipster != null)
/*1688*/      tip = tipster.generateToolTip(dataset, row, column); 
/*1690*/    String url = null;
/*1691*/    CategoryURLGenerator urlster = getURLGenerator(row, column);
/*1692*/    if (urlster != null)
/*1693*/      url = urlster.generateURL(dataset, row, column); 
/*1695*/    CategoryItemEntity entity = new CategoryItemEntity(hotspot, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
/*1697*/    entities.add(entity);
/*   0*/  }
/*   0*/}
