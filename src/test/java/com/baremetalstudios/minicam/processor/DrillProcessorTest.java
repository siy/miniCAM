package com.baremetalstudios.minicam.processor;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;


public class DrillProcessorTest {
    private OutputConfig config = new OutputConfig();
    private DrillProcessor processor = new DrillProcessor(config);

    @Test
    public void closeDrillsAreMerged() throws Exception {
        DrillGroup drillGroup1 = new DrillGroup("T01", 0.50, Arrays.asList(new Point(1.5, 1.5)));
        DrillGroup drillGroup2 = new DrillGroup("T02", 0.55, Arrays.asList(new Point(1.6, 1.6)));
        DrillGroup drillGroup3 = new DrillGroup("T03", 0.60, Arrays.asList(new Point(1.7, 1.7)));
        List<DrillGroup> list = new ArrayList<DrillGroup>(Arrays.asList(drillGroup1, drillGroup2, drillGroup3));

        List<DrillGroup> result = processor.process(list);
        assertEquals(2, result.size());
        assertEquals(0.50, result.get(0).getDiameter(), 0.0000001);
        assertEquals(0.60, result.get(1).getDiameter(), 0.0000001);
        assertEquals(2, result.get(1).getDrills().size());
    }

    @Test
    public void drillDiametersAreSnappedToGrid() throws Exception {
        DrillGroup drillGroup1 = new DrillGroup("T01", 0.51, Arrays.asList(new Point(1.5, 1.5)));
        DrillGroup drillGroup2 = new DrillGroup("T02", 0.62, Arrays.asList(new Point(1.6, 1.6)));
        DrillGroup drillGroup3 = new DrillGroup("T03", 0.74, Arrays.asList(new Point(1.7, 1.7)));
        List<DrillGroup> list = new ArrayList<DrillGroup>(Arrays.asList(drillGroup1, drillGroup2, drillGroup3));

        List<DrillGroup> result = processor.process(list);

        assertEquals(3, result.size());
        assertEquals(0.50, result.get(0).getDiameter(), 0.0000001);
        assertEquals(0.60, result.get(1).getDiameter(), 0.0000001);
        assertEquals(0.70, result.get(2).getDiameter(), 0.0000001);
    }
}
