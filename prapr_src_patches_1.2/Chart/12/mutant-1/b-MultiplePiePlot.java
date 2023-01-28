/*   0*/package org.jfree.chart.plot;
/*   0*/
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Font;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Rectangle;
/*   0*/import java.awt.geom.Point2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import java.util.HashMap;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.List;
/*   0*/import java.util.Map;
/*   0*/import org.jfree.chart.ChartRenderingInfo;
/*   0*/import org.jfree.chart.JFreeChart;
/*   0*/import org.jfree.chart.LegendItem;
/*   0*/import org.jfree.chart.LegendItemCollection;
/*   0*/import org.jfree.chart.title.TextTitle;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.RectangleInsets;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/import org.jfree.chart.util.TableOrder;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.category.CategoryToPieDataset;
/*   0*/import org.jfree.data.general.DatasetChangeEvent;
/*   0*/import org.jfree.data.general.DatasetUtilities;
/*   0*/import org.jfree.data.general.PieDataset;
/*   0*/
/*   0*/public class MultiplePiePlot extends Plot implements Cloneable, Serializable {
/*   0*/  private static final long serialVersionUID = -355377800470807389L;
/*   0*/  
/*   0*/  private JFreeChart pieChart;
/*   0*/  
/*   0*/  private CategoryDataset dataset;
/*   0*/  
/*   0*/  private TableOrder dataExtractOrder;
/*   0*/  
/* 111*/  private double limit = 0.0D;
/*   0*/  
/*   0*/  private Comparable aggregatedItemsKey;
/*   0*/  
/*   0*/  private transient Paint aggregatedItemsPaint;
/*   0*/  
/*   0*/  private transient Map sectionPaints;
/*   0*/  
/*   0*/  public MultiplePiePlot() {
/* 135*/    this(null);
/*   0*/  }
/*   0*/  
/*   0*/  public MultiplePiePlot(CategoryDataset dataset) {
/* 145*/    setDataset(dataset);
/* 146*/    PiePlot piePlot = new PiePlot(null);
/* 147*/    this.pieChart = new JFreeChart(piePlot);
/* 148*/    this.pieChart.removeLegend();
/* 149*/    this.dataExtractOrder = TableOrder.BY_COLUMN;
/* 150*/    this.pieChart.setBackgroundPaint(null);
/* 151*/    TextTitle seriesTitle = new TextTitle("Series Title", new Font("SansSerif", 1, 12));
/* 153*/    seriesTitle.setPosition(RectangleEdge.BOTTOM);
/* 154*/    this.pieChart.setTitle(seriesTitle);
/* 155*/    this.aggregatedItemsKey = "Other";
/* 156*/    this.aggregatedItemsPaint = Color.lightGray;
/* 157*/    this.sectionPaints = new HashMap();
/*   0*/  }
/*   0*/  
/*   0*/  public CategoryDataset getDataset() {
/* 166*/    return this.dataset;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataset(CategoryDataset dataset) {
/* 178*/    if (this.dataset != null)
/* 179*/      this.dataset.removeChangeListener(this); 
/* 183*/    this.dataset = dataset;
/* 184*/    if (dataset != null) {
/* 185*/      setDatasetGroup(dataset.getGroup());
/* 186*/      dataset.addChangeListener(this);
/*   0*/    } 
/* 190*/    datasetChanged(new DatasetChangeEvent(this, dataset));
/*   0*/  }
/*   0*/  
/*   0*/  public JFreeChart getPieChart() {
/* 201*/    return this.pieChart;
/*   0*/  }
/*   0*/  
/*   0*/  public void setPieChart(JFreeChart pieChart) {
/* 213*/    if (pieChart == null)
/* 214*/      throw new IllegalArgumentException("Null 'pieChart' argument."); 
/* 216*/    if (!(pieChart.getPlot() instanceof PiePlot))
/* 217*/      throw new IllegalArgumentException("The 'pieChart' argument must be a chart based on a PiePlot."); 
/* 220*/    this.pieChart = pieChart;
/* 221*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public TableOrder getDataExtractOrder() {
/* 230*/    return this.dataExtractOrder;
/*   0*/  }
/*   0*/  
/*   0*/  public void setDataExtractOrder(TableOrder order) {
/* 240*/    if (order == null)
/* 241*/      throw new IllegalArgumentException("Null 'order' argument"); 
/* 243*/    this.dataExtractOrder = order;
/* 244*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public double getLimit() {
/* 254*/    return this.limit;
/*   0*/  }
/*   0*/  
/*   0*/  public void setLimit(double limit) {
/* 264*/    this.limit = limit;
/* 265*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Comparable getAggregatedItemsKey() {
/* 277*/    return this.aggregatedItemsKey;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAggregatedItemsKey(Comparable key) {
/* 289*/    if (key == null)
/* 290*/      throw new IllegalArgumentException("Null 'key' argument."); 
/* 292*/    this.aggregatedItemsKey = key;
/* 293*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public Paint getAggregatedItemsPaint() {
/* 305*/    return this.aggregatedItemsPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setAggregatedItemsPaint(Paint paint) {
/* 317*/    if (paint == null)
/* 318*/      throw new IllegalArgumentException("Null 'paint' argument."); 
/* 320*/    this.aggregatedItemsPaint = paint;
/* 321*/    fireChangeEvent();
/*   0*/  }
/*   0*/  
/*   0*/  public String getPlotType() {
/* 330*/    return "Multiple Pie Plot";
/*   0*/  }
/*   0*/  
/*   0*/  public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
/* 352*/    RectangleInsets insets = getInsets();
/* 353*/    insets.trim(area);
/* 354*/    drawBackground(g2, area);
/* 355*/    drawOutline(g2, area);
/* 358*/    if (DatasetUtilities.isEmptyOrNull(this.dataset)) {
/* 359*/      drawNoDataMessage(g2, area);
/*   0*/      return;
/*   0*/    } 
/* 363*/    int pieCount = 0;
/* 364*/    if (this.dataExtractOrder == TableOrder.BY_ROW) {
/* 365*/      pieCount = this.dataset.getRowCount();
/*   0*/    } else {
/* 368*/      pieCount = this.dataset.getColumnCount();
/*   0*/    } 
/* 372*/    int displayCols = (int)Math.ceil(Math.sqrt(pieCount));
/* 373*/    int displayRows = (int)Math.ceil(pieCount / displayCols);
/* 377*/    if (displayCols > displayRows && area.getWidth() < area.getHeight()) {
/* 378*/      int temp = displayCols;
/* 379*/      displayCols = displayRows;
/* 380*/      displayRows = temp;
/*   0*/    } 
/* 383*/    prefetchSectionPaints();
/* 385*/    int x = (int)area.getX();
/* 386*/    int y = (int)area.getY();
/* 387*/    int width = (int)area.getWidth() / displayCols;
/* 388*/    int height = (int)area.getHeight() / displayRows;
/* 389*/    int row = 0;
/* 390*/    int column = 0;
/* 391*/    int diff = displayRows * displayCols - pieCount;
/* 392*/    int xoffset = 0;
/* 393*/    Rectangle rect = new Rectangle();
/* 395*/    for (int pieIndex = 0; pieIndex < pieCount; pieIndex++) {
/* 396*/      rect.setBounds(x + xoffset + width * column, y + height * row, width, height);
/* 399*/      String title = null;
/* 400*/      if (this.dataExtractOrder == TableOrder.BY_ROW) {
/* 401*/        title = this.dataset.getRowKey(pieIndex).toString();
/*   0*/      } else {
/* 404*/        title = this.dataset.getColumnKey(pieIndex).toString();
/*   0*/      } 
/* 406*/      this.pieChart.setTitle(title);
/* 408*/      PieDataset piedataset = null;
/* 409*/      PieDataset dd = new CategoryToPieDataset(this.dataset, this.dataExtractOrder, pieIndex);
/* 411*/      if (this.limit > 0.0D) {
/* 412*/        piedataset = DatasetUtilities.createConsolidatedPieDataset(dd, this.aggregatedItemsKey, this.limit);
/*   0*/      } else {
/* 416*/        piedataset = dd;
/*   0*/      } 
/* 418*/      PiePlot piePlot = (PiePlot)this.pieChart.getPlot();
/* 419*/      piePlot.setDataset(piedataset);
/* 420*/      piePlot.setPieIndex(pieIndex);
/* 423*/      for (int i = 0; i < piedataset.getItemCount(); i++) {
/*   0*/        Paint p;
/* 424*/        Comparable key = piedataset.getKey(i);
/* 426*/        if (key.equals(this.aggregatedItemsKey)) {
/* 427*/          p = this.aggregatedItemsPaint;
/*   0*/        } else {
/* 430*/          p = (Paint)this.sectionPaints.get(key);
/*   0*/        } 
/* 432*/        piePlot.setSectionPaint(key, p);
/*   0*/      } 
/* 435*/      ChartRenderingInfo subinfo = null;
/* 436*/      if (info != null)
/* 437*/        subinfo = new ChartRenderingInfo(); 
/* 439*/      this.pieChart.draw(g2, rect, subinfo);
/* 440*/      if (info != null) {
/* 441*/        info.getOwner().getEntityCollection().addAll(subinfo.getEntityCollection());
/* 443*/        info.addSubplotInfo(subinfo.getPlotInfo());
/*   0*/      } 
/* 446*/      column++;
/* 447*/      if (column == displayCols) {
/* 448*/        column = 0;
/* 449*/        row++;
/* 451*/        if (row == displayRows - 1 && diff != 0)
/* 452*/          xoffset = diff * width / 2; 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void prefetchSectionPaints() {
/* 471*/    PiePlot piePlot = (PiePlot)getPieChart().getPlot();
/* 473*/    if (this.dataExtractOrder == TableOrder.BY_ROW) {
/* 475*/      for (int c = 0; c < this.dataset.getColumnCount(); c++) {
/* 476*/        Comparable key = this.dataset.getColumnKey(c);
/* 477*/        Paint p = piePlot.getSectionPaint(key);
/* 478*/        if (p == null) {
/* 479*/          p = (Paint)this.sectionPaints.get(key);
/* 480*/          if (p == null)
/* 481*/            p = getDrawingSupplier().getNextPaint(); 
/*   0*/        } 
/* 484*/        this.sectionPaints.put(key, p);
/*   0*/      } 
/*   0*/    } else {
/* 489*/      for (int r = 0; r < this.dataset.getRowCount(); r++) {
/* 490*/        Comparable key = this.dataset.getRowKey(r);
/* 491*/        Paint p = piePlot.getSectionPaint(key);
/* 492*/        if (p == null) {
/* 493*/          p = (Paint)this.sectionPaints.get(key);
/* 494*/          if (p == null)
/* 495*/            p = getDrawingSupplier().getNextPaint(); 
/*   0*/        } 
/* 498*/        this.sectionPaints.put(key, p);
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public LegendItemCollection getLegendItems() {
/* 511*/    LegendItemCollection result = new LegendItemCollection();
/* 513*/    if (this.dataset != null) {
/* 514*/      List keys = null;
/* 516*/      prefetchSectionPaints();
/* 517*/      if (this.dataExtractOrder == TableOrder.BY_ROW) {
/* 518*/        keys = this.dataset.getColumnKeys();
/* 520*/      } else if (this.dataExtractOrder == TableOrder.BY_COLUMN) {
/* 521*/        keys = this.dataset.getRowKeys();
/*   0*/      } 
/* 524*/      if (keys != null) {
/* 525*/        int section = 0;
/* 526*/        Iterator<Comparable> iterator = keys.iterator();
/* 527*/        while (iterator.hasNext()) {
/* 528*/          Comparable key = iterator.next();
/* 529*/          String label = key.toString();
/* 530*/          String description = label;
/* 531*/          Paint paint = (Paint)this.sectionPaints.get(key);
/* 532*/          LegendItem item = new LegendItem(label, description, null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, paint, Plot.DEFAULT_OUTLINE_STROKE, paint);
/* 535*/          item.setDataset(getDataset());
/* 536*/          result.add(item);
/* 537*/          section++;
/*   0*/        } 
/*   0*/      } 
/* 540*/      if (this.limit > 0.0D)
/* 541*/        result.add(new LegendItem(this.aggregatedItemsKey.toString(), this.aggregatedItemsKey.toString(), null, null, Plot.DEFAULT_LEGEND_ITEM_CIRCLE, this.aggregatedItemsPaint, Plot.DEFAULT_OUTLINE_STROKE, this.aggregatedItemsPaint)); 
/*   0*/    } 
/* 549*/    return result;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 562*/    if (obj == this)
/* 563*/      return true; 
/* 565*/    if (!(obj instanceof MultiplePiePlot))
/* 566*/      return false; 
/* 568*/    MultiplePiePlot that = (MultiplePiePlot)obj;
/* 569*/    if (this.dataExtractOrder != that.dataExtractOrder)
/* 570*/      return false; 
/* 572*/    if (this.limit != that.limit)
/* 573*/      return false; 
/* 575*/    if (!this.aggregatedItemsKey.equals(that.aggregatedItemsKey))
/* 576*/      return false; 
/* 578*/    if (!PaintUtilities.equal(this.aggregatedItemsPaint, that.aggregatedItemsPaint))
/* 580*/      return false; 
/* 582*/    if (!ObjectUtilities.equal(this.pieChart, that.pieChart))
/* 583*/      return false; 
/* 585*/    if (!super.equals(obj))
/* 586*/      return false; 
/* 588*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/* 599*/    stream.defaultWriteObject();
/* 600*/    SerialUtilities.writePaint(this.aggregatedItemsPaint, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 613*/    stream.defaultReadObject();
/* 614*/    this.aggregatedItemsPaint = SerialUtilities.readPaint(stream);
/* 615*/    this.sectionPaints = new HashMap();
/*   0*/  }
/*   0*/}
