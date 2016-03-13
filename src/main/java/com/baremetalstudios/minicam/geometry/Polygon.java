/*******************************************************************************
 * Copyright 2014, 2015 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.baremetalstudios.minicam.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Polygon implements Transformable {
    public static final double PRECISION = 0.001;

    private List<Point> points;
    private double minX;
    private double minY;
    private double maxX;
    private double maxY;

    private boolean inner;
    private Direction direction = Direction.CCW;

    public Polygon(List<Point> points) {
        this.points = new ArrayList<>(points);

        calculateMinMax();
    }

    @Override
    public void translate(double dX, double dY) {
        for (Point point : points) {
            point.translate(dX, dY);
        }
        calculateMinMax();
    }

    @Override
    public void rotate90(Direction direction) {
        for (Point point : points) {
            point.rotate90(direction);
        }
        calculateMinMax();
    }

    private final void calculateMinMax() {
        minX = Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        maxY = Double.MIN_VALUE;
        for (Point point : points) {
            minX = Math.min(point.getX(), minX);
            minY = Math.min(point.getY(), minY);
            maxX = Math.max(point.getX(), maxX);
            maxY = Math.max(point.getY(), maxY);
        }
    }

    public boolean isClosed() {
        return Point.distance(getFirst(), getLast()) < PRECISION;
    }

    public Point getLast() {
        return points.get(points.size() - 1);
    }

    public Point getFirst() {
        return points.get(0);
    }

    public int count() {
        return points.size();
    }

    public double sizeX() {
        return maxX - minX;
    }

    public double sizeY() {
        return maxY - minY;
    }

    public static Polygon merge(Polygon preceding, Polygon area) {
        List<Point> points = new ArrayList<>();
        points.addAll(preceding.points);
        points.addAll(area.points);

        return new Polygon(points);
    }

    public List<Point> getPoints() {
        return points;
    }

    @Override
    public String toString() {
        String body = String.format("{size (%.5f, %.5f), %s, %s, %s, points(%d)}", sizeX(), sizeY(),
                        (isClosed() ? "closed" : "open"), (isInner() ? "inner":"outer"), direction, points.size());
        return body;
    }

    public void extend(List<Point> points) {
        this.points.addAll(points);
        calculateMinMax();
    }

    public boolean isInner() {
        return inner;
    }

    public void setInner(boolean inner) {
        this.inner = inner;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getMinX() {
        return minX;
    }

    public double getMinY() {
        return minY;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMaxY() {
        return maxY;
    }

    public void insert(Point point, int idx) {
        points.add(idx, point);
    }

    public double getSizeX() {
        return maxX - minX;
    }

    public double getSizeY() {
        return maxY - minY;
    }

    public void optimize() {
        List<Point> inPoints = new ArrayList<>(points);
        Point lastPoint = getLast();
        points.clear();
        points.add(inPoints.get(0));
        for (int i = 2; i < inPoints.size(); i++) {
            Point ab = inPoints.get(i - 2);
            Point cd = inPoints.get(i - 1);
            Point ef = inPoints.get(i);

            if (ab.getX() == cd.getX() && cd.getX() == ef.getX()) {
                continue;
            }

            if (ab.getY() == cd.getY() && cd.getY() == ef.getY()) {
                continue;
            }

            points.add(cd);
        }
        points.add(lastPoint);
        calculateMinMax();
    }

    public Point reorder(Point center) {
        int idx = Point.findNearest(center, points, false);
        if (isClosed()) {
            points.remove(points.size() - 1);
        }
        if (idx >= 0) {
            Collections.rotate(points, -idx);
        }
        points.add(new Point(points.get(0)));
        return getLast();
    }
}
