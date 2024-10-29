# Robotics-NXT-Minsdstorm

---

# EX1 PID and Fuzzy Control Exercise

## Introduction

This project demonstrates the implementation of two control algorithms PID (Proportional-Integral-Derivative) and Fuzzy Logic for robot path following in an offline simulation. The robot follows a circular path without obstacles, aiming to stay on the path while traveling at maximum velocity.

The project was developed to compare the performance of these two algorithms in controlling the robot's movement on a fixed path and maintaining stability at high speeds.

## Features

1. **Two Control Algorithms:**
   - **Fuzzy Logic Control:** Uses a set of defined rules to adjust the robot’s behavior based on the current error and velocity, providing flexibility in control.
   - **PID Control:** Adjusts the robot’s movement based on proportional, integral, and derivative terms to minimize the error between the robot’s position and the target path.

2. **User Input Options:**
   - Users can select either the PID or Fuzzy Logic algorithm to control the robot and observe the differences in how each maintains the robot’s speed and stability on the circular path.
   - Both algorithms allow tuning and adjustments to parameters or rule sets for optimal performance.

3. **Simulated Environment:**
   - The robot moves along a predefined circular path at maximum velocity without obstacles.
   - The focus is on maintaining accurate path-following and stability at high speeds.

## Instructions for Use

1. **Control Algorithm Selection:**
   - **Fuzzy Algorithm:** The robot follows a set of fuzzy rules to stay on the circular path, adjusting its behavior based on error and speed.
   - **PID Algorithm:** The robot corrects its trajectory using proportional, integral, and derivative terms to minimize error while traveling at maximum velocity.

2. **Simulating Robot Motion:**
   - Offline simulation in Webots where the robot follows the circular path at maximum speed.
   - Users can observe how each algorithm maintains stability and accuracy on the path.

### Setup

- Clone this repository:  
  `git clone <repository-url>`
- Follow these steps to run the simulation:
  - **Step 1:** Install the required dependencies.
  - **Step 2:** Launch the Webots robot simulation.
  - **Step 3:** Choose either the PID or Fuzzy Logic algorithm in the configuration file.
  - **Step 4:** Adjust the tuning parameters for PID or modify the fuzzy rule set if needed.

## Algorithm Details

### PID Control

The PID controller is composed of three components:
- **Proportional (P):** Minimizes the current error between the robot and the circular path.
- **Integral (I):** Corrects any accumulated error to ensure the robot stays on track.
- **Derivative (D):** Predicts future error based on the rate of change, helping the robot maintain smooth and stable movement at high speeds.

### Fuzzy Logic Control

The fuzzy control system uses predefined rules and inputs such as the robot's distance from the circular path and its speed. Based on these inputs, it applies fuzzy logic rules to adjust the robot’s speed and direction to maintain the path.

Fuzzy logic allows for flexible decision-making, especially when the system faces varying speeds and turning angles.

## Videos

- **Fuzzy Logic Control Demo:**  
  [Insert Fuzzy Logic Video URL here]

- **PID Control Demo:**  
  [Insert PID Control Video URL here]

## Additional Information

- **Circular Path:** The robot follows a simple circular path without obstacles, focusing solely on path-following accuracy and stability at high speed.


# EX2 Mobile Robot Mapping and Navigation Project

## Overview
This project involves two main tasks: **Mapping** and **Navigation** using a mobile robot equipped with sensors. The robot follows a convex arena's walls, maps the perimeter, and uses the generated map to navigate to various points within the arena.

### Task 1: Mapping the Arena
The first task is to map the perimeter of the arena using an ultrasonic distance sensor and odometry. The robot follows the walls while keeping a consistent distance using a **PID controller**.

- **Arena Specifications**:
  - The arena is a convex polygon with fewer than 8 straight walls.
  - The walls' angles are between 70° and 170°.

- **Process**:
  1. The robot starts at a marked position near the wall.
  2. It moves clockwise around the arena, following the walls using the ultrasonic sensor.
  3. The robot calculates its position using odometry.
  4. The perimeter is mapped by combining odometry data and sensor readings, creating straight-line segments for the walls.

- **Mapping Implementation**:
  - The raw sensor data (distance readings and odometry) is processed in Python.
  - The software fits straight lines to the data using **RANSAC** to ignore noise.
  - The final map, a closed polygon representing the arena walls, is generated and visualized.

### Task 2: Navigation Using the Map
The second task involves using the map from Task 1 to navigate the robot to three key points in the arena: the **start point**, the **middle point**, and the **end point**.

- **Process**:
  1. The robot starts at an unknown location near the arena's perimeter.
  2. It moves to the **start point** and aligns itself in the correct direction.
  3. The robot continues to the **middle point**, announces its arrival via a voice message, and waits for further instructions.
  4. Finally, the robot reaches the **end point**, aligning itself accordingly and announcing its arrival.

- **Navigation Strategy**:
  - The robot relies on odometry, the map, and sensor data to navigate between points.
  - Voice commands are used to confirm reaching points, and time is allotted for photos of the robot’s position at each key location.

### Videos
- [Mapping Task Video](#)
- [Navigation Task Video](#)


2. **Navigation Task**:
    - The robot will load the map and navigate to the defined points.
    - The robot will announce each arrival with a voice message.
