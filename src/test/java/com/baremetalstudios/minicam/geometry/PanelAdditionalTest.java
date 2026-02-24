package com.baremetalstudios.minicam.geometry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PanelAdditionalTest {

    @Test
    public void getCenterReturnsMidpoint() {
        Panel panel = createSimplePanel();
        Point center = panel.getCenter();

        // Outlines span (0,0) to (10,10)
        assertEquals(5.0, center.getX(), 0.000001);
        assertEquals(5.0, center.getY(), 0.000001);
    }

    @Test
    public void getPanelSizeReturnsWidthHeight() {
        Panel panel = createSimplePanel();
        Point size = panel.getPanelSize();

        assertEquals(10.0, size.getX(), 0.000001);
        assertEquals(10.0, size.getY(), 0.000001);
    }

    @Test
    public void getMinXYReturnsBottomLeft() {
        Panel panel = createSimplePanel();
        Point minXY = panel.getMinXY();

        assertEquals(0.0, minXY.getX(), 0.000001);
        assertEquals(0.0, minXY.getY(), 0.000001);
    }

    @Test
    public void getMinXYWithOffsetPolygons() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(5, 3),
                new Point(5, 8),
                new Point(15, 8),
                new Point(15, 3),
                new Point(5, 3)
        ));
        Panel panel = new Panel(Arrays.asList(p), new ArrayList<>());
        Point minXY = panel.getMinXY();

        assertEquals(5.0, minXY.getX(), 0.000001);
        assertEquals(3.0, minXY.getY(), 0.000001);
    }

    @Test
    public void translateMovesAllOutlinesAndDrills() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        DrillGroup d = new DrillGroup("T01", 0.5, Arrays.asList(new Point(5, 5)));
        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d));

        panel.translate(10, 20);

        // Check outline points moved
        assertEquals(10.0, p.getPoints().get(0).getX(), 0.000001);
        assertEquals(20.0, p.getPoints().get(0).getY(), 0.000001);
        assertEquals(10.0, p.getMinX(), 0.000001);
        assertEquals(20.0, p.getMinY(), 0.000001);
        assertEquals(20.0, p.getMaxX(), 0.000001);
        assertEquals(30.0, p.getMaxY(), 0.000001);

        // Check drill moved
        assertEquals(15.0, d.getDrills().get(0).getX(), 0.000001);
        assertEquals(25.0, d.getDrills().get(0).getY(), 0.000001);
    }

    @Test
    public void rotate90RotatesAllOutlinesAndDrills() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(1, 0),
                new Point(0, 1),
                new Point(1, 0)
        ));
        DrillGroup d = new DrillGroup("T01", 0.5, Arrays.asList(new Point(3, 4)));
        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d));

        panel.rotate90(Direction.CCW);

        // CCW rotation: (x, y) -> (-y, x)
        assertEquals(0.0, p.getPoints().get(0).getX(), 0.000001);
        assertEquals(1.0, p.getPoints().get(0).getY(), 0.000001);

        // Drill should also be rotated
        assertEquals(-4.0, d.getDrills().get(0).getX(), 0.000001);
        assertEquals(3.0, d.getDrills().get(0).getY(), 0.000001);
    }

    @Test
    public void rotate90CWRotatesAllOutlinesAndDrills() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(1, 0),
                new Point(0, 1),
                new Point(1, 0)
        ));
        DrillGroup d = new DrillGroup("T01", 0.5, Arrays.asList(new Point(3, 4)));
        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d));

        panel.rotate90(Direction.CW);

        // CW rotation: (x, y) -> (y, -x)
        assertEquals(0.0, p.getPoints().get(0).getX(), 0.000001);
        assertEquals(-1.0, p.getPoints().get(0).getY(), 0.000001);

        assertEquals(4.0, d.getDrills().get(0).getX(), 0.000001);
        assertEquals(-3.0, d.getDrills().get(0).getY(), 0.000001);
    }

    @Test
    public void constructorDetectsOversizedDrillsAndScalesDown() {
        // Outline spans (0,0) to (10,10), so size is (10,10), offset by min (0,0) gives (10,10)
        // Drill at (1000, 1000) which is > 8 * 10 = 80 -> should trigger scale down
        Polygon p = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        // Drill at coordinates much larger than panel -> should be scaled down
        Point drillPoint = new Point(1000, 1000);
        DrillGroup d = new DrillGroup("T01", 0.5, Arrays.asList(drillPoint));

        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d));

        // After constructor, drill should be scaled down by /10
        assertEquals(100.0, drillPoint.getX(), 0.000001);
        assertEquals(100.0, drillPoint.getY(), 0.000001);
    }

    @Test
    public void constructorDoesNotScaleNormalDrills() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        // Drill within panel bounds
        Point drillPoint = new Point(5, 5);
        DrillGroup d = new DrillGroup("T01", 0.5, Arrays.asList(drillPoint));

        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d));

        // Drill coordinates should remain unchanged
        assertEquals(5.0, drillPoint.getX(), 0.000001);
        assertEquals(5.0, drillPoint.getY(), 0.000001);
    }

    @Test
    public void getDrillStatsReturnsAggregatedStatistics() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        DrillGroup d1 = new DrillGroup("T01", 0.5, Arrays.asList(
                new Point(1, 1), new Point(2, 2)
        ));
        DrillGroup d2 = new DrillGroup("T02", 1.0, Arrays.asList(
                new Point(5, 5), new Point(6, 6), new Point(7, 7)
        ));
        Panel panel = new Panel(Arrays.asList(p), Arrays.asList(d1, d2));

        DrillStatistics stats = panel.getDrillStats();

        assertNotNull(stats);
        String statsStr = stats.toString();
        // Should contain info about both drill groups
        assertTrue(statsStr.contains("0.50"));
        assertTrue(statsStr.contains("1.00"));
        assertTrue(statsStr.contains("2 drills"));
        assertTrue(statsStr.contains("3 drills"));
        assertTrue(statsStr.contains("Total: 5 drills"));
    }

    @Test
    public void getPanelSizeWithMultiplePolygons() {
        Polygon p1 = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 5),
                new Point(5, 5),
                new Point(5, 0),
                new Point(0, 0)
        ));
        Polygon p2 = new Polygon(Arrays.asList(
                new Point(8, 8),
                new Point(8, 20),
                new Point(20, 20),
                new Point(20, 8),
                new Point(8, 8)
        ));
        Panel panel = new Panel(Arrays.asList(p1, p2), new ArrayList<>());
        Point size = panel.getPanelSize();

        // Should span from min(0,8)=0 to max(5,20)=20 in both directions
        assertEquals(20.0, size.getX(), 0.000001);
        assertEquals(20.0, size.getY(), 0.000001);
    }

    @Test
    public void getCenterWithMultiplePolygons() {
        Polygon p1 = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        Polygon p2 = new Polygon(Arrays.asList(
                new Point(10, 0),
                new Point(10, 10),
                new Point(20, 10),
                new Point(20, 0),
                new Point(10, 0)
        ));
        Panel panel = new Panel(Arrays.asList(p1, p2), new ArrayList<>());
        Point center = panel.getCenter();

        // Total span (0,0) to (20,10), center at (10, 5)
        assertEquals(10.0, center.getX(), 0.000001);
        assertEquals(5.0, center.getY(), 0.000001);
    }

    private Panel createSimplePanel() {
        Polygon p = new Polygon(Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        ));
        return new Panel(Arrays.asList(p), new ArrayList<>());
    }
}
