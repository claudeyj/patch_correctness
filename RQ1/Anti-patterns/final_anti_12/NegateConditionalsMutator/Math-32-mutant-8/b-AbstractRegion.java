/*   0*/package org.apache.commons.math3.geometry.partitioning;
/*   0*/
/*   0*/import java.util.ArrayList;
/*   0*/import java.util.Collection;
/*   0*/import java.util.Comparator;
/*   0*/import java.util.Iterator;
/*   0*/import java.util.TreeSet;
/*   0*/import org.apache.commons.math3.exception.MathInternalError;
/*   0*/import org.apache.commons.math3.geometry.Space;
/*   0*/import org.apache.commons.math3.geometry.Vector;
/*   0*/
/*   0*/public abstract class AbstractRegion<S extends Space, T extends Space> implements Region<S> {
/*   0*/  private BSPTree<S> tree;
/*   0*/  
/*   0*/  private double size;
/*   0*/  
/*   0*/  private Vector<S> barycenter;
/*   0*/  
/*   0*/  protected AbstractRegion() {
/*  51*/    this.tree = new BSPTree<S>(Boolean.TRUE);
/*   0*/  }
/*   0*/  
/*   0*/  protected AbstractRegion(BSPTree<S> tree) {
/*  67*/    this.tree = tree;
/*   0*/  }
/*   0*/  
/*   0*/  protected AbstractRegion(Collection<SubHyperplane<S>> boundary) {
/*  91*/    if (boundary.size() == 0) {
/*  94*/      this.tree = new BSPTree<S>(Boolean.TRUE);
/*   0*/    } else {
/* 101*/      TreeSet<SubHyperplane<S>> ordered = new TreeSet<SubHyperplane<S>>(new Comparator<SubHyperplane<S>>() {
/*   0*/            public int compare(SubHyperplane<S> o1, SubHyperplane<S> o2) {
/* 103*/              double size1 = o1.getSize();
/* 104*/              double size2 = o2.getSize();
/* 105*/              return (size2 >= size1) ? -1 : ((o1 == o2) ? 0 : 1);
/*   0*/            }
/*   0*/          });
/* 108*/      ordered.addAll(boundary);
/* 111*/      this.tree = new BSPTree<S>();
/* 112*/      insertCuts(this.tree, ordered);
/* 115*/      this.tree.visit(new BSPTreeVisitor<S>() {
/*   0*/            public BSPTreeVisitor.Order visitOrder(BSPTree<S> node) {
/* 119*/              return BSPTreeVisitor.Order.PLUS_SUB_MINUS;
/*   0*/            }
/*   0*/            
/*   0*/            public void visitInternalNode(BSPTree<S> node) {}
/*   0*/            
/*   0*/            public void visitLeafNode(BSPTree<S> node) {
/* 128*/              node.setAttribute((node == node.getParent().getPlus()) ? Boolean.FALSE : Boolean.TRUE);
/*   0*/            }
/*   0*/          });
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public AbstractRegion(Hyperplane<S>[] hyperplanes) {
/* 142*/    if (hyperplanes == null || hyperplanes.length == 0) {
/* 143*/      this.tree = new BSPTree<S>(Boolean.FALSE);
/*   0*/    } else {
/* 147*/      this.tree = hyperplanes[0].wholeSpace().getTree(false);
/* 150*/      BSPTree<S> node = this.tree;
/* 151*/      node.setAttribute(Boolean.TRUE);
/* 152*/      for (Hyperplane<S> hyperplane : hyperplanes) {
/* 153*/        if (node.insertCut(hyperplane)) {
/* 154*/          node.setAttribute(null);
/* 155*/          node.getPlus().setAttribute(Boolean.FALSE);
/* 156*/          node = node.getMinus();
/* 157*/          node.setAttribute(Boolean.TRUE);
/*   0*/        } 
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void insertCuts(BSPTree<S> node, Collection<SubHyperplane<S>> boundary) {
/* 176*/    Iterator<SubHyperplane<S>> iterator = boundary.iterator();
/* 179*/    Hyperplane<S> inserted = null;
/* 180*/    while (inserted == null && iterator.hasNext()) {
/* 181*/      inserted = ((SubHyperplane<S>)iterator.next()).getHyperplane();
/* 182*/      if (!node.insertCut(inserted.copySelf()))
/* 183*/        inserted = null; 
/*   0*/    } 
/* 187*/    if (!iterator.hasNext())
/*   0*/      return; 
/* 192*/    ArrayList<SubHyperplane<S>> plusList = new ArrayList<SubHyperplane<S>>();
/* 193*/    ArrayList<SubHyperplane<S>> minusList = new ArrayList<SubHyperplane<S>>();
/* 194*/    while (iterator.hasNext()) {
/*   0*/      SubHyperplane.SplitSubHyperplane<S> split;
/* 195*/      SubHyperplane<S> other = iterator.next();
/* 196*/      switch (other.side(inserted)) {
/*   0*/        case PLUS:
/* 198*/          plusList.add(other);
/*   0*/        case MINUS:
/* 201*/          minusList.add(other);
/*   0*/        case BOTH:
/* 204*/          split = other.split(inserted);
/* 205*/          plusList.add(split.getPlus());
/* 206*/          minusList.add(split.getMinus());
/*   0*/      } 
/*   0*/    } 
/* 214*/    insertCuts(node.getPlus(), plusList);
/* 215*/    insertCuts(node.getMinus(), minusList);
/*   0*/  }
/*   0*/  
/*   0*/  public AbstractRegion<S, T> copySelf() {
/* 221*/    return buildNew(this.tree.copySelf());
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty() {
/* 226*/    return isEmpty(this.tree);
/*   0*/  }
/*   0*/  
/*   0*/  public boolean isEmpty(BSPTree<S> node) {
/* 236*/    if (node.getCut() == null)
/* 238*/      return !((Boolean)node.getAttribute()); 
/* 242*/    return (isEmpty(node.getMinus()) && isEmpty(node.getPlus()));
/*   0*/  }
/*   0*/  
/*   0*/  public boolean contains(Region<S> region) {
/* 248*/    return new RegionFactory<S>().difference(region, this).isEmpty();
/*   0*/  }
/*   0*/  
/*   0*/  public Region.Location checkPoint(Vector<S> point) {
/* 253*/    return checkPoint(this.tree, point);
/*   0*/  }
/*   0*/  
/*   0*/  protected Region.Location checkPoint(BSPTree<S> node, Vector<S> point) {
/* 264*/    BSPTree<S> cell = node.getCell(point);
/* 265*/    if (cell.getCut() == null)
/* 267*/      return (Boolean)cell.getAttribute() ? Region.Location.INSIDE : Region.Location.OUTSIDE; 
/* 271*/    Region.Location minusCode = checkPoint(cell.getMinus(), point);
/* 272*/    Region.Location plusCode = checkPoint(cell.getPlus(), point);
/* 273*/    return (minusCode == plusCode) ? minusCode : Region.Location.BOUNDARY;
/*   0*/  }
/*   0*/  
/*   0*/  public BSPTree<S> getTree(boolean includeBoundaryAttributes) {
/* 279*/    if (includeBoundaryAttributes && this.tree.getCut() != null && this.tree.getAttribute() == null)
/* 281*/      recurseBuildBoundary(this.tree); 
/* 283*/    return this.tree;
/*   0*/  }
/*   0*/  
/*   0*/  private void recurseBuildBoundary(BSPTree<S> node) {
/* 290*/    if (node.getCut() != null) {
/* 292*/      SubHyperplane<S> plusOutside = null;
/* 293*/      SubHyperplane<S> plusInside = null;
/* 297*/      Characterization<S> plusChar = new Characterization<S>();
/* 298*/      characterize(node.getPlus(), node.getCut().copySelf(), plusChar);
/* 300*/      if (plusChar.hasOut()) {
/* 305*/        Characterization<S> minusChar = new Characterization<S>();
/* 306*/        characterize(node.getMinus(), plusChar.getOut(), minusChar);
/* 307*/        if (minusChar.hasIn())
/* 308*/          plusOutside = minusChar.getIn(); 
/*   0*/      } 
/* 312*/      if (plusChar.hasIn()) {
/* 317*/        Characterization<S> minusChar = new Characterization<S>();
/* 318*/        characterize(node.getMinus(), plusChar.getIn(), minusChar);
/* 319*/        if (minusChar.hasOut())
/* 320*/          plusInside = minusChar.getOut(); 
/*   0*/      } 
/* 324*/      node.setAttribute(new BoundaryAttribute<S>(plusOutside, plusInside));
/* 325*/      recurseBuildBoundary(node.getPlus());
/* 326*/      recurseBuildBoundary(node.getMinus());
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private void characterize(BSPTree<S> node, SubHyperplane<S> sub, Characterization<S> characterization) {
/* 346*/    if (node.getCut() == null) {
/* 348*/      boolean inside = (Boolean)node.getAttribute();
/* 349*/      characterization.add(sub, inside);
/*   0*/    } else {
/*   0*/      SubHyperplane.SplitSubHyperplane<S> split;
/* 351*/      Hyperplane<S> hyperplane = node.getCut().getHyperplane();
/* 352*/      switch (sub.side(hyperplane)) {
/*   0*/        case PLUS:
/* 354*/          characterize(node.getPlus(), sub, characterization);
/*   0*/          break;
/*   0*/        case MINUS:
/* 357*/          characterize(node.getMinus(), sub, characterization);
/*   0*/          break;
/*   0*/        case BOTH:
/* 360*/          split = sub.split(hyperplane);
/* 361*/          characterize(node.getPlus(), split.getPlus(), characterization);
/* 362*/          characterize(node.getMinus(), split.getMinus(), characterization);
/*   0*/          break;
/*   0*/        default:
/* 366*/          throw new MathInternalError();
/*   0*/      } 
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  public double getBoundarySize() {
/* 373*/    BoundarySizeVisitor<S> visitor = new BoundarySizeVisitor<S>();
/* 374*/    getTree(true).visit(visitor);
/* 375*/    return visitor.getSize();
/*   0*/  }
/*   0*/  
/*   0*/  public double getSize() {
/* 380*/    if (this.barycenter == null)
/* 381*/      computeGeometricalProperties(); 
/* 383*/    return this.size;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setSize(double size) {
/* 390*/    this.size = size;
/*   0*/  }
/*   0*/  
/*   0*/  public Vector<S> getBarycenter() {
/* 395*/    if (this.barycenter == null)
/* 396*/      computeGeometricalProperties(); 
/* 398*/    return this.barycenter;
/*   0*/  }
/*   0*/  
/*   0*/  protected void setBarycenter(Vector<S> barycenter) {
/* 405*/    this.barycenter = barycenter;
/*   0*/  }
/*   0*/  
/*   0*/  public Side side(Hyperplane<S> hyperplane) {
/* 415*/    Sides sides = new Sides();
/* 416*/    recurseSides(this.tree, hyperplane.wholeHyperplane(), sides);
/* 417*/    return sides.plusFound() ? (sides.minusFound() ? Side.BOTH : Side.PLUS) : (sides.minusFound() ? Side.MINUS : Side.HYPER);
/*   0*/  }
/*   0*/  
/*   0*/  private void recurseSides(BSPTree<S> node, SubHyperplane<S> sub, Sides sides) {
/*   0*/    SubHyperplane.SplitSubHyperplane<S> split;
/* 439*/    if (node.getCut() == null) {
/* 440*/      if ((Boolean)node.getAttribute()) {
/* 442*/        sides.rememberPlusFound();
/* 443*/        sides.rememberMinusFound();
/*   0*/      } 
/*   0*/      return;
/*   0*/    } 
/* 448*/    Hyperplane<S> hyperplane = node.getCut().getHyperplane();
/* 449*/    switch (sub.side(hyperplane)) {
/*   0*/      case PLUS:
/* 452*/        if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
/* 453*/          if (!isEmpty(node.getMinus()))
/* 454*/            sides.rememberPlusFound(); 
/* 457*/        } else if (!isEmpty(node.getMinus())) {
/* 458*/          sides.rememberMinusFound();
/*   0*/        } 
/* 461*/        if (!sides.plusFound() || !sides.minusFound())
/* 462*/          recurseSides(node.getPlus(), sub, sides); 
/*   0*/        break;
/*   0*/      case MINUS:
/* 467*/        if (node.getCut().side(sub.getHyperplane()) == Side.PLUS) {
/* 468*/          if (!isEmpty(node.getPlus()))
/* 469*/            sides.rememberPlusFound(); 
/* 472*/        } else if (!isEmpty(node.getPlus())) {
/* 473*/          sides.rememberMinusFound();
/*   0*/        } 
/* 476*/        if (!sides.plusFound() || !sides.minusFound())
/* 477*/          recurseSides(node.getMinus(), sub, sides); 
/*   0*/        break;
/*   0*/      case BOTH:
/* 482*/        split = sub.split(hyperplane);
/* 485*/        recurseSides(node.getPlus(), split.getPlus(), sides);
/* 488*/        if (!sides.plusFound() || !sides.minusFound())
/* 489*/          recurseSides(node.getMinus(), split.getMinus(), sides); 
/*   0*/        break;
/*   0*/      default:
/* 494*/        if (node.getCut().getHyperplane().sameOrientationAs(sub.getHyperplane())) {
/* 495*/          if (node.getPlus().getCut() != null || (Boolean)node.getPlus().getAttribute())
/* 496*/            sides.rememberPlusFound(); 
/* 498*/          if (node.getMinus().getCut() != null || (Boolean)node.getMinus().getAttribute())
/* 499*/            sides.rememberMinusFound(); 
/*   0*/          break;
/*   0*/        } 
/* 502*/        if (node.getPlus().getCut() != null || (Boolean)node.getPlus().getAttribute())
/* 503*/          sides.rememberMinusFound(); 
/* 505*/        if (node.getMinus().getCut() != null || (Boolean)node.getMinus().getAttribute())
/* 506*/          sides.rememberPlusFound(); 
/*   0*/        break;
/*   0*/    } 
/*   0*/  }
/*   0*/  
/*   0*/  private static final class Sides {
/*   0*/    private boolean plusFound = false;
/*   0*/    
/*   0*/    private boolean minusFound = false;
/*   0*/    
/*   0*/    public void rememberPlusFound() {
/* 532*/      this.plusFound = true;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean plusFound() {
/* 539*/      return this.plusFound;
/*   0*/    }
/*   0*/    
/*   0*/    public void rememberMinusFound() {
/* 545*/      this.minusFound = true;
/*   0*/    }
/*   0*/    
/*   0*/    public boolean minusFound() {
/* 552*/      return this.minusFound;
/*   0*/    }
/*   0*/  }
/*   0*/  
/*   0*/  public SubHyperplane<S> intersection(SubHyperplane<S> sub) {
/* 559*/    return recurseIntersection(this.tree, sub);
/*   0*/  }
/*   0*/  
/*   0*/  private SubHyperplane<S> recurseIntersection(BSPTree<S> node, SubHyperplane<S> sub) {
/*   0*/    SubHyperplane.SplitSubHyperplane<S> split;
/*   0*/    SubHyperplane<S> plus, minus;
/* 570*/    if (node.getCut() == null)
/* 571*/      return (Boolean)node.getAttribute() ? sub.copySelf() : null; 
/* 574*/    Hyperplane<S> hyperplane = node.getCut().getHyperplane();
/* 575*/    switch (sub.side(hyperplane)) {
/*   0*/      case PLUS:
/* 577*/        return recurseIntersection(node.getPlus(), sub);
/*   0*/      case MINUS:
/* 579*/        return recurseIntersection(node.getMinus(), sub);
/*   0*/      case BOTH:
/* 581*/        split = sub.split(hyperplane);
/* 582*/        plus = recurseIntersection(node.getPlus(), split.getPlus());
/* 583*/        minus = recurseIntersection(node.getMinus(), split.getMinus());
/* 584*/        if (plus == null)
/* 585*/          return minus; 
/* 586*/        if (minus == null)
/* 587*/          return plus; 
/* 589*/        return plus.reunite(minus);
/*   0*/    } 
/* 592*/    return recurseIntersection(node.getPlus(), recurseIntersection(node.getMinus(), sub));
/*   0*/  }
/*   0*/  
/*   0*/  public AbstractRegion<S, T> applyTransform(Transform<S, T> transform) {
/* 609*/    return buildNew(recurseTransform(getTree(false), transform));
/*   0*/  }
/*   0*/  
/*   0*/  private BSPTree<S> recurseTransform(BSPTree<S> node, Transform<S, T> transform) {
/* 620*/    if (node.getCut() == null)
/* 621*/      return new BSPTree<S>(node.getAttribute()); 
/* 624*/    SubHyperplane<S> sub = node.getCut();
/* 625*/    SubHyperplane<S> tSub = ((AbstractSubHyperplane)sub).applyTransform(transform);
/* 626*/    BoundaryAttribute<S> attribute = (BoundaryAttribute<S>)node.getAttribute();
/* 627*/    if (attribute != null) {
/* 628*/      SubHyperplane<S> tPO = (attribute.getPlusOutside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusOutside()).applyTransform(transform);
/* 630*/      SubHyperplane<S> tPI = (attribute.getPlusInside() == null) ? null : ((AbstractSubHyperplane)attribute.getPlusInside()).applyTransform(transform);
/* 632*/      attribute = new BoundaryAttribute<S>(tPO, tPI);
/*   0*/    } 
/* 635*/    return new BSPTree<S>(tSub, recurseTransform(node.getPlus(), transform), recurseTransform(node.getMinus(), transform), attribute);
/*   0*/  }
/*   0*/  
/*   0*/  public abstract AbstractRegion<S, T> buildNew(BSPTree<S> paramBSPTree);
/*   0*/  
/*   0*/  protected abstract void computeGeometricalProperties();
/*   0*/}
