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

public interface SimulatorDefaults {
    
//  public static final double ARC_RESOLUTION = Math.PI / 128.0;
//  public static final int MAX_FORMAT = 6;
//  // modes
//  public static final int RAPID = 0;
//  public static final int LINEAR = 1;
//  public static final int POLYGON = 2;
//  public static final int CW = 3;
//  public static final int CCW = 4;
//  public static final String[] MODE_NAME = new String[] { "rapid", "linear", "polygon", "cw", "ccw" };
//  // exposures
//  public static final int OPEN = 0;
//  public static final int CLOSED = 1;
//  public static final int FLASH = 2;
//  public static final String[] EXPOSURE_NAME = new String[] { "open", "closed", "flash" };
//  // defaults
//  public static final int DEF_MODE = LINEAR;
//  public static final Aperture DEF_APERTURE = null;
//  public static final int DEF_EXPOSURE = CLOSED;
//  public static final int DEF_X = 0;
//  public static final int DEF_Y = 0;
//  public static final int DEF_I = 0;
//  public static final int DEF_J = 0;
//  public static final int DEF_XSCALE = 10000;
//  public static final int DEF_YSCALE = 10000;
//  public static final boolean DEF_INCREMENTAL = false;
//  public static final boolean DEF_IGNORELEADING = true;
//  public static final boolean DEF_IGNORETRAILING = false;
//  public static final boolean DEF_METRIC = false;
//  public static final boolean DEF_ARC360 = false;
//  public static final int DEF_X_BEFORE = 2;
//  public static final int DEF_X_AFTER = 4;
//  public static final int DEF_Y_BEFORE = 2;
//  public static final int DEF_Y_AFTER = 4;
//  // Other variables
//  private Map<String, Macro> macros = new LinkedHashMap<String, Macro>();
//  private Map<Integer, Aperture> apertures = new LinkedHashMap<Integer, Aperture>();
//  private Map<Vertex, Vertex> vertices = new LinkedHashMap<Vertex, Vertex>();
//  private List<Action> actions = new LinkedList<Action>();
//  private int mode = DEF_MODE;
//  private Aperture aperture = DEF_APERTURE;
//  private int exposure = DEF_EXPOSURE;
//  private Vertex p = getVertex(DEF_X, DEF_Y);
//  private int ci = DEF_I;
//  private int cj = DEF_J;
//  private int xScale = DEF_XSCALE;
//  private int yScale = DEF_YSCALE;
//  private boolean incremental = DEF_INCREMENTAL;
//  private boolean ignoreTrailing = DEF_IGNORETRAILING;
//  private boolean metric = DEF_METRIC;
//  private boolean arc360 = DEF_ARC360;
//  private int xWidth = DEF_X_BEFORE + DEF_X_AFTER;
//  private int yWidth = DEF_Y_BEFORE + DEF_Y_AFTER;

    ExposureMode EXPOSURE = ExposureMode.CLOSED;
    EnumSet<PlotterMode> OPTIONS = EnumSet.of(PlotterMode.IMPERIAL, PlotterMode.LINEAR);

}
