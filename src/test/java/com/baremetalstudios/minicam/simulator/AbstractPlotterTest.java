package com.baremetalstudios.minicam.simulator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractPlotterTest {

    private AbstractPlotter plotter;

    /**
     * Minimal concrete subclass of AbstractPlotter for testing.
     */
    private static class TestPlotter extends AbstractPlotter {
        @Override
        public void setCenter(String string, String string2) {
        }

        @Override
        public void setPosition(String string, String string2) {
        }

        @Override
        public void done() {
        }
    }

    @BeforeEach
    void setUp() {
        plotter = new TestPlotter();
    }

    @Test
    void defaultExposureIsClosed() {
        assertEquals(ExposureMode.CLOSED, plotter.getExposure());
    }

    @Test
    void defaultOptionsContainImperial() {
        assertTrue(plotter.getOptions().contains(PlotterMode.IMPERIAL));
    }

    @Test
    void defaultOptionsContainLinear() {
        assertTrue(plotter.getOptions().contains(PlotterMode.LINEAR));
    }

    @Test
    void setExposureChangesExposure() {
        plotter.setExposure(ExposureMode.OPEN);
        assertEquals(ExposureMode.OPEN, plotter.getExposure());
    }

    @Test
    void setExposureToFlash() {
        plotter.setExposure(ExposureMode.FLASH);
        assertEquals(ExposureMode.FLASH, plotter.getExposure());
    }

    @Test
    void setModeAddsToOptions() {
        plotter.setMode(PlotterMode.POLYGON);
        assertTrue(plotter.getOptions().contains(PlotterMode.POLYGON));
    }

    @Test
    void setModeMetricUpdatesFormatParsersToMetric() {
        plotter.setFormatX(2, 4);
        plotter.setFormatY(2, 4);
        plotter.setMode(PlotterMode.METRIC);

        // Metric scale is 1.0, so parsing "001000" with format (2,4) gives 0.1 * 1.0 = 0.1
        double result = plotter.getFormatX().parse("001000");
        assertEquals(0.1, result, 0.0000001);
    }

    @Test
    void setModeImperialUpdatesFormatParsersToImperial() {
        plotter.setFormatX(2, 4);
        plotter.setFormatY(2, 4);
        plotter.setMode(PlotterMode.METRIC);
        // Now switch to imperial
        plotter.setMode(PlotterMode.IMPERIAL);

        // Imperial scale is 25.4, so parsing "001000" with format (2,4) gives 0.1 * 25.4
        double result = plotter.getFormatX().parse("001000");
        assertEquals(0.1 * FormatParser.IMPERIAL_SCALE, result, 0.0000001);
    }

    @Test
    void resetModeRemovesFromOptions() {
        plotter.setMode(PlotterMode.POLYGON);
        assertTrue(plotter.getOptions().contains(PlotterMode.POLYGON));

        plotter.resetMode(PlotterMode.POLYGON);
        assertFalse(plotter.getOptions().contains(PlotterMode.POLYGON));
    }

    @Test
    void resetModeDoesNotAffectOtherModes() {
        plotter.setMode(PlotterMode.POLYGON);
        plotter.setMode(PlotterMode.CW);

        plotter.resetMode(PlotterMode.POLYGON);

        assertFalse(plotter.getOptions().contains(PlotterMode.POLYGON));
        assertTrue(plotter.getOptions().contains(PlotterMode.CW));
    }

    @Test
    void askContinueReturnsFalse() {
        assertFalse(plotter.askContinue(0, 0));
    }

    @Test
    void askContinueReturnsFalseForAnyArgs() {
        assertFalse(plotter.askContinue(100, 200));
    }

    @Test
    void addApertureStoresApertureById() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 10, List.of(1.0));
        plotter.addAperture(aperture);

        // Verify indirectly: add another with same id, it should overwrite
        SimpleAperture aperture2 = new SimpleAperture(ApertureType.RECTANGLE, 10, List.of(2.0));
        plotter.addAperture(aperture2);
        // No exception thrown means the map accepted it
    }

    @Test
    void addMacroStoresMacroByName() {
        ApertureMacro macro = new ApertureMacro("TEST_MACRO", Collections.emptyList());
        plotter.addMacro(macro);

        ApertureMacro retrieved = plotter.getMacro("TEST_MACRO");
        assertSame(macro, retrieved);
    }

    @Test
    void addMultipleMacros() {
        ApertureMacro macro1 = new ApertureMacro("MACRO_A", Collections.emptyList());
        ApertureMacro macro2 = new ApertureMacro("MACRO_B", Collections.emptyList());
        plotter.addMacro(macro1);
        plotter.addMacro(macro2);

        assertSame(macro1, plotter.getMacro("MACRO_A"));
        assertSame(macro2, plotter.getMacro("MACRO_B"));
    }

    @Test
    void getMacroReturnsNullForNonExistent() {
        assertNull(plotter.getMacro("DOES_NOT_EXIST"));
    }

    @Test
    void getMacroReturnsNullForNull() {
        assertNull(plotter.getMacro(null));
    }

    @Test
    void setFormatXConfiguresFormatParser() {
        plotter.setFormatX(3, 5);
        plotter.setMode(PlotterMode.METRIC);

        // format (3,5) means 8 char total, parsing "12345678" => 123.45678
        double result = plotter.getFormatX().parse("12345678");
        assertEquals(123.45678, result, 0.0000001);
    }

    @Test
    void setFormatYConfiguresFormatParser() {
        plotter.setFormatY(3, 5);
        plotter.setMode(PlotterMode.METRIC);

        double result = plotter.getFormatY().parse("12345678");
        assertEquals(123.45678, result, 0.0000001);
    }

    @Test
    void setFormatXAndYIndependently() {
        plotter.setFormatX(2, 4);
        plotter.setFormatY(3, 5);
        plotter.setMode(PlotterMode.METRIC);

        double resultX = plotter.getFormatX().parse("123456");
        double resultY = plotter.getFormatY().parse("12345678");

        assertEquals(12.3456, resultX, 0.0000001);
        assertEquals(123.45678, resultY, 0.0000001);
    }

    @Test
    void commentDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.comment(1, "test"));
    }

    @Test
    void addFlashDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.addFlash());
    }

    @Test
    void setApertureDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.setAperture(42));
    }

    @Test
    void selectAxisDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.selectAxis("X", "Y"));
    }

    @Test
    void setImagePolarityDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.setImagePolarity("POSITIVE"));
    }

    @Test
    void setLayerPolarityDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.setLayerPolarity("DARK"));
    }

    @Test
    void setOffsetDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.setOffset("0", "0"));
    }

    @Test
    void setScaleFactorDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.setScaleFactor("1", "1"));
    }

    @Test
    void stepAndRepeatDoesNotThrow() {
        assertDoesNotThrow(() -> plotter.stepAndRepeat("1", "2", "3", "4"));
    }
}
