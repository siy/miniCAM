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
package com.baremetalstudios.minicam;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

import com.baremetalstudios.minicam.config.ConfigurationReader;
import com.baremetalstudios.minicam.config.OutputConfig;
import com.baremetalstudios.minicam.geometry.DrillGroup;
import com.baremetalstudios.minicam.geometry.DrillStatistics;
import com.baremetalstudios.minicam.geometry.Panel;
import com.baremetalstudios.minicam.geometry.TransformationStatus;
import com.baremetalstudios.minicam.parser.ExcellonParser;
import com.baremetalstudios.minicam.parser.ParseException;
import com.baremetalstudios.minicam.parser.Parser;
import com.baremetalstudios.minicam.processor.DrillProcessor;
import com.baremetalstudios.minicam.processor.OutputGenerator;
import com.baremetalstudios.minicam.processor.PolygonProcessor;
import com.baremetalstudios.minicam.simulator.OutlinePlotter;

public class MiniCAM {
    private FileInputStream drillStream;
    private FileInputStream outlineStream;
    private BufferedReader configReader;
    private PrintStream out;
    private PrintStream outMill;
    private PrintStream outDrill;
    private OutputConfig config;

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        FileBundle fileBundle = parseArgs(args);
        new MiniCAM().runProcessing(fileBundle);
    }

    private static FileBundle parseArgs(String[] args) {
        FileBundle fileBundle = new FileBundle();

        if (args.length == 0) {
            usage();
            System.exit(-2);
        }

        for (String string : args) {
            File file;
            file = checkParam(string, "--outline=");
            if (file != null) {
                fileBundle.setInFile(file);
            }
            file = checkParam(string, "--drill=");
            if (file != null) {
                fileBundle.setDrillFile(file);
            }

            file = checkParam(string, "--config=");
            if (file != null) {
                fileBundle.setConfigFile(file);
            }

            file = checkParam(string, "--output=");
            if (file != null) {
                fileBundle.setOutFile(file);
            }

            file = checkParam(string, "--output-mill=");
            if (file != null) {
                fileBundle.setOutMillFile(file);
            }

            file = checkParam(string, "--output-drill=");
            if (file != null) {
                fileBundle.setOutDrillFile(file);
            }
        }

        if (!fileBundle.isValid()) {
            System.err.println("Required parameter is missing");
            System.exit(-1);
        }

        return fileBundle;
    }

    private static void usage() {
        System.out.println("Usage: miniCAM --outline=<outline file> --drill=<drill file> --config=<configuration file> --output=<output file> --output-mill=<output file> --output-drill=<output file>\n");
    }

    private static File checkParam(String string, String prefix) {
        if (string.startsWith(prefix)) {
            return new File(string.substring(prefix.length()));
        }
        return null;
    }

    private void runProcessing(FileBundle fileBundle) {

        try {
            banner();
            loadConfiguration(fileBundle);

            System.out.println("Loading drills from " + fileBundle.getDrillFile().getCanonicalPath() + " ...");
            drillStream = new FileInputStream(fileBundle.getDrillFile().getCanonicalFile());
            List<DrillGroup> drills = new ExcellonParser(drillStream).parse();

            outlineStream = new FileInputStream(fileBundle.getInFile().getCanonicalFile());
            System.out.println("Loading outline(s) from " + fileBundle.getInFile().getAbsolutePath() + " ...");
            Parser parser = new Parser(outlineStream);

            OutlinePlotter simulator = new OutlinePlotter();
            parser.setSimulator(simulator);
            parser.Input();
            
            Panel panel = new Panel(simulator.getPolygons(), drills);
            
            System.out.println("Optimizing drill tool path...");
            DrillStatistics before = panel.process(new PolygonProcessor(config), new DrillProcessor(config));

            TransformationStatus status = panel.rotateAndCenter(config);

            if (fileBundle.getOutFile() != null) {
                out = new PrintStream(fileBundle.getOutFile());
                panel.generate(new OutputGenerator(out, config));
            }
            if (fileBundle.getOutDrillFile() != null) {
                outDrill = new PrintStream(fileBundle.getOutDrillFile());
                panel.generateDrills(new OutputGenerator(outDrill, config));
            }
            if (fileBundle.getOutMillFile() != null) {
                outMill = new PrintStream(fileBundle.getOutMillFile());
                panel.generateMills(new OutputGenerator(outMill, config));
            }

            System.out.println();
            System.out.println("Before optimization:");
            System.out.println(before);
            System.out.println(panel.getStats());
            System.out.format("Panel is %srotated\n", status.isRotated() ? "": "not ");
            System.out.format("Panel is %scentered\n", status.isCentered() ? "": "not ");

        } catch (ParseException e) {
            System.err.println("Error during input parsing " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during read/write " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void loadConfiguration(FileBundle fileBundle) throws IOException, FileNotFoundException {
        System.out.println("Loading configuration from " + fileBundle.getConfigFile().getCanonicalPath() + " ...");
        configReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileBundle.getConfigFile().getCanonicalFile())));
        config = ConfigurationReader.readConfig(configReader);
    }

    private void banner() {
        System.out.println("MiniCAM 1.2.0");
        System.out.println();
    }

    private void cleanup() {
        close(drillStream);
        close(outlineStream);
        close(configReader);
        close(out);
        close(outDrill);
        close(outMill);
    }

    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }

}
