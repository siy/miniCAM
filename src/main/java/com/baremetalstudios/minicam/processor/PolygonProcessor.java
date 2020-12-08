/*******************************************************************************
 * Copyright (c) 2014, 2015, 2020 Sergiy Yevtushenko
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
package com.baremetalstudios.minicam.processor;

import java.util.ArrayList;
import java.util.List;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.Direction;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;

/*
 * Polygon inset/line intersection code is a Java translation of public-domain code by Darel Rex Finley, 2006,2007
 * See diagrams at http://alienryderflex.com/polygon_inset/
 * and http://alienryderflex.com/intersect/
 */
public class PolygonProcessor {
    private static final double MIN_OFFSET = 0.005;
    private OutputConfig config;

    public PolygonProcessor(OutputConfig config) {
        this.config = config;
    }

    public List<Polygon> process(List<Polygon> polygonList, List<DrillGroup> drillList, Point center) {
        List<Polygon> polygons = mergeOpenPolygons(polygonList);
        polygons = optimizePolygons(polygons);
        markInnerPolygon(polygons);
        markPolygonDirection(polygons);
        polygons.forEach(polygon -> polygon.reorder(center));

        polygons = insetPolygons(polygons);
        if (!generateTabs(polygons, drillList)) {
            System.out.println("WARNING: no tabs were generated! Perhaps tab distance is too big for this board.");
        }
        return polygons;
    }

    private List<Polygon> optimizePolygons(List<Polygon> polygons) {
        polygons.forEach(polygon -> polygon.optimize());
        return polygons;
    }

    private List<Polygon> insetPolygons(List<Polygon> polygons) {
        double outherCutterRadius = (config.getCutterDiameter() + config.getTabDrillDiameter()) / 2;
        double innerCutterRadius  = config.getCutterDiameter() / 2;
        List<Polygon> result = new ArrayList<>();

        for (Polygon polygon : polygons) {
            double insetDistance = polygon.isInner() ? -innerCutterRadius : -outherCutterRadius;

            if (polygon.getDirection() == Direction.CCW) {
                insetDistance = -insetDistance;
            }

            if (polygon.isInner()) {
                insetDistance = -insetDistance;
            }
            result.add(insetPolygon(polygon, insetDistance));
        }
        return result;
    }

    private boolean generateTabs(List<Polygon> polygons, List<DrillGroup> drills) {
        DrillGroup group = new DrillGroup("MouseBytes", config.getTabDrillDiameter());
        int numDrills = (int) Math.round(config.getTabWidth() / (config.getTabDrillDiameter() * 2));

        for (Polygon polygon : polygons) {
            generateTabsForPolygon(group, numDrills, polygon);
        }
        if (group.isEmpty()) {
            return false;
        }
        drills.add(group);
        return true;
    }

    private void generateTabsForPolygon(DrillGroup group, int numDrills, Polygon polygon) {
        Point startPoint = polygon.getFirst();
        Point polygonCenter = new Point(polygon.getMinX() + polygon.sizeX() / 2, polygon.getMinY() + polygon.getSizeY() / 2);

        for (int i = 1; i < polygon.getPoints().size(); i++) {
            Point endPoint = polygon.getPoints().get(i);
            double length = Point.distance(startPoint, endPoint);
            int numSplits = (int) Math.round(length / config.getMinTabDistance());

            if (numSplits < 2 || hasSlope(startPoint, endPoint)) {
                startPoint = endPoint;
                continue;
            }

            boolean swap = (startPoint.getX() - endPoint.getX()) > 0.1 || (startPoint.getY() - endPoint.getY()) > 0.1;
            double offset = length / numSplits;
            double halfTab = (config.getTabWidth() + config.getCutterDiameter()) / 2.0;

            if (swap) {
                offset = -offset;
                halfTab = -halfTab;
            }

            boolean horizontal = coordinateDifference(startPoint.getX(), endPoint.getX()) < MIN_OFFSET;

            double drillShift = -(config.getCutterDiameter() / 2 - config.getTabDrillDiameter() / 2);

            if (negativeShiftIsCloser(startPoint, polygonCenter, drillShift, horizontal)) {
                drillShift = -drillShift;
            }

            double x1 = startPoint.getX();
            double y1 = startPoint.getY();
            for (int j = 1; j < numSplits; j++) {
                Point point1;
                Point point2;

                if (horizontal) {
                    y1 += offset;

                    point1 = new Point(x1, y1 - halfTab);
                    point2 = new Point(x1, y1 + halfTab);

                    addHorizontalDrills(group, x1, y1, drillShift, numDrills);
                } else {
                    x1 += offset;

                    point1 = new Point(x1 - halfTab, y1);
                    point2 = new Point(x1 + halfTab, y1);

                    addVerticalDrills(group, x1, y1, drillShift, numDrills);
                }

                point1.setRetract(true);
                polygon.insert(point2, i);
                polygon.insert(point1, i);

                i += 2;
            }
            startPoint = endPoint;
        }
    }

