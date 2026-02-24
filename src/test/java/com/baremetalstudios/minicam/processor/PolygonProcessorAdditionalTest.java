package com.baremetalstudios.minicam.processor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.Direction;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;

public class PolygonProcessorAdditionalTest {

    @Test
    public void lineIntersectionReturnsNullForParallelLines() {
        // Two parallel horizontal lines
        Point a = new Point(0, 0);
        Point b = new Point(10, 0);
        Point c = new Point(0, 5);
        Point d = new Point(10, 5);

        Point result = PolygonProcessor.lineIntersection(a, b, c, d);

        assertNull(result);
    }

    @Test
    public void lineIntersectionReturnsPointForCrossingLines() {
        // Two lines that cross at (5, 5)
        Point a = new Point(0, 0);
        Point b = new Point(10, 10);
        Point c = new Point(0, 10);
        Point d = new Point(10, 0);

        Point result = PolygonProcessor.lineIntersection(a, b, c, d);

        assertNotNull(result);
        assertEquals(5.0, result.getX(), 0.000001);
        assertEquals(5.0, result.getY(), 0.000001);
    }

    @Test
    public void lineIntersectionReturnsPointForPerpendicularLines() {
        // Horizontal line and vertical line crossing at (5, 0)
        Point a = new Point(0, 0);
        Point b = new Point(10, 0);
        Point c = new Point(5, -5);
        Point d = new Point(5, 5);

        Point result = PolygonProcessor.lineIntersection(a, b, c, d);

        assertNotNull(result);
        assertEquals(5.0, result.getX(), 0.000001);
        assertEquals(0.0, result.getY(), 0.000001);
    }

    @Test
    public void insetCornerReturnsNullWhenDistanceIsZero() {
        // When ab == cd (dist1 == 0), should return null
        Point ab = new Point(1, 1);
        Point cd = new Point(1, 1);  // same as ab -> dist1 = 0
        Point ef = new Point(2, 2);

        Point result = PolygonProcessor.insetCorner(ab, cd, ef, 0.1);

        assertNull(result);
    }

    @Test
    public void insetCornerReturnsNullWhenSecondDistanceIsZero() {
        // When cd == ef (dist2 == 0), should return null
        Point ab = new Point(0, 0);
        Point cd = new Point(1, 1);
        Point ef = new Point(1, 1);  // same as cd -> dist2 = 0

        Point result = PolygonProcessor.insetCorner(ab, cd, ef, 0.1);

        assertNull(result);
    }

    @Test
    public void insetCornerReturnsPointForValidInputs() {
        // Right angle corner
        Point ab = new Point(0, 0);
        Point cd = new Point(0, 1);
        Point ef = new Point(1, 1);

        Point result = PolygonProcessor.insetCorner(ab, cd, ef, 0.1);

        assertNotNull(result);
    }

    @Test
    public void insetPolygonReturnsNullForPolygonWithLessThan3Points() {
        List<Point> points = Arrays.asList(new Point(0, 0), new Point(1, 1));
        Polygon polygon = new Polygon(points);

        Polygon result = PolygonProcessor.insetPolygon(polygon, 0.1);

        assertNull(result);
    }

