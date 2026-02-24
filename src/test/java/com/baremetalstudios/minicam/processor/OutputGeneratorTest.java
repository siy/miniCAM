package com.baremetalstudios.minicam.processor;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.geometry.Polygon;

public class OutputGeneratorTest {
    private String expected = "G94     ( Millimeters per minute feed rate. )\n" + "G21     ( Units == Millimeters. )\n"
                              + "G17     ( X-Y plane )\n" + "G90     ( Absolute coordinates.        )\n"
                              + "G64 P0.00100 ( Set maximum deviation from commanded toolpath )\n"
                              + "G10 L1 P1 R0.80000 ( Define cutter tool )\n" + "\n"
                              + "G10 L1 P2 R0.25000 (Define drill tool)\n" + "\n"
                              + "G10 L1 P3 R0.45000 (Define drill tool)\n" + "\n" + "G00 Z55.00000 ( retract )\n"
                              + "T1\n" + "M5\n" + "M6\n" + "(MSG, Change tool bit to mill size 1.60 [1])\n" + "M0\n"
                              + "S60000  ( RPM spindle speed.           )\n" + "F400.00000\n"
                              + "M3      ( Spindle on clockwise.        )\n" + "\n"
                              + "G04 P5.00000 ( wait while spindle reach full speed )\n" + "( Polygon  1, 5 points, 1.000mm x 1.000mm )\n"
                              + "G00 X1.00000 Y1.00000 Z6.00000 ( rapid move to begin )\n"
                              + "G01 Z-2.20000 F150.00000 ( plunge )\n" + "X1.00000 Y1.00000\n" + "X1.00000 Y2.00000\n"
                              + "X2.00000 Y2.00000\n" + "X2.00000 Y1.00000\n" + "X1.00000 Y1.00000\n"
                              + "G00 Z6.00000 ( retract )\n" + "G00 Z55.00000 ( retract )\n" + "T2\n" + "M5\n" + "M6\n"
                              + "(MSG, Change tool bit to drill size 0.50 [1])\n" + "M0\n"
                              + "S60000  ( RPM spindle speed.           )\n" + "F400.00000\n"
                              + "M3      ( Spindle on clockwise.        )\n" + "\n"
                              + "G04 P5.00000 ( wait while spindle reach full speed )\n"
                              + "G00 X1.50000 Y1.50000 Z5.00000 (rapid move to begin)\n"
                              + "G81 R5.00000 Z-2.20000 X1.50000 Y1.50000\n" + "G00 Z55.00000 ( retract )\n" + "T3\n"
                              + "M5\n" + "M6\n" + "(MSG, Change tool bit to drill size 0.90 [1])\n" + "M0\n"
                              + "S60000  ( RPM spindle speed.           )\n" + "F400.00000\n"
                              + "M3      ( Spindle on clockwise.        )\n" + "\n"
                              + "G04 P5.00000 ( wait while spindle reach full speed )\n"
                              + "G00 X1.80000 Y1.80000 Z5.00000 (rapid move to begin)\n"
                              + "G81 R5.00000 Z-2.20000 X1.80000 Y1.80000\n" + "\n" + "G00 Z55.00000 ( retract )\n"
                              + "M5 ( Spindle stop. )\n" + "M9 ( Coolant off. )\n" + "M2 ( Program end. )\n";

    @Test
    public void singlePolygonIsGeneratedCorrectly() throws Exception {
        List<Point> points = new ArrayList<Point>(Arrays.asList(new Point(1, 1), new Point(1, 2), new Point(2, 2),
                                                                new Point(2, 1), new Point(1, 1)));
        Polygon p1 = new Polygon(points);
        DrillGroup drillGroup1 = new DrillGroup("T01", 0.5, Arrays.asList(new Point(1.5, 1.5)));
        DrillGroup drillGroup2 = new DrillGroup("T02", 0.9, Arrays.asList(new Point(1.8, 1.8)));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream pw = new PrintStream(os);
        OutputConfig config = new OutputConfig();
        config.setAdjustDrillDepth(false);
        config.setSeparator("\n");
        OutputGenerator generator = new OutputGenerator(pw, config);

        generator.generate(Arrays.asList(p1), Arrays.asList(drillGroup1, drillGroup2));

        pw.close();
        String result = os.toString("UTF-8");
        assertEquals(expected, result);
    }

    @Test
    public void testRound() throws Exception {
        assertEquals(0.24, OutputGenerator.round(0.241, 0.01), 0.00000001);
    }
}
