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
package com.baremetalstudios.minicam.processor;

import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.Circle;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;

public class OutputGenerator {
    private static final double DRILL_POINT_ANGLE = 130.0; // drill point angle
                                                           // assumed 130
                                                           // degrees
    private static final double DRILL_POINT_HALF_ANGLE_RAD = Math.toRadians(DRILL_POINT_ANGLE / 2);

    public static final double DRILL_ADJUST_SCALER = Math.cos(DRILL_POINT_HALF_ANGLE_RAD) /
                                                     Math.sin(DRILL_POINT_HALF_ANGLE_RAD);

    private PrintStream writer;
    private OutputConfig config;

    public OutputGenerator(PrintStream writer, OutputConfig config) {
        this.writer = writer;
        this.config = config;
    }

    public void generate(List<Polygon> polygons, List<DrillGroup> drills) {
        generatePreamble(polygons, drills);
        generatePath(polygons, drills);
        generatePostamble();
    }

    private void generatePath(List<Polygon> polygons, List<DrillGroup> drills) {
        if (polygons != null) {
            generatePolygons(polygons);

            if (config.isDoublePass()) {
                generatePolygons(polygons);
            }
        }

        if (drills != null) {
            drills.forEach(group -> generateSingleDrillGroup(group));
        }
    }

    private void generatePolygons(List<Polygon> polygons) {
        int i = 0;
        for (Polygon polygon : polygons) {
            if (polygon instanceof Circle || !polygon.isInner() || config.generateInnerCut()) {
                generateSinglePolygon(polygon, ++i);
            }
        }
    }

    private void generateSingleDrillGroup(DrillGroup group) {
        if (group.isEmpty()) {
            return;
        }
        toolChange(group.getOrdinal(), group.getDiameter(), "drill", group.getDrills().size());
        Iterator<Point> iterator = group.getDrills().iterator();
        Point point = iterator.next();
        writer.printf("G00 %s Z%.5f (rapid move to begin)%s", point(point), config.getZDrillSafe(), config.getSeparator());
        writer.printf("G81 R%.5f Z%.5f %s%s", config.getZDrillSafe(), calculateDrillZ(group), point(point), config.getSeparator());

        while (iterator.hasNext()) {
            point = iterator.next();
            writer.printf("%s%s", point(point), config.getSeparator());
        }
    }

    private double scaleX(double x) {
        return x * config.getXScale();
    }

    private double scaleY(double y) {
        return y * config.getYScale();
    }

    private String point(Point point) {
        return String.format("X%.5f Y%.5f", scaleX(point.getX()), scaleY(point.getY()));
    }

    private double calculateDrillZ(DrillGroup group) {
        double extraLength = config.isAdjustDrillDepth() ? group.getDiameter() / 2 * DRILL_ADJUST_SCALER : 0;

        return config.getZDrill() + extraLength;
    }

    private void toolChange(int ordinal, double metricDiameter, String toolType, int count) {
        toolChangeRetract();
        writer.printf("T%d%s", ordinal, config.getSeparator());
        writer.printf("M5%s", config.getSeparator());
        writer.printf("M6%s", config.getSeparator());
        writer.printf("(MSG, Change tool bit to %s size %.2f [%d])%s", toolType, metricDiameter, count, config.getSeparator());
        writer.printf("M0%s", config.getSeparator());
        writer.printf("S%d  ( RPM spindle speed.           )%s", config.getSpindleSpeed(), config.getSeparator());
        writer.printf("F%.5f%s", config.getFreeMoveRate(), config.getSeparator());
        writer.printf("M3      ( Spindle on clockwise.        )%s%s", config.getSeparator(), config.getSeparator());
        writer.printf("G04 P%.5f ( wait while spindle reach full speed )%s", (double) config.getSpindleDelay(), config.getSeparator());
    }

    private void generateSinglePolygon(Polygon polygon, int ordinal) {
        writer.printf("( Polygon  %d, %d points, %.3fmm x %.3fmm )%s", ordinal, polygon.count(), polygon.getSizeX(), polygon.getSizeY(), config.getSeparator());
        writer.printf("G00 %s Z%.5f ( rapid move to begin )%s", point(polygon.getFirst()), config.getZSafe(), config.getSeparator());

        plunge();

        boolean retracted = false;
        for (Point point : polygon.getPoints()) {
            writer.printf("%s%s", point(point), config.getSeparator());

            if (retracted && !point.isRetract()) {
                retracted = plunge();
            }
            if (point.isRetract()) {
                retracted = retract();
            }
        }
        retract();
    }

    private boolean retract() {
        writer.printf("G00 Z%.5f ( retract )%s", config.getZSafe(), config.getSeparator());
        return true;
    }

    private boolean plunge() {
        writer.printf("G01 Z%.5f F%.5f ( plunge )%s", config.getZCut(), config.getCutFeedRate(), config.getSeparator());
        return false;
    }

    private void toolChangeRetract() {
        writer.printf("G00 Z%.5f ( retract )%s", config.getToolChangeZ(), config.getSeparator());
    }

    private void generatePostamble() {
        writer.println();
        toolChangeRetract();
        writer.printf("M5 ( Spindle stop. )%s", config.getSeparator());
        writer.printf("M9 ( Coolant off. )%s", config.getSeparator());
        writer.printf("M2 ( Program end. )%s", config.getSeparator());
    }

    private void generatePreamble(List<Polygon> polygons, List<DrillGroup> drills) {
        // General
        writer.printf("G94     ( Millimeters per minute feed rate. )%s", config.getSeparator());
        writer.printf("G21     ( Units == Millimeters. )%s", config.getSeparator());
        writer.printf("G17     ( X-Y plane )%s", config.getSeparator());
        writer.printf("G90     ( Absolute coordinates.        )%s", config.getSeparator());
        writer.printf("G64 P%.5f ( Set maximum deviation from commanded toolpath )%s", Polygon.PRECISION, config.getSeparator());

        // Tool preparation
        // cutter
        int toolNumber = 0;

        if (polygons != null) {
            writer.printf("G10 L1 P%d R%.5f ( Define cutter tool )%s%s", ++toolNumber, config.getCutterDiameter() / 2, config.getSeparator(), config.getSeparator());
        }

        if (drills != null) {
            for (DrillGroup group : drills) {
                group.setOrdinal(++toolNumber);
                writer.printf("G10 L1 P%d R%.5f (Define drill tool)%s%s", group.getOrdinal(), group.getDiameter() / 2, config.getSeparator(), config.getSeparator());
            }
        }

        if (polygons != null) {
            toolChange(1, config.getCutterDiameter(), "mill", polygons.size());
        } else {
            DrillGroup group = drills.get(0);
            toolChange(group.getOrdinal(), group.getDiameter(), "drill", group.getDrills().size());
        }
    }

    public static double round(double value, double precision) {
        BigDecimal bd = new BigDecimal(value);
        int scale = (int) -Math.round(Math.log10(precision));
        bd = bd.setScale(scale, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
