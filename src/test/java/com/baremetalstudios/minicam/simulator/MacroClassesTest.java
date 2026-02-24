package com.baremetalstudios.minicam.simulator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MacroClassesTest {

    // --- MacroConstant ---

    @Test
    void macroConstantToStringReturnsConstantValue() {
        MacroConstant constant = new MacroConstant(3.14);
        assertEquals("constant 3.14", constant.toString());
    }

    @Test
    void macroConstantToStringWithZero() {
        MacroConstant constant = new MacroConstant(0.0);
        assertEquals("constant 0.0", constant.toString());
    }

    @Test
    void macroConstantToStringWithNegativeValue() {
        MacroConstant constant = new MacroConstant(-1.5);
        assertEquals("constant -1.5", constant.toString());
    }

    @Test
    void macroConstantToStringWithIntegerValue() {
        MacroConstant constant = new MacroConstant(42.0);
        assertEquals("constant 42.0", constant.toString());
    }

    // --- MacroVariable ---

    @Test
    void macroVariableToStringReturnsVarIndex() {
        MacroVariable variable = new MacroVariable(1);
        assertEquals("var 1", variable.toString());
    }

    @Test
    void macroVariableToStringWithZeroIndex() {
        MacroVariable variable = new MacroVariable(0);
        assertEquals("var 0", variable.toString());
    }

    @Test
    void macroVariableToStringWithLargeIndex() {
        MacroVariable variable = new MacroVariable(99);
        assertEquals("var 99", variable.toString());
    }

    // --- MacroBinOp ---

    @Test
    void macroBinOpToStringReturnsLhsOpRhs() {
        MacroConstant lhs = new MacroConstant(1.0);
        MacroConstant rhs = new MacroConstant(2.0);
        MacroBinOp binOp = new MacroBinOp(BinaryOperation.ADD, lhs, rhs);

        assertEquals("constant 1.0 ADD constant 2.0", binOp.toString());
    }

    @Test
    void macroBinOpSubtract() {
        MacroConstant lhs = new MacroConstant(5.0);
        MacroConstant rhs = new MacroConstant(3.0);
        MacroBinOp binOp = new MacroBinOp(BinaryOperation.SUBTRACT, lhs, rhs);

        assertEquals("constant 5.0 SUBTRACT constant 3.0", binOp.toString());
    }

    @Test
    void macroBinOpMultiply() {
        MacroVariable lhs = new MacroVariable(1);
        MacroConstant rhs = new MacroConstant(2.0);
        MacroBinOp binOp = new MacroBinOp(BinaryOperation.MULTIPLY, lhs, rhs);

        assertEquals("var 1 MULTIPLY constant 2.0", binOp.toString());
    }

    @Test
    void macroBinOpDivide() {
        MacroConstant lhs = new MacroConstant(10.0);
        MacroVariable rhs = new MacroVariable(3);
        MacroBinOp binOp = new MacroBinOp(BinaryOperation.DIVIDE, lhs, rhs);

        assertEquals("constant 10.0 DIVIDE var 3", binOp.toString());
    }

    @Test
    void macroBinOpNested() {
        MacroConstant a = new MacroConstant(1.0);
        MacroConstant b = new MacroConstant(2.0);
        MacroBinOp inner = new MacroBinOp(BinaryOperation.ADD, a, b);

        MacroConstant c = new MacroConstant(3.0);
        MacroBinOp outer = new MacroBinOp(BinaryOperation.MULTIPLY, inner, c);

        assertEquals("constant 1.0 ADD constant 2.0 MULTIPLY constant 3.0", outer.toString());
    }

    // --- ApertureMacro ---

    @Test
    void apertureMacroGetName() {
        ApertureMacro macro = new ApertureMacro("ROUNDRECT", Collections.emptyList());
        assertEquals("ROUNDRECT", macro.getName());
    }

    @Test
    void apertureMacroToStringWithEmptyPrimitives() {
        ApertureMacro macro = new ApertureMacro("TEST", Collections.emptyList());
        assertEquals("TEST {[]}", macro.toString());
    }

    @Test
    void apertureMacroToStringIncludesName() {
        ApertureMacro macro = new ApertureMacro("MY_MACRO", Collections.emptyList());
        assertTrue(macro.toString().contains("MY_MACRO"));
    }

    @Test
    void apertureMacroToStringIncludesPrimitives() {
        SimpleMacroPrimitive prim = new SimpleMacroPrimitive(MacroPrimitiveType.CIRCLE, Collections.emptyList());
        ApertureMacro macro = new ApertureMacro("CIRC", List.of(prim));
        String result = macro.toString();

        assertTrue(result.startsWith("CIRC"));
        assertTrue(result.contains("{"));
        assertTrue(result.contains("}"));
    }

    // --- MacroAperture ---

    @Test
    void macroApertureExtraTextReturnsMacroToString() {
        ApertureMacro macro = new ApertureMacro("MYMACRO", Collections.emptyList());
        MacroAperture aperture = new MacroAperture(10, macro, List.of(1.0));

        assertEquals(macro.toString(), aperture.extraText());
    }

    @Test
    void macroApertureGetIdReturnsNum() {
        ApertureMacro macro = new ApertureMacro("M", Collections.emptyList());
        MacroAperture aperture = new MacroAperture(42, macro, List.of(1.0));

        assertEquals(42, aperture.getId());
    }

    @Test
    void macroApertureGetTypeIsMacro() {
        ApertureMacro macro = new ApertureMacro("M", Collections.emptyList());
        MacroAperture aperture = new MacroAperture(1, macro, List.of(1.0));

        assertEquals(ApertureType.MACRO, aperture.getType());
    }

    @Test
    void macroApertureToStringIncludesMacroInfo() {
        ApertureMacro macro = new ApertureMacro("THERMAL", Collections.emptyList());
        MacroAperture aperture = new MacroAperture(15, macro, List.of(0.5));
        String result = aperture.toString();

        // toString from SimpleAperture: type.name().toLowerCase() + extraText() + " " + num + " (" + modifiers + ")"
        // extraText returns macro.toString() = "THERMAL {[]}"
        assertTrue(result.startsWith("macro"));
        assertTrue(result.contains("THERMAL"));
        assertTrue(result.contains("15"));
    }

    // --- SimpleMacroPrimitive ---

    @Test
    void simpleMacroPrimitiveGetType() {
        SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(MacroPrimitiveType.CIRCLE, Collections.emptyList());
        assertEquals(MacroPrimitiveType.CIRCLE, primitive.getType());
    }

    @Test
    void simpleMacroPrimitiveSetType() {
        SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(MacroPrimitiveType.CIRCLE, Collections.emptyList());
        primitive.setType(MacroPrimitiveType.POLYGON);
        assertEquals(MacroPrimitiveType.POLYGON, primitive.getType());
    }

    @Test
    void simpleMacroPrimitiveGetExprs() {
        List<MacroPrimitive> exprs = new ArrayList<>();
        exprs.add(new SimpleMacroPrimitive(MacroPrimitiveType.THERMAL, Collections.emptyList()));
        SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(MacroPrimitiveType.OUTLINE, exprs);

        assertEquals(1, primitive.getExprs().size());
        assertSame(exprs, primitive.getExprs());
    }

    @Test
    void simpleMacroPrimitiveSetExprs() {
        SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(MacroPrimitiveType.LINE_VECTOR, Collections.emptyList());
        List<MacroPrimitive> newExprs = new ArrayList<>();
        newExprs.add(new SimpleMacroPrimitive(MacroPrimitiveType.MOIRE, Collections.emptyList()));

        primitive.setExprs(newExprs);

        assertSame(newExprs, primitive.getExprs());
        assertEquals(1, primitive.getExprs().size());
    }

    @Test
    void simpleMacroPrimitiveGetExprsEmpty() {
        SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(MacroPrimitiveType.LINE_CENTER, Collections.emptyList());
        assertTrue(primitive.getExprs().isEmpty());
    }

    @Test
    void simpleMacroPrimitiveAllTypes() {
        for (MacroPrimitiveType type : MacroPrimitiveType.values()) {
            SimpleMacroPrimitive primitive = new SimpleMacroPrimitive(type, Collections.emptyList());
            assertEquals(type, primitive.getType());
        }
    }
}
