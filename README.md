# RUMaps

A Java-based interactive mapping application for Rutgers University campuses, featuring pathfinding algorithms and network analysis capabilities.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Input File Format](#input-file-format)
- [Testing](#testing)

## Overview

RUMaps is a comprehensive mapping application that provides interactive navigation and pathfinding capabilities for Rutgers University campuses. The application uses graph theory concepts to model the university's road network, allowing users to find optimal routes between locations while considering factors like traffic, distance, and intersection count.

## Features

- **Interactive Map Interface**: Visual representation of Rutgers University campuses with satellite imagery
- **Pathfinding Algorithms**: 
  - Shortest path calculation
  - Minimum intersections routing
  - Traffic-aware routing
- **Network Analysis**: Graph-based representation of streets, intersections, and blocks
- **Real-time Information**: Display of street names, block numbers, and traffic factors
- **Multiple Campus Support**: Configurable for different campus layouts
- **Scrollable Interface**: Navigate large campus areas with ease

## Project Structure

```
RUMaps/
├── src/rumaps/           # Main source code
│   ├── Driver.java      # Main application entry point
│   ├── RUMaps.java      # Core mapping logic
│   ├── Network.java     # Graph representation
│   ├── Block.java       # Street block implementation
│   ├── Intersection.java # Road intersection nodes
│   ├── Coordinate.java  # Geographic coordinates
│   ├── MapPanel.java    # GUI rendering
│   ├── Queue.java       # Queue data structure
│   └── StdIn.java       # Input utilities
├── test/                # JUnit test files
├── assets/              # Map images and overlays
├── bin/                 # Compiled classes
├── lib/                 # External libraries
├── Busch.in            # Busch campus data
└── AllCampuses.in      # Complete campus dataset
```

## Prerequisites

- **Java Development Kit (JDK)**: Version 8 or higher
- **Java Runtime Environment (JRE)**: For running the compiled application
- **JUnit**: For running tests (included in lib/ directory)

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/RUMaps.git
   cd RUMaps
   ```

2. **Compile the project**
   ```bash
   javac -cp "lib/*" -d bin src/rumaps/*.java
   ```

3. **Run the application**
   ```bash
   java -cp "bin;lib/*" rumaps.Driver
   ```

## Usage

### Running the Application

1. **Start the application** by running the `Driver` class
2. **Navigate the map** using scroll bars or mouse interaction
3. **Hover over blocks** to see street information and block details
4. **Use control panel** to access various mapping features

### Key Features

- **Street Information**: Hover over any block to see street name and block number
- **Block Details**: View length, traffic factor, and traffic information
- **Pathfinding**: Calculate optimal routes between intersections
- **Network Analysis**: Explore the campus road network structure

## Input File Format

The application reads campus data from `.in` files with the following structure:

```
[Number of Intersections]
[Number of Streets]
[Street Name 1]
[Number of Blocks in Street 1]
[Block Number] [Number of Points] [Road Size]
[X1] [Y1]
[X2] [Y2]
...
[Block Number] [Number of Points] [Road Size]
...
[Street Name 2]
...
```

### Example (Busch.in):
```
29          # 29 intersections
12          # 12 streets
Sutphen Rd  # Street name
2           # 2 blocks in this street
122         # Block number
7           # 7 coordinate points
4.75        # Road size
108 341     # X, Y coordinates
117 331
...
```

## Testing

Run the test suite using JUnit:

```bash
java -cp "bin;lib/*" org.junit.runner.JUnitCore test.RUMapsTest
```

### Test Coverage

The test suite covers:
- Block and intersection initialization
- Block length calculations
- Reachable intersections analysis
- Path optimization algorithms
- Route information calculations

## Troubleshooting

### Common Issues

1. **Classpath errors**: Ensure all dependencies are in the `lib/` directory
2. **File not found**: Verify input files are in the project root directory
3. **Memory issues**: Large campus files may require increased JVM heap size

### Performance Tips

- Use `Busch.in` for development and testing (smaller dataset)
- Use `AllCampuses.in` for full campus analysis
- Adjust JVM settings for large datasets: `java -Xmx2g -cp "bin;lib/*" rumaps.Driver`

---

