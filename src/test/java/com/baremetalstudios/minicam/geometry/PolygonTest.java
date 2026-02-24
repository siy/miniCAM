package com.baremetalstudios.minicam.geometry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PolygonTest {

    @Test
    public void optimizeRemovesCollinearPointsWithSameX() {
        // Three points with same X (vertical collinear): middle point should be removed
        List<Point> points = Arrays.asList(
                new Point(1, 1),
                new Point(1, 2),
                new Point(1, 3),
                new Point(2, 3),
                new Point(2, 1),
                new Point(1, 1)
        );
        Polygon polygon = new Polygon(points);
        assertEquals(6, polygon.count());

        polygon.optimize();

        // The middle collinear point (1,2) should be removed
        assertEquals(5, polygon.count());
        // First point preserved
        assertEquals(1.0, polygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(0).getY(), 0.000001);
        // Skipped (1,2), next is (1,3)
        assertEquals(1.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(3.0, polygon.getPoints().get(1).getY(), 0.000001);
        // Last point always preserved
        assertEquals(1.0, polygon.getLast().getX(), 0.000001);
        assertEquals(1.0, polygon.getLast().getY(), 0.000001);
    }

    @Test
    public void optimizeRemovesCollinearPointsWithSameY() {
        // Three points with same Y (horizontal collinear): middle point should be removed
        List<Point> points = Arrays.asList(
                new Point(1, 1),
                new Point(2, 1),
                new Point(3, 1),
                new Point(3, 3),
                new Point(1, 3),
                new Point(1, 1)
        );
        Polygon polygon = new Polygon(points);
        assertEquals(6, polygon.count());

        polygon.optimize();

        // The middle collinear point (2,1) should be removed
        assertEquals(5, polygon.count());
        assertEquals(1.0, polygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(3.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(1).getY(), 0.000001);
    }

    @Test
    public void optimizePreservesNonCollinearPoints() {
        // A square: no collinear points to remove
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 1),
                new Point(1, 1),
                new Point(1, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);
        polygon.optimize();

        assertEquals(5, polygon.count());
    }

    @Test
    public void optimizeRemovesMultipleCollinearSequences() {
        // Multiple collinear sequences: both X and Y
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 1),
                new Point(0, 2),  // collinear X with previous two
                new Point(1, 2),
                new Point(2, 2),  // collinear Y with previous two
                new Point(2, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);
        assertEquals(7, polygon.count());

        polygon.optimize();

        // Both (0,1) and (1,2) should be removed
        assertEquals(5, polygon.count());
    }

    @Test
    public void reorderRotatesToNearestPointToCenter() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);

        // Center near (10,10) - nearest point should be (10,10)
        Point center = new Point(9, 9);
        polygon.reorder(center);

        // After reorder, first point should be (10,10)
        assertEquals(10.0, polygon.getFirst().getX(), 0.000001);
        assertEquals(10.0, polygon.getFirst().getY(), 0.000001);
        // Polygon should be closed: last == first
        assertTrue(polygon.isClosed());
    }

    @Test
    public void reorderClosesOpenPolygon() {
        // Open polygon (first != last)
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 10),
                new Point(10, 10),
                new Point(10, 0)
        );
        Polygon polygon = new Polygon(points);
        assertFalse(polygon.isClosed());

        Point center = new Point(0, 0);
        polygon.reorder(center);

        // After reorder, polygon should be closed
        assertTrue(polygon.isClosed());
    }

    @Test
    public void mergeCreatesCombinedPolygon() {
        List<Point> points1 = Arrays.asList(new Point(0, 0), new Point(1, 1));
        List<Point> points2 = Arrays.asList(new Point(2, 2), new Point(3, 3));
        Polygon p1 = new Polygon(points1);
        Polygon p2 = new Polygon(points2);

        Polygon merged = Polygon.merge(p1, p2);

        assertEquals(4, merged.count());
        assertEquals(0.0, merged.getPoints().get(0).getX(), 0.000001);
        assertEquals(0.0, merged.getPoints().get(0).getY(), 0.000001);
        assertEquals(1.0, merged.getPoints().get(1).getX(), 0.000001);
        assertEquals(1.0, merged.getPoints().get(1).getY(), 0.000001);
        assertEquals(2.0, merged.getPoints().get(2).getX(), 0.000001);
        assertEquals(2.0, merged.getPoints().get(2).getY(), 0.000001);
        assertEquals(3.0, merged.getPoints().get(3).getX(), 0.000001);
        assertEquals(3.0, merged.getPoints().get(3).getY(), 0.000001);
    }

    @Test
    public void toStringIncludesSizeClosedInnerDirectionPointCount() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 2),
                new Point(3, 2),
                new Point(3, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);
        polygon.setInner(false);
        polygon.setDirection(Direction.CW);

        String result = polygon.toString();

        assertTrue(result.contains("3.00000"));  // sizeX
        assertTrue(result.contains("2.00000"));  // sizeY
        assertTrue(result.contains("closed"));
        assertTrue(result.contains("outer"));
        assertTrue(result.contains("CW"));
        assertTrue(result.contains("5"));        // point count
    }

    @Test
    public void toStringShowsOpenAndInner() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(5, 5)
        );
        Polygon polygon = new Polygon(points);
        polygon.setInner(true);
        polygon.setDirection(Direction.CCW);

        String result = polygon.toString();

        assertTrue(result.contains("open"));
        assertTrue(result.contains("inner"));
        assertTrue(result.contains("CCW"));
    }

    @Test
    public void extendAddsPointsAndRecalculatesMinMax() {
        List<Point> points = Arrays.asList(new Point(0, 0), new Point(1, 1));
        Polygon polygon = new Polygon(points);

        assertEquals(1.0, polygon.getMaxX(), 0.000001);
        assertEquals(1.0, polygon.getMaxY(), 0.000001);

        polygon.extend(Arrays.asList(new Point(5, 5), new Point(6, 6)));

        assertEquals(4, polygon.count());
        assertEquals(6.0, polygon.getMaxX(), 0.000001);
        assertEquals(6.0, polygon.getMaxY(), 0.000001);
        assertEquals(0.0, polygon.getMinX(), 0.000001);
        assertEquals(0.0, polygon.getMinY(), 0.000001);
    }

    @Test
    public void insertInsertsPointAtIndex() {
        List<Point> points = Arrays.asList(new Point(0, 0), new Point(2, 2));
        Polygon polygon = new Polygon(points);

        polygon.insert(new Point(1, 1), 1);

        assertEquals(3, polygon.count());
        assertEquals(1.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(1).getY(), 0.000001);
    }

    @Test
    public void isClosedReturnsTrueWhenFirstEqualsLastWithinPrecision() {
        List<Point> points = Arrays.asList(
                new Point(1, 1),
                new Point(2, 2),
                new Point(1.0005, 1.0005)  // within PRECISION (0.001) of first
        );
        Polygon polygon = new Polygon(points);

        assertTrue(polygon.isClosed());
    }

    @Test
    public void isClosedReturnsFalseWhenFirstNotEqualToLast() {
        List<Point> points = Arrays.asList(
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3)
        );
        Polygon polygon = new Polygon(points);

        assertFalse(polygon.isClosed());
    }

    @Test
    public void translateMovesAllPoints() {
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(1, 1),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);

        polygon.translate(5, 10);

        assertEquals(5.0, polygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(10.0, polygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(6.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(11.0, polygon.getPoints().get(1).getY(), 0.000001);
        assertEquals(5.0, polygon.getPoints().get(2).getX(), 0.000001);
        assertEquals(10.0, polygon.getPoints().get(2).getY(), 0.000001);

        // MinMax should be recalculated
        assertEquals(5.0, polygon.getMinX(), 0.000001);
        assertEquals(10.0, polygon.getMinY(), 0.000001);
        assertEquals(6.0, polygon.getMaxX(), 0.000001);
        assertEquals(11.0, polygon.getMaxY(), 0.000001);
    }

    @Test
    public void rotate90RotatesAllPoints() {
        List<Point> points = Arrays.asList(
                new Point(1, 0),
                new Point(0, 1),
                new Point(1, 0)
        );
        Polygon polygon = new Polygon(points);

        polygon.rotate90(Direction.CCW);

        // CCW rotation: (x, y) -> (-y, x)
        assertEquals(0.0, polygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(-1.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(0.0, polygon.getPoints().get(1).getY(), 0.000001);
    }

    @Test
    public void rotate90CWRotatesAllPoints() {
        List<Point> points = Arrays.asList(
                new Point(1, 0),
                new Point(0, 1),
                new Point(1, 0)
        );
        Polygon polygon = new Polygon(points);

        polygon.rotate90(Direction.CW);

        // CW rotation: (x, y) -> (y, -x)
        assertEquals(0.0, polygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(-1.0, polygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(1.0, polygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(0.0, polygon.getPoints().get(1).getY(), 0.000001);
    }

    @Test
    public void sizeXAndSizeYCalculatedCorrectly() {
        List<Point> points = Arrays.asList(
                new Point(1, 2),
                new Point(4, 7),
                new Point(1, 2)
        );
        Polygon polygon = new Polygon(points);

        assertEquals(3.0, polygon.sizeX(), 0.000001);
        assertEquals(5.0, polygon.sizeY(), 0.000001);
        assertEquals(3.0, polygon.getSizeX(), 0.000001);
        assertEquals(5.0, polygon.getSizeY(), 0.000001);
    }

    @Test
    public void innerAndDirectionGettersAndSetters() {
        List<Point> points = Arrays.asList(new Point(0, 0), new Point(1, 1));
        Polygon polygon = new Polygon(points);

        assertFalse(polygon.isInner());
        assertEquals(Direction.CCW, polygon.getDirection());

        polygon.setInner(true);
        polygon.setDirection(Direction.CW);

        assertTrue(polygon.isInner());
        assertEquals(Direction.CW, polygon.getDirection());
    }

    @Test
    public void getFirstAndGetLastReturnCorrectPoints() {
        List<Point> points = Arrays.asList(
                new Point(1, 2),
                new Point(3, 4),
                new Point(5, 6)
        );
        Polygon polygon = new Polygon(points);

        assertEquals(1.0, polygon.getFirst().getX(), 0.000001);
        assertEquals(2.0, polygon.getFirst().getY(), 0.000001);
        assertEquals(5.0, polygon.getLast().getX(), 0.000001);
        assertEquals(6.0, polygon.getLast().getY(), 0.000001);
    }

    @Test
    public void insertAtBeginning() {
        List<Point> points = Arrays.asList(new Point(1, 1), new Point(2, 2));
        Polygon polygon = new Polygon(points);

        polygon.insert(new Point(0, 0), 0);

        assertEquals(3, polygon.count());
        assertEquals(0.0, polygon.getFirst().getX(), 0.000001);
        assertEquals(0.0, polygon.getFirst().getY(), 0.000001);
    }

    @Test
    public void reorderWithClosedPolygonRotatesCorrectly() {
        // Closed square polygon
        List<Point> points = Arrays.asList(
                new Point(0, 0),
                new Point(0, 5),
                new Point(5, 5),
                new Point(5, 0),
                new Point(0, 0)
        );
        Polygon polygon = new Polygon(points);
        assertTrue(polygon.isClosed());

        // Center near (5,5) - should rotate to start at (5,5)
        Point result = polygon.reorder(new Point(5, 5));

        assertEquals(5.0, polygon.getFirst().getX(), 0.000001);
        assertEquals(5.0, polygon.getFirst().getY(), 0.000001);
        // Closed after reorder
        assertTrue(polygon.isClosed());
        // reorder returns getLast()
        assertEquals(polygon.getLast().getX(), result.getX(), 0.000001);
        assertEquals(polygon.getLast().getY(), result.getY(), 0.000001);
    }
}
