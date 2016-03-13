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
package com.baremetalstudios.minicam.simulator;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPlotter implements Plotter {
    private final Logger logger = LoggerFactory.getLogger(AbstractPlotter.class);
    
    private ExposureMode exposure = SimulatorDefaults.EXPOSURE;
    private final EnumSet<PlotterMode> opts = SimulatorDefaults.OPTIONS;
    
    private FormatParser formatX = new FormatParser();
    private FormatParser formatY = new FormatParser();
    
    private final Map<Integer, Aperture> appertures = new HashMap<>();
    private final Map<String, ApertureMacro> definitions = new HashMap<>();

    public AbstractPlotter() {
    }

    @Override
    public void setExposure(ExposureMode mode) {
        exposure = mode;
    }
    
    public ExposureMode getExposure() {
        return exposure;
    }
    
    @Override
    public boolean askContinue(int beginLine, int i) {
        return false;
    }

    @Override
    public void setMode(PlotterMode mode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Setting mode " + mode);
        }
        opts.add(mode);
        
        if (mode == PlotterMode.IMPERIAL) {
            formatX.setModeImperial();
            formatY.setModeImperial();
        } else if (mode == PlotterMode.METRIC) {
            formatX.setModeMetric();
            formatY.setModeMetric();
        }

    }

    @Override
    public void resetMode(PlotterMode mode) {
        if (logger.isDebugEnabled()) {
            logger.debug("Resetting mode " + mode);
        }
        opts.remove(mode);
    }

    @Override
    public void comment(int beginLine, String image) {
        if (logger.isDebugEnabled()) {
            logger.debug("Comment " + beginLine + ", (" + image + ")");
        }
    }

    @Override
    public void addFlash() {
        if (logger.isDebugEnabled()) {
            logger.debug("Add flash");
        }
    }

    @Override
    public void setAperture(int parseInt) {
        //System.out.println("setAperture(" + parseInt + ")");
    }

    @Override
    public void setFormatX(int parseInt, int parseInt2) {
        formatX.setFormat(parseInt, parseInt2);
    }

    @Override
    public void setFormatY(int parseInt, int parseInt2) {
        formatY.setFormat(parseInt, parseInt2);
    }

    @Override
    public ApertureMacro getMacro(String id) {
        //System.out.println("getMacro(" + id + ")");
        return definitions.get(id);
    }

    @Override
    public void addAperture(Aperture aperture) {
        //System.out.println("addAperture(" + aperture + ")");
        appertures.put(aperture.getId(), aperture);
    }

    @Override
    public void addMacro(ApertureMacro macro) {
        //System.out.println("addMacro(" + macro + ")");
        definitions.put(macro.getName(), macro);
    }

    @Override
    public void selectAxis(String image, String image2) {
        
    }

    @Override
    public void setImagePolarity(String image) {
        
    }

    @Override
    public void setLayerPolarity(String image) {
        
    }

    @Override
    public void setOffset(String a, String b) {
        
    }

    @Override
    public void setScaleFactor(String a, String b) {
        
    }

    @Override
    public void stepAndRepeat(String x, String y, String i, String j) {
        
    }

    public FormatParser getFormatX() {
        return formatX;
    }

    public FormatParser getFormatY() {
        return formatY;
    }

    public EnumSet<PlotterMode> getOptions() {
        return opts;
    }
}
