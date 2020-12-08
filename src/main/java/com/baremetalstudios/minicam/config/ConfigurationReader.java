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
package com.baremetalstudios.minicam.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigurationReader {
    private static final Pattern VAR_PATTERN = Pattern.compile("([\\w\\.]+)(\\s*)?=(\\s*)?(.+)");

    private ConfigurationReader() {
    }

    public static OutputConfig readConfig(BufferedReader configReader) throws IOException {
        OutputConfig config = new OutputConfig();

        String line = null;

        while ((line = configReader.readLine()) != null) {
            line = line.trim();
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }

            NamedVar<String> var = parseLine(line);

            if (var == null) {
                continue;
            }

            String result = config.putRaw(var);

            if (result != null) {
                System.out.format("Error: %s %s %s, variable ignored", result, var.getName(), var.getValue());
            }
        }

        return config;
    }

    public static NamedVar<String> parseLine(String line) {
        Matcher matcher = VAR_PATTERN.matcher(line);
        matcher.find();

        if (!matcher.matches() || matcher.groupCount() != 4) {
            System.out.println("Config line \""+ line +"\" is ignored");
            return null;
        }

        return new NamedVar<String>(matcher.group(1), matcher.group(4));
    }
}
