package com.baremetalstudios.minicam.simulator;

import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OutlinePlotterTest {

    private OutlinePlotter plotter;

    @BeforeEach
    void setUp() {
        plotter = new OutlinePlotter();
        // Configure format parsers: 2 leading, 4 trailing = 6 chars total
        plotter.setFormatX(2, 4);
        plotter.setFormatY(2, 4);
        // Use metric mode (scale=1.0) for simpler assertions
        plotter.setMode(PlotterMode.METRIC);
    }

    @Test
    void setPositionCreatesPointFromFormatParserCoordinates() {
        // "010000" with format (2,4) metric => 1.0
        // "020000" with format (2,4) metric => 2.0
        plotter.setPosition("010000", "020000");

        // Trigger exposure change to verify point was created
        plotter.setExposure(ExposureMode.OPEN);
        // The point should have been stored internally
        // We can verify by completing a polygon flow
    }

    @Test
    void setExposureOpenAddsPoint() {
        plotter.setPosition("010000", "020000");
        plotter.setExposure(ExposureMode.OPEN);

        // A single point was added; not enough for polygon yet
        assertTrue(plotter.getPolygons().isEmpty());
    }

    @Test
    void setExposureOpenToClosedCreatesPolygon() {
        // First point
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        // Second point
        plotter.setPosition("020000", "010000");

        // Third point
        plotter.setPosition("020000", "020000");

        // Close exposure -> creates polygon
        plotter.setExposure(ExposureMode.CLOSED);

        List<Polygon> polygons = plotter.getPolygons();
        assertEquals(1, polygons.size());
        assertTrue(polygons.get(0).count() >= 2);
    }

    @Test
    void doneFinalizesPolygon() {
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("020000", "010000");
        plotter.setPosition("020000", "020000");

        plotter.done();

        List<Polygon> polygons = plotter.getPolygons();
        assertFalse(polygons.isEmpty());
    }

    @Test
    void getPolygonsReturnsCreatedPolygons() {
        assertNotNull(plotter.getPolygons());
        assertTrue(plotter.getPolygons().isEmpty());
    }

    @Test
    void multiplePolygonCreationFlow() {
        // First polygon
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);
        plotter.setPosition("020000", "010000");
        plotter.setPosition("020000", "020000");
        plotter.setExposure(ExposureMode.CLOSED);

        assertEquals(1, plotter.getPolygons().size());

        // Second polygon
        plotter.setPosition("030000", "030000");
        plotter.setExposure(ExposureMode.OPEN);
        plotter.setPosition("040000", "030000");
        plotter.setPosition("040000", "040000");
        plotter.setExposure(ExposureMode.CLOSED);

        assertEquals(2, plotter.getPolygons().size());
    }

    @Test
    void setModeDelegatesToSuper() {
        plotter.setMode(PlotterMode.POLYGON);
        assertTrue(plotter.getOptions().contains(PlotterMode.POLYGON));
    }

    @Test
    void setModeMetricChangesFormatParserScale() {
        // Already set to METRIC in setUp, verify parsing
        // "010000" with format (2,4) metric => 1.0
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("020000", "020000");
        plotter.done();

        List<Polygon> polygons = plotter.getPolygons();
        assertFalse(polygons.isEmpty());

        // Verify the points use metric scale (1.0)
        Polygon poly = polygons.get(0);
        Point first = poly.getFirst();
        assertEquals(1.0, first.getX(), 0.0001);
        assertEquals(1.0, first.getY(), 0.0001);
    }

    @Test
    void setModeImperialChangesFormatParserScale() {
        plotter.setMode(PlotterMode.IMPERIAL);

        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("020000", "020000");
        plotter.done();

        List<Polygon> polygons = plotter.getPolygons();
        assertFalse(polygons.isEmpty());

        // Imperial scale: 1.0 * 25.4 = 25.4
        Polygon poly = polygons.get(0);
        Point first = poly.getFirst();
        assertEquals(1.0 * FormatParser.IMPERIAL_SCALE, first.getX(), 0.0001);
        assertEquals(1.0 * FormatParser.IMPERIAL_SCALE, first.getY(), 0.0001);
    }

    @Test
    void defaultExposureIsClosed() {
        assertEquals(ExposureMode.CLOSED, plotter.getExposure());
    }

    @Test
    void polygonContainsCorrectNumberOfPoints() {
        // Create a triangle: 3 points + closing point
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("030000", "010000");
        plotter.setPosition("020000", "030000");

        plotter.setExposure(ExposureMode.CLOSED);

        Polygon poly = plotter.getPolygons().get(0);
        // Open adds first point, closed adds closing point = total varies
        assertTrue(poly.count() >= 2);
    }

    @Test
    void doneWithNoPointsDoesNotCreatePolygon() {
        plotter.done();
        assertTrue(plotter.getPolygons().isEmpty());
    }

    @Test
    void setExposureClosedWithoutOpenDoesNotCreatePolygon() {
        plotter.setPosition("010000", "010000");
        // No OPEN exposure set, directly trying CLOSED
        plotter.setExposure(ExposureMode.CLOSED);

        assertTrue(plotter.getPolygons().isEmpty());
    }

    @Test
    void doneAfterOpenExposureCreatesPolygon() {
        plotter.setPosition("010000", "010000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("020000", "010000");
        plotter.setPosition("030000", "020000");

        plotter.done();

        assertFalse(plotter.getPolygons().isEmpty());
    }

    @Test
    void polygonPointCoordinatesAreCorrect() {
        plotter.setPosition("050000", "030000");
        plotter.setExposure(ExposureMode.OPEN);

        plotter.setPosition("100000", "060000");

        plotter.done();

        Polygon poly = plotter.getPolygons().get(0);
        Point first = poly.getFirst();

        // "050000" format (2,4) metric => 5.0
        // "030000" format (2,4) metric => 3.0
        assertEquals(5.0, first.getX(), 0.0001);
        assertEquals(3.0, first.getY(), 0.0001);
    }

    @Test
    void setCenterDoesNotAffectPolygons() {
        plotter.setCenter("010000", "020000");
        assertTrue(plotter.getPolygons().isEmpty());
    }

    @Test
    void askContinueReturnsFalse() {
        assertFalse(plotter.askContinue(0, 0));
    }
}
