# Robotics-NXT-Minsdstorm

---

# EX1 PID and Fuzzy Control Exercise

## Introduction

This project demonstrates the implementation of two control algorithms—PID (Proportional-Integral-Derivative) and Fuzzy Logic—for robot path following in an offline simulation. The robot follows a circular path without obstacles, aiming to stay on the path while traveling at maximum velocity.

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
