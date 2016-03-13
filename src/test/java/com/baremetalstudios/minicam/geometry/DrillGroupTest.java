package com.baremetalstudios.minicam.geometry;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.baremetalstudios.minicam.Util;
import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.parser.ExcellonParser;


public class DrillGroupTest {

    //TODO: not a real test!!!
    @Test
    public void testOptimize() throws Exception {
        DrillGroup group = loadDrillGroup();
        double d = group.calculatePathLenght();
        group.optimize(new OutputConfig());
        assertTrue(d > group.calculatePathLenght());
    }

    private DrillGroup loadDrillGroup() {
        File drillfile = new File("src/test/resources/opt.drd");
        FileInputStream drillStream = null;

        try {
            drillStream = new FileInputStream(drillfile);
            List<DrillGroup> drills = new ExcellonParser(drillStream).parse();
            return drills.get(0);
        } catch (IOException e) {
            return null;
        } finally {
            Util.close(drillStream);
        }
    }
}
