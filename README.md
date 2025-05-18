## Java Gradient Noise

**Introduction:**

Java Gradient Noise, Java Noise, ``noise-lib``, or ``Noise`` is a pure-Java library designed for generating **gradient noise**. Specifically, **Perlin** and **Simplex noise**, essential tools for procedural content generation in games, simulations, and graphics. It aims to be a Java equivalent of the popular Python ``noise`` package (https://github.com/caseman/noise), offering functionalities for creating 1D, 2D, 3D, and 4D noise.

**Core Features:**

- **Perlin Noise:** Implements Ken Perlin's "Improved Noise" algorithm, a versatile gradient noise function widely used for natural-looking textures and patterns.
  - Available in 1D (``pnoise1``), 2D (``pnoise2``), and 3D (``pnoise3``).
- **Simplex Noise:** Provides Ken Perlin's Simplex noise algorithm, which offers similar results to Perlin noise but with better performance characteristics and fewer directional artifacts, especially in higher dimensions.
  - Available in 2D (``snoise2``), 3D (``snoise3``), and 4D (``snoise4``).
- **Fractal Brownian Motion (fBm):** Both Perlin and Simplex noise functions support summing multiple scaled-down versions of themselves (octaves) to create more detailed and complex noise, often referred to as fBm.
- **Customization:**
  - **Octaves:** Control the level of detail in the noise.
  - **Persistence & Lacunarity:** (Primarily for Perlin noise via the main Noise facade) Fine-tune how the amplitude and frequency of noise change across octaves.
  - **Seeding/Base:** The Perlin noise functions in the Noise facade accept a base parameter, which shifts the permutation table lookups, effectively acting as a seed to produce different noise patterns. For more direct seed control using custom ``PermutationTable`` instances, you can use the ``com.yousefonweb.noise.PerlinNoise`` and ``com.yousefonweb.noise.SimplexNoise`` classes directly. The ``Noise.createPermutationTable()`` methods allow you to generate these tables.
- **Tileable Noise:** Specific 3D Perlin noise functions are available for generating **seamlessly tiling** noise patterns, crucial for textures that repeat.
- **GLSL Shader Support**: Generate and consume noise textures on the GPU
  - The library includes utilities to generate 3D noise textures (``ShaderNoiseTexture``) that are optimized for use in GLSL shaders.
  - Pre-written GLSL functions (``ShaderNoiseResources.SHADER_NOISE_GLSL``) are provided to efficiently sample these textures and compute noise on the GPU. This part of the library relies on LWJGL for OpenGL interoperation.

The primary entry point for most common -CPU- noise generation tasks is the ``com.yousefonweb.noise.Noise`` class, which provides static methods for easy access to various noise functions.

Let's quickly re-iterate over what the library provides:

- **Simplex noise** (2D, 3D, 4D)
  - Via `Noise.snoise2(x,y[,octaves])`, `Noise.snoise3(x,y,z[,octaves])`, `Noise.snoise4(x,y,z,w[,octaves])` 
- **Perlin (improved) noise** (1D, 2D, 3D, tileable 3D)
  - Via `Noise.pnoise1(x[,octaves[,persistence,lacunarity,base]])`
  - `Noise.pnoise2(x,y[,octaves[,…]])`
  - `Noise.pnoise3(x,y,z[,octaves[,…]])` and a tiling variant `pnoise3(x,y,z,repeatX,repeatY,repeatZ,base)` 
- **Custom seeding** via `Noise.createPermutationTable(int period)` or `.createPermutationTable(int[] customTable)`
- A `PermutationTable` class (periodic or custom arrays) for more control.
- **Shader support** under `com.yousefonweb.noise.gl` and `com.yousefonweb.noise.shader` to generate and consume noise textures on the GPU.

All functions output values normalized to approximately [-1, 1].

## Get Started

This guide will walk you through setting up a minimal Java Maven project to use the Java Noise Package and demonstrate its basic functionalities with simple Swing examples.

## Prerequisites

1. **Java Development Kit (JDK):** Version 17 or newer is recommended, as the library (specifically ``ShaderNoiseResources.java`` with its text block) and this guide assume modern Java features.

2. **Apache Maven:** A build automation tool. Ensure it's installed and configured in your system's PATH.

3. **The Java Noise Library (java_gradient_noise):** You need to have ``java_gradient_noise`` itself. Since it's not on a public Maven repository, you'll typically build it from its source and install it into your local Maven repository. To do this, navigate to the root directory of the java_gradient_noise (where its pom.xml is located) and run:

   ```
   mvn clean install
   ```

   This will make the library available for other local Maven projects on your machine. The library's Maven coordinates are assumed to be:

   - GroupId: ``com.yousefonweb.noise``
   - ArtifactId: ``noise-lib``
   - Version: ``1.0.0``

## 1. Create a New Maven Project

Open your terminal or command prompt (your favorite shell) and run the following command to generate a new Maven project for the noise demo. **Choose the command appropriate for your shell**:

- **Bash/Zsh (Linux/macOS):**

  ```bash
  mvn archetype:generate \
    -DgroupId=com.example.noisedemo \
    -DartifactId=java-noise-demo \
    -Dpackage=com.example.noisedemo \
    -DarchetypeGroupId=org.apache.maven.archetypes \
    -DarchetypeArtifactId=maven-archetype-quickstart \
    -DarchetypeVersion=1.4 \
    -DinteractiveMode=false
  ```

- **CMD (Windows):**

  ```bat
  mvn archetype:generate ^
    -DgroupId=com.example.noisedemo ^
    -DartifactId=java-noise-demo ^
    -Dpackage=com.example.noisedemo ^
    -DarchetypeGroupId=org.apache.maven.archetypes ^
    -DarchetypeArtifactId=maven-archetype-quickstart ^
    -DarchetypeVersion=1.4 ^
    -DinteractiveMode=false
  ```

- **PowerShell (Windows):**

  ```powershell
  mvn archetype:generate `
    "-DgroupId=com.example.noisedemo" `
    "-DartifactId=java-noise-demo" `
    "-Dpackage=com.example.noisedemo" `
    "-DarchetypeGroupId=org.apache.maven.archetypes" `
    "-DarchetypeArtifactId=maven-archetype-quickstart" `
    "-DarchetypeVersion=1.4" `
    "-DinteractiveMode=false"
  ```

This will create a directory named ``java-noise-demo`` and generate inside it a brand-new, standard Maven‐based Java project.
The directory layout of the generated project is like this (typical directory layout of a Maven project):

```
└───java-noise-demo
    │   pom.xml
    │
    └───src
        ├───main
        │   └───java
        │       └───com
        │           └───example
        │               └───noisedemo
        │                       App.java
        │
        └───test
            └───java
                └───com
                    └───example
                        └───noisedemo
                                AppTest.java
```

Navigate (cd) into this directory:

```
cd java-noise-demo
```

## 2. Add Noise Library as a Dependency to ``pom.xml`` of  Your Project

Open the ``pom.xml`` file in the ``java-noise-demo`` directory.
Make sure your project is configured to use Java >= 17. Add or update the following build properties to make them look like this:

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

Now, add Java Noise as a dependency within the ``<dependencies>`` section:

```xml
<dependencies>
    <dependency>
        <groupId>junit</groupId> <!-- This is from the archetype, keep or update as needed -->
        <artifactId>junit</artifactId>
        <version>4.11</version>
        <scope>test</scope>
    </dependency>
    <!-- Add the Java Noise Package dependency -->
    <dependency>
        <groupId>com.yousefonweb.noise</groupId>
        <artifactId>noise-lib</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

(Note: The noise package itself uses LWJGL for its gl package. When you add com.yousefonweb:noise as a dependency, Maven should pull in LWJGL transitively since it's declared as a regular dependency in the noise library's ``pom.xml``. The CPU-based noise demos below do not directly require LWJGL setup in your demo project, but running code that uses the ``gl`` package from the noise library would.)

## 3. ``noise`` is Now Ready To Be Used in The Project!

Given you have created a Maven project using the "quickstart" archetype, the ``App.java`` file (e.g., located at ``src/main/java/com/example/noisedemo/App.java``) will initially contain a simple "Hello World!" program that looks like this:
```java
package com.example.noisedemo;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
    }
}
```

If you know what you are doing, you can now start working on your project that uses the ``noise`` package.

If you want to first try/see examples of the noise package in use, **replace the content of the above ``App.java`` with the following:**
(the following is a quick-and-dirty, but a bit comprehensive and good demonstration of the ``noise`` package. It consists of eight different demos for eight different features.)

```java
package com.example.noisedemo;

import com.yousefonweb.noise.Noise;
import com.yousefonweb.noise.shader.ShaderNoiseTexture; // For ShaderNoiseTexture demo

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.function.Consumer;

// Main application class
public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Choose which demo to run by uncommenting one:
            // createAndShowPerlin1DDemo();
            // createAndShowPerlin2DDemo();
            createAndShowPerlin3DDemo();
            // createAndShowPerlin3DTileableDemo();
            // createAndShowSimplex2DDemo();
            // createAndShowSimplex3DDemo();
            // createAndShowSimplex4DDemo();
            // createAndShowShaderNoiseTextureDemo(); // Uses its own panel
        });
    }

    private static void setupFrame(String title, JPanel mainPanel, JPanel controlPanel) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);
        if (controlPanel != null) {
            frame.add(controlPanel, BorderLayout.SOUTH);
        }
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // --- Demo Launchers ---
    private static void createAndShowPerlin1DDemo() {
        VisualizationConfig config = new VisualizationConfig("1D Perlin Noise", NoiseAlgorithm.PERLIN, 1);
        config.enableOctaves(1, 8, 1);
        config.enablePersistence(0, 100, 50); // 0.0 to 1.0
        config.enableLacunarity(100, 400, 200); // 1.0 to 4.0
        config.enableBase(0, 255, 0);
        config.enableScale(1, 200, 20); // Affects density along X
        
        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 400, 200);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }

    private static void createAndShowPerlin2DDemo() {
        VisualizationConfig config = new VisualizationConfig("2D Perlin Noise", NoiseAlgorithm.PERLIN, 2);
        config.enableOctaves(1, 8, 1);
        config.enablePersistence(0, 100, 50);
        config.enableLacunarity(100, 400, 200);
        config.enableBase(0, 255, 0);
        config.enableScale(1, 200, 50); // scale for x, y

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }

    private static void createAndShowPerlin3DDemo() {
        VisualizationConfig config = new VisualizationConfig("3D Perlin Noise (XY Slice)", NoiseAlgorithm.PERLIN, 3);
        config.enableOctaves(1, 8, 1);
        config.enablePersistence(0, 100, 50);
        config.enableLacunarity(100, 400, 200);
        config.enableBase(0, 255, 0);
        config.enableScale(1, 200, 50);
        config.enableZSlice(0, 200, 0); // Z-coordinate for the slice

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }
    
    private static void createAndShowPerlin3DTileableDemo() {
        VisualizationConfig config = new VisualizationConfig("3D Tileable Perlin Noise (XY Slice, 1 Octave)", NoiseAlgorithm.PERLIN_TILEABLE_3D, 3);
        // This specific facade method implies 1 octave, default persistence/lacunarity
        config.enableBase(0, 255, 0);
        config.enableScale(1, 200, 50);
        config.enableZSlice(0, 200, 0);
        config.enableRepeat(1, 64, 16); // Repeat period for tiling

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }

    private static void createAndShowSimplex2DDemo() {
        VisualizationConfig config = new VisualizationConfig("2D Simplex Noise", NoiseAlgorithm.SIMPLEX, 2);
        config.enableOctaves(1, 8, 1); // Persistence & Lacunarity are fixed by Noise facade
        config.enableScale(1, 200, 50);
        // Note: Noise.snoise* facade methods do not take base/persistence/lacunarity directly.
        // For full control, use SimplexNoise class directly with a custom PermutationTable.

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }

    private static void createAndShowSimplex3DDemo() {
        VisualizationConfig config = new VisualizationConfig("3D Simplex Noise (XY Slice)", NoiseAlgorithm.SIMPLEX, 3);
        config.enableOctaves(1, 8, 1);
        config.enableScale(1, 200, 50);
        config.enableZSlice(0, 200, 0);

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }

    private static void createAndShowSimplex4DDemo() {
        VisualizationConfig config = new VisualizationConfig("4D Simplex Noise (XY Slice)", NoiseAlgorithm.SIMPLEX, 4);
        config.enableOctaves(1, 8, 1);
        config.enableScale(1, 200, 50);
        config.enableZSlice(0, 200, 0); // z-coordinate
        config.enableWSlice(0, 200, 0); // w-coordinate

        NoiseDisplayPanel displayPanel = new NoiseDisplayPanel(config, 300, 300);
        NoiseControlPanel controlPanel = new NoiseControlPanel(config, displayPanel::updateNoise);
        setupFrame(config.title, displayPanel, controlPanel);
    }
    
    private static void createAndShowShaderNoiseTextureDemo() {
        // This demo uses its own specific panel setup
        ShaderNoiseTextureVisualizerPanel panel = new ShaderNoiseTextureVisualizerPanel(64, 0); // width 64, z-slice 0
        setupFrame("ShaderNoiseTexture Data (Slice of R-Channel)", panel, panel.getControlPanel());
    }
}

enum NoiseAlgorithm { PERLIN, SIMPLEX, PERLIN_TILEABLE_3D }

class VisualizationConfig {
    String title;
    NoiseAlgorithm algorithm;
    int dimensions;

    // Parameter values
    public int octavesVal = 1;
    public double persistenceVal = 0.5;
    public double lacunarityVal = 2.0;
    public int baseVal = 0;
    public double scaleVal = 0.1;
    public double zSliceVal = 0.0;
    public double wSliceVal = 0.0;
    public int repeatVal = 16;


    // Flags to enable/disable controls
    boolean octavesEnabled, persistenceEnabled, lacunarityEnabled, baseEnabled, 
            scaleEnabled, zSliceEnabled, wSliceEnabled, repeatEnabled;

    public VisualizationConfig(String title, NoiseAlgorithm algorithm, int dimensions) {
        this.title = title;
        this.algorithm = algorithm;
        this.dimensions = dimensions;
    }

    public void enableOctaves(int min, int max, int initial) { octavesEnabled = true; octavesVal = initial; }
    public void enablePersistence(int min, int max, int initial) { persistenceEnabled = true; persistenceVal = initial / 100.0; } // Slider 0-100
    public void enableLacunarity(int min, int max, int initial) { lacunarityEnabled = true; lacunarityVal = initial / 100.0; } // Slider 100-400 for 1.0-4.0
    public void enableBase(int min, int max, int initial) { baseEnabled = true; baseVal = initial; }
    public void enableScale(int min, int max, int initial) { scaleEnabled = true; scaleVal = initial / 100.0; } // Slider for 0.01 steps
    public void enableZSlice(int min, int max, int initial) { zSliceEnabled = true; zSliceVal = initial / 100.0; }
    public void enableWSlice(int min, int max, int initial) { wSliceEnabled = true; wSliceVal = initial / 100.0; }
    public void enableRepeat(int min, int max, int initial) { repeatEnabled = true; repeatVal = initial; }
}

class NoiseControlPanel extends JPanel {
    private VisualizationConfig config;
    private Consumer<VisualizationConfig> onConfigChange;
    private List<JSlider> sliders = new ArrayList<>();

    public NoiseControlPanel(VisualizationConfig config, Consumer<VisualizationConfig> onConfigChange) {
        this.config = config;
        this.onConfigChange = onConfigChange;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        
        // Slider scaling factors
        final int HUNDRED = 100;

        if (config.scaleEnabled) {
            addSlider("Scale:", 1, 200, (int)(config.scaleVal * HUNDRED), HUNDRED, 
                val -> config.scaleVal = val.doubleValue()); 
        }
        if (config.octavesEnabled) {
            addSlider("Octaves:", 1, 8, config.octavesVal, 1, 
                val -> config.octavesVal = val.intValue()); 
        }
        if (config.persistenceEnabled && config.algorithm == NoiseAlgorithm.PERLIN) {
            addSlider("Persistence:", 0, HUNDRED, (int)(config.persistenceVal * HUNDRED), HUNDRED, 
                val -> config.persistenceVal = val.doubleValue()); 
        }
        if (config.lacunarityEnabled && config.algorithm == NoiseAlgorithm.PERLIN) {
            addSlider("Lacunarity:", HUNDRED, 4 * HUNDRED, (int)(config.lacunarityVal * HUNDRED), HUNDRED, 
                val -> config.lacunarityVal = val.doubleValue()); 
        }
        if (config.baseEnabled && (config.algorithm == NoiseAlgorithm.PERLIN || config.algorithm == NoiseAlgorithm.PERLIN_TILEABLE_3D)) {
            addSlider("Base (Seed):", 0, 255, config.baseVal, 1, 
                val -> config.baseVal = val.intValue()); 
        }
        if (config.zSliceEnabled && (config.dimensions == 3 || config.dimensions == 4 || config.algorithm == NoiseAlgorithm.PERLIN_TILEABLE_3D)) {
            addSlider("Z-Slice:", 0, 200, (int)(config.zSliceVal * HUNDRED), HUNDRED, 
                val -> config.zSliceVal = val.doubleValue()); 
        }
        if (config.wSliceEnabled && config.dimensions == 4) {
            addSlider("W-Slice:", 0, 200, (int)(config.wSliceVal * HUNDRED), HUNDRED, 
                val -> config.wSliceVal = val.doubleValue()); 
        }
        if (config.repeatEnabled && config.algorithm == NoiseAlgorithm.PERLIN_TILEABLE_3D) {
            addSlider("Repeat Period:", 1, 64, config.repeatVal, 1, 
                val -> config.repeatVal = val.intValue()); 
        }

        if (config.algorithm == NoiseAlgorithm.SIMPLEX) {
             JLabel infoLabel = new JLabel("<html><small><i>Simplex via Noise facade uses fixed<br>Persistence (0.5) & Lacunarity (2.0), and no 'base' seed.</i></small></html>");
             add(infoLabel);
        }
    }

    private void addSlider(String label, int min, int max, int initial, int scaleFactor, Consumer<Double> valueSetter) {
        JPanel panel = new JPanel(new BorderLayout(5,0));
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, jLabel.getPreferredSize().height));
        panel.add(jLabel, BorderLayout.WEST);
        
        JSlider slider = new JSlider(min, max, initial);
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(min, new JLabel(String.format("%.2f", (double)min/scaleFactor)));
        labelTable.put(max, new JLabel(String.format("%.2f", (double)max/scaleFactor)));
        if (min < 0 && max > 0 && 0 >=min && 0 <= max) labelTable.put(0, new JLabel(String.format("%.2f",0.0)));
        else if ( (min+max)/2 != min && (min+max)/2 != max) labelTable.put( (min+max)/2, new JLabel(String.format("%.2f", (double)(min+max)/(2*scaleFactor))));
        slider.setLabelTable(labelTable);
        slider.setPaintLabels(true);
        
        slider.addChangeListener(e -> {
            valueSetter.accept((double)slider.getValue() / scaleFactor); 
            onConfigChange.accept(config);
        });
        sliders.add(slider);
        panel.add(slider, BorderLayout.CENTER);
        add(panel);
    }
}

class NoiseDisplayPanel extends JPanel {
    private VisualizationConfig config;
    private BufferedImage image; // For 2D noise
    private int panelWidth, panelHeight;

    public NoiseDisplayPanel(VisualizationConfig config, int width, int height) {
        this.config = config;
        this.panelWidth = width;
        this.panelHeight = height;
        setPreferredSize(new Dimension(panelWidth, panelHeight));
        if (config.dimensions > 1 || config.algorithm == NoiseAlgorithm.PERLIN_TILEABLE_3D) { // 2D, 3D, 4D are image based
            this.image = new BufferedImage(panelWidth, panelHeight, BufferedImage.TYPE_INT_RGB);
        }
        updateNoise(config);
    }

    public void updateNoise(VisualizationConfig newConfig) {
        this.config = newConfig; // Update internal config reference if changed by controls

        if (config.dimensions == 1 && config.algorithm == NoiseAlgorithm.PERLIN) {
            // For 1D, we don't use the BufferedImage, paint directly
        } else { // 2D, 3D slice, 4D slice
            for (int y = 0; y < panelHeight; y++) {
                for (int x = 0; x < panelWidth; x++) {
                    double px = x * config.scaleVal;
                    double py = y * config.scaleVal;
                    double pz = config.zSliceVal; // config.zSliceVal is already scaled by 100 in control panel
                    double pw = config.wSliceVal;

                    double noiseValue = 0;

                    switch (config.algorithm) {
                        case PERLIN:
                            if (config.dimensions == 2) {
                                noiseValue = Noise.pnoise2(px, py, config.octavesVal, config.persistenceVal, config.lacunarityVal, config.baseVal);
                            } else if (config.dimensions == 3) {
                                noiseValue = Noise.pnoise3(px, py, pz, config.octavesVal, config.persistenceVal, config.lacunarityVal, config.baseVal);
                            }
                            break;
                        case PERLIN_TILEABLE_3D: // This is specifically for the 3D tileable facade method
                             noiseValue = Noise.pnoise3(px, py, pz, config.repeatVal, config.repeatVal, config.repeatVal, config.baseVal);
                            break;
                        case SIMPLEX:
                            if (config.dimensions == 2) {
                                noiseValue = Noise.snoise2(px, py, config.octavesVal);
                            } else if (config.dimensions == 3) {
                                noiseValue = Noise.snoise3(px, py, pz, config.octavesVal);
                            } else if (config.dimensions == 4) {
                                noiseValue = Noise.snoise4(px, py, pz, pw, config.octavesVal);
                            }
                            break;
                    }
                    int gray = (int) ((noiseValue + 1.0) * 0.5 * 255);
                    gray = Math.max(0, Math.min(255, gray));
                    int color = (gray << 16) | (gray << 8) | gray;
                    image.setRGB(x, y, color);
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (config.dimensions == 1 && config.algorithm == NoiseAlgorithm.PERLIN) {
            g.setColor(Color.WHITE);
            g.fillRect(0,0, getWidth(), getHeight());
            g.setColor(Color.BLACK);
            int prevY = -1;
            for (int x = 0; x < getWidth(); x++) {
                double px = x * config.scaleVal;
                double noiseValue = Noise.pnoise1(px, config.octavesVal, config.persistenceVal, config.lacunarityVal, config.baseVal);
                int yVal = (int) ((noiseValue + 1.0) * 0.5 * (getHeight() - 20)) + 10; // Scale and offset
                yVal = getHeight() - yVal; // Invert for screen coordinates

                if (prevY != -1) {
                    g.drawLine(x - 1, prevY, x, yVal);
                }
                prevY = yVal;
            }
        } else if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}


// Panel for ShaderNoiseTexture visualization
class ShaderNoiseTextureVisualizerPanel extends JPanel {
    private BufferedImage image;
    private ShaderNoiseTexture shaderNoiseTexture;
    private int textureWidth; // This is the noise texture's width, not panel width
    private int zSlice;
    private JSlider zSliceSlider;

    public ShaderNoiseTextureVisualizerPanel(int texWidth, int initialZSlice) {
        this.textureWidth = texWidth;
        this.zSlice = initialZSlice;
        this.shaderNoiseTexture = new ShaderNoiseTexture(textureWidth / 4, textureWidth); 
        
        this.image = new BufferedImage(textureWidth, textureWidth, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(textureWidth * 4, textureWidth * 4));
        generateSliceImage();
    }

    public JPanel getControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Z-Slice:"));
        zSliceSlider = new JSlider(0, textureWidth - 1, zSlice);
        zSliceSlider.addChangeListener(e -> {
            zSlice = zSliceSlider.getValue();
            generateSliceImage();
            repaint();
        });
        // Add labels to slider
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("0"));
        labelTable.put(textureWidth - 1, new JLabel(String.valueOf(textureWidth - 1)));
        if (textureWidth/2 != 0 && textureWidth/2 != textureWidth -1) labelTable.put(textureWidth/2, new JLabel(String.valueOf(textureWidth/2)));
        zSliceSlider.setLabelTable(labelTable);
        zSliceSlider.setPaintLabels(true);
        zSliceSlider.setPreferredSize(new Dimension(200, zSliceSlider.getPreferredSize().height));

        controlPanel.add(zSliceSlider);
        return controlPanel;
    }

    private void generateSliceImage() {
        ShortBuffer texData = shaderNoiseTexture.getTextureData();
        texData.rewind(); 

        for (int y = 0; y < textureWidth; y++) {
            for (int x = 0; x < textureWidth; x++) {
                int index = (zSlice * textureWidth * textureWidth + y * textureWidth + x) * 2;
                
                if (index + 1 < texData.limit()) {
                    short r_val_short = texData.get(index);
                    // G channel not visualized here for simplicity

                    double r_noise = (r_val_short / 32767.5) - 1.0; // Map back from [0, 65535] to [-1, 1]

                    int gray = (int) ((r_noise + 1.0) * 0.5 * 255);
                    gray = Math.max(0, Math.min(255, gray));
                    int color = (gray << 16) | (gray << 8) | gray;
                    image.setRGB(x, y, color);
                } else {
                     image.setRGB(x, y, Color.MAGENTA.getRGB()); // Error color
                }
            }
        }
    }
     @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // Draw image scaled to panel size
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this); 
        }
    }
}
```

**Running the Demos:**

1. **Example 1: Basic 2D Perlin Noise**
   - In ``App.java``, ensure ``createAndShowPerlinDemo();`` is uncommented in the ``main`` method.
   - Compile and run: ``mvn compile exec:java -Dexec.mainClass="com.example.noisedemo.App"``
   - You'll see a window displaying 2D Perlin noise. Use the "Perlin Base (Seed)" slider to change the noise pattern and "Scale" to zoom.
2. **Example 2: Basic 2D Simplex Noise**
   - In ``App.java``, comment out other demos and uncomment ``createAndShowSimplexDemo();``.
   - Re-run. You'll see Simplex noise. The ``Noise.snoise2`` facade doesn't directly expose seed control via a base parameter or PermutationTable. For custom seeds with Simplex, you'd use ``com.yousefonweb.noise.SimplexNoise.snoise2(..., PermutationTable table)`` directly.
3. **Example 3: Exploring Octaves (with Perlin Noise)**
   - Uncomment ``createAndShowOctavesDemo();``.
   - Re-run. Now you have an "Octaves" slider. Observe how increasing octaves adds more detail to the Perlin noise.
4. **Example 4: Custom Seed for Perlin Noise**
   - Uncomment ``createAndShowCustomSeedPerlinDemo();``.
   - Re-run. The "Perlin Base (Seed)" slider changes the base parameter for ``Noise.pnoise2``, generating different noise patterns.
5. **Example 5: Visualizing ``ShaderNoiseTexture`` Data**
   - Uncomment ``createAndShowShaderNoiseTextureDemo();``.
   - Re-run. This demo creates an instance of ``ShaderNoiseTexture`` (which internally uses 3D tileable Perlin noise) and visualizes one 2D slice of its R-channel data. Use the slider to view different Z-slices of the 3D texture data. This shows the CPU-side generation of the texture.
   - The actual GLSL code for using this texture on the GPU is available as a string in ``ShaderNoiseResources.SHADER_NOISE_GLSL``. Using it would require an OpenGL application setup (e.g., with LWJGL).

## A Note on GLSL Shader Noise

The Java Noise Package provides tools for GPU-accelerated noise via GLSL:

- **``com.yousefonweb.noise.shader.ShaderNoiseTexture``**: Generates 3D texture data suitable for efficient shader-based noise. Its ``loadToGL()`` method (which uses LWJGL) can upload this data to an OpenGL 3D texture.
- **``com.yousefonweb.noise.shader.ShaderNoiseResources.SHADER_NOISE_GLSL``**: Contains GLSL functions (``pnoise``, ``fbmnoise``, ``fbmturbulence``) that work with the texture generated by ``ShaderNoiseTexture``. You can include this GLSL code in your shaders.
- **``com.yousefonweb.noise.gl`` package**: Contains helper classes (``Shader``, ``ShaderProgram``, ``GLUtil``, etc.) for working with OpenGL and shaders using LWJGL. These are useful if you're building an application that renders with OpenGL.

Using these features requires an OpenGL context in your application, typically set up using a library like LWJGL. The demos above focus on CPU-based generation and visualization with Swing to keep the "Get Started" simple.

This guide should help you begin exploring the capabilities of the Java Noise Package. Experiment with different parameters and see the ``PerlinNoise`` and ``SimplexNoise`` classes for more advanced control.