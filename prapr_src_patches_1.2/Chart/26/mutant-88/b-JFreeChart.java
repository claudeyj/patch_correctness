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
/*   0*/import javax.swing.UIManager;
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
/* 242*/  public static final ProjectInfo INFO = new JFreeChartInfo();
/*   0*/  
/* 245*/  public static final Font DEFAULT_TITLE_FONT = new Font("SansSerif", 1, 18);
/*   0*/  
/* 249*/  public static final Paint DEFAULT_BACKGROUND_PAINT = UIManager.getColor("Panel.background");
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
/* 571*/    this.title = title;
/* 572*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void setTitle(String text) {
/* 588*/    if (text != null) {
/* 589*/      if (this.title == null) {
/* 590*/        setTitle(new TextTitle(text, DEFAULT_TITLE_FONT));
/*   0*/      } else {
/* 593*/        this.title.setText(text);
/*   0*/      } 
/*   0*/    } else {
/* 597*/      setTitle((TextTitle)null);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addLegend(LegendTitle legend) {
/* 610*/    addSubtitle(legend);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendTitle getLegend() {
/* 622*/    return getLegend(0);
/*   0*/  }
/*   0*/  
/*   0*/  public LegendTitle getLegend(int index) {
/* 635*/    int seen = 0;
/* 636*/    Iterator<Title> iterator = this.subtitles.iterator();
/* 637*/    while (iterator.hasNext()) {
/* 638*/      Title subtitle = iterator.next();
/* 639*/      if (subtitle instanceof LegendTitle) {
/* 640*/        if (seen == index)
/* 641*/          return (LegendTitle)subtitle; 
/* 644*/        seen++;
/*   0*/      } 
/*   0*/    } 
/* 648*/    return null;
/*   0*/  }
/*   0*/  
/*   0*/  public void removeLegend() {
/* 658*/    removeSubtitle(getLegend());
/*   0*/  }
/*   0*/  
/*   0*/  public List getSubtitles() {
/* 669*/    return new ArrayList(this.subtitles);
/*   0*/  }
/*   0*/  
/*   0*/  public void setSubtitles(List subtitles) {
/* 683*/    if (subtitles == null)
/* 684*/      throw new NullPointerException("Null 'subtitles' argument."); 
/* 686*/    setNotify(false);
/* 687*/    clearSubtitles();
/* 688*/    Iterator<Title> iterator = subtitles.iterator();
/* 689*/    while (iterator.hasNext()) {
/* 690*/      Title t = iterator.next();
/* 691*/      if (t != null)
/* 692*/        addSubtitle(t); 
/*   0*/    } 
/* 695*/    setNotify(true);
/*   0*/  }
/*   0*/  
/*   0*/  public int getSubtitleCount() {
/* 706*/    return this.subtitles.size();
/*   0*/  }
/*   0*/  
/*   0*/  public Title getSubtitle(int index) {
/* 719*/    if (index < 0 || index >= getSubtitleCount())
/* 720*/      throw new IllegalArgumentException("Index out of range."); 
/* 722*/    return this.subtitles.get(index);
/*   0*/  }
/*   0*/  
/*   0*/  public void addSubtitle(Title subtitle) {
/* 734*/    if (subtitle == null)
/* 735*/      throw new IllegalArgumentException("Null 'subtitle' argument."); 
/* 737*/    this.subtitles.add(subtitle);
/* 738*/    subtitle.addChangeListener(this);
/* 739*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void addSubtitle(int index, Title subtitle) {
/* 752*/    if (index < 0 || index > getSubtitleCount())
/* 753*/      throw new IllegalArgumentException("The 'index' argument is out of range."); 
/* 756*/    if (subtitle == null)
/* 757*/      throw new IllegalArgumentException("Null 'subtitle' argument."); 
/* 759*/    this.subtitles.add(index, subtitle);
/* 760*/    subtitle.addChangeListener(this);
/* 761*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void clearSubtitles() {
/* 771*/    Iterator<Title> iterator = this.subtitles.iterator();
/* 772*/    while (iterator.hasNext()) {
/* 773*/      Title t = iterator.next();
/* 774*/      t.removeChangeListener(this);
/*   0*/    } 
/* 776*/    this.subtitles.clear();
/* 777*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public void removeSubtitle(Title title) {
/* 789*/    this.subtitles.remove(title);
/* 790*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Plot getPlot() {
/* 801*/    return this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryPlot getCategoryPlot() {
/* 815*/    return (CategoryPlot)this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public XYPlot getXYPlot() {
/* 829*/    return (XYPlot)this.plot;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean getAntiAlias() {
/* 841*/    Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
/* 842*/    return RenderingHints.VALUE_ANTIALIAS_ON.equals(val);
/*   0*/  }
/*   0*/  
/*   0*/  public void setAntiAlias(boolean flag) {
/* 857*/    Object val = this.renderingHints.get(RenderingHints.KEY_ANTIALIASING);
/* 858*/    if (val == null)
/* 859*/      val = RenderingHints.VALUE_ANTIALIAS_DEFAULT; 
/* 861*/    if ((!flag && RenderingHints.VALUE_ANTIALIAS_OFF.equals(val)) || (flag && RenderingHints.VALUE_ANTIALIAS_ON.equals(val)))
/*   0*/      return; 
/* 866*/    if (flag) {
/* 867*/      this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*   0*/    } else {
/* 871*/      this.renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
/*   0*/    } 
/* 874*/    fireChartChanged();
/*   0*/  }
/*   0*/  
/*   0*/  public Object getTextAntiAlias() {
/* 889*/    return this.renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
/*   0*/  }
/*   0*/  
/*   0*/  public void setTextAntiAlias(boolean flag) {
/* 907*/    if (flag) {
/* 908*/      setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
/*   0*/    } else {
/* 911*/      setTextAntiAlias(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void setTextAntiAlias(Object val) {
/* 928*/    this.renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, val);
/* 929*/    notifyListeners(new ChartChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getBackgroundPaint() {
/* 940*/    return this.backgroundPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundPaint(Paint paint) {
/* 953*/    if (this.backgroundPaint != null) {
/* 954*/      if (!this.backgroundPaint.equals(paint)) {
/* 955*/        this.backgroundPaint = paint;
/* 956*/        fireChartChanged();
/*   0*/      } 
/* 960*/    } else if (paint != null) {
/* 961*/      this.backgroundPaint = paint;
/* 962*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Image getBackgroundImage() {
/* 977*/    return this.backgroundImage;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImage(Image image) {
/* 990*/    if (this.backgroundImage != null) {
/* 991*/      if (!this.backgroundImage.equals(image)) {
/* 992*/        this.backgroundImage = image;
/* 993*/        fireChartChanged();
/*   0*/      } 
/* 997*/    } else if (image != null) {
/* 998*/      this.backgroundImage = image;
/* 999*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public int getBackgroundImageAlignment() {
/*1015*/    return this.backgroundImageAlignment;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImageAlignment(int alignment) {
/*1027*/    if (this.backgroundImageAlignment != alignment) {
/*1028*/      this.backgroundImageAlignment = alignment;
/*1029*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public float getBackgroundImageAlpha() {
/*1041*/    return this.backgroundImageAlpha;
/*   0*/  }
/*   0*/  
/*   0*/  public void setBackgroundImageAlpha(float alpha) {
/*1054*/    if (this.backgroundImageAlpha != alpha) {
/*1055*/      this.backgroundImageAlpha = alpha;
/*1056*/      fireChartChanged();
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isNotify() {
/*1070*/    return this.notify;
/*   0*/  }
/*   0*/  
/*   0*/  public void setNotify(boolean notify) {
/*1082*/    this.notify = notify;
/*1084*/    if (notify)
/*1085*/      notifyListeners(new ChartChangeEvent(this)); 
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area) {
/*1099*/    draw(g2, area, null, null);
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, ChartRenderingInfo info) {
/*1111*/    draw(g2, area, null, info);
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D chartArea, Point2D anchor, ChartRenderingInfo info) {
/*1130*/    notifyListeners(new ChartProgressEvent(this, this, 1, 0));
/*1134*/    if (info != null) {
/*1135*/      info.clear();
/*1136*/      info.setChartArea(chartArea);
/*   0*/    } 
/*1140*/    Shape savedClip = g2.getClip();
/*1141*/    g2.clip(chartArea);
/*1143*/    g2.addRenderingHints(this.renderingHints);
/*1146*/    if (this.backgroundPaint != null) {
/*1147*/      g2.setPaint(this.backgroundPaint);
/*1148*/      g2.fill(chartArea);
/*   0*/    } 
/*1151*/    if (this.backgroundImage != null) {
/*1152*/      Composite originalComposite = g2.getComposite();
/*1153*/      g2.setComposite(AlphaComposite.getInstance(3, this.backgroundImageAlpha));
/*1155*/      Rectangle2D dest = new Rectangle2D.Double(0.0D, 0.0D, this.backgroundImage.getWidth(null), this.backgroundImage.getHeight(null));
/*1158*/      Align.align(dest, chartArea, this.backgroundImageAlignment);
/*1159*/      g2.drawImage(this.backgroundImage, (int)dest.getX(), (int)dest.getY(), (int)dest.getWidth(), (int)dest.getHeight(), null);
/*1162*/      g2.setComposite(originalComposite);
/*   0*/    } 
/*1165*/    if (isBorderVisible()) {
/*1166*/      Paint paint = getBorderPaint();
/*1167*/      Stroke stroke = getBorderStroke();
/*1168*/      if (paint != null && stroke != null) {
/*1169*/        Rectangle2D borderArea = new Rectangle2D.Double(chartArea.getX(), chartArea.getY(), chartArea.getWidth() - 1.0D, chartArea.getHeight() - 1.0D);
/*1173*/        g2.setPaint(paint);
/*1174*/        g2.setStroke(stroke);
/*1175*/        g2.draw(borderArea);
/*   0*/      } 
/*   0*/    } 
/*1180*/    Rectangle2D nonTitleArea = new Rectangle2D.Double();
/*1181*/    Rectangle2D rectangle2D1 = chartArea;
/*1181*/    nonTitleArea.setRect(0.0D, 0.0D, 0.0D, 0.0D);
/*1182*/    this.padding.trim(nonTitleArea);
/*1184*/    EntityCollection entities = null;
/*1185*/    if (info != null)
/*1186*/      entities = info.getEntityCollection(); 
/*1188*/    if (this.title != null) {
/*1189*/      EntityCollection e = drawTitle(this.title, g2, nonTitleArea, (entities != null));
/*1191*/      if (e != null)
/*1192*/        entities.addAll(e); 
/*   0*/    } 
/*1196*/    Iterator<Title> iterator = this.subtitles.iterator();
/*1197*/    while (iterator.hasNext()) {
/*1198*/      Title currentTitle = iterator.next();
/*1199*/      EntityCollection e = drawTitle(currentTitle, g2, nonTitleArea, (entities != null));
/*1201*/      if (e != null)
/*1202*/        entities.addAll(e); 
/*   0*/    } 
/*1206*/    Rectangle2D plotArea = nonTitleArea;
/*1209*/    PlotRenderingInfo plotInfo = null;
/*1210*/    if (info != null)
/*1211*/      plotInfo = info.getPlotInfo(); 
/*1213*/    this.plot.draw(g2, plotArea, anchor, null, plotInfo);
/*1215*/    g2.setClip(savedClip);
/*1217*/    notifyListeners(new ChartProgressEvent(this, this, 2, 100));
/*   0*/  }
/*   0*/  
/*   0*/  private Rectangle2D createAlignedRectangle2D(Size2D dimensions, Rectangle2D frame, HorizontalAlignment hAlign, VerticalAlignment vAlign) {
/*1234*/    double x = Double.NaN;
/*1235*/    double y = Double.NaN;
/*1236*/    if (hAlign == HorizontalAlignment.LEFT) {
/*1237*/      x = frame.getX();
/*1239*/    } else if (hAlign == HorizontalAlignment.CENTER) {
/*1240*/      x = frame.getCenterX() - dimensions.width / 2.0D;
/*1242*/    } else if (hAlign == HorizontalAlignment.RIGHT) {
/*1243*/      x = frame.getMaxX() - dimensions.width;
/*   0*/    } 
/*1245*/    if (vAlign == VerticalAlignment.TOP) {
/*1246*/      y = frame.getY();
/*1248*/    } else if (vAlign == VerticalAlignment.CENTER) {
/*1249*/      y = frame.getCenterY() - dimensions.height / 2.0D;
/*1251*/    } else if (vAlign == VerticalAlignment.BOTTOM) {
/*1252*/      y = frame.getMaxY() - dimensions.height;
/*   0*/    } 
/*1255*/    return new Rectangle2D.Double(x, y, dimensions.width, dimensions.height);
/*   0*/  }
/*   0*/  
/*   0*/  protected EntityCollection drawTitle(Title t, Graphics2D g2, Rectangle2D area, boolean entities) {
/*1276*/    if (t == null)
/*1277*/      throw new IllegalArgumentException("Null 't' argument."); 
/*1279*/    if (area == null)
/*1280*/      throw new IllegalArgumentException("Null 'area' argument."); 
/*1282*/    Rectangle2D titleArea = new Rectangle2D.Double();
/*1283*/    RectangleEdge position = t.getPosition();
/*1284*/    double ww = area.getWidth();
/*1285*/    if (ww <= 0.0D)
/*1286*/      return null; 
/*1288*/    double hh = area.getHeight();
/*1289*/    if (hh <= 0.0D)
/*1290*/      return null; 
/*1292*/    RectangleConstraint constraint = new RectangleConstraint(ww, new Range(0.0D, ww), LengthConstraintType.RANGE, hh, new Range(0.0D, hh), LengthConstraintType.RANGE);
/*1295*/    Object retValue = null;
/*1296*/    BlockParams p = new BlockParams();
/*1297*/    p.setGenerateEntities(entities);
/*1298*/    if (position == RectangleEdge.TOP) {
/*1299*/      Size2D size = t.arrange(g2, constraint);
/*1300*/      titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.TOP);
/*1302*/      retValue = t.draw(g2, titleArea, p);
/*1303*/      area.setRect(area.getX(), Math.min(area.getY() + size.height, area.getMaxY()), area.getWidth(), Math.max(area.getHeight() - size.height, 0.0D));
/*1307*/    } else if (position == RectangleEdge.BOTTOM) {
/*1308*/      Size2D size = t.arrange(g2, constraint);
/*1309*/      titleArea = createAlignedRectangle2D(size, area, t.getHorizontalAlignment(), VerticalAlignment.BOTTOM);
/*1311*/      retValue = t.draw(g2, titleArea, p);
/*1312*/      area.setRect(area.getX(), area.getY(), area.getWidth(), area.getHeight() - size.height);
/*1315*/    } else if (position == RectangleEdge.RIGHT) {
/*1316*/      Size2D size = t.arrange(g2, constraint);
/*1317*/      titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.RIGHT, t.getVerticalAlignment());
/*1319*/      retValue = t.draw(g2, titleArea, p);
/*1320*/      area.setRect(area.getX(), area.getY(), area.getWidth() - size.width, area.getHeight());
/*1324*/    } else if (position == RectangleEdge.LEFT) {
/*1325*/      Size2D size = t.arrange(g2, constraint);
/*1326*/      titleArea = createAlignedRectangle2D(size, area, HorizontalAlignment.LEFT, t.getVerticalAlignment());
/*1328*/      retValue = t.draw(g2, titleArea, p);
/*1329*/      area.setRect(area.getX() + size.width, area.getY(), area.getWidth() - size.width, area.getHeight());
/*   0*/    } else {
/*1333*/      throw new RuntimeException("Unrecognised title position.");
/*   0*/    } 
/*1335*/    EntityCollection result = null;
/*1336*/    if (retValue instanceof EntityBlockResult) {
/*1337*/      EntityBlockResult ebr = (EntityBlockResult)retValue;
/*1338*/      result = ebr.getEntityCollection();
/*   0*/    } 
/*1340*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height) {
/*1352*/    return createBufferedImage(width, height, null);
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height, ChartRenderingInfo info) {
/*1367*/    return createBufferedImage(width, height, 1, info);
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int width, int height, int imageType, ChartRenderingInfo info) {
/*1385*/    BufferedImage image = new BufferedImage(width, height, imageType);
/*1386*/    Graphics2D g2 = image.createGraphics();
/*1387*/    draw(g2, new Rectangle2D.Double(0.0D, 0.0D, width, height), null, info);
/*1388*/    g2.dispose();
/*1389*/    return image;
/*   0*/  }
/*   0*/  
/*   0*/  public BufferedImage createBufferedImage(int imageWidth, int imageHeight, double drawWidth, double drawHeight, ChartRenderingInfo info) {
/*1412*/    BufferedImage image = new BufferedImage(imageWidth, imageHeight, 1);
/*1414*/    Graphics2D g2 = image.createGraphics();
/*1415*/    double scaleX = imageWidth / drawWidth;
/*1416*/    double scaleY = imageHeight / drawHeight;
/*1417*/    AffineTransform st = AffineTransform.getScaleInstance(scaleX, scaleY);
/*1418*/    g2.transform(st);
/*1419*/    draw(g2, new Rectangle2D.Double(0.0D, 0.0D, drawWidth, drawHeight), null, info);
/*1421*/    g2.dispose();
/*1422*/    return image;
/*   0*/  }
/*   0*/  
/*   0*/  public void handleClick(int x, int y, ChartRenderingInfo info) {
/*1442*/    this.plot.handleClick(x, y, info.getPlotInfo());
/*   0*/  }
/*   0*/  
/*   0*/  public void addChangeListener(ChartChangeListener listener) {
/*1454*/    if (listener == null)
/*1455*/      throw new IllegalArgumentException("Null 'listener' argument."); 
/*1457*/    this.changeListeners.add(ChartChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeChangeListener(ChartChangeListener listener) {
/*1468*/    if (listener == null)
/*1469*/      throw new IllegalArgumentException("Null 'listener' argument."); 
/*1471*/    this.changeListeners.remove(ChartChangeListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void fireChartChanged() {
/*1480*/    ChartChangeEvent event = new ChartChangeEvent(this);
/*1481*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(ChartChangeEvent event) {
/*1491*/    if (this.notify) {
/*1492*/      Object[] listeners = this.changeListeners.getListenerList();
/*1493*/      for (int i = listeners.length - 2; i >= 0; i -= 2) {
/*1494*/        if (listeners[i] == ChartChangeListener.class)
/*1495*/          ((ChartChangeListener)listeners[i + 1]).chartChanged(event); 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void addProgressListener(ChartProgressListener listener) {
/*1511*/    this.progressListeners.add(ChartProgressListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  public void removeProgressListener(ChartProgressListener listener) {
/*1522*/    this.progressListeners.remove(ChartProgressListener.class, listener);
/*   0*/  }
/*   0*/  
/*   0*/  protected void notifyListeners(ChartProgressEvent event) {
/*1533*/    Object[] listeners = this.progressListeners.getListenerList();
/*1534*/    for (int i = listeners.length - 2; i >= 0; i -= 2) {
/*1535*/      if (listeners[i] == ChartProgressListener.class)
/*1536*/        ((ChartProgressListener)listeners[i + 1]).chartProgress(event); 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public void titleChanged(TitleChangeEvent event) {
/*1549*/    event.setChart(this);
/*1550*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  public void plotChanged(PlotChangeEvent event) {
/*1560*/    event.setChart(this);
/*1561*/    notifyListeners(event);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/*1572*/    if (obj == this)
/*1573*/      return true; 
/*1575*/    if (!(obj instanceof JFreeChart))
/*1576*/      return false; 
/*1578*/    JFreeChart that = (JFreeChart)obj;
/*1579*/    if (!this.renderingHints.equals(that.renderingHints))
/*1580*/      return false; 
/*1582*/    if (this.borderVisible != that.borderVisible)
/*1583*/      return false; 
/*1585*/    if (!ObjectUtilities.equal(this.borderStroke, that.borderStroke))
/*1586*/      return false; 
/*1588*/    if (!PaintUtilities.equal(this.borderPaint, that.borderPaint))
/*1589*/      return false; 
/*1591*/    if (!this.padding.equals(that.padding))
/*1592*/      return false; 
/*1594*/    if (!ObjectUtilities.equal(this.title, that.title))
/*1595*/      return false; 
/*1597*/    if (!ObjectUtilities.equal(this.subtitles, that.subtitles))
/*1598*/      return false; 
/*1600*/    if (!ObjectUtilities.equal(this.plot, that.plot))
/*1601*/      return false; 
/*1603*/    if (!PaintUtilities.equal(this.backgroundPaint, that.backgroundPaint))
/*1606*/      return false; 
/*1608*/    if (!ObjectUtilities.equal(this.backgroundImage, that.backgroundImage))
/*1610*/      return false; 
/*1612*/    if (this.backgroundImageAlignment != that.backgroundImageAlignment)
/*1613*/      return false; 
/*1615*/    if (this.backgroundImageAlpha != that.backgroundImageAlpha)
/*1616*/      return false; 
/*1618*/    if (this.notify != that.notify)
/*1619*/      return false; 
/*1621*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/*1632*/    stream.defaultWriteObject();
/*1633*/    SerialUtilities.writeStroke(this.borderStroke, stream);
/*1634*/    SerialUtilities.writePaint(this.borderPaint, stream);
/*1635*/    SerialUtilities.writePaint(this.backgroundPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/*1648*/    stream.defaultReadObject();
/*1649*/    this.borderStroke = SerialUtilities.readStroke(stream);
/*1650*/    this.borderPaint = SerialUtilities.readPaint(stream);
/*1651*/    this.backgroundPaint = SerialUtilities.readPaint(stream);
/*1652*/    this.progressListeners = new EventListenerList();
/*1653*/    this.changeListeners = new EventListenerList();
/*1654*/    this.renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*1659*/    if (this.title != null)
/*1660*/      this.title.addChangeListener(this); 
/*1663*/    for (int i = 0; i < getSubtitleCount(); i++)
/*1664*/      getSubtitle(i).addChangeListener(this); 
/*1666*/    this.plot.addChangeListener(this);
/*   0*/  }
/*   0*/  
/*   0*/  public static void main(String[] args) {
/*1675*/    System.out.println(INFO.toString());
/*   0*/  }
/*   0*/  
/*   0*/  public Object clone() throws CloneNotSupportedException {
/*1687*/    JFreeChart chart = (JFreeChart)super.clone();
/*1689*/    chart.renderingHints = (RenderingHints)this.renderingHints.clone();
/*1694*/    if (this.title != null) {
/*1695*/      chart.title = (TextTitle)this.title.clone();
/*1696*/      chart.title.addChangeListener(chart);
/*   0*/    } 
/*1699*/    chart.subtitles = new ArrayList();
/*1700*/    for (int i = 0; i < getSubtitleCount(); i++) {
/*1701*/      Title subtitle = (Title)getSubtitle(i).clone();
/*1702*/      chart.subtitles.add(subtitle);
/*1703*/      subtitle.addChangeListener(chart);
/*   0*/    } 
/*1706*/    if (this.plot != null) {
/*1707*/      chart.plot = (Plot)this.plot.clone();
/*1708*/      chart.plot.addChangeListener(chart);
/*   0*/    } 
/*1711*/    chart.progressListeners = new EventListenerList();
/*1712*/    chart.changeListeners = new EventListenerList();
/*1713*/    return chart;
/*   0*/  }
/*   0*/}
