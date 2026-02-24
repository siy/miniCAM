package com.baremetalstudios.minicam.geometry;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class PointAdditionalTest {

    @Test
    public void copyConstructorCreatesIndependentCopy() {
        Point original = new Point(3.5, 7.2);
        Point copy = new Point(original);

        assertEquals(original.getX(), copy.getX(), 0.000001);
        assertEquals(original.getY(), copy.getY(), 0.000001);

        // Modifying copy should not affect original
        copy.translate(1, 1);
        assertEquals(3.5, original.getX(), 0.000001);
        assertEquals(7.2, original.getY(), 0.000001);
        assertEquals(4.5, copy.getX(), 0.000001);
        assertEquals(8.2, copy.getY(), 0.000001);
    }

    @Test
    public void toStringReturnsFormattedString() {
        Point p = new Point(1.5, 2.75);
        String result = p.toString();

        assertEquals("(1.500000, 2.750000)", result);
    }

    @Test
    public void toStringForZeroCoordinates() {
        Point p = new Point(0, 0);
        String result = p.toString();

        assertEquals("(0.000000, 0.000000)", result);
    }

    @Test
    public void scaleDownDividesByTen() {
        Point p = new Point(30, 50);
        p.scaleDown();

        assertEquals(3.0, p.getX(), 0.000001);
        assertEquals(5.0, p.getY(), 0.000001);
    }

    @Test
    public void scaleDownWithFractionalValues() {
        Point p = new Point(1.5, 2.5);
        p.scaleDown();

        assertEquals(0.15, p.getX(), 0.000001);
        assertEquals(0.25, p.getY(), 0.000001);
    }

    @Test
    public void findNearestReturnsIndexOfNearestPoint() {
        Point base = new Point(0, 0);
        List<Point> list = Arrays.asList(
                new Point(10, 10),
                new Point(1, 1),
                new Point(5, 5)
        );

        int idx = Point.findNearest(base, list, false);

        assertEquals(1, idx);  // (1,1) is nearest to (0,0)
    }

    @Test
    public void findNearestReturnsMinusOneForEmptyList() {
        Point base = new Point(0, 0);
        List<Point> list = Collections.emptyList();

        int idx = Point.findNearest(base, list, false);

        assertEquals(-1, idx);
    }

    @Test
    public void findNearestReturnsFirstWhenAllEquidistant() {
        Point base = new Point(0, 0);
        List<Point> list = Arrays.asList(
                new Point(1, 0),
                new Point(0, 1),
                new Point(-1, 0)
        );

        // All are distance 1.0 from origin; first one found with minimum distance wins
        int idx = Point.findNearest(base, list, false);
        assertEquals(0, idx);
    }

    @Test
    public void isRetractDefaultsFalse() {
        Point p = new Point(1, 1);
        assertFalse(p.isRetract());
    }

    @Test
    public void setRetractChangesFlag() {
        Point p = new Point(1, 1);
        p.setRetract(true);
        assertTrue(p.isRetract());

        p.setRetract(false);
        assertFalse(p.isRetract());
    }

    @Test
    public void distanceCalculation() {
        Point p1 = new Point(0, 0);
        Point p2 = new Point(3, 4);

        assertEquals(5.0, Point.distance(p1, p2), 0.000001);
    }

    @Test
    public void distanceToSamePointIsZero() {
        Point p1 = new Point(5, 5);
        Point p2 = new Point(5, 5);

        assertEquals(0.0, Point.distance(p1, p2), 0.000001);
    }

    @Test
    public void distanceIsSymmetric() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(4, 6);

        assertEquals(Point.distance(p1, p2), Point.distance(p2, p1), 0.000001);
    }

    @Test
    public void atFactoryMethodCreatesPoint() {
        Point p = Point.at(3.14, 2.71);

        assertEquals(3.14, p.getX(), 0.000001);
        assertEquals(2.71, p.getY(), 0.000001);
    }

    @Test
    public void translateMovesPoint() {
        Point p = new Point(1, 2);
        p.translate(3, 4);

        assertEquals(4.0, p.getX(), 0.000001);
        assertEquals(6.0, p.getY(), 0.000001);
    }

    @Test
    public void translateWithNegativeValues() {
        Point p = new Point(5, 5);
        p.translate(-3, -2);

        assertEquals(2.0, p.getX(), 0.000001);
        assertEquals(3.0, p.getY(), 0.000001);
    }

    @Test
    public void findNearestWithSingleElement() {
        Point base = new Point(0, 0);
        List<Point> list = Arrays.asList(new Point(100, 100));

        int idx = Point.findNearest(base, list, false);

        assertEquals(0, idx);
    }

    @Test
    public void distanceWithNegativeCoordinates() {
        Point p1 = new Point(-3, -4);
        Point p2 = new Point(0, 0);

        assertEquals(5.0, Point.distance(p1, p2), 0.000001);
    }

    @Test
    public void copyConstructorDoesNotCopyRetract() {
        Point original = new Point(1, 2);
        original.setRetract(true);

        Point copy = new Point(original);
        // Copy constructor only copies x,y - retract defaults to false
        assertFalse(copy.isRetract());
    }
}
