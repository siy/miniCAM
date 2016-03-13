package com.baremetalstudios.minicam.geometry;

import java.util.ArrayList;
import java.util.List;

public class Circle extends Polygon {
    public Circle(Point center, double diameter) {
        super(generateCircle(center, diameter));
        setInner(true);
        setDirection(Direction.CW);
    }

    private static List<Point> generateCircle(Point center, double diameter) {
        List<Point> result = new ArrayList<>();
        double radius = diameter / 2;
        long numSteps = (long) (((Math.PI * diameter) / (PRECISION * 4)) + 0.5) * 4;

        double step = (2.0 * Math.PI) / numSteps;
        double theta = 0;

        for (long i = 0; i <= numSteps; i++, theta += step) {
            Point point = Point.at(center.getX() + radius * Math.cos(theta), center.getY() - radius * Math.sin(theta));
            result.add(point);
        }
        return result;
    }
}
