package com.baremetalstudios.minicam.geometry;

import static org.junit.Assert.*;

import org.junit.Test;


public class PointTest {

    @Test
    public void pointIsCorrectlyRotatedCounterclockwise() throws Exception {
        Point p = new Point(3, 5);
        p.rotate90(Direction.CCW);
        assertEquals(-5, p.getX(), 0.000001);
        assertEquals(3, p.getY(), 0.000001);
        p.rotate90(Direction.CCW);
        assertEquals(-3, p.getX(), 0.000001);
        assertEquals(-5, p.getY(), 0.000001);
        p.rotate90(Direction.CCW);
        assertEquals(5, p.getX(), 0.000001);
        assertEquals(-3, p.getY(), 0.000001);
        p.rotate90(Direction.CCW);
        assertEquals(3, p.getX(), 0.000001);
        assertEquals(5, p.getY(), 0.000001);
    }

    @Test
    public void pointIsCorrectlyRotatedClockwise() throws Exception {
        Point p = new Point(3, 5);
        p.rotate90(Direction.CW);
        assertEquals(5, p.getX(), 0.000001);
        assertEquals(-3, p.getY(), 0.000001);
        p.rotate90(Direction.CW);
        assertEquals(-3, p.getX(), 0.000001);
        assertEquals(-5, p.getY(), 0.000001);
        p.rotate90(Direction.CW);
        assertEquals(-5, p.getX(), 0.000001);
        assertEquals(3, p.getY(), 0.000001);
        p.rotate90(Direction.CW);
        assertEquals(3, p.getX(), 0.000001);
        assertEquals(5, p.getY(), 0.000001);
    }
}
