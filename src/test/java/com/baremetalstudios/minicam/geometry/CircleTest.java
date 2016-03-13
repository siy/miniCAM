package com.baremetalstudios.minicam.geometry;

import static org.junit.Assert.*;

import org.junit.Test;


public class CircleTest {
    @Test
    public void isCircleClosed() throws Exception {
        assertTrue(new Circle(Point.at(10, 10), 2.8).isClosed());
    }
}
