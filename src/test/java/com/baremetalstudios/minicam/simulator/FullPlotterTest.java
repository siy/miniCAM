package com.baremetalstudios.minicam.simulator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FullPlotterTest {

    private FullPlotter plotter;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        plotter = new FullPlotter();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String capturedOutput() {
        System.out.flush();
        return outputStream.toString();
    }

    // --- Methods that just print, no super call ---

    @Test
    void setCenterPrintsFormattedOutput() {
        plotter.setCenter("10", "20");
        assertEquals("setCenter(10, 20)\n", capturedOutput());
    }

    @Test
    void setCenterWithNullArgs() {
        plotter.setCenter(null, null);
        assertEquals("setCenter(null, null)\n", capturedOutput());
    }

    @Test
    void setPositionPrintsFormattedOutput() {
        plotter.setPosition("100", "200");
        assertEquals("setPosition(100, 200)\n", capturedOutput());
    }

    @Test
    void setPositionWithEmptyStrings() {
        plotter.setPosition("", "");
        assertEquals("setPosition(, )\n", capturedOutput());
    }

    @Test
    void donePrintsFormattedOutput() {
        plotter.done();
        assertEquals("done()\n", capturedOutput());
    }

    // --- Methods that print + call super ---

    @Test
    void setExposurePrintsAndDelegatesToSuper() {
        plotter.setExposure(ExposureMode.OPEN);

        assertTrue(capturedOutput().contains("setExposure(OPEN)"));
        assertEquals(ExposureMode.OPEN, plotter.getExposure());
    }

    @Test
    void setExposureClosedPrintsAndDelegatesToSuper() {
        plotter.setExposure(ExposureMode.CLOSED);

        assertTrue(capturedOutput().contains("setExposure(CLOSED)"));
        assertEquals(ExposureMode.CLOSED, plotter.getExposure());
    }

    @Test
    void setExposureFlashPrintsAndDelegatesToSuper() {
        plotter.setExposure(ExposureMode.FLASH);

        assertTrue(capturedOutput().contains("setExposure(FLASH)"));
        assertEquals(ExposureMode.FLASH, plotter.getExposure());
    }

    @Test
    void setModePrintsAndDelegatesToSuper() {
        plotter.setMode(PlotterMode.METRIC);

        assertTrue(capturedOutput().contains("setMode(METRIC)"));
        assertTrue(plotter.getOptions().contains(PlotterMode.METRIC));
    }

    @Test
    void setModeImperialPrintsAndDelegatesToSuper() {
        plotter.setMode(PlotterMode.IMPERIAL);

        assertTrue(capturedOutput().contains("setMode(IMPERIAL)"));
        assertTrue(plotter.getOptions().contains(PlotterMode.IMPERIAL));
    }

    @Test
    void resetModePrintsAndDelegatesToSuper() {
        plotter.setMode(PlotterMode.POLYGON);
        outputStream.reset();

        plotter.resetMode(PlotterMode.POLYGON);

        assertTrue(capturedOutput().contains("resetMode(POLYGON)"));
        assertFalse(plotter.getOptions().contains(PlotterMode.POLYGON));
    }

    @Test
    void commentPrintsAndDelegatesToSuper() {
        plotter.comment(42, "test comment");

        String output = capturedOutput();
        assertTrue(output.contains("comment(42, test comment)"));
    }

    @Test
    void addFlashPrintsAndDelegatesToSuper() {
        plotter.addFlash();

        assertTrue(capturedOutput().contains("addFlash()"));
    }

    @Test
    void setAperturePrintsAndDelegatesToSuper() {
        plotter.setAperture(10);

        assertTrue(capturedOutput().contains("setAperture(10)"));
    }

    @Test
    void setApertureWithLargeValue() {
        plotter.setAperture(999);

        assertTrue(capturedOutput().contains("setAperture(999)"));
    }

    @Test
    void setFormatXPrintsAndDelegatesToSuper() {
        plotter.setFormatX(2, 4);

        String output = capturedOutput();
        assertTrue(output.contains("setFormatX(2, 4)"));
        // Verify super was called by checking the format parser works
        plotter.setMode(PlotterMode.METRIC);
        assertNotNull(plotter.getFormatX());
    }

    @Test
    void setFormatYPrintsAndDelegatesToSuper() {
        plotter.setFormatY(3, 5);

        String output = capturedOutput();
        // Note: setFormatY uses %s, %s format in FullPlotter source
        assertTrue(output.contains("setFormatY(3, 5)"));
        assertNotNull(plotter.getFormatY());
    }

    @Test
    void getMacroPrintsAndDelegatesToSuper() {
        ApertureMacro result = plotter.getMacro("TEST");

        assertTrue(capturedOutput().contains("getMacro(TEST)"));
        assertNull(result);
    }

    @Test
    void getMacroReturnsStoredMacro() {
        ApertureMacro macro = new ApertureMacro("MY_MACRO", Collections.emptyList());
        plotter.addMacro(macro);
        outputStream.reset();

        ApertureMacro result = plotter.getMacro("MY_MACRO");

        assertTrue(capturedOutput().contains("getMacro(MY_MACRO)"));
        assertSame(macro, result);
    }

    @Test
    void addAperturePrintsAndDelegatesToSuper() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 10, List.of(0.5));
        plotter.addAperture(aperture);

        String output = capturedOutput();
        assertTrue(output.contains("addAperture("));
        assertTrue(output.contains("circle"));
    }

    @Test
    void addMacroPrintsAndDelegatesToSuper() {
        ApertureMacro macro = new ApertureMacro("MACRO1", Collections.emptyList());
        plotter.addMacro(macro);

        String output = capturedOutput();
        assertTrue(output.contains("addMacro("));
        assertTrue(output.contains("MACRO1"));
        // Verify stored via super
        assertNotNull(plotter.getMacro("MACRO1"));
    }

    // --- Methods that just print (no super delegation beyond AbstractPlotter no-ops) ---

    @Test
    void selectAxisPrintsFormattedOutput() {
        plotter.selectAxis("A", "B");

        assertEquals("selectAxis(A, B)\n", capturedOutput());
    }

    @Test
    void setImagePolarityPrintsFormattedOutput() {
        plotter.setImagePolarity("POSITIVE");

        assertEquals("setImagePolarity(POSITIVE)\n", capturedOutput());
    }

    @Test
    void setLayerPolarityPrintsFormattedOutput() {
        plotter.setLayerPolarity("DARK");

        assertEquals("setLayerPolarity(DARK)\n", capturedOutput());
    }

    @Test
    void setOffsetPrintsFormattedOutput() {
        plotter.setOffset("1.0", "2.0");

        assertEquals("setOffset(1.0, 2.0)\n", capturedOutput());
    }

    @Test
    void setScaleFactorPrintsFormattedOutput() {
        plotter.setScaleFactor("1.5", "2.5");

        assertEquals("setScaleFactor(1.5, 2.5)\n", capturedOutput());
    }

    @Test
    void stepAndRepeatPrintsFormattedOutput() {
        plotter.stepAndRepeat("3", "4", "10", "20");

        assertEquals("stepAndRepeat(3, 4, 10, 20)\n", capturedOutput());
    }

    @Test
    void stepAndRepeatWithNullArgs() {
        plotter.stepAndRepeat(null, null, null, null);

        assertEquals("stepAndRepeat(null, null, null, null)\n", capturedOutput());
    }

    // --- Integration: multiple calls produce sequential output ---

    @Test
    void multiplePrintCallsAppendOutput() {
        plotter.setCenter("1", "2");
        plotter.setPosition("3", "4");
        plotter.done();

        String output = capturedOutput();
        assertTrue(output.contains("setCenter(1, 2)"));
        assertTrue(output.contains("setPosition(3, 4)"));
        assertTrue(output.contains("done()"));
        // Verify ordering
        int centerIdx = output.indexOf("setCenter");
        int posIdx = output.indexOf("setPosition");
        int doneIdx = output.indexOf("done");
        assertTrue(centerIdx < posIdx);
        assertTrue(posIdx < doneIdx);
    }

    @Test
    void askContinueReturnsFalse() {
        assertFalse(plotter.askContinue(0, 0));
    }

    @Test
    void defaultExposureIsClosed() {
        assertEquals(ExposureMode.CLOSED, plotter.getExposure());
    }
}
