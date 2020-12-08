## MiniCAM 1.3.0

This utility is intended for generation of G-Code programs for CNC mill for cutting board outline and drilling.
Unlike vast majority of the similar utilities it does not prepare anything for PCB track milling. Instead it is
targeted to preparation of the board for photoresist processing (optionally with through hole plating). So, it
takes outline Gerber and Excellon-like drill files and generates control file for CNC to prepare board(s) in
single step - mill outlines with routing tabs and then drill all holes.

Features:

- Single step processing - mill then drill and panel is ready for further processing, no need to install
  it to CNC again.
- Support for multiple boards at single panel (tested gerbers from GerbMerge output).
- Automatic tool diameter compensation.
- Automatic tab routing with "mouse bites" generation.
- Optional milling of inner outline polygons (for example, Eagle generates ones for large drills).
- Optional rotation and centering of the panel for the given blank board dimensions.
- Optimization of the drill tool path.
- Drill depth auto-adjustment.
- Optionally mill outlines twice.
- Optionally replace larger drills with milling. Threshold for such replacement is configurable.
- Configurable separate scaling by X and Y axes.

This utility is for metric world, sorry. I tried to accommodate myself to imperial units, but failed, and since
I wrote it mostly for myself, it uses metric units everywhere and converts imperial units into metric ones where
necessary.

## Notes:

### Drill Depth Automatic Adjustment
  Usually CNC is configured to have PCB surface at zero of Z coordinate. Configured drill depth in this case should
  be close to PCB thickness. Since drill point angle is not 180 degrees, different drills need different drill
  depth to fully penetrate the PCB. With fixed drill depth it is necessary to configure larger drill depth than
  necessary for small drill diameters. Automatic drill depth adjustment takes care of this and increases drill depth
  depending on the drill diameter.

### Double passing of outline
  After outline milling cuts often are filled with chips and it's quite complicated to remove chips from cuts.
  Issue is especially visible when small diameter mills are used. Second pass completely resolves this issue.

### Separate scaling by X and Y axes
  Separate scaling (and scaling in general) is necessary to achieve perfect match between photomask and PCB.
  Note that significant scaling (more than about few percents) can cause inability to insert through-hole components.

## TODO:
- continue work on optimization

__Change log__:

 _(1.3.0)_
- (fix) Switched to Java 11
- (fix) Updated dependencies and cleaned up code base
- (add) By default build generates native image

 _(1.2.0)_
  
- (fix) Fixed optimization of starting point of polygon cutting (sometimes one side of the polygon disappeared)
- (add) Significantly improved support for Excellon files (experimental)
- (add) Added drill depth auto-adjustment
- (add) Added configurable spindle startup delay
- (add) Added option for double passing outline
- (add) Added option for replacement of larger drills with mills
- (add) Added separate scaling by X and Y
- (add) Line separator in generated file is taken as the system default instead of plain '\n'

## Requirements:

### Build
- GraalVM 20.3.0

### Runtime
 none

## Building from sources
Once GraalVM is installed and configured, miniCAM can be built using following 
command:

```./mvnw clean package```

Built binary can be found in `./target` directory. 

## Usage

__Usage:__ `miniCAM <parameters>`

__Parameters:__
```
--outline=<outline file>       - Gerber with outline
--drill=<drill file>           - Excellon-like drill file
--config=<configuration file>  - configuration file (see below)
--output=<output file>         - Combined (mill+drill) output file (WARNING: it's silently overwrites existing file if it exists)
--output-drill=<output file>   - Output file for drilling (WARNING: it's silently overwrites existing file if it exists)
--output-outline=<output file> - Output file for milling (WARNING: it's silently overwrites existing file if it exists)
```

Configuration file is a simple text file consisting of pairs `<variable> = <value>`. Leading '#' marks start
of the comment. Note that in-line comments (in same line as variable) are not supported.
Sample configuration file is included in distribution package.

Following variables are recognized (all measurements are in millimeters or mm/min):

General configuration variables:
```
    config.free.move.feed.rate         - Speed (in mm/min) of free move of spindle.
    config.tool.change.z               - How high spindle should be moved before tool change.
    config.spindle.speed               - Cutting/drilling speed (in RPM).
    config.spindle.startup.delay       - Time (in seconds) between spindle start command and first move. Necessary to let spindle
                                         reach configured speed.
    config.drills.diameter.step        - Drill diameter "grid" step. Default of 0.1mm means that all drills will be "snapped" to
                                         closest diameters such as 0.2mm/0.3mm/0.4mm/etc. If you have drill bits with finer
                                         granularity (for example, 0.05mm), set this parameter accordingly.
    config.optimization.level          - Level of the optimization of the drill tool path. Must be in range 0-9 (0 - optimization
                                         is disabled, 9 - maximal optimization).
    config.drills.adjust.depth         - Enable/disable (true/false) adjustment of drill depth depending on drill diameter.
    config.outline.double.pass         - Generate two passes for outline. It might be necessary to make cuts clean, especially if
                                         small diameter mill is used.
	config.mill.large.drills           - Enable/disable replacing of larger drills with mills.
    config.mill.large.drills.threshold - Threshold for drills-to-mills replacement. All drills with diameter greater or equal
                                         to threshold will be replaced with milling operation.
    config.scale.x                     - Scaling factor for X axis. Scaling is specified as number (not in percents!).
    config.scale.y                     - Scaling factor for Y axis. Scaling is specified as number (not in percents!).
```

Output control:
```
    output.generate.inner.cut - Generate G-code for inner outlines (inner board holes/slots).
                                Cutter diameter compensation works correctly for such outlines.
    output.board.height       - blank board height
    output.board.width        - blank board width
    output.board.center.panel - enable centering of the panel on the blank board
    output.board.rotate.panel - enable rotation of the panel. Rotation tries to match direction of the blank board
                                (i.e. longer dimension of the panel will match longer dimension of the blank).
```
Tab routing parameters (NOTE: tabs are placed only at horizontal and vertical edges of the board outline!):
```
	tab.drill.diameter   - diameter of the holes drilled in breakaway tab. This diameter also defines "safety border" around
	                       the board (half of the drill diameter is added to the cutter radius during calculation of tool path).
	tab.width            - actual tab width after milling
	tab.minimal.distance - minimal distance between tabs. If board edge is shorter than about twice of this distance, no tabs
	                       will be generated. This parameter is used for calculation of number and position of the tabs.
```
Cutting parameters:
```
	cut.cutter.diameter - mill bit diameter. Half of this value (i.e. cutter radius) is used to calculate tool path.
	cut.feed.rate       - feed rate during milling
	cut.z               - depth of the milling (if CNC is configured to have board surface to be at Z = 0, this value will
	                      have negative value.
	cut.safe.z          - height of the tool end when tool can safely move over PCB without cutting
```
Drilling parameters:
```
	drill.z              - depth of the drilling (similar to cut.z, see comments about it above)
	drill.safe.z         - see (cut.safe.z) comment, used for drilling cycles.
```
