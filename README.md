# Real-Engine (v3)
An implementation of a Stippling 3D Rendering Engine written in plain Java.


https://github.com/user-attachments/assets/d5c15251-92c5-48c8-8fd8-ca2942a350f2



V2 can be found [here](https://github.com/PyneKoyne/Craft-Me-In/tree/Real-Engine).

V1 has been deprecated

---

## Features
- Ability to spawn in Cubes during run-time
- Jumping and movement
- Optimized CPU Rendering
- Ability to add Trigonometric Functions to output
- Abiilty to change focal length

### Changelog From V2
- Removed JOCL for ease of use to build and run
  - Added back in the ability to add
- Trigonometric Functions to the output
- Added Graphics Optimizations from Minecraft Project (10x Speed Increase)
  - Implemented Buffered Image Graphics
  - Implemented Multi-Threaded Rendering
  - Added Blending of Overlapping Pixels
- Added Plane Class
- Added Fast Inverse Square Root

---
## Usage

### Dependencies
- JDK version 17+ (with JFrame)

### Build
Download the project, compile all the files, and run Main.

---
### Notes

> *Applying 1 tangent function turns the display into a planar instead of a spherical projection*
