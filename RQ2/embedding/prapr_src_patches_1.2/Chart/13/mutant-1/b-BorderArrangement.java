/*   0*/package org.jfree.chart.block;
/*   0*/
/*   0*/import java.awt.Graphics2D;
/*   0*/import java.awt.geom.Rectangle2D;
/*   0*/import java.io.Serializable;
/*   0*/import org.jfree.chart.util.ObjectUtilities;
/*   0*/import org.jfree.chart.util.RectangleEdge;
/*   0*/import org.jfree.chart.util.Size2D;
/*   0*/import org.jfree.data.Range;
/*   0*/
/*   0*/public class BorderArrangement implements Arrangement, Serializable {
/*   0*/  private static final long serialVersionUID = 506071142274883745L;
/*   0*/  
/*   0*/  private Block centerBlock;
/*   0*/  
/*   0*/  private Block topBlock;
/*   0*/  
/*   0*/  private Block bottomBlock;
/*   0*/  
/*   0*/  private Block leftBlock;
/*   0*/  
/*   0*/  private Block rightBlock;
/*   0*/  
/*   0*/  public void add(Block block, Object key) {
/*  98*/    if (key == null) {
/*  99*/      this.centerBlock = block;
/*   0*/    } else {
/* 102*/      RectangleEdge edge = (RectangleEdge)key;
/* 103*/      if (edge == RectangleEdge.TOP) {
/* 104*/        this.topBlock = block;
/* 106*/      } else if (edge == RectangleEdge.BOTTOM) {
/* 107*/        this.bottomBlock = block;
/* 109*/      } else if (edge == RectangleEdge.LEFT) {
/* 110*/        this.leftBlock = block;
/* 112*/      } else if (edge == RectangleEdge.RIGHT) {
/* 113*/        this.rightBlock = block;
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public Size2D arrange(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
/* 131*/    RectangleConstraint contentConstraint = container.toContentConstraint(constraint);
/* 133*/    Size2D contentSize = null;
/* 134*/    LengthConstraintType w = contentConstraint.getWidthConstraintType();
/* 135*/    LengthConstraintType h = contentConstraint.getHeightConstraintType();
/* 136*/    if (w == LengthConstraintType.NONE) {
/* 137*/      if (h == LengthConstraintType.NONE) {
/* 138*/        contentSize = arrangeNN(container, g2);
/*   0*/      } else {
/* 140*/        if (h == LengthConstraintType.FIXED)
/* 141*/          throw new RuntimeException("Not implemented."); 
/* 143*/        if (h == LengthConstraintType.RANGE)
/* 144*/          throw new RuntimeException("Not implemented."); 
/*   0*/      } 
/* 147*/    } else if (w == LengthConstraintType.FIXED) {
/* 148*/      if (h == LengthConstraintType.NONE) {
/* 149*/        contentSize = arrangeFN(container, g2, constraint.getWidth());
/* 151*/      } else if (h == LengthConstraintType.FIXED) {
/* 152*/        contentSize = arrangeFF(container, g2, constraint);
/* 154*/      } else if (h == LengthConstraintType.RANGE) {
/* 155*/        contentSize = arrangeFR(container, g2, constraint);
/*   0*/      } 
/* 158*/    } else if (w == LengthConstraintType.RANGE) {
/* 159*/      if (h == LengthConstraintType.NONE)
/* 160*/        throw new RuntimeException("Not implemented."); 
/* 162*/      if (h == LengthConstraintType.FIXED)
/* 163*/        throw new RuntimeException("Not implemented."); 
/* 165*/      if (h == LengthConstraintType.RANGE)
/* 166*/        contentSize = arrangeRR(container, constraint.getWidthRange(), constraint.getHeightRange(), g2); 
/*   0*/    } 
/* 170*/    return new Size2D(container.calculateTotalWidth(contentSize.getWidth()), container.calculateTotalHeight(contentSize.getHeight()));
/*   0*/  }
/*   0*/  
/*   0*/  protected Size2D arrangeNN(BlockContainer container, Graphics2D g2) {
/* 183*/    double[] w = new double[5];
/* 184*/    double[] h = new double[5];
/* 185*/    if (this.topBlock != null) {
/* 186*/      Size2D size = this.topBlock.arrange(g2, RectangleConstraint.NONE);
/* 187*/      w[0] = size.width;
/* 188*/      h[0] = size.height;
/*   0*/    } 
/* 190*/    if (this.bottomBlock != null) {
/* 191*/      Size2D size = this.bottomBlock.arrange(g2, RectangleConstraint.NONE);
/* 193*/      w[1] = size.width;
/* 194*/      h[1] = size.height;
/*   0*/    } 
/* 196*/    if (this.leftBlock != null) {
/* 197*/      Size2D size = this.leftBlock.arrange(g2, RectangleConstraint.NONE);
/* 198*/      w[2] = size.width;
/* 199*/      h[2] = size.height;
/*   0*/    } 
/* 201*/    if (this.rightBlock != null) {
/* 202*/      Size2D size = this.rightBlock.arrange(g2, RectangleConstraint.NONE);
/* 203*/      w[3] = size.width;
/* 204*/      h[3] = size.height;
/*   0*/    } 
/* 207*/    h[2] = Math.max(h[2], h[3]);
/* 208*/    h[3] = h[2];
/* 210*/    if (this.centerBlock != null) {
/* 211*/      Size2D size = this.centerBlock.arrange(g2, RectangleConstraint.NONE);
/* 213*/      w[4] = size.width;
/* 214*/      h[4] = size.height;
/*   0*/    } 
/* 216*/    double width = Math.max(w[0], Math.max(w[1], w[2] + w[4] + w[3]));
/* 217*/    double centerHeight = Math.max(h[2], Math.max(h[3], h[4]));
/* 218*/    double height = h[0] + h[1] + centerHeight;
/* 219*/    if (this.topBlock != null)
/* 220*/      this.topBlock.setBounds(new Rectangle2D.Double(0.0D, 0.0D, width, h[0])); 
/* 223*/    if (this.bottomBlock != null)
/* 224*/      this.bottomBlock.setBounds(new Rectangle2D.Double(0.0D, height - h[1], width, h[1])); 
/* 227*/    if (this.leftBlock != null)
/* 228*/      this.leftBlock.setBounds(new Rectangle2D.Double(0.0D, h[0], w[2], centerHeight)); 
/* 231*/    if (this.rightBlock != null)
/* 232*/      this.rightBlock.setBounds(new Rectangle2D.Double(width - w[3], h[0], w[3], centerHeight)); 
/* 236*/    if (this.centerBlock != null)
/* 237*/      this.centerBlock.setBounds(new Rectangle2D.Double(w[2], h[0], width - w[2] - w[3], centerHeight)); 
/* 240*/    return new Size2D(width, height);
/*   0*/  }
/*   0*/  
/*   0*/  protected Size2D arrangeFR(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
/* 254*/    Size2D size1 = arrangeFN(container, g2, constraint.getWidth());
/* 255*/    if (constraint.getHeightRange().contains(size1.getHeight()))
/* 256*/      return size1; 
/* 259*/    double h = constraint.getHeightRange().constrain(size1.getHeight());
/* 260*/    RectangleConstraint c2 = constraint.toFixedHeight(h);
/* 261*/    return arrange(container, g2, c2);
/*   0*/  }
/*   0*/  
/*   0*/  protected Size2D arrangeFN(BlockContainer container, Graphics2D g2, double width) {
/* 277*/    double[] w = new double[5];
/* 278*/    double[] h = new double[5];
/* 279*/    RectangleConstraint c1 = new RectangleConstraint(width, null, LengthConstraintType.FIXED, 0.0D, null, LengthConstraintType.NONE);
/* 282*/    if (this.topBlock != null) {
/* 283*/      Size2D size = this.topBlock.arrange(g2, c1);
/* 284*/      w[0] = size.width;
/* 285*/      h[0] = size.height;
/*   0*/    } 
/* 287*/    if (this.bottomBlock != null) {
/* 288*/      Size2D size = this.bottomBlock.arrange(g2, c1);
/* 289*/      w[1] = size.width;
/* 290*/      h[1] = size.height;
/*   0*/    } 
/* 292*/    RectangleConstraint c2 = new RectangleConstraint(0.0D, new Range(0.0D, width), LengthConstraintType.RANGE, 0.0D, null, LengthConstraintType.NONE);
/* 295*/    if (this.leftBlock != null) {
/* 296*/      Size2D size = this.leftBlock.arrange(g2, c2);
/* 297*/      w[2] = size.width;
/* 298*/      h[2] = size.height;
/*   0*/    } 
/* 300*/    if (this.rightBlock != null) {
/* 301*/      double maxW = Math.max(width - w[2], 0.0D);
/* 302*/      RectangleConstraint c3 = new RectangleConstraint(0.0D, new Range(Math.min(w[2], maxW), maxW), LengthConstraintType.RANGE, 0.0D, null, LengthConstraintType.NONE);
/* 306*/      Size2D size = this.rightBlock.arrange(g2, c3);
/* 307*/      w[3] = size.width;
/* 308*/      h[3] = size.height;
/*   0*/    } 
/* 311*/    h[2] = Math.max(h[2], h[3]);
/* 312*/    h[3] = h[2];
/* 314*/    if (this.centerBlock != null) {
/* 315*/      RectangleConstraint c4 = new RectangleConstraint(width - w[2] - w[3], null, LengthConstraintType.FIXED, 0.0D, null, LengthConstraintType.NONE);
/* 318*/      Size2D size = this.centerBlock.arrange(g2, c4);
/* 319*/      w[4] = size.width;
/* 320*/      h[4] = size.height;
/*   0*/    } 
/* 322*/    double height = h[0] + h[1] + Math.max(h[2], Math.max(h[3], h[4]));
/* 323*/    return arrange(container, g2, new RectangleConstraint(width, height));
/*   0*/  }
/*   0*/  
/*   0*/  protected Size2D arrangeRR(BlockContainer container, Range widthRange, Range heightRange, Graphics2D g2) {
/* 340*/    double[] w = new double[5];
/* 341*/    double[] h = new double[5];
/* 342*/    if (this.topBlock != null) {
/* 343*/      RectangleConstraint c1 = new RectangleConstraint(widthRange, heightRange);
/* 345*/      Size2D size = this.topBlock.arrange(g2, c1);
/* 346*/      w[0] = size.width;
/* 347*/      h[0] = size.height;
/*   0*/    } 
/* 349*/    if (this.bottomBlock != null) {
/* 350*/      Range heightRange2 = Range.shift(heightRange, -h[0], false);
/* 351*/      RectangleConstraint c2 = new RectangleConstraint(widthRange, heightRange2);
/* 353*/      Size2D size = this.bottomBlock.arrange(g2, c2);
/* 354*/      w[1] = size.width;
/* 355*/      h[1] = size.height;
/*   0*/    } 
/* 357*/    Range heightRange3 = Range.shift(heightRange, -(h[0] + h[1]));
/* 358*/    if (this.leftBlock != null) {
/* 359*/      RectangleConstraint c3 = new RectangleConstraint(widthRange, heightRange3);
/* 361*/      Size2D size = this.leftBlock.arrange(g2, c3);
/* 362*/      w[2] = size.width;
/* 363*/      h[2] = size.height;
/*   0*/    } 
/* 365*/    Range widthRange2 = Range.shift(widthRange, -w[2], false);
/* 366*/    if (this.rightBlock != null) {
/* 367*/      RectangleConstraint c4 = new RectangleConstraint(widthRange2, heightRange3);
/* 369*/      Size2D size = this.rightBlock.arrange(g2, c4);
/* 370*/      w[3] = size.width;
/* 371*/      h[3] = size.height;
/*   0*/    } 
/* 374*/    h[2] = Math.max(h[2], h[3]);
/* 375*/    h[3] = h[2];
/* 376*/    Range widthRange3 = Range.shift(widthRange, -(w[2] + w[3]), false);
/* 377*/    if (this.centerBlock != null) {
/* 378*/      RectangleConstraint c5 = new RectangleConstraint(widthRange3, heightRange3);
/* 383*/      Size2D size = this.centerBlock.arrange(g2, c5);
/* 384*/      w[4] = size.width;
/* 385*/      h[4] = size.height;
/*   0*/    } 
/* 387*/    double width = Math.max(w[0], Math.max(w[1], w[2] + w[4] + w[3]));
/* 388*/    double height = h[0] + h[1] + Math.max(h[2], Math.max(h[3], h[4]));
/* 389*/    if (this.topBlock != null)
/* 390*/      this.topBlock.setBounds(new Rectangle2D.Double(0.0D, 0.0D, width, h[0])); 
/* 393*/    if (this.bottomBlock != null)
/* 394*/      this.bottomBlock.setBounds(new Rectangle2D.Double(0.0D, height - h[1], width, h[1])); 
/* 397*/    if (this.leftBlock != null)
/* 398*/      this.leftBlock.setBounds(new Rectangle2D.Double(0.0D, h[0], w[2], h[2])); 
/* 401*/    if (this.rightBlock != null)
/* 402*/      this.rightBlock.setBounds(new Rectangle2D.Double(width - w[3], h[0], w[3], h[3])); 
/* 406*/    if (this.centerBlock != null)
/* 407*/      this.centerBlock.setBounds(new Rectangle2D.Double(w[2], h[0], width - w[2] - w[3], height - h[0] - h[1])); 
/* 410*/    return new Size2D(width, height);
/*   0*/  }
/*   0*/  
/*   0*/  protected Size2D arrangeFF(BlockContainer container, Graphics2D g2, RectangleConstraint constraint) {
/* 424*/    double[] w = new double[5];
/* 425*/    double[] h = new double[5];
/* 426*/    w[0] = constraint.getWidth();
/* 427*/    if (this.topBlock != null) {
/* 428*/      RectangleConstraint c1 = new RectangleConstraint(w[0], null, LengthConstraintType.FIXED, 0.0D, new Range(0.0D, constraint.getHeight()), LengthConstraintType.RANGE);
/* 432*/      Size2D size = this.topBlock.arrange(g2, c1);
/* 433*/      h[0] = size.height;
/*   0*/    } 
/* 435*/    w[1] = w[0];
/* 436*/    if (this.bottomBlock != null) {
/* 437*/      RectangleConstraint c2 = new RectangleConstraint(w[0], null, LengthConstraintType.FIXED, 0.0D, new Range(0.0D, constraint.getHeight() - h[0]), LengthConstraintType.RANGE);
/* 440*/      Size2D size = this.bottomBlock.arrange(g2, c2);
/* 441*/      h[1] = size.height;
/*   0*/    } 
/* 443*/    h[2] = constraint.getHeight() - h[1] - h[0];
/* 444*/    if (this.leftBlock != null) {
/* 445*/      RectangleConstraint c3 = new RectangleConstraint(0.0D, new Range(0.0D, constraint.getWidth()), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED);
/* 449*/      Size2D size = this.leftBlock.arrange(g2, c3);
/* 450*/      w[2] = size.width;
/*   0*/    } 
/* 452*/    h[3] = h[2];
/* 453*/    if (this.rightBlock != null) {
/* 454*/      RectangleConstraint c4 = new RectangleConstraint(0.0D, new Range(0.0D, constraint.getWidth() - w[3]), LengthConstraintType.RANGE, h[2], null, LengthConstraintType.FIXED);
/* 458*/      Size2D size = this.rightBlock.arrange(g2, c4);
/* 459*/      w[3] = size.width;
/*   0*/    } 
/* 461*/    h[4] = h[2];
/* 462*/    w[4] = constraint.getWidth() - w[3] - w[2];
/* 463*/    RectangleConstraint c5 = new RectangleConstraint(w[4], h[4]);
/* 464*/    if (this.centerBlock != null)
/* 465*/      this.centerBlock.arrange(g2, c5); 
/* 468*/    if (this.topBlock != null)
/* 469*/      this.topBlock.setBounds(new Rectangle2D.Double(0.0D, 0.0D, w[0], h[0])); 
/* 472*/    if (this.bottomBlock != null)
/* 473*/      this.bottomBlock.setBounds(new Rectangle2D.Double(0.0D, h[0] + h[2], w[1], h[1])); 
/* 476*/    if (this.leftBlock != null)
/* 477*/      this.leftBlock.setBounds(new Rectangle2D.Double(0.0D, h[0], w[2], h[2])); 
/* 480*/    if (this.rightBlock != null)
/* 481*/      this.rightBlock.setBounds(new Rectangle2D.Double(w[2] + w[4], h[0], w[3], h[3])); 
/* 484*/    if (this.centerBlock != null)
/* 485*/      this.centerBlock.setBounds(new Rectangle2D.Double(w[2], h[0], w[4], h[4])); 
/* 488*/    return new Size2D(constraint.getWidth(), constraint.getHeight());
/*   0*/  }
/*   0*/  
/*   0*/  public void clear() {
/* 495*/    this.centerBlock = null;
/* 496*/    this.topBlock = null;
/* 497*/    this.bottomBlock = null;
/* 498*/    this.leftBlock = null;
/* 499*/    this.rightBlock = null;
/*   0*/  }
/*   0*/  
/*   0*/  public boolean equals(Object obj) {
/* 510*/    if (obj == this)
/* 511*/      return true; 
/* 513*/    if (!(obj instanceof BorderArrangement))
/* 514*/      return false; 
/* 516*/    BorderArrangement that = (BorderArrangement)obj;
/* 517*/    if (!ObjectUtilities.equal(this.topBlock, that.topBlock))
/* 518*/      return false; 
/* 520*/    if (!ObjectUtilities.equal(this.bottomBlock, that.bottomBlock))
/* 521*/      return false; 
/* 523*/    if (!ObjectUtilities.equal(this.leftBlock, that.leftBlock))
/* 524*/      return false; 
/* 526*/    if (!ObjectUtilities.equal(this.rightBlock, that.rightBlock))
/* 527*/      return false; 
/* 529*/    if (!ObjectUtilities.equal(this.centerBlock, that.centerBlock))
/* 530*/      return false; 
/* 532*/    return true;
/*   0*/  }
/*   0*/}
