package com.baremetalstudios.minicam.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.baremetalstudios.minicam.geometry.Point;

public class OutputConfigTest {

    private OutputConfig config;

    @BeforeEach
    public void setUp() {
        config = new OutputConfig();
    }

    // --- Default values (those NOT already covered by ConfigurationReaderTest) ---

    @Test
    public void defaultXScaleIsOne() {
        assertEquals(1.0, config.getXScale());
    }

    @Test
    public void defaultYScaleIsOne() {
        assertEquals(1.0, config.getYScale());
    }

    @Test
    public void defaultSeparatorIsSystemLineSeparator() {
        assertEquals(System.lineSeparator(), config.getSeparator());
    }

    @Test
    public void defaultIsDoublePassIsFalse() {
        assertFalse(config.isDoublePass());
    }

    @Test
    public void defaultIsReplaceDrillsWithPolygonsIsFalse() {
        assertFalse(config.isReplaceDrillsWithPolygons());
    }

    @Test
    public void defaultDrillThresholdIsTwo() {
        assertEquals(2.0, config.getDrillThreshold());
    }

    @Test
    public void defaultZSafe() {
        assertEquals(6.0, config.getZSafe());
    }

    @Test
    public void defaultZDrillSafe() {
        assertEquals(5.0, config.getZDrillSafe());
    }

    @Test
    public void defaultZCut() {
        assertEquals(-2.2, config.getZCut());
    }

    @Test
    public void defaultZDrill() {
        assertEquals(-2.2, config.getZDrill());
    }

    @Test
    public void defaultCutFeedRate() {
        assertEquals(150.0, config.getCutFeedRate());
    }

    @Test
    public void defaultToolChangeZ() {
        assertEquals(55.0, config.getToolChangeZ());
    }

    @Test
    public void defaultFreeMoveRate() {
        assertEquals(400.0, config.getFreeMoveRate());
    }

    @Test
    public void defaultCutterDiameter() {
        assertEquals(1.6, config.getCutterDiameter());
    }

    @Test
    public void defaultMinTabDistance() {
        assertEquals(16.0, config.getMinTabDistance());
    }

    @Test
    public void defaultTabWidth() {
        assertEquals(5.0, config.getTabWidth());
    }

    @Test
    public void defaultTabDrillDiameter() {
        assertEquals(0.5, config.getTabDrillDiameter());
    }

    @Test
    public void defaultDrillDiameterStep() {
        assertEquals(0.1, config.getDrillDiameterStep());
    }

    @Test
    public void defaultSpindleSpeed() {
        assertEquals(60000, config.getSpindleSpeed());
    }

    @Test
    public void defaultSpindleDelay() {
        assertEquals(5, config.getSpindleDelay());
    }

    @Test
    public void defaultOptimizationLevel() {
        assertEquals(5, config.getOptimizationLevel());
    }

    @Test
    public void defaultGenerateInnerCutIsFalse() {
        assertFalse(config.generateInnerCut());
    }

    @Test
    public void defaultRotateBoardIsTrue() {
        assertTrue(config.isRotationEnabled());
    }

    @Test
    public void defaultCenterBoardIsTrue() {
        assertTrue(config.isCenteringEnabled());
    }

    @Test
    public void defaultAdjustDrillDepthIsTrue() {
        assertTrue(config.isAdjustDrillDepth());
    }

    // --- getCenteringDimensions ---

    @Test
    public void getCenteringDimensionsReturnsBoardHeightAndWidth() {
        Point p = config.getCenteringDimensions();
        // getCenteringDimensions uses BOARD_HEIGHT for x, BOARD_WIDTH for y
        assertEquals(200.0, p.getX());
        assertEquals(160.0, p.getY());
    }

    // --- getVarCount ---

    @Test
    public void getVarCountReturns27() {
        assertEquals(27, config.getVarCount());
    }

    // --- getVarNames ---

