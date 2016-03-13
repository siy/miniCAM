package com.baremetalstudios.minicam.simulator;

import static org.junit.Assert.*;

import org.junit.Test;

import com.baremetalstudios.minicam.parser.ParseException;


public class FormatParserTest {
    private FormatParser parser = new FormatParser();

    @Test
    public void validNumberIsParsedProperly() throws Exception {
        parser.setFormat(3, 5);
        parser.setModeMetric();
        
        assertEquals(123.56789, parser.parse("12356789"), 0.00000000001);
    }

    @Test
    public void validNumberIsParsedAndScaledProperly() throws Exception {
        parser.setFormat(3, 5);
        parser.setModeImperial();
        
        assertEquals(123.56789*FormatParser.IMPERIAL_SCALE, parser.parse("12356789"), 0.00000000001);
    }
    
    @Test(expected = ParseException.class)
    public void shortStringIsReported() throws Exception {
        parser.setFormat(3, 5);
        
        parser.parse("1235689");
    }
}
