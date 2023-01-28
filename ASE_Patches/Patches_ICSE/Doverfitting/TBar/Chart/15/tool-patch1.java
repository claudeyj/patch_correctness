/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.  
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]
 *
 * --------------
 * PiePlot3D.java
 * --------------
 * (C) Copyright 2000-2007, by Object Refinery and Contributors.
 *
 * Original Author:  Tomer Peretz;
 * Contributor(s):   Richard Atkinson;
 *                   David Gilbert (for Object Refinery Limited);
 *                   Xun Kang;
 *                   Christian W. Zuckschwerdt;
 *                   Arnaud Lelievre;
 *                   Dave Crane;
 *
 * Changes
 * -------
 * 21-Jun-2002 : Version 1;
 * 31-Jul-2002 : Modified to use startAngle and direction, drawing modified so 
 *               that charts render with foreground alpha < 1.0 (DG);
 * 05-Aug-2002 : Small modification to draw method to support URLs for HTML 
 *               image maps (RA);
 * 26-Sep-2002 : Fixed errors reported by Checkstyle (DG);
 * 18-Oct-2002 : Added drawing bug fix sent in by Xun Kang, and made a couple 
 *               of other related fixes (DG);
 * 30-Oct-2002 : Changed the PieDataset interface. Fixed another drawing 
 *               bug (DG);
 * 12-Nov-2002 : Fixed null pointer exception for zero or negative values (DG);
 * 07-Mar-2003 : Modified to pass pieIndex on to PieSectionEntity (DG);
 * 21-Mar-2003 : Added workaround for bug id 620031 (DG);
 * 26-Mar-2003 : Implemented Serializable (DG);
 * 30-Jul-2003 : Modified entity constructor (CZ);
 * 29-Aug-2003 : Small changes for API updates in PiePlot class (DG);
 * 02-Sep-2003 : Fixed bug where the 'no data' message is not displayed (DG);
 * 08-Sep-2003 : Added internationalization via use of properties 
 *               resourceBundle (RFE 690236) (AL); 
 * 29-Oct-2003 : Added workaround for font alignment in PDF output (DG);
 * 20-Nov-2003 : Fixed bug 845289 (sides not showing) (DG);
 * 25-Nov-2003 : Added patch (845095) to fix outline paint issues (DG);
 * 10-Mar-2004 : Numerous changes to enhance labelling (DG);
 * 31-Mar-2004 : Adjusted plot area when label generator is null (DG);
 * 08-Apr-2004 : Added flag to PiePlot class to control the treatment of null 
 *               values (DG);
 *               Added pieIndex to PieSectionEntity (DG);
 * 15-Nov-2004 : Removed creation of default tool tip generator (DG);
 * 16-Jun-2005 : Added default constructor (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 27-Sep-2006 : Updated draw() method for new lookup methods (DG);
 * 22-Mar-2007 : Added equals() override (DG);
 * 20-Jun-2007 : Removed JCommon dependencies (DG);
 * 18-Jun-2007 : Added handling for simple label option (DG);
 * 04-Oct-2007 : Added option to darken sides of plot - thanks to Alex Moots 
 *               (see patch 1805262) (DG);
 * 21-Nov-2007 : Changed default depth factor, fixed labelling bugs and added
 *               debug code - see debug flags in PiePlot class (DG);
 *
 */

package org.jfree.chart.plot;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.util.RectangleInsets;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;

/**
 * A plot that displays data in the form of a 3D pie chart, using data from
 * any class that implements the {@link PieDataset} interface.
 * <P>
 * Although this class extends {@link PiePlot}, it does not currently support
 * exploded sections.
 */
public class PiePlot3D extends PiePlot implements Serializable {

    /** For serialization. */
    private static final long serialVersionUID = 3408984188945161432L;
    
    /** The factor of the depth of the pie from the plot height */
    private double depthFactor = 0.12;

    /** 
     * A flag that controls whether or not the sides of the pie chart
     * are rendered using a darker colour.
     * 
     *  @since 1.0.7.
     */
    private boolean darkerSides = false;  // default preserves previous 
                                          // behaviour
    
    /**
     * Creates a new instance with no dataset.
     */
    public PiePlot3D() {
        this(null);   
    }
    
    /**
     * Creates a pie chart with a three dimensional effect using the specified 
     * dataset.
     *
     * @param dataset  the dataset (<code>null</code> permitted).
     */
    public PiePlot3D(PieDataset dataset) {
        super(dataset);
        setCircular(false, false);
    }

    /**
     * Returns the depth factor for the chart.
     *
     * @return The depth factor.
     * 
     * @see #setDepthFactor(double)
     */
    public double getDepthFactor() {
        return this.depthFactor;
    }

    /**
     * Sets the pie depth as a percentage of the height of the plot area, and
     * sends a {@link PlotChangeEvent} to all registered listeners.
     *
     * @param factor  the depth factor (for example, 0.20 is twenty percent).
     * 
     * @see #getDepthFactor()
     */
    public void setDepthFactor(double factor) {
        this.depthFactor = factor;
        notifyListeners(new PlotChangeEvent(this));
    }

    /**
     * Returns a flag that controls whether or not the sides of the pie chart
     * are rendered using a darker colour.  This is only applied if the
     * section colour is an instance of {@link java.awt.Color}.
     *
     * @return A boolean.
     * 
     * @see #setDarkerSides(boolean)
     * 
     * @since 1.0.7
     */
    public boolean getDarkerSides() {
        return this.darkerSides;
    }

    /**
     * Sets a flag that controls whether or not the sides of the pie chart
     * are rendered using a darker colour, and sends a {@link PlotChangeEvent} 
     * to all registered listeners.  This is only applied if the
     * section colour is an instance of {@link java.awt.Color}.
     *
     * @param darker true to darken the sides, false to use the default 
     *         behaviour.
     * 
     * @see #getDarkerSides()
     * 
     * @since 1.0.7.
     */
    public void setDarkerSides(boolean darker) {
        this.darkerSides = darker;
        notifyListeners(new PlotChangeEvent(this));
    }

    

    /**
     * Draws the side of a pie section.
     *
     * @param g2  the graphics device.
     * @param plotArea  the plot area.
     * @param arc  the arc.
     * @param front  the front of the pie.
     * @param back  the back of the pie.
     * @param paint  the color.
     * @param outlinePaint  the outline paint.
     * @param outlineStroke  the outline stroke.
     * @param drawFront  draw the front?
     * @param drawBack  draw the back?
     */
    protected void drawSide(Graphics2D g2,
                            Rectangle2D plotArea, 
                            Arc2D arc, 
                            Area front, 
                            Area back,
                            Paint paint, 
                            Paint outlinePaint,
                            Stroke outlineStroke,
                            boolean drawFront, 
                            boolean drawBack) {

        if (getDarkerSides()) {
            if (paint instanceof Color) {
                Color c = (Color) paint;
                c = c.darker();
                paint = c;
            }
        }

        double start = arc.getAngleStart();
        double extent = arc.getAngleExtent();
        double end = start + extent;

        g2.setStroke(outlineStroke);
        
        // for CLOCKWISE charts, the extent will be negative...
        if (extent < 0.0) {

            if (isAngleAtFront(start)) {  // start at front

                if (!isAngleAtBack(end)) {

                    if (extent > -180.0) {  // the segment is entirely at the 
                                            // front of the chart
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getEndPoint().getX(), plotArea.getY(), 
                                    arc.getStartPoint().getX() 
                                    - arc.getEndPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // the segment starts at the front, and wraps all 
                            // the way around
                            // the back and finishes at the front again
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(), 
                                plotArea.getHeight()));
                        side1.intersect(front);

                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));

                        side2.intersect(front);
                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawBack) {
                            g2.fill(back);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawBack) {
                            g2.draw(back);
                        }

                    }
                }
                else {  // starts at the front, finishes at the back (going 
                        // around the left side)

                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(), 
                                plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            }
            else {  // the segment starts at the back (still extending 
                    // CLOCKWISE)

                if (!isAngleAtFront(end)) {
                    if (extent > -180.0) {  // whole segment stays at the back
                        if (drawBack) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getStartPoint().getX(), plotArea.getY(),
                                    arc.getEndPoint().getX() 
                                    - arc.getStartPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(back);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // starts at the back, wraps around front, and 
                            // finishes at back again
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);

                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));

                        side2.intersect(back);

                        g2.setPaint(paint);
                        if (drawBack) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawFront) {
                            g2.fill(front);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawBack) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawFront) {
                            g2.draw(front);
                        }

                    }
                }
                else {  // starts at back, finishes at front (CLOCKWISE)

                    if (drawBack) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }

                    if (drawFront) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                }
            }
        }
        else if (extent > 0.0) {  // the pie sections are arranged ANTICLOCKWISE

            if (isAngleAtFront(start)) {  // segment starts at the front

                if (!isAngleAtBack(end)) {  // and finishes at the front

                    if (extent < 180.0) {  // segment only occupies the front
                        if (drawFront) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getStartPoint().getX(), plotArea.getY(),
                                    arc.getEndPoint().getX() 
                                    - arc.getStartPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(front);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // segments wraps right around the back...
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);

                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);

                        g2.setPaint(paint);
                        if (drawFront) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawBack) {
                            g2.fill(back);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawFront) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawBack) {
                            g2.draw(back);
                        }

                    }
                }
                else {  // segments starts at front and finishes at back...
                    if (drawBack) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }

                    if (drawFront) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }
                }
            }
            else {  // segment starts at back

                if (!isAngleAtFront(end)) {
                    if (extent < 180.0) {  // and finishes at back
                        if (drawBack) {
                            Area side = new Area(new Rectangle2D.Double(
                                    arc.getEndPoint().getX(), plotArea.getY(),
                                    arc.getStartPoint().getX() 
                                    - arc.getEndPoint().getX(),
                                    plotArea.getHeight()));
                            side.intersect(back);
                            g2.setPaint(paint);
                            g2.fill(side);
                            g2.setPaint(outlinePaint);
                            g2.draw(side);
                        }
                    }
                    else {  // starts at back and wraps right around to the 
                            // back again
                        Area side1 = new Area(new Rectangle2D.Double(
                                arc.getStartPoint().getX(), plotArea.getY(),
                                plotArea.getX() - arc.getStartPoint().getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);

                        Area side2 = new Area(new Rectangle2D.Double(
                                arc.getEndPoint().getX(), plotArea.getY(),
                                plotArea.getMaxX() - arc.getEndPoint().getX(),
                                plotArea.getHeight()));
                        side2.intersect(back);

                        g2.setPaint(paint);
                        if (drawBack) {
                            g2.fill(side1);
                            g2.fill(side2);
                        }

                        if (drawFront) {
                            g2.fill(front);
                        }

                        g2.setPaint(outlinePaint);
                        if (drawBack) {
                            g2.draw(side1);
                            g2.draw(side2);
                        }

                        if (drawFront) {
                            g2.draw(front);
                        }

                    }
                }
                else {  // starts at the back and finishes at the front 
                        // (wrapping the left side)
                    if (drawBack) {
                        Area side1 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getStartPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side1.intersect(back);
                        g2.setPaint(paint);
                        g2.fill(side1);
                        g2.setPaint(outlinePaint);
                        g2.draw(side1);
                    }

                    if (drawFront) {
                        Area side2 = new Area(new Rectangle2D.Double(
                                plotArea.getX(), plotArea.getY(),
                                arc.getEndPoint().getX() - plotArea.getX(),
                                plotArea.getHeight()));
                        side2.intersect(front);
                        g2.setPaint(paint);
                        g2.fill(side2);
                        g2.setPaint(outlinePaint);
                        g2.draw(side2);
                    }
                }
            }

        }

    }

    /**
     * Returns a short string describing the type of plot.
     *
     * @return <i>Pie 3D Plot</i>.
     */
    public String getPlotType() {
        return localizationResources.getString("Pie_3D_Plot");
    }

    /**
     * A utility method that returns true if the angle represents a point at 
     * the front of the 3D pie chart.  0 - 180 degrees is the back, 180 - 360 
     * is the front.
     *
     * @param angle  the angle.
     *
     * @return A boolean.
     */
    private boolean isAngleAtFront(double angle) {
        return (Math.sin(Math.toRadians(angle)) < 0.0);
    }

    /**
     * A utility method that returns true if the angle represents a point at 
     * the back of the 3D pie chart.  0 - 180 degrees is the back, 180 - 360 
     * is the front.
     *
     * @param angle  the angle.
     *
     * @return <code>true</code> if the angle is at the back of the pie.
     */
    private boolean isAngleAtBack(double angle) {
        return (Math.sin(Math.toRadians(angle)) > 0.0);
    }
    
    /**
     * Tests this plot for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean.
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PiePlot3D)) {
            return false;
        }
        PiePlot3D that = (PiePlot3D) obj;
        if (this.depthFactor != that.depthFactor) {
            return false;
        }
        if (this.darkerSides != that.darkerSides) {
            return false;
        }
        return super.equals(obj);
    }

}
