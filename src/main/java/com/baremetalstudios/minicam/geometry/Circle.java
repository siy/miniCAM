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
