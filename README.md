# Gyroscope-Controlled Maze Game

## Overview
This project is a simple mobile game built using **Jetpack Compose**. The game uses the phone’s **gyroscope sensor** to control a ball that moves through a maze. The goal is to navigate the ball from the start area to the goal area without crossing walls.

---

## Features
- Ball movement controlled by the **gyroscope (phone tilt)**
- Maze built using **Canvas drawing**
- Collision detection with walls and obstacles
- Goal detection with win message
- Smooth movement with basic physics (velocity + friction)

---

## How It Works

### 1. Sensor Input
The app uses the **gyroscope sensor** to detect the phone’s rotation.  
These values are used to update the ball’s velocity in both X and Y directions.

### 2. Ball Movement
The ball moves continuously based on velocity:
- Velocity is updated from gyroscope input
- Friction is applied to smooth the motion
- Movement is updated every frame (~60 FPS)

### 3. Collision Detection
The ball is represented as a rectangle (bounding box).  
Before moving, the app checks if the next position overlaps with any wall:
- If collision occurs → movement is stopped
- Otherwise → position is updated

### 4. Maze Rendering
All visual elements are drawn using **Jetpack Compose Canvas**:
- Walls → rectangles
- Ball → circle
- Goal → rectangle

### 5. Win Condition
When the ball overlaps with the goal area:
- The game stops
- A "You Win" message is displayed

---

## Technologies Used
- Kotlin
- Jetpack Compose
- Canvas (custom drawing)
- Android Sensor API (Gyroscope)

---

## How to Run

### Option 1: Emulator (UI only)
- Run the app using Android Studio emulator
- You can view the maze and UI
- Play with Virtual Sensor in the emulator

### Option 2: Real Device (Recommended)
1. Enable **Developer Options** on your phone
2. Turn on **USB Debugging**
3. Connect the device to your computer
4. Run the app from Android Studio

---

## AI Usage
I used ChatGPT to help understand sensor usage and troubleshoot some issues during development. The final implementation, design decisions, and testing were completed independently.