    @Test
    public void getVarNamesReturnsAllKeys() {
        var names = config.getVarNames();
        assertEquals(27, names.size());
        assertTrue(names.contains(OutputConfig.ZSAFE));
        assertTrue(names.contains(OutputConfig.ZCUT));
        assertTrue(names.contains(OutputConfig.CUT_FEED_RATE));
        assertTrue(names.contains(OutputConfig.TOOL_CHANGE_Z));
        assertTrue(names.contains(OutputConfig.SPINDLE_SPEED));
        assertTrue(names.contains(OutputConfig.SPINDLE_DELAY));
        assertTrue(names.contains(OutputConfig.FREE_MOVE_RATE));
        assertTrue(names.contains(OutputConfig.CUTTER_DIAMETER));
        assertTrue(names.contains(OutputConfig.MIN_TAB_DISTANCE));
        assertTrue(names.contains(OutputConfig.TAB_WIDTH));
        assertTrue(names.contains(OutputConfig.ZDRILL_SAFE));
        assertTrue(names.contains(OutputConfig.ZDRILL));
        assertTrue(names.contains(OutputConfig.TAB_DRILL_DIAMETER));
        assertTrue(names.contains(OutputConfig.DRILL_DIAMETER_STEP));
        assertTrue(names.contains(OutputConfig.GENERATE_INNER_CUT));
        assertTrue(names.contains(OutputConfig.ROTATE_BOARD));
        assertTrue(names.contains(OutputConfig.CENTER_BOARD));
        assertTrue(names.contains(OutputConfig.BOARD_HEIGHT));
        assertTrue(names.contains(OutputConfig.BOARD_WIDTH));
        assertTrue(names.contains(OutputConfig.OPTIMIZATION_LEVEL));
        assertTrue(names.contains(OutputConfig.DRILL_DEPTH_AUTOADJUST));
        assertTrue(names.contains(OutputConfig.DOUBLE_PASS_OUTLINE));
        assertTrue(names.contains(OutputConfig.REPLACE_DRILLS_WITH_POLYGONS));
        assertTrue(names.contains(OutputConfig.DRILL_THRESHOLD));
        assertTrue(names.contains(OutputConfig.SCALE_X));
        assertTrue(names.contains(OutputConfig.SCALE_Y));
        assertTrue(names.contains(OutputConfig.LINE_SEPARATOR));
    }

    // --- putRaw ---

    @Test
    public void putRawWithUnknownVariableReturnsUnknownVariable() {
        String result = config.putRaw(new NamedVar<>("nonexistent.var", "42"));
        assertEquals("Unknown variable", result);
    }

    @Test
    public void putRawWithInvalidIntegerValueReturnsInvalidValue() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.SPINDLE_SPEED, "abc"));
        assertEquals("Invalid value", result);
    }

    @Test
    public void putRawWithInvalidDoubleValueReturnsInvalidValue() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.ZCUT, "not-a-number"));
        assertEquals("Invalid value", result);
    }

    @Test
    public void putRawWithValidDoubleReturnsNull() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.ZCUT, "-1.5"));
        assertNull(result);
        assertEquals(-1.5, config.getZCut());
    }

    @Test
    public void putRawWithValidIntegerReturnsNull() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.SPINDLE_SPEED, "30000"));
        assertNull(result);
        assertEquals(30000, config.getSpindleSpeed());
    }

    @Test
    public void putRawWithValidBooleanReturnsNull() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.GENERATE_INNER_CUT, "true"));
        assertNull(result);
        assertTrue(config.generateInnerCut());
    }

    @Test
    public void putRawWithValidStringReturnsNull() {
        String result = config.putRaw(new NamedVar<>(OutputConfig.LINE_SEPARATOR, "\r\n"));
        assertNull(result);
        assertEquals("\r\n", config.getSeparator());
    }

    @Test
    public void putRawIsCaseInsensitive() {
        // Key lookup is lowercased in putRaw
        String result = config.putRaw(new NamedVar<>("CUT.SAFE.Z", "10.0"));
        assertNull(result);
        assertEquals(10.0, config.getZSafe());
    }

    // --- Setter methods ---

    @Test
    public void setBoardWidthUpdatesValue() {
        config.setBoardWidth(300);
        assertEquals(300.0, config.get(OutputConfig.BOARD_WIDTH, Double.class).getValue());
    }

    @Test
    public void setBoardHeightUpdatesValue() {
        config.setBoardHeight(400);
        assertEquals(400.0, config.get(OutputConfig.BOARD_HEIGHT, Double.class).getValue());
    }

    @Test
    public void setRotateUpdatesValue() {
        config.setRotate(false);
        assertFalse(config.isRotationEnabled());
    }

    @Test
    public void setCenterUpdatesValue() {
        config.setCenter(false);
        assertFalse(config.isCenteringEnabled());
    }

    @Test
    public void setAdjustDrillDepthUpdatesValue() {
        config.setAdjustDrillDepth(false);
        assertFalse(config.isAdjustDrillDepth());
    }

    @Test
    public void setSeparatorUpdatesValue() {
        config.setSeparator("\n");
        assertEquals("\n", config.getSeparator());
    }

    // --- getCenteringDimensions after setBoardWidth/Height ---

    @Test
    public void getCenteringDimensionsReflectsUpdatedBoardDimensions() {
        config.setBoardHeight(500);
        config.setBoardWidth(250);
        Point p = config.getCenteringDimensions();
        assertEquals(500.0, p.getX());
        assertEquals(250.0, p.getY());
    }
}
