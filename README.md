# ExceptionScanPlugin

ExceptionScanPlugin is a plugin for the Recaf bytecode editor that scans a Java project for exceptions being thrown outside of a try-catch statement.

## Features

- Scans the project for exceptions being thrown outside of a try-catch statement.
- Provides a context menu option to scan for throws within the Recaf bytecode editor.
- Displays the class name and line number where an exception is thrown.

## Installation

1. Download the latest release of Recaf from the official Recaf repository: [Recaf Releases](https://github.com/Col-E/Recaf/releases)
2. Launch Recaf.
3. In the Recaf menu, click on `Plugins` -> `Open Plugins Folder`.
4. Select the ExceptionScanPlugin JAR file and move it into that folder.
5. Restart Recaf.

## Usage

1. Open a Java project in Recaf.
2. Right-click on a class in the project.
3. In the context menu, select `Scan for throws`.
4. The plugin will analyze the class and display any exceptions thrown outside of a try-catch statement.
5. The console output will show the class name and line number where each exception is thrown.

## Compatibility

ExceptionScanPlugin is made for Recaf, a powerful bytecode editor for Java.

## Contributing

Contributions to the  project are welcome. 
