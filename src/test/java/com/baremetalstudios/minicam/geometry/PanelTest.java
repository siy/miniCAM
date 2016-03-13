package com.baremetalstudios.minicam.geometry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import com.baremetalstudios.minicam.config.OutputConfig;


public class PanelTest {
    private String expectedStats = "Panel dimension 2.000mm x 1.000mm\n" +
                    "Total: 2 boards on the panel\n" +
                    "\n" +
                    "Drill 0.50 (0.117mm extra depth),    2 drills, path lenght 2.83\n" +
                    "Drill 0.90 (0.210mm extra depth),    2 drills, path lenght 2.83\n" +
                    "Total: 4 drills, 5.66mm tool path\n";

    @Test
    public void testRotateAndCenter() throws Exception {
        Panel panel = createPanel();
        OutputConfig config = createConfig();

        TransformationStatus result = panel.rotateAndCenter(config);
        assertTrue(result.isCentered());
        assertTrue(result.isRotated());
    }

    @Test
    public void panelIsNotRotatedIfRotationIsDisabled() throws Exception {
        Panel panel = createPanel();
        OutputConfig config = createConfig();

        config.setRotate(false);
        TransformationStatus result = panel.rotateAndCenter(config);
        assertTrue(result.isCentered());
        assertFalse(result.isRotated());
    }

    @Test
    public void panelIsNotRotatedIfPanelDirectionMathcesBoard() throws Exception {
        Panel panel = createPanel();
        OutputConfig config = createConfig();

        config.setBoardWidth(100);
        config.setBoardHeight(110);
        TransformationStatus result = panel.rotateAndCenter(config);
        assertTrue(result.isCentered());
        assertFalse(result.isRotated());
    }

    @Test
    public void panelIsNotCenteredIfCenteringIsDisabled() throws Exception {
        Panel panel = createPanel();
        OutputConfig config = createConfig();

        config.setCenter(false);
        TransformationStatus result = panel.rotateAndCenter(config);
        assertFalse(result.isCentered());
        assertTrue(result.isRotated());
    }

    @Test
    public void panelStatisticIsObtained() throws Exception {
        Panel panel = createPanel();
        PanelStatistics stats = panel.getStats();

        assertEquals(expectedStats , stats.toString());
    }

    private Panel createPanel() {
        Polygon p1 = new Polygon(Arrays.asList(new Point(0,0), new Point(0,1), new Point(1,1), new Point(1,0), new Point(0,0)));
        Polygon p2 = new Polygon(Arrays.asList(new Point(2,0), new Point(2,1), new Point(2,1), new Point(2,0), new Point(2,0)));
        DrillGroup d1 = new DrillGroup("T01", 0.5, Arrays.asList(new Point(0.5, 0.5), new Point(2.5, 2.5)));
        DrillGroup d2 = new DrillGroup("T02", 0.9, Arrays.asList(new Point(0.8, 0.8), new Point(2.8, 2.8)));
        return new Panel(Arrays.asList(p1, p2), Arrays.asList(d1, d2));
    }

    private OutputConfig createConfig() {
        OutputConfig config = new OutputConfig();
        config.setBoardWidth(110);
        config.setBoardHeight(100);
        return config;
    }
}

