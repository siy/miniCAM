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

import java.util.List;


public class Point implements Transformable {
    private double x;
    private double y;
    private boolean retract;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(Point point) {
        this.x = point.x;
        this.y = point.y;
    }

    public static Point at(double x, double y) {
        return new Point(x, y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public void translate(double dX, double dY) {
        x += dX;
        y += dY;
    }

    @Override
    public void rotate90(Direction direction) {
        if (Direction.CCW == direction) {
            double saveX = x;
            x = -y;
            y = saveX;
        } else {
            double saveY = y;
            y = -x;
            x = saveY;
        }
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", x, y);
    }

    public static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public boolean isRetract() {
        return retract;
    }

    public void setRetract(boolean retract) {
        this.retract = retract;
    }

    public static int findNearest(Point base, List<Point> list, boolean skipRetract) {
        int idx = -1;
        double candidateDistance = Double.MAX_VALUE;

        int i = 0;
        for (Point point : list) {
            double distance = distance(point, base);
            if (distance < candidateDistance) {
                idx = i;
                candidateDistance = distance;
            }
            i++;
        }

        return idx;
    }

	public void scaleDown() {
		x /= 10;
		y /= 10;
	}
}