    private void addVerticalDrills(DrillGroup group, double x1, double y1, double drillShift, int numDrills) {
        double drillOffset = config.getTabDrillDiameter() * 2;
        for (int i = -numDrills / 2; i <= numDrills / 2; i++) {
            group.addDrill(new Point(x1 + drillOffset * i, y1 + drillShift));
        }
    }

    private void addHorizontalDrills(DrillGroup group, double x1, double y1, double drillShift, int numDrills) {
        double drillOffset = config.getTabDrillDiameter() * 2;
        for (int i = -numDrills / 2; i <= numDrills / 2; i++) {
            group.addDrill(new Point((x1 + drillShift), (y1 + drillOffset * i)));
        }
    }

    private boolean negativeShiftIsCloser(Point startPoint, Point insidePolygon, double drillShift, boolean horizontal) {
        double startX = startPoint.getX();
        double startY = startPoint.getY();
        Point point1;
        Point point2;
        if (horizontal) {
            point1 = new Point(startX + drillShift, startY);
            point2 = new Point(startX - drillShift, startY);
        } else {
            point1 = new Point(startX, startY + drillShift);
            point2 = new Point(startX, startY - drillShift);
        }

        return Point.distance(point1, insidePolygon) > Point.distance(point2, insidePolygon);
    }

    private boolean hasSlope(Point startPoint, Point endPoint) {
        double diffX = coordinateDifference(startPoint.getX(), endPoint.getX());
        double diffY = coordinateDifference(startPoint.getY(), endPoint.getY());
        return diffX > MIN_OFFSET && diffY > MIN_OFFSET;
    }

    private double coordinateDifference(double startX, double endX) {
        return Math.abs(startX - endX);
    }

    private void markPolygonDirection(List<Polygon> polygons) {
        for (Polygon polygon : polygons) {
            double sum = 0;
            List<Point> points = polygon.getPoints();
            for (int i = 0; i < points.size(); i++) {
                Point p2 = points.get((i + 1) % points.size());
                Point p1 = points.get(i);
                sum += (p2.getX() - p1.getX()) * (p2.getY() + p1.getY());
            }

            polygon.setDirection((sum > 0) ? Direction.CW : Direction.CCW);
        }
    }

    private void markInnerPolygon(List<Polygon> polygons) {
        for (Polygon polygon : polygons) {
            if (polygon.isInner()) {
                continue;
            }

            if (foundContaining(polygons, polygon)) {
                polygon.setInner(true);
            }
        }
    }

    private boolean foundContaining(List<Polygon> polygons, Polygon testedPolygon) {
        for (Polygon polygon : polygons) {
            if (polygon == testedPolygon) {
                continue;
            }
            if (polygon.getMinX() <= testedPolygon.getMinX() && polygon.getMaxX() >= testedPolygon.getMaxX()
                            && polygon.getMinY() <= testedPolygon.getMinY() && polygon.getMaxY() >= testedPolygon.getMaxY()) {
                return true;
            }
        }
        return false;
    }

    private List<Polygon> mergeOpenPolygons(List<Polygon> polygons) {
        List<Polygon> openPolygons = new ArrayList<>();
        List<Polygon> closedPolygons = new ArrayList<>();
        splitPolygons(polygons, openPolygons, closedPolygons);

        List<Polygon> result = new ArrayList<>();
        result.addAll(closedPolygons);
        result.addAll(mergePolygons(openPolygons));

        return result;
    }

