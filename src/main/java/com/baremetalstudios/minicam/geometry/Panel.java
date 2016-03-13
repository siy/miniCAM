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
import java.util.Iterator;
import java.util.List;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.processor.DrillProcessor;
import com.baremetalstudios.minicam.processor.OutputGenerator;
import com.baremetalstudios.minicam.processor.PolygonProcessor;

public class Panel implements Transformable {
    private List<Polygon> outlines;
    private List<DrillGroup> drills;

    public Panel(List<Polygon> outlines, List<DrillGroup> drills) {
        this.drills = drills;
        this.outlines = outlines;
        
        Point size = getPanelSize();
        Point minXY = getMinXY();
		size.translate(minXY.getX(), minXY.getY());
		
		if (checkDrillGroups(drills, size)) {
			System.out.println("WARNING: Weird drill file, trying to adjust coordinates. Verify output file manually!!!");
			
			for (DrillGroup drillGroup : drills) {
				drillGroup.scaleDown();
			}
		}
    }

	private boolean checkDrillGroups(List<DrillGroup> drills, Point size) {
		for (DrillGroup drillGroup : drills) {
			if (checkDrills(drillGroup, size)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkDrills(DrillGroup drillGroup, Point size) {
		for (Point drill : drillGroup.getDrills()) {
			if (drill.getX() > size.getX() * 8) {
				return true;
			}
			if (drill.getY() > size.getY() * 8) {
				return true;
			}
		}
		return false;
	}

    @Override
    public void translate(double dX, double dY) {
        transtale(outlines, dX, dY);
        transtale(drills, dX, dY);
    }

    @Override
    public void rotate90(Direction direction) {
        rotate90(outlines, direction);
        rotate90(drills, direction);
    }

    public DrillStatistics process(PolygonProcessor polygonProcessor,
                                   DrillProcessor drillProcessor) {
        outlines = polygonProcessor.process(outlines, drills, getCenter());
        replaceDrillsWithPolygons(drillProcessor);
        drills = drillProcessor.process(drills);
        DrillStatistics before = getDrillStats();
        drills = drillProcessor.optimize(drills);
        return before;
    }

    private void replaceDrillsWithPolygons(DrillProcessor drillProcessor) {
        OutputConfig config = drillProcessor.getConfig();

        if (!config.isReplaceDrillsWithPolygons()) {
            return;
        }

        List<DrillGroup> replaced = new ArrayList<>();

        for (Iterator<DrillGroup> iterator = drills.iterator(); iterator.hasNext();) {
            DrillGroup drillGroup = iterator.next();
            if (drillGroup.getDiameter() >= config.getDrillThreshold()) {
                replaced.add(drillGroup);
                iterator.remove();
            }
        }

        replaced = drillProcessor.process(replaced);
        replaced = drillProcessor.optimize(replaced);
        replaced.forEach(drillGroup -> replaceDrills(drillGroup, config));
    }

    private void replaceDrills(DrillGroup drillGroup, OutputConfig config) {
        List<Point> points = drillGroup.getDrills();
        for (Point point : points) {
            outlines.add(new Circle(point, drillGroup.getDiameter() - config.getCutterDiameter()));
        }
    }

    public void generate(OutputGenerator generator) {
        generator.generate(outlines, drills);
    }

    public void generateDrills(OutputGenerator generator) {
        generator.generate(null, drills);
    }

    public void generateMills(OutputGenerator generator) {
        generator.generate(outlines, null);
    }

    private void transtale(List<? extends Transformable> list, double dX,
                           double dY) {
        for (Transformable t : list) {
            t.translate(dX, dY);
        }
    }

    private void rotate90(List<? extends Transformable> list,
                          Direction direction) {
        for (Transformable t : list) {
            t.rotate90(direction);
        }
    }

    public PanelStatistics getStats() {
        return new PanelStatistics(getPanelSize(), getOutlineDimensions(),
                                   getDrillStats());
    }

    public DrillStatistics getDrillStats() {
        DrillStatistics result = new DrillStatistics();
        for (DrillGroup group : drills) {
            result.add(group.getDiameter(), group.getDrills().size(),
                       group.calculatePathLenght());
        }
        return result;
    }

    private List<Point> getOutlineDimensions() {
        List<Point> result = new ArrayList<Point>();

        for (Polygon polygon : outlines) {
            if (!polygon.isInner()) {
                result.add(new Point(polygon.getSizeX(), polygon.sizeY()));
            }
        }
        return result;
    }

    public Point getMinXY() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Polygon polygon : outlines) {
            minX = Math.min(polygon.getMinX(), minX);
            minY = Math.min(polygon.getMinY(), minY);
        }

        return new Point(minX, minY);
    }

    public Point getPanelSize() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Polygon polygon : outlines) {
            minX = Math.min(polygon.getMinX(), minX);
            minY = Math.min(polygon.getMinY(), minY);
            maxX = Math.max(polygon.getMaxX(), maxX);
            maxY = Math.max(polygon.getMaxY(), maxY);
        }

        return new Point(maxX - minX, maxY - minY);
    }

    public Point getCenter() {
        Point size = getPanelSize();
        Point bottomLeftPoint = getMinXY();
        return new Point(bottomLeftPoint.getX() + size.getX() / 2,
                         bottomLeftPoint.getY() + size.getY() / 2);
    }

    public TransformationStatus rotateAndCenter(OutputConfig config) {
        return new TransformationStatus(rotate(config), center(config));
    }

    private boolean center(OutputConfig config) {
        if (!config.isCenteringEnabled()) {
            return false;
        }

        Point blankSize = config.getCenteringDimensions();
        Point panelSize = getPanelSize();
        Point minXY = getMinXY();
        double dX = (blankSize.getX() - panelSize.getX()) / 2 - minXY.getX();
        double dY = (blankSize.getY() - panelSize.getY()) / 2 - minXY.getY();
        translate(dX, dY);

        return true;
    }

    private boolean rotate(OutputConfig config) {
        if (!config.isRotationEnabled()) {
            return false;
        }

        if (!isRotationRequired(config.getCenteringDimensions(), getPanelSize())) {
            return false;
        }

        Point minXY = getMinXY();
        rotate90(Direction.CW);
        Point newMinXY = getMinXY();
        translate(minXY.getX() - newMinXY.getX(),
                  minXY.getY() - newMinXY.getY());
        return true;
    }

    private boolean isRotationRequired(Point blankSize, Point panelSize) {
        boolean blankRotated = blankSize.getX() < blankSize.getY();
        boolean panelRotated = panelSize.getX() < panelSize.getY();
        return blankRotated == panelRotated ? false : true;
    }
}
