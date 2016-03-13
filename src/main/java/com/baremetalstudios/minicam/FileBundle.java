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
package com.baremetalstudios.minicam;

import java.io.File;

public class FileBundle {
    private File inFile;
    private File drillFile;
    private File outFile;
    private File configFile;
    private File outMillFile;
    private File outDrillFile;

    public File getInFile() {
        return inFile;
    }
    public void setInFile(File infile) {
        this.inFile = infile;
    }
    public File getDrillFile() {
        return drillFile;
    }
    public void setDrillFile(File drillfile) {
        this.drillFile = drillfile;
    }
    public File getOutFile() {
        return outFile;
    }
    public void setOutFile(File outfile) {
        this.outFile = outfile;
    }
    public File getConfigFile() {
        return configFile;
    }
    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public boolean isValid() {
        return notNull(inFile, drillFile, configFile) && (outFile != null || notNull(outDrillFile, outMillFile));
    }

    public File getOutMillFile() {
        return outMillFile;
    }
    public void setOutMillFile(File outMillFile) {
        this.outMillFile = outMillFile;
    }
    public File getOutDrillFile() {
        return outDrillFile;
    }
    public void setOutDrillFile(File outDrillFile) {
        this.outDrillFile = outDrillFile;
    }

    public boolean isSeparateMode() {
        return outFile == null;
    }

    @SafeVarargs
    private static<T> boolean notNull(T ... objs) {
        for (T t : objs) {
            if (t == null) {
                return false;
            }
        }
        return true;
    }
}