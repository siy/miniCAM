package com.baremetalstudios.minicam.simulator;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleApertureTest {

    @Test
    void constructorStoresTypeIdModifiers() {
        List<Double> modifiers = List.of(1.0, 2.0);
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 10, modifiers);

        assertEquals(10, aperture.getId());
        assertEquals(ApertureType.CIRCLE, aperture.getType());
    }

    @Test
    void getIdReturnsNum() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.RECTANGLE, 42, List.of(0.5));
        assertEquals(42, aperture.getId());
    }

    @Test
    void getTypeReturnsType() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.OBROUND, 7, List.of(1.0));
        assertEquals(ApertureType.OBROUND, aperture.getType());
    }

    @Test
    void toStringWithSingleModifier() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 10, List.of(0.5));
        String result = aperture.toString();

        // Expected: "circle 10 (0.5)"
        assertEquals("circle 10 (0.5)", result);
    }

    @Test
    void toStringWithMultipleModifiers() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.RECTANGLE, 20, List.of(1.0, 2.0, 3.0));
        String result = aperture.toString();

        // Expected: "rectangle 20 (1.0,2.0,3.0)"
        assertEquals("rectangle 20 (1.0,2.0,3.0)", result);
    }

    @Test
    void toStringWithTwoModifiers() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.OBROUND, 15, List.of(0.8, 1.2));
        String result = aperture.toString();

        assertEquals("obround 15 (0.8,1.2)", result);
    }

    @Test
    void toStringIncludesTypeLowercase() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.POLYGON, 5, List.of(1.0));
        String result = aperture.toString();

        assertTrue(result.startsWith("polygon"));
    }

    @Test
    void toStringIncludesNum() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 99, List.of(1.0));
        String result = aperture.toString();

        assertTrue(result.contains("99"));
    }

    @Test
    void toStringIncludesModifiersJoinedByComma() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 1, Arrays.asList(10.0, 20.0, 30.0));
        String result = aperture.toString();

        assertTrue(result.contains("10.0,20.0,30.0"));
    }

    @Test
    void extraTextReturnsEmptyString() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.CIRCLE, 1, List.of(1.0));
        assertEquals("", aperture.extraText());
    }

    @Test
    void toStringForMacroType() {
        SimpleAperture aperture = new SimpleAperture(ApertureType.MACRO, 100, List.of(0.0));
        String result = aperture.toString();

        assertTrue(result.startsWith("macro"));
        assertTrue(result.contains("100"));
    }

    @Test
    void getTypeForAllApertureTypes() {
        for (ApertureType type : ApertureType.values()) {
            SimpleAperture aperture = new SimpleAperture(type, 1, List.of(1.0));
            assertEquals(type, aperture.getType());
        }
    }
}
