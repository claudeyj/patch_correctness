/*   0*/package org.jfree.chart.renderer.category;
/*   0*/
/*   0*/import java.awt.BasicStroke;
/*   0*/import java.awt.Color;
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.Paint;
/*   0*/import java.awt.Stroke;
/*   0*/import java.awt.geom.Line2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.IOException;
/*   0*/import java.io.ObjectInputStream;
/*   0*/import java.io.ObjectOutputStream;
/*   0*/import java.io.Serializable;
/*   0*/import org.jfree.chart.axis.CategoryAxis;
/*   0*/import org.jfree.chart.axis.ValueAxis;
/*   0*/import org.jfree.chart.entity.EntityCollection;
/*   0*/import org.jfree.chart.event.RendererChangeEvent;
/*   0*/import org.jfree.chart.labels.CategoryItemLabelGenerator;
/*   0*/import org.jfree.chart.plot.CategoryPlot;
/*   0*/import org.jfree.chart.plot.PlotOrientation;
/*   0*/import org.jfree.chart.util.PaintUtilities;
/*   0*/import org.jfree.chart.util.PublicCloneable;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.SerialUtilities;
/*   0*/import org.jfree.data.category.CategoryDataset;
/*   0*/import org.jfree.data.statistics.StatisticalCategoryDataset;
/*   0*/
/*   0*/public class StatisticalBarRenderer extends BarRenderer implements CategoryItemRenderer, Cloneable, PublicCloneable, Serializable {
/*   0*/  private static final long serialVersionUID = -4986038395414039117L;
/*   0*/  
/* 116*/  private transient Paint errorIndicatorPaint = Color.gray;
/*   0*/  
/* 117*/  private transient Stroke errorIndicatorStroke = new BasicStroke(0.5F);
/*   0*/  
/*   0*/  public Paint getErrorIndicatorPaint() {
/* 129*/    return this.errorIndicatorPaint;
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorIndicatorPaint(Paint paint) {
/* 141*/    this.errorIndicatorPaint = paint;
/* 142*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public Stroke getErrorIndicatorStroke() {
/* 155*/    return this.errorIndicatorStroke;
/*   0*/  }
/*   0*/  
/*   0*/  public void setErrorIndicatorStroke(Stroke stroke) {
/* 169*/    this.errorIndicatorStroke = stroke;
/* 170*/    notifyListeners(new RendererChangeEvent(this));
/*   0*/  }
/*   0*/  
/*   0*/  public void drawItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, CategoryDataset data, int row, int column, int pass) {
/* 200*/    if (!(data instanceof StatisticalCategoryDataset))
/* 201*/      throw new IllegalArgumentException("Requires StatisticalCategoryDataset."); 
/* 204*/    StatisticalCategoryDataset statData = (StatisticalCategoryDataset)data;
/* 206*/    PlotOrientation orientation = plot.getOrientation();
/* 207*/    if (orientation == PlotOrientation.HORIZONTAL) {
/* 208*/      drawHorizontalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, row, column);
/* 211*/    } else if (orientation == PlotOrientation.VERTICAL) {
/* 212*/      drawVerticalItem(g2, state, dataArea, plot, domainAxis, rangeAxis, statData, row, column);
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawHorizontalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, StatisticalCategoryDataset dataset, int row, int column) {
/* 240*/    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
/* 243*/    double rectY = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, xAxisLocation);
/* 246*/    int seriesCount = getRowCount();
/* 247*/    int categoryCount = getColumnCount();
/* 248*/    if (seriesCount > 1) {
/* 249*/      double seriesGap = dataArea.getHeight() * getItemMargin() / (categoryCount * (seriesCount - 1));
/* 251*/      rectY += row * (state.getBarWidth() + seriesGap);
/*   0*/    } else {
/* 254*/      rectY += row * state.getBarWidth();
/*   0*/    } 
/* 258*/    Number meanValue = dataset.getMeanValue(row, column);
/* 260*/    double value = meanValue.doubleValue();
/* 261*/    double base = 0.0D;
/* 262*/    double lclip = getLowerClip();
/* 263*/    double uclip = getUpperClip();
/* 265*/    if (uclip <= 0.0D) {
/* 266*/      if (value >= uclip)
/*   0*/        return; 
/* 269*/      base = uclip;
/* 270*/      if (value <= lclip)
/* 271*/        value = lclip; 
/* 274*/    } else if (lclip <= 0.0D) {
/* 275*/      if (value >= uclip) {
/* 276*/        value = uclip;
/* 279*/      } else if (value <= lclip) {
/* 280*/        value = lclip;
/*   0*/      } 
/*   0*/    } else {
/* 285*/      if (value <= lclip)
/*   0*/        return; 
/* 288*/      base = getLowerClip();
/* 289*/      if (value >= uclip)
/* 290*/        value = uclip; 
/*   0*/    } 
/* 294*/    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
/* 295*/    double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
/* 296*/    double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
/* 298*/    double rectX = Math.min(transY2, transY1);
/* 300*/    double rectHeight = state.getBarWidth();
/* 301*/    double rectWidth = Math.abs(transY2 - transY1);
/* 303*/    Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
/* 305*/    Paint seriesPaint = getItemPaint(row, column);
/* 306*/    g2.setPaint(seriesPaint);
/* 307*/    g2.fill(bar);
/* 308*/    if (isDrawBarOutline() && state.getBarWidth() > 3.0D) {
/* 309*/      g2.setStroke(getItemStroke(row, column));
/* 310*/      g2.setPaint(getItemOutlinePaint(row, column));
/* 311*/      g2.draw(bar);
/*   0*/    } 
/* 315*/    double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
/* 316*/    double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
/* 318*/    double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
/* 321*/    if (this.errorIndicatorStroke != null) {
/* 322*/      g2.setStroke(this.errorIndicatorStroke);
/*   0*/    } else {
/* 325*/      g2.setStroke(getItemOutlineStroke(row, column));
/*   0*/    } 
/* 327*/    if (this.errorIndicatorPaint != null) {
/* 328*/      g2.setPaint(this.errorIndicatorPaint);
/*   0*/    } else {
/* 331*/      g2.setPaint(getItemOutlinePaint(row, column));
/*   0*/    } 
/* 334*/    Line2D line = null;
/* 335*/    line = new Line2D.Double(lowVal, rectY + rectHeight / 2.0D, highVal, rectY + rectHeight / 2.0D);
/* 337*/    g2.draw(line);
/* 338*/    line = new Line2D.Double(highVal, rectY + rectHeight * 0.25D, highVal, rectY + rectHeight * 0.75D);
/* 340*/    g2.draw(line);
/* 341*/    line = new Line2D.Double(lowVal, rectY + rectHeight * 0.25D, lowVal, rectY + rectHeight * 0.75D);
/* 343*/    g2.draw(line);
/* 345*/    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
/* 347*/    if (generator != null && isItemLabelVisible(row, column))
/* 348*/      drawItemLabel(g2, dataset, row, column, plot, generator, bar, (value < 0.0D)); 
/* 353*/    EntityCollection entities = state.getEntityCollection();
/* 354*/    if (entities != null)
/* 355*/      addItemEntity(entities, dataset, row, column, bar); 
/*   0*/  }
/*   0*/  
/*   0*/  protected void drawVerticalItem(Graphics2D g2, CategoryItemRendererState state, Rectangle2D dataArea, CategoryPlot plot, CategoryAxis domainAxis, ValueAxis rangeAxis, StatisticalCategoryDataset dataset, int row, int column) {
/* 383*/    RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
/* 386*/    double rectX = domainAxis.getCategoryStart(column, getColumnCount(), dataArea, xAxisLocation);
/* 390*/    int seriesCount = getRowCount();
/* 391*/    int categoryCount = getColumnCount();
/* 392*/    if (seriesCount > 1) {
/* 393*/      double seriesGap = dataArea.getWidth() * getItemMargin() / (categoryCount * (seriesCount - 1));
/* 395*/      rectX += row * (state.getBarWidth() + seriesGap);
/*   0*/    } else {
/* 398*/      rectX += row * state.getBarWidth();
/*   0*/    } 
/* 402*/    Number meanValue = dataset.getMeanValue(row, column);
/* 404*/    double value = meanValue.doubleValue();
/* 405*/    double base = 0.0D;
/* 406*/    double lclip = getLowerClip();
/* 407*/    double uclip = getUpperClip();
/* 409*/    if (uclip <= 0.0D) {
/* 410*/      if (value >= uclip)
/*   0*/        return; 
/* 413*/      base = uclip;
/* 414*/      if (value <= lclip)
/* 415*/        value = lclip; 
/* 418*/    } else if (lclip <= 0.0D) {
/* 419*/      if (value >= uclip) {
/* 420*/        value = uclip;
/* 423*/      } else if (value <= lclip) {
/* 424*/        value = lclip;
/*   0*/      } 
/*   0*/    } else {
/* 429*/      if (value <= lclip)
/*   0*/        return; 
/* 432*/      base = getLowerClip();
/* 433*/      if (value >= uclip)
/* 434*/        value = uclip; 
/*   0*/    } 
/* 438*/    RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
/* 439*/    double transY1 = rangeAxis.valueToJava2D(base, dataArea, yAxisLocation);
/* 440*/    double transY2 = rangeAxis.valueToJava2D(value, dataArea, yAxisLocation);
/* 442*/    double rectY = Math.min(transY2, transY1);
/* 444*/    double rectWidth = state.getBarWidth();
/* 445*/    double rectHeight = Math.abs(transY2 - transY1);
/* 447*/    Rectangle2D bar = new Rectangle2D.Double(rectX, rectY, rectWidth, rectHeight);
/* 449*/    Paint seriesPaint = getItemPaint(row, column);
/* 450*/    g2.setPaint(seriesPaint);
/* 451*/    g2.fill(bar);
/* 452*/    if (isDrawBarOutline() && state.getBarWidth() > 3.0D) {
/* 453*/      g2.setStroke(getItemStroke(row, column));
/* 454*/      g2.setPaint(getItemOutlinePaint(row, column));
/* 455*/      g2.draw(bar);
/*   0*/    } 
/* 459*/    double valueDelta = dataset.getStdDevValue(row, column).doubleValue();
/* 460*/    double highVal = rangeAxis.valueToJava2D(meanValue.doubleValue() + valueDelta, dataArea, yAxisLocation);
/* 462*/    double lowVal = rangeAxis.valueToJava2D(meanValue.doubleValue() - valueDelta, dataArea, yAxisLocation);
/* 465*/    if (this.errorIndicatorStroke != null) {
/* 466*/      g2.setStroke(this.errorIndicatorStroke);
/*   0*/    } else {
/* 469*/      g2.setStroke(getItemOutlineStroke(row, column));
/*   0*/    } 
/* 471*/    if (this.errorIndicatorPaint != null) {
/* 472*/      g2.setPaint(this.errorIndicatorPaint);
/*   0*/    } else {
/* 475*/      g2.setPaint(getItemOutlinePaint(row, column));
/*   0*/    } 
/* 477*/    Line2D line = null;
/* 478*/    line = new Line2D.Double(rectX + rectWidth / 2.0D, lowVal, rectX + rectWidth / 2.0D, highVal);
/* 480*/    g2.draw(line);
/* 481*/    line = new Line2D.Double(rectX + rectWidth / 2.0D - 5.0D, highVal, rectX + rectWidth / 2.0D + 5.0D, highVal);
/* 483*/    g2.draw(line);
/* 484*/    line = new Line2D.Double(rectX + rectWidth / 2.0D - 5.0D, lowVal, rectX + rectWidth / 2.0D + 5.0D, lowVal);
/* 486*/    g2.draw(line);
/* 488*/    CategoryItemLabelGenerator generator = getItemLabelGenerator(row, column);
/* 490*/    if (generator != null && isItemLabelVisible(row, column))
/* 491*/      drawItemLabel(g2, dataset, row, column, plot, generator, bar, (value < 0.0D)); 
/* 496*/    EntityCollection entities = state.getEntityCollection();
/* 497*/    if (entities != null)
/* 498*/      addItemEntity(entities, dataset, row, column, bar); 
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 510*/    if (obj == this)
/* 511*/      return true; 
/* 513*/    if (!(obj instanceof StatisticalBarRenderer))
/* 514*/      return false; 
/* 516*/    if (!super.equals(obj))
/* 517*/      return false; 
/* 519*/    StatisticalBarRenderer that = (StatisticalBarRenderer)obj;
/* 520*/    if (!PaintUtilities.equal(this.errorIndicatorPaint, that.errorIndicatorPaint))
/* 522*/      return false; 
/* 524*/    return true;
/*   0*/  }
/*   0*/  
/*   0*/  private void writeObject(ObjectOutputStream stream) throws IOException {
/* 535*/    stream.defaultWriteObject();
/* 536*/    SerialUtilities.writePaint(this.errorIndicatorPaint, stream);
/* 537*/    SerialUtilities.writeStroke(this.errorIndicatorStroke, stream);
/*   0*/  }
/*   0*/  
/*   0*/  private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 550*/    stream.defaultReadObject();
/* 551*/    this.errorIndicatorPaint = SerialUtilities.readPaint(stream);
/* 552*/    this.errorIndicatorStroke = SerialUtilities.readStroke(stream);
/*   0*/  }
/*   0*/}
