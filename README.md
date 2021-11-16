
# Java Project Library

![GitHub release (latest by date)](https://img.shields.io/github/v/release/realPaulsen/Java-Project-Library?label=version)
![GitHub Release Date](https://img.shields.io/github/release-date/realPaulsen/Java-Project-Library?label=last%20RELEASE)
![GitHub last commit](https://img.shields.io/github/last-commit/realPaulsen/Java-Project-Library?label=last%20COMMIT)

![GitHub](https://img.shields.io/github/license/realPaulsen/Java-Project-Library)
![GitHub top language](https://img.shields.io/github/languages/top/realPaulsen/Java-Project-Library)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/realPaulsen/Java-Project-Library)
![Lines of code](https://img.shields.io/tokei/lines/github/realPaulsen/Java-Project-Library)
![GitHub all releases](https://img.shields.io/github/downloads/realPaulsen/Java-Project-Library/total)

`Java-Project-Library` is a library for making small ***Coding-Projects*** of any kind really **fast**.

## Downloads

<!--  TODO: Update D-Link after every new Release  -->
- [![GitHub release (latest by date) ](https://img.shields.io/github/v/release/realPaulsen/Java-Project-Library) (latest)](https://github.com/realPaulsen/Java-Project-Library/releases/download/v1.1.0/Java-Project-Library.jar)
- [All](https://github.com/realPaulsen/Java-Project-Library/releases)

## Introduction

Features:
- PUI-Engine
  - `PUIFrame`(based on JFrame): simplifies creation of Frames and manages all Elements
  - Custom UI-Elements (e.g. `RotaryControl`: Digital Knob the user can rotate)
  - Automatic scaling
  - Gives pixel-control over Window
- IO
  - `PFile` & `PFolder`: Advanced controls over Files & Folders
  - `PDataStorage`(similar to json): store/read basic attributes from/to files
  - `PCustomProtocol`: Manage serial-communication between different programs/devices 

## Why should I use this Library?

- ### Graphics:

This library solves the problem for people, who are ***not experienced enough with Graphics*** in Java
or who don't want to ***invest too much time*** in creating a simple window with buttons, dialogues, user-inputs & graphics.

This Library is ***beginner-friendly*** and allows programmers to create a new ***Window*** and add a ***Button***
to it with ***two lines*** and still have the possibility to draw on the frame on a pixel-level
 
- ### IO-Functionality:

This library solves the problem of writing a lot of code to ***read*** and ***write*** files. It also provides functions
to get the content in a specific format.

You can get a File as a `One big String`,`Array of Lines` or `Array of Words/Paragraphs`

You can also ***store basic attributes*** to files and later read them (similar to json) by using `PDataStorage`.

## How do I use it?

1. **Download** the [latest `.jar` file](#downloads)
2. **Include** the library into your project
   - ***IntelliJ:*** `File > Project Structure > Libraries > +`
   - ***Eclipse:*** `Right-Click Project > Properties > Java Build Path > Add External JARs`
3. **Start** with your project

(Detailed Tutorial for using the Library comming soon)

## Project status

The Library is ***fully functional*** and ![Maintenance](https://img.shields.io/maintenance/yes/2021)
by [`Paulsen`](https://github.com/realPaulsen)

### Planned Improvements / In Development

- Mouse-Update-Bug: When not moving Mouse after click-event, Elements can not be clicked until the Mouse moves again
- Graph plotter: Plot multiple graphs and analyze them
- Comments/JavaDoc still missing😬

