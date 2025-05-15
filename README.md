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

## Get Started Guide

This guide will walk you through setting up a minimal Java Maven project to use the Java Noise Package and demonstrate its basic functionalities with simple Swing examples.

## Prerequisites

1. **Java Development Kit (JDK):** Version 17 or newer is recommended, as the library (specifically ``ShaderNoiseResources.java`` with its text block) and this guide assume modern Java features.

2. **Apache Maven:** A build automation tool. Ensure it's installed and configured in your system's PATH.

3. **The Java Noise Library (noise_java_project):** You need to have the Java Noise Package itself. Since it's not on a public Maven repository, you'll typically build it from its source (java_noise_lib_project.md describes its structure) and install it into your local Maven repository. To do this, navigate to the root directory of the noise_java_project (where its pom.xml is located) and run:

   ```
   mvn clean install
   ```

   This will make the library available for other local Maven projects. The library's Maven coordinates are assumed to be:

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

*(Note: The noise_java_project itself uses LWJGL for its gl package. When you add com.yousefonweb:noise as a dependency, Maven should pull in LWJGL transitively if it's declared as a regular dependency in the noise library's pom.xml. The CPU-based noise demos below do not directly require LWJGL setup in your demo project, but running code that uses the gl package from the noise library would.)*