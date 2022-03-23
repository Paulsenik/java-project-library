# Java Project Library

![GitHub release (latest by date)](https://img.shields.io/github/v/release/realPaulsen/Java-Project-Library)
![GitHub Release Date](https://img.shields.io/github/release-date/realPaulsen/Java-Project-Library?label=last%20RELEASE)
![GitHub last commit](https://img.shields.io/github/last-commit/realPaulsen/Java-Project-Library?label=last%20COMMIT)

![GitHub](https://img.shields.io/github/license/realPaulsen/Java-Project-Library)
![Lines of code](https://img.shields.io/tokei/lines/github/realPaulsen/Java-Project-Library)

`Java-Project-Library` is a library for making small ***Coding-Projects*** of any kind really **fast**.

## Download

<!--  TODO: Update D-Link after every new Release  -->

- [Versions](https://github.com/realPaulsen/Java-Project-Library/releases)

> **All Versions** compiled with **Java 11**

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
    - `PCustomProtocol`: Manage communication between different programs/devices
    - `PSerialConnection`: Connect to an **Arduino, RaspberryPi** or other USB-Devices and communicate with them
- Utility
    - `PSystem`: Provides **System-Information** and functions (e.g. find out the OS-Type)
    - `PConsole`: Run a given Command in the Terminal/Console (Linux/Windows) and get the response

## Why should I use this Library?

- ### Graphics:

This library solves the problem for people, who are ***not experienced enough with Graphics*** in Java or who don't want
to ***invest too much time*** in creating a simple window with buttons, dialogues, user-inputs & graphics.

This Library is ***beginner-friendly*** and allows programmers to create a new ***Window*** and add a ***Button***
to it with ***two lines*** and still have the possibility to draw other stuff with `PUICanvas` on the frame on a
pixel-level

- ### IO-Functionality:

This library solves the problem of writing a lot of code to ***read*** and ***write*** files. It also provides functions
to get the content in a specific format.

With `PFile` you can get a File as a `One big String`,`Array of Lines` or `Array of Words/Paragraphs`

You can also ***store basic attributes*** to files and later read them (similar to json) by using `PDataStorage`.

**USB-Communication** with *an Arduino, RaspberryPi, etc.* is simplified in `PSerialConnection`. Developers can
simply `connect()` and `disConnect()` a device and attach a `PSerialListener` to read Data from the USB-Attached Device.
Use `write(data)` to send data to the device.

## How do I use it?

1. **Download** the [latest `.jar` file](#downloads)
2. **Include** the library into your project
    - ***IntelliJ:*** `File > Project Structure > Libraries > +`
    - ***Eclipse:*** `Right-Click Project > Properties > Java Build Path > Add External JARs`
3. **Start** with your project

**For more see 
[included Demo](https://github.com/realPaulsen/Java-Project-Library/blob/main/src/com/paulsen/demo/Demo.java)**

(Detailed Tutorial for using the Library comming soon)

## Troubleshooting

* Problems when using `PSerialConnection` on Linux:
    * **Either** run your program as `root`
    * **Or** add your **User** to some of those **4 Groups**:
      `uucp` `dialout` `lock` `tty` *(Some might not exist on your distro)*
    * **Still** got **problems** or have issues adding yourself to the Groups:<br>
      Look up the **jSerialComm-[Troubleshooting-Wiki](https://github.com/Fazecast/jSerialComm/wiki/Troubleshooting)**

## Credit

The Library [jSerialComm](https://github.com/Fazecast/jSerialComm) is used for USB/Serial communication.

## Project status

The Library is ***fully functional*** and ![Maintenance](https://img.shields.io/maintenance/yes/2021)
by [`Paulsen`](https://github.com/realPaulsen)

### Planned Improvements / In Development

- **BugFixes:**
    - Make `run()` of **PConsole** _semi-blocking_ instead of _full-blocking_. In case the Console/Terminal wants an input the run() would be blocked forever
- Make `PUIElement` **abstract** and create `PUIButton` to replace the parent-class of the other Elements
- **FixPoints** on PUISlider
- **Free** movable PUIElements on PUIScrollPanel (without snapped locations)
- **Graph plotter**: Plot multiple graphs and analyze them
- **Comments**/JavaDoc still missing😬


## Build-Notes

* Import latest Build of external library [jSerialComm](https://github.com/Fazecast/jSerialComm) for serial/USB-Connection

