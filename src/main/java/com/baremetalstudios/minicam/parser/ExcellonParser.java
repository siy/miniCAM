/*******************************************************************************
 * Copyright (c) 2014, 2015, 2020 Sergiy Yevtushenko
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
package com.baremetalstudios.minicam.parser;

import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.Point;
import com.baremetalstudios.minicam.simulator.FormatParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExcellonParser {
    private static final Pattern METRIC_PATTERN = Pattern.compile("METRIC(\\s+)?(0+)\\.(0+).*");
    private static final Pattern TOOL_PATTERN = Pattern.compile("(T[0-9]+)(C([0-9]+\\.[0-9]+))?");
    private static final Pattern DRILL_PATTERN = Pattern.compile("X([0-9]+)Y([0-9]+)");
    private double scale = FormatParser.IMPERIAL_SCALE;
    private int decimals = 3;

    private BufferedReader reader;

    public ExcellonParser() {
    }

    public ExcellonParser(InputStream stream) {
        this(new InputStreamReader(stream));
    }

    public ExcellonParser(InputStreamReader reader) {
        this.reader = new BufferedReader(reader);
    }

    public List<DrillGroup> parse() {
        Map<String, DrillGroup> groups = new LinkedHashMap<>();
        DrillGroup group = null;
        try {
            String line;
            int cnt = 0;
            while ((line = reader.readLine()) != null) {
                cnt++;
                line = line.trim();
                if (line.startsWith("%")) {
                    continue;
                }
                if (line.startsWith("M71")) {
                    scale = FormatParser.METRIC_SCALE;
                }
                if (line.startsWith("M72") || line.startsWith("M70")) {
                    scale = FormatParser.IMPERIAL_SCALE;
                }

                if (line.startsWith("METRIC")) {
                    decimals = parseMetricSettings(line, decimals);
                }

                if (line.startsWith("T")) { // new tool
                    group = parseGroup(line, groups);
                }
                if (line.startsWith("X")) { // new drill
                    if (group == null) {
                        throw new RuntimeException("Coordinates defined before tool at " + cnt);
                    }
                    group.addDrill(parsePoint(line));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse input", e);
        }
        return new ArrayList<DrillGroup>(groups.values());
    }

    public static int parseMetricSettings(String line, int decimals) {
        Matcher matcher = METRIC_PATTERN.matcher(line);
        matcher.find();

        if (!matcher.matches()) {
            System.out.println("Unable to parse format string: " + line);
            return decimals;
        }

        return matcher.group(3).length();
    }

    public Point parsePoint(String line) {
        Matcher matcher = DRILL_PATTERN.matcher(line);
        matcher.find();
        String xString = matcher.group(1);
        String yString = matcher.group(2);
        double divisor;

        if (scale == FormatParser.METRIC_SCALE) {
            // metric can be 5 or 6 digits, 2 or 3 digits after decimal : xxx.xx, xxxx.xx, xxx.xxx
            divisor = Math.pow(10, decimals);
        } else {
            // imperial always uses 6 digits ( xx.xxxx)
            divisor = 10000.0;
        }
        double x = parseCoordinate(xString, divisor);
        double y = parseCoordinate(yString, divisor);
        return new Point(x * scale, y * scale);
    }

    private double parseCoordinate(String string, double divisor) {
        int decimal = string.indexOf('.');
        if (decimal >= 0) {
            return Double.parseDouble(decimal == 0 ? "0" + string : string);
        }

        return Double.parseDouble(string) / divisor;
    }

    public DrillGroup parseGroup(String line, Map<String, DrillGroup> groups) {
        Matcher matcher = TOOL_PATTERN.matcher(line);
        matcher.find();

        if (!matcher.matches()) {
            throw new IllegalStateException("Invlid drill group definition " + line);
        }

        DrillGroup group = groups.get(matcher.group(1));

        if (group != null) {
            return group;
        }

        if (matcher.groupCount() == 3) {
            double diameter = Double.parseDouble(matcher.group(3));
            group = new DrillGroup(matcher.group(1), diameter * scale);
            groups.put(matcher.group(1), group);
            return group;
        }
        throw new IllegalStateException("Missing definition for drill tool " + matcher.group(1));
    }
}
