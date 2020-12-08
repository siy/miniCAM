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
package com.baremetalstudios.minicam.simulator;

import java.util.ArrayList;
import java.util.List;

import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OutlinePlotter extends AbstractPlotter {
    protected final Logger logger = LogManager.getLogger();
    
    private List<Point> points = new ArrayList<>();
    private List<Polygon> polygons = new ArrayList<>();
    private Point point;
    
    public OutlinePlotter() {
    }
    
    @Override
    public void setMode(PlotterMode mode) {
        super.setMode(mode);
    }

    @Override
    public void setCenter(String stringX, String stringY) {
        if (logger.isDebugEnabled()) {
            logger.debug("setCenter(" + getFormatX().parse(stringX) + ", " + getFormatY().parse(stringY) + ")");
        }
    }

    @Override
    public void setPosition(String stringX, String stringY) {
        point = new Point(getFormatX().parse(stringX), getFormatY().parse(stringY));
        if (logger.isDebugEnabled()) {
            logger.debug("setPosition(" + getFormatX().parse(stringX) + ", " + getFormatY().parse(stringY) + ")");
        }
    }

    @Override
    public void setExposure(ExposureMode mode) {
        if (getExposure() == ExposureMode.OPEN && mode == ExposureMode.CLOSED) {
            points.add(new Point(point));
            flush();
        }
        if (mode == ExposureMode.OPEN) {
            points.add(new Point(point));
        }
        super.setExposure(mode);
    }
    
    @Override
    public void done() {
        if (point != null) {
            points.add(new Point(point));
        }
        flush();
    }
    
    public List<Polygon> getPolygons() {
        return polygons;
    }

    private void flush() {
        if (points.size() > 1) {
            polygons.add(new Polygon(points));
            points.clear();
        } else if(!points.isEmpty()) {
            Polygon lastPolygon = polygons.get(polygons.size() - 1);
            double distance = Point.distance(lastPolygon.getLast(), points.get(0));
            //System.out.println("trying to extend polygon with " + points.get(0) + " with distance " + distance);
            if (distance <= Polygon.PRECISION) {
                //System.out.println("Polygon extended with " + points);
                lastPolygon.extend(points);
            }
        }
    }
}
