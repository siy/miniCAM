package com.baremetalstudios.minicam.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.Direction;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;


public class PolygonProcessorTest {

    @Test
    public void positiveInsetIsHandledProperly() throws Exception {
        List<Point> points = Arrays.asList(new Point(1, 1),
                        new Point(1, 2),
                        new Point(2, 2),
                        new Point(2, 1),
                        new Point(1, 1));
        Polygon polygon = new Polygon(points);
        Polygon insetPolygon = PolygonProcessor.insetPolygon(polygon, 0.1);

        assertEquals(1.1, insetPolygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(1.1, insetPolygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(1.1, insetPolygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(1.9, insetPolygon.getPoints().get(1).getY(), 0.000001);
        assertEquals(1.9, insetPolygon.getPoints().get(2).getX(), 0.000001);
        assertEquals(1.9, insetPolygon.getPoints().get(2).getY(), 0.000001);
        assertEquals(1.9, insetPolygon.getPoints().get(3).getX(), 0.000001);
        assertEquals(1.1, insetPolygon.getPoints().get(3).getY(), 0.000001);
        assertEquals(1.1, insetPolygon.getPoints().get(4).getX(), 0.000001);
        assertEquals(1.1, insetPolygon.getPoints().get(4).getY(), 0.000001);

        assertEquals(0.8, insetPolygon.getSizeX(), 0.000001);
        assertEquals(0.8, insetPolygon.getSizeY(), 0.000001);
    }

    @Test
    public void negativeInsetIsHandledProperly() throws Exception {
        List<Point> points = Arrays.asList(new Point(1, 1),
                        new Point(1, 2),
                        new Point(2, 2),
                        new Point(2, 1),
                        new Point(1, 1));
        Polygon polygon = new Polygon(points);
        Polygon insetPolygon = PolygonProcessor.insetPolygon(polygon, -0.1);

        assertEquals(0.9, insetPolygon.getPoints().get(0).getX(), 0.000001);
        assertEquals(0.9, insetPolygon.getPoints().get(0).getY(), 0.000001);
        assertEquals(0.9, insetPolygon.getPoints().get(1).getX(), 0.000001);
        assertEquals(2.1, insetPolygon.getPoints().get(1).getY(), 0.000001);
        assertEquals(2.1, insetPolygon.getPoints().get(2).getX(), 0.000001);
        assertEquals(2.1, insetPolygon.getPoints().get(2).getY(), 0.000001);
        assertEquals(2.1, insetPolygon.getPoints().get(3).getX(), 0.000001);
        assertEquals(0.9, insetPolygon.getPoints().get(3).getY(), 0.000001);
        assertEquals(0.9, insetPolygon.getPoints().get(4).getX(), 0.000001);
        assertEquals(0.9, insetPolygon.getPoints().get(4).getY(), 0.000001);

        assertEquals(1.2, insetPolygon.getSizeX(), 0.000001);
        assertEquals(1.2, insetPolygon.getSizeY(), 0.000001);
    }

    @Test
    public void processorMarksPolygonDirectionCorrectly() throws Exception {
        List<Point> points = new ArrayList<Point>(Arrays.asList(new Point(1,1), new Point(1, 2),
                        new Point(2, 2), new Point(2, 1), new Point(1, 1)));
        Polygon p1 = new Polygon(points);

        PolygonProcessor processor = new PolygonProcessor(new OutputConfig());
        processor.process(Arrays.asList(p1), new ArrayList<DrillGroup>(), Point.at(0, 0));
        assertEquals(Direction.CW, p1.getDirection());
    }
}
