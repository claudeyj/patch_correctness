/*   0*/package org.jfree.chart.renderer.category;
/*   0*/
/*   0*/import java.awt.AlphaComposite;
/*   0*/import java.awt.Composite;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.GradientPaint;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Rectangle;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.Ellipse2D;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import org.jfree.chart.ChartRenderingInfo;
/*   0*/import org.jfree.chart.LegendItem;
/*   0*/import org.jfree.chart.LegendItemCollection;
/*   0*/import org.jfree.chart.RenderingSource;
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
/*   0*/import org.jfree.chart.plot.CategoryCrosshairState;
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
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.SortOrder;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.category.CategoryDatasetSelectionState;
/*   0*/import org.jfree.data.category.SelectableCategoryDataset;
/*   0*/import org.jfree.data.general.DatasetUtilities;
/*   0*/
/*   0*/public abstract class AbstractCategoryItemRenderer extends AbstractRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {
/*   0*/  private static final long serialVersionUID = 1247553218442497391L;
/*   0*/  
/*   0*/  private CategoryPlot plot;
/*   0*/  
/* 250*/  private ObjectList itemLabelGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryItemLabelGenerator baseItemLabelGenerator;
/*   0*/  
/* 251*/  private ObjectList toolTipGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryToolTipGenerator baseToolTipGenerator;
/*   0*/  
/* 252*/  private ObjectList urlGeneratorList = new ObjectList();
/*   0*/  
/*   0*/  private CategoryURLGenerator baseURLGenerator;
/*   0*/  
/* 253*/  private CategorySeriesLabelGenerator legendItemLabelGenerator = new StandardCategorySeriesLabelGenerator();
/*   0*/  
/*   0*/  private CategorySeriesLabelGenerator legendItemToolTipGenerator;
/*   0*/  
/*   0*/  private CategorySeriesLabelGenerator legendItemURLGenerator;
/*   0*/  
/* 255*/  private List backgroundAnnotations = new ArrayList();
/*   0*/  
/* 256*/  private List foregroundAnnotations = new ArrayList();
/*   0*/  
/*   0*/  private transient int rowCount;
/*   0*/  
/*   0*/  private transient int columnCount;
/*   0*/  
/*   0*/  public int getPassCount() {
/* 267*/    return 1;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot getPlot() {
/* 280*/    return this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPlot(CategoryPlot plot) {
/* 293*/    if (plot == null)
/* 294*/      throw new IllegalArgumentException("Null 'plot' argument."); 
/* 296*/    this.plot = plot;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getItemLabelGenerator(int row, int column, boolean selected) {
/* 317*/    CategoryItemLabelGenerator generator = (CategoryItemLabelGenerator)this.itemLabelGeneratorList.get(row);
/* 319*/    if (generator == null)
/* 320*/      generator = this.baseItemLabelGenerator; 
/* 322*/    return generator;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getSeriesItemLabelGenerator(int series) {
/* 335*/    return (CategoryItemLabelGenerator)this.itemLabelGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator) {
/* 350*/    setSeriesItemLabelGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesItemLabelGenerator(int series, CategoryItemLabelGenerator generator, boolean notify) {
/* 367*/    this.itemLabelGeneratorList.set(series, generator);
/* 368*/    if (notify)
/* 369*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemLabelGenerator getBaseItemLabelGenerator() {
/* 381*/    return this.baseItemLabelGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator) {
/* 394*/    setBaseItemLabelGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseItemLabelGenerator(CategoryItemLabelGenerator generator, boolean notify) {
/* 410*/    this.baseItemLabelGenerator = generator;
/* 411*/    if (notify)
/* 412*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getToolTipGenerator(int row, int column, boolean selected) {
/* 434*/    CategoryToolTipGenerator result = null;
/* 435*/    result = getSeriesToolTipGenerator(row);
/* 436*/    if (result == null)
/* 437*/      result = this.baseToolTipGenerator; 
/* 439*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getSeriesToolTipGenerator(int series) {
/* 453*/    return (CategoryToolTipGenerator)this.toolTipGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator) {
/* 467*/    setSeriesToolTipGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesToolTipGenerator(int series, CategoryToolTipGenerator generator, boolean notify) {
/* 485*/    this.toolTipGeneratorList.set(series, generator);
/* 486*/    if (notify)
/* 487*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryToolTipGenerator getBaseToolTipGenerator() {
/* 499*/    return this.baseToolTipGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseToolTipGenerator(CategoryToolTipGenerator generator) {
/* 511*/    setBaseToolTipGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseToolTipGenerator(CategoryToolTipGenerator generator, boolean notify) {
/* 527*/    this.baseToolTipGenerator = generator;
/* 528*/    if (notify)
/* 529*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getURLGenerator(int row, int column, boolean selected) {
/* 548*/    CategoryURLGenerator generator = (CategoryURLGenerator)this.urlGeneratorList.get(row);
/* 550*/    if (generator == null)
/* 551*/      generator = this.baseURLGenerator; 
/* 553*/    return generator;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getSeriesURLGenerator(int series) {
/* 566*/    return (CategoryURLGenerator)this.urlGeneratorList.get(series);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesURLGenerator(int series, CategoryURLGenerator generator) {
/* 580*/    setSeriesURLGenerator(series, generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesURLGenerator(int series, CategoryURLGenerator generator, boolean notify) {
/* 597*/    this.urlGeneratorList.set(series, generator);
/* 598*/    if (notify)
/* 599*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryURLGenerator getBaseURLGenerator() {
/* 611*/    return this.baseURLGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseURLGenerator(CategoryURLGenerator generator) {
/* 622*/    setBaseURLGenerator(generator, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setBaseURLGenerator(CategoryURLGenerator generator, boolean notify) {
/* 637*/    this.baseURLGenerator = generator;
/* 638*/    if (notify)
/* 639*/      notifyListeners(new RendererChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation) {
/* 656*/    addAnnotation(annotation, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation, Layer layer) {
/* 668*/    if (annotation == null)
/* 669*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/* 671*/    if (layer.equals(Layer.FOREGROUND)) {
/* 672*/      this.foregroundAnnotations.add(annotation);
/* 673*/      notifyListeners(new RendererChangeEvent(this));
/* 675*/    } else if (layer.equals(Layer.BACKGROUND)) {
/* 676*/      this.backgroundAnnotations.add(annotation);
/* 677*/      notifyListeners(new RendererChangeEvent(this));
/*   0*/    } else {
/* 681*/      throw new RuntimeException("Unknown layer.");
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(CategoryAnnotation annotation) {
/* 697*/    boolean removed = this.foregroundAnnotations.remove(annotation);
/* 698*/    removed &= this.backgroundAnnotations.remove(annotation);
/* 699*/    notifyListeners(new RendererChangeEvent(this));
/* 700*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeAnnotations() {
/* 710*/    this.foregroundAnnotations.clear();
/* 711*/    this.backgroundAnnotations.clear();
/* 712*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemLabelGenerator() {
/* 723*/    return this.legendItemLabelGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemLabelGenerator(CategorySeriesLabelGenerator generator) {
/* 736*/    if (generator == null)
/* 737*/      throw new IllegalArgumentException("Null 'generator' argument."); 
/* 739*/    this.legendItemLabelGenerator = generator;
/* 740*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemToolTipGenerator() {
/* 751*/    return this.legendItemToolTipGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemToolTipGenerator(CategorySeriesLabelGenerator generator) {
/* 764*/    this.legendItemToolTipGenerator = generator;
/* 765*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public CategorySeriesLabelGenerator getLegendItemURLGenerator() {
/* 776*/    return this.legendItemURLGenerator;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLegendItemURLGenerator(CategorySeriesLabelGenerator generator) {
/* 789*/    this.legendItemURLGenerator = generator;
/* 790*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public int getRowCount() {
/* 800*/    return this.rowCount;
/*   0*/  }
/*   0*/  
/*   0*/  public int getColumnCount() {
/* 810*/    return this.columnCount;
/*   0*/  }
/*   0*/  
/*   0*/  protected CategoryItemRendererState createState(PlotRenderingInfo info) {
/* 826*/    CategoryItemRendererState state = new CategoryItemRendererState(info);
/* 827*/    int[] visibleSeriesTemp = new int[this.rowCount];
/* 828*/    int visibleSeriesCount = 0;
/* 829*/    for (int row = 0; row < this.rowCount; row++) {
/* 830*/      if (isSeriesVisible(row)) {
/* 831*/        visibleSeriesTemp[visibleSeriesCount] = row;
/* 832*/        visibleSeriesCount++;
/*   0*/      } 
/*   0*/    } 
/* 835*/    int[] visibleSeries = new int[visibleSeriesCount];
/* 836*/    System.arraycopy(visibleSeriesTemp, 0, visibleSeries, 0, visibleSeriesCount);
/* 838*/    state.setVisibleSeriesArray(visibleSeries);
/* 839*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, CategoryDataset dataset, PlotRenderingInfo info) {
/* 860*/    setPlot(plot);
/* 861*/    if (dataset != null) {
/* 862*/      this.rowCount = dataset.getRowCount();
/* 863*/      this.columnCount = dataset.getColumnCount();
/*   0*/    } else {
/* 866*/      this.rowCount = 0;
/* 867*/      this.columnCount = 0;
/*   0*/    } 
/* 869*/    CategoryItemRendererState state = createState(info);
/* 872*/    CategoryDatasetSelectionState selectionState = null;
/* 873*/    if (dataset instanceof SelectableCategoryDataset) {
/* 874*/      SelectableCategoryDataset scd = (SelectableCategoryDataset)dataset;
/* 875*/      selectionState = scd.getSelectionState();
/*   0*/    } 
/* 879*/    if (selectionState == null && info != null) {
/* 880*/      ChartRenderingInfo cri = info.getOwner();
/* 881*/      if (cri != null) {
/* 882*/        RenderingSource rs = cri.getRenderingSource();
/* 883*/        selectionState = (CategoryDatasetSelectionState)rs.getSelectionState(dataset);
/*   0*/      } 
/*   0*/    } 
/* 887*/    state.setSelectionState(selectionState);
/* 889*/    return state;
/*   0*/  }
/*   0*/  
/*   0*/  public Range findRangeBounds(CategoryDataset dataset) {
/* 902*/    return findRangeBounds(dataset, false);
/*   0*/  }
/*   0*/  
/*   0*/  protected Range findRangeBounds(CategoryDataset dataset, boolean includeInterval) {
/* 919*/    if (dataset == null)
/* 920*/      return null; 
/* 922*/    if (getDataBoundsIncludesVisibleSeriesOnly()) {
/* 923*/      List<Comparable> visibleSeriesKeys = new ArrayList();
/* 924*/      int seriesCount = dataset.getRowCount();
/* 925*/      for (int s = 0; s < seriesCount; s++) {
/* 926*/        if (isSeriesVisible(s))
/* 927*/          visibleSeriesKeys.add(dataset.getRowKey(s)); 
/*   0*/      } 
/* 930*/      return DatasetUtilities.findRangeBounds(dataset, visibleSeriesKeys, includeInterval);
/*   0*/    } 
/* 934*/    return DatasetUtilities.findRangeBounds(dataset, includeInterval);
/*   0*/  }
/*   0*/  
/*   0*/  public double getItemMiddle(Comparable rowKey, Comparable columnKey, CategoryDataset dataset, CategoryAxis axis, Rectangle2D area, RectangleEdge edge) {
/* 955*/    return axis.getCategoryMiddle(columnKey, dataset.getColumnKeys(), area, edge);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawBackground(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
/* 972*/    plot.drawBackground(g2, dataArea);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawOutline(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea) {
/* 989*/    plot.drawOutline(g2, dataArea);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawDomainLine(Graphics2D g2, CategoryPlot plot, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
/*1016*/    if (paint == null)
/*1017*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1019*/    if (stroke == null)
/*1020*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*1022*/    Line2D line = null;
/*1023*/    PlotOrientation orientation = plot.getOrientation();
/*1025*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1026*/      line = new Line2D.Double(dataArea.getMinX(), value, dataArea.getMaxX(), value);
/*1029*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1030*/      line = new Line2D.Double(value, dataArea.getMinY(), value, dataArea.getMaxY());
/*   0*/    } 
/*1034*/    g2.setPaint(paint);
/*1035*/    g2.setStroke(stroke);
/*1036*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawRangeLine(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Rectangle2D dataArea, double value, Paint paint, Stroke stroke) {
/*1059*/    Range range = axis.getRange();
/*1060*/    if (!range.contains(value))
/*   0*/      return; 
/*1064*/    PlotOrientation orientation = plot.getOrientation();
/*1065*/    Line2D line = null;
/*1066*/    double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
/*1067*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1068*/      line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/*1071*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1072*/      line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/*   0*/    } 
/*1076*/    g2.setPaint(paint);
/*1077*/    g2.setStroke(stroke);
/*1078*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawDomainMarker(Graphics2D g2, CategoryPlot plot, CategoryAxis axis, CategoryMarker marker, Rectangle2D dataArea) {
/*1100*/    Comparable category = marker.getKey();
/*1101*/    CategoryDataset dataset = plot.getDataset(plot.getIndexOf(this));
/*1102*/    int columnIndex = dataset.getColumnIndex(category);
/*1103*/    if (columnIndex < 0)
/*   0*/      return; 
/*1107*/    Composite savedComposite = g2.getComposite();
/*1108*/    g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/*1111*/    PlotOrientation orientation = plot.getOrientation();
/*1112*/    Rectangle2D bounds = null;
/*1113*/    if (marker.getDrawAsLine()) {
/*1114*/      double v = axis.getCategoryMiddle(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/*1117*/      Line2D line = null;
/*1118*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1119*/        line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/*1122*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1123*/        line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/*   0*/      } 
/*1126*/      g2.setPaint(marker.getPaint());
/*1127*/      g2.setStroke(marker.getStroke());
/*1128*/      g2.draw(line);
/*1129*/      bounds = line.getBounds2D();
/*   0*/    } else {
/*1132*/      double v0 = axis.getCategoryStart(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/*1135*/      double v1 = axis.getCategoryEnd(columnIndex, dataset.getColumnCount(), dataArea, plot.getDomainAxisEdge());
/*1138*/      Rectangle2D area = null;
/*1139*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1140*/        area = new Rectangle2D.Double(dataArea.getMinX(), v0, dataArea.getWidth(), v1 - v0);
/*1143*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1144*/        area = new Rectangle2D.Double(v0, dataArea.getMinY(), v1 - v0, dataArea.getHeight());
/*   0*/      } 
/*1147*/      g2.setPaint(marker.getPaint());
/*1148*/      g2.fill(area);
/*1149*/      bounds = area;
/*   0*/    } 
/*1152*/    String label = marker.getLabel();
/*1153*/    RectangleAnchor anchor = marker.getLabelAnchor();
/*1154*/    if (label != null) {
/*1155*/      Font labelFont = marker.getLabelFont();
/*1156*/      g2.setFont(labelFont);
/*1157*/      g2.setPaint(marker.getLabelPaint());
/*1158*/      Point2D coordinates = calculateDomainMarkerTextAnchorPoint(g2, orientation, dataArea, bounds, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
/*1161*/      TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/    } 
/*1165*/    g2.setComposite(savedComposite);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawRangeMarker(Graphics2D g2, CategoryPlot plot, ValueAxis axis, Marker marker, Rectangle2D dataArea) {
/*1186*/    if (marker instanceof ValueMarker) {
/*1187*/      ValueMarker vm = (ValueMarker)marker;
/*1188*/      double value = vm.getValue();
/*1189*/      Range range = axis.getRange();
/*1191*/      if (!range.contains(value))
/*   0*/        return; 
/*1195*/      Composite savedComposite = g2.getComposite();
/*1196*/      g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/*1199*/      PlotOrientation orientation = plot.getOrientation();
/*1200*/      double v = axis.valueToJava2D(value, dataArea, plot.getRangeAxisEdge());
/*1202*/      Line2D line = null;
/*1203*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1204*/        line = new Line2D.Double(v, dataArea.getMinY(), v, dataArea.getMaxY());
/*1207*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1208*/        line = new Line2D.Double(dataArea.getMinX(), v, dataArea.getMaxX(), v);
/*   0*/      } 
/*1212*/      g2.setPaint(marker.getPaint());
/*1213*/      g2.setStroke(marker.getStroke());
/*1214*/      g2.draw(line);
/*1216*/      String label = marker.getLabel();
/*1217*/      RectangleAnchor anchor = marker.getLabelAnchor();
/*1218*/      if (label != null) {
/*1219*/        Font labelFont = marker.getLabelFont();
/*1220*/        g2.setFont(labelFont);
/*1221*/        g2.setPaint(marker.getLabelPaint());
/*1222*/        Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, line.getBounds2D(), marker.getLabelOffset(), LengthAdjustmentType.EXPAND, anchor);
/*1226*/        TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/      } 
/*1230*/      g2.setComposite(savedComposite);
/*1232*/    } else if (marker instanceof IntervalMarker) {
/*1233*/      IntervalMarker im = (IntervalMarker)marker;
/*1234*/      double start = im.getStartValue();
/*1235*/      double end = im.getEndValue();
/*1236*/      Range range = axis.getRange();
/*1237*/      if (!range.intersects(start, end))
/*   0*/        return; 
/*1241*/      Composite savedComposite = g2.getComposite();
/*1242*/      g2.setComposite(AlphaComposite.getInstance(3, marker.getAlpha()));
/*1245*/      double start2d = axis.valueToJava2D(start, dataArea, plot.getRangeAxisEdge());
/*1247*/      double end2d = axis.valueToJava2D(end, dataArea, plot.getRangeAxisEdge());
/*1249*/      double low = Math.min(start2d, end2d);
/*1250*/      double high = Math.max(start2d, end2d);
/*1252*/      PlotOrientation orientation = plot.getOrientation();
/*1253*/      Rectangle2D rect = null;
/*1254*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*1256*/        low = Math.max(low, dataArea.getMinX());
/*1257*/        high = Math.min(high, dataArea.getMaxX());
/*1258*/        rect = new Rectangle2D.Double(low, dataArea.getMinY(), high - low, dataArea.getHeight());
/*1262*/      } else if (orientation == PlotOrientation.VERTICAL) {
/*1264*/        low = Math.max(low, dataArea.getMinY());
/*1265*/        high = Math.min(high, dataArea.getMaxY());
/*1266*/        rect = new Rectangle2D.Double(dataArea.getMinX(), low, dataArea.getWidth(), high - low);
/*   0*/      } 
/*1270*/      Paint p = marker.getPaint();
/*1271*/      if (p instanceof GradientPaint) {
/*1272*/        GradientPaint gp = (GradientPaint)p;
/*1273*/        GradientPaintTransformer t = im.getGradientPaintTransformer();
/*1274*/        if (t != null)
/*1275*/          gp = t.transform(gp, rect); 
/*1277*/        g2.setPaint(gp);
/*   0*/      } else {
/*1280*/        g2.setPaint(p);
/*   0*/      } 
/*1282*/      g2.fill(rect);
/*1285*/      if (im.getOutlinePaint() != null && im.getOutlineStroke() != null)
/*1286*/        if (orientation == PlotOrientation.VERTICAL) {
/*1287*/          Line2D line = new Line2D.Double();
/*1288*/          double x0 = dataArea.getMinX();
/*1289*/          double x1 = dataArea.getMaxX();
/*1290*/          g2.setPaint(im.getOutlinePaint());
/*1291*/          g2.setStroke(im.getOutlineStroke());
/*1292*/          if (range.contains(start)) {
/*1293*/            line.setLine(x0, start2d, x1, start2d);
/*1294*/            g2.draw(line);
/*   0*/          } 
/*1296*/          if (range.contains(end)) {
/*1297*/            line.setLine(x0, end2d, x1, end2d);
/*1298*/            g2.draw(line);
/*   0*/          } 
/*   0*/        } else {
/*1302*/          Line2D line = new Line2D.Double();
/*1303*/          double y0 = dataArea.getMinY();
/*1304*/          double y1 = dataArea.getMaxY();
/*1305*/          g2.setPaint(im.getOutlinePaint());
/*1306*/          g2.setStroke(im.getOutlineStroke());
/*1307*/          if (range.contains(start)) {
/*1308*/            line.setLine(start2d, y0, start2d, y1);
/*1309*/            g2.draw(line);
/*   0*/          } 
/*1311*/          if (range.contains(end)) {
/*1312*/            line.setLine(end2d, y0, end2d, y1);
/*1313*/            g2.draw(line);
/*   0*/          } 
/*   0*/        }  
/*1318*/      String label = marker.getLabel();
/*1319*/      RectangleAnchor anchor = marker.getLabelAnchor();
/*1320*/      if (label != null) {
/*1321*/        Font labelFont = marker.getLabelFont();
/*1322*/        g2.setFont(labelFont);
/*1323*/        g2.setPaint(marker.getLabelPaint());
/*1324*/        Point2D coordinates = calculateRangeMarkerTextAnchorPoint(g2, orientation, dataArea, rect, marker.getLabelOffset(), marker.getLabelOffsetType(), anchor);
/*1328*/        TextUtilities.drawAlignedString(label, g2, (float)coordinates.getX(), (float)coordinates.getY(), marker.getLabelTextAnchor());
/*   0*/      } 
/*1332*/      g2.setComposite(savedComposite);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Point2D calculateDomainMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
/*1358*/    Rectangle2D anchorRect = null;
/*1359*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1360*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
/*1363*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1364*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
/*   0*/    } 
/*1367*/    return RectangleAnchor.coordinates(anchorRect, anchor);
/*   0*/  }
/*   0*/  
/*   0*/  protected Point2D calculateRangeMarkerTextAnchorPoint(Graphics2D g2, PlotOrientation orientation, Rectangle2D dataArea, Rectangle2D markerArea, RectangleInsets markerOffset, LengthAdjustmentType labelOffsetType, RectangleAnchor anchor) {
/*1392*/    Rectangle2D anchorRect = null;
/*1393*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*1394*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, labelOffsetType, LengthAdjustmentType.CONTRACT);
/*1397*/    } else if (orientation == PlotOrientation.VERTICAL) {
/*1398*/      anchorRect = markerOffset.createAdjustedRectangle(markerArea, LengthAdjustmentType.CONTRACT, labelOffsetType);
/*   0*/    } 
/*1401*/    return RectangleAnchor.coordinates(anchorRect, anchor);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItem getLegendItem(int datasetIndex, int series) {
/*1419*/    CategoryPlot p = getPlot();
/*1420*/    if (p == null)
/*1421*/      return null; 
/*1425*/    if (!isSeriesVisible(series) || !isSeriesVisibleInLegend(series))
/*1426*/      return null; 
/*1429*/    CategoryDataset dataset = p.getDataset(datasetIndex);
/*1430*/    String label = this.legendItemLabelGenerator.generateLabel(dataset, series);
/*1432*/    String description = label;
/*1433*/    String toolTipText = null;
/*1434*/    if (this.legendItemToolTipGenerator != null)
/*1435*/      toolTipText = this.legendItemToolTipGenerator.generateLabel(dataset, series); 
/*1438*/    String urlText = null;
/*1439*/    if (this.legendItemURLGenerator != null)
/*1440*/      urlText = this.legendItemURLGenerator.generateLabel(dataset, series); 
/*1443*/    Shape shape = lookupLegendShape(series);
/*1444*/    Paint paint = lookupSeriesPaint(series);
/*1445*/    Paint outlinePaint = lookupSeriesOutlinePaint(series);
/*1446*/    Stroke outlineStroke = lookupSeriesOutlineStroke(series);
/*1448*/    LegendItem item = new LegendItem(label, description, toolTipText, urlText, shape, paint, outlineStroke, outlinePaint);
/*1450*/    item.setLabelFont(lookupLegendTextFont(series));
/*1451*/    Paint labelPaint = lookupLegendTextPaint(series);
/*1452*/    if (labelPaint != null)
/*1453*/      item.setLabelPaint(labelPaint); 
/*1455*/    item.setSeriesKey(dataset.getRowKey(series));
/*1456*/    item.setSeriesIndex(series);
/*1457*/    item.setDataset(dataset);
/*1458*/    item.setDatasetIndex(datasetIndex);
/*1459*/    return item;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1471*/    if (obj == this)
/*1472*/      return true; 
/*1474*/    if (!(obj instanceof AbstractCategoryItemRenderer))
/*1475*/      return false; 
/*1477*/    AbstractCategoryItemRenderer that = (AbstractCategoryItemRenderer)obj;
/*1479*/    if (!ObjectUtilities.equal(this.itemLabelGeneratorList, that.itemLabelGeneratorList))
/*1481*/      return false; 
/*1483*/    if (!ObjectUtilities.equal(this.baseItemLabelGenerator, that.baseItemLabelGenerator))
/*1485*/      return false; 
/*1487*/    if (!ObjectUtilities.equal(this.toolTipGeneratorList, that.toolTipGeneratorList))
/*1489*/      return false; 
/*1491*/    if (!ObjectUtilities.equal(this.baseToolTipGenerator, that.baseToolTipGenerator))
/*1493*/      return false; 
/*1495*/    if (!ObjectUtilities.equal(this.urlGeneratorList, that.urlGeneratorList))
/*1497*/      return false; 
/*1499*/    if (!ObjectUtilities.equal(this.baseURLGenerator, that.baseURLGenerator))
/*1501*/      return false; 
/*1503*/    if (!ObjectUtilities.equal(this.legendItemLabelGenerator, that.legendItemLabelGenerator))
/*1505*/      return false; 
/*1507*/    if (!ObjectUtilities.equal(this.legendItemToolTipGenerator, that.legendItemToolTipGenerator))
/*1509*/      return false; 
/*1511*/    if (!ObjectUtilities.equal(this.legendItemURLGenerator, that.legendItemURLGenerator))
/*1513*/      return false; 
/*1515*/    if (!ObjectUtilities.equal(this.backgroundAnnotations, that.backgroundAnnotations))
/*1517*/      return false; 
/*1519*/    if (!ObjectUtilities.equal(this.foregroundAnnotations, that.foregroundAnnotations))
/*1521*/      return false; 
/*1523*/    return super.equals(obj);
/*   0*/  }
/*   0*/  
/*   0*/  public int hashCode() {
/*1532*/    int result = super.hashCode();
/*1533*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public DrawingSupplier getDrawingSupplier() {
/*1542*/    DrawingSupplier result = null;
/*1543*/    CategoryPlot cp = getPlot();
/*1544*/    if (cp != null)
/*1545*/      result = cp.getDrawingSupplier(); 
/*1547*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected void updateCrosshairValues(CategoryCrosshairState crosshairState, Comparable rowKey, Comparable columnKey, double value, int datasetIndex, double transX, double transY, PlotOrientation orientation) {
/*1573*/    if (orientation == null)
/*1574*/      throw new IllegalArgumentException("Null 'orientation' argument."); 
/*1577*/    if (crosshairState != null)
/*1578*/      if (this.plot.isRangeCrosshairLockedOnData()) {
/*1580*/        crosshairState.updateCrosshairPoint(rowKey, columnKey, value, datasetIndex, transX, transY, orientation);
/*   0*/      } else {
/*1584*/        crosshairState.updateCrosshairX(rowKey, columnKey, datasetIndex, transX, orientation);
/*   0*/      }  
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawItemLabel(Graphics2D g2, PlotOrientation orientation, CategoryDataset dataset, int row, int column, boolean selected, double x, double y, boolean negative) {
/*1610*/    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column, selected);
/*1612*/    if (generator != null) {
/*1613*/      Font labelFont = getItemLabelFont(row, column, selected);
/*1614*/      Paint paint = getItemLabelPaint(row, column, selected);
/*1615*/      g2.setFont(labelFont);
/*1616*/      g2.setPaint(paint);
/*1617*/      String label = generator.generateLabel(dataset, row, column);
/*1618*/      ItemLabelPosition position = null;
/*1619*/      if (!negative) {
/*1620*/        position = getPositiveItemLabelPosition(row, column, selected);
/*   0*/      } else {
/*1623*/        position = getNegativeItemLabelPosition(row, column, selected);
/*   0*/      } 
/*1625*/      Point2D anchorPoint = calculateLabelAnchorPoint(position.getItemLabelAnchor(), x, y, orientation);
/*1627*/      TextUtilities.drawRotatedString(label, g2, (float)anchorPoint.getX(), (float)anchorPoint.getY(), position.getTextAnchor(), position.getAngle(), position.getRotationAnchor());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, CategoryAxis domainAxis, ValueAxis rangeAxis, Layer layer, PlotRenderingInfo info) {
/*1651*/    Iterator<CategoryAnnotation> iterator = null;
/*1652*/    if (layer.equals(Layer.FOREGROUND)) {
/*1653*/      iterator = this.foregroundAnnotations.iterator();
/*1655*/    } else if (layer.equals(Layer.BACKGROUND)) {
/*1656*/      iterator = this.backgroundAnnotations.iterator();
/*   0*/    } else {
/*1660*/      throw new RuntimeException("Unknown layer.");
/*   0*/    } 
/*1662*/    while (iterator.hasNext()) {
/*1663*/      CategoryAnnotation annotation = iterator.next();
/*1664*/      annotation.draw(g2, this.plot, dataArea, domainAxis, rangeAxis, 0, info);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1682*/    AbstractCategoryItemRenderer clone = (AbstractCategoryItemRenderer)super.clone();
/*1686*/    if (this.itemLabelGeneratorList != null)
/*1687*/      clone.itemLabelGeneratorList = (ObjectList)this.itemLabelGeneratorList.clone(); 
/*1691*/    if (this.baseItemLabelGenerator != null)
/*1692*/      if (this.baseItemLabelGenerator instanceof PublicCloneable) {
/*1693*/        PublicCloneable pc = (PublicCloneable)this.baseItemLabelGenerator;
/*1695*/        clone.baseItemLabelGenerator = (CategoryItemLabelGenerator)pc.clone();
/*   0*/      } else {
/*1699*/        throw new CloneNotSupportedException("ItemLabelGenerator not cloneable.");
/*   0*/      }  
/*1704*/    if (this.toolTipGeneratorList != null)
/*1705*/      clone.toolTipGeneratorList = (ObjectList)this.toolTipGeneratorList.clone(); 
/*1709*/    if (this.baseToolTipGenerator != null)
/*1710*/      if (this.baseToolTipGenerator instanceof PublicCloneable) {
/*1711*/        PublicCloneable pc = (PublicCloneable)this.baseToolTipGenerator;
/*1713*/        clone.baseToolTipGenerator = (CategoryToolTipGenerator)pc.clone();
/*   0*/      } else {
/*1717*/        throw new CloneNotSupportedException("Base tool tip generator not cloneable.");
/*   0*/      }  
/*1722*/    if (this.urlGeneratorList != null)
/*1723*/      clone.urlGeneratorList = (ObjectList)this.urlGeneratorList.clone(); 
/*1726*/    if (this.baseURLGenerator != null)
/*1727*/      if (this.baseURLGenerator instanceof PublicCloneable) {
/*1728*/        PublicCloneable pc = (PublicCloneable)this.baseURLGenerator;
/*1729*/        clone.baseURLGenerator = (CategoryURLGenerator)pc.clone();
/*   0*/      } else {
/*1732*/        throw new CloneNotSupportedException("Base item URL generator not cloneable.");
/*   0*/      }  
/*1737*/    if (this.legendItemLabelGenerator instanceof PublicCloneable)
/*1738*/      clone.legendItemLabelGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemLabelGenerator); 
/*1741*/    if (this.legendItemToolTipGenerator instanceof PublicCloneable)
/*1742*/      clone.legendItemToolTipGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemToolTipGenerator); 
/*1745*/    if (this.legendItemURLGenerator instanceof PublicCloneable)
/*1746*/      clone.legendItemURLGenerator = (CategorySeriesLabelGenerator)ObjectUtilities.clone(this.legendItemURLGenerator); 
/*1749*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  protected CategoryAxis getDomainAxis(CategoryPlot plot, CategoryDataset dataset) {
/*1762*/    int datasetIndex = plot.indexOf(dataset);
/*1763*/    return plot.getDomainAxisForDataset(datasetIndex);
/*   0*/  }
/*   0*/  
/*   0*/  protected ValueAxis getRangeAxis(CategoryPlot plot, int index) {
/*1775*/    ValueAxis result = plot.getRangeAxis(index);
/*1776*/    if (result == null)
/*1777*/      result = plot.getRangeAxis(); 
/*1779*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/*1791*/    LegendItemCollection result = new LegendItemCollection();
/*1792*/    if (this.plot == null)
/*1793*/      return result; 
/*1795*/    int index = this.plot.getIndexOf(this);
/*1796*/    CategoryDataset dataset = this.plot.getDataset(index);
/*1797*/    if (dataset != null)
/*1798*/      return result; 
/*1800*/    int seriesCount = dataset.getRowCount();
/*1801*/    if (this.plot.getRowRenderingOrder().equals(SortOrder.ASCENDING)) {
/*1802*/      for (int i = 0; i < seriesCount; i++) {
/*1803*/        if (isSeriesVisibleInLegend(i)) {
/*1804*/          LegendItem item = getLegendItem(index, i);
/*1805*/          if (item != null)
/*1806*/            result.add(item); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/*1812*/      for (int i = seriesCount - 1; i >= 0; i--) {
/*1813*/        if (isSeriesVisibleInLegend(i)) {
/*1814*/          LegendItem item = getLegendItem(index, i);
/*1815*/          if (item != null)
/*1816*/            result.add(item); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1821*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected void addEntity(EntityCollection entities, Shape hotspot, CategoryDataset dataset, int row, int column, boolean selected) {
/*1839*/    if (hotspot == null)
/*1840*/      throw new IllegalArgumentException("Null 'hotspot' argument."); 
/*1842*/    addEntity(entities, hotspot, dataset, row, column, selected, 0.0D, 0.0D);
/*   0*/  }
/*   0*/  
/*   0*/  protected void addEntity(EntityCollection entities, Shape hotspot, CategoryDataset dataset, int row, int column, boolean selected, double entityX, double entityY) {
/*1865*/    if (!getItemCreateEntity(row, column, selected))
/*   0*/      return; 
/*1868*/    Shape s = hotspot;
/*1869*/    if (hotspot == null) {
/*1870*/      double r = getDefaultEntityRadius();
/*1871*/      double w = r * 2.0D;
/*1872*/      if (getPlot().getOrientation() == PlotOrientation.VERTICAL) {
/*1873*/        s = new Ellipse2D.Double(entityX - r, entityY - r, w, w);
/*   0*/      } else {
/*1876*/        s = new Ellipse2D.Double(entityY - r, entityX - r, w, w);
/*   0*/      } 
/*   0*/    } 
/*1879*/    String tip = null;
/*1880*/    CategoryToolTipGenerator generator = getToolTipGenerator(row, column, selected);
/*1882*/    if (generator != null)
/*1883*/      tip = generator.generateToolTip(dataset, row, column); 
/*1885*/    String url = null;
/*1886*/    CategoryURLGenerator urlster = getURLGenerator(row, column, selected);
/*1887*/    if (urlster != null)
/*1888*/      url = urlster.generateURL(dataset, row, column); 
/*1890*/    CategoryItemEntity entity = new CategoryItemEntity(s, tip, url, dataset, dataset.getRowKey(row), dataset.getColumnKey(column));
/*1892*/    entities.add(entity);
/*   0*/  }
/*   0*/  
/*   0*/  public Shape createHotSpotShape(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, boolean selected, CategoryItemRendererState state) {
/*1916*/    throw new RuntimeException("Not implemented.");
/*   0*/  }
/*   0*/  
/*   0*/  public Rectangle2D createHotSpotBounds(Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, boolean selected, CategoryItemRendererState state, Rectangle2D result) {
/*1941*/    if (result == null)
/*1942*/      result = new Rectangle(); 
/*1944*/    Comparable key = dataset.getColumnKey(column);
/*1945*/    Number y = dataset.getValue(row, column);
/*1946*/    if (y == null)
/*1947*/      return null; 
/*1949*/    double xx = domainAxis.getCategoryMiddle(key, plot.getCategoriesForAxis(domainAxis), dataArea, plot.getDomainAxisEdge());
/*1952*/    double yy = rangeAxis.valueToJava2D(y.doubleValue(), dataArea, plot.getRangeAxisEdge());
/*1954*/    result.setRect(xx - 2.0D, yy - 2.0D, 4.0D, 4.0D);
/*1955*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean hitTest(double xx, double yy, Graphics2D g2, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset dataset, int row, int column, boolean selected, CategoryItemRendererState state) {
/*1983*/    Rectangle2D bounds = createHotSpotBounds(g2, dataArea, plot, domainAxis, rangeAxis, dataset, row, column, selected, state, null);
/*1986*/    if (bounds == null)
/*1987*/      return false; 
/*1991*/    return bounds.contains(xx, yy);
/*   0*/  }
/*   0*/}
