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
/* 242*/  public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5F, 0, 2, 0.0F, new float[] { 2.0F, 2.0F }, 0.0F);
/*   0*/  
/* 250*/  public static final Paint DEFAULT_GRIDLINE_PAINT = Color.lightGray;
/*   0*/  
/* 253*/  public static final Font DEFAULT_VALUE_LABEL_FONT = new Font("SansSerif", 0, 10);
/*   0*/  
/*   0*/  public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
/*   0*/  
/* 268*/  public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
/*   0*/  
/* 276*/  public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;
/*   0*/  
/* 279*/  protected static ResourceBundle localizationResources = ResourceBundle.getBundle("org.jfree.chart.plot.LocalizationBundle");
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
/* 319*/  private DatasetRenderingOrder renderingOrder = DatasetRenderingOrder.REVERSE;
/*   0*/  
/* 326*/  private SortOrder columnRenderingOrder = SortOrder.ASCENDING;
/*   0*/  
/* 332*/  private SortOrder rowRenderingOrder = SortOrder.ASCENDING;
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
/* 423*/    this(null, null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot(CategoryDataset dataset, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryItemRenderer renderer) {
/* 442*/    this.orientation = PlotOrientation.VERTICAL;
/* 445*/    this.domainAxes = new ObjectList();
/* 446*/    this.domainAxisLocations = new ObjectList();
/* 447*/    this.rangeAxes = new ObjectList();
/* 448*/    this.rangeAxisLocations = new ObjectList();
/* 450*/    this.datasetToDomainAxisMap = new ObjectList();
/* 451*/    this.datasetToRangeAxisMap = new ObjectList();
/* 453*/    this.renderers = new ObjectList();
/* 455*/    this.datasets = new ObjectList();
/* 456*/    this.datasets.set(0, dataset);
/* 457*/    if (dataset != null)
/* 458*/      dataset.addChangeListener(this); 
/* 461*/    this.axisOffset = RectangleInsets.ZERO_INSETS;
/* 463*/    setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT, false);
/* 464*/    setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, false);
/* 466*/    this.renderers.set(0, renderer);
/* 467*/    if (renderer != null) {
/* 468*/      renderer.setPlot(this);
/* 469*/      renderer.addChangeListener(this);
/*   0*/    } 
/* 472*/    this.domainAxes.set(0, domainAxis);
/* 473*/    mapDatasetToDomainAxis(0, 0);
/* 474*/    if (domainAxis != null) {
/* 475*/      domainAxis.setPlot(this);
/* 476*/      domainAxis.addChangeListener(this);
/*   0*/    } 
/* 478*/    this.drawSharedDomainAxis = false;
/* 480*/    this.rangeAxes.set(0, rangeAxis);
/* 481*/    mapDatasetToRangeAxis(0, 0);
/* 482*/    if (rangeAxis != null) {
/* 483*/      rangeAxis.setPlot(this);
/* 484*/      rangeAxis.addChangeListener(this);
/*   0*/    } 
/* 487*/    configureDomainAxes();
/* 488*/    configureRangeAxes();
/* 490*/    this.domainGridlinesVisible = false;
/* 491*/    this.domainGridlinePosition = CategoryAnchor.MIDDLE;
/* 492*/    this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 493*/    this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 495*/    this.rangeGridlinesVisible = true;
/* 496*/    this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 497*/    this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 499*/    this.foregroundDomainMarkers = new HashMap();
/* 500*/    this.backgroundDomainMarkers = new HashMap();
/* 501*/    this.foregroundRangeMarkers = new HashMap();
/* 502*/    this.backgroundRangeMarkers = new HashMap();
/* 504*/    Marker baseline = new ValueMarker(0.0D, new Color(0.8F, 0.8F, 0.8F, 0.5F), new BasicStroke(1.0F), new Color(0.85F, 0.85F, 0.95F, 0.5F), new BasicStroke(1.0F), 0.6F);
/* 507*/    addRangeMarker(baseline, Layer.BACKGROUND);
/* 509*/    this.anchorValue = 0.0D;
/* 511*/    this.rangeCrosshairVisible = false;
/* 512*/    this.rangeCrosshairValue = 0.0D;
/* 513*/    this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
/* 514*/    this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
/* 516*/    this.annotations = new ArrayList();
/*   0*/  }
/*   0*/  
/*   0*/  public String getPlotType() {
/* 526*/    return localizationResources.getString("Category_Plot");
/*   0*/  }
/*   0*/  
/*   0*/  public PlotOrientation getOrientation() {
/* 537*/    return this.orientation;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOrientation(PlotOrientation orientation) {
/* 549*/    if (orientation == null)
/* 550*/      throw new IllegalArgumentException("Null 'orientation' argument."); 
/* 552*/    this.orientation = orientation;
/* 553*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getAxisOffset() {
/* 564*/    return this.axisOffset;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisOffset(RectangleInsets offset) {
/* 576*/    if (offset == null)
/* 577*/      throw new IllegalArgumentException("Null 'offset' argument."); 
/* 579*/    this.axisOffset = offset;
/* 580*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxis() {
/* 593*/    return getDomainAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxis(int index) {
/* 606*/    CategoryAxis result = null;
/* 607*/    if (index < this.domainAxes.size())
/* 608*/      result = (CategoryAxis)this.domainAxes.get(index); 
/* 610*/    if (result == null) {
/* 611*/      Plot parent = getParent();
/* 612*/      if (parent instanceof CategoryPlot) {
/* 613*/        CategoryPlot cp = (CategoryPlot)parent;
/* 614*/        result = cp.getDomainAxis(index);
/*   0*/      } 
/*   0*/    } 
/* 617*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(CategoryAxis axis) {
/* 629*/    setDomainAxis(0, axis);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, CategoryAxis axis) {
/* 642*/    setDomainAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, CategoryAxis axis, boolean notify) {
/* 654*/    CategoryAxis existing = (CategoryAxis)this.domainAxes.get(index);
/* 655*/    if (existing != null)
/* 656*/      existing.removeChangeListener(this); 
/* 658*/    if (axis != null)
/* 659*/      axis.setPlot(this); 
/* 661*/    this.domainAxes.set(index, axis);
/* 662*/    if (axis != null) {
/* 663*/      axis.configure();
/* 664*/      axis.addChangeListener(this);
/*   0*/    } 
/* 666*/    if (notify)
/* 667*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxes(CategoryAxis[] axes) {
/* 680*/    for (int i = 0; i < axes.length; i++)
/* 681*/      setDomainAxis(i, axes[i], false); 
/* 683*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisIndex(CategoryAxis axis) {
/* 697*/    return this.domainAxes.indexOf(axis);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation() {
/* 708*/    return getDomainAxisLocation(0);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation(int index) {
/* 721*/    AxisLocation result = null;
/* 722*/    if (index < this.domainAxisLocations.size())
/* 723*/      result = (AxisLocation)this.domainAxisLocations.get(index); 
/* 725*/    if (result == null)
/* 726*/      result = AxisLocation.getOpposite(getDomainAxisLocation(0)); 
/* 728*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location) {
/* 742*/    setDomainAxisLocation(0, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location, boolean notify) {
/* 754*/    setDomainAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location) {
/* 769*/    setDomainAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
/* 787*/    if (index == 0 && location == null)
/* 788*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/* 791*/    this.domainAxisLocations.set(index, location);
/* 792*/    if (notify)
/* 793*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge() {
/* 804*/    return getDomainAxisEdge(0);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge(int index) {
/* 815*/    RectangleEdge result = null;
/* 816*/    AxisLocation location = getDomainAxisLocation(index);
/* 817*/    if (location != null) {
/* 818*/      result = Plot.resolveDomainAxisLocation(location, this.orientation);
/*   0*/    } else {
/* 821*/      result = RectangleEdge.opposite(getDomainAxisEdge(0));
/*   0*/    } 
/* 823*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisCount() {
/* 832*/    return this.domainAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainAxes() {
/* 840*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 841*/      CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
/* 842*/      if (axis != null)
/* 843*/        axis.removeChangeListener(this); 
/*   0*/    } 
/* 846*/    this.domainAxes.clear();
/* 847*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void configureDomainAxes() {
/* 854*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 855*/      CategoryAxis axis = (CategoryAxis)this.domainAxes.get(i);
/* 856*/      if (axis != null)
/* 857*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis() {
/* 870*/    return getRangeAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis(int index) {
/* 881*/    ValueAxis result = null;
/* 882*/    if (index < this.rangeAxes.size())
/* 883*/      result = (ValueAxis)this.rangeAxes.get(index); 
/* 885*/    if (result == null) {
/* 886*/      Plot parent = getParent();
/* 887*/      if (parent instanceof CategoryPlot) {
/* 888*/        CategoryPlot cp = (CategoryPlot)parent;
/* 889*/        result = cp.getRangeAxis(index);
/*   0*/      } 
/*   0*/    } 
/* 892*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(ValueAxis axis) {
/* 902*/    setRangeAxis(0, axis);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis) {
/* 913*/    setRangeAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
/* 925*/    ValueAxis existing = (ValueAxis)this.rangeAxes.get(index);
/* 926*/    if (existing != null)
/* 927*/      existing.removeChangeListener(this); 
/* 929*/    if (axis != null)
/* 930*/      axis.setPlot(this); 
/* 932*/    this.rangeAxes.set(index, axis);
/* 933*/    if (axis != null) {
/* 934*/      axis.configure();
/* 935*/      axis.addChangeListener(this);
/*   0*/    } 
/* 937*/    if (notify)
/* 938*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxes(ValueAxis[] axes) {
/* 951*/    for (int i = 0; i < axes.length; i++)
/* 952*/      setRangeAxis(i, axes[i], false); 
/* 954*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation() {
/* 963*/    return getRangeAxisLocation(0);
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation(int index) {
/* 976*/    AxisLocation result = null;
/* 977*/    if (index < this.rangeAxisLocations.size())
/* 978*/      result = (AxisLocation)this.rangeAxisLocations.get(index); 
/* 980*/    if (result == null)
/* 981*/      result = AxisLocation.getOpposite(getRangeAxisLocation(0)); 
/* 983*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location) {
/* 997*/    setRangeAxisLocation(location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location, boolean notify) {
/*1010*/    setRangeAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location) {
/*1024*/    setRangeAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
/*1040*/    if (index == 0 && location == null)
/*1041*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/*1044*/    this.rangeAxisLocations.set(index, location);
/*1045*/    if (notify)
/*1046*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge() {
/*1056*/    return getRangeAxisEdge(0);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge(int index) {
/*1067*/    AxisLocation location = getRangeAxisLocation(index);
/*1068*/    RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
/*1070*/    if (result == null)
/*1071*/      result = RectangleEdge.opposite(getRangeAxisEdge(0)); 
/*1073*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getRangeAxisCount() {
/*1082*/    return this.rangeAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeAxes() {
/*1090*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1091*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1092*/      if (axis != null)
/*1093*/        axis.removeChangeListener(this); 
/*   0*/    } 
/*1096*/    this.rangeAxes.clear();
/*1097*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void configureRangeAxes() {
/*1104*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1105*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1106*/      if (axis != null)
/*1107*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryDataset getDataset() {
/*1120*/    return getDataset(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryDataset getDataset(int index) {
/*1133*/    CategoryDataset result = null;
/*1134*/    if (this.datasets.size() > index)
/*1135*/      result = (CategoryDataset)this.datasets.get(index); 
/*1137*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(CategoryDataset dataset) {
/*1152*/    setDataset(0, dataset);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(int index, CategoryDataset dataset) {
/*1165*/    CategoryDataset existing = (CategoryDataset)this.datasets.get(index);
/*1166*/    if (existing != null)
/*1167*/      existing.removeChangeListener(this); 
/*1169*/    this.datasets.set(index, dataset);
/*1170*/    if (dataset != null)
/*1171*/      dataset.addChangeListener(this); 
/*1175*/    DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
/*1176*/    datasetChanged(event);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDatasetCount() {
/*1188*/    return this.datasets.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToDomainAxis(int index, int axisIndex) {
/*1200*/    this.datasetToDomainAxisMap.set(index, new Integer(axisIndex));
/*1202*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAxis getDomainAxisForDataset(int index) {
/*1216*/    CategoryAxis result = getDomainAxis();
/*1217*/    Integer axisIndex = (Integer)this.datasetToDomainAxisMap.get(index);
/*1218*/    if (axisIndex != null)
/*1219*/      result = getDomainAxis(axisIndex); 
/*1221*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToRangeAxis(int index, int axisIndex) {
/*1233*/    this.datasetToRangeAxisMap.set(index, new Integer(axisIndex));
/*1235*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxisForDataset(int index) {
/*1249*/    ValueAxis result = getRangeAxis();
/*1250*/    Integer axisIndex = (Integer)this.datasetToRangeAxisMap.get(index);
/*1251*/    if (axisIndex != null)
/*1252*/      result = getRangeAxis(axisIndex); 
/*1254*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRenderer() {
/*1265*/    return getRenderer(0);
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRenderer(int index) {
/*1278*/    CategoryItemRenderer result = null;
/*1279*/    if (this.renderers.size() > index)
/*1280*/      result = (CategoryItemRenderer)this.renderers.get(index); 
/*1282*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(CategoryItemRenderer renderer) {
/*1295*/    setRenderer(0, renderer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(CategoryItemRenderer renderer, boolean notify) {
/*1316*/    setRenderer(0, renderer, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, CategoryItemRenderer renderer) {
/*1330*/    setRenderer(index, renderer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, CategoryItemRenderer renderer, boolean notify) {
/*1347*/    CategoryItemRenderer existing = (CategoryItemRenderer)this.renderers.get(index);
/*1349*/    if (existing != null)
/*1350*/      existing.removeChangeListener(this); 
/*1354*/    this.renderers.set(index, renderer);
/*1355*/    if (renderer != null) {
/*1356*/      renderer.setPlot(this);
/*1357*/      renderer.addChangeListener(this);
/*   0*/    } 
/*1360*/    configureDomainAxes();
/*1361*/    configureRangeAxes();
/*1363*/    if (notify)
/*1364*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderers(CategoryItemRenderer[] renderers) {
/*1375*/    for (int i = 0; i < renderers.length; i++)
/*1376*/      setRenderer(i, renderers[i], false); 
/*1378*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryItemRenderer getRendererForDataset(CategoryDataset dataset) {
/*1390*/    CategoryItemRenderer result = null;
/*1391*/    for (int i = 0; i < this.datasets.size(); i++) {
/*1392*/      if (this.datasets.get(i) == dataset) {
/*1393*/        result = (CategoryItemRenderer)this.renderers.get(i);
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1397*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOf(CategoryItemRenderer renderer) {
/*1409*/    return this.renderers.indexOf(renderer);
/*   0*/  }
/*   0*/  
/*   0*/  public DatasetRenderingOrder getDatasetRenderingOrder() {
/*1420*/    return this.renderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
/*1434*/    if (order == null)
/*1435*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1437*/    this.renderingOrder = order;
/*1438*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public SortOrder getColumnRenderingOrder() {
/*1450*/    return this.columnRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setColumnRenderingOrder(SortOrder order) {
/*1465*/    if (order == null)
/*1466*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1468*/    this.columnRenderingOrder = order;
/*1469*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public SortOrder getRowRenderingOrder() {
/*1481*/    return this.rowRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRowRenderingOrder(SortOrder order) {
/*1496*/    if (order == null)
/*1497*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1499*/    this.rowRenderingOrder = order;
/*1500*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainGridlinesVisible() {
/*1511*/    return this.domainGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinesVisible(boolean visible) {
/*1526*/    if (this.domainGridlinesVisible != visible) {
/*1527*/      this.domainGridlinesVisible = visible;
/*1528*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryAnchor getDomainGridlinePosition() {
/*1540*/    return this.domainGridlinePosition;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinePosition(CategoryAnchor position) {
/*1552*/    if (position == null)
/*1553*/      throw new IllegalArgumentException("Null 'position' argument."); 
/*1555*/    this.domainGridlinePosition = position;
/*1556*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainGridlineStroke() {
/*1567*/    return this.domainGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlineStroke(Stroke stroke) {
/*1579*/    if (stroke == null)
/*1580*/      throw new IllegalArgumentException("Null 'stroke' not permitted."); 
/*1582*/    this.domainGridlineStroke = stroke;
/*1583*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainGridlinePaint() {
/*1594*/    return this.domainGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinePaint(Paint paint) {
/*1606*/    if (paint == null)
/*1607*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1609*/    this.domainGridlinePaint = paint;
/*1610*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeGridlinesVisible() {
/*1621*/    return this.rangeGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinesVisible(boolean visible) {
/*1634*/    if (this.rangeGridlinesVisible != visible) {
/*1635*/      this.rangeGridlinesVisible = visible;
/*1636*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeGridlineStroke() {
/*1648*/    return this.rangeGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlineStroke(Stroke stroke) {
/*1660*/    if (stroke == null)
/*1661*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*1663*/    this.rangeGridlineStroke = stroke;
/*1664*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeGridlinePaint() {
/*1675*/    return this.rangeGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinePaint(Paint paint) {
/*1687*/    if (paint == null)
/*1688*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1690*/    this.rangeGridlinePaint = paint;
/*1691*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getFixedLegendItems() {
/*1702*/    return this.fixedLegendItems;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedLegendItems(LegendItemCollection items) {
/*1715*/    this.fixedLegendItems = items;
/*1716*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/*1727*/    LegendItemCollection result = this.fixedLegendItems;
/*1728*/    if (result == null) {
/*1729*/      result = new LegendItemCollection();
/*1731*/      int count = this.datasets.size();
/*1732*/      for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
/*1733*/        CategoryDataset dataset = getDataset(datasetIndex);
/*1734*/        if (dataset != null) {
/*1735*/          CategoryItemRenderer renderer = getRenderer(datasetIndex);
/*1736*/          if (renderer != null) {
/*1737*/            int seriesCount = dataset.getRowCount();
/*1738*/            for (int i = 0; i < seriesCount; i++) {
/*1739*/              LegendItem item = renderer.getLegendItem(datasetIndex, i);
/*1741*/              if (item != null)
/*1742*/                result.add(item); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*1749*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void handleClick(int x, int y, PlotRenderingInfo info) {
/*1762*/    Rectangle2D dataArea = info.getDataArea();
/*1763*/    if (dataArea.contains(x, y)) {
/*1765*/      double java2D = 0.0D;
/*1766*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*1767*/        java2D = x;
/*1769*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*1770*/        java2D = y;
/*   0*/      } 
/*1772*/      RectangleEdge edge = Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
/*1774*/      double value = getRangeAxis().java2DToValue(java2D, info.getDataArea(), edge);
/*1776*/      setAnchorValue(value);
/*1777*/      setRangeCrosshairValue(value);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoom(double percent) {
/*1793*/    if (percent > 0.0D) {
/*1794*/      double range = getRangeAxis().getRange().getLength();
/*1795*/      double scaledRange = range * percent;
/*1796*/      getRangeAxis().setRange(this.anchorValue - scaledRange / 2.0D, this.anchorValue + scaledRange / 2.0D);
/*   0*/    } else {
/*1800*/      getRangeAxis().setAutoRange(true);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void datasetChanged(DatasetChangeEvent event) {
/*1814*/    int count = this.rangeAxes.size();
/*1815*/    for (int axisIndex = 0; axisIndex < count; axisIndex++) {
/*1816*/      ValueAxis yAxis = getRangeAxis(axisIndex);
/*1817*/      if (yAxis != null)
/*1818*/        yAxis.configure(); 
/*   0*/    } 
/*1821*/    if (getParent() != null) {
/*1822*/      getParent().datasetChanged(event);
/*   0*/    } else {
/*1825*/      PlotChangeEvent e = new PlotChangeEvent(this);
/*1826*/      e.setType(ChartChangeEventType.DATASET_UPDATED);
/*1827*/      notifyListeners(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rendererChanged(RendererChangeEvent event) {
/*1838*/    Plot parent = getParent();
/*1839*/    if (parent != null) {
/*1840*/      if (parent instanceof RendererChangeListener) {
/*1841*/        RendererChangeListener rcl = (RendererChangeListener)parent;
/*1842*/        rcl.rendererChanged(event);
/*   0*/      } else {
/*1847*/        throw new RuntimeException("The renderer has changed and I don't know what to do!");
/*   0*/      } 
/*   0*/    } else {
/*1852*/      configureRangeAxes();
/*1853*/      PlotChangeEvent e = new PlotChangeEvent(this);
/*1854*/      notifyListeners(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(CategoryMarker marker) {
/*1867*/    addDomainMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(CategoryMarker marker, Layer layer) {
/*1881*/    addDomainMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(int index, CategoryMarker marker, Layer layer) {
/*1895*/    if (marker == null)
/*1896*/      throw new IllegalArgumentException("Null 'marker' not permitted."); 
/*1898*/    if (layer == null)
/*1899*/      throw new IllegalArgumentException("Null 'layer' not permitted."); 
/*1902*/    if (layer == Layer.FOREGROUND) {
/*1903*/      Collection<CategoryMarker> markers = (Collection)this.foregroundDomainMarkers.get(new Integer(index));
/*1905*/      if (markers == null) {
/*1906*/        markers = new ArrayList();
/*1907*/        this.foregroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*1909*/      markers.add(marker);
/*1911*/    } else if (layer == Layer.BACKGROUND) {
/*1912*/      Collection<CategoryMarker> markers = (Collection)this.backgroundDomainMarkers.get(new Integer(index));
/*1914*/      if (markers == null) {
/*1915*/        markers = new ArrayList();
/*1916*/        this.backgroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*1918*/      markers.add(marker);
/*   0*/    } 
/*1920*/    marker.addChangeListener(this);
/*1921*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers() {
/*1931*/    if (this.backgroundDomainMarkers != null) {
/*1932*/      Set keys = this.backgroundDomainMarkers.keySet();
/*1933*/      Iterator<Integer> iterator = keys.iterator();
/*1934*/      while (iterator.hasNext()) {
/*1935*/        Integer key = iterator.next();
/*1936*/        clearDomainMarkers(key);
/*   0*/      } 
/*1938*/      this.backgroundDomainMarkers.clear();
/*   0*/    } 
/*1940*/    if (this.foregroundDomainMarkers != null) {
/*1941*/      Set keys = this.foregroundDomainMarkers.keySet();
/*1942*/      Iterator<Integer> iterator = keys.iterator();
/*1943*/      while (iterator.hasNext()) {
/*1944*/        Integer key = iterator.next();
/*1945*/        clearDomainMarkers(key);
/*   0*/      } 
/*1947*/      this.foregroundDomainMarkers.clear();
/*   0*/    } 
/*1949*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(Layer layer) {
/*1960*/    return getDomainMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(int index, Layer layer) {
/*1973*/    Collection<?> result = null;
/*1974*/    Integer key = new Integer(index);
/*1975*/    if (layer == Layer.FOREGROUND) {
/*1976*/      result = (Collection)this.foregroundDomainMarkers.get(key);
/*1978*/    } else if (layer == Layer.BACKGROUND) {
/*1979*/      result = (Collection)this.backgroundDomainMarkers.get(key);
/*   0*/    } 
/*1981*/    if (result != null)
/*1982*/      result = Collections.unmodifiableCollection(result); 
/*1984*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers(int index) {
/*1995*/    Integer key = new Integer(index);
/*1996*/    if (this.backgroundDomainMarkers != null) {
/*1997*/      Collection markers = (Collection)this.backgroundDomainMarkers.get(key);
/*1999*/      if (markers != null) {
/*2000*/        Iterator<Marker> iterator = markers.iterator();
/*2001*/        while (iterator.hasNext()) {
/*2002*/          Marker m = iterator.next();
/*2003*/          m.removeChangeListener(this);
/*   0*/        } 
/*2005*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2008*/    if (this.foregroundDomainMarkers != null) {
/*2009*/      Collection markers = (Collection)this.foregroundDomainMarkers.get(key);
/*2011*/      if (markers != null) {
/*2012*/        Iterator<Marker> iterator = markers.iterator();
/*2013*/        while (iterator.hasNext()) {
/*2014*/          Marker m = iterator.next();
/*2015*/          m.removeChangeListener(this);
/*   0*/        } 
/*2017*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2020*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker) {
/*2032*/    addRangeMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker, Layer layer) {
/*2046*/    addRangeMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(int index, Marker marker, Layer layer) {
/*2061*/    if (layer == Layer.FOREGROUND) {
/*2062*/      Collection<Marker> markers = (Collection)this.foregroundRangeMarkers.get(new Integer(index));
/*2064*/      if (markers == null) {
/*2065*/        markers = new ArrayList();
/*2066*/        this.foregroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2068*/      markers.add(marker);
/*2070*/    } else if (layer == Layer.BACKGROUND) {
/*2071*/      Collection<Marker> markers = (Collection)this.backgroundRangeMarkers.get(new Integer(index));
/*2073*/      if (markers == null) {
/*2074*/        markers = new ArrayList();
/*2075*/        this.backgroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2077*/      markers.add(marker);
/*   0*/    } 
/*2079*/    marker.addChangeListener(this);
/*2080*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers() {
/*2090*/    if (this.backgroundRangeMarkers != null) {
/*2091*/      Set keys = this.backgroundRangeMarkers.keySet();
/*2092*/      Iterator<Integer> iterator = keys.iterator();
/*2093*/      while (iterator.hasNext()) {
/*2094*/        Integer key = iterator.next();
/*2095*/        clearRangeMarkers(key);
/*   0*/      } 
/*2097*/      this.backgroundRangeMarkers.clear();
/*   0*/    } 
/*2099*/    if (this.foregroundRangeMarkers != null) {
/*2100*/      Set keys = this.foregroundRangeMarkers.keySet();
/*2101*/      Iterator<Integer> iterator = keys.iterator();
/*2102*/      while (iterator.hasNext()) {
/*2103*/        Integer key = iterator.next();
/*2104*/        clearRangeMarkers(key);
/*   0*/      } 
/*2106*/      this.foregroundRangeMarkers.clear();
/*   0*/    } 
/*2108*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(Layer layer) {
/*2121*/    return getRangeMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(int index, Layer layer) {
/*2134*/    Collection<?> result = null;
/*2135*/    Integer key = new Integer(index);
/*2136*/    if (layer == Layer.FOREGROUND) {
/*2137*/      result = (Collection)this.foregroundRangeMarkers.get(key);
/*2139*/    } else if (layer == Layer.BACKGROUND) {
/*2140*/      result = (Collection)this.backgroundRangeMarkers.get(key);
/*   0*/    } 
/*2142*/    if (result != null)
/*2143*/      result = Collections.unmodifiableCollection(result); 
/*2145*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers(int index) {
/*2156*/    Integer key = new Integer(index);
/*2157*/    if (this.backgroundRangeMarkers != null) {
/*2158*/      Collection markers = (Collection)this.backgroundRangeMarkers.get(key);
/*2160*/      if (markers != null) {
/*2161*/        Iterator<Marker> iterator = markers.iterator();
/*2162*/        while (iterator.hasNext()) {
/*2163*/          Marker m = iterator.next();
/*2164*/          m.removeChangeListener(this);
/*   0*/        } 
/*2166*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2169*/    if (this.foregroundRangeMarkers != null) {
/*2170*/      Collection markers = (Collection)this.foregroundRangeMarkers.get(key);
/*2172*/      if (markers != null) {
/*2173*/        Iterator<Marker> iterator = markers.iterator();
/*2174*/        while (iterator.hasNext()) {
/*2175*/          Marker m = iterator.next();
/*2176*/          m.removeChangeListener(this);
/*   0*/        } 
/*2178*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2181*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairVisible() {
/*2192*/    return this.rangeCrosshairVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairVisible(boolean flag) {
/*2203*/    if (this.rangeCrosshairVisible != flag) {
/*2204*/      this.rangeCrosshairVisible = flag;
/*2205*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairLockedOnData() {
/*2218*/    return this.rangeCrosshairLockedOnData;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairLockedOnData(boolean flag) {
/*2231*/    if (this.rangeCrosshairLockedOnData != flag) {
/*2232*/      this.rangeCrosshairLockedOnData = flag;
/*2233*/      notifyListeners(new PlotChangeEvent(this));
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getRangeCrosshairValue() {
/*2246*/    return this.rangeCrosshairValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value) {
/*2260*/    setRangeCrosshairValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value, boolean notify) {
/*2275*/    this.rangeCrosshairValue = value;
/*2276*/    if (isRangeCrosshairVisible() && notify)
/*2277*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeCrosshairStroke() {
/*2292*/    return this.rangeCrosshairStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairStroke(Stroke stroke) {
/*2306*/    if (stroke == null)
/*2307*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2309*/    this.rangeCrosshairStroke = stroke;
/*2310*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeCrosshairPaint() {
/*2323*/    return this.rangeCrosshairPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairPaint(Paint paint) {
/*2335*/    if (paint == null)
/*2336*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2338*/    this.rangeCrosshairPaint = paint;
/*2339*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public List getAnnotations() {
/*2348*/    return this.annotations;
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(CategoryAnnotation annotation) {
/*2360*/    if (annotation == null)
/*2361*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2363*/    this.annotations.add(annotation);
/*2364*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(CategoryAnnotation annotation) {
/*2378*/    if (annotation == null)
/*2379*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2381*/    boolean removed = this.annotations.remove(annotation);
/*2382*/    if (removed)
/*2383*/      notifyListeners(new PlotChangeEvent(this)); 
/*2385*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearAnnotations() {
/*2393*/    this.annotations.clear();
/*2394*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*2410*/    if (space == null)
/*2411*/      space = new AxisSpace(); 
/*2415*/    if (this.fixedDomainAxisSpace != null) {
/*2416*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*2417*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
/*2419*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
/*2422*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*2423*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
/*2425*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*   0*/      } 
/*   0*/    } else {
/*2431*/      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
/*2433*/      if (this.drawSharedDomainAxis)
/*2434*/        space = getDomainAxis().reserveSpace(g2, this, plotArea, domainEdge, space); 
/*2439*/      for (int i = 0; i < this.domainAxes.size(); i++) {
/*2440*/        Axis xAxis = (Axis)this.domainAxes.get(i);
/*2441*/        if (xAxis != null) {
/*2442*/          RectangleEdge edge = getDomainAxisEdge(i);
/*2443*/          space = xAxis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2448*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*2465*/    if (space == null)
/*2466*/      space = new AxisSpace(); 
/*2470*/    if (this.fixedRangeAxisSpace != null) {
/*2471*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*2472*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
/*2474*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*2477*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*2478*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
/*2480*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
/*   0*/      } 
/*   0*/    } else {
/*2486*/      for (int i = 0; i < this.rangeAxes.size(); i++) {
/*2487*/        Axis yAxis = (Axis)this.rangeAxes.get(i);
/*2488*/        if (yAxis != null) {
/*2489*/          RectangleEdge edge = getRangeAxisEdge(i);
/*2490*/          space = yAxis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2494*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
/*2508*/    AxisSpace space = new AxisSpace();
/*2509*/    space = calculateRangeAxisSpace(g2, plotArea, space);
/*2510*/    space = calculateDomainAxisSpace(g2, plotArea, space);
/*2511*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo state) {
/*2536*/    boolean b1 = (area.getWidth() <= 10.0D);
/*2537*/    boolean b2 = (area.getHeight() <= 10.0D);
/*2538*/    if (b1 || b2)
/*   0*/      return; 
/*2543*/    if (state == null)
/*2547*/      state = new PlotRenderingInfo(null); 
/*2549*/    state.setPlotArea(area);
/*2552*/    RectangleInsets insets = getInsets();
/*2553*/    insets.trim(area);
/*2556*/    AxisSpace space = calculateAxisSpace(g2, area);
/*2557*/    Rectangle2D dataArea = space.shrink(area, null);
/*2558*/    this.axisOffset.trim(dataArea);
/*2560*/    state.setDataArea(dataArea);
/*2564*/    if (getRenderer() != null) {
/*2565*/      getRenderer().drawBackground(g2, this, dataArea);
/*   0*/    } else {
/*2568*/      drawBackground(g2, dataArea);
/*   0*/    } 
/*2571*/    this;
/*2571*/    Map axisStateMap = this.foregroundDomainMarkers;
/*2574*/    Shape savedClip = g2.getClip();
/*2575*/    g2.clip(dataArea);
/*2577*/    drawDomainGridlines(g2, dataArea);
/*2579*/    AxisState rangeAxisState = (AxisState)axisStateMap.get(getRangeAxis());
/*2580*/    if (rangeAxisState == null && 
/*2581*/      parentState != null)
/*2582*/      rangeAxisState = (AxisState)parentState.getSharedAxisStates().get(getRangeAxis()); 
/*2586*/    if (rangeAxisState != null)
/*2587*/      drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks()); 
/*2591*/    for (int i = 0; i < this.renderers.size(); i++)
/*2592*/      drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND); 
/*2594*/    for (int j = 0; j < this.renderers.size(); j++)
/*2595*/      drawRangeMarkers(g2, dataArea, j, Layer.BACKGROUND); 
/*   0*/    boolean foundData = false;
/*2602*/    Composite originalComposite = g2.getComposite();
/*2603*/    g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
/*2606*/    DatasetRenderingOrder order = getDatasetRenderingOrder();
/*2607*/    if (order == DatasetRenderingOrder.FORWARD) {
/*2608*/      for (int n = 0; n < this.datasets.size(); n++)
/*2609*/        foundData = (render(g2, dataArea, n, state) || foundData); 
/*   0*/    } else {
/*2613*/      for (int n = this.datasets.size() - 1; n >= 0; n--)
/*2614*/        foundData = (render(g2, dataArea, n, state) || foundData); 
/*   0*/    } 
/*2618*/    for (int m = 0; m < this.renderers.size(); m++)
/*2619*/      drawDomainMarkers(g2, dataArea, m, Layer.FOREGROUND); 
/*2621*/    for (int k = 0; k < this.renderers.size(); k++)
/*2622*/      drawRangeMarkers(g2, dataArea, k, Layer.FOREGROUND); 
/*2626*/    drawAnnotations(g2, dataArea);
/*2628*/    g2.setClip(savedClip);
/*2629*/    g2.setComposite(originalComposite);
/*2631*/    if (!foundData)
/*2632*/      drawNoDataMessage(g2, dataArea); 
/*2636*/    if (isRangeCrosshairVisible())
/*2638*/      drawRangeCrosshair(g2, dataArea, getOrientation(), getRangeCrosshairValue(), getRangeAxis(), getRangeCrosshairStroke(), getRangeCrosshairPaint()); 
/*2644*/    if (getRenderer() != null) {
/*2645*/      getRenderer().drawOutline(g2, this, dataArea);
/*   0*/    } else {
/*2648*/      drawOutline(g2, dataArea);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawBackground(Graphics2D g2, Rectangle2D area) {
/*2664*/    fillBackground(g2, area, this.orientation);
/*2665*/    drawBackgroundImage(g2, area);
/*   0*/  }
/*   0*/  
/*   0*/  protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
/*2684*/    AxisCollection axisCollection = new AxisCollection();
/*2687*/    for (int index = 0; index < this.domainAxes.size(); index++) {
/*2688*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(index);
/*2689*/      if (xAxis != null)
/*2690*/        axisCollection.add(xAxis, getDomainAxisEdge(index)); 
/*   0*/    } 
/*2695*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*2696*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(i);
/*2697*/      if (yAxis != null)
/*2698*/        axisCollection.add(yAxis, getRangeAxisEdge(i)); 
/*   0*/    } 
/*2702*/    Map<Axis, AxisState> axisStateMap = new HashMap();
/*2705*/    double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
/*2707*/    Iterator<Axis> iterator = axisCollection.getAxesAtTop().iterator();
/*2708*/    while (iterator.hasNext()) {
/*2709*/      Axis axis = iterator.next();
/*2710*/      if (axis != null) {
/*2711*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
/*2713*/        cursor = axisState.getCursor();
/*2714*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2719*/    cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
/*2721*/    iterator = axisCollection.getAxesAtBottom().iterator();
/*2722*/    while (iterator.hasNext()) {
/*2723*/      Axis axis = iterator.next();
/*2724*/      if (axis != null) {
/*2725*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
/*2727*/        cursor = axisState.getCursor();
/*2728*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2733*/    cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
/*2735*/    iterator = axisCollection.getAxesAtLeft().iterator();
/*2736*/    while (iterator.hasNext()) {
/*2737*/      Axis axis = iterator.next();
/*2738*/      if (axis != null) {
/*2739*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
/*2741*/        cursor = axisState.getCursor();
/*2742*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2747*/    cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
/*2749*/    iterator = axisCollection.getAxesAtRight().iterator();
/*2750*/    while (iterator.hasNext()) {
/*2751*/      Axis axis = iterator.next();
/*2752*/      if (axis != null) {
/*2753*/        AxisState axisState = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
/*2755*/        cursor = axisState.getCursor();
/*2756*/        axisStateMap.put(axis, axisState);
/*   0*/      } 
/*   0*/    } 
/*2760*/    return axisStateMap;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info) {
/*   0*/    boolean foundData = false;
/*2779*/    CategoryDataset currentDataset = getDataset(index);
/*2780*/    CategoryItemRenderer renderer = getRenderer(index);
/*2781*/    CategoryAxis domainAxis = getDomainAxisForDataset(index);
/*2782*/    ValueAxis rangeAxis = getRangeAxisForDataset(index);
/*2783*/    boolean hasData = !DatasetUtilities.isEmptyOrNull(currentDataset);
/*2784*/    if (hasData && renderer != null) {
/*2786*/      foundData = true;
/*2787*/      CategoryItemRendererState state = renderer.initialise(g2, dataArea, this, index, info);
/*2789*/      int columnCount = currentDataset.getColumnCount();
/*2790*/      int rowCount = currentDataset.getRowCount();
/*2791*/      int passCount = renderer.getPassCount();
/*2792*/      for (int pass = 0; pass < passCount; pass++) {
/*2793*/        if (this.columnRenderingOrder == SortOrder.ASCENDING) {
/*2794*/          for (int column = 0; column < columnCount; column++) {
/*2795*/            if (this.rowRenderingOrder == SortOrder.ASCENDING) {
/*2796*/              for (int row = 0; row < rowCount; row++)
/*2797*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } else {
/*2803*/              for (int row = rowCount - 1; row >= 0; row--)
/*2804*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } else {
/*2812*/          for (int column = columnCount - 1; column >= 0; column--) {
/*2813*/            if (this.rowRenderingOrder == SortOrder.ASCENDING) {
/*2814*/              for (int row = 0; row < rowCount; row++)
/*2815*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } else {
/*2821*/              for (int row = rowCount - 1; row >= 0; row--)
/*2822*/                renderer.drawItem(g2, state, dataArea, this, domainAxis, rangeAxis, currentDataset, row, column, pass); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*2831*/    return foundData;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea) {
/*2846*/    if (isDomainGridlinesVisible()) {
/*2847*/      CategoryAnchor anchor = getDomainGridlinePosition();
/*2848*/      RectangleEdge domainAxisEdge = getDomainAxisEdge();
/*2849*/      Stroke gridStroke = getDomainGridlineStroke();
/*2850*/      Paint gridPaint = getDomainGridlinePaint();
/*2851*/      if (gridStroke != null && gridPaint != null) {
/*2853*/        CategoryDataset data = getDataset();
/*2854*/        if (data != null) {
/*2855*/          CategoryAxis axis = getDomainAxis();
/*2856*/          if (axis != null) {
/*2857*/            int columnCount = data.getColumnCount();
/*2858*/            for (int c = 0; c < columnCount; c++) {
/*2859*/              double xx = axis.getCategoryJava2DCoordinate(anchor, c, columnCount, dataArea, domainAxisEdge);
/*2862*/              CategoryItemRenderer renderer1 = getRenderer();
/*2863*/              if (renderer1 != null)
/*2864*/                renderer1.drawDomainGridline(g2, this, dataArea, xx); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
/*2886*/    if (isRangeGridlinesVisible()) {
/*2887*/      Stroke gridStroke = getRangeGridlineStroke();
/*2888*/      Paint gridPaint = getRangeGridlinePaint();
/*2889*/      if (gridStroke != null && gridPaint != null) {
/*2890*/        ValueAxis axis = getRangeAxis();
/*2891*/        if (axis != null) {
/*2892*/          Iterator<ValueTick> iterator = ticks.iterator();
/*2893*/          while (iterator.hasNext()) {
/*2894*/            ValueTick tick = iterator.next();
/*2895*/            CategoryItemRenderer renderer1 = getRenderer();
/*2896*/            if (renderer1 != null)
/*2897*/              renderer1.drawRangeGridline(g2, this, getRangeAxis(), dataArea, tick.getValue()); 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawAnnotations(Graphics2D g2, Rectangle2D dataArea) {
/*2914*/    if (getAnnotations() != null) {
/*2915*/      Iterator<CategoryAnnotation> iterator = getAnnotations().iterator();
/*2916*/      while (iterator.hasNext()) {
/*2917*/        CategoryAnnotation annotation = iterator.next();
/*2919*/        annotation.draw(g2, this, dataArea, getDomainAxis(), getRangeAxis());
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*2940*/    CategoryItemRenderer r = getRenderer(index);
/*2941*/    if (r == null)
/*   0*/      return; 
/*2945*/    Collection markers = getDomainMarkers(index, layer);
/*2946*/    CategoryAxis axis = getDomainAxisForDataset(index);
/*2947*/    if (markers != null && axis != null) {
/*2948*/      Iterator<CategoryMarker> iterator = markers.iterator();
/*2949*/      while (iterator.hasNext()) {
/*2950*/        CategoryMarker marker = iterator.next();
/*2951*/        r.drawDomainMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*2971*/    CategoryItemRenderer r = getRenderer(index);
/*2972*/    if (r == null)
/*   0*/      return; 
/*2976*/    Collection markers = getRangeMarkers(index, layer);
/*2977*/    ValueAxis axis = getRangeAxisForDataset(index);
/*2978*/    if (markers != null && axis != null) {
/*2979*/      Iterator<Marker> iterator = markers.iterator();
/*2980*/      while (iterator.hasNext()) {
/*2981*/        Marker marker = iterator.next();
/*2982*/        r.drawRangeMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
/*3001*/    double java2D = getRangeAxis().valueToJava2D(value, dataArea, getRangeAxisEdge());
/*3003*/    Line2D line = null;
/*3004*/    if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3005*/      line = new Line2D.Double(java2D, dataArea.getMinY(), java2D, dataArea.getMaxY());
/*3008*/    } else if (this.orientation == PlotOrientation.VERTICAL) {
/*3009*/      line = new Line2D.Double(dataArea.getMinX(), java2D, dataArea.getMaxX(), java2D);
/*   0*/    } 
/*3012*/    g2.setStroke(stroke);
/*3013*/    g2.setPaint(paint);
/*3014*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
/*3035*/    if (!axis.getRange().contains(value))
/*   0*/      return; 
/*3038*/    Line2D line = null;
/*3039*/    if (orientation == PlotOrientation.HORIZONTAL) {
/*3040*/      double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
/*3042*/      line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
/*   0*/    } else {
/*3046*/      double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
/*3048*/      line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
/*   0*/    } 
/*3051*/    g2.setStroke(stroke);
/*3052*/    g2.setPaint(paint);
/*3053*/    g2.draw(line);
/*   0*/  }
/*   0*/  
/*   0*/  public Range getDataRange(ValueAxis axis) {
/*3068*/    Range result = null;
/*3069*/    List mappedDatasets = new ArrayList();
/*3071*/    int rangeIndex = this.rangeAxes.indexOf(axis);
/*3072*/    if (rangeIndex >= 0) {
/*3073*/      mappedDatasets.addAll(datasetsMappedToRangeAxis(rangeIndex));
/*3075*/    } else if (axis == getRangeAxis()) {
/*3076*/      mappedDatasets.addAll(datasetsMappedToRangeAxis(0));
/*   0*/    } 
/*3081*/    Iterator<CategoryDataset> iterator = mappedDatasets.iterator();
/*3082*/    while (iterator.hasNext()) {
/*3083*/      CategoryDataset d = iterator.next();
/*3084*/      CategoryItemRenderer r = getRendererForDataset(d);
/*3085*/      if (r != null)
/*3086*/        result = Range.combine(result, r.findRangeBounds(d)); 
/*   0*/    } 
/*3089*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List datasetsMappedToDomainAxis(int axisIndex) {
/*3104*/    List<Object> result = new ArrayList();
/*3105*/    for (int datasetIndex = 0; datasetIndex < this.datasets.size(); 
/*3106*/      datasetIndex++) {
/*3107*/      Object dataset = this.datasets.get(datasetIndex);
/*3108*/      if (dataset != null) {
/*3109*/        Integer m = (Integer)this.datasetToDomainAxisMap.get(datasetIndex);
/*3111*/        if (m == null) {
/*3113*/          if (axisIndex == 0)
/*3114*/            result.add(dataset); 
/*3118*/        } else if (m == axisIndex) {
/*3119*/          result.add(dataset);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3124*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List datasetsMappedToRangeAxis(int index) {
/*3136*/    List<Object> result = new ArrayList();
/*3137*/    for (int i = 0; i < this.datasets.size(); i++) {
/*3138*/      Object dataset = this.datasets.get(i);
/*3139*/      if (dataset != null) {
/*3140*/        Integer m = (Integer)this.datasetToRangeAxisMap.get(i);
/*3141*/        if (m == null) {
/*3143*/          if (index == 0)
/*3144*/            result.add(dataset); 
/*3148*/        } else if (m == index) {
/*3149*/          result.add(dataset);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3154*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWeight() {
/*3166*/    return this.weight;
/*   0*/  }
/*   0*/  
/*   0*/  public void setWeight(int weight) {
/*3177*/    this.weight = weight;
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedDomainAxisSpace() {
/*3189*/    return this.fixedDomainAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedDomainAxisSpace(AxisSpace space) {
/*3200*/    this.fixedDomainAxisSpace = space;
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedRangeAxisSpace() {
/*3212*/    return this.fixedRangeAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedRangeAxisSpace(AxisSpace space) {
/*3223*/    this.fixedRangeAxisSpace = space;
/*   0*/  }
/*   0*/  
/*   0*/  public List getCategories() {
/*3235*/    List<?> result = null;
/*3236*/    if (getDataset() != null)
/*3237*/      result = Collections.unmodifiableList(getDataset().getColumnKeys()); 
/*3239*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public List getCategoriesForAxis(CategoryAxis axis) {
/*3253*/    List<Comparable> result = new ArrayList();
/*3254*/    int axisIndex = this.domainAxes.indexOf(axis);
/*3255*/    List datasets = datasetsMappedToDomainAxis(axisIndex);
/*3256*/    Iterator<CategoryDataset> iterator = datasets.iterator();
/*3257*/    while (iterator.hasNext()) {
/*3258*/      CategoryDataset dataset = iterator.next();
/*3260*/      for (int i = 0; i < dataset.getColumnCount(); i++) {
/*3261*/        Comparable category = dataset.getColumnKey(i);
/*3262*/        if (!result.contains(category))
/*3263*/          result.add(category); 
/*   0*/      } 
/*   0*/    } 
/*3267*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getDrawSharedDomainAxis() {
/*3279*/    return this.drawSharedDomainAxis;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDrawSharedDomainAxis(boolean draw) {
/*3291*/    this.drawSharedDomainAxis = draw;
/*3292*/    notifyListeners(new PlotChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainZoomable() {
/*3304*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeZoomable() {
/*3315*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomDomainAxes(double factor, PlotRenderingInfo state, Point2D source) {}
/*   0*/  
/*   0*/  public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {}
/*   0*/  
/*   0*/  public void zoomRangeAxes(double factor, PlotRenderingInfo state, Point2D source) {
/*3354*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3355*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*3356*/      if (rangeAxis != null)
/*3357*/        rangeAxis.resizeRange(factor); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo state, Point2D source) {
/*3372*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3373*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*3374*/      if (rangeAxis != null)
/*3375*/        rangeAxis.zoomRange(lowerPercent, upperPercent); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getAnchorValue() {
/*3388*/    return this.anchorValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAnchorValue(double value) {
/*3400*/    setAnchorValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setAnchorValue(double value, boolean notify) {
/*3413*/    this.anchorValue = value;
/*3414*/    if (notify)
/*3415*/      notifyListeners(new PlotChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*3428*/    if (obj == this)
/*3429*/      return true; 
/*3431*/    if (!(obj instanceof CategoryPlot))
/*3432*/      return false; 
/*3434*/    if (!super.equals(obj))
/*3435*/      return false; 
/*3438*/    CategoryPlot that = (CategoryPlot)obj;
/*3440*/    if (this.orientation != that.orientation)
/*3441*/      return false; 
/*3443*/    if (!ObjectUtilities.equal(this.axisOffset, that.axisOffset))
/*3444*/      return false; 
/*3446*/    if (!this.domainAxes.equals(that.domainAxes))
/*3447*/      return false; 
/*3449*/    if (!this.domainAxisLocations.equals(that.domainAxisLocations))
/*3450*/      return false; 
/*3452*/    if (this.drawSharedDomainAxis != that.drawSharedDomainAxis)
/*3453*/      return false; 
/*3455*/    if (!this.rangeAxes.equals(that.rangeAxes))
/*3456*/      return false; 
/*3458*/    if (!this.rangeAxisLocations.equals(that.rangeAxisLocations))
/*3459*/      return false; 
/*3461*/    if (!ObjectUtilities.equal(this.datasetToDomainAxisMap, that.datasetToDomainAxisMap))
/*3463*/      return false; 
/*3465*/    if (!ObjectUtilities.equal(this.datasetToRangeAxisMap, that.datasetToRangeAxisMap))
/*3467*/      return false; 
/*3469*/    if (!ObjectUtilities.equal(this.renderers, that.renderers))
/*3470*/      return false; 
/*3472*/    if (this.renderingOrder != that.renderingOrder)
/*3473*/      return false; 
/*3475*/    if (this.columnRenderingOrder != that.columnRenderingOrder)
/*3476*/      return false; 
/*3478*/    if (this.rowRenderingOrder != that.rowRenderingOrder)
/*3479*/      return false; 
/*3481*/    if (this.domainGridlinesVisible != that.domainGridlinesVisible)
/*3482*/      return false; 
/*3484*/    if (this.domainGridlinePosition != that.domainGridlinePosition)
/*3485*/      return false; 
/*3487*/    if (!ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke))
/*3489*/      return false; 
/*3491*/    if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint))
/*3493*/      return false; 
/*3495*/    if (this.rangeGridlinesVisible != that.rangeGridlinesVisible)
/*3496*/      return false; 
/*3498*/    if (!ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke))
/*3500*/      return false; 
/*3502*/    if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint))
/*3504*/      return false; 
/*3506*/    if (this.anchorValue != that.anchorValue)
/*3507*/      return false; 
/*3509*/    if (this.rangeCrosshairVisible != that.rangeCrosshairVisible)
/*3510*/      return false; 
/*3512*/    if (this.rangeCrosshairValue != that.rangeCrosshairValue)
/*3513*/      return false; 
/*3515*/    if (!ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke))
/*3517*/      return false; 
/*3519*/    if (!PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint))
/*3521*/      return false; 
/*3523*/    if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData)
/*3525*/      return false; 
/*3527*/    if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers))
/*3529*/      return false; 
/*3531*/    if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers))
/*3533*/      return false; 
/*3535*/    if (!ObjectUtilities.equal(this.annotations, that.annotations))
/*3536*/      return false; 
/*3538*/    if (this.weight != that.weight)
/*3539*/      return false; 
/*3541*/    if (!ObjectUtilities.equal(this.fixedDomainAxisSpace, that.fixedDomainAxisSpace))
/*3543*/      return false; 
/*3545*/    if (!ObjectUtilities.equal(this.fixedRangeAxisSpace, that.fixedRangeAxisSpace))
/*3547*/      return false; 
/*3550*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*3563*/    CategoryPlot clone = (CategoryPlot)super.clone();
/*3565*/    clone.domainAxes = new ObjectList();
/*3566*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*3567*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
/*3568*/      if (xAxis != null) {
/*3569*/        CategoryAxis clonedAxis = (CategoryAxis)xAxis.clone();
/*3570*/        clone.setDomainAxis(i, clonedAxis);
/*   0*/      } 
/*   0*/    } 
/*3573*/    clone.domainAxisLocations = (ObjectList)this.domainAxisLocations.clone();
/*3576*/    clone.rangeAxes = new ObjectList();
/*3577*/    for (int k = 0; k < this.rangeAxes.size(); k++) {
/*3578*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(k);
/*3579*/      if (yAxis != null) {
/*3580*/        ValueAxis clonedAxis = (ValueAxis)yAxis.clone();
/*3581*/        clone.setRangeAxis(k, clonedAxis);
/*   0*/      } 
/*   0*/    } 
/*3584*/    clone.rangeAxisLocations = (ObjectList)this.rangeAxisLocations.clone();
/*3586*/    clone.datasets = (ObjectList)this.datasets.clone();
/*3587*/    for (int j = 0; j < clone.datasets.size(); j++) {
/*3588*/      CategoryDataset dataset = clone.getDataset(j);
/*3589*/      if (dataset != null)
/*3590*/        dataset.addChangeListener(clone); 
/*   0*/    } 
/*3593*/    clone.datasetToDomainAxisMap = (ObjectList)this.datasetToDomainAxisMap.clone();
/*3595*/    clone.datasetToRangeAxisMap = (ObjectList)this.datasetToRangeAxisMap.clone();
/*3597*/    clone.renderers = (ObjectList)this.renderers.clone();
/*3598*/    if (this.fixedDomainAxisSpace != null)
/*3599*/      clone.fixedDomainAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedDomainAxisSpace); 
/*3602*/    if (this.fixedRangeAxisSpace != null)
/*3603*/      clone.fixedRangeAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedRangeAxisSpace); 
/*3607*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*3619*/    stream.defaultWriteObject();
/*3620*/    SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
/*3621*/    SerialUtilities.writePaint(this.domainGridlinePaint, stream);
/*3622*/    SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
/*3623*/    SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
/*3624*/    SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
/*3625*/    SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*3639*/    stream.defaultReadObject();
/*3640*/    this.domainGridlineStroke = SerialUtilities.readStroke(stream);
/*3641*/    this.domainGridlinePaint = SerialUtilities.readPaint(stream);
/*3642*/    this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
/*3643*/    this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
/*3644*/    this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
/*3645*/    this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
/*3647*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*3648*/      CategoryAxis xAxis = (CategoryAxis)this.domainAxes.get(i);
/*3649*/      if (xAxis != null) {
/*3650*/        xAxis.setPlot(this);
/*3651*/        xAxis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*3654*/    for (int j = 0; j < this.rangeAxes.size(); j++) {
/*3655*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(j);
/*3656*/      if (yAxis != null) {
/*3657*/        yAxis.setPlot(this);
/*3658*/        yAxis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*3661*/    int datasetCount = this.datasets.size();
/*3662*/    for (int k = 0; k < datasetCount; k++) {
/*3663*/      Dataset dataset = (Dataset)this.datasets.get(k);
/*3664*/      if (dataset != null)
/*3665*/        dataset.addChangeListener(this); 
/*   0*/    } 
/*3668*/    int rendererCount = this.renderers.size();
/*3669*/    for (int m = 0; m < rendererCount; m++) {
/*3670*/      CategoryItemRenderer renderer = (CategoryItemRenderer)this.renderers.get(m);
/*3672*/      if (renderer != null)
/*3673*/        renderer.addChangeListener(this); 
/*   0*/    } 
/*   0*/  }
/*   0*/}
