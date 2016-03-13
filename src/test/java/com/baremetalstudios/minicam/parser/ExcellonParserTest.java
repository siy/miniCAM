package com.baremetalstudios.minicam.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.simulator.FormatParser;


public class ExcellonParserTest {
    //TODO: negative tests
    @Test
    public void fullGroupIsParsedSuccessfully() throws Exception {
        DrillGroup group = new ExcellonParser().parseGroup("T01C0.021650", new HashMap<>());
        assertEquals("T01", group.getId());
        assertEquals(0.021650 * FormatParser.IMPERIAL_SCALE, group.getDiameter(), 0.0000001);
    }
    @Test
    public void partialGroupIsParsedSuccessfully() throws Exception {
        HashMap<String, DrillGroup> groups = new HashMap<>();
        groups.put("T01", new DrillGroup("T01", 0.55));
        DrillGroup group = new ExcellonParser().parseGroup("T01", groups);
        assertEquals("T01", group.getId());
        assertEquals(0.55, group.getDiameter(), 0.0000001);
    }
    @Test
    public void drillIsParsedSuccessfully() throws Exception {
        Point point = new ExcellonParser().parsePoint("X017606Y021307");
        assertEquals(1.7606 * FormatParser.IMPERIAL_SCALE, point.getX(), 0.0000001);
        assertEquals(2.1307 * FormatParser.IMPERIAL_SCALE, point.getY(), 0.0000001);
    }
    @Test
    public void fileWithInlineToolsIsParsedSuccessfully() throws Exception {
        File infile = new File("src/test/resources/merge2.drd");
        ExcellonParser parser = new ExcellonParser(new FileInputStream(infile));

        List<DrillGroup> result = parser.parse();

        assertEquals(5, result.size());
        assertEquals("T01", result.get(0).getId());
        assertEquals("T02", result.get(1).getId());
        assertEquals("T03", result.get(2).getId());
        assertEquals("T04", result.get(3).getId());

        assertEquals(0.0118  * FormatParser.IMPERIAL_SCALE, result.get(0).getDiameter(), 0.0000001);
        assertEquals(0.02165 * FormatParser.IMPERIAL_SCALE, result.get(1).getDiameter(), 0.0000001);
        assertEquals(0.0374  * FormatParser.IMPERIAL_SCALE, result.get(2).getDiameter(), 0.0000001);
        assertEquals(0.063   * FormatParser.IMPERIAL_SCALE, result.get(3).getDiameter(), 0.0000001);
        assertEquals(0.126   * FormatParser.IMPERIAL_SCALE, result.get(4).getDiameter(), 0.0000001);

        assertEquals(21, result.get(0).getDrills().size());
        assertEquals(46, result.get(1).getDrills().size());
        assertEquals(52, result.get(2).getDrills().size());
        assertEquals(16, result.get(3).getDrills().size());

        assertEquals(1.1898 * FormatParser.IMPERIAL_SCALE, result.get(0).getDrills().get(0).getX(), 0.0000001);
        assertEquals(0.6937 * FormatParser.IMPERIAL_SCALE, result.get(0).getDrills().get(0).getY(), 0.0000001);
    }
    @Test
    public void fileWithSplitToolDefinitionsIsParsedSuccessfully() throws Exception {
        File infile = new File("src/test/resources/merge3.drd");
        ExcellonParser parser = new ExcellonParser(new FileInputStream(infile));

        List<DrillGroup> result = parser.parse();

        assertEquals(3, result.size());
        assertEquals("T01", result.get(0).getId());
        assertEquals("T02", result.get(1).getId());
        assertEquals("T03", result.get(2).getId());

        assertEquals(0.02362  * FormatParser.IMPERIAL_SCALE, result.get(0).getDiameter(), 0.0000001);
        assertEquals(0.04000 * FormatParser.IMPERIAL_SCALE, result.get(1).getDiameter(), 0.0000001);
        assertEquals(0.06299 * FormatParser.IMPERIAL_SCALE, result.get(2).getDiameter(), 0.0000001);

        assertEquals(1, result.get(0).getDrills().size());
        assertEquals(5, result.get(1).getDrills().size());
        assertEquals(3, result.get(2).getDrills().size());

        assertEquals(14.7061 * FormatParser.IMPERIAL_SCALE, result.get(0).getDrills().get(0).getX(), 0.0000001);
        assertEquals(20.6511 * FormatParser.IMPERIAL_SCALE, result.get(0).getDrills().get(0).getY(), 0.0000001);
    }
}
