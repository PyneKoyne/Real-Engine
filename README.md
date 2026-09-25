# Real-Engine (v3)
An implementation of a Stippling 3D Rendering Engine written in plain Java.



https://github.com/user-attachments/assets/126372c2-8d67-46f9-be74-2aeb73d4d88c




V2 can be found [here](https://github.com/PyneKoyne/Craft-Me-In/tree/Real-Engine).

V1 has been deprecated

---

## Features
- Ability to spawn in Cubes during run-time
- Jumping and movement
- Optimized CPU Rendering
- Ability to add Trigonometric Functions to output
- Ability to change focal length

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
Download the project, and create the main package to hold all the files.
Then compile all the files and run Main.
> Note: If you're using IntelliJ, remember to declare the parent folder as "Source"
---
### Notes

> *Applying 1 tangent function turns the display into a planar instead of a spherical projection*