    private List<Polygon> mergePolygons(List<Polygon> openPolygons) {
        List<Polygon> result = new ArrayList<>();

        while (!openPolygons.isEmpty()) {
            Polygon polygon = openPolygons.iterator().next();

            int idx = findPrecedingPolygon(polygon, openPolygons);
            if (idx != -1) {
                Polygon preceding = openPolygons.remove(idx);
                openPolygons.remove(0);
                Polygon merged = Polygon.merge(preceding, polygon);
                if (merged.isClosed()) {
                    result.add(merged);
                } else {
                    openPolygons.add(merged);
                }
            } else {
                openPolygons.remove(0);
                result.add(polygon);
            }
        }

        return result;
    }

    private int findPrecedingPolygon(Polygon polygon, List<Polygon> openPolygons) {
        for (int i = 0; i < openPolygons.size(); i++) {
            double distance = Point.distance(openPolygons.get(i).getLast(), polygon.getFirst());
            if (distance < Polygon.PRECISION) {
                return i;
            }
        }

        return -1;
    }

    private void splitPolygons(List<Polygon> polygons, List<Polygon> openPolygons, List<Polygon> closedPolygons) {
        for (Polygon polygon : polygons) {
            if (polygon.isClosed()) {
                closedPolygons.add(polygon);
            } else {
                openPolygons.add(polygon);
            }
        }
    }

    public void dump(List<Polygon> polygons) {
        int count = 0;
        for (Polygon polygon : polygons) {
            System.out.println("polygon " + (++count) + " " + polygon);
        }
    }

    static Point lineIntersection(Point a, Point b, Point c, Point d) {
        double distAB = Point.distance(a, b);

        b = new Point(b.getX() - a.getX(), b.getY() - a.getY());
        c = new Point(c.getX() - a.getX(), c.getY() - a.getY());
        d = new Point(d.getX() - a.getX(), d.getY() - a.getY());

        double theCos = b.getX() / distAB;
        double theSin = b.getY() / distAB;
        c = new Point(c.getX() * theCos + c.getY() * theSin, c.getY() * theCos - c.getX() * theSin);
        d = new Point(d.getX() * theCos + d.getY() * theSin, d.getY() * theCos - d.getX() * theSin);

        if (c.getY() == d.getY()) {
            return null;
        }

        double intersectionDist = d.getX() + (c.getX() - d.getX()) * d.getY() / (d.getY() - c.getY());
        double x = a.getX() + intersectionDist * theCos;
        double y = a.getY() + intersectionDist * theSin;

        return new Point(x, y);
    }

    static Point insetCorner(Point ab, Point cd, Point ef, double insetDistance) {
        double dx1 = cd.getX() - ab.getX();
        double dy1 = cd.getY() - ab.getY();

        double dx2 = ef.getX() - cd.getX();
        double dy2 = ef.getY() - cd.getY();

        double dist1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
        double dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

        if (dist1 == 0 || dist2 == 0) {
            return null;
        }

        double insetX;
        double insetY;

        insetX = dy1 / dist1 * insetDistance;
        insetY = -dx1 / dist1 * insetDistance;
        Point nab = new Point(ab.getX() + insetX, ab.getY() + insetY);
        Point ncd1 = new Point(cd.getX() + insetX, cd.getY() + insetY);
        insetX = dy2 / dist2 * insetDistance;
        insetY = -dx2 / dist2 * insetDistance;
        Point nef = new Point(ef.getX() + insetX, ef.getY() + insetY);
        Point ncd2 = new Point(cd.getX() + insetX, cd.getY() + insetY);

        return lineIntersection(nab, ncd1, nef, ncd2);
    }

    public static Polygon insetPolygon(Polygon polygon, double insetDistance) {
        List<Point> points = new ArrayList<>();

        if (polygon.count() < 3 || !polygon.isClosed()) {
            return null;
        }

        int size = polygon.count() - 1;
        List<Point> inPoints = polygon.getPoints().subList(0, size);

        Point cd = inPoints.get(size - 1);
        Point ef = inPoints.get(0);
        for (int i = 0; i < size; i++) {
            Point ab = cd;
            cd = ef;
            ef = inPoints.get((i + 1) % size);
            Point corner = insetCorner(ab, cd, ef, insetDistance);
            if (corner == null) {
                System.out.println("Unable to calculate inset: ab=" + ab + ", cd=" + cd + ", ef=" + ef + ", distance=" + insetDistance);
            }
            points.add(corner);
        }
        points.add(new Point(points.get(0)));

        Polygon result = new Polygon(points);
        result.setDirection(polygon.getDirection());
        result.setInner(polygon.isInner());
        return result;
    }

    public OutputConfig getConfig() {
        return config;
    }
}
