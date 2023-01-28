/*   0*/package org.jfree.chart.plot;
/*   0*/
/*   0*/import java.awt.AlphaComposite;
/*   0*/import java.awt.BasicStroke;
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Composite;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.GeneralPath;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.PathIterator;
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
/*   0*/import java.util.HashSet;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import java.util.ResourceBundle;
/*   0*/import java.util.Set;
/*   0*/import java.util.TreeMap;
/*   0*/import org.jfree.chart.LegendItem;
/*   0*/import org.jfree.chart.LegendItemCollection;
/*   0*/import org.jfree.chart.RenderingSource;
/*   0*/import org.jfree.chart.annotations.XYAnnotation;
/*   0*/import org.jfree.chart.annotations.XYAnnotationBoundsInfo;
/*   0*/import org.jfree.chart.axis.Axis;
/*   0*/import org.jfree.chart.axis.AxisCollection;
/*   0*/import org.jfree.chart.axis.AxisLocation;
/*   0*/import org.jfree.chart.axis.AxisSpace;
/*   0*/import org.jfree.chart.axis.AxisState;
/*   0*/import org.jfree.chart.axis.TickType;
/*   0*/import org.jfree.chart.axis.ValueAxis;
/*   0*/import org.jfree.chart.axis.ValueTick;
/*   0*/import org.jfree.chart.event.ChartChangeEventType;
/*   0*/import org.jfree.chart.event.PlotChangeEvent;
/*   0*/import org.jfree.chart.event.RendererChangeEvent;
/*   0*/import org.jfree.chart.event.RendererChangeListener;
/*   0*/import org.jfree.chart.renderer.RendererUtilities;
/*   0*/import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
/*   0*/import org.jfree.chart.renderer.xy.XYItemRenderer;
/*   0*/import org.jfree.chart.renderer.xy.XYItemRendererState;
/*   0*/import org.jfree.chart.util.Layer;
/*   0*/import org.jfree.chart.util.ObjectList;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.PublicCloneable;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.ResourceBundleWrapper;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/import org.jfree.data.Range;
/*   0*/import org.jfree.data.general.Dataset;
/*   0*/import org.jfree.data.general.DatasetChangeEvent;
/*   0*/import org.jfree.data.general.DatasetUtilities;
/*   0*/import org.jfree.data.xy.AbstractXYDataset;
/*   0*/import org.jfree.data.xy.SelectableXYDataset;
/*   0*/import org.jfree.data.xy.XYDataset;
/*   0*/import org.jfree.data.xy.XYDatasetSelectionState;
/*   0*/
/*   0*/public class XYPlot extends Plot implements ValueAxisPlot, Pannable, Selectable, Zoomable, RendererChangeListener, Cloneable, PublicCloneable, Serializable {
/*   0*/  private static final long serialVersionUID = 7044148245716569264L;
/*   0*/  
/* 321*/  public static final Stroke DEFAULT_GRIDLINE_STROKE = new BasicStroke(0.5F, 0, 2, 0.0F, new float[] { 2.0F, 2.0F }, 0.0F);
/*   0*/  
/* 326*/  public static final Paint DEFAULT_GRIDLINE_PAINT = Color.WHITE;
/*   0*/  
/*   0*/  public static final boolean DEFAULT_CROSSHAIR_VISIBLE = false;
/*   0*/  
/* 332*/  public static final Stroke DEFAULT_CROSSHAIR_STROKE = DEFAULT_GRIDLINE_STROKE;
/*   0*/  
/* 336*/  public static final Paint DEFAULT_CROSSHAIR_PAINT = Color.blue;
/*   0*/  
/* 339*/  protected static ResourceBundle localizationResources = ResourceBundleWrapper.getBundle("org.jfree.chart.plot.LocalizationBundle");
/*   0*/  
/*   0*/  private PlotOrientation orientation;
/*   0*/  
/*   0*/  private RectangleInsets axisOffset;
/*   0*/  
/*   0*/  private ObjectList domainAxes;
/*   0*/  
/*   0*/  private ObjectList domainAxisLocations;
/*   0*/  
/*   0*/  private ObjectList rangeAxes;
/*   0*/  
/*   0*/  private ObjectList rangeAxisLocations;
/*   0*/  
/*   0*/  private ObjectList datasets;
/*   0*/  
/*   0*/  private ObjectList renderers;
/*   0*/  
/*   0*/  private Map datasetToDomainAxesMap;
/*   0*/  
/*   0*/  private Map datasetToRangeAxesMap;
/*   0*/  
/* 388*/  private transient Point2D quadrantOrigin = new Point2D.Double(0.0D, 0.0D);
/*   0*/  
/* 391*/  private transient Paint[] quadrantPaint = new Paint[] { null, null, null, null };
/*   0*/  
/*   0*/  private boolean domainGridlinesVisible;
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
/*   0*/  private boolean domainMinorGridlinesVisible;
/*   0*/  
/*   0*/  private transient Stroke domainMinorGridlineStroke;
/*   0*/  
/*   0*/  private transient Paint domainMinorGridlinePaint;
/*   0*/  
/*   0*/  private boolean rangeMinorGridlinesVisible;
/*   0*/  
/*   0*/  private transient Stroke rangeMinorGridlineStroke;
/*   0*/  
/*   0*/  private transient Paint rangeMinorGridlinePaint;
/*   0*/  
/*   0*/  private boolean domainZeroBaselineVisible;
/*   0*/  
/*   0*/  private transient Stroke domainZeroBaselineStroke;
/*   0*/  
/*   0*/  private transient Paint domainZeroBaselinePaint;
/*   0*/  
/*   0*/  private boolean rangeZeroBaselineVisible;
/*   0*/  
/*   0*/  private transient Stroke rangeZeroBaselineStroke;
/*   0*/  
/*   0*/  private transient Paint rangeZeroBaselinePaint;
/*   0*/  
/*   0*/  private boolean domainCrosshairVisible;
/*   0*/  
/*   0*/  private double domainCrosshairValue;
/*   0*/  
/*   0*/  private transient Stroke domainCrosshairStroke;
/*   0*/  
/*   0*/  private transient Paint domainCrosshairPaint;
/*   0*/  
/*   0*/  private boolean domainCrosshairLockedOnData = true;
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
/*   0*/  private transient Paint domainTickBandPaint;
/*   0*/  
/*   0*/  private transient Paint rangeTickBandPaint;
/*   0*/  
/*   0*/  private AxisSpace fixedDomainAxisSpace;
/*   0*/  
/*   0*/  private AxisSpace fixedRangeAxisSpace;
/*   0*/  
/* 559*/  private DatasetRenderingOrder datasetRenderingOrder = DatasetRenderingOrder.REVERSE;
/*   0*/  
/* 566*/  private SeriesRenderingOrder seriesRenderingOrder = SeriesRenderingOrder.REVERSE;
/*   0*/  
/*   0*/  private int weight;
/*   0*/  
/*   0*/  private LegendItemCollection fixedLegendItems;
/*   0*/  
/*   0*/  private boolean domainPannable;
/*   0*/  
/*   0*/  private boolean rangePannable;
/*   0*/  
/*   0*/  public XYPlot() {
/* 602*/    this(null, null, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public XYPlot(XYDataset dataset, ValueAxis domainAxis, ValueAxis rangeAxis, XYItemRenderer renderer) {
/* 623*/    this.orientation = PlotOrientation.VERTICAL;
/* 624*/    this.weight = 1;
/* 625*/    this.axisOffset = new RectangleInsets(4.0D, 4.0D, 4.0D, 4.0D);
/* 628*/    this.domainAxes = new ObjectList();
/* 629*/    this.domainAxisLocations = new ObjectList();
/* 630*/    this.foregroundDomainMarkers = new HashMap();
/* 631*/    this.backgroundDomainMarkers = new HashMap();
/* 633*/    this.rangeAxes = new ObjectList();
/* 634*/    this.rangeAxisLocations = new ObjectList();
/* 635*/    this.foregroundRangeMarkers = new HashMap();
/* 636*/    this.backgroundRangeMarkers = new HashMap();
/* 638*/    this.datasets = new ObjectList();
/* 639*/    this.renderers = new ObjectList();
/* 641*/    this.datasetToDomainAxesMap = new TreeMap();
/* 642*/    this.datasetToRangeAxesMap = new TreeMap();
/* 644*/    this.annotations = new ArrayList();
/* 646*/    this.datasets.set(0, dataset);
/* 647*/    if (dataset != null)
/* 648*/      dataset.addChangeListener(this); 
/* 651*/    this.renderers.set(0, renderer);
/* 652*/    if (renderer != null) {
/* 653*/      renderer.setPlot(this);
/* 654*/      renderer.addChangeListener(this);
/*   0*/    } 
/* 657*/    this.domainAxes.set(0, domainAxis);
/* 658*/    mapDatasetToDomainAxis(0, 0);
/* 659*/    if (domainAxis != null) {
/* 660*/      domainAxis.setPlot(this);
/* 661*/      domainAxis.addChangeListener(this);
/*   0*/    } 
/* 663*/    this.domainAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);
/* 665*/    this.rangeAxes.set(0, rangeAxis);
/* 666*/    mapDatasetToRangeAxis(0, 0);
/* 667*/    if (rangeAxis != null) {
/* 668*/      rangeAxis.setPlot(this);
/* 669*/      rangeAxis.addChangeListener(this);
/*   0*/    } 
/* 671*/    this.rangeAxisLocations.set(0, AxisLocation.BOTTOM_OR_LEFT);
/* 673*/    configureDomainAxes();
/* 674*/    configureRangeAxes();
/* 676*/    this.domainGridlinesVisible = true;
/* 677*/    this.domainGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 678*/    this.domainGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 680*/    this.domainMinorGridlinesVisible = false;
/* 681*/    this.domainMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 682*/    this.domainMinorGridlinePaint = Color.white;
/* 684*/    this.domainZeroBaselineVisible = false;
/* 685*/    this.domainZeroBaselinePaint = Color.black;
/* 686*/    this.domainZeroBaselineStroke = new BasicStroke(0.5F);
/* 688*/    this.rangeGridlinesVisible = true;
/* 689*/    this.rangeGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 690*/    this.rangeGridlinePaint = DEFAULT_GRIDLINE_PAINT;
/* 692*/    this.rangeMinorGridlinesVisible = false;
/* 693*/    this.rangeMinorGridlineStroke = DEFAULT_GRIDLINE_STROKE;
/* 694*/    this.rangeMinorGridlinePaint = Color.white;
/* 696*/    this.rangeZeroBaselineVisible = false;
/* 697*/    this.rangeZeroBaselinePaint = Color.black;
/* 698*/    this.rangeZeroBaselineStroke = new BasicStroke(0.5F);
/* 700*/    this.domainCrosshairVisible = false;
/* 701*/    this.domainCrosshairValue = 0.0D;
/* 702*/    this.domainCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
/* 703*/    this.domainCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
/* 705*/    this.rangeCrosshairVisible = false;
/* 706*/    this.rangeCrosshairValue = 0.0D;
/* 707*/    this.rangeCrosshairStroke = DEFAULT_CROSSHAIR_STROKE;
/* 708*/    this.rangeCrosshairPaint = DEFAULT_CROSSHAIR_PAINT;
/*   0*/  }
/*   0*/  
/*   0*/  public String getPlotType() {
/* 718*/    return localizationResources.getString("XY_Plot");
/*   0*/  }
/*   0*/  
/*   0*/  public PlotOrientation getOrientation() {
/* 729*/    return this.orientation;
/*   0*/  }
/*   0*/  
/*   0*/  public void setOrientation(PlotOrientation orientation) {
/* 741*/    if (orientation == null)
/* 742*/      throw new IllegalArgumentException("Null 'orientation' argument."); 
/* 744*/    if (orientation != this.orientation) {
/* 745*/      this.orientation = orientation;
/* 746*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getAxisOffset() {
/* 758*/    return this.axisOffset;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAxisOffset(RectangleInsets offset) {
/* 770*/    if (offset == null)
/* 771*/      throw new IllegalArgumentException("Null 'offset' argument."); 
/* 773*/    this.axisOffset = offset;
/* 774*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getDomainAxis() {
/* 788*/    return getDomainAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getDomainAxis(int index) {
/* 801*/    ValueAxis result = null;
/* 802*/    if (index < this.domainAxes.size())
/* 803*/      result = (ValueAxis)this.domainAxes.get(index); 
/* 805*/    if (result == null) {
/* 806*/      Plot parent = getParent();
/* 807*/      if (parent instanceof XYPlot) {
/* 808*/        XYPlot xy = (XYPlot)parent;
/* 809*/        result = xy.getDomainAxis(index);
/*   0*/      } 
/*   0*/    } 
/* 812*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(ValueAxis axis) {
/* 825*/    setDomainAxis(0, axis);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, ValueAxis axis) {
/* 839*/    setDomainAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxis(int index, ValueAxis axis, boolean notify) {
/* 853*/    ValueAxis existing = getDomainAxis(index);
/* 854*/    if (existing != null)
/* 855*/      existing.removeChangeListener(this); 
/* 857*/    if (axis != null)
/* 858*/      axis.setPlot(this); 
/* 860*/    this.domainAxes.set(index, axis);
/* 861*/    if (axis != null) {
/* 862*/      axis.configure();
/* 863*/      axis.addChangeListener(this);
/*   0*/    } 
/* 865*/    if (notify)
/* 866*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxes(ValueAxis[] axes) {
/* 879*/    for (int i = 0; i < axes.length; i++)
/* 880*/      setDomainAxis(i, axes[i], false); 
/* 882*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation() {
/* 893*/    return (AxisLocation)this.domainAxisLocations.get(0);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location) {
/* 906*/    setDomainAxisLocation(0, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(AxisLocation location, boolean notify) {
/* 920*/    setDomainAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge() {
/* 933*/    return Plot.resolveDomainAxisLocation(getDomainAxisLocation(), this.orientation);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisCount() {
/* 945*/    return this.domainAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainAxes() {
/* 955*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 956*/      ValueAxis axis = (ValueAxis)this.domainAxes.get(i);
/* 957*/      if (axis != null)
/* 958*/        axis.removeChangeListener(this); 
/*   0*/    } 
/* 961*/    this.domainAxes.clear();
/* 962*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void configureDomainAxes() {
/* 969*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/* 970*/      ValueAxis axis = (ValueAxis)this.domainAxes.get(i);
/* 971*/      if (axis != null)
/* 972*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getDomainAxisLocation(int index) {
/* 989*/    AxisLocation result = null;
/* 990*/    if (index < this.domainAxisLocations.size())
/* 991*/      result = (AxisLocation)this.domainAxisLocations.get(index); 
/* 993*/    if (result == null)
/* 994*/      result = AxisLocation.getOpposite(getDomainAxisLocation()); 
/* 996*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location) {
/*1011*/    setDomainAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainAxisLocation(int index, AxisLocation location, boolean notify) {
/*1031*/    if (index == 0 && location == null)
/*1032*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/*1035*/    this.domainAxisLocations.set(index, location);
/*1036*/    if (notify)
/*1037*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getDomainAxisEdge(int index) {
/*1051*/    AxisLocation location = getDomainAxisLocation(index);
/*1052*/    RectangleEdge result = Plot.resolveDomainAxisLocation(location, this.orientation);
/*1054*/    if (result == null)
/*1055*/      result = RectangleEdge.opposite(getDomainAxisEdge()); 
/*1057*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis() {
/*1071*/    return getRangeAxis(0);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(ValueAxis axis) {
/*1085*/    if (axis != null)
/*1086*/      axis.setPlot(this); 
/*1090*/    ValueAxis existing = getRangeAxis();
/*1091*/    if (existing != null)
/*1092*/      existing.removeChangeListener(this); 
/*1095*/    this.rangeAxes.set(0, axis);
/*1096*/    if (axis != null) {
/*1097*/      axis.configure();
/*1098*/      axis.addChangeListener(this);
/*   0*/    } 
/*1100*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation() {
/*1112*/    return (AxisLocation)this.rangeAxisLocations.get(0);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location) {
/*1125*/    setRangeAxisLocation(0, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(AxisLocation location, boolean notify) {
/*1139*/    setRangeAxisLocation(0, location, notify);
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge() {
/*1151*/    return Plot.resolveRangeAxisLocation(getRangeAxisLocation(), this.orientation);
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxis(int index) {
/*1165*/    ValueAxis result = null;
/*1166*/    if (index < this.rangeAxes.size())
/*1167*/      result = (ValueAxis)this.rangeAxes.get(index); 
/*1169*/    if (result == null) {
/*1170*/      Plot parent = getParent();
/*1171*/      if (parent instanceof XYPlot) {
/*1172*/        XYPlot xy = (XYPlot)parent;
/*1173*/        result = xy.getRangeAxis(index);
/*   0*/      } 
/*   0*/    } 
/*1176*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis) {
/*1189*/    setRangeAxis(index, axis, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxis(int index, ValueAxis axis, boolean notify) {
/*1203*/    ValueAxis existing = getRangeAxis(index);
/*1204*/    if (existing != null)
/*1205*/      existing.removeChangeListener(this); 
/*1207*/    if (axis != null)
/*1208*/      axis.setPlot(this); 
/*1210*/    this.rangeAxes.set(index, axis);
/*1211*/    if (axis != null) {
/*1212*/      axis.configure();
/*1213*/      axis.addChangeListener(this);
/*   0*/    } 
/*1215*/    if (notify)
/*1216*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxes(ValueAxis[] axes) {
/*1229*/    for (int i = 0; i < axes.length; i++)
/*1230*/      setRangeAxis(i, axes[i], false); 
/*1232*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public int getRangeAxisCount() {
/*1243*/    return this.rangeAxes.size();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeAxes() {
/*1253*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1254*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1255*/      if (axis != null)
/*1256*/        axis.removeChangeListener(this); 
/*   0*/    } 
/*1259*/    this.rangeAxes.clear();
/*1260*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void configureRangeAxes() {
/*1269*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*1270*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(i);
/*1271*/      if (axis != null)
/*1272*/        axis.configure(); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public AxisLocation getRangeAxisLocation(int index) {
/*1289*/    AxisLocation result = null;
/*1290*/    if (index < this.rangeAxisLocations.size())
/*1291*/      result = (AxisLocation)this.rangeAxisLocations.get(index); 
/*1293*/    if (result == null)
/*1294*/      result = AxisLocation.getOpposite(getRangeAxisLocation()); 
/*1296*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location) {
/*1310*/    setRangeAxisLocation(index, location, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeAxisLocation(int index, AxisLocation location, boolean notify) {
/*1330*/    if (index == 0 && location == null)
/*1331*/      throw new IllegalArgumentException("Null 'location' for index 0 not permitted."); 
/*1334*/    this.rangeAxisLocations.set(index, location);
/*1335*/    if (notify)
/*1336*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleEdge getRangeAxisEdge(int index) {
/*1351*/    AxisLocation location = getRangeAxisLocation(index);
/*1352*/    RectangleEdge result = Plot.resolveRangeAxisLocation(location, this.orientation);
/*1354*/    if (result == null)
/*1355*/      result = RectangleEdge.opposite(getRangeAxisEdge()); 
/*1357*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataset getDataset() {
/*1369*/    return getDataset(0);
/*   0*/  }
/*   0*/  
/*   0*/  public XYDataset getDataset(int index) {
/*1382*/    XYDataset result = null;
/*1383*/    if (this.datasets.size() > index)
/*1384*/      result = (XYDataset)this.datasets.get(index); 
/*1386*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(XYDataset dataset) {
/*1399*/    setDataset(0, dataset);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(int index, XYDataset dataset) {
/*1411*/    XYDataset existing = getDataset(index);
/*1412*/    if (existing != null)
/*1413*/      existing.removeChangeListener(this); 
/*1415*/    this.datasets.set(index, dataset);
/*1416*/    if (dataset != null)
/*1417*/      dataset.addChangeListener(this); 
/*1421*/    DatasetChangeEvent event = new DatasetChangeEvent(this, dataset);
/*1422*/    datasetChanged(event);
/*   0*/  }
/*   0*/  
/*   0*/  public int getDatasetCount() {
/*1431*/    return this.datasets.size();
/*   0*/  }
/*   0*/  
/*   0*/  public int indexOf(XYDataset dataset) {
/*1443*/    int result = -1;
/*1444*/    for (int i = 0; i < this.datasets.size(); i++) {
/*1445*/      if (dataset == this.datasets.get(i)) {
/*1446*/        result = i;
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1450*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToDomainAxis(int index, int axisIndex) {
/*1463*/    List<Integer> axisIndices = new ArrayList(1);
/*1464*/    axisIndices.add(new Integer(axisIndex));
/*1465*/    mapDatasetToDomainAxes(index, axisIndices);
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToDomainAxes(int index, List axisIndices) {
/*1479*/    if (index < 0)
/*1480*/      throw new IllegalArgumentException("Requires 'index' >= 0."); 
/*1482*/    checkAxisIndices(axisIndices);
/*1483*/    Integer key = new Integer(index);
/*1484*/    this.datasetToDomainAxesMap.put(key, new ArrayList(axisIndices));
/*1486*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToRangeAxis(int index, int axisIndex) {
/*1499*/    List<Integer> axisIndices = new ArrayList(1);
/*1500*/    axisIndices.add(new Integer(axisIndex));
/*1501*/    mapDatasetToRangeAxes(index, axisIndices);
/*   0*/  }
/*   0*/  
/*   0*/  public void mapDatasetToRangeAxes(int index, List axisIndices) {
/*1515*/    if (index < 0)
/*1516*/      throw new IllegalArgumentException("Requires 'index' >= 0."); 
/*1518*/    checkAxisIndices(axisIndices);
/*1519*/    Integer key = new Integer(index);
/*1520*/    this.datasetToRangeAxesMap.put(key, new ArrayList(axisIndices));
/*1522*/    datasetChanged(new DatasetChangeEvent(this, getDataset(index)));
/*   0*/  }
/*   0*/  
/*   0*/  private void checkAxisIndices(List indices) {
/*1536*/    if (indices == null)
/*   0*/      return; 
/*1539*/    int count = indices.size();
/*1540*/    if (count == 0)
/*1541*/      throw new IllegalArgumentException("Empty list not permitted."); 
/*1543*/    HashSet<Object> set = new HashSet();
/*1544*/    for (int i = 0; i < count; i++) {
/*1545*/      Object item = indices.get(i);
/*1546*/      if (!(item instanceof Integer))
/*1547*/        throw new IllegalArgumentException("Indices must be Integer instances."); 
/*1550*/      if (set.contains(item))
/*1551*/        throw new IllegalArgumentException("Indices must be unique."); 
/*1553*/      set.add(item);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getRendererCount() {
/*1565*/    return this.renderers.size();
/*   0*/  }
/*   0*/  
/*   0*/  public XYItemRenderer getRenderer() {
/*1576*/    return getRenderer(0);
/*   0*/  }
/*   0*/  
/*   0*/  public XYItemRenderer getRenderer(int index) {
/*1589*/    XYItemRenderer result = null;
/*1590*/    if (this.renderers.size() > index)
/*1591*/      result = (XYItemRenderer)this.renderers.get(index); 
/*1593*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(XYItemRenderer renderer) {
/*1607*/    setRenderer(0, renderer);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, XYItemRenderer renderer) {
/*1620*/    setRenderer(index, renderer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderer(int index, XYItemRenderer renderer, boolean notify) {
/*1635*/    XYItemRenderer existing = getRenderer(index);
/*1636*/    if (existing != null)
/*1637*/      existing.removeChangeListener(this); 
/*1639*/    this.renderers.set(index, renderer);
/*1640*/    if (renderer != null) {
/*1641*/      renderer.setPlot(this);
/*1642*/      renderer.addChangeListener(this);
/*   0*/    } 
/*1644*/    configureDomainAxes();
/*1645*/    configureRangeAxes();
/*1646*/    if (notify)
/*1647*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderers(XYItemRenderer[] renderers) {
/*1658*/    for (int i = 0; i < renderers.length; i++)
/*1659*/      setRenderer(i, renderers[i], false); 
/*1661*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public DatasetRenderingOrder getDatasetRenderingOrder() {
/*1672*/    return this.datasetRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDatasetRenderingOrder(DatasetRenderingOrder order) {
/*1686*/    if (order == null)
/*1687*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1689*/    this.datasetRenderingOrder = order;
/*1690*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public SeriesRenderingOrder getSeriesRenderingOrder() {
/*1701*/    return this.seriesRenderingOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setSeriesRenderingOrder(SeriesRenderingOrder order) {
/*1715*/    if (order == null)
/*1716*/      throw new IllegalArgumentException("Null 'order' argument."); 
/*1718*/    this.seriesRenderingOrder = order;
/*1719*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public int getIndexOf(XYItemRenderer renderer) {
/*1731*/    return this.renderers.indexOf(renderer);
/*   0*/  }
/*   0*/  
/*   0*/  public XYItemRenderer getRendererForDataset(XYDataset dataset) {
/*1744*/    XYItemRenderer result = null;
/*1745*/    for (int i = 0; i < this.datasets.size(); i++) {
/*1746*/      if (this.datasets.get(i) == dataset) {
/*1747*/        result = (XYItemRenderer)this.renderers.get(i);
/*1748*/        if (result == null)
/*1749*/          result = getRenderer(); 
/*   0*/        break;
/*   0*/      } 
/*   0*/    } 
/*1754*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getWeight() {
/*1766*/    return this.weight;
/*   0*/  }
/*   0*/  
/*   0*/  public void setWeight(int weight) {
/*1778*/    this.weight = weight;
/*1779*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainGridlinesVisible() {
/*1791*/    return this.domainGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinesVisible(boolean visible) {
/*1806*/    if (this.domainGridlinesVisible != visible) {
/*1807*/      this.domainGridlinesVisible = visible;
/*1808*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainMinorGridlinesVisible() {
/*1823*/    return this.domainMinorGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainMinorGridlinesVisible(boolean visible) {
/*1840*/    if (this.domainMinorGridlinesVisible != visible) {
/*1841*/      this.domainMinorGridlinesVisible = visible;
/*1842*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainGridlineStroke() {
/*1855*/    return this.domainGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlineStroke(Stroke stroke) {
/*1870*/    if (stroke == null)
/*1871*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*1873*/    this.domainGridlineStroke = stroke;
/*1874*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainMinorGridlineStroke() {
/*1889*/    return this.domainMinorGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainMinorGridlineStroke(Stroke stroke) {
/*1906*/    if (stroke == null)
/*1907*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*1909*/    this.domainMinorGridlineStroke = stroke;
/*1910*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainGridlinePaint() {
/*1922*/    return this.domainGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainGridlinePaint(Paint paint) {
/*1937*/    if (paint == null)
/*1938*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1940*/    this.domainGridlinePaint = paint;
/*1941*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainMinorGridlinePaint() {
/*1955*/    return this.domainMinorGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainMinorGridlinePaint(Paint paint) {
/*1972*/    if (paint == null)
/*1973*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*1975*/    this.domainMinorGridlinePaint = paint;
/*1976*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeGridlinesVisible() {
/*1988*/    return this.rangeGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinesVisible(boolean visible) {
/*2003*/    if (this.rangeGridlinesVisible != visible) {
/*2004*/      this.rangeGridlinesVisible = visible;
/*2005*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeGridlineStroke() {
/*2018*/    return this.rangeGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlineStroke(Stroke stroke) {
/*2030*/    if (stroke == null)
/*2031*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2033*/    this.rangeGridlineStroke = stroke;
/*2034*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeGridlinePaint() {
/*2046*/    return this.rangeGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeGridlinePaint(Paint paint) {
/*2058*/    if (paint == null)
/*2059*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2061*/    this.rangeGridlinePaint = paint;
/*2062*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeMinorGridlinesVisible() {
/*2076*/    return this.rangeMinorGridlinesVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeMinorGridlinesVisible(boolean visible) {
/*2093*/    if (this.rangeMinorGridlinesVisible != visible) {
/*2094*/      this.rangeMinorGridlinesVisible = visible;
/*2095*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeMinorGridlineStroke() {
/*2110*/    return this.rangeMinorGridlineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeMinorGridlineStroke(Stroke stroke) {
/*2124*/    if (stroke == null)
/*2125*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2127*/    this.rangeMinorGridlineStroke = stroke;
/*2128*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeMinorGridlinePaint() {
/*2142*/    return this.rangeMinorGridlinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeMinorGridlinePaint(Paint paint) {
/*2156*/    if (paint == null)
/*2157*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2159*/    this.rangeMinorGridlinePaint = paint;
/*2160*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainZeroBaselineVisible() {
/*2174*/    return this.domainZeroBaselineVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainZeroBaselineVisible(boolean visible) {
/*2189*/    this.domainZeroBaselineVisible = visible;
/*2190*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainZeroBaselineStroke() {
/*2203*/    return this.domainZeroBaselineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainZeroBaselineStroke(Stroke stroke) {
/*2217*/    if (stroke == null)
/*2218*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2220*/    this.domainZeroBaselineStroke = stroke;
/*2221*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainZeroBaselinePaint() {
/*2235*/    return this.domainZeroBaselinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainZeroBaselinePaint(Paint paint) {
/*2249*/    if (paint == null)
/*2250*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2252*/    this.domainZeroBaselinePaint = paint;
/*2253*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeZeroBaselineVisible() {
/*2265*/    return this.rangeZeroBaselineVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeZeroBaselineVisible(boolean visible) {
/*2278*/    this.rangeZeroBaselineVisible = visible;
/*2279*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeZeroBaselineStroke() {
/*2290*/    return this.rangeZeroBaselineStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeZeroBaselineStroke(Stroke stroke) {
/*2302*/    if (stroke == null)
/*2303*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*2305*/    this.rangeZeroBaselineStroke = stroke;
/*2306*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeZeroBaselinePaint() {
/*2318*/    return this.rangeZeroBaselinePaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeZeroBaselinePaint(Paint paint) {
/*2330*/    if (paint == null)
/*2331*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*2333*/    this.rangeZeroBaselinePaint = paint;
/*2334*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainTickBandPaint() {
/*2346*/    return this.domainTickBandPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainTickBandPaint(Paint paint) {
/*2357*/    this.domainTickBandPaint = paint;
/*2358*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeTickBandPaint() {
/*2370*/    return this.rangeTickBandPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeTickBandPaint(Paint paint) {
/*2381*/    this.rangeTickBandPaint = paint;
/*2382*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Point2D getQuadrantOrigin() {
/*2394*/    return this.quadrantOrigin;
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuadrantOrigin(Point2D origin) {
/*2406*/    if (origin == null)
/*2407*/      throw new IllegalArgumentException("Null 'origin' argument."); 
/*2409*/    this.quadrantOrigin = origin;
/*2410*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getQuadrantPaint(int index) {
/*2423*/    if (index < 0 || index > 3)
/*2424*/      throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3."); 
/*2427*/    return this.quadrantPaint[index];
/*   0*/  }
/*   0*/  
/*   0*/  public void setQuadrantPaint(int index, Paint paint) {
/*2440*/    if (index < 0 || index > 3)
/*2441*/      throw new IllegalArgumentException("The index value (" + index + ") should be in the range 0 to 3."); 
/*2444*/    this.quadrantPaint[index] = paint;
/*2445*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(Marker marker) {
/*2462*/    addDomainMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(Marker marker, Layer layer) {
/*2478*/    addDomainMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers() {
/*2488*/    if (this.backgroundDomainMarkers != null) {
/*2489*/      Set keys = this.backgroundDomainMarkers.keySet();
/*2490*/      Iterator<Integer> iterator = keys.iterator();
/*2491*/      while (iterator.hasNext()) {
/*2492*/        Integer key = iterator.next();
/*2493*/        clearDomainMarkers(key);
/*   0*/      } 
/*2495*/      this.backgroundDomainMarkers.clear();
/*   0*/    } 
/*2497*/    if (this.foregroundDomainMarkers != null) {
/*2498*/      Set keys = this.foregroundDomainMarkers.keySet();
/*2499*/      Iterator<Integer> iterator = keys.iterator();
/*2500*/      while (iterator.hasNext()) {
/*2501*/        Integer key = iterator.next();
/*2502*/        clearDomainMarkers(key);
/*   0*/      } 
/*2504*/      this.foregroundDomainMarkers.clear();
/*   0*/    } 
/*2506*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearDomainMarkers(int index) {
/*2518*/    Integer key = new Integer(index);
/*2519*/    if (this.backgroundDomainMarkers != null) {
/*2520*/      Collection markers = (Collection)this.backgroundDomainMarkers.get(key);
/*2522*/      if (markers != null) {
/*2523*/        Iterator<Marker> iterator = markers.iterator();
/*2524*/        while (iterator.hasNext()) {
/*2525*/          Marker m = iterator.next();
/*2526*/          m.removeChangeListener(this);
/*   0*/        } 
/*2528*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2531*/    if (this.foregroundRangeMarkers != null) {
/*2532*/      Collection markers = (Collection)this.foregroundDomainMarkers.get(key);
/*2534*/      if (markers != null) {
/*2535*/        Iterator<Marker> iterator = markers.iterator();
/*2536*/        while (iterator.hasNext()) {
/*2537*/          Marker m = iterator.next();
/*2538*/          m.removeChangeListener(this);
/*   0*/        } 
/*2540*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2543*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(int index, Marker marker, Layer layer) {
/*2562*/    addDomainMarker(index, marker, layer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void addDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
/*2582*/    if (marker == null)
/*2583*/      throw new IllegalArgumentException("Null 'marker' not permitted."); 
/*2585*/    if (layer == null)
/*2586*/      throw new IllegalArgumentException("Null 'layer' not permitted."); 
/*2589*/    if (layer == Layer.FOREGROUND) {
/*2590*/      Collection<Marker> markers = (Collection)this.foregroundDomainMarkers.get(new Integer(index));
/*2592*/      if (markers == null) {
/*2593*/        markers = new ArrayList();
/*2594*/        this.foregroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2596*/      markers.add(marker);
/*2598*/    } else if (layer == Layer.BACKGROUND) {
/*2599*/      Collection<Marker> markers = (Collection)this.backgroundDomainMarkers.get(new Integer(index));
/*2601*/      if (markers == null) {
/*2602*/        markers = new ArrayList();
/*2603*/        this.backgroundDomainMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2605*/      markers.add(marker);
/*   0*/    } 
/*2607*/    marker.addChangeListener(this);
/*2608*/    if (notify)
/*2609*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeDomainMarker(Marker marker) {
/*2625*/    return removeDomainMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeDomainMarker(Marker marker, Layer layer) {
/*2641*/    return removeDomainMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeDomainMarker(int index, Marker marker, Layer layer) {
/*2658*/    return removeDomainMarker(index, marker, layer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeDomainMarker(int index, Marker marker, Layer layer, boolean notify) {
/*   0*/    ArrayList markers;
/*2678*/    if (layer == Layer.FOREGROUND) {
/*2679*/      markers = (ArrayList)this.foregroundDomainMarkers.get(new Integer(index));
/*   0*/    } else {
/*2683*/      markers = (ArrayList)this.backgroundDomainMarkers.get(new Integer(index));
/*   0*/    } 
/*2686*/    if (markers == null)
/*2687*/      return false; 
/*2689*/    boolean removed = markers.remove(marker);
/*2690*/    if (removed && notify)
/*2691*/      fireChangeEvent(); 
/*2693*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker) {
/*2708*/    addRangeMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(Marker marker, Layer layer) {
/*2724*/    addRangeMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers() {
/*2734*/    if (this.backgroundRangeMarkers != null) {
/*2735*/      Set keys = this.backgroundRangeMarkers.keySet();
/*2736*/      Iterator<Integer> iterator = keys.iterator();
/*2737*/      while (iterator.hasNext()) {
/*2738*/        Integer key = iterator.next();
/*2739*/        clearRangeMarkers(key);
/*   0*/      } 
/*2741*/      this.backgroundRangeMarkers.clear();
/*   0*/    } 
/*2743*/    if (this.foregroundRangeMarkers != null) {
/*2744*/      Set keys = this.foregroundRangeMarkers.keySet();
/*2745*/      Iterator<Integer> iterator = keys.iterator();
/*2746*/      while (iterator.hasNext()) {
/*2747*/        Integer key = iterator.next();
/*2748*/        clearRangeMarkers(key);
/*   0*/      } 
/*2750*/      this.foregroundRangeMarkers.clear();
/*   0*/    } 
/*2752*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(int index, Marker marker, Layer layer) {
/*2770*/    addRangeMarker(index, marker, layer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void addRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
/*2790*/    if (layer == Layer.FOREGROUND) {
/*2791*/      Collection<Marker> markers = (Collection)this.foregroundRangeMarkers.get(new Integer(index));
/*2793*/      if (markers == null) {
/*2794*/        markers = new ArrayList();
/*2795*/        this.foregroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2797*/      markers.add(marker);
/*2799*/    } else if (layer == Layer.BACKGROUND) {
/*2800*/      Collection<Marker> markers = (Collection)this.backgroundRangeMarkers.get(new Integer(index));
/*2802*/      if (markers == null) {
/*2803*/        markers = new ArrayList();
/*2804*/        this.backgroundRangeMarkers.put(new Integer(index), markers);
/*   0*/      } 
/*2806*/      markers.add(marker);
/*   0*/    } 
/*2808*/    marker.addChangeListener(this);
/*2809*/    if (notify)
/*2810*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public void clearRangeMarkers(int index) {
/*2821*/    Integer key = new Integer(index);
/*2822*/    if (this.backgroundRangeMarkers != null) {
/*2823*/      Collection markers = (Collection)this.backgroundRangeMarkers.get(key);
/*2825*/      if (markers != null) {
/*2826*/        Iterator<Marker> iterator = markers.iterator();
/*2827*/        while (iterator.hasNext()) {
/*2828*/          Marker m = iterator.next();
/*2829*/          m.removeChangeListener(this);
/*   0*/        } 
/*2831*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2834*/    if (this.foregroundRangeMarkers != null) {
/*2835*/      Collection markers = (Collection)this.foregroundRangeMarkers.get(key);
/*2837*/      if (markers != null) {
/*2838*/        Iterator<Marker> iterator = markers.iterator();
/*2839*/        while (iterator.hasNext()) {
/*2840*/          Marker m = iterator.next();
/*2841*/          m.removeChangeListener(this);
/*   0*/        } 
/*2843*/        markers.clear();
/*   0*/      } 
/*   0*/    } 
/*2846*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeRangeMarker(Marker marker) {
/*2861*/    return removeRangeMarker(marker, Layer.FOREGROUND);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeRangeMarker(Marker marker, Layer layer) {
/*2877*/    return removeRangeMarker(0, marker, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeRangeMarker(int index, Marker marker, Layer layer) {
/*2894*/    return removeRangeMarker(index, marker, layer, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeRangeMarker(int index, Marker marker, Layer layer, boolean notify) {
/*   0*/    ArrayList markers;
/*2913*/    if (marker == null)
/*2914*/      throw new IllegalArgumentException("Null 'marker' argument."); 
/*2917*/    if (layer == Layer.FOREGROUND) {
/*2918*/      markers = (ArrayList)this.foregroundRangeMarkers.get(new Integer(index));
/*   0*/    } else {
/*2922*/      markers = (ArrayList)this.backgroundRangeMarkers.get(new Integer(index));
/*   0*/    } 
/*2925*/    if (markers == null)
/*2926*/      return false; 
/*2928*/    boolean removed = markers.remove(marker);
/*2929*/    if (removed && notify)
/*2930*/      fireChangeEvent(); 
/*2932*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(XYAnnotation annotation) {
/*2945*/    addAnnotation(annotation, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void addAnnotation(XYAnnotation annotation, boolean notify) {
/*2958*/    if (annotation == null)
/*2959*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2961*/    this.annotations.add(annotation);
/*2962*/    if (notify)
/*2963*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(XYAnnotation annotation) {
/*2979*/    return removeAnnotation(annotation, true);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean removeAnnotation(XYAnnotation annotation, boolean notify) {
/*2994*/    if (annotation == null)
/*2995*/      throw new IllegalArgumentException("Null 'annotation' argument."); 
/*2997*/    boolean removed = this.annotations.remove(annotation);
/*2998*/    if (removed && notify)
/*2999*/      fireChangeEvent(); 
/*3001*/    return removed;
/*   0*/  }
/*   0*/  
/*   0*/  public List getAnnotations() {
/*3014*/    return new ArrayList(this.annotations);
/*   0*/  }
/*   0*/  
/*   0*/  public void clearAnnotations() {
/*3024*/    this.annotations.clear();
/*3025*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateAxisSpace(Graphics2D g2, Rectangle2D plotArea) {
/*3038*/    AxisSpace space = new AxisSpace();
/*3039*/    space = calculateRangeAxisSpace(g2, plotArea, space);
/*3040*/    Rectangle2D revPlotArea = space.shrink(plotArea, null);
/*3041*/    space = calculateDomainAxisSpace(g2, revPlotArea, space);
/*3042*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateDomainAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*3058*/    if (space == null)
/*3059*/      space = new AxisSpace(); 
/*3063*/    if (this.fixedDomainAxisSpace != null) {
/*3064*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3065*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getLeft(), RectangleEdge.LEFT);
/*3067*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getRight(), RectangleEdge.RIGHT);
/*3070*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*3071*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getTop(), RectangleEdge.TOP);
/*3073*/        space.ensureAtLeast(this.fixedDomainAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*   0*/      } 
/*   0*/    } else {
/*3079*/      for (int i = 0; i < this.domainAxes.size(); i++) {
/*3080*/        Axis axis = (Axis)this.domainAxes.get(i);
/*3081*/        if (axis != null) {
/*3082*/          RectangleEdge edge = getDomainAxisEdge(i);
/*3083*/          space = axis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3088*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  protected AxisSpace calculateRangeAxisSpace(Graphics2D g2, Rectangle2D plotArea, AxisSpace space) {
/*3105*/    if (space == null)
/*3106*/      space = new AxisSpace(); 
/*3110*/    if (this.fixedRangeAxisSpace != null) {
/*3111*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3112*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getTop(), RectangleEdge.TOP);
/*3114*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getBottom(), RectangleEdge.BOTTOM);
/*3117*/      } else if (this.orientation == PlotOrientation.VERTICAL) {
/*3118*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getLeft(), RectangleEdge.LEFT);
/*3120*/        space.ensureAtLeast(this.fixedRangeAxisSpace.getRight(), RectangleEdge.RIGHT);
/*   0*/      } 
/*   0*/    } else {
/*3126*/      for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3127*/        Axis axis = (Axis)this.rangeAxes.get(i);
/*3128*/        if (axis != null) {
/*3129*/          RectangleEdge edge = getRangeAxisEdge(i);
/*3130*/          space = axis.reserveSpace(g2, this, plotArea, edge, space);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3134*/    return space;
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
/*3154*/    boolean b1 = (area.getWidth() <= 10.0D);
/*3155*/    boolean b2 = (area.getHeight() <= 10.0D);
/*3156*/    if (b1 || b2)
/*   0*/      return; 
/*3161*/    if (info != null)
/*3162*/      info.setPlotArea(area); 
/*3166*/    RectangleInsets insets = getInsets();
/*3167*/    insets.trim(area);
/*3169*/    AxisSpace space = calculateAxisSpace(g2, area);
/*3170*/    Rectangle2D dataArea = space.shrink(area, null);
/*3171*/    this.axisOffset.trim(dataArea);
/*3172*/    createAndAddEntity((Rectangle2D)dataArea.clone(), info, null, null);
/*3173*/    if (info != null)
/*3174*/      info.setDataArea(dataArea); 
/*3178*/    drawBackground(g2, dataArea);
/*3179*/    Map axisStateMap = drawAxes(g2, area, dataArea, info);
/*3181*/    PlotOrientation orient = getOrientation();
/*3185*/    if (anchor != null && !dataArea.contains(anchor))
/*3186*/      anchor = null; 
/*3188*/    CrosshairState crosshairState = new CrosshairState();
/*3189*/    crosshairState.setCrosshairDistance(Double.POSITIVE_INFINITY);
/*3190*/    crosshairState.setAnchor(anchor);
/*3192*/    crosshairState.setAnchorX(Double.NaN);
/*3193*/    crosshairState.setAnchorY(Double.NaN);
/*3194*/    if (anchor != null) {
/*3195*/      ValueAxis domainAxis = getDomainAxis();
/*3196*/      if (domainAxis != null) {
/*   0*/        double x;
/*3198*/        if (orient == PlotOrientation.VERTICAL) {
/*3199*/          x = domainAxis.java2DToValue(anchor.getX(), dataArea, getDomainAxisEdge());
/*   0*/        } else {
/*3203*/          x = domainAxis.java2DToValue(anchor.getY(), dataArea, getDomainAxisEdge());
/*   0*/        } 
/*3206*/        crosshairState.setAnchorX(x);
/*   0*/      } 
/*3208*/      ValueAxis rangeAxis = getRangeAxis();
/*3209*/      if (rangeAxis != null) {
/*   0*/        double y;
/*3211*/        if (orient == PlotOrientation.VERTICAL) {
/*3212*/          y = rangeAxis.java2DToValue(anchor.getY(), dataArea, getRangeAxisEdge());
/*   0*/        } else {
/*3216*/          y = rangeAxis.java2DToValue(anchor.getX(), dataArea, getRangeAxisEdge());
/*   0*/        } 
/*3219*/        crosshairState.setAnchorY(y);
/*   0*/      } 
/*   0*/    } 
/*3222*/    crosshairState.setCrosshairX(getDomainCrosshairValue());
/*3223*/    crosshairState.setCrosshairY(getRangeCrosshairValue());
/*3224*/    Shape originalClip = g2.getClip();
/*3225*/    Composite originalComposite = g2.getComposite();
/*3227*/    g2.clip(dataArea);
/*3228*/    g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
/*3231*/    AxisState domainAxisState = (AxisState)axisStateMap.get(getDomainAxis());
/*3233*/    if (domainAxisState == null && 
/*3234*/      parentState != null)
/*3235*/      domainAxisState = (AxisState)parentState.getSharedAxisStates().get(getDomainAxis()); 
/*3240*/    AxisState rangeAxisState = (AxisState)axisStateMap.get(getRangeAxis());
/*3241*/    if (rangeAxisState == null && 
/*3242*/      parentState != null)
/*3243*/      rangeAxisState = (AxisState)parentState.getSharedAxisStates().get(getRangeAxis()); 
/*3247*/    if (domainAxisState != null)
/*3248*/      drawDomainTickBands(g2, dataArea, domainAxisState.getTicks()); 
/*3250*/    if (rangeAxisState != null)
/*3251*/      drawRangeTickBands(g2, dataArea, rangeAxisState.getTicks()); 
/*3253*/    if (domainAxisState != null) {
/*3254*/      drawDomainGridlines(g2, dataArea, domainAxisState.getTicks());
/*3255*/      drawZeroDomainBaseline(g2, dataArea);
/*   0*/    } 
/*3257*/    if (rangeAxisState != null) {
/*3258*/      drawRangeGridlines(g2, dataArea, rangeAxisState.getTicks());
/*3259*/      drawZeroRangeBaseline(g2, dataArea);
/*   0*/    } 
/*3263*/    for (int i = 0; i < this.renderers.size(); i++)
/*3264*/      drawDomainMarkers(g2, dataArea, i, Layer.BACKGROUND); 
/*3266*/    for (int j = 0; j < this.renderers.size(); j++)
/*3267*/      drawRangeMarkers(g2, dataArea, j, Layer.BACKGROUND); 
/*   0*/    boolean foundData = false;
/*3272*/    DatasetRenderingOrder order = getDatasetRenderingOrder();
/*3273*/    if (order == DatasetRenderingOrder.FORWARD) {
/*3276*/      int rendererCount = this.renderers.size();
/*3277*/      for (int i2 = 0; i2 < rendererCount; i2++) {
/*3278*/        XYItemRenderer r = getRenderer(i2);
/*3279*/        if (r != null) {
/*3280*/          ValueAxis domainAxis = getDomainAxisForDataset(i2);
/*3281*/          ValueAxis rangeAxis = getRangeAxisForDataset(i2);
/*3282*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
/*   0*/        } 
/*   0*/      } 
/*3288*/      for (int i1 = 0; i1 < getDatasetCount(); i1++)
/*3289*/        foundData = (render(g2, dataArea, i1, info, crosshairState) || foundData); 
/*3294*/      for (int n = 0; n < rendererCount; n++) {
/*3295*/        XYItemRenderer r = getRenderer(n);
/*3296*/        if (r != null) {
/*3297*/          ValueAxis domainAxis = getDomainAxisForDataset(n);
/*3298*/          ValueAxis rangeAxis = getRangeAxisForDataset(n);
/*3299*/          r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
/*   0*/        } 
/*   0*/      } 
/*3305*/    } else if (order == DatasetRenderingOrder.REVERSE) {
/*3308*/      int rendererCount = this.renderers.size();
/*3309*/      for (int i2 = rendererCount - 1; i2 >= 0; i2--) {
/*3310*/        XYItemRenderer r = getRenderer(i2);
/*3311*/        if (i2 < getDatasetCount())
/*3314*/          if (r != null) {
/*3315*/            ValueAxis domainAxis = getDomainAxisForDataset(i2);
/*3316*/            ValueAxis rangeAxis = getRangeAxisForDataset(i2);
/*3317*/            r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.BACKGROUND, info);
/*   0*/          }  
/*   0*/      } 
/*3322*/      for (int i1 = getDatasetCount() - 1; i1 >= 0; i1--)
/*3323*/        foundData = (render(g2, dataArea, i1, info, crosshairState) || foundData); 
/*3328*/      for (int n = rendererCount - 1; n >= 0; n--) {
/*3329*/        XYItemRenderer r = getRenderer(n);
/*3330*/        if (n < getDatasetCount())
/*3333*/          if (r != null) {
/*3334*/            ValueAxis domainAxis = getDomainAxisForDataset(n);
/*3335*/            ValueAxis rangeAxis = getRangeAxisForDataset(n);
/*3336*/            r.drawAnnotations(g2, dataArea, domainAxis, rangeAxis, Layer.FOREGROUND, info);
/*   0*/          }  
/*   0*/      } 
/*   0*/    } 
/*3344*/    int xAxisIndex = crosshairState.getDomainAxisIndex();
/*3345*/    ValueAxis xAxis = getDomainAxis(xAxisIndex);
/*3346*/    RectangleEdge xAxisEdge = getDomainAxisEdge(xAxisIndex);
/*3347*/    if (!this.domainCrosshairLockedOnData && anchor != null) {
/*   0*/      double xx;
/*3349*/      if (orient == PlotOrientation.VERTICAL) {
/*3350*/        xx = xAxis.java2DToValue(anchor.getX(), dataArea, xAxisEdge);
/*   0*/      } else {
/*3353*/        xx = xAxis.java2DToValue(anchor.getY(), dataArea, xAxisEdge);
/*   0*/      } 
/*3355*/      crosshairState.setCrosshairX(xx);
/*   0*/    } 
/*3357*/    setDomainCrosshairValue(crosshairState.getCrosshairX(), false);
/*3358*/    if (isDomainCrosshairVisible()) {
/*3359*/      double x = getDomainCrosshairValue();
/*3360*/      Paint paint = getDomainCrosshairPaint();
/*3361*/      Stroke stroke = getDomainCrosshairStroke();
/*3362*/      drawDomainCrosshair(g2, dataArea, orient, x, xAxis, stroke, paint);
/*   0*/    } 
/*3366*/    int yAxisIndex = crosshairState.getRangeAxisIndex();
/*3367*/    ValueAxis yAxis = getRangeAxis(yAxisIndex);
/*3368*/    RectangleEdge yAxisEdge = getRangeAxisEdge(yAxisIndex);
/*3369*/    if (!this.rangeCrosshairLockedOnData && anchor != null) {
/*   0*/      double yy;
/*3371*/      if (orient == PlotOrientation.VERTICAL) {
/*3372*/        yy = yAxis.java2DToValue(anchor.getY(), dataArea, yAxisEdge);
/*   0*/      } else {
/*3374*/        yy = yAxis.java2DToValue(anchor.getX(), dataArea, yAxisEdge);
/*   0*/      } 
/*3376*/      crosshairState.setCrosshairY(yy);
/*   0*/    } 
/*3378*/    setRangeCrosshairValue(crosshairState.getCrosshairY(), false);
/*3379*/    if (isRangeCrosshairVisible()) {
/*3380*/      double y = getRangeCrosshairValue();
/*3381*/      Paint paint = getRangeCrosshairPaint();
/*3382*/      Stroke stroke = getRangeCrosshairStroke();
/*3383*/      drawRangeCrosshair(g2, dataArea, orient, y, yAxis, stroke, paint);
/*   0*/    } 
/*3386*/    if (!foundData)
/*3387*/      drawNoDataMessage(g2, dataArea); 
/*3390*/    for (int m = 0; m < this.renderers.size(); m++)
/*3391*/      drawDomainMarkers(g2, dataArea, m, Layer.FOREGROUND); 
/*3393*/    for (int k = 0; k < this.renderers.size(); k++)
/*3394*/      drawRangeMarkers(g2, dataArea, k, Layer.FOREGROUND); 
/*3397*/    drawAnnotations(g2, dataArea, info);
/*3398*/    g2.setClip(originalClip);
/*3399*/    g2.setComposite(originalComposite);
/*3401*/    drawOutline(g2, dataArea);
/*   0*/  }
/*   0*/  
/*   0*/  public void drawBackground(Graphics2D g2, Rectangle2D area) {
/*3412*/    fillBackground(g2, area, this.orientation);
/*3413*/    drawQuadrants(g2, area);
/*3414*/    drawBackgroundImage(g2, area);
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawQuadrants(Graphics2D g2, Rectangle2D area) {
/*   0*/    boolean somethingToDraw = false;
/*3432*/    ValueAxis xAxis = getDomainAxis();
/*3433*/    if (xAxis == null)
/*   0*/      return; 
/*3436*/    double x = xAxis.getRange().constrain(this.quadrantOrigin.getX());
/*3437*/    double xx = xAxis.valueToJava2D(x, area, getDomainAxisEdge());
/*3439*/    ValueAxis yAxis = getRangeAxis();
/*3440*/    if (yAxis == null)
/*   0*/      return; 
/*3443*/    double y = yAxis.getRange().constrain(this.quadrantOrigin.getY());
/*3444*/    double yy = yAxis.valueToJava2D(y, area, getRangeAxisEdge());
/*3446*/    double xmin = xAxis.getLowerBound();
/*3447*/    double xxmin = xAxis.valueToJava2D(xmin, area, getDomainAxisEdge());
/*3449*/    double xmax = xAxis.getUpperBound();
/*3450*/    double xxmax = xAxis.valueToJava2D(xmax, area, getDomainAxisEdge());
/*3452*/    double ymin = yAxis.getLowerBound();
/*3453*/    double yymin = yAxis.valueToJava2D(ymin, area, getRangeAxisEdge());
/*3455*/    double ymax = yAxis.getUpperBound();
/*3456*/    double yymax = yAxis.valueToJava2D(ymax, area, getRangeAxisEdge());
/*3458*/    Rectangle2D[] r = { null, null, null, null };
/*3459*/    if (this.quadrantPaint[0] != null && 
/*3460*/      x > xmin && y < ymax) {
/*3461*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3462*/        r[0] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmin, xx), Math.abs(yy - yymax), Math.abs(xx - xxmin));
/*   0*/      } else {
/*3467*/        r[0] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymax, yy), Math.abs(xx - xxmin), Math.abs(yy - yymax));
/*   0*/      } 
/*3471*/      somethingToDraw = true;
/*   0*/    } 
/*3474*/    if (this.quadrantPaint[1] != null && 
/*3475*/      x < xmax && y < ymax) {
/*3476*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3477*/        r[1] = new Rectangle2D.Double(Math.min(yymax, yy), Math.min(xxmax, xx), Math.abs(yy - yymax), Math.abs(xx - xxmax));
/*   0*/      } else {
/*3482*/        r[1] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymax, yy), Math.abs(xx - xxmax), Math.abs(yy - yymax));
/*   0*/      } 
/*3486*/      somethingToDraw = true;
/*   0*/    } 
/*3489*/    if (this.quadrantPaint[2] != null && 
/*3490*/      x > xmin && y > ymin) {
/*3491*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3492*/        r[2] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmin, xx), Math.abs(yy - yymin), Math.abs(xx - xxmin));
/*   0*/      } else {
/*3497*/        r[2] = new Rectangle2D.Double(Math.min(xxmin, xx), Math.min(yymin, yy), Math.abs(xx - xxmin), Math.abs(yy - yymin));
/*   0*/      } 
/*3501*/      somethingToDraw = true;
/*   0*/    } 
/*3504*/    if (this.quadrantPaint[3] != null && 
/*3505*/      x < xmax && y > ymin) {
/*3506*/      if (this.orientation == PlotOrientation.HORIZONTAL) {
/*3507*/        r[3] = new Rectangle2D.Double(Math.min(yymin, yy), Math.min(xxmax, xx), Math.abs(yy - yymin), Math.abs(xx - xxmax));
/*   0*/      } else {
/*3512*/        r[3] = new Rectangle2D.Double(Math.min(xx, xxmax), Math.min(yymin, yy), Math.abs(xx - xxmax), Math.abs(yy - yymin));
/*   0*/      } 
/*3516*/      somethingToDraw = true;
/*   0*/    } 
/*3519*/    if (somethingToDraw) {
/*3520*/      Composite originalComposite = g2.getComposite();
/*3521*/      g2.setComposite(AlphaComposite.getInstance(3, getBackgroundAlpha()));
/*3523*/      for (int i = 0; i < 4; i++) {
/*3524*/        if (this.quadrantPaint[i] != null && r[i] != null) {
/*3525*/          g2.setPaint(this.quadrantPaint[i]);
/*3526*/          g2.fill(r[i]);
/*   0*/        } 
/*   0*/      } 
/*3529*/      g2.setComposite(originalComposite);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawDomainTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
/*3544*/    Paint bandPaint = getDomainTickBandPaint();
/*3545*/    if (bandPaint != null) {
/*   0*/      boolean fillBand = false;
/*3547*/      ValueAxis xAxis = getDomainAxis();
/*3548*/      double previous = xAxis.getLowerBound();
/*3549*/      Iterator<ValueTick> iterator = ticks.iterator();
/*3550*/      while (iterator.hasNext()) {
/*3551*/        ValueTick tick = iterator.next();
/*3552*/        double current = tick.getValue();
/*3553*/        if (fillBand)
/*3554*/          getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, current); 
/*3557*/        previous = current;
/*3558*/        fillBand = !fillBand;
/*   0*/      } 
/*3560*/      double end = xAxis.getUpperBound();
/*3561*/      if (fillBand)
/*3562*/        getRenderer().fillDomainGridBand(g2, this, xAxis, dataArea, previous, end); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawRangeTickBands(Graphics2D g2, Rectangle2D dataArea, List ticks) {
/*3579*/    Paint bandPaint = getRangeTickBandPaint();
/*3580*/    if (bandPaint != null) {
/*   0*/      boolean fillBand = false;
/*3582*/      ValueAxis axis = getRangeAxis();
/*3583*/      double previous = axis.getLowerBound();
/*3584*/      Iterator<ValueTick> iterator = ticks.iterator();
/*3585*/      while (iterator.hasNext()) {
/*3586*/        ValueTick tick = iterator.next();
/*3587*/        double current = tick.getValue();
/*3588*/        if (fillBand)
/*3589*/          getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, current); 
/*3592*/        previous = current;
/*3593*/        fillBand = !fillBand;
/*   0*/      } 
/*3595*/      double end = axis.getUpperBound();
/*3596*/      if (fillBand)
/*3597*/        getRenderer().fillRangeGridBand(g2, this, axis, dataArea, previous, end); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected Map drawAxes(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea, PlotRenderingInfo plotState) {
/*3619*/    AxisCollection axisCollection = new AxisCollection();
/*3622*/    for (int index = 0; index < this.domainAxes.size(); index++) {
/*3623*/      ValueAxis axis = (ValueAxis)this.domainAxes.get(index);
/*3624*/      if (axis != null)
/*3625*/        axisCollection.add(axis, getDomainAxisEdge(index)); 
/*   0*/    } 
/*3630*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*3631*/      ValueAxis yAxis = (ValueAxis)this.rangeAxes.get(i);
/*3632*/      if (yAxis != null)
/*3633*/        axisCollection.add(yAxis, getRangeAxisEdge(i)); 
/*   0*/    } 
/*3637*/    Map<ValueAxis, AxisState> axisStateMap = new HashMap();
/*3640*/    double cursor = dataArea.getMinY() - this.axisOffset.calculateTopOutset(dataArea.getHeight());
/*3642*/    Iterator<ValueAxis> iterator = axisCollection.getAxesAtTop().iterator();
/*3643*/    while (iterator.hasNext()) {
/*3644*/      ValueAxis axis = iterator.next();
/*3645*/      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.TOP, plotState);
/*3647*/      cursor = info.getCursor();
/*3648*/      axisStateMap.put(axis, info);
/*   0*/    } 
/*3652*/    cursor = dataArea.getMaxY() + this.axisOffset.calculateBottomOutset(dataArea.getHeight());
/*3654*/    iterator = axisCollection.getAxesAtBottom().iterator();
/*3655*/    while (iterator.hasNext()) {
/*3656*/      ValueAxis axis = iterator.next();
/*3657*/      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.BOTTOM, plotState);
/*3659*/      cursor = info.getCursor();
/*3660*/      axisStateMap.put(axis, info);
/*   0*/    } 
/*3664*/    cursor = dataArea.getMinX() - this.axisOffset.calculateLeftOutset(dataArea.getWidth());
/*3666*/    iterator = axisCollection.getAxesAtLeft().iterator();
/*3667*/    while (iterator.hasNext()) {
/*3668*/      ValueAxis axis = iterator.next();
/*3669*/      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.LEFT, plotState);
/*3671*/      cursor = info.getCursor();
/*3672*/      axisStateMap.put(axis, info);
/*   0*/    } 
/*3676*/    cursor = dataArea.getMaxX() + this.axisOffset.calculateRightOutset(dataArea.getWidth());
/*3678*/    iterator = axisCollection.getAxesAtRight().iterator();
/*3679*/    while (iterator.hasNext()) {
/*3680*/      ValueAxis axis = iterator.next();
/*3681*/      AxisState info = axis.draw(g2, cursor, plotArea, dataArea, RectangleEdge.RIGHT, plotState);
/*3683*/      cursor = info.getCursor();
/*3684*/      axisStateMap.put(axis, info);
/*   0*/    } 
/*3687*/    return axisStateMap;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean render(Graphics2D g2, Rectangle2D dataArea, int index, PlotRenderingInfo info, CrosshairState crosshairState) {
/*   0*/    boolean foundData = false;
/*3710*/    XYDataset dataset = getDataset(index);
/*3711*/    if (!DatasetUtilities.isEmptyOrNull(dataset)) {
/*3712*/      foundData = true;
/*3713*/      ValueAxis xAxis = getDomainAxisForDataset(index);
/*3714*/      ValueAxis yAxis = getRangeAxisForDataset(index);
/*3715*/      if (xAxis == null || yAxis == null)
/*3716*/        return foundData; 
/*3718*/      XYItemRenderer renderer = getRenderer(index);
/*3719*/      if (renderer == null) {
/*3720*/        renderer = getRenderer();
/*3721*/        if (renderer == null)
/*3722*/          return foundData; 
/*   0*/      } 
/*3726*/      XYItemRendererState state = renderer.initialise(g2, dataArea, this, dataset, info);
/*3728*/      XYDatasetSelectionState selectionState = state.getSelectionState();
/*3729*/      int passCount = renderer.getPassCount();
/*3731*/      SeriesRenderingOrder seriesOrder = getSeriesRenderingOrder();
/*3732*/      if (seriesOrder == SeriesRenderingOrder.REVERSE) {
/*3734*/        for (int pass = 0; pass < passCount; pass++) {
/*3735*/          int seriesCount = dataset.getSeriesCount();
/*3736*/          for (int series = seriesCount - 1; series >= 0; series--) {
/*3737*/            int firstItem = 0;
/*3738*/            int lastItem = dataset.getItemCount(series) - 1;
/*3739*/            if (lastItem != -1) {
/*3742*/              if (state.getProcessVisibleItemsOnly()) {
/*3743*/                int[] itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
/*3746*/                firstItem = Math.max(itemBounds[0] - 1, 0);
/*3747*/                lastItem = Math.min(itemBounds[1] + 1, lastItem);
/*   0*/              } 
/*3749*/              state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
/*3751*/              for (int item = firstItem; item <= lastItem; item++) {
/*   0*/                boolean selected = false;
/*3753*/                if (selectionState != null)
/*3754*/                  selected = selectionState.isSelected(series, item); 
/*3757*/                renderer.drawItem(g2, state, dataArea, this, xAxis, yAxis, dataset, series, item, selected, pass);
/*   0*/              } 
/*3761*/              state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } else {
/*3768*/        for (int pass = 0; pass < passCount; pass++) {
/*3769*/          int seriesCount = dataset.getSeriesCount();
/*3770*/          for (int series = 0; series < seriesCount; series++) {
/*3771*/            int firstItem = 0;
/*3772*/            int lastItem = dataset.getItemCount(series) - 1;
/*3773*/            if (state.getProcessVisibleItemsOnly()) {
/*3774*/              int[] itemBounds = RendererUtilities.findLiveItems(dataset, series, xAxis.getLowerBound(), xAxis.getUpperBound());
/*3777*/              firstItem = Math.max(itemBounds[0] - 1, 0);
/*3778*/              lastItem = Math.min(itemBounds[1] + 1, lastItem);
/*   0*/            } 
/*3780*/            state.startSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
/*3782*/            for (int item = firstItem; item <= lastItem; item++) {
/*   0*/              boolean selected = false;
/*3784*/              if (selectionState != null)
/*3785*/                selected = selectionState.isSelected(series, item); 
/*3788*/              renderer.drawItem(g2, state, dataArea, this, xAxis, yAxis, dataset, series, item, selected, pass);
/*   0*/            } 
/*3792*/            state.endSeriesPass(dataset, series, firstItem, lastItem, pass, passCount);
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*3798*/    return foundData;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getDomainAxisForDataset(int index) {
/*3809*/    int upper = Math.max(getDatasetCount(), getRendererCount());
/*3810*/    if (index < 0 || index >= upper)
/*3811*/      throw new IllegalArgumentException("Index " + index + " out of bounds."); 
/*3814*/    ValueAxis valueAxis = null;
/*3815*/    List<Integer> axisIndices = (List)this.datasetToDomainAxesMap.get(new Integer(index));
/*3817*/    if (axisIndices != null) {
/*3819*/      Integer axisIndex = axisIndices.get(0);
/*3820*/      valueAxis = getDomainAxis(axisIndex);
/*   0*/    } else {
/*3823*/      valueAxis = getDomainAxis(0);
/*   0*/    } 
/*3825*/    return valueAxis;
/*   0*/  }
/*   0*/  
/*   0*/  public ValueAxis getRangeAxisForDataset(int index) {
/*3836*/    int upper = Math.max(getDatasetCount(), getRendererCount());
/*3837*/    if (index < 0 || index >= upper)
/*3838*/      throw new IllegalArgumentException("Index " + index + " out of bounds."); 
/*3841*/    ValueAxis valueAxis = null;
/*3842*/    List<Integer> axisIndices = (List)this.datasetToRangeAxesMap.get(new Integer(index));
/*3844*/    if (axisIndices != null) {
/*3846*/      Integer axisIndex = axisIndices.get(0);
/*3847*/      valueAxis = getRangeAxis(axisIndex);
/*   0*/    } else {
/*3850*/      valueAxis = getRangeAxis(0);
/*   0*/    } 
/*3852*/    return valueAxis;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainGridlines(Graphics2D g2, Rectangle2D dataArea, List ticks) {
/*3868*/    if (getRenderer() == null)
/*   0*/      return; 
/*3873*/    if (isDomainGridlinesVisible() || isDomainMinorGridlinesVisible()) {
/*3874*/      Stroke gridStroke = null;
/*3875*/      Paint gridPaint = null;
/*3876*/      Iterator<ValueTick> iterator = ticks.iterator();
/*   0*/      boolean paintLine = false;
/*3878*/      while (iterator.hasNext()) {
/*3879*/        paintLine = false;
/*3880*/        ValueTick tick = iterator.next();
/*3881*/        if (tick.getTickType() == TickType.MINOR && isDomainMinorGridlinesVisible()) {
/*3883*/          gridStroke = getDomainMinorGridlineStroke();
/*3884*/          gridPaint = getDomainMinorGridlinePaint();
/*3885*/          paintLine = true;
/*3887*/        } else if (tick.getTickType() == TickType.MAJOR && isDomainGridlinesVisible()) {
/*3889*/          gridStroke = getDomainGridlineStroke();
/*3890*/          gridPaint = getDomainGridlinePaint();
/*3891*/          paintLine = true;
/*   0*/        } 
/*3893*/        XYItemRenderer r = getRenderer();
/*3894*/        if (r instanceof AbstractXYItemRenderer && paintLine)
/*3895*/          ((AbstractXYItemRenderer)r).drawDomainLine(g2, this, getDomainAxis(), dataArea, tick.getValue(), gridPaint, gridStroke); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeGridlines(Graphics2D g2, Rectangle2D area, List ticks) {
/*3917*/    if (getRenderer() == null)
/*   0*/      return; 
/*3922*/    if (isRangeGridlinesVisible() || isRangeMinorGridlinesVisible()) {
/*3923*/      Stroke gridStroke = null;
/*3924*/      Paint gridPaint = null;
/*3925*/      ValueAxis axis = getRangeAxis();
/*3926*/      if (axis != null) {
/*3927*/        Iterator<ValueTick> iterator = ticks.iterator();
/*   0*/        boolean paintLine = false;
/*3929*/        while (iterator.hasNext()) {
/*3930*/          paintLine = false;
/*3931*/          ValueTick tick = iterator.next();
/*3932*/          if (tick.getTickType() == TickType.MINOR && isRangeMinorGridlinesVisible()) {
/*3934*/            gridStroke = getRangeMinorGridlineStroke();
/*3935*/            gridPaint = getRangeMinorGridlinePaint();
/*3936*/            paintLine = true;
/*3938*/          } else if (tick.getTickType() == TickType.MAJOR && isRangeGridlinesVisible()) {
/*3940*/            gridStroke = getRangeGridlineStroke();
/*3941*/            gridPaint = getRangeGridlinePaint();
/*3942*/            paintLine = true;
/*   0*/          } 
/*3944*/          if ((tick.getValue() != 0.0D || !isRangeZeroBaselineVisible()) && paintLine)
/*3946*/            getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, tick.getValue(), gridPaint, gridStroke); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawZeroDomainBaseline(Graphics2D g2, Rectangle2D area) {
/*3965*/    if (isDomainZeroBaselineVisible()) {
/*3966*/      XYItemRenderer r = getRenderer();
/*3967*/      r.drawDomainLine(g2, this, getDomainAxis(), area, 0.0D, this.domainZeroBaselinePaint, this.domainZeroBaselineStroke);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawZeroRangeBaseline(Graphics2D g2, Rectangle2D area) {
/*3982*/    if (isRangeZeroBaselineVisible())
/*3983*/      getRenderer().drawRangeLine(g2, this, getRangeAxis(), area, 0.0D, this.rangeZeroBaselinePaint, this.rangeZeroBaselineStroke); 
/*   0*/  }
/*   0*/  
/*   0*/  public void drawAnnotations(Graphics2D g2, Rectangle2D dataArea, PlotRenderingInfo info) {
/*3999*/    Iterator<XYAnnotation> iterator = this.annotations.iterator();
/*4000*/    while (iterator.hasNext()) {
/*4001*/      XYAnnotation annotation = iterator.next();
/*4002*/      ValueAxis xAxis = getDomainAxis();
/*4003*/      ValueAxis yAxis = getRangeAxis();
/*4004*/      annotation.draw(g2, this, dataArea, xAxis, yAxis, 0, info);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*4021*/    XYItemRenderer r = getRenderer(index);
/*4022*/    if (r == null)
/*   0*/      return; 
/*4027*/    if (index >= getDatasetCount())
/*   0*/      return; 
/*4030*/    Collection markers = getDomainMarkers(index, layer);
/*4031*/    ValueAxis axis = getDomainAxisForDataset(index);
/*4032*/    if (markers != null && axis != null) {
/*4033*/      Iterator<Marker> iterator = markers.iterator();
/*4034*/      while (iterator.hasNext()) {
/*4035*/        Marker marker = iterator.next();
/*4036*/        r.drawDomainMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeMarkers(Graphics2D g2, Rectangle2D dataArea, int index, Layer layer) {
/*4054*/    XYItemRenderer r = getRenderer(index);
/*4055*/    if (r == null)
/*   0*/      return; 
/*4060*/    if (index >= getDatasetCount())
/*   0*/      return; 
/*4063*/    Collection markers = getRangeMarkers(index, layer);
/*4064*/    ValueAxis axis = getRangeAxisForDataset(index);
/*4065*/    if (markers != null && axis != null) {
/*4066*/      Iterator<Marker> iterator = markers.iterator();
/*4067*/      while (iterator.hasNext()) {
/*4068*/        Marker marker = iterator.next();
/*4069*/        r.drawRangeMarker(g2, this, axis, marker, dataArea);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(Layer layer) {
/*4084*/    return getDomainMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(Layer layer) {
/*4097*/    return getRangeMarkers(0, layer);
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getDomainMarkers(int index, Layer layer) {
/*4112*/    Collection<?> result = null;
/*4113*/    Integer key = new Integer(index);
/*4114*/    if (layer == Layer.FOREGROUND) {
/*4115*/      result = (Collection)this.foregroundDomainMarkers.get(key);
/*4117*/    } else if (layer == Layer.BACKGROUND) {
/*4118*/      result = (Collection)this.backgroundDomainMarkers.get(key);
/*   0*/    } 
/*4120*/    if (result != null)
/*4121*/      result = Collections.unmodifiableCollection(result); 
/*4123*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Collection getRangeMarkers(int index, Layer layer) {
/*4138*/    Collection<?> result = null;
/*4139*/    Integer key = new Integer(index);
/*4140*/    if (layer == Layer.FOREGROUND) {
/*4141*/      result = (Collection)this.foregroundRangeMarkers.get(key);
/*4143*/    } else if (layer == Layer.BACKGROUND) {
/*4144*/      result = (Collection)this.backgroundRangeMarkers.get(key);
/*   0*/    } 
/*4146*/    if (result != null)
/*4147*/      result = Collections.unmodifiableCollection(result); 
/*4149*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawHorizontalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
/*4166*/    ValueAxis axis = getRangeAxis();
/*4167*/    if (getOrientation() == PlotOrientation.HORIZONTAL)
/*4168*/      axis = getDomainAxis(); 
/*4170*/    if (axis.getRange().contains(value)) {
/*4171*/      double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
/*4172*/      Line2D line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
/*4174*/      g2.setStroke(stroke);
/*4175*/      g2.setPaint(paint);
/*4176*/      g2.draw(line);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawDomainCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
/*4198*/    if (axis.getRange().contains(value)) {
/*4199*/      Line2D line = null;
/*4200*/      if (orientation == PlotOrientation.VERTICAL) {
/*4201*/        double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
/*4203*/        line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
/*   0*/      } else {
/*4207*/        double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
/*4209*/        line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
/*   0*/      } 
/*4212*/      g2.setStroke(stroke);
/*4213*/      g2.setPaint(paint);
/*4214*/      g2.draw(line);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawVerticalLine(Graphics2D g2, Rectangle2D dataArea, double value, Stroke stroke, Paint paint) {
/*4231*/    ValueAxis axis = getDomainAxis();
/*4232*/    if (getOrientation() == PlotOrientation.HORIZONTAL)
/*4233*/      axis = getRangeAxis(); 
/*4235*/    if (axis.getRange().contains(value)) {
/*4236*/      double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
/*4238*/      Line2D line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
/*4240*/      g2.setStroke(stroke);
/*4241*/      g2.setPaint(paint);
/*4242*/      g2.draw(line);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawRangeCrosshair(Graphics2D g2, Rectangle2D dataArea, PlotOrientation orientation, double value, ValueAxis axis, Stroke stroke, Paint paint) {
/*4264*/    if (axis.getRange().contains(value)) {
/*4265*/      Line2D line = null;
/*4266*/      if (orientation == PlotOrientation.HORIZONTAL) {
/*4267*/        double xx = axis.valueToJava2D(value, dataArea, RectangleEdge.BOTTOM);
/*4269*/        line = new Line2D.Double(xx, dataArea.getMinY(), xx, dataArea.getMaxY());
/*   0*/      } else {
/*4273*/        double yy = axis.valueToJava2D(value, dataArea, RectangleEdge.LEFT);
/*4275*/        line = new Line2D.Double(dataArea.getMinX(), yy, dataArea.getMaxX(), yy);
/*   0*/      } 
/*4278*/      g2.setStroke(stroke);
/*4279*/      g2.setPaint(paint);
/*4280*/      g2.draw(line);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void handleClick(int x, int y, PlotRenderingInfo info) {
/*4294*/    Rectangle2D dataArea = info.getDataArea();
/*4295*/    if (dataArea.contains(x, y)) {
/*4297*/      ValueAxis xaxis = getDomainAxis();
/*4298*/      if (xaxis != null) {
/*4299*/        double hvalue = xaxis.java2DToValue(x, info.getDataArea(), getDomainAxisEdge());
/*4301*/        setDomainCrosshairValue(hvalue);
/*   0*/      } 
/*4305*/      ValueAxis yaxis = getRangeAxis();
/*4306*/      if (yaxis != null) {
/*4307*/        double vvalue = yaxis.java2DToValue(y, info.getDataArea(), getRangeAxisEdge());
/*4309*/        setRangeCrosshairValue(vvalue);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private List getDatasetsMappedToDomainAxis(Integer axisIndex) {
/*4323*/    if (axisIndex == null)
/*4324*/      throw new IllegalArgumentException("Null 'axisIndex' argument."); 
/*4326*/    List<Object> result = new ArrayList();
/*4327*/    for (int i = 0; i < this.datasets.size(); i++) {
/*4328*/      List mappedAxes = (List)this.datasetToDomainAxesMap.get(new Integer(i));
/*4330*/      if (mappedAxes == null) {
/*4331*/        if (axisIndex.equals(ZERO))
/*4332*/          result.add(this.datasets.get(i)); 
/*4336*/      } else if (mappedAxes.contains(axisIndex)) {
/*4337*/        result.add(this.datasets.get(i));
/*   0*/      } 
/*   0*/    } 
/*4341*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  private List getDatasetsMappedToRangeAxis(Integer axisIndex) {
/*4353*/    if (axisIndex == null)
/*4354*/      throw new IllegalArgumentException("Null 'axisIndex' argument."); 
/*4356*/    List<Object> result = new ArrayList();
/*4357*/    for (int i = 0; i < this.datasets.size(); i++) {
/*4358*/      List mappedAxes = (List)this.datasetToRangeAxesMap.get(new Integer(i));
/*4360*/      if (mappedAxes == null) {
/*4361*/        if (axisIndex.equals(ZERO))
/*4362*/          result.add(this.datasets.get(i)); 
/*4366*/      } else if (mappedAxes.contains(axisIndex)) {
/*4367*/        result.add(this.datasets.get(i));
/*   0*/      } 
/*   0*/    } 
/*4371*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getDomainAxisIndex(ValueAxis axis) {
/*4384*/    int result = this.domainAxes.indexOf(axis);
/*4385*/    if (result < 0) {
/*4387*/      Plot parent = getParent();
/*4388*/      if (parent instanceof XYPlot) {
/*4389*/        XYPlot p = (XYPlot)parent;
/*4390*/        result = p.getDomainAxisIndex(axis);
/*   0*/      } 
/*   0*/    } 
/*4393*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public int getRangeAxisIndex(ValueAxis axis) {
/*4406*/    int result = this.rangeAxes.indexOf(axis);
/*4407*/    if (result < 0) {
/*4409*/      Plot parent = getParent();
/*4410*/      if (parent instanceof XYPlot) {
/*4411*/        XYPlot p = (XYPlot)parent;
/*4412*/        result = p.getRangeAxisIndex(axis);
/*   0*/      } 
/*   0*/    } 
/*4415*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public Range getDataRange(ValueAxis axis) {
/*4427*/    Range result = null;
/*4428*/    List mappedDatasets = new ArrayList();
/*4429*/    List<XYAnnotation> includedAnnotations = new ArrayList();
/*   0*/    boolean isDomainAxis = true;
/*4433*/    int domainIndex = getDomainAxisIndex(axis);
/*4434*/    if (domainIndex >= 0) {
/*4435*/      isDomainAxis = true;
/*4436*/      mappedDatasets.addAll(getDatasetsMappedToDomainAxis(new Integer(domainIndex)));
/*4438*/      if (domainIndex == 0) {
/*4440*/        Iterator<XYAnnotation> iterator1 = this.annotations.iterator();
/*4441*/        while (iterator1.hasNext()) {
/*4442*/          XYAnnotation annotation = iterator1.next();
/*4443*/          if (annotation instanceof XYAnnotationBoundsInfo)
/*4444*/            includedAnnotations.add(annotation); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*4451*/    int rangeIndex = getRangeAxisIndex(axis);
/*4452*/    if (rangeIndex >= 0) {
/*4453*/      isDomainAxis = false;
/*4454*/      mappedDatasets.addAll(getDatasetsMappedToRangeAxis(new Integer(rangeIndex)));
/*4456*/      if (rangeIndex == 0) {
/*4457*/        Iterator<XYAnnotation> iterator1 = this.annotations.iterator();
/*4458*/        while (iterator1.hasNext()) {
/*4459*/          XYAnnotation annotation = iterator1.next();
/*4460*/          if (annotation instanceof XYAnnotationBoundsInfo)
/*4461*/            includedAnnotations.add(annotation); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*4469*/    Iterator<XYDataset> iterator = mappedDatasets.iterator();
/*4470*/    while (iterator.hasNext()) {
/*4471*/      XYDataset d = iterator.next();
/*4472*/      if (d != null) {
/*4473*/        XYItemRenderer r = getRendererForDataset(d);
/*4474*/        if (isDomainAxis) {
/*4475*/          if (r != null) {
/*4476*/            result = Range.combine(result, r.findDomainBounds(d));
/*   0*/          } else {
/*4479*/            result = Range.combine(result, DatasetUtilities.findDomainBounds(d));
/*   0*/          } 
/*4484*/        } else if (r != null) {
/*4485*/          result = Range.combine(result, r.findRangeBounds(d));
/*   0*/        } else {
/*4488*/          result = Range.combine(result, DatasetUtilities.findRangeBounds(d));
/*   0*/        } 
/*4493*/        if (r == null)
/*4493*/          return result; 
/*4493*/        Collection c = r.getAnnotations();
/*4494*/        Iterator<XYAnnotation> i = c.iterator();
/*4495*/        while (i.hasNext()) {
/*4496*/          XYAnnotation a = i.next();
/*4497*/          if (a instanceof XYAnnotationBoundsInfo)
/*4498*/            includedAnnotations.add(a); 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*4504*/    Iterator<XYAnnotation> it = includedAnnotations.iterator();
/*4505*/    while (it.hasNext()) {
/*4506*/      XYAnnotationBoundsInfo xyabi = (XYAnnotationBoundsInfo)it.next();
/*4507*/      if (xyabi.getIncludeInDataBounds()) {
/*4508*/        if (isDomainAxis) {
/*4509*/          result = Range.combine(result, xyabi.getXRange());
/*   0*/          continue;
/*   0*/        } 
/*4512*/        result = Range.combine(result, xyabi.getYRange());
/*   0*/      } 
/*   0*/    } 
/*4517*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void datasetChanged(DatasetChangeEvent event) {
/*4529*/    configureDomainAxes();
/*4530*/    configureRangeAxes();
/*4531*/    if (getParent() != null) {
/*4532*/      getParent().datasetChanged(event);
/*   0*/    } else {
/*4535*/      PlotChangeEvent e = new PlotChangeEvent(this);
/*4536*/      e.setType(ChartChangeEventType.DATASET_UPDATED);
/*4537*/      notifyListeners(e);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void rendererChanged(RendererChangeEvent event) {
/*4549*/    if (event.getSeriesVisibilityChanged()) {
/*4550*/      configureDomainAxes();
/*4551*/      configureRangeAxes();
/*   0*/    } 
/*4553*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainCrosshairVisible() {
/*4564*/    return this.domainCrosshairVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairVisible(boolean flag) {
/*4577*/    if (this.domainCrosshairVisible != flag) {
/*4578*/      this.domainCrosshairVisible = flag;
/*4579*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainCrosshairLockedOnData() {
/*4592*/    return this.domainCrosshairLockedOnData;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairLockedOnData(boolean flag) {
/*4605*/    if (this.domainCrosshairLockedOnData != flag) {
/*4606*/      this.domainCrosshairLockedOnData = flag;
/*4607*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getDomainCrosshairValue() {
/*4619*/    return this.domainCrosshairValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairValue(double value) {
/*4631*/    setDomainCrosshairValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairValue(double value, boolean notify) {
/*4645*/    this.domainCrosshairValue = value;
/*4646*/    if (isDomainCrosshairVisible() && notify)
/*4647*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getDomainCrosshairStroke() {
/*4661*/    return this.domainCrosshairStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairStroke(Stroke stroke) {
/*4674*/    if (stroke == null)
/*4675*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*4677*/    this.domainCrosshairStroke = stroke;
/*4678*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getDomainCrosshairPaint() {
/*4691*/    return this.domainCrosshairPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainCrosshairPaint(Paint paint) {
/*4703*/    if (paint == null)
/*4704*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*4706*/    this.domainCrosshairPaint = paint;
/*4707*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairVisible() {
/*4719*/    return this.rangeCrosshairVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairVisible(boolean flag) {
/*4732*/    if (this.rangeCrosshairVisible != flag) {
/*4733*/      this.rangeCrosshairVisible = flag;
/*4734*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeCrosshairLockedOnData() {
/*4747*/    return this.rangeCrosshairLockedOnData;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairLockedOnData(boolean flag) {
/*4760*/    if (this.rangeCrosshairLockedOnData != flag) {
/*4761*/      this.rangeCrosshairLockedOnData = flag;
/*4762*/      fireChangeEvent();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getRangeCrosshairValue() {
/*4774*/    return this.rangeCrosshairValue;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value) {
/*4788*/    setRangeCrosshairValue(value, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairValue(double value, boolean notify) {
/*4802*/    this.rangeCrosshairValue = value;
/*4803*/    if (isRangeCrosshairVisible() && notify)
/*4804*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getRangeCrosshairStroke() {
/*4818*/    return this.rangeCrosshairStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairStroke(Stroke stroke) {
/*4831*/    if (stroke == null)
/*4832*/      throw new IllegalArgumentException("Null 'stroke' argument."); 
/*4834*/    this.rangeCrosshairStroke = stroke;
/*4835*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getRangeCrosshairPaint() {
/*4848*/    return this.rangeCrosshairPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangeCrosshairPaint(Paint paint) {
/*4860*/    if (paint == null)
/*4861*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/*4863*/    this.rangeCrosshairPaint = paint;
/*4864*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedDomainAxisSpace() {
/*4875*/    return this.fixedDomainAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedDomainAxisSpace(AxisSpace space) {
/*4887*/    setFixedDomainAxisSpace(space, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedDomainAxisSpace(AxisSpace space, boolean notify) {
/*4902*/    this.fixedDomainAxisSpace = space;
/*4903*/    if (notify)
/*4904*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public AxisSpace getFixedRangeAxisSpace() {
/*4916*/    return this.fixedRangeAxisSpace;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedRangeAxisSpace(AxisSpace space) {
/*4928*/    setFixedRangeAxisSpace(space, true);
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedRangeAxisSpace(AxisSpace space, boolean notify) {
/*4943*/    this.fixedRangeAxisSpace = space;
/*4944*/    if (notify)
/*4945*/      fireChangeEvent(); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainPannable() {
/*4958*/    return this.domainPannable;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDomainPannable(boolean pannable) {
/*4970*/    this.domainPannable = pannable;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangePannable() {
/*4982*/    return this.rangePannable;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRangePannable(boolean pannable) {
/*4994*/    this.rangePannable = pannable;
/*   0*/  }
/*   0*/  
/*   0*/  public void panDomainAxes(double percent, PlotRenderingInfo info, Point2D source) {
/*5008*/    if (!isDomainPannable())
/*   0*/      return; 
/*5011*/    int domainAxisCount = getDomainAxisCount();
/*5012*/    for (int i = 0; i < domainAxisCount; i++) {
/*5013*/      ValueAxis axis = getDomainAxis(i);
/*5014*/      if (axis != null) {
/*5017*/        if (axis.isInverted())
/*5018*/          percent = -percent; 
/*5020*/        axis.pan(percent);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void panRangeAxes(double percent, PlotRenderingInfo info, Point2D source) {
/*5035*/    if (!isRangePannable())
/*   0*/      return; 
/*5038*/    int rangeAxisCount = getRangeAxisCount();
/*5039*/    for (int i = 0; i < rangeAxisCount; i++) {
/*5040*/      ValueAxis axis = getRangeAxis(i);
/*5041*/      if (axis != null) {
/*5044*/        if (axis.isInverted())
/*5045*/          percent = -percent; 
/*5047*/        axis.pan(percent);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source) {
/*5063*/    zoomDomainAxes(factor, info, source, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomDomainAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
/*5082*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*5083*/      ValueAxis domainAxis = (ValueAxis)this.domainAxes.get(i);
/*5084*/      if (domainAxis != null)
/*5085*/        if (useAnchor) {
/*5088*/          double sourceX = source.getX();
/*5089*/          if (this.orientation == PlotOrientation.HORIZONTAL)
/*5090*/            sourceX = source.getY(); 
/*5092*/          double anchorX = domainAxis.java2DToValue(sourceX, info.getDataArea(), getDomainAxisEdge());
/*5094*/          domainAxis.resizeRange2(factor, anchorX);
/*   0*/        } else {
/*5097*/          domainAxis.resizeRange(factor);
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomDomainAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
/*5119*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*5120*/      ValueAxis domainAxis = (ValueAxis)this.domainAxes.get(i);
/*5121*/      if (domainAxis != null)
/*5122*/        domainAxis.zoomRange(lowerPercent, upperPercent); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source) {
/*5139*/    zoomRangeAxes(factor, info, source, false);
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomRangeAxes(double factor, PlotRenderingInfo info, Point2D source, boolean useAnchor) {
/*5159*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*5160*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*5161*/      if (rangeAxis != null)
/*5162*/        if (useAnchor) {
/*5165*/          double sourceY = source.getY();
/*5166*/          if (this.orientation == PlotOrientation.HORIZONTAL)
/*5167*/            sourceY = source.getX(); 
/*5169*/          double anchorY = rangeAxis.java2DToValue(sourceY, info.getDataArea(), getRangeAxisEdge());
/*5171*/          rangeAxis.resizeRange2(factor, anchorY);
/*   0*/        } else {
/*5174*/          rangeAxis.resizeRange(factor);
/*   0*/        }  
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void zoomRangeAxes(double lowerPercent, double upperPercent, PlotRenderingInfo info, Point2D source) {
/*5192*/    for (int i = 0; i < this.rangeAxes.size(); i++) {
/*5193*/      ValueAxis rangeAxis = (ValueAxis)this.rangeAxes.get(i);
/*5194*/      if (rangeAxis != null)
/*5195*/        rangeAxis.zoomRange(lowerPercent, upperPercent); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isDomainZoomable() {
/*5209*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isRangeZoomable() {
/*5221*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public int getSeriesCount() {
/*5231*/    int result = 0;
/*5232*/    XYDataset dataset = getDataset();
/*5233*/    if (dataset != null)
/*5234*/      result = dataset.getSeriesCount(); 
/*5236*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getFixedLegendItems() {
/*5247*/    return this.fixedLegendItems;
/*   0*/  }
/*   0*/  
/*   0*/  public void setFixedLegendItems(LegendItemCollection items) {
/*5260*/    this.fixedLegendItems = items;
/*5261*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/*5272*/    if (this.fixedLegendItems != null)
/*5273*/      return this.fixedLegendItems; 
/*5275*/    LegendItemCollection result = new LegendItemCollection();
/*5276*/    int count = this.datasets.size();
/*5277*/    for (int datasetIndex = 0; datasetIndex < count; datasetIndex++) {
/*5278*/      XYDataset dataset = getDataset(datasetIndex);
/*5279*/      if (dataset != null) {
/*5280*/        XYItemRenderer renderer = getRenderer(datasetIndex);
/*5281*/        if (renderer == null)
/*5282*/          renderer = getRenderer(0); 
/*5284*/        if (renderer != null) {
/*5285*/          int seriesCount = dataset.getSeriesCount();
/*5286*/          for (int i = 0; i < seriesCount; i++) {
/*5287*/            if (renderer.isSeriesVisible(i) && renderer.isSeriesVisibleInLegend(i)) {
/*5289*/              LegendItem item = renderer.getLegendItem(datasetIndex, i);
/*5291*/              if (item != null)
/*5292*/                result.add(item); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*5299*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*5310*/    if (obj == this)
/*5311*/      return true; 
/*5313*/    if (!(obj instanceof XYPlot))
/*5314*/      return false; 
/*5316*/    XYPlot that = (XYPlot)obj;
/*5317*/    if (this.weight != that.weight)
/*5318*/      return false; 
/*5320*/    if (this.orientation != that.orientation)
/*5321*/      return false; 
/*5323*/    if (!this.domainAxes.equals(that.domainAxes))
/*5324*/      return false; 
/*5326*/    if (!this.domainAxisLocations.equals(that.domainAxisLocations))
/*5327*/      return false; 
/*5329*/    if (this.rangeCrosshairLockedOnData != that.rangeCrosshairLockedOnData)
/*5331*/      return false; 
/*5333*/    if (this.domainGridlinesVisible != that.domainGridlinesVisible)
/*5334*/      return false; 
/*5336*/    if (this.rangeGridlinesVisible != that.rangeGridlinesVisible)
/*5337*/      return false; 
/*5339*/    if (this.domainMinorGridlinesVisible != that.domainMinorGridlinesVisible)
/*5341*/      return false; 
/*5343*/    if (this.rangeMinorGridlinesVisible != that.rangeMinorGridlinesVisible)
/*5345*/      return false; 
/*5347*/    if (this.domainZeroBaselineVisible != that.domainZeroBaselineVisible)
/*5348*/      return false; 
/*5350*/    if (this.rangeZeroBaselineVisible != that.rangeZeroBaselineVisible)
/*5351*/      return false; 
/*5353*/    if (this.domainCrosshairVisible != that.domainCrosshairVisible)
/*5354*/      return false; 
/*5356*/    if (this.domainCrosshairValue != that.domainCrosshairValue)
/*5357*/      return false; 
/*5359*/    if (this.domainCrosshairLockedOnData != that.domainCrosshairLockedOnData)
/*5361*/      return false; 
/*5363*/    if (this.rangeCrosshairVisible != that.rangeCrosshairVisible)
/*5364*/      return false; 
/*5366*/    if (this.rangeCrosshairValue != that.rangeCrosshairValue)
/*5367*/      return false; 
/*5369*/    if (!ObjectUtilities.equal(this.axisOffset, that.axisOffset))
/*5370*/      return false; 
/*5372*/    if (!ObjectUtilities.equal(this.renderers, that.renderers))
/*5373*/      return false; 
/*5375*/    if (!ObjectUtilities.equal(this.rangeAxes, that.rangeAxes))
/*5376*/      return false; 
/*5378*/    if (!this.rangeAxisLocations.equals(that.rangeAxisLocations))
/*5379*/      return false; 
/*5381*/    if (!ObjectUtilities.equal(this.datasetToDomainAxesMap, that.datasetToDomainAxesMap))
/*5383*/      return false; 
/*5385*/    if (!ObjectUtilities.equal(this.datasetToRangeAxesMap, that.datasetToRangeAxesMap))
/*5387*/      return false; 
/*5389*/    if (!ObjectUtilities.equal(this.domainGridlineStroke, that.domainGridlineStroke))
/*5391*/      return false; 
/*5393*/    if (!PaintUtilities.equal(this.domainGridlinePaint, that.domainGridlinePaint))
/*5395*/      return false; 
/*5397*/    if (!ObjectUtilities.equal(this.rangeGridlineStroke, that.rangeGridlineStroke))
/*5399*/      return false; 
/*5401*/    if (!PaintUtilities.equal(this.rangeGridlinePaint, that.rangeGridlinePaint))
/*5403*/      return false; 
/*5405*/    if (!ObjectUtilities.equal(this.domainMinorGridlineStroke, that.domainMinorGridlineStroke))
/*5407*/      return false; 
/*5409*/    if (!PaintUtilities.equal(this.domainMinorGridlinePaint, that.domainMinorGridlinePaint))
/*5411*/      return false; 
/*5413*/    if (!ObjectUtilities.equal(this.rangeMinorGridlineStroke, that.rangeMinorGridlineStroke))
/*5415*/      return false; 
/*5417*/    if (!PaintUtilities.equal(this.rangeMinorGridlinePaint, that.rangeMinorGridlinePaint))
/*5419*/      return false; 
/*5421*/    if (!PaintUtilities.equal(this.domainZeroBaselinePaint, that.domainZeroBaselinePaint))
/*5423*/      return false; 
/*5425*/    if (!ObjectUtilities.equal(this.domainZeroBaselineStroke, that.domainZeroBaselineStroke))
/*5427*/      return false; 
/*5429*/    if (!PaintUtilities.equal(this.rangeZeroBaselinePaint, that.rangeZeroBaselinePaint))
/*5431*/      return false; 
/*5433*/    if (!ObjectUtilities.equal(this.rangeZeroBaselineStroke, that.rangeZeroBaselineStroke))
/*5435*/      return false; 
/*5437*/    if (!ObjectUtilities.equal(this.domainCrosshairStroke, that.domainCrosshairStroke))
/*5439*/      return false; 
/*5441*/    if (!PaintUtilities.equal(this.domainCrosshairPaint, that.domainCrosshairPaint))
/*5443*/      return false; 
/*5445*/    if (!ObjectUtilities.equal(this.rangeCrosshairStroke, that.rangeCrosshairStroke))
/*5447*/      return false; 
/*5449*/    if (!PaintUtilities.equal(this.rangeCrosshairPaint, that.rangeCrosshairPaint))
/*5451*/      return false; 
/*5453*/    if (!ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers))
/*5455*/      return false; 
/*5457*/    if (!ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers))
/*5459*/      return false; 
/*5461*/    if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers))
/*5463*/      return false; 
/*5465*/    if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers))
/*5467*/      return false; 
/*5469*/    if (!ObjectUtilities.equal(this.foregroundDomainMarkers, that.foregroundDomainMarkers))
/*5471*/      return false; 
/*5473*/    if (!ObjectUtilities.equal(this.backgroundDomainMarkers, that.backgroundDomainMarkers))
/*5475*/      return false; 
/*5477*/    if (!ObjectUtilities.equal(this.foregroundRangeMarkers, that.foregroundRangeMarkers))
/*5479*/      return false; 
/*5481*/    if (!ObjectUtilities.equal(this.backgroundRangeMarkers, that.backgroundRangeMarkers))
/*5483*/      return false; 
/*5485*/    if (!ObjectUtilities.equal(this.annotations, that.annotations))
/*5486*/      return false; 
/*5488*/    if (!ObjectUtilities.equal(this.fixedLegendItems, that.fixedLegendItems))
/*5490*/      return false; 
/*5492*/    if (!PaintUtilities.equal(this.domainTickBandPaint, that.domainTickBandPaint))
/*5494*/      return false; 
/*5496*/    if (!PaintUtilities.equal(this.rangeTickBandPaint, that.rangeTickBandPaint))
/*5498*/      return false; 
/*5500*/    if (!this.quadrantOrigin.equals(that.quadrantOrigin))
/*5501*/      return false; 
/*5503*/    for (int i = 0; i < 4; i++) {
/*5504*/      if (!PaintUtilities.equal(this.quadrantPaint[i], that.quadrantPaint[i]))
/*5506*/        return false; 
/*   0*/    } 
/*5509*/    return super.equals(obj);
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*5522*/    XYPlot clone = (XYPlot)super.clone();
/*5523*/    clone.domainAxes = (ObjectList)ObjectUtilities.clone(this.domainAxes);
/*5524*/    for (int i = 0; i < this.domainAxes.size(); i++) {
/*5525*/      ValueAxis axis = (ValueAxis)this.domainAxes.get(i);
/*5526*/      if (axis != null) {
/*5527*/        ValueAxis clonedAxis = (ValueAxis)axis.clone();
/*5528*/        clone.domainAxes.set(i, clonedAxis);
/*5529*/        clonedAxis.setPlot(clone);
/*5530*/        clonedAxis.addChangeListener(clone);
/*   0*/      } 
/*   0*/    } 
/*5533*/    clone.domainAxisLocations = (ObjectList)this.domainAxisLocations.clone();
/*5536*/    clone.rangeAxes = (ObjectList)ObjectUtilities.clone(this.rangeAxes);
/*5537*/    for (int m = 0; m < this.rangeAxes.size(); m++) {
/*5538*/      ValueAxis axis = (ValueAxis)this.rangeAxes.get(m);
/*5539*/      if (axis != null) {
/*5540*/        ValueAxis clonedAxis = (ValueAxis)axis.clone();
/*5541*/        clone.rangeAxes.set(m, clonedAxis);
/*5542*/        clonedAxis.setPlot(clone);
/*5543*/        clonedAxis.addChangeListener(clone);
/*   0*/      } 
/*   0*/    } 
/*5546*/    clone.rangeAxisLocations = (ObjectList)ObjectUtilities.clone(this.rangeAxisLocations);
/*5550*/    clone.datasets = (ObjectList)ObjectUtilities.clone(this.datasets);
/*5551*/    for (int k = 0; k < clone.datasets.size(); k++) {
/*5552*/      XYDataset d = getDataset(k);
/*5553*/      if (d != null)
/*5554*/        d.addChangeListener(clone); 
/*   0*/    } 
/*5558*/    clone.datasetToDomainAxesMap = new TreeMap();
/*5559*/    clone.datasetToDomainAxesMap.putAll(this.datasetToDomainAxesMap);
/*5560*/    clone.datasetToRangeAxesMap = new TreeMap();
/*5561*/    clone.datasetToRangeAxesMap.putAll(this.datasetToRangeAxesMap);
/*5563*/    clone.renderers = (ObjectList)ObjectUtilities.clone(this.renderers);
/*5564*/    for (int j = 0; j < this.renderers.size(); j++) {
/*5565*/      XYItemRenderer renderer2 = (XYItemRenderer)this.renderers.get(j);
/*5566*/      if (renderer2 instanceof PublicCloneable) {
/*5567*/        PublicCloneable pc = (PublicCloneable)renderer2;
/*5568*/        clone.renderers.set(j, pc.clone());
/*   0*/      } 
/*   0*/    } 
/*5571*/    clone.foregroundDomainMarkers = (Map)ObjectUtilities.clone(this.foregroundDomainMarkers);
/*5573*/    clone.backgroundDomainMarkers = (Map)ObjectUtilities.clone(this.backgroundDomainMarkers);
/*5575*/    clone.foregroundRangeMarkers = (Map)ObjectUtilities.clone(this.foregroundRangeMarkers);
/*5577*/    clone.backgroundRangeMarkers = (Map)ObjectUtilities.clone(this.backgroundRangeMarkers);
/*5579*/    clone.annotations = (List)ObjectUtilities.deepClone(this.annotations);
/*5580*/    if (this.fixedDomainAxisSpace != null)
/*5581*/      clone.fixedDomainAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedDomainAxisSpace); 
/*5584*/    if (this.fixedRangeAxisSpace != null)
/*5585*/      clone.fixedRangeAxisSpace = (AxisSpace)ObjectUtilities.clone(this.fixedRangeAxisSpace); 
/*5588*/    if (this.fixedLegendItems != null)
/*5589*/      clone.fixedLegendItems = (LegendItemCollection)this.fixedLegendItems.clone(); 
/*5592*/    clone.quadrantOrigin = (Point2D)ObjectUtilities.clone(this.quadrantOrigin);
/*5594*/    clone.quadrantPaint = (Paint[])this.quadrantPaint.clone();
/*5595*/    return clone;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*5607*/    stream.defaultWriteObject();
/*5608*/    SerialUtilities.writeStroke(this.domainGridlineStroke, stream);
/*5609*/    SerialUtilities.writePaint(this.domainGridlinePaint, stream);
/*5610*/    SerialUtilities.writeStroke(this.rangeGridlineStroke, stream);
/*5611*/    SerialUtilities.writePaint(this.rangeGridlinePaint, stream);
/*5612*/    SerialUtilities.writeStroke(this.domainMinorGridlineStroke, stream);
/*5613*/    SerialUtilities.writePaint(this.domainMinorGridlinePaint, stream);
/*5614*/    SerialUtilities.writeStroke(this.rangeMinorGridlineStroke, stream);
/*5615*/    SerialUtilities.writePaint(this.rangeMinorGridlinePaint, stream);
/*5616*/    SerialUtilities.writeStroke(this.rangeZeroBaselineStroke, stream);
/*5617*/    SerialUtilities.writePaint(this.rangeZeroBaselinePaint, stream);
/*5618*/    SerialUtilities.writeStroke(this.domainCrosshairStroke, stream);
/*5619*/    SerialUtilities.writePaint(this.domainCrosshairPaint, stream);
/*5620*/    SerialUtilities.writeStroke(this.rangeCrosshairStroke, stream);
/*5621*/    SerialUtilities.writePaint(this.rangeCrosshairPaint, stream);
/*5622*/    SerialUtilities.writePaint(this.domainTickBandPaint, stream);
/*5623*/    SerialUtilities.writePaint(this.rangeTickBandPaint, stream);
/*5624*/    SerialUtilities.writePoint2D(this.quadrantOrigin, stream);
/*5625*/    for (int i = 0; i < 4; i++)
/*5626*/      SerialUtilities.writePaint(this.quadrantPaint[i], stream); 
/*5628*/    SerialUtilities.writeStroke(this.domainZeroBaselineStroke, stream);
/*5629*/    SerialUtilities.writePaint(this.domainZeroBaselinePaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*5643*/    stream.defaultReadObject();
/*5644*/    this.domainGridlineStroke = SerialUtilities.readStroke(stream);
/*5645*/    this.domainGridlinePaint = SerialUtilities.readPaint(stream);
/*5646*/    this.rangeGridlineStroke = SerialUtilities.readStroke(stream);
/*5647*/    this.rangeGridlinePaint = SerialUtilities.readPaint(stream);
/*5648*/    this.domainMinorGridlineStroke = SerialUtilities.readStroke(stream);
/*5649*/    this.domainMinorGridlinePaint = SerialUtilities.readPaint(stream);
/*5650*/    this.rangeMinorGridlineStroke = SerialUtilities.readStroke(stream);
/*5651*/    this.rangeMinorGridlinePaint = SerialUtilities.readPaint(stream);
/*5652*/    this.rangeZeroBaselineStroke = SerialUtilities.readStroke(stream);
/*5653*/    this.rangeZeroBaselinePaint = SerialUtilities.readPaint(stream);
/*5654*/    this.domainCrosshairStroke = SerialUtilities.readStroke(stream);
/*5655*/    this.domainCrosshairPaint = SerialUtilities.readPaint(stream);
/*5656*/    this.rangeCrosshairStroke = SerialUtilities.readStroke(stream);
/*5657*/    this.rangeCrosshairPaint = SerialUtilities.readPaint(stream);
/*5658*/    this.domainTickBandPaint = SerialUtilities.readPaint(stream);
/*5659*/    this.rangeTickBandPaint = SerialUtilities.readPaint(stream);
/*5660*/    this.quadrantOrigin = SerialUtilities.readPoint2D(stream);
/*5661*/    this.quadrantPaint = new Paint[4];
/*5662*/    for (int i = 0; i < 4; i++)
/*5663*/      this.quadrantPaint[i] = SerialUtilities.readPaint(stream); 
/*5666*/    this.domainZeroBaselineStroke = SerialUtilities.readStroke(stream);
/*5667*/    this.domainZeroBaselinePaint = SerialUtilities.readPaint(stream);
/*5671*/    int domainAxisCount = this.domainAxes.size();
/*5672*/    for (int j = 0; j < domainAxisCount; j++) {
/*5673*/      Axis axis = (Axis)this.domainAxes.get(j);
/*5674*/      if (axis != null) {
/*5675*/        axis.setPlot(this);
/*5676*/        axis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*5679*/    int rangeAxisCount = this.rangeAxes.size();
/*5680*/    for (int k = 0; k < rangeAxisCount; k++) {
/*5681*/      Axis axis = (Axis)this.rangeAxes.get(k);
/*5682*/      if (axis != null) {
/*5683*/        axis.setPlot(this);
/*5684*/        axis.addChangeListener(this);
/*   0*/      } 
/*   0*/    } 
/*5687*/    int datasetCount = this.datasets.size();
/*5688*/    for (int m = 0; m < datasetCount; m++) {
/*5689*/      Dataset dataset = (Dataset)this.datasets.get(m);
/*5690*/      if (dataset != null)
/*5691*/        dataset.addChangeListener(this); 
/*   0*/    } 
/*5694*/    int rendererCount = this.renderers.size();
/*5695*/    for (int n = 0; n < rendererCount; n++) {
/*5696*/      XYItemRenderer renderer = (XYItemRenderer)this.renderers.get(n);
/*5697*/      if (renderer != null)
/*5698*/        renderer.addChangeListener(this); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canSelectByPoint() {
/*5714*/    return false;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean canSelectByRegion() {
/*5726*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  public void select(double x, double y, Rectangle2D dataArea, RenderingSource source) {}
/*   0*/  
/*   0*/  public void select(GeneralPath region, Rectangle2D dataArea, RenderingSource source) {
/*5752*/    int datasetCount = this.datasets.size();
/*5753*/    for (int d = 0; d < datasetCount; d++) {
/*5754*/      XYDataset dataset = (XYDataset)this.datasets.get(d);
/*5755*/      if (dataset != null) {
/*5758*/        XYDatasetSelectionState state = findSelectionStateForDataset(dataset, source);
/*5760*/        if (state != null) {
/*5763*/          GeneralPath path = convertToDataSpace(region, dataArea, dataset);
/*5767*/          int seriesCount = dataset.getSeriesCount();
/*5768*/          for (int s = 0; s < seriesCount; s++) {
/*5769*/            int itemCount = dataset.getItemCount(s);
/*5770*/            for (int i = 0; i < itemCount; i++) {
/*5771*/              double x = dataset.getXValue(s, i);
/*5772*/              double y = dataset.getYValue(s, i);
/*5773*/              if (path.contains(x, y))
/*5774*/                state.setSelected(s, i, true); 
/*   0*/            } 
/*   0*/          } 
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private XYDatasetSelectionState findSelectionStateForDataset(XYDataset dataset, Object source) {
/*5795*/    if (dataset instanceof SelectableXYDataset) {
/*5796*/      SelectableXYDataset sd = (SelectableXYDataset)dataset;
/*5797*/      XYDatasetSelectionState s = sd.getSelectionState();
/*5798*/      return s;
/*   0*/    } 
/*5800*/    throw new RuntimeException();
/*   0*/  }
/*   0*/  
/*   0*/  private GeneralPath convertToDataSpace(GeneralPath path, Rectangle2D dataArea, XYDataset dataset) {
/*5816*/    GeneralPath result = new GeneralPath(path.getWindingRule());
/*5817*/    int datasetIndex = indexOf(dataset);
/*5818*/    ValueAxis xAxis = getDomainAxisForDataset(datasetIndex);
/*5819*/    ValueAxis yAxis = getRangeAxisForDataset(datasetIndex);
/*5820*/    RectangleEdge xAxisEdge = getDomainAxisEdge();
/*5821*/    RectangleEdge yAxisEdge = getRangeAxisEdge();
/*5822*/    double[] coords = new double[6];
/*5823*/    PathIterator iterator = path.getPathIterator(null);
/*5824*/    while (!iterator.isDone()) {
/*5825*/      int segType = iterator.currentSegment(coords);
/*5826*/      double xx = xAxis.java2DToValue(coords[0], dataArea, xAxisEdge);
/*5827*/      double yy = yAxis.java2DToValue(coords[1], dataArea, yAxisEdge);
/*5828*/      if (segType == 0) {
/*5829*/        result.moveTo((float)xx, (float)yy);
/*5831*/      } else if (segType == 1) {
/*5832*/        result.lineTo((float)xx, (float)yy);
/*5834*/      } else if (segType == 4) {
/*5835*/        result.closePath();
/*   0*/      } 
/*5837*/      iterator.next();
/*   0*/    } 
/*5839*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public void clearSelection() {
/*5849*/    int datasetCount = this.datasets.size();
/*5850*/    for (int d = 0; d < datasetCount; d++) {
/*5851*/      XYDataset dataset = (XYDataset)this.datasets.get(d);
/*5852*/      if (dataset instanceof AbstractXYDataset) {
/*5855*/        AbstractXYDataset axyd = (AbstractXYDataset)dataset;
/*5856*/        if (axyd.getSelectionState() != null) {
/*5857*/          XYDatasetSelectionState selState = axyd.getSelectionState();
/*5858*/          selState.clearSelection();
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/}
