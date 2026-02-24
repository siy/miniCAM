## MiniCAM 2.0.0

G-Code generator for CNC mill PCB outline cutting and drilling. Takes Gerber outline and Excellon drill files, produces G-Code to prepare boards for photoresist processing in a single step: mill outlines with routing tabs, then drill all holes.

### Features

- **Single-step processing** -- mill outlines then drill holes, no re-clamping needed
- **Multi-board panels** -- supports GerbMerge output
- **Automatic tool diameter compensation**
- **Tab routing with mouse bites** -- automatic placement along horizontal/vertical edges
- **Inner outline milling** -- optional support for inner polygons (e.g. Eagle large drill slots)
- **Panel rotation and centering** -- fits panel to blank board dimensions
- **Drill path optimization** -- configurable 0-9 levels
- **Drill depth auto-adjustment** -- compensates for drill point angle by diameter
- **Double-pass outline** -- optional second pass for clean cuts
- **Drill-to-mill replacement** -- configurable threshold for large holes
- **Separate X/Y scaling** -- fine-tune photomask-to-PCB alignment
- **Metric-first** -- converts imperial units where necessary

### Requirements

**Build:** JDK 25+, Maven 3.9+. For native binary: GraalVM 25+.

**Runtime:** None (native binary is self-contained).

### Building from sources

```
mvn clean package
```

The native binary is generated in `./target/miniCAM`. To skip native image and build JAR only:

```
mvn clean package -DskipNativeBuild=true
```

### Pre-built binaries

Download native binaries for Linux, macOS, and Windows from the [Releases](../../releases) page. Binaries are automatically built and published on each tagged release.

### Usage

```
miniCAM --outline=<gerber file> --drill=<excellon file> --config=<config file> \
        --output=<combined gcode>
```

Or generate separate mill/drill files:

```
miniCAM --outline=<gerber file> --drill=<excellon file> --config=<config file> \
        --output-mill=<mill gcode> --output-drill=<drill gcode>
```

### Configuration

Configuration is a text file with `variable = value` pairs. Lines starting with `#` are comments.
A sample configuration file (`minicam.cfg`) is included.

All measurements are in millimeters or mm/min.

#### General

| Variable | Description | Default |
|----------|-------------|---------|
| `config.free.move.feed.rate` | Free move speed (mm/min) | 400 |
| `config.tool.change.z` | Tool change retract height (mm) | 55 |
| `config.spindle.speed` | Spindle speed (RPM) | 60000 |
| `config.spindle.startup.delay` | Delay after spindle start (seconds) | 5 |
| `config.optimization.level` | Drill path optimization 0-9 | 5 |
| `config.drills.diameter.step` | Drill diameter snap grid (mm) | 0.1 |
| `config.drills.adjust.depth` | Auto-adjust drill depth by diameter | true |
| `config.outline.double.pass` | Mill outline twice for clean cuts | false |
| `config.mill.large.drills` | Replace large drills with milling | false |
| `config.mill.large.drills.threshold` | Diameter threshold for drill-to-mill (mm) | 2.0 |
| `config.scale.x` | X-axis scale factor | 1.0 |
| `config.scale.y` | Y-axis scale factor | 1.0 |

#### Output control

| Variable | Description | Default |
|----------|-------------|---------|
| `output.board.height` | Blank board height (mm) | 200 |
| `output.board.width` | Blank board width (mm) | 160 |
| `output.board.center.panel` | Center panel on blank board | true |
| `output.board.rotate.panel` | Rotate panel to match blank orientation | true |
| `output.generate.inner.cut` | Generate G-Code for inner outlines | false |

#### Tab routing

Tabs are placed only along horizontal and vertical edges.

| Variable | Description | Default |
|----------|-------------|---------|
| `tab.drill.diameter` | Mouse bite hole diameter (mm) | 0.5 |
| `tab.width` | Tab width after milling (mm) | 5 |
| `tab.minimal.distance` | Minimum distance between tabs (mm) | 16 |

#### Cutting

| Variable | Description | Default |
|----------|-------------|---------|
| `cut.cutter.diameter` | Mill bit diameter (mm) | 1.6 |
| `cut.feed.rate` | Milling feed rate (mm/min) | 150 |
| `cut.z` | Milling depth (mm, negative) | -2.2 |
| `cut.safe.z` | Safe Z height for rapid moves (mm) | 6.0 |

#### Drilling

| Variable | Description | Default |
|----------|-------------|---------|
| `drill.z` | Drilling depth (mm, negative) | -2.2 |
| `drill.safe.z` | Safe Z height for drill rapid moves (mm) | 5.0 |

### Notes

**Drill depth auto-adjustment:** Different drill diameters need different depths to fully penetrate the PCB due to the drill point angle. This feature automatically increases depth for smaller drills.

**Double-pass outline:** Chips often fill cuts during milling, especially with small diameter mills. A second pass cleans the cuts completely.

**Separate X/Y scaling:** Compensates for dimensional differences between photomask and PCB. Significant scaling (more than a few percent) can prevent through-hole component insertion.

### Changelog

#### 2.0.0

- Modernized to Java 25 (from Java 11)
- Updated GraalVM native image build to `org.graalvm.buildtools:native-maven-plugin` 0.11.4
- Migrated tests from JUnit 4 to JUnit 5.14.3
- Updated Log4j to 2.25.3
- Updated all Maven plugins to latest versions
- Added JaCoCo code coverage (85%+ excluding generated parser code)
- Added GitHub Actions CI (build + test on push/PR)
- Added GitHub Actions release workflow (native binaries for Linux, macOS, Windows on tag push)
- Removed Maven wrapper (use system Maven)

#### 1.3.0

- Switched to Java 11
- Updated dependencies and cleaned up code base
- Build generates native image by default

#### 1.2.0

- Fixed optimization of polygon cutting start point
- Improved Excellon file support (experimental)
- Added drill depth auto-adjustment
- Added configurable spindle startup delay
- Added double-pass outline option
- Added drill-to-mill replacement option
- Added separate X/Y scaling
- Line separator uses system default

### License

Apache License 2.0
