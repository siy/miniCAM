/*******************************************************************************
 * Copyright 2014, 2015 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.baremetalstudios.minicam.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.baremetalstudios.minicam.geometry.Point;

public class OutputConfig {
    public static final String GENERATE_INNER_CUT = "output.generate.inner.cut";
    public static final String SPINDLE_SPEED = "config.spindle.speed";
    public static final String SPINDLE_DELAY = "config.spindle.startup.delay";
    public static final String DRILL_DIAMETER_STEP = "config.drills.diameter.step";
    public static final String TAB_DRILL_DIAMETER = "tab.drill.diameter";
    public static final String TAB_WIDTH = "tab.width";
    public static final String MIN_TAB_DISTANCE = "tab.minimal.distance";
    public static final String CUTTER_DIAMETER = "cut.cutter.diameter";
    public static final String FREE_MOVE_RATE = "config.free.move.feed.rate";
    public static final String TOOL_CHANGE_Z = "config.tool.change.z";
    public static final String CUT_FEED_RATE = "cut.feed.rate";
    public static final String ZDRILL = "drill.z";
    public static final String ZCUT = "cut.z";
    public static final String ZDRILL_SAFE = "drill.safe.z";
    public static final String ZSAFE = "cut.safe.z";
    public static final String BOARD_HEIGHT = "output.board.height";
    public static final String BOARD_WIDTH = "output.board.width";
    public static final String CENTER_BOARD = "output.board.center.panel";
    public static final String ROTATE_BOARD = "output.board.rotate.panel";
    public static final String OPTIMIZATION_LEVEL = "config.optimization.level";
    public static final String DRILL_DEPTH_AUTOADJUST = "config.drills.adjust.depth";
    public static final String DOUBLE_PASS_OUTLINE = "config.outline.double.pass";
    public static final String REPLACE_DRILLS_WITH_POLYGONS = "config.mill.large.drills";
    public static final String DRILL_THRESHOLD = "config.mill.large.drills.threshold";
    public static final String SCALE_X = "config.scale.x";
    public static final String SCALE_Y = "config.scale.y";
    public static final String LINE_SEPARATOR = "config.line.separator";

    private final Map<String, NamedVar<?>> vars = new HashMap<String, NamedVar<?>>();

    private static NamedVar<?>[] defaults = {
        new NamedVar<Double>(ZSAFE, 6.0),
        new NamedVar<Double>(ZDRILL_SAFE, 5.0),
        new NamedVar<Double>(ZCUT, -2.2),
        new NamedVar<Double>(ZDRILL, -2.2),
        new NamedVar<Double>(CUT_FEED_RATE, 150.0),
        new NamedVar<Double>(TOOL_CHANGE_Z, 55.0),
        new NamedVar<Double>(FREE_MOVE_RATE, 400.0),
        new NamedVar<Double>(CUTTER_DIAMETER, 1.6),
        new NamedVar<Double>(MIN_TAB_DISTANCE, 16.0),
        new NamedVar<Double>(TAB_WIDTH, 5.0),
        new NamedVar<Double>(TAB_DRILL_DIAMETER, 0.5),
        new NamedVar<Double>(DRILL_DIAMETER_STEP, 0.1),
        new NamedVar<Double>(BOARD_WIDTH, 160.0),
        new NamedVar<Double>(BOARD_HEIGHT, 200.0),
        new NamedVar<Double>(DRILL_THRESHOLD, 2.0),
        new NamedVar<Double>(SCALE_X, 1.0),
        new NamedVar<Double>(SCALE_Y, 1.0),
        new NamedVar<Integer>(SPINDLE_SPEED, 60000),
        new NamedVar<Integer>(SPINDLE_DELAY, 5),
        new NamedVar<Integer>(OPTIMIZATION_LEVEL, 5),
        new NamedVar<Boolean>(GENERATE_INNER_CUT, false),
        new NamedVar<Boolean>(ROTATE_BOARD, true),
        new NamedVar<Boolean>(CENTER_BOARD, true),
        new NamedVar<Boolean>(DRILL_DEPTH_AUTOADJUST, true),
        new NamedVar<Boolean>(DOUBLE_PASS_OUTLINE, false),
        new NamedVar<Boolean>(REPLACE_DRILLS_WITH_POLYGONS, false),
        new NamedVar<String>(LINE_SEPARATOR, System.lineSeparator()),
    };

    public OutputConfig() {
        for (NamedVar<?> var : defaults) {
            vars.put(var.getName(), var);
        }
    }

    public int getVarCount() {
        return vars.size();
    }

    public Set<String> getVarNames() {
        return vars.keySet();
    }

    @SuppressWarnings("unchecked")
    public <T> NamedVar<T> get(String name, Class<T> clazz) {
        return (NamedVar<T>) vars.get(name);
    }

    public OutputConfig put(NamedVar<?> var) {
        vars.put(var.getName(), var);
        return this;
    }

    public String putRaw(NamedVar<String> var) {
        String key = var.getName().toLowerCase();
        if (!vars.containsKey(key)) {
            return "Unknown variable";
        }

        Class<?> clazz = vars.get(key).getValue().getClass();

        try {
            if (clazz == Boolean.class) {
                vars.put(key, new NamedVar<Boolean>(key, Boolean.parseBoolean(var.getValue())));
            } else if (clazz == Integer.class) {
                vars.put(key, new NamedVar<Integer>(key, Integer.parseInt(var.getValue())));
            } else if (clazz == Double.class) {
                vars.put(key, new NamedVar<Double>(key, Double.parseDouble(var.getValue())));
            } else if (clazz == String.class) {
                vars.put(key, new NamedVar<String>(key, var.getValue()));
            }
        } catch (Exception e) {
            return "Invalid value";
        }

        return null;
    }

    public double getZSafe() {
        return get(ZSAFE, Double.class).getValue();
    }

    public double getZCut() {
        return get(ZCUT, Double.class).getValue();
    }

    public double getCutFeedRate() {
        return get(CUT_FEED_RATE, Double.class).getValue();
    }

    public double getToolChangeZ() {
        return get(TOOL_CHANGE_Z, Double.class).getValue();
    }

    public int getSpindleSpeed() {
        return get(SPINDLE_SPEED, Integer.class).getValue();
    }

    public int getSpindleDelay() {
        return get(SPINDLE_DELAY, Integer.class).getValue();
    }

    public double getFreeMoveRate() {
        return get(FREE_MOVE_RATE, Double.class).getValue();
    }

    public double getCutterDiameter() {
        return get(CUTTER_DIAMETER, Double.class).getValue();
    }

    public double getMinTabDistance() {
        return get(MIN_TAB_DISTANCE, Double.class).getValue();
    }

    public double getTabWidth() {
        return get(TAB_WIDTH, Double.class).getValue();
    }

    public double getZDrillSafe() {
        return get(ZDRILL_SAFE, Double.class).getValue();
    }

    public double getZDrill() {
        return get(ZDRILL, Double.class).getValue();
    }

    public double getTabDrillDiameter() {
        return get(TAB_DRILL_DIAMETER, Double.class).getValue();
    }

    public double getDrillDiameterStep() {
        return get(DRILL_DIAMETER_STEP, Double.class).getValue();
    }

    public boolean generateInnerCut() {
        return get(GENERATE_INNER_CUT, Boolean.class).getValue();
    }

    public boolean isRotationEnabled() {
        return get(ROTATE_BOARD, Boolean.class).getValue();
    }

    public boolean isCenteringEnabled() {
        return get(CENTER_BOARD, Boolean.class).getValue();
    }

    public Point getCenteringDimensions() {
        double x = get(BOARD_HEIGHT, Double.class).getValue();
        double y = get(BOARD_WIDTH, Double.class).getValue();
        return new Point(x, y);
    }

    public void setBoardWidth(int i) {
        put(new NamedVar<Double>(BOARD_WIDTH, Double.valueOf(i)));
    }

    public void setBoardHeight(int i) {
        put(new NamedVar<Double>(BOARD_HEIGHT, Double.valueOf(i)));
    }

    public void setRotate(boolean b) {
        put(new NamedVar<Boolean>(ROTATE_BOARD, b));
    }

    public void setCenter(boolean b) {
        put(new NamedVar<Boolean>(CENTER_BOARD, b));
    }

    public int getOptimizationLevel() {
        return get(OPTIMIZATION_LEVEL, Integer.class).getValue();
    }

    public boolean isAdjustDrillDepth() {
    	return get(DRILL_DEPTH_AUTOADJUST, Boolean.class).getValue();
    }

    public void setAdjustDrillDepth(boolean b) {
        put(new NamedVar<Boolean>(DRILL_DEPTH_AUTOADJUST, b));
    }

    public boolean isDoublePass() {
        return get(DOUBLE_PASS_OUTLINE, Boolean.class).getValue();
    }

    public boolean isReplaceDrillsWithPolygons() {
        return get(REPLACE_DRILLS_WITH_POLYGONS, Boolean.class).getValue();
    }

    public double getDrillThreshold() {
        return get(DRILL_THRESHOLD, Double.class).getValue();
    }

    public double getXScale() {
        return get(SCALE_X, Double.class).getValue();
    }

    public double getYScale() {
        return get(SCALE_Y, Double.class).getValue();
    }

    public String getSeparator() {
        return get(LINE_SEPARATOR, String.class).getValue();
    }

    public void setSeparator(String string) {
        put(new NamedVar<String>(LINE_SEPARATOR, string));
    }
}
