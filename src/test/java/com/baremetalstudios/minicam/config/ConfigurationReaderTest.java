package com.baremetalstudios.minicam.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.CharArrayReader;

import org.junit.jupiter.api.Test;


public class ConfigurationReaderTest {
    private static final String configText =
                    "config.free.move.feed.rate = 10\n" +
                    "config.tool.change.z = 45\n" +
                    "config.spindle.speed = 450\n" +
                    "config.spindle.startup.delay = 10\n" +
                    "config.drills.diameter.step = 0.05\n" +
                    "config.optimization.level = 9\n" +
                    "output.board.height = 100\n" +
                    "output.board.width = 60\n" +
                    "output.board.center.panel = false\n" +
                    "output.board.rotate.panel = false\n" +
                    "output.generate.inner.cut = true\n" +
                    "tab.drill.diameter = 0.6\n" +
                    "tab.width = 4\n" +
                    "tab.minimal.distance = 25\n" +
                    "cut.cutter.diameter = 1.0\n" +
                    "cut.feed.rate = 50\n" +
                    "cut.z = -1.6\n" +
                    "cut.safe.z = 5.5\n" +
                    "drill.z = -1.8\n" +
                    "drill.safe.z = 5.5\n" +
                    "config.drills.adjust.depth = false\n" +
                    "config.outline.double.pass =  true\n" +
                    "config.mill.large.drills   = true\n" +
                    "config.mill.large.drills.threshold= 1.5\n" +
                    "config.scale.x = 0.9965\n" +
                    "config.scale.y = 0.9965\n";

    @Test
    public void testSimpleVar() throws Exception {
        NamedVar<String> res = ConfigurationReader.parseLine("var = value");

        assertEquals("var", res.getName());
        assertEquals("value", res.getValue());
    }

    @Test
    public void testSimpleVarManySpaces() throws Exception {
        NamedVar<String> res = ConfigurationReader.parseLine("var  =   value");

        assertEquals("var", res.getName());
        assertEquals("value", res.getValue());
    }

    @Test
    public void testSimpleVarWithNoSpaces() throws Exception {
        NamedVar<String> res = ConfigurationReader.parseLine("var=value");

        assertEquals("var", res.getName());
        assertEquals("value", res.getValue());
    }

    @Test
    public void testComplexVar() throws Exception {
        NamedVar<String> res = ConfigurationReader.parseLine("vaR.Var = value");

        assertEquals("vaR.Var", res.getName());
        assertEquals("value", res.getValue());
    }

    @Test
    public void configurationReadSuccessFully() throws Exception {
        OutputConfig config = ConfigurationReader.readConfig(toBufferedReader(configText));

        assertEquals(27, config.getVarCount());

        assertEquals(Double.valueOf(  0.05), config.get(OutputConfig.DRILL_DIAMETER_STEP, Double.class ).getValue());
        assertEquals(Double.valueOf(   0.6), config.get(OutputConfig.TAB_DRILL_DIAMETER , Double.class ).getValue());
        assertEquals(Double.valueOf(   4.0), config.get(OutputConfig.TAB_WIDTH          , Double.class ).getValue());
        assertEquals(Double.valueOf(  25.0), config.get(OutputConfig.MIN_TAB_DISTANCE   , Double.class ).getValue());
        assertEquals(Double.valueOf(   1.0), config.get(OutputConfig.CUTTER_DIAMETER    , Double.class ).getValue());
        assertEquals(Double.valueOf(  10.0), config.get(OutputConfig.FREE_MOVE_RATE     , Double.class ).getValue());
        assertEquals(Double.valueOf(  45.0), config.get(OutputConfig.TOOL_CHANGE_Z      , Double.class ).getValue());
        assertEquals(Double.valueOf(  50.0), config.get(OutputConfig.CUT_FEED_RATE      , Double.class ).getValue());
        assertEquals(Double.valueOf(  -1.8), config.get(OutputConfig.ZDRILL             , Double.class ).getValue());
        assertEquals(Double.valueOf(  -1.6), config.get(OutputConfig.ZCUT               , Double.class ).getValue());
        assertEquals(Double.valueOf(   5.5), config.get(OutputConfig.ZDRILL_SAFE        , Double.class ).getValue());
        assertEquals(Double.valueOf(   5.5), config.get(OutputConfig.ZSAFE              , Double.class ).getValue());
        assertEquals(Double.valueOf( 100.0), config.get(OutputConfig.BOARD_HEIGHT       , Double.class ).getValue());
        assertEquals(Double.valueOf(  60.0), config.get(OutputConfig.BOARD_WIDTH        , Double.class ).getValue());
        assertEquals(Double.valueOf(   1.5), config.get(OutputConfig.DRILL_THRESHOLD    , Double.class ).getValue());
        assertEquals(Double.valueOf(0.9965), config.get(OutputConfig.SCALE_X            , Double.class ).getValue());
        assertEquals(Double.valueOf(0.9965), config.get(OutputConfig.SCALE_Y            , Double.class ).getValue());

        assertEquals(Integer.valueOf(450), config.get(OutputConfig.SPINDLE_SPEED        , Integer.class).getValue());
        assertEquals(Integer.valueOf(10), config.get(OutputConfig.SPINDLE_DELAY         , Integer.class).getValue());
        assertEquals(Integer.valueOf(9)  , config.get(OutputConfig.OPTIMIZATION_LEVEL   , Integer.class).getValue());
        assertEquals(Boolean.TRUE , config.get(OutputConfig.GENERATE_INNER_CUT          , Boolean.class).getValue());
        assertEquals(Boolean.FALSE, config.get(OutputConfig.CENTER_BOARD                , Boolean.class).getValue());
        assertEquals(Boolean.FALSE, config.get(OutputConfig.ROTATE_BOARD                , Boolean.class).getValue());
        assertEquals(Boolean.FALSE, config.get(OutputConfig.DRILL_DEPTH_AUTOADJUST      , Boolean.class).getValue());
        assertEquals(Boolean.TRUE , config.get(OutputConfig.REPLACE_DRILLS_WITH_POLYGONS, Boolean.class).getValue());
        assertEquals(Boolean.TRUE , config.get(OutputConfig.DOUBLE_PASS_OUTLINE         , Boolean.class).getValue());

    }

    private BufferedReader toBufferedReader(String configText) {
        return new BufferedReader(new CharArrayReader(configText.toCharArray()));
    }
}
