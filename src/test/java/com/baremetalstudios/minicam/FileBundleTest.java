package com.baremetalstudios.minicam;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

public class FileBundleTest {

    private static final File DUMMY = new File("dummy");

    @Test
    public void isValidFalseWhenInFileNull() {
        FileBundle bundle = new FileBundle();
        bundle.setDrillFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutFile(DUMMY);

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidFalseWhenDrillFileNull() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutFile(DUMMY);

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidFalseWhenConfigFileNull() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setDrillFile(DUMMY);
        bundle.setOutFile(DUMMY);

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidFalseWhenOutFileNullAndOutDrillFileNull() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setDrillFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutMillFile(DUMMY);
        // outFile = null, outDrillFile = null -> notNull(outDrillFile, outMillFile) fails

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidFalseWhenOutFileNullAndOutMillFileNull() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setDrillFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutDrillFile(DUMMY);
        // outFile = null, outMillFile = null -> notNull(outDrillFile, outMillFile) fails

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidTrueWithAllRequiredPlusOutFile() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setDrillFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutFile(DUMMY);

        assertTrue(bundle.isValid());
    }

    @Test
    public void isValidTrueWithAllRequiredPlusOutDrillFileAndOutMillFile() {
        FileBundle bundle = new FileBundle();
        bundle.setInFile(DUMMY);
        bundle.setDrillFile(DUMMY);
        bundle.setConfigFile(DUMMY);
        bundle.setOutDrillFile(DUMMY);
        bundle.setOutMillFile(DUMMY);

        assertTrue(bundle.isValid());
    }

    @Test
    public void isSeparateModeTrueWhenOutFileNull() {
        FileBundle bundle = new FileBundle();

        assertTrue(bundle.isSeparateMode());
    }

    @Test
    public void isSeparateModeFalseWhenOutFileSet() {
        FileBundle bundle = new FileBundle();
        bundle.setOutFile(DUMMY);

        assertFalse(bundle.isSeparateMode());
    }

    @Test
    public void allGettersReturnWhatWasSet() {
        FileBundle bundle = new FileBundle();

        File inFile = new File("in");
        File drillFile = new File("drill");
        File outFile = new File("out");
        File configFile = new File("config");
        File outMillFile = new File("outMill");
        File outDrillFile = new File("outDrill");

        bundle.setInFile(inFile);
        bundle.setDrillFile(drillFile);
        bundle.setOutFile(outFile);
        bundle.setConfigFile(configFile);
        bundle.setOutMillFile(outMillFile);
        bundle.setOutDrillFile(outDrillFile);

        assertSame(inFile, bundle.getInFile());
        assertSame(drillFile, bundle.getDrillFile());
        assertSame(outFile, bundle.getOutFile());
        assertSame(configFile, bundle.getConfigFile());
        assertSame(outMillFile, bundle.getOutMillFile());
        assertSame(outDrillFile, bundle.getOutDrillFile());
    }

    @Test
    public void isValidFalseWhenAllFieldsNull() {
        FileBundle bundle = new FileBundle();

        assertFalse(bundle.isValid());
    }

    @Test
    public void isValidFalseWhenOnlyOutDrillAndOutMillSetButRequiredFieldsMissing() {
        FileBundle bundle = new FileBundle();
        bundle.setOutDrillFile(DUMMY);
        bundle.setOutMillFile(DUMMY);

        assertFalse(bundle.isValid());
    }
}