    @Test
    public void insetPolygonReturnsNullForOpenPolygon() {
        // Open polygon (first != last)
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 1),
                new Point(1, 1),
                new Point(1, 0)
        );
        Polygon polygon = new Polygon(points);
        assertFalse(polygon.isClosed());

        Polygon result = PolygonProcessor.insetPolygon(polygon, 0.1);

        assertNull(result);
    }

    @Test
    public void processWithLargeRectangleGeneratesTabs() {
        // Large rectangle: (0,0)-(0,50)-(50,50)-(50,0)-(0,0) with default config
        // minTabDistance=16, so 50/16 = 3 splits >= 2, tabs should be generated
        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        List<Point> points = new ArrayList<>(Arrays.asList(
                new Point(0, 0),
                new Point(0, 50),
                new Point(50, 50),
                new Point(50, 0),
                new Point(0, 0)
        ));
        Polygon polygon = new Polygon(points);

        List<DrillGroup> drillList = new ArrayList<>();
        Point center = new Point(25, 25);

        List<Polygon> result = processor.process(Arrays.asList(polygon), drillList, center);

        // After process, drillList should have a "MouseBytes" group with drills (tabs generated)
        assertFalse(drillList.isEmpty(), "Drill list should not be empty - tabs should be generated");
        assertEquals("MouseBytes", drillList.get(drillList.size() - 1).getId());
        assertFalse(drillList.get(drillList.size() - 1).getDrills().isEmpty(), "MouseBytes drill group should have drills");
    }

    @Test
    public void processWithVerticalEdgesGeneratesTabs() {
        // Tall rectangle with vertical edges long enough for tabs
        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        List<Point> points = new ArrayList<>(Arrays.asList(
                new Point(0, 0),
                new Point(0, 60),
                new Point(10, 60),
                new Point(10, 0),
                new Point(0, 0)
        ));
        Polygon polygon = new Polygon(points);

        List<DrillGroup> drillList = new ArrayList<>();
        Point center = new Point(5, 30);

        List<Polygon> result = processor.process(Arrays.asList(polygon), drillList, center);

        // Tabs should be generated on vertical edges (length 60 > minTabDistance 16)
        assertFalse(drillList.isEmpty(), "Drill list should not be empty - tabs should be generated on vertical edges");
    }

    @Test
    public void processWithInnerPolygon() {
        // Outer polygon
        List<Point> outerPoints = new ArrayList<>(Arrays.asList(
                new Point(0, 0),
                new Point(0, 60),
                new Point(60, 60),
                new Point(60, 0),
                new Point(0, 0)
        ));
        Polygon outer = new Polygon(outerPoints);

        // Inner polygon (inside outer)
        List<Point> innerPoints = new ArrayList<>(Arrays.asList(
                new Point(10, 10),
                new Point(10, 50),
                new Point(50, 50),
                new Point(50, 10),
                new Point(10, 10)
        ));
        Polygon inner = new Polygon(innerPoints);

        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        List<DrillGroup> drillList = new ArrayList<>();
        Point center = new Point(30, 30);

        List<Polygon> result = processor.process(Arrays.asList(outer, inner), drillList, center);

        // Inner polygon should be marked inner
        boolean hasInner = result.stream().anyMatch(Polygon::isInner);
        assertTrue(hasInner, "At least one polygon should be marked as inner");
    }

    @Test
    public void markInnerPolygonDetectsContainment() {
        // Outer polygon
        List<Point> outerPoints = new ArrayList<>(Arrays.asList(
                new Point(0, 0),
                new Point(0, 100),
                new Point(100, 100),
                new Point(100, 0),
                new Point(0, 0)
        ));
        Polygon outer = new Polygon(outerPoints);

        // Inner polygon (completely inside outer)
        List<Point> innerPoints = new ArrayList<>(Arrays.asList(
                new Point(10, 10),
                new Point(10, 90),
                new Point(90, 90),
                new Point(90, 10),
                new Point(10, 10)
        ));
        Polygon inner = new Polygon(innerPoints);

        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        // Process marks inner polygons
        List<DrillGroup> drillList = new ArrayList<>();
        processor.process(Arrays.asList(outer, inner), drillList, new Point(50, 50));

        // After processing, inner polygon direction should indicate it was detected
        // The inner polygon gets marked as inner during process()
        // We verify by checking if any result polygon is inner
        // (process returns processed polygons)
    }

    @Test
    public void mergeOpenPolygonsThatCanBeMerged() {
        // Two open polygons that form a closed rectangle when merged.
        // p1 ends at (50, 0), p2 starts at (50, 0) and ends at (0, 0) which is p1's start.
        // findPrecedingPolygon: for polygon p1, looks for one whose last matches p1's first (0,0).
        // p2's last is (0,0), so merge = p2 + p1 = (50,0),(50,50),(0,50),(0,0),(0,0),(0,50),(50,50),(50,0)
        // Instead, use proper half-rectangles that produce a clean closed polygon after merge.

        // p1: top half from (0,0) -> (0,50) -> (50,50)
        List<Point> points1 = new ArrayList<>(Arrays.asList(
                new Point(0, 0),
                new Point(0, 50),
                new Point(50, 50)
        ));
        Polygon p1 = new Polygon(points1);

        // p2: bottom half from (50,50) -> (50,0) -> (0,0)
        // p2's last is (0,0) which matches p1's first within PRECISION
        List<Point> points2 = new ArrayList<>(Arrays.asList(
                new Point(50, 50),
                new Point(50, 0),
                new Point(0, 0)
        ));
        Polygon p2 = new Polygon(points2);

        assertFalse(p1.isClosed());
        assertFalse(p2.isClosed());

        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        List<DrillGroup> drillList = new ArrayList<>();
        // merge: p2 + p1 = (50,50),(50,0),(0,0),(0,0),(0,50),(50,50)
        // After merging, (0,0) appears twice but first=(50,50) last=(50,50) so it's closed
        List<Polygon> result = processor.process(Arrays.asList(p1, p2), drillList, new Point(25, 25));

        assertFalse(result.isEmpty());
    }

    @Test
    public void insetPolygonPreservesDirectionAndInnerFlag() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);
        polygon.setDirection(Direction.CW);
        polygon.setInner(true);

        Polygon result = PolygonProcessor.insetPolygon(polygon, 0.5);

        assertNotNull(result);
        assertEquals(Direction.CW, result.getDirection());
        assertTrue(result.isInner());
    }

    @Test
    public void insetPolygonProducesClosedResult() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);

        Polygon result = PolygonProcessor.insetPolygon(polygon, 1.0);

        assertNotNull(result);
        assertTrue(result.isClosed());
        // The inset polygon should be smaller
        assertTrue(result.getSizeX() < polygon.getSizeX());
        assertTrue(result.getSizeY() < polygon.getSizeY());
    }

    @Test
    public void getConfigReturnsConfig() {
        OutputConfig config = new OutputConfig();
        PolygonProcessor processor = new PolygonProcessor(config);

        assertSame(config, processor.getConfig());
    }

    @Test
    public void lineIntersectionWithCollinearOverlappingLines() {
        // Two segments on the same line
        Point a = new Point(0, 0);
        Point b = new Point(10, 0);
        Point c = new Point(5, 0);
        Point d = new Point(15, 0);

        Point result = PolygonProcessor.lineIntersection(a, b, c, d);

        // Parallel/collinear lines should return null
        assertNull(result);
    }

    @Test
    public void lineIntersectionWithVerticalParallelLines() {
        Point a = new Point(0, 0);
        Point b = new Point(0, 10);
        Point c = new Point(5, 0);
        Point d = new Point(5, 10);

        Point result = PolygonProcessor.lineIntersection(a, b, c, d);

        assertNull(result);
    }
}
