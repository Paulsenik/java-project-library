# Java Project Library

![GitHub release (latest by date)](https://img.shields.io/github/v/release/paulsenik/java-project-library)
[![](https://jitpack.io/v/paulsenik/java-project-library.svg)](https://jitpack.io/#paulsenik/java-project-library)
![GitHub](https://img.shields.io/github/license/paulsenik/java-project-library)

![GitHub Release Date](https://img.shields.io/github/release-date/paulsenik/java-project-library?label=last%20RELEASE)
![GitHub last commit](https://img.shields.io/github/last-commit/paulsenik/java-project-library?label=last%20COMMIT)

The `Java-Project-Library` is a library for making small ***Coding-Projects*** of any kind really *
*fast**.

## Introduction

- PUI-Engine
    - `PUIFrame`(based on JFrame): simplifies creation of UI and manages all Elements
    - Custom UI-Elements (e.g. `RotaryControl`: Digital Knob the user can rotate)
    - Gives pixel-control over Window
    - Automatic scaling
- IO
    - `PFile` & `PFolder`: Advanced controls over Files & Folders
    - `PDataStorage`(similar to json): store/read basic attributes from/to files
    - `PCustomProtocol`: Manage communication between different programs/devices
    - `PSerialConnection`: Connect to an **Arduino, RaspberryPi** or other USB-Devices and
      communicate with them
- Utility
    - `PSystem`: Provides **System-Information** and functions (e.g. find out the OS-Type)
    - `PConsole`: Run a given Command in the Terminal/Console (Linux/Windows) and get the response

## Why should I use this Library?

- ### Graphics:

This library solves the problem for people, who are ***not experienced enough with Graphics*** in
Java or who don't want
to ***invest too much time*** in creating a simple window with buttons, dialogues, user-inputs &
graphics.

This Library is ***beginner-friendly*** and allows programmers to create a new ***Window*** and add
a ***Button***
to it with ***two lines*** and still have the possibility to draw other stuff with `PUICanvas` on
the frame on a
pixel-level

- ### IO-Functionality:

This library solves the problem of writing a lot of code to ***read*** and ***write*** files. It
also provides functions
to get the content in a specific format.

With `PFile` you can get a File as a `One big String`,`Array of Lines`
or `Array of Words/Paragraphs`

You can also ***store basic attributes*** to files and later read them (similar to json) by
using `PDataStorage`.

**USB-Communication** with *an Arduino, RaspberryPi, etc.* is simplified in `PSerialConnection`.
Developers can
simply `connect()` and `disConnect()` a device and attach a `PSerialListener` to read Data from the
USB-Attached Device.
Use `write(data)` to send data to the device.

**For more see my
[included Demo](https://github.com/paulsenik/java-project-library/blob/main/src/com/paulsen/demo/Demo.java)
**

## Projects built with JPL:

- [JAudioController](https://github.com/paulsenik/AudioController)
- [FileManager](https://github.com/paulsenik/FileManager)
- [ButtonBox V2](https://github.com/paulsenik/ButtonBox_V2)

## Usage

This library can be loaded using [JitPack](https://jitpack.io/#paulsenik/java-project-library)

```xml

<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Include the dependency (change version
*[version](https://github.com/paulsenik/java-project-library/releases)* if needed)

```xml

<dependencies>
  <dependency>
    <groupId>com.github.paulsenik</groupId>
    <artifactId>java-project-library</artifactId>
    <version>1.1.6</version>
  </dependency>
</dependencies>
```

### Alternatively using jar

1. **Download / Build** the latest Version
2. **Include** the library into your project
    - ***IntelliJ:*** `File > Project Structure > Libraries > +`
    - ***Eclipse:*** `Right-Click Project > Properties > Java Build Path > Add External JARs`
3. **Start** with your project

## Troubleshooting

* Problems when using `PSerialConnection` on Linux:
    * **Either** run your program as `root`
    * **Or** add your **User** to some of those **4 Groups**:
      `uucp` `dialout` `lock` `tty` *(Some might not exist on your distro)*
    * **Still** got **problems** or have issues adding yourself to the Groups:<br>
      Look up the *
      *jSerialComm-[Troubleshooting-Wiki](https://github.com/Fazecast/jSerialComm/wiki/Troubleshooting)
      **

## Credit & Status

The Library [jSerialComm](https://github.com/Fazecast/jSerialComm) is used for USB/Serial
communication.

It is ***fully functional*** and ![Maintenance](https://img.shields.io/maintenance/yes/2023)
by [`Paulsen`](https://github.com/paulsenik)

### TODOs & In-Progress

- **BugFixes**
    - Resizing Window to Fullscreen does not always draw/update to latest size
- **Changes**
    - Optimize draw call by only redrawing region of parent that intersects with the updated Element
    - Use JSON as DataStorage
    - Base `PUIElement` directly form JComponent and restructure PUICore/PUIFrame
    - **FixPoints** on PUISlider
    - **Free** movable PUIElements on PUIScrollPanel (without snapped locations)
- **Additions**
    - **Graph plotter**: Plot multiple graphs and analyze them
    - **Built-In File-Browser** based on PUIList
    - **Comments**/JavaDoc still missing😬
