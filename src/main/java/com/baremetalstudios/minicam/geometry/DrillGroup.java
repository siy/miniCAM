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
package com.baremetalstudios.minicam.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.baremetalstudios.minicam.config.OutputConfig;

public class DrillGroup implements Transformable {
    private final List<Point> drills = new ArrayList<Point>();
    private final String id;
    private double diameter;
    private int ordinal;
    private Random random = new Random(0);

    public DrillGroup(String id, double diameter, List<Point> drills) {
        this(id, diameter);
        this.drills.addAll(drills);
    }

    public DrillGroup(String id, double diameter) {
        this.id = id;
        this.diameter = diameter;
    }

    @Override
    public void translate(double dX, double dY) {
        drills.forEach(point -> point.translate(dX, dY));
    }

    @Override
    public void rotate90(Direction direction) {
        drills.forEach(point -> point.rotate90(direction));
    }

    public double getDiameter() {
        return diameter;
    }

    public List<Point> getDrills() {
        return drills;
    }

    public void addDrill(Point point) {
        drills.add(point);
    }

    public void optimize(OutputConfig config) {
        sortDrills();
        optimizeStep1(config);
        optimizeStep2(config);
    }

    private void optimizeStep2(OutputConfig config) {
        List<Point> generation = new ArrayList<>(drills);

        double pathLength = calculatePathLenght(generation);

        int scale = 16 * config.getOptimizationLevel();

        for (int i = 0; i < (generation.size() * scale); i++) {
            List<Point> temp2 = new ArrayList<>(generation);

            int p1 = random.nextInt(temp2.size());
            int p2 = random.nextInt(temp2.size());

            Point save1 = temp2.get(p1);
            Point save2 = temp2.get(p2);
            temp2.set(p1, save2);
            temp2.set(p2, save1);

            double tmpLen = calculatePathLenght(temp2);

            if (tmpLen < pathLength) {
                generation = temp2;
                pathLength = tmpLen;
            }
        }

        drills.clear();
        drills.addAll(generation);
    }

    private void optimizeStep1(OutputConfig config) {
        double pathLength = calculatePathLenght();
        List<Point> generation = new ArrayList<>(drills);
        int totalAttempts =
                            (int) Math.round(generation.size() *
                                             2 *
                                             (config.getOptimizationLevel() / 18.0));
        for (int i = 0; i < totalAttempts; i++) {
            List<Point> newDrills = oneTry(new ArrayList<>(generation));

            double tmpLen = calculatePathLenght(newDrills);

            if (tmpLen < pathLength) {
                generation = newDrills;
                pathLength = tmpLen;
            }
        }

        drills.clear();
        drills.addAll(generation);
    }

    private List<Point> oneTry(List<Point> list) {
        List<Point> generation = new ArrayList<>();

        Point point = list.remove(random.nextInt(list.size()));
        while (!list.isEmpty()) {
            generation.add(point);
            point = list.remove(Point.findNearest(point, list, false));
        }
        generation.add(point);

        return generation;
    }

    private void sortDrills() {
        drills.sort((o1, o2) -> {
            double d = o1.getX() - o2.getX();
            d = (d == 0) ? (o1.getY() - o2.getY()) : d;
            return (int) Math.signum(d);
        });
    }

    public String getId() {
        return id;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setDiameter(double diameter) {
        this.diameter = diameter;
    }

    public double calculatePathLenght() {
        return calculatePathLenght(drills);
    }

    private double calculatePathLenght(List<Point> list) {
        Point prev = null;
        double result = 0;

        for (Point point : list) {
            if (prev != null) {
                result += Point.distance(prev, point);
            }
            prev = point;
        }
        return result;
    }

    public Point reorder(Point start) {
        int idx = Point.findNearest(start, drills, false);
        if (idx > 0) {
            Collections.rotate(drills, -idx);
        }
        return drills.get(drills.size() - 1);
    }

    public boolean isEmpty() {
        return drills.isEmpty();
    }

	public void scaleDown() {
		for (Point point : drills) {
			point.scaleDown();
		}
	}
}
