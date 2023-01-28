/*   0*/package org.jfree.chart.plot;
/*   0*/
/*   0*/import java.awt.AlphaComposite;
/*   0*/import java.awt.BasicStroke;
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Composite;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Collections;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.ResourceBundle;
/*   0*/import java.util.Set;
/*   0*/import org.jfree.chart.LegendItem;
/*   0*/import org.jfree.chart.LegendItemCollection;
/*   0*/import org.jfree.chart.annotations.CategoryAnnotation;
/*   0*/import org.jfree.chart.axis.Axis;
/*   0*/import org.jfree.chart.axis.AxisCollection;
/*   0*/import org.jfree.chart.axis.AxisLocation;
/*   0*/import org.jfree.chart.axis.AxisSpace;
/*   0*/import org.jfree.chart.axis.AxisState;
/*   0*/import org.jfree.chart.axis.CategoryAnchor;
/*   0*/import org.jfree.chart.axis.CategoryAxis;
/*   0*/import org.jfree.chart.axis.ValueAxis;
/*   0*/import org.jfree.chart.axis.ValueTick;
/*   0*/import org.jfree.chart.event.ChartChangeEventType;
/*   0*/import org.jfree.chart.event.PlotChangeEvent;
/*   0*/import org.jfree.chart.event.RendererChangeEvent;
/*   0*/import org.jfree.chart.event.RendererChangeListener;
/*   0*/import org.jfree.chart.renderer.category.CategoryItemRenderer;
/*   0*/import org.jfree.chart.renderer.category.CategoryItemRendererState;
/*   0*/import org.jfree.chart.util.Layer;
/*   0*/import org.jfree.chart.util.ObjectList;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.PublicCloneable;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/import org.jfree.chart.util.SortOrder;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.general.Dataset;
/*   0*/import org.jfree.data.general.DatasetChangeEvent;
/*   0*/import org.jfree.data.general.DatasetUtilities;
/*   0*/
/*   0*/public class CategoryPlot extends Plot implements ValueAxisPlot, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {
/*   0*/  private static final long serialVersionUID = -3537691700434728188L;
/*   0*/  
/*   0*/  public static final boolean DEFAULT_DOMAIN_GRIDLINES_VISIBLE = false;
/*   0*/  
/*   0*/  public static final boolean DEFAULT_RANGE_GRIDLINES_VISIBLE = true;
/*   0*/  
/* 243*/  public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5F, 0, 2, 0.0F, new float[] { 2.0F, 2.0F }, 0.0F);
/*   0*/  
/* 248*/  public static final Paint DEFAULT_GRIDLINE_PAINT = Color.WHITE;
/*   0*/  
/* 251*/  public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
/*   0*/  
/*   0*/  public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
/*   0*/  
/* 266*/  public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
/*   0*/  
/* 274*/  public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;
/*   0*/  
/* 277*/  protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
/*   0*/  
/*   0*/  private PlotOrientation orientation;
/*   0*/  
/*   0*/  private RectangleInsets axisOffset;
/*   0*/  
/*   0*/  private ObjectList domainAxes;
/*   0*/  
/*   0*/  private ObjectList domainAxisLocations;
/*   0*/  
/*   0*/  private boolean drawSharedDomainAxis;
/*   0*/  
/*   0*/  private ObjectList rangeAxes;
/*   0*/  
/*   0*/  private ObjectList rangeAxisLocations;
/*   0*/  
/*   0*/  private ObjectList datasets;
/*   0*/  
/*   0*/  private ObjectList datasetToDomainAxisMap;
/*   0*/  
/*   0*/  private ObjectList datasetToRangeAxisMap;
/*   0*/  
/*   0*/  private ObjectList renderers;
/*   0*/  
/* 317*/  private DatasetRenderingOrder renderingOrder = DatasetRenderingOrder.REVERSE;
/*   0*/  
/* 324*/  private SortOrder columnRenderingOrder = SortOrder.ASCENDING;
/*   0*/  
/* 330*/  private SortOrder rowRenderingOrder = SortOrder.ASCENDING;
/*   0*/  
/*   0*/  private boolean domainGridlinesVisible;
/*   0*/  
/*   0*/  private CategoryAnchor domainGridlinePosition;
/*   0*/  
/*   0*/  private transient Stroke domainGridlineStroke;
/*   0*/  
/*   0*/  private transient Paint domainGridlinePaint;
/*   0*/  
/*   0*/  private boolean rangeGridlinesVisible;
/*   0*/  
/*   0*/  private transient Stroke rangeGridlineStroke;
/*   0*/  
/*   0*/  private transient Paint rangeGridlinePaint;
/*   0*/  
/*   0*/  private double anchorValue;
/*   0*/  
/*   0*/  private boolean rangeCrosshairVisible;
/*   0*/  
/*   0*/  private double rangeCrosshairValue;
/*   0*/  
/*   0*/  private transient Stroke rangeCrosshairStroke;
/*   0*/  
/*   0*/  private transient Paint rangeCrosshairPaint;
/*   0*/  
/*   0*/  private boolean rangeCrosshairLockedOnData = true;
/*   0*/  
/*   0*/  private Map foregroundDomainMarkers;
/*   0*/  
/*   0*/  private Map backgroundDomainMarkers;
/*   0*/  
/*   0*/  private Map foregroundRangeMarkers;
/*   0*/  
/*   0*/  private Map backgroundRangeMarkers;
/*   0*/  
/*   0*/  private List annotations;
/*   0*/  
/*   0*/  private int weight;
/*   0*/  
/*   0*/  private AxisSpace fixedDomainAxisSpace;
/*   0*/  
/*   0*/  private AxisSpace fixedRangeAxisSpace;
/*   0*/  
/*   0*/  private LegendItemCollection fixedLegendItems;
/*   0*/  
/*   0*/  public CategoryPlot() {
/* 421*/    this(null, null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot(CategoryDataset dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
/* 440*/    this.orientation = PlotOrientation.VERTICAL;
/* 443*/    this.domainAxes = new ObjectList();
/* 444*/    this.domainAxisLocations = new ObjectList();
/* 445*/    this.rangeAxes = new ObjectList();
/* 446*/    this.rangeAxisLocations = new ObjectList();
/* 448*/    this.datasetToDomainAxisMap = new ObjectList();
/* 449*/    this.datasetToRangeAxisMap = new ObjectList();
/* 451*/    this.renderers = new ObjectList();
/* 453*/    this.datasets = new ObjectList();
/* 454*/    this.datasets.set(0, dataset);
/* 455*/    if (dataset != null)
/* 456*/      dataset.addChangeListener(this); 
/* 459*/    this.axisOffset = new RectangleInsets(4.0D, 4.0D, 4.0D, 4.0D);
/* 461*/    setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT, false);
/* 462*/    setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, false);
/* 464*/    this.renderers.set(0, renderer);
/* 465*/    if (renderer != null) {
/* 466*/      renderer.setPlot(this);
/* 467*/      renderer.addChangeListener(this);
/*   0*/    } 
/* 470*/    this.domainAxes.set(0, domainAxis);
/* 471*/    mapDatasetToDomainAxis(0, 0);
/* 472*/    if (domainAxis != null) {
/* 473*/      domainAxis.setPlot(this);
/* 474*/      domainAxis.addChangeListener(this);
/*   0*/    } 
/* 476*/    this.drawSharedDomainAxis = false;
/* 478*/    this.rangeAxes.set(0, rangeAxis);
/* 479*/    mapDatasetToRangeAxis(0, 0);
/* 480*/    if (rangeAxis != null) {
/* 481*/      rangeAxis.setPlot(this);
/* 482*/      rangeAxis.addChangeListener(this);
/*   0*/    } 
/* 485*/    configureDomainAxes();
/* 486*/    configureRangeAxes();
/* 488*/    this.domainGridlinesVisible = false;
/* 489*/    this.domainGridlinePosition = CategoryAnchor.MIDDLE;
/* 490*/    this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 491*/    this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 493*/    this.rangeGridlinesVisible = true;
/* 494*/    this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 495*/    this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 497*/    this.foregroundDomainMarkers = new HashMap();
/* 498*/    this.backgroundDomainMarkers = new HashMap();
/* 499*/    this.foregroundRangeMarkers = new HashMap();
/* 500*/    this.backgroundRangeMarkers = new HashMap();
/* 502*/    Marker baseline = new ValueMarker(0.0D, new Color(0.8F, 0.8F, 0.8F, 0.5F), new BasicStroke(1.0F), new Color(0.85F, 0.85F, 0.95F, 0.5F), new BasicStroke(1.0F), 0.6F);
/* 505*/    addRangeMarker(baseline, Layer.BACKGROUND);
/* 507*/    this.anchorValue = 0.0D;
/* 509*/    this.rangeCrosshairVisible = false;
/* 510*/    this.rangeCrosshairValue = 0.0D;
/* 511*/    this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
/* 512*/    this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
/* 514*/    this.annotations = new ArrayList();
/*   0*/  }
/*   0*/  
/*   0*/  public String getPlotType() {
/* 524*/    return localizationResources.getString("Category_Plot");
/*   0*/  }
/*   0*/  
/*   0*/  public PlotOrientation getOrientation() {
/* 535*/    return this.orientation;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOrientation(PlotOrientation orientation) {
/* 547*/    if (orientation == null)
/* 548*/      throw new IllegalArgumentException("Null 'orientation' argument."); 
/* 550*/    this.orientation = orientation;
/* 551*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getAxisOffset() {
/* 562*/    return this.axisOffset;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisOffset(RectangleInsets offset) {
/* 574*/    if (offset == null)
/* 575*/      throw new IllegalArgumentException("Null 'offset' argument."); 
/* 577*/    this.axisOffset = offset;
/* 578*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxis() {
/* 591*/    return getDomainAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxis(int index) {
/* 604*/    CategoryAxis result = null;
/* 605*/    if (index < this.domainAxes.size())
/* 606*/      result = (CategoryAxis)this.domainAxes.get(index); 
/* 608*/    if (result == null) {
/* 609*/      Plot parent = getParent();
/* 610*/      if (parent instanceof CategoryPlot) {
/* 611*/        CategoryPlot cp = (CategoryPlot)parent;
/* 612*/        result = cp.getDomainAxis(index);
/*   0*/      } 
/*   0*/    } 
/* 615*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(CategoryAxis axis) {
/* 627*/    setDomainAxis(0, axis);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, CategoryAxis axis) {
/* 640*/    setDomainAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
/* 652*/    CategoryAxis existing = (CategoryAxis)this.domainAxes.get(index);
/* 653*/    if (existing != null)
/* 654*/      existing.removeChangeListener(this); 
/* 656*/    if (axis != null)
/* 657*/      axis.setPlot(this); 
/* 659*/    this.domainAxes.set(index, axis);
/* 660*/    if (axis != null) {
/* 661*/      axis.configure();
/* 662*/      axis.addChangeListener(this);
/*   0*/    } 
/* 664*/    if (notify)
/* 665*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxes(CategoryAxis[] axes) {
/* 678*/    for (int i = 0; i < axes.length; i++)
/* 679*/      setDomainAxis(i, axes[i], false); 
/* 681*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisIndex(CategoryAxis axis) {
/* 695*/    return this.domainAxes.indexOf(axis);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation() {
/* 706*/    return getDomainAxisLocation(0);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation(int index) {
/* 719*/    AxisLocation result = null;
/* 720*/    if (index < this.domainAxisLocations.size())
/* 721*/      result = (AxisLocation)this.domainAxisLocations.get(index); 
/* 723*/    if (result == null)
/* 724*/      result = AxisLocation.getOpposite(getDomainAxisLocation(0)); 
/* 726*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location) {
/* 740*/    setDomainAxisLocation(0, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location, boolean notify) {
/* 752*/    setDomainAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location) {
/* 767*/    setDomainAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
/* 785*/    if (index == 0 && location == null)
/* 786*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/* 789*/    this.domainAxisLocations.set(index, location);
/* 790*/    if (notify)
/* 791*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge() {
/* 802*/    return getDomainAxisEdge(0);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge(int index) {
/* 813*/    RectangleEdge result = null;
/* 814*/    AxisLocation location = getDomainAxisLocation(index);
/* 815*/    if (location != null) {
/* 816*/      result = Plot.resolveDomainAxisLocation(location, this.orientation);
/*   0*/    } else {
/* 819*/      result = RectangleEdge.opposite(getDomainAxisEdge(0));
/*   0*/    } 
/* 821*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisCount() {
/* 830*/    return this.domainAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainAxes() {
/* 838*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 839*/      CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
/* 840*/      if (axis != null)
/* 841*/        axis.removeChangeListener(this); 
/*   0*/    } 
/* 844*/    this.domainAxes.clear();
/* 845*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void configureDomainAxes() {
/* 852*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 853*/      CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
/* 854*/      if (axis != null)
/* 855*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis() {
/* 868*/    return getRangeAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis(int index) {
/* 879*/    ValueAxis result = null;
/* 880*/    if (index < this.rangeAxes.size())
/* 881*/      result = (ValueAxis)this.rangeAxes.get(index); 
/* 883*/    if (result == null) {
/* 884*/      Plot parent = getParent();
/* 885*/      if (parent instanceof CategoryPlot) {
/* 886*/        CategoryPlot cp = (CategoryPlot)parent;
/* 887*/        result = cp.getRangeAxis(index);
/*   0*/      } 
/*   0*/    } 
/* 890*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(ValueAxis axis) {
/* 900*/    setRangeAxis(0, axis);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis) {
/* 911*/    setRangeAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
/* 923*/    ValueAxis existing = (ValueAxis)this.rangeAxes.get(index);
/* 924*/    if (existing != null)
/* 925*/      existing.removeChangeListener(this); 
/* 927*/    if (axis != null)
/* 928*/      axis.setPlot(this); 
/* 930*/    this.rangeAxes.set(index, axis);
/* 931*/    if (axis != null) {
/* 932*/      axis.configure();
/* 933*/      axis.addChangeListener(this);
/*   0*/    } 
/* 935*/    if (notify)
/* 936*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxes(ValueAxis[] axes) {
/* 949*/    for (int i = 0; i < axes.length; i++)
/* 950*/      setRangeAxis(i, axes[i], false); 
/* 952*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public int getRangeAxisIndex(ValueAxis axis) {
/* 968*/    int result = this.rangeAxes.indexOf(axis);
/* 969*/    if (result < 0) {
/* 970*/      Plot parent = getParent();
/* 971*/      if (parent instanceof CategoryPlot) {
/* 972*/        CategoryPlot p = (CategoryPlot)parent;
/* 973*/        result = p.getRangeAxisIndex(axis);
/*   0*/      } 
/*   0*/    } 
/* 976*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation() {
/* 985*/    return getRangeAxisLocation(0);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation(int index) {
/* 998*/    AxisLocation result = null;
/* 999*/    if (index < this.rangeAxisLocations.size())
/*1000*/      result = (AxisLocation)this.rangeAxisLocations.get(index); 
/*1002*/    if (result == null)
/*1003*/      result = AxisLocation.getOpposite(getRangeAxisLocation(0)); 
/*1005*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location) {
/*1019*/    setRangeAxisLocation(location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location, boolean notify) {
/*1032*/    setRangeAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location) {
/*1046*/    setRangeAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
/*1062*/    if (index == 0 && location == null)
/*1063*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/*1066*/    this.rangeAxisLocations.set(index, location);
/*1067*/    if (notify)
/*1068*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge() {
/*1078*/    return getRangeAxisEdge(0);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge(int index) {
/*1089*/    AxisLocation location = getRangeAxisLocation(index);
/*1090*/    RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
/*1092*/    if (result == null)
/*1093*/      result = RectangleEdge.opposite(getRangeAxisEdge(0)); 
/*1095*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getRangeAxisCount() {
/*1104*/    return this.rangeAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeAxes() {
/*1112*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1113*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1114*/      if (axis != null)
/*1115*/        axis.removeChangeListener(this); 
/*   0*/    } 
/*1118*/    this.rangeAxes.clear();
/*1119*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void configureRangeAxes() {
/*1126*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1127*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1128*/      if (axis != null)
/*1129*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryDataset getDataset() {
/*1142*/    return getDataset(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryDataset getDataset(int index) {
/*1155*/    CategoryDataset result = null;
/*1156*/    if (this.datasets.size() > index)
/*1157*/      result = (CategoryDataset)this.datasets.get(index); 
/*1159*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(CategoryDataset dataset) {
/*1174*/    setDataset(0, dataset);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(int index, CategoryDataset dataset) {
/*1187*/    CategoryDataset existing = (CategoryDataset)this.datasets.get(index);
/*1188*/    if (existing != null)
/*1189*/      existing.removeChangeListener(this); 
/*1191*/    this.datasets.set(index, dataset);
/*1192*/    if (dataset != null)
/*1193*/      dataset.addChangeListener(this); 
/*1197*/    DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
/*1198*/    datasetChanged(event);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDatasetCount() {
/*1210*/    return this.datasets.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToDomainAxis(int index, int axisIndex) {
/*1222*/    this.datasetToDomainAxisMap.set(index, new Integer(axisIndex));
/*1224*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxisForDataset(int index) {
/*1238*/    CategoryAxis result = getDomainAxis();
/*1239*/    Integer axisIndex = (Integer)this.datasetToDomainAxisMap.get(index);
/*1240*/    if (axisIndex != null)
/*1241*/      result = getDomainAxis(axisIndex); 
/*1243*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToRangeAxis(int index, int axisIndex) {
/*1255*/    this.datasetToRangeAxisMap.set(index, new Integer(axisIndex));
/*1257*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxisForDataset(int index) {
/*1271*/    ValueAxis result = getRangeAxis();
/*1272*/    Integer axisIndex = (Integer)this.datasetToRangeAxisMap.get(index);
/*1273*/    if (axisIndex != null)
/*1274*/      result = getRangeAxis(axisIndex); 
/*1276*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRenderer() {
/*1287*/    return getRenderer(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRenderer(int index) {
/*1300*/    CategoryItemRenderer result = null;
/*1301*/    if (this.renderers.size() > index)
/*1302*/      result = (CategoryItemRenderer)this.renderers.get(index); 
/*1304*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(CategoryItemRenderer renderer) {
/*1317*/    setRenderer(0, renderer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(CategoryItemRenderer renderer, boolean notify) {
/*1338*/    setRenderer(0, renderer, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, CategoryItemRenderer renderer) {
/*1352*/    setRenderer(index, renderer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, CategoryItemRenderer renderer, boolean notify) {
/*1369*/    CategoryItemRenderer existing = (CategoryItemRenderer)this.renderers.get(index);
/*1371*/    if (existing != null)
/*1372*/      existing.removeChangeListener(this); 
/*1376*/    this.renderers.set(index, renderer);
/*1377*/    if (renderer != null) {
/*1378*/      renderer.setPlot(this);
/*1379*/      renderer.addChangeListener(this);
/*   0*/    } 
/*1382*/    configureDomainAxes();
/*1383*/    configureRangeAxes();
/*1385*/    if (notify)
/*1386*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderers(CategoryItemRenderer[] renderers) {
/*1397*/    for (int i = 0; i < renderers.length; i++)
/*1398*/      setRenderer(i, renderers[i], false); 
/*1400*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRendererForDataset(CategoryDataset dataset) {
/*1412*/    CategoryItemRenderer result = null;
/*1413*/    for (int i = 0; i < this.datasets.size(); i++) {
/*1414*/      if (this.datasets.get(i) == dataset) {
/*1415*/        result = (CategoryItemRenderer)this.renderers.get(i);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1419*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOf(CategoryItemRenderer renderer) {
/*1431*/    return this.renderers.indexOf(renderer);
/*   0*/  }
/*   0*/  
/*   0*/  public DatasetRenderingOrder getDatasetRenderingOrder() {
/*1442*/    return this.renderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
/*1456*/    if (order == null)
/*1457*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1459*/    this.renderingOrder = order;
/*1460*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public SortOrder getColumnRenderingOrder() {
/*1472*/    return this.columnRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setColumnRenderingOrder(SortOrder order) {
/*1487*/    if (order == null)
/*1488*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1490*/    this.columnRenderingOrder = order;
/*1491*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public SortOrder getRowRenderingOrder() {
/*1503*/    return this.rowRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRowRenderingOrder(SortOrder order) {
/*1518*/    if (order == null)
/*1519*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1521*/    this.rowRenderingOrder = order;
/*1522*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainGridlinesVisible() {
/*1533*/    return this.domainGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinesVisible(boolean visible) {
/*1548*/    if (this.domainGridlinesVisible != visible) {
/*1549*/      this.domainGridlinesVisible = visible;
/*1550*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAnchor getDomainGridlinePosition() {
/*1562*/    return this.domainGridlinePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinePosition(CategoryAnchor position) {
/*1574*/    if (position == null)
/*1575*/      throw new IllegalArgumentException("Null 'position' argument."); 
/*1577*/    this.domainGridlinePosition = position;
/*1578*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainGridlineStroke() {
/*1589*/    return this.domainGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlineStroke(Stroke stroke) {
/*1601*/    if (stroke == null)
/*1602*/      throw new IllegalArgumentException("Null 'stroke' not permitted."); 
/*1604*/    this.domainGridlineStroke = stroke;
/*1605*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainGridlinePaint() {
/*1616*/    return this.domainGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinePaint(Paint paint) {
/*1628*/    if (paint == null)
/*1629*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1631*/    this.domainGridlinePaint = paint;
/*1632*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeGridlinesVisible() {
/*1643*/    return this.rangeGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinesVisible(boolean visible) {
/*1656*/    if (this.rangeGridlinesVisible != visible) {
/*1657*/      this.rangeGridlinesVisible = visible;
/*1658*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeGridlineStroke() {
/*1670*/    return this.rangeGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlineStroke(Stroke stroke) {
/*1682*/    if (stroke == null)
/*1683*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*1685*/    this.rangeGridlineStroke = stroke;
/*1686*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeGridlinePaint() {
/*1697*/    return this.rangeGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinePaint(Paint paint) {
/*1709*/    if (paint == null)
/*1710*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1712*/    this.rangeGridlinePaint = paint;
/*1713*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getFixedLegendItems() {
/*1724*/    return this.fixedLegendItems;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedLegendItems(LegendItemCollection items) {
/*1737*/    this.fixedLegendItems = items;
/*1738*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/*1749*/    LegendItemCollection result = this.fixedLegendItems;
/*1750*/    if (result == null) {
/*1751*/      result = new LegendItemCollection();
/*1753*/      int count = this.datasets.size();
/*1754*/      for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
/*1755*/        CategoryDataset dataset = getDataset(datasetIndex);
/*1756*/        if (dataset != null) {
/*1757*/          CategoryItemRenderer renderer = getRenderer(datasetIndex);
/*1758*/          if (renderer != null) {
/*1759*/            int seriesCount = dataset.getRowCount();
/*1760*/            for (int i = 0; i < seriesCount; i++) {
/*1761*/              LegendItem item = renderer.getLegendItem(datasetIndex, i);
/*1763*/              if (item != null)
/*1764*/                result.add(item); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1771*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void handleClick(int x, int y, PlotRenderingInfo info) {
/*1784*/    Rectangle2D dataArea = info.getDataArea();
/*1785*/    if (dataArea.contains(x, y)) {
/*1787*/      double java2D = 0.0D;
/*1788*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*1789*/        java2D = x;
/*1791*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*1792*/        java2D = y;
/*   0*/      } 
/*1794*/      RectangleEdge edge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
/*1796*/      double value = getRangeAxis().java2DToValue(java2D, info.getDataArea(), edge);
/*1798*/      setAnchorValue(value);
/*1799*/      setRangeCrosshairValue(value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoom(double percent) {
/*1815*/    if (percent > 0.0D) {
/*1816*/      double range = getRangeAxis().getRange().getLength();
/*1817*/      double scaledRange = range * percent;
/*1818*/      getRangeAxis().setRange(this.anchorValue - scaledRange / 2.0D, this.anchorValue + scaledRange / 2.0D);
/*   0*/    } else {
/*1822*/      getRangeAxis().setAutoRange(true);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void datasetChanged(DatasetChangeEvent event) {
/*1836*/    int count = this.rangeAxes.size();
/*1837*/    for (int axisIndex = 0; axisIndex < count; axisIndex++) {
/*1838*/      ValueAxis yAxis = getRangeAxis(axisIndex);
/*1839*/      if (yAxis != null)
/*1840*/        yAxis.configure(); 
/*   0*/    } 
/*1843*/    if (getParent() != null) {
/*1844*/      getParent().datasetChanged(event);
/*   0*/    } else {
/*1847*/      PlotChangeEvent e = new PlotChangeEvent(this);
/*1848*/      e.setType(ChartChangeEventType.DATASET_UPDATED);
/*1849*/      notifyListeners(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rendererChanged(RendererChangeEvent event) {
/*1860*/    Plot parent = getParent();
/*1861*/    if (parent != null) {
/*1862*/      if (parent instanceof RendererChangeListener) {
/*1863*/        RendererChangeListener rcl = (RendererChangeListener)parent;
/*1864*/        rcl.rendererChanged(event);
/*   0*/      } else {
/*1869*/        throw new RuntimeException("The renderer has changed and I don't know what to do!");
/*   0*/      } 
/*   0*/    } else {
/*1874*/      configureRangeAxes();
/*1875*/      PlotChangeEvent e = new PlotChangeEvent(this);
/*1876*/      notifyListeners(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(CategoryMarker marker) {
/*1889*/    addDomainMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(CategoryMarker marker, Layer layer) {
/*1903*/    addDomainMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(int index, CategoryMarker marker, Layer layer) {
/*1917*/    if (marker == null)
/*1918*/      throw new IllegalArgumentException("Null 'marker' not permitted."); 
/*1920*/    if (layer == null)
/*1921*/      throw new IllegalArgumentException("Null 'layer' not permitted."); 
/*1924*/    if (layer == Layer.FOREGROUND) {
/*1925*/      Collection<CategoryMarker> markers = (Collection)this.foregroundDomainMarkers.get(new Integer(index));
/*1927*/      if (markers == null) {
/*1928*/        markers = new ArrayList();
/*1929*/        this.foregroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*1931*/      markers.add(marker);
/*1933*/    } else if (layer == Layer.BACKGROUND) {
/*1934*/      Collection<CategoryMarker> markers = (Collection)this.backgroundDomainMarkers.get(new Integer(index));
/*1936*/      if (markers == null) {
/*1937*/        markers = new ArrayList();
/*1938*/        this.backgroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*1940*/      markers.add(marker);
/*   0*/    } 
/*1942*/    marker.addChangeListener(this);
/*1943*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers() {
/*1953*/    if (this.backgroundDomainMarkers != null) {
/*1954*/      Set keys = this.backgroundDomainMarkers.keySet();
/*1955*/      Iterator<Integer> iterator = keys.iterator();
/*1956*/      while (iterator.hasNext()) {
/*1957*/        Integer key = iterator.next();
/*1958*/        clearDomainMarkers(key);
/*   0*/      } 
/*1960*/      this.backgroundDomainMarkers.clear();
/*   0*/    } 
/*1962*/    if (this.foregroundDomainMarkers != null) {
/*1963*/      Set keys = this.foregroundDomainMarkers.keySet();
/*1964*/      Iterator<Integer> iterator = keys.iterator();
/*1965*/      while (iterator.hasNext()) {
/*1966*/        Integer key = iterator.next();
/*1967*/        clearDomainMarkers(key);
/*   0*/      } 
/*1969*/      this.foregroundDomainMarkers.clear();
/*   0*/    } 
/*1971*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(Layer layer) {
/*1982*/    return getDomainMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(int index, Layer layer) {
/*1995*/    Collection<?> result = null;
/*1996*/    Integer key = new Integer(index);
/*1997*/    if (layer == Layer.FOREGROUND) {
/*1998*/      result = (Collection)this.foregroundDomainMarkers.get(key);
/*2000*/    } else if (layer == Layer.BACKGROUND) {
/*2001*/      result = (Collection)this.backgroundDomainMarkers.get(key);
/*   0*/    } 
/*2003*/    if (result != null)
/*2004*/      result = Collections.unmodifiableCollection(result); 
/*2006*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers(int index) {
/*2017*/    Integer key = new Integer(index);
/*2018*/    if (this.backgroundDomainMarkers != null) {
/*2019*/      Collection markers = (Collection)this.backgroundDomainMarkers.get(key);
/*2021*/      if (markers != null) {
/*2022*/        Iterator<Marker> iterator = markers.iterator();
/*2023*/        while (iterator.hasNext()) {
/*2024*/          Marker m = iterator.next();
/*2025*/          m.removeChangeListener(this);
/*   0*/        } 
/*2027*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2030*/    if (this.foregroundDomainMarkers != null) {
/*2031*/      Collection markers = (Collection)this.foregroundDomainMarkers.get(key);
/*2033*/      if (markers != null) {
/*2034*/        Iterator<Marker> iterator = markers.iterator();
/*2035*/        while (iterator.hasNext()) {
/*2036*/          Marker m = iterator.next();
/*2037*/          m.removeChangeListener(this);
/*   0*/        } 
/*2039*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2042*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker) {
/*2054*/    addRangeMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker, Layer layer) {
/*2068*/    addRangeMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(int index, Marker marker, Layer layer) {
/*2083*/    if (layer == Layer.FOREGROUND) {
/*2084*/      Collection<Marker> markers = (Collection)this.foregroundRangeMarkers.get(new Integer(index));
/*2086*/      if (markers == null) {
/*2087*/        markers = new ArrayList();
/*2088*/        this.foregroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2090*/      markers.add(marker);
/*2092*/    } else if (layer == Layer.BACKGROUND) {
/*2093*/      Collection<Marker> markers = (Collection)this.backgroundRangeMarkers.get(new Integer(index));
/*2095*/      if (markers == null) {
/*2096*/        markers = new ArrayList();
/*2097*/        this.backgroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2099*/      markers.add(marker);
/*   0*/    } 
/*2101*/    marker.addChangeListener(this);
/*2102*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers() {
/*2112*/    if (this.backgroundRangeMarkers != null) {
/*2113*/      Set keys = this.backgroundRangeMarkers.keySet();
/*2114*/      Iterator<Integer> iterator = keys.iterator();
/*2115*/      while (iterator.hasNext()) {
/*2116*/        Integer key = iterator.next();
/*2117*/        clearRangeMarkers(key);
/*   0*/      } 
/*2119*/      this.backgroundRangeMarkers.clear();
/*   0*/    } 
/*2121*/    if (this.foregroundRangeMarkers != null) {
/*2122*/      Set keys = this.foregroundRangeMarkers.keySet();
/*2123*/      Iterator<Integer> iterator = keys.iterator();
/*2124*/      while (iterator.hasNext()) {
/*2125*/        Integer key = iterator.next();
/*2126*/        clearRangeMarkers(key);
/*   0*/      } 
/*2128*/      this.foregroundRangeMarkers.clear();
/*   0*/    } 
/*2130*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(Layer layer) {
/*2143*/    return getRangeMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(int index, Layer layer) {
/*2156*/    Collection<?> result = null;
/*2157*/    Integer key = new Integer(index);
/*2158*/    if (layer == Layer.FOREGROUND) {
/*2159*/      result = (Collection)this.foregroundRangeMarkers.get(key);
/*2161*/    } else if (layer == Layer.BACKGROUND) {
/*2162*/      result = (Collection)this.backgroundRangeMarkers.get(key);
/*   0*/    } 
/*2164*/    if (result != null)
/*2165*/      result = Collections.unmodifiableCollection(result); 
/*2167*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers(int index) {
/*2178*/    Integer key = new Integer(index);
/*2179*/    if (this.backgroundRangeMarkers != null) {
/*2180*/      Collection markers = (Collection)this.backgroundRangeMarkers.get(key);
/*2182*/      if (markers != null) {
/*2183*/        Iterator<Marker> iterator = markers.iterator();
/*2184*/        while (iterator.hasNext()) {
/*2185*/          Marker m = iterator.next();
/*2186*/          m.removeChangeListener(this);
/*   0*/        } 
/*2188*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2191*/    if (this.foregroundRangeMarkers != null) {
/*2192*/      Collection markers = (Collection)this.foregroundRangeMarkers.get(key);
/*2194*/      if (markers != null) {
/*2195*/        Iterator<Marker> iterator = markers.iterator();
/*2196*/        while (iterator.hasNext()) {
/*2197*/          Marker m = iterator.next();
/*2198*/          m.removeChangeListener(this);
/*   0*/        } 
/*2200*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2203*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairVisible() {
/*2214*/    return this.rangeCrosshairVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairVisible(boolean flag) {
/*2225*/    if (this.rangeCrosshairVisible != flag) {
/*2226*/      this.rangeCrosshairVisible = flag;
/*2227*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairLockedOnData() {
/*2240*/    return this.rangeCrosshairLockedOnData;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairLockedOnData(boolean flag) {
/*2253*/    if (this.rangeCrosshairLockedOnData != flag) {
/*2254*/      this.rangeCrosshairLockedOnData = flag;
/*2255*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getRangeCrosshairValue() {
/*2268*/    return this.rangeCrosshairValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value) {
/*2282*/    setRangeCrosshairValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value, boolean notify) {
/*2297*/    this.rangeCrosshairValue = value;
/*2298*/    if (isRangeCrosshairVisible() && notify)
/*2299*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeCrosshairStroke() {
/*2314*/    return this.rangeCrosshairStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairStroke(Stroke stroke) {
/*2328*/    if (stroke == null)
/*2329*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2331*/    this.rangeCrosshairStroke = stroke;
/*2332*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeCrosshairPaint() {
/*2345*/    return this.rangeCrosshairPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairPaint(Paint paint) {
/*2357*/    if (paint == null)
/*2358*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2360*/    this.rangeCrosshairPaint = paint;
/*2361*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public List getAnnotations() {
/*2370*/    return this.annotations;
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation) {
/*2382*/    if (annotation == null)
/*2383*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2385*/    this.annotations.add(annotation);
/*2386*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(CategoryAnnotation annotation) {
/*2400*/    if (annotation == null)
/*2401*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2403*/    boolean removed = this.annotations.remove(annotation);
/*2404*/    if (removed)
/*2405*/      notifyListeners(new PlotChangeEvent(this)); 
/*2407*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearAnnotations() {
/*2415*/    this.annotations.clear();
/*2416*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*2432*/    if (space == null)
/*2433*/      space = new AxisSpace(); 
/*2437*/    if (this.fixedDomainAxisSpace != null) {
/*2438*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*2439*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
/*2441*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
/*2444*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*2445*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
/*2447*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*   0*/      } 
/*   0*/    } else {
/*2453*/      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
/*2455*/      if (this.drawSharedDomainAxis)
/*2456*/        space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space); 
/*2461*/      for (int i = 0; i < this.domainAxes.size(); i++) {
/*2462*/        Axis xAxis = (Axis)this.domainAxes.get(i);
/*2463*/        if (xAxis != null) {
/*2464*/          RectangleEdge edge = getDomainAxisEdge(i);
/*2465*/          space = xAxis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2470*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*2487*/    if (space == null)
/*2488*/      space = new AxisSpace(); 
/*2492*/    if (this.fixedRangeAxisSpace != null) {
/*2493*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*2494*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
/*2496*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*2499*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*2500*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
/*2502*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
/*   0*/      } 
/*   0*/    } else {
/*2508*/      for (int i = 0; i < this.rangeAxes.size(); i++) {
/*2509*/        Axis yAxis = (Axis)this.rangeAxes.get(i);
/*2510*/        if (yAxis != null) {
/*2511*/          RectangleEdge edge = getRangeAxisEdge(i);
/*2512*/          space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2516*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
/*2530*/    AxisSpace space = new AxisSpace();
/*2531*/    space = calculateRangeAxisSpace(g2, plotArea, space);
/*2532*/    space = calculateDomainAxisSpace(g2, plotArea, space);
/*2533*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo state) {
/*2558*/    boolean b1 = (area.getWidth() <= 10.0D);
/*2559*/    boolean b2 = (area.getHeight() <= 10.0D);
/*2560*/    if (b1 || b2)
/*   0*/      return; 
/*2565*/    if (state == null)
/*2569*/      state = new PlotRenderingInfo(null); 
/*2571*/    state.setPlotArea(area);
/*2574*/    RectangleInsets insets = getInsets();
/*2575*/    insets.trim(area);
/*2578*/    AxisSpace space = calculateAxisSpace(g2, area);
/*2579*/    Rectangle2D dataArea = space.shrink(area, null);
/*2580*/    this.axisOffset.trim(dataArea);
/*2582*/    state.setDataArea(dataArea);
/*2586*/    if (getRenderer() != null) {
/*2587*/      getRenderer().drawBackground(g2, this, dataArea);
/*   0*/    } else {
/*2590*/      drawBackground(g2, dataArea);
/*   0*/    } 
/*2593*/    Map axisStateMap = drawAxes(g2, area, dataArea, state);
/*2596*/    Shape savedClip = g2.getClip();
/*2597*/    g2.clip(dataArea);
/*2599*/    drawDomainGridlines(g2, dataArea);
/*2601*/    AxisState rangeAxisState = (AxisState)axisStateMap.get(getRangeAxis());
/*2602*/    if (rangeAxisState == null && 
/*2603*/      parentState != null)
/*2604*/      rangeAxisState = (AxisState)parentState.getSharedAxisStates().get(getRangeAxis()); 
/*2608*/    if (rangeAxisState != null)
/*2609*/      drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks()); 
/*2613*/    for (int i = 0; i < this.renderers.size(); i++)
/*2614*/      drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND); 
/*2616*/    for (int j = 0; j < this.renderers.size(); j++)
/*2617*/      drawRangeMarkers(g2, dataArea, j, Layer.BACKGROUND); 
/*   0*/    boolean foundData = false;
/*2624*/    Composite originalComposite = g2.getComposite();
/*2625*/    g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
/*2628*/    DatasetRenderingOrder order = getDatasetRenderingOrder();
/*2629*/    if (order == DatasetRenderingOrder.FORWARD) {
/*2632*/      int datasetCount = this.datasets.size();
/*2633*/      for (int i2 = 0; i2 < datasetCount; i2++) {
/*2634*/        CategoryItemRenderer r = getRenderer(i2);
/*2635*/        if (r != null) {
/*2636*/          CategoryAxis domainAxis = getDomainAxisForDataset(i2);
/*2637*/          ValueAxis rangeAxis = getRangeAxisForDataset(i2);
/*2638*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, state);
/*   0*/        } 
/*   0*/      } 
/*2643*/      for (int i1 = 0; i1 < datasetCount; i1++)
/*2644*/        foundData = (render(g2, dataArea, i1, state) || foundData); 
/*2648*/      for (int n = 0; n < datasetCount; n++) {
/*2649*/        CategoryItemRenderer r = getRenderer(n);
/*2650*/        if (r != null) {
/*2651*/          CategoryAxis domainAxis = getDomainAxisForDataset(n);
/*2652*/          ValueAxis rangeAxis = getRangeAxisForDataset(n);
/*2653*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, state);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } else {
/*2661*/      int datasetCount = this.datasets.size();
/*2662*/      for (int i2 = datasetCount - 1; i2 >= 0; i2--) {
/*2663*/        CategoryItemRenderer r = getRenderer(i2);
/*2664*/        if (r != null) {
/*2665*/          CategoryAxis domainAxis = getDomainAxisForDataset(i2);
/*2666*/          ValueAxis rangeAxis = getRangeAxisForDataset(i2);
/*2667*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, state);
/*   0*/        } 
/*   0*/      } 
/*2672*/      for (int i1 = this.datasets.size() - 1; i1 >= 0; i1--)
/*2673*/        foundData = (render(g2, dataArea, i1, state) || foundData); 
/*2677*/      for (int n = datasetCount - 1; n >= 0; n--) {
/*2678*/        CategoryItemRenderer r = getRenderer(n);
/*2679*/        if (r != null) {
/*2680*/          CategoryAxis domainAxis = getDomainAxisForDataset(n);
/*2681*/          ValueAxis rangeAxis = getRangeAxisForDataset(n);
/*2682*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, state);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2689*/    for (int m = 0; m < this.renderers.size(); m++)
/*2690*/      drawDomainMarkers(g2, dataArea, m, Layer.FOREGROUND); 
/*2692*/    for (int k = 0; k < this.renderers.size(); k++)
/*2693*/      drawRangeMarkers(g2, dataArea, k, Layer.FOREGROUND); 
/*2697*/    drawAnnotations(g2, dataArea, state);
/*2699*/    g2.setClip(savedClip);
/*2700*/    g2.setComposite(originalComposite);
/*2702*/    if (!foundData)
/*2703*/      drawNoDataMessage(g2, dataArea); 
/*2707*/    if (isRangeCrosshairVisible())
/*2709*/      drawRangeCrosshair(g2, dataArea, getOrientation(), getRangeCrosshairValue(), getRangeAxis(), getRangeCrosshairStroke(), getRangeCrosshairPaint()); 
/*2715*/    if (getRenderer() != null) {
/*2716*/      getRenderer().drawOutline(g2, this, dataArea);
/*   0*/    } else {
/*2719*/      drawOutline(g2, dataArea);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawBackground(Graphics2D g2, Rectangle2D area) {
/*2735*/    fillBackground(g2, area, this.orientation);
/*2736*/    drawBackgroundImage(g2, area);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
/*2755*/    AxisCollection axisCollection = new AxisCollection();
/*2758*/    for (int index = 0; index < this.domainAxes.size(); index++) {
/*2759*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(index);
/*2760*/      if (xAxis != null)
/*2761*/        axisCollection.add(xAxis, getDomainAxisEdge(index)); 
/*   0*/    } 
/*2766*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*2767*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(i);
/*2768*/      if (yAxis != null)
/*2769*/        axisCollection.add(yAxis, getRangeAxisEdge(i)); 
/*   0*/    } 
/*2773*/    Map<Axis, AxisState> axisStateMap = new HashMap();
/*2776*/    double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
/*2778*/    Iterator<Axis> iterator = axisCollection.getAxesAtTop().iterator();
/*2779*/    while (iterator.hasNext()) {
/*2780*/      Axis axis = iterator.next();
/*2781*/      if (axis != null) {
/*2782*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
/*2784*/        cursor = axisState.getCursor();
/*2785*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2790*/    cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
/*2792*/    iterator = axisCollection.getAxesAtBottom().iterator();
/*2793*/    while (iterator.hasNext()) {
/*2794*/      Axis axis = iterator.next();
/*2795*/      if (axis != null) {
/*2796*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
/*2798*/        cursor = axisState.getCursor();
/*2799*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2804*/    cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
/*2806*/    iterator = axisCollection.getAxesAtLeft().iterator();
/*2807*/    while (iterator.hasNext()) {
/*2808*/      Axis axis = iterator.next();
/*2809*/      if (axis != null) {
/*2810*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
/*2812*/        cursor = axisState.getCursor();
/*2813*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2818*/    cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
/*2820*/    iterator = axisCollection.getAxesAtRight().iterator();
/*2821*/    while (iterator.hasNext()) {
/*2822*/      Axis axis = iterator.next();
/*2823*/      if (axis != null) {
/*2824*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
/*2826*/        cursor = axisState.getCursor();
/*2827*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2831*/    return axisStateMap;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info) {
/*   0*/    boolean foundData = false;
/*2850*/    CategoryDataset currentDataset = getDataset(index);
/*2851*/    CategoryItemRenderer renderer = getRenderer(index);
/*2852*/    CategoryAxis domainAxis = getDomainAxisForDataset(index);
/*2853*/    ValueAxis rangeAxis = getRangeAxisForDataset(index);
/*2854*/    boolean hasData = !DatasetUtilities.isEmptyOrNull(currentDataset);
/*2855*/    if (hasData && renderer != null) {
/*2857*/      foundData = true;
/*2858*/      CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
/*2860*/      int columnCount = currentDataset.getColumnCount();
/*2861*/      int rowCount = currentDataset.getRowCount();
/*2862*/      int passCount = renderer.getPassCount();
/*2863*/      for (int pass = 0; pass < pass; pass++) {
/*2864*/        if (this.columnRenderingOrder == SortOrder.ASCENDING) {
/*2865*/          for (int column = 0; column < columnCount; column++) {
/*2866*/            if (this.rowRenderingOrder == SortOrder.ASCENDING) {
/*2867*/              for (int row = 0; row < rowCount; row++)
/*2868*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } else {
/*2874*/              for (int row = rowCount - 1; row >= 0; row--)
/*2875*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/*2883*/          for (int column = columnCount - 1; column >= 0; column--) {
/*2884*/            if (this.rowRenderingOrder == SortOrder.ASCENDING) {
/*2885*/              for (int row = 0; row < rowCount; row++)
/*2886*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } else {
/*2892*/              for (int row = rowCount - 1; row >= 0; row--)
/*2893*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2902*/    return foundData;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea) {
/*2917*/    if (isDomainGridlinesVisible()) {
/*2918*/      CategoryAnchor anchor = getDomainGridlinePosition();
/*2919*/      RectangleEdge domainAxisEdge = getDomainAxisEdge();
/*2920*/      Stroke gridStroke = getDomainGridlineStroke();
/*2921*/      Paint gridPaint = getDomainGridlinePaint();
/*2922*/      if (gridStroke != null && gridPaint != null) {
/*2924*/        CategoryDataset data = getDataset();
/*2925*/        if (data != null) {
/*2926*/          CategoryAxis axis = getDomainAxis();
/*2927*/          if (axis != null) {
/*2928*/            int columnCount = data.getColumnCount();
/*2929*/            for (int c = 0; c < columnCount; c++) {
/*2930*/              double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
/*2933*/              CategoryItemRenderer renderer1 = getRenderer();
/*2934*/              if (renderer1 != null)
/*2935*/                renderer1.drawDomainGridline(g2, this, dataArea, xx); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
/*2957*/    if (isRangeGridlinesVisible()) {
/*2958*/      Stroke gridStroke = getRangeGridlineStroke();
/*2959*/      Paint gridPaint = getRangeGridlinePaint();
/*2960*/      if (gridStroke != null && gridPaint != null) {
/*2961*/        ValueAxis axis = getRangeAxis();
/*2962*/        if (axis != null) {
/*2963*/          Iterator<ValueTick> iterator = ticks.iterator();
/*2964*/          while (iterator.hasNext()) {
/*2965*/            ValueTick tick = iterator.next();
/*2966*/            CategoryItemRenderer renderer1 = getRenderer();
/*2967*/            if (renderer1 != null)
/*2968*/              renderer1.drawRangeGridline(g2, this, getRangeAxis(), dataArea, tick.getValue()); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
/*2987*/    Iterator<CategoryAnnotation> iterator = getAnnotations().iterator();
/*2988*/    while (iterator.hasNext()) {
/*2989*/      CategoryAnnotation annotation = iterator.next();
/*2991*/      annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis(), 0, info);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*3011*/    CategoryItemRenderer r = getRenderer(index);
/*3012*/    if (r == null)
/*   0*/      return; 
/*3016*/    Collection markers = getDomainMarkers(index, layer);
/*3017*/    CategoryAxis axis = getDomainAxisForDataset(index);
/*3018*/    if (markers != null && axis != null) {
/*3019*/      Iterator<CategoryMarker> iterator = markers.iterator();
/*3020*/      while (iterator.hasNext()) {
/*3021*/        CategoryMarker marker = iterator.next();
/*3022*/        r.drawDomainMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*3042*/    CategoryItemRenderer r = getRenderer(index);
/*3043*/    if (r == null)
/*   0*/      return; 
/*3047*/    Collection markers = getRangeMarkers(index, layer);
/*3048*/    ValueAxis axis = getRangeAxisForDataset(index);
/*3049*/    if (markers != null && axis != null) {
/*3050*/      Iterator<Marker> iterator = markers.iterator();
/*3051*/      while (iterator.hasNext()) {
/*3052*/        Marker marker = iterator.next();
/*3053*/        r.drawRangeMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
/*3072*/    double java2D = getRangeAxis().valueToJava2D(value, dataArea, getRangeAxisEdge());
/*3074*/    Line2D line = null;
/*3075*/    if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3076*/      line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
/*3079*/    } else if (this.orientation == PlotOrientation.VERTICAL) {
/*3080*/      line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
/*   0*/    } 
/*3083*/    g2.setStroke(stroke);
/*3084*/    g2.setPaint(paint);
/*3085*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
/*3106*/    if (!axis.getRange().contains(value))
/*   0*/      return; 
/*3109*/    Line2D line = null;
/*3110*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*3111*/      double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
/*3113*/      line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
/*   0*/    } else {
/*3117*/      double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
/*3119*/      line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
/*   0*/    } 
/*3122*/    g2.setStroke(stroke);
/*3123*/    g2.setPaint(paint);
/*3124*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public Range getDataRange(ValueAxis axis) {
/*3139*/    Range result = null;
/*3140*/    List mappedDatasets = new ArrayList();
/*3142*/    int rangeIndex = this.rangeAxes.indexOf(axis);
/*3143*/    if (rangeIndex >= 0) {
/*3144*/      mappedDatasets.addAll(datasetsMappedToRangeAxis(rangeIndex));
/*3146*/    } else if (axis == getRangeAxis()) {
/*3147*/      mappedDatasets.addAll(datasetsMappedToRangeAxis(0));
/*   0*/    } 
/*3152*/    Iterator<CategoryDataset> iterator = mappedDatasets.iterator();
/*3153*/    while (iterator.hasNext()) {
/*3154*/      CategoryDataset d = iterator.next();
/*3155*/      CategoryItemRenderer r = getRendererForDataset(d);
/*3156*/      if (r != null)
/*3157*/        result = Range.combine(result, r.findRangeBounds(d)); 
/*   0*/    } 
/*3160*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List datasetsMappedToDomainAxis(int axisIndex) {
/*3175*/    List<Object> result = new ArrayList();
/*3176*/    for (int datasetIndex = 0; datasetIndex < this.datasets.size(); 
/*3177*/      datasetIndex++) {
/*3178*/      Object dataset = this.datasets.get(datasetIndex);
/*3179*/      if (dataset != null) {
/*3180*/        Integer m = (Integer)this.datasetToDomainAxisMap.get(datasetIndex);
/*3182*/        if (m == null) {
/*3184*/          if (axisIndex == 0)
/*3185*/            result.add(dataset); 
/*3189*/        } else if (m == axisIndex) {
/*3190*/          result.add(dataset);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3195*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List datasetsMappedToRangeAxis(int index) {
/*3207*/    List<Object> result = new ArrayList();
/*3208*/    for (int i = 0; i < this.datasets.size(); i++) {
/*3209*/      Object dataset = this.datasets.get(i);
/*3210*/      if (dataset != null) {
/*3211*/        Integer m = (Integer)this.datasetToRangeAxisMap.get(i);
/*3212*/        if (m == null) {
/*3214*/          if (index == 0)
/*3215*/            result.add(dataset); 
/*3219*/        } else if (m == index) {
/*3220*/          result.add(dataset);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3225*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWeight() {
/*3237*/    return this.weight;
/*   0*/  }
/*   0*/  
/*   0*/  public void setWeight(int weight) {
/*3248*/    this.weight = weight;
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedDomainAxisSpace() {
/*3260*/    return this.fixedDomainAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedDomainAxisSpace(AxisSpace space) {
/*3271*/    this.fixedDomainAxisSpace = space;
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedRangeAxisSpace() {
/*3283*/    return this.fixedRangeAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedRangeAxisSpace(AxisSpace space) {
/*3294*/    this.fixedRangeAxisSpace = space;
/*   0*/  }
/*   0*/  
/*   0*/  public List getCategories() {
/*3306*/    List<?> result = null;
/*3307*/    if (getDataset() != null)
/*3308*/      result = Collections.unmodifiableList(getDataset().getColumnKeys()); 
/*3310*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public List getCategoriesForAxis(CategoryAxis axis) {
/*3324*/    List<Comparable> result = new ArrayList();
/*3325*/    int axisIndex = this.domainAxes.indexOf(axis);
/*3326*/    List datasets = datasetsMappedToDomainAxis(axisIndex);
/*3327*/    Iterator<CategoryDataset> iterator = datasets.iterator();
/*3328*/    while (iterator.hasNext()) {
/*3329*/      CategoryDataset dataset = iterator.next();
/*3331*/      for (int i = 0; i < dataset.getColumnCount(); i++) {
/*3332*/        Comparable category = dataset.getColumnKey(i);
/*3333*/        if (!result.contains(category))
/*3334*/          result.add(category); 
/*   0*/      } 
/*   0*/    } 
/*3338*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getDrawSharedDomainAxis() {
/*3350*/    return this.drawSharedDomainAxis;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDrawSharedDomainAxis(boolean draw) {
/*3362*/    this.drawSharedDomainAxis = draw;
/*3363*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainZoomable() {
/*3375*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeZoomable() {
/*3386*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {}
/*   0*/  
/*   0*/  public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {}
/*   0*/  
/*   0*/  public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
/*3425*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3426*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*3427*/      if (rangeAxis != null)
/*3428*/        rangeAxis.resizeRange(factor); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
/*3443*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3444*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*3445*/      if (rangeAxis != null)
/*3446*/        rangeAxis.zoomRange(lowerPercent, upperPercent); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getAnchorValue() {
/*3459*/    return this.anchorValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAnchorValue(double value) {
/*3471*/    setAnchorValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setAnchorValue(double value, boolean notify) {
/*3484*/    this.anchorValue = value;
/*3485*/    if (notify)
/*3486*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*3499*/    if (obj == this)
/*3500*/      return true; 
/*3502*/    if (!(obj instanceof CategoryPlot))
/*3503*/      return false; 
/*3505*/    if (!super.equals(obj))
/*3506*/      return false; 
/*3509*/    CategoryPlot that = (CategoryPlot)obj;
/*3511*/    if (this.orientation != that.orientation)
/*3512*/      return false; 
/*3514*/    if (!ObjectUtilities.equal(this.axisOffset, that.axisOffset))
/*3515*/      return false; 
/*3517*/    if (!this.domainAxes.equals(that.domainAxes))
/*3518*/      return false; 
/*3520*/    if (!this.domainAxisLocations.equals(that.domainAxisLocations))
/*3521*/      return false; 
/*3523*/    if (this.drawSharedDomainAxis != that.drawSharedDomainAxis)
/*3524*/      return false; 
/*3526*/    if (!this.rangeAxes.equals(that.rangeAxes))
/*3527*/      return false; 
/*3529*/    if (!this.rangeAxisLocations.equals(that.rangeAxisLocations))
/*3530*/      return false; 
/*3532*/    if (!ObjectUtilities.equal(this.datasetToDomainAxisMap, that.datasetToDomainAxisMap))
/*3534*/      return false; 
/*3536*/    if (!ObjectUtilities.equal(this.datasetToRangeAxisMap, that.datasetToRangeAxisMap))
/*3538*/      return false; 
/*3540*/    if (!ObjectUtilities.equal(this.renderers, that.renderers))
/*3541*/      return false; 
/*3543*/    if (this.renderingOrder != that.renderingOrder)
/*3544*/      return false; 
/*3546*/    if (this.columnRenderingOrder != that.columnRenderingOrder)
/*3547*/      return false; 
/*3549*/    if (this.rowRenderingOrder != that.rowRenderingOrder)
/*3550*/      return false; 
/*3552*/    if (this.domainGridlinesVisible != that.domainGridlinesVisible)
/*3553*/      return false; 
/*3555*/    if (this.domainGridlinePosition != that.domainGridlinePosition)
/*3556*/      return false; 
/*3558*/    if (!ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke))
/*3560*/      return false; 
/*3562*/    if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint))
/*3564*/      return false; 
/*3566*/    if (this.rangeGridlinesVisible != that.rangeGridlinesVisible)
/*3567*/      return false; 
/*3569*/    if (!ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke))
/*3571*/      return false; 
/*3573*/    if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint))
/*3575*/      return false; 
/*3577*/    if (this.anchorValue != that.anchorValue)
/*3578*/      return false; 
/*3580*/    if (this.rangeCrosshairVisible != that.rangeCrosshairVisible)
/*3581*/      return false; 
/*3583*/    if (this.rangeCrosshairValue != that.rangeCrosshairValue)
/*3584*/      return false; 
/*3586*/    if (!ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke))
/*3588*/      return false; 
/*3590*/    if (!PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint))
/*3592*/      return false; 
/*3594*/    if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData)
/*3596*/      return false; 
/*3598*/    if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers))
/*3600*/      return false; 
/*3602*/    if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers))
/*3604*/      return false; 
/*3606*/    if (!ObjectUtilities.equal(this.annotations, that.annotations))
/*3607*/      return false; 
/*3609*/    if (this.weight != that.weight)
/*3610*/      return false; 
/*3612*/    if (!ObjectUtilities.equal(this.fixedDomainAxisSpace, that.fixedDomainAxisSpace))
/*3614*/      return false; 
/*3616*/    if (!ObjectUtilities.equal(this.fixedRangeAxisSpace, that.fixedRangeAxisSpace))
/*3618*/      return false; 
/*3621*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*3634*/    CategoryPlot clone = (CategoryPlot)super.clone();
/*3636*/    clone.domainAxes = new ObjectList();
/*3637*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*3638*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
/*3639*/      if (xAxis != null) {
/*3640*/        CategoryAxis clonedAxis = (CategoryAxis)xAxis.clone();
/*3641*/        clone.setDomainAxis(i, clonedAxis);
/*   0*/      } 
/*   0*/    } 
/*3644*/    clone.domainAxisLocations = (ObjectList)this.domainAxisLocations.clone();
/*3647*/    clone.rangeAxes = new ObjectList();
/*3648*/    for (int k = 0; k < this.rangeAxes.size(); k++) {
/*3649*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(k);
/*3650*/      if (yAxis != null) {
/*3651*/        ValueAxis clonedAxis = (ValueAxis)yAxis.clone();
/*3652*/        clone.setRangeAxis(k, clonedAxis);
/*   0*/      } 
/*   0*/    } 
/*3655*/    clone.rangeAxisLocations = (ObjectList)this.rangeAxisLocations.clone();
/*3657*/    clone.datasets = (ObjectList)this.datasets.clone();
/*3658*/    for (int j = 0; j < clone.datasets.size(); j++) {
/*3659*/      CategoryDataset dataset = clone.getDataset(j);
/*3660*/      if (dataset != null)
/*3661*/        dataset.addChangeListener(clone); 
/*   0*/    } 
/*3664*/    clone.datasetToDomainAxisMap = (ObjectList)this.datasetToDomainAxisMap.clone();
/*3666*/    clone.datasetToRangeAxisMap = (ObjectList)this.datasetToRangeAxisMap.clone();
/*3668*/    clone.renderers = (ObjectList)this.renderers.clone();
/*3669*/    if (this.fixedDomainAxisSpace != null)
/*3670*/      clone.fixedDomainAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedDomainAxisSpace); 
/*3673*/    if (this.fixedRangeAxisSpace != null)
/*3674*/      clone.fixedRangeAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedRangeAxisSpace); 
/*3678*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*3690*/    stream.defaultWriteObject();
/*3691*/    SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
/*3692*/    SerialUtilities.writePaint(this.domainGridlinePaint, stream);
/*3693*/    SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
/*3694*/    SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
/*3695*/    SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
/*3696*/    SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*3710*/    stream.defaultReadObject();
/*3711*/    this.domainGridlineStroke = SerialUtilities.readStroke(stream);
/*3712*/    this.domainGridlinePaint = SerialUtilities.readPaint(stream);
/*3713*/    this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
/*3714*/    this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
/*3715*/    this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
/*3716*/    this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
/*3718*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*3719*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
/*3720*/      if (xAxis != null) {
/*3721*/        xAxis.setPlot(this);
/*3722*/        xAxis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*3725*/    for (int j = 0; j < this.rangeAxes.size(); j++) {
/*3726*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(j);
/*3727*/      if (yAxis != null) {
/*3728*/        yAxis.setPlot(this);
/*3729*/        yAxis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*3732*/    int datasetCount = this.datasets.size();
/*3733*/    for (int k = 0; k < datasetCount; k++) {
/*3734*/      Dataset dataset = (Dataset)this.datasets.get(k);
/*3735*/      if (dataset != null)
/*3736*/        dataset.addChangeListener(this); 
/*   0*/    } 
/*3739*/    int rendererCount = this.renderers.size();
/*3740*/    for (int m = 0; m < rendererCount; m++) {
/*3741*/      CategoryItemRenderer renderer = (CategoryItemRenderer)this.renderers.get(m);
/*3743*/      if (renderer != null)
/*3744*/        renderer.addChangeListener(this); 
/*   0*/    } 
/*   0*/  }
/*   0*/}
