
package com.baremetalstudios.minicam.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;

import org.junit.Test;

import com.baremetalstudios.minicam.Util;
import com.baremetalstudios.minicam.simulator.OutlinePlotter;

public class ParserTest {
    @Test
    public void parserCorrectlyHandlesSimpleCases() throws Exception {
        File infile = new File("src/test/resources/merge2.bor");
        FileInputStream outlineStream = null;

        try {
            outlineStream = new FileInputStream(infile);
            Parser parser = new Parser(outlineStream);
            TestOutlinePlotter simulator = new TestOutlinePlotter();
            parser.setSimulator(simulator);
            parser.Input();
            assertEquals(1075, simulator.getCounter());
        } finally {
            Util.close(outlineStream);
        }
    }

    @Test
    public void parserCorrectlyHandlesComplexCases() throws Exception {
        File infile = new File("src/test/resources/merge2.cmp");
        FileInputStream outlineStream = null;

        try {
            outlineStream = new FileInputStream(infile);
            Parser parser = new Parser(outlineStream);
            TestOutlinePlotter simulator = new TestOutlinePlotter();
            parser.setSimulator(simulator);
            parser.Input();
            assertEquals(55731, simulator.getCounter());
        } finally {
            Util.close(outlineStream);
        }
    }
    
    static class TestOutlinePlotter extends OutlinePlotter {
        private int counter = 0;
        @Override
        public void setCenter(String string, String string2) {
            super.setCenter(string, string2);
            counter += 1;
        }

        @Override
        public void setPosition(String string, String string2) {
            super.setPosition(string, string2);
            counter += 1;
        }

        @Override
        public void done() {
            super.done();
            counter += 1;
        }

        public int getCounter() {
            return counter;
        }
    }
}
