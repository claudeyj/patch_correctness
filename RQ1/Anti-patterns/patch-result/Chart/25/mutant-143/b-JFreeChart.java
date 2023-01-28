/*   0*/package org.jfree.chart;
/*   0*/
/*   0*/import java.awt.AlphaComposite;
/*   0*/import java.awt.BasicStroke;
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Composite;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Image;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.RenderingHints;
/*   0*/import java.awt.Shape;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.AffineTransform;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.awt.image.BufferedImage;
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import javax.swing.event.EventListenerList;
/*   0*/import org.jfree.chart.block.BlockParams;
/*   0*/import org.jfree.chart.block.EntityBlockResult;
/*   0*/import org.jfree.chart.block.LengthConstraintType;
/*   0*/import org.jfree.chart.block.LineBorder;
/*   0*/import org.jfree.chart.block.RectangleConstraint;
/*   0*/import org.jfree.chart.entity.EntityCollection;
/*   0*/import org.jfree.chart.event.ChartChangeEvent;
/*   0*/import org.jfree.chart.event.ChartChangeListener;
/*   0*/import org.jfree.chart.event.ChartProgressEvent;
/*   0*/import org.jfree.chart.event.ChartProgressListener;
/*   0*/import org.jfree.chart.event.PlotChangeEvent;
/*   0*/import org.jfree.chart.event.PlotChangeListener;
/*   0*/import org.jfree.chart.event.TitleChangeEvent;
/*   0*/import org.jfree.chart.event.TitleChangeListener;
/*   0*/import org.jfree.chart.plot.CategoryPlot;
/*   0*/import org.jfree.chart.plot.Plot;
/*   0*/import org.jfree.chart.plot.PlotRenderingInfo;
/*   0*/import org.jfree.chart.plot.XYPlot;
/*   0*/import org.jfree.chart.title.LegendTitle;
/*   0*/import org.jfree.chart.title.TextTitle;
/*   0*/import org.jfree.chart.title.Title;
/*   0*/import org.jfree.chart.ui.ProjectInfo;
/*   0*/import org.jfree.chart.util.Align;
/*   0*/import org.jfree.chart.util.HorizontalAlignment;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/import org.jfree.chart.util.Size2D;
/*   0*/import org.jfree.chart.util.VerticalAlignment;
/*   0*/import org.jfree.data.Range;
/*   0*/
/*   0*/public class JFreeChart implements Drawable, TitleChangeListener, PlotChangeListener, Serializable, Cloneable {
/*   0*/  private static final long serialVersionUID = -3470703747817429120L;
/*   0*/  
/* 243*/  public static final ProjectInfo INFO = new JFreeChartInfo();
/*   0*/  
/* 246*/  public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", 1, 18);
/*   0*/  
/* 250*/  public static final Paint DEFAULT_BACKGROUND_PAINT = Color.WHITE;
/*   0*/  
/* 253*/  public static final Image DEFAULT_BACKGROUND_IMAGE = null;
/*   0*/  
/*   0*/  public static final int DEFAULT_BACKGROUND_IMAGE_ALIGNMENT = 15;
/*   0*/  
/*   0*/  public static final float DEFAULT_BACKGROUND_IMAGE_ALPHA = 0.5F;
/*   0*/  
/*   0*/  private transient RenderingHints renderingHints;
/*   0*/  
/*   0*/  private boolean borderVisible;
/*   0*/  
/*   0*/  private transient Stroke borderStroke;
/*   0*/  
/*   0*/  private transient Paint borderPaint;
/*   0*/  
/*   0*/  private RectangleInsets padding;
/*   0*/  
/*   0*/  private TextTitle title;
/*   0*/  
/*   0*/  private List subtitles;
/*   0*/  
/*   0*/  private Plot plot;
/*   0*/  
/*   0*/  private transient Paint backgroundPaint;
/*   0*/  
/*   0*/  private transient Image backgroundImage;
/*   0*/  
/* 298*/  private int backgroundImageAlignment = 15;
/*   0*/  
/* 301*/  private float backgroundImageAlpha = 0.5F;
/*   0*/  
/*   0*/  private transient EventListenerList changeListeners;
/*   0*/  
/*   0*/  private transient EventListenerList progressListeners;
/*   0*/  
/*   0*/  private boolean notify;
/*   0*/  
/*   0*/  public JFreeChart(Plot plot) {
/* 327*/    this(null, null, plot, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JFreeChart(String title, Plot plot) {
/* 343*/    this(title, DEFAULT_TITLE_FONT, plot, true);
/*   0*/  }
/*   0*/  
/*   0*/  public JFreeChart(String title, Font titleFont, Plot plot, boolean createLegend) {
/* 366*/    if (plot == null)
/* 367*/      throw new NullPointerException("Null 'plot' argument."); 
/* 371*/    this.progressListeners = new EventListenerList();
/* 372*/    this.changeListeners = new EventListenerList();
/* 373*/    this.notify = true;
/* 376*/    this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 380*/    this.borderVisible = false;
/* 381*/    this.borderStroke = new BasicStroke(1.0F);
/* 382*/    this.borderPaint = Color.black;
/* 384*/    this.padding = RectangleInsets.ZERO_INSETS;
/* 386*/    this.plot = plot;
/* 387*/    plot.addChangeListener(this);
/* 389*/    this.subtitles = new ArrayList();
/* 392*/    if (createLegend) {
/* 393*/      LegendTitle legend = new LegendTitle(this.plot);
/* 394*/      legend.setMargin(new RectangleInsets(1.0D, 1.0D, 1.0D, 1.0D));
/* 395*/      legend.setFrame(new LineBorder());
/* 396*/      legend.setBackgroundPaint(Color.white);
/* 397*/      legend.setPosition(RectangleEdge.BOTTOM);
/* 398*/      this.subtitles.add(legend);
/* 399*/      legend.addChangeListener(this);
/*   0*/    } 
/* 403*/    if (title != null) {
/* 404*/      if (titleFont == null)
/* 405*/        titleFont = DEFAULT_TITLE_FONT; 
/* 407*/      this.title = new TextTitle(title, titleFont);
/* 408*/      this.title.addChangeListener(this);
/*   0*/    } 
/* 411*/    this.backgroundPaint = DEFAULT_BACKGROUND_PAINT;
/* 413*/    this.backgroundImage = DEFAULT_BACKGROUND_IMAGE;
/* 414*/    this.backgroundImageAlignment = 15;
/* 415*/    this.backgroundImageAlpha = 0.5F;
/*   0*/  }
/*   0*/  
/*   0*/  public RenderingHints getRenderingHints() {
/* 427*/    return this.renderingHints;
/*   0*/  }
/*   0*/  
/*   0*/  public void setRenderingHints(RenderingHints renderingHints) {
/* 441*/    if (renderingHints == null)
/* 442*/      throw new NullPointerException("RenderingHints given are null"); 
/* 444*/    this.renderingHints = renderingHints;
/* 445*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isBorderVisible() {
/* 457*/    return this.borderVisible;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBorderVisible(boolean visible) {
/* 469*/    this.borderVisible = visible;
/* 470*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getBorderStroke() {
/* 481*/    return this.borderStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBorderStroke(Stroke stroke) {
/* 492*/    this.borderStroke = stroke;
/* 493*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getBorderPaint() {
/* 504*/    return this.borderPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBorderPaint(Paint paint) {
/* 515*/    this.borderPaint = paint;
/* 516*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public RectangleInsets getPadding() {
/* 527*/    return this.padding;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPadding(RectangleInsets padding) {
/* 539*/    if (padding == null)
/* 540*/      throw new IllegalArgumentException("Null 'padding' argument."); 
/* 542*/    this.padding = padding;
/* 543*/    notifyListeners(new ChartChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public TextTitle getTitle() {
/* 557*/    return this.title;
/*   0*/  }
/*   0*/  
/*   0*/  public void setTitle(TextTitle title) {
/* 571*/    if (this.title != null)
/* 572*/      this.title.removeChangeListener(this); 
/* 574*/    this.title = title;
/* 575*/    if (title != null)
/* 576*/      title.addChangeListener(this); 
/* 578*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void setTitle(String text) {
/* 594*/    if (text != null) {
/* 595*/      if (this.title == null) {
/* 596*/        setTitle(new TextTitle(text, DEFAULT_TITLE_FONT));
/*   0*/      } else {
/* 599*/        this.title.setText(text);
/*   0*/      } 
/*   0*/    } else {
/* 603*/      setTitle((TextTitle)null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addLegend(LegendTitle legend) {
/* 616*/    addSubtitle(legend);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendTitle getLegend() {
/* 628*/    return getLegend(0);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendTitle getLegend(int index) {
/* 641*/    int seen = 0;
/* 642*/    Iterator<Title> iterator = this.subtitles.iterator();
/* 643*/    while (iterator.hasNext()) {
/* 644*/      Title subtitle = iterator.next();
/* 645*/      if (subtitle instanceof LegendTitle) {
/* 646*/        if (seen == index)
/* 647*/          return (LegendTitle)subtitle; 
/* 650*/        seen++;
/*   0*/      } 
/*   0*/    } 
/* 654*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeLegend() {
/* 664*/    removeSubtitle(getLegend());
/*   0*/  }
/*   0*/  
/*   0*/  public List getSubtitles() {
/* 675*/    return new ArrayList(this.subtitles);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSubtitles(List subtitles) {
/* 689*/    if (subtitles == null)
/* 690*/      throw new NullPointerException("Null 'subtitles' argument."); 
/* 692*/    setNotify(false);
/* 693*/    clearSubtitles();
/* 694*/    Iterator<Title> iterator = subtitles.iterator();
/* 695*/    while (iterator.hasNext()) {
/* 696*/      Title t = iterator.next();
/* 697*/      if (t != null)
/* 698*/        addSubtitle(t); 
/*   0*/    } 
/* 701*/    setNotify(true);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSubtitleCount() {
/* 712*/    return this.subtitles.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Title getSubtitle(int index) {
/* 725*/    if (index < 0 || index >= getSubtitleCount())
/* 726*/      throw new IllegalArgumentException("Index out of range."); 
/* 728*/    return this.subtitles.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSubtitle(Title subtitle) {
/* 740*/    if (subtitle == null)
/* 741*/      throw new IllegalArgumentException("Null 'subtitle' argument."); 
/* 743*/    this.subtitles.add(subtitle);
/* 744*/    subtitle.addChangeListener(this);
/* 745*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void addSubtitle(int index, Title subtitle) {
/* 758*/    if (index < 0 || index > getSubtitleCount())
/* 759*/      throw new IllegalArgumentException("The 'index' argument is out of range."); 
/* 762*/    if (subtitle == null)
/* 763*/      throw new IllegalArgumentException("Null 'subtitle' argument."); 
/* 765*/    this.subtitles.add(index, subtitle);
/* 766*/    subtitle.addChangeListener(this);
/* 767*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearSubtitles() {
/* 777*/    Iterator<Title> iterator = this.subtitles.iterator();
/* 778*/    while (iterator.hasNext()) {
/* 779*/      Title t = iterator.next();
/* 780*/      t.removeChangeListener(this);
/*   0*/    } 
/* 782*/    this.subtitles.clear();
/* 783*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void removeSubtitle(Title title) {
/* 795*/    this.subtitles.remove(title);
/* 796*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Plot getPlot() {
/* 807*/    return this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot getCategoryPlot() {
/* 821*/    return (CategoryPlot)this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public XYPlot getXYPlot() {
/* 835*/    return (XYPlot)this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAntiAlias() {
/* 847*/    Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
/* 848*/    return RenderingHints.VALUE_ANTIALIAS_ON.equals(val);
/*   0*/  }
/*   0*/  
/*   0*/  public void setAntiAlias(boolean flag) {
/* 863*/    Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
/* 864*/    if (val == null)
/* 865*/      val = RenderingHints.VALUE_ANTIALIAS_DEFAULT; 
/* 867*/    if ((!flag && RenderingHints.VALUE_ANTIALIAS_OFF.equals(val)) || (flag && RenderingHints.VALUE_ANTIALIAS_ON.equals(val)))
/*   0*/      return; 
/* 872*/    if (flag) {
/* 873*/      this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*   0*/    } else {
/* 877*/      this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
/*   0*/    } 
/* 880*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getTextAntiAlias() {
/* 895*/    return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
/*   0*/  }
/*   0*/  
/*   0*/  public void setTextAntiAlias(boolean flag) {
/* 913*/    if (flag) {
/* 914*/      setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*   0*/    } else {
/* 917*/      setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setTextAntiAlias(Object val) {
/* 934*/    this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
/* 935*/    notifyListeners(new ChartChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getBackgroundPaint() {
/* 946*/    return this.backgroundPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundPaint(Paint paint) {
/* 959*/    if (this.backgroundPaint != null) {
/* 960*/      if (!this.backgroundPaint.equals(paint)) {
/* 961*/        this.backgroundPaint = paint;
/* 962*/        fireChartChanged();
/*   0*/      } 
/* 966*/    } else if (paint != null) {
/* 967*/      this.backgroundPaint = paint;
/* 968*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Image getBackgroundImage() {
/* 983*/    return this.backgroundImage;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImage(Image image) {
/* 996*/    if (this.backgroundImage != null) {
/* 997*/      if (!this.backgroundImage.equals(image)) {
/* 998*/        this.backgroundImage = image;
/* 999*/        fireChartChanged();
/*   0*/      } 
/*1003*/    } else if (image != null) {
/*1004*/      this.backgroundImage = image;
/*1005*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getBackgroundImageAlignment() {
/*1021*/    return this.backgroundImageAlignment;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImageAlignment(int alignment) {
/*1033*/    if (this.backgroundImageAlignment != alignment) {
/*1034*/      this.backgroundImageAlignment = alignment;
/*1035*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public float getBackgroundImageAlpha() {
/*1047*/    return this.backgroundImageAlpha;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImageAlpha(float alpha) {
/*1060*/    if (this.backgroundImageAlpha != alpha) {
/*1061*/      this.backgroundImageAlpha = alpha;
/*1062*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNotify() {
/*1076*/    return this.notify;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNotify(boolean notify) {
/*1088*/    this.notify = notify;
/*1090*/    if (notify)
/*1091*/      notifyListeners(new ChartChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area) {
/*1105*/    draw(g2, area, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
/*1117*/    draw(g2, area, null, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
/*1136*/    notifyListeners(new ChartProgressEvent(this, this, 1, 0));
/*1140*/    if (info != null) {
/*1141*/      info.clear();
/*1142*/      info.setChartArea(chartArea);
/*   0*/    } 
/*1146*/    Shape savedClip = g2.getClip();
/*1147*/    g2.clip(chartArea);
/*1149*/    g2.addRenderingHints(this.renderingHints);
/*1152*/    if (this.backgroundPaint != null) {
/*1153*/      g2.setPaint(this.backgroundPaint);
/*1154*/      g2.fill(chartArea);
/*   0*/    } 
/*1157*/    if (this.backgroundImage != null) {
/*1158*/      Composite originalComposite = g2.getComposite();
/*1159*/      g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
/*1161*/      Rectangle2D dest = new Rectangle2D.Double(0.0D, 0.0D, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null));
/*1164*/      Align.align(dest, chartArea, this.backgroundImageAlignment);
/*1165*/      g2.drawImage(this.backgroundImage, (int)dest.getX(), (int)dest.getY(), (int)dest.getWidth(), (int)dest.getHeight(), null);
/*1168*/      g2.setComposite(originalComposite);
/*   0*/    } 
/*1171*/    if (isBorderVisible()) {
/*1172*/      Paint paint = getBorderPaint();
/*1173*/      Stroke stroke = getBorderStroke();
/*1174*/      if (paint != null && stroke != null) {
/*1175*/        Rectangle2D borderArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(), chartArea.getWidth() - 1.0D, chartArea.getHeight() - 1.0D);
/*1179*/        g2.setPaint(paint);
/*1180*/        g2.setStroke(stroke);
/*1181*/        g2.draw(borderArea);
/*   0*/      } 
/*   0*/    } 
/*1186*/    Rectangle2D nonTitleArea = new Rectangle2D.Double();
/*1187*/    nonTitleArea.setRect(chartArea);
/*1188*/    this.padding.trim(nonTitleArea);
/*1190*/    EntityCollection entities = null;
/*1191*/    if (info != null)
/*1192*/      entities = info.getEntityCollection(); 
/*1194*/    if (this.title != null) {
/*1195*/      EntityCollection e = drawTitle(this.title, g2, nonTitleArea, (entities != null));
/*1197*/      if (e != null)
/*1198*/        entities.addAll(e); 
/*   0*/    } 
/*1202*/    Iterator<Title> iterator = this.subtitles.iterator();
/*1203*/    while (iterator.hasNext()) {
/*1204*/      Title currentTitle = iterator.next();
/*1205*/      EntityCollection e = drawTitle(currentTitle, g2, nonTitleArea, (entities != null));
/*1207*/      if (e != null)
/*1208*/        entities.addAll(e); 
/*   0*/    } 
/*1212*/    Rectangle2D plotArea = nonTitleArea;
/*1215*/    PlotRenderingInfo plotInfo = null;
/*1216*/    if (info != null)
/*1217*/      plotInfo = info.getPlotInfo(); 
/*1219*/    this.plot.draw(g2, plotArea, anchor, null, plotInfo);
/*1221*/    g2.setClip(savedClip);
/*1223*/    notifyListeners(new ChartProgressEvent(this, this, 2, 100));
/*   0*/  }
/*   0*/  
/*   0*/  private Rectangle2D createAlignedRectangle2D(Size2D dimensions, Rectangle2D frame, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
/*1240*/    double x = Double.NaN;
/*1241*/    double y = Double.NaN;
/*1242*/    if (hAlign == HorizontalAlignment.LEFT) {
/*1243*/      x = frame.getX();
/*1245*/    } else if (hAlign == HorizontalAlignment.CENTER) {
/*1246*/      x = frame.getCenterX() - dimensions.width / 2.0D;
/*1248*/    } else if (hAlign == HorizontalAlignment.RIGHT) {
/*1249*/      x = frame.getMaxX() - dimensions.width;
/*   0*/    } 
/*1251*/    if (vAlign == VerticalAlignment.TOP) {
/*1252*/      y = frame.getY();
/*1254*/    } else if (vAlign == VerticalAlignment.CENTER) {
/*1255*/      y = frame.getCenterY() - dimensions.height / 2.0D;
/*1257*/    } else if (vAlign == VerticalAlignment.BOTTOM) {
/*1258*/      y = frame.getMaxY() - dimensions.height;
/*   0*/    } 
/*1261*/    return new Rectangle2D.Double(x, y, dimensions.width, dimensions.height);
/*   0*/  }
/*   0*/  
/*   0*/  protected EntityCollection drawTitle(Title t, Graphics2D g2, Rectangle2D area, boolean entities) {
/*1282*/    if (t == null)
/*1283*/      throw new IllegalArgumentException("Null 't' argument."); 
/*1285*/    if (area == null)
/*1286*/      throw new IllegalArgumentException("Null 'area' argument."); 
/*1288*/    Rectangle2D titleArea = new Rectangle2D.Double();
/*1289*/    RectangleEdge position = t.getPosition();
/*1290*/    double ww = area.getWidth();
/*1291*/    if (ww <= 0.0D)
/*1292*/      return null; 
/*1294*/    double hh = area.getHeight();
/*1295*/    if (hh <= 0.0D)
/*1296*/      return null; 
/*1298*/    RectangleConstraint constraint = new RectangleConstraint(ww, new Range(0.0D, ww), LengthConstraintType.RANGE, hh, new Range(0.0D, hh), LengthConstraintType.RANGE);
/*1301*/    Object retValue = null;
/*1302*/    BlockParams p = new BlockParams();
/*1303*/    p.setGenerateEntities(entities);
/*1304*/    if (position == RectangleEdge.TOP) {
/*1305*/      Size2D size = t.arrange(g2, constraint);
/*1306*/      titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP);
/*1308*/      retValue = t.draw(g2, titleArea, p);
/*1309*/      area.setRect(area.getX(), Math.min(area.getY() + size.height, area.getMaxY()), area.getWidth(), Math.max(area.getHeight() - size.height, 0.0D));
/*1313*/    } else if (position == RectangleEdge.BOTTOM) {
/*1314*/      Size2D size = t.arrange(g2, constraint);
/*1315*/      titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
/*1317*/      retValue = t.draw(g2, titleArea, p);
/*1318*/      area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
/*1321*/    } else if (position == RectangleEdge.RIGHT) {
/*1322*/      Size2D size = t.arrange(g2, constraint);
/*1323*/      titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment());
/*1325*/      retValue = t.draw(g2, titleArea, p);
/*1326*/      area.setRect(area.getX(), area.getY(), area.getWidth() - size.width, area.getHeight());
/*1330*/    } else if (position == RectangleEdge.LEFT) {
/*1331*/      Size2D size = t.arrange(g2, constraint);
/*1332*/      titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment());
/*1334*/      retValue = t.draw(g2, titleArea, p);
/*1335*/      area.setRect(area.getX() + size.width, area.getY(), area.getWidth() - size.width, area.getHeight());
/*   0*/    } else {
/*1339*/      throw new RuntimeException("Unrecognised title position.");
/*   0*/    } 
/*1341*/    EntityCollection result = null;
/*1342*/    if (retValue instanceof EntityBlockResult) {
/*1343*/      EntityBlockResult ebr = (EntityBlockResult)retValue;
/*1344*/      result = ebr.getEntityCollection();
/*   0*/    } 
/*1346*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height) {
/*1358*/    return createBufferedImage(width, height, null);
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
/*1373*/    return createBufferedImage(width, this.backgroundImageAlignment, 1, info);
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
/*1391*/    BufferedImage image = new BufferedImage(width, height, imageType);
/*1392*/    Graphics2D g2 = image.createGraphics();
/*1393*/    draw(g2, new Rectangle2D.Double(0.0D, 0.0D, width, height), null, info);
/*1394*/    g2.dispose();
/*1395*/    return image;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
/*1418*/    BufferedImage image = new BufferedImage(imageWidth, imageHeight, 1);
/*1420*/    Graphics2D g2 = image.createGraphics();
/*1421*/    double scaleX = imageWidth / drawWidth;
/*1422*/    double scaleY = imageHeight / drawHeight;
/*1423*/    AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
/*1424*/    g2.transform(st);
/*1425*/    draw(g2, new Rectangle2D.Double(0.0D, 0.0D, drawWidth, drawHeight), null, info);
/*1427*/    g2.dispose();
/*1428*/    return image;
/*   0*/  }
/*   0*/  
/*   0*/  public void handleClick(int x, int y, ChartRenderingInfo info) {
/*1448*/    this.plot.handleClick(x, y, info.getPlotInfo());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChangeListener(ChartChangeListener listener) {
/*1460*/    if (listener == null)
/*1461*/      throw new IllegalArgumentException("Null 'listener' argument."); 
/*1463*/    this.changeListeners.add(ChartChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChangeListener(ChartChangeListener listener) {
/*1474*/    if (listener == null)
/*1475*/      throw new IllegalArgumentException("Null 'listener' argument."); 
/*1477*/    this.changeListeners.remove(ChartChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void fireChartChanged() {
/*1486*/    ChartChangeEvent event = new ChartChangeEvent(this);
/*1487*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(ChartChangeEvent event) {
/*1497*/    if (this.notify) {
/*1498*/      Object[] listeners = this.changeListeners.getListenerList();
/*1499*/      for (int i = listeners.length - 2; i >= 0; i -= 2) {
/*1500*/        if (listeners[i] == ChartChangeListener.class)
/*1501*/          ((ChartChangeListener)listeners[i + 1]).chartChanged(event); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addProgressListener(ChartProgressListener listener) {
/*1517*/    this.progressListeners.add(ChartProgressListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProgressListener(ChartProgressListener listener) {
/*1528*/    this.progressListeners.remove(ChartProgressListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(ChartProgressEvent event) {
/*1539*/    Object[] listeners = this.progressListeners.getListenerList();
/*1540*/    for (int i = listeners.length - 2; i >= 0; i -= 2) {
/*1541*/      if (listeners[i] == ChartProgressListener.class)
/*1542*/        ((ChartProgressListener)listeners[i + 1]).chartProgress(event); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void titleChanged(TitleChangeEvent event) {
/*1555*/    event.setChart(this);
/*1556*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  public void plotChanged(PlotChangeEvent event) {
/*1566*/    event.setChart(this);
/*1567*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1578*/    if (obj == this)
/*1579*/      return true; 
/*1581*/    if (!(obj instanceof JFreeChart))
/*1582*/      return false; 
/*1584*/    JFreeChart that = (JFreeChart)obj;
/*1585*/    if (!this.renderingHints.equals(that.renderingHints))
/*1586*/      return false; 
/*1588*/    if (this.borderVisible != that.borderVisible)
/*1589*/      return false; 
/*1591*/    if (!ObjectUtilities.equal(this.borderStroke, that.borderStroke))
/*1592*/      return false; 
/*1594*/    if (!PaintUtilities.equal(this.borderPaint, that.borderPaint))
/*1595*/      return false; 
/*1597*/    if (!this.padding.equals(that.padding))
/*1598*/      return false; 
/*1600*/    if (!ObjectUtilities.equal(this.title, that.title))
/*1601*/      return false; 
/*1603*/    if (!ObjectUtilities.equal(this.subtitles, that.subtitles))
/*1604*/      return false; 
/*1606*/    if (!ObjectUtilities.equal(this.plot, that.plot))
/*1607*/      return false; 
/*1609*/    if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint))
/*1612*/      return false; 
/*1614*/    if (!ObjectUtilities.equal(this.backgroundImage, that.backgroundImage))
/*1616*/      return false; 
/*1618*/    if (this.backgroundImageAlignment != that.backgroundImageAlignment)
/*1619*/      return false; 
/*1621*/    if (this.backgroundImageAlpha != that.backgroundImageAlpha)
/*1622*/      return false; 
/*1624*/    if (this.notify != that.notify)
/*1625*/      return false; 
/*1627*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*1638*/    stream.defaultWriteObject();
/*1639*/    SerialUtilities.writeStroke(this.borderStroke, stream);
/*1640*/    SerialUtilities.writePaint(this.borderPaint, stream);
/*1641*/    SerialUtilities.writePaint(this.backgroundPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*1654*/    stream.defaultReadObject();
/*1655*/    this.borderStroke = SerialUtilities.readStroke(stream);
/*1656*/    this.borderPaint = SerialUtilities.readPaint(stream);
/*1657*/    this.backgroundPaint = SerialUtilities.readPaint(stream);
/*1658*/    this.progressListeners = new EventListenerList();
/*1659*/    this.changeListeners = new EventListenerList();
/*1660*/    this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*1665*/    if (this.title != null)
/*1666*/      this.title.addChangeListener(this); 
/*1669*/    for (int i = 0; i < getSubtitleCount(); i++)
/*1670*/      getSubtitle(i).addChangeListener(this); 
/*1672*/    this.plot.addChangeListener(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static void main(String[] args) {
/*1681*/    System.out.println(INFO.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1693*/    JFreeChart chart = (JFreeChart)super.clone();
/*1695*/    chart.renderingHints = (RenderingHints)this.renderingHints.clone();
/*1700*/    if (this.title != null) {
/*1701*/      chart.title = (TextTitle)this.title.clone();
/*1702*/      chart.title.addChangeListener(chart);
/*   0*/    } 
/*1705*/    chart.subtitles = new ArrayList();
/*1706*/    for (int i = 0; i < getSubtitleCount(); i++) {
/*1707*/      Title subtitle = (Title)getSubtitle(i).clone();
/*1708*/      chart.subtitles.add(subtitle);
/*1709*/      subtitle.addChangeListener(chart);
/*   0*/    } 
/*1712*/    if (this.plot != null) {
/*1713*/      chart.plot = (Plot)this.plot.clone();
/*1714*/      chart.plot.addChangeListener(chart);
/*   0*/    } 
/*1717*/    chart.progressListeners = new EventListenerList();
/*1718*/    chart.changeListeners = new EventListenerList();
/*1719*/    return chart;
/*   0*/  }
/*   0*/}
