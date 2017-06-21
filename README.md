# Whicher

[![license](https://img.shields.io/github/license/Drako/whicher.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

> which -- locate a program file in the user's path

This class basically does what the `which` program does.

## Usage

*Kotlin*:
```kotlin
import guru.drako.utils.which.Whicher

// in some fun:
val location = Whicher.which("foo")
// location contains path to foo executable (e.g. "/usr/bin/foo")
// which returns a Path?, so null is returned when the executable is not found
```

*Java*
```java
import guru.drako.utils.which.Whicher;
import java.nio.Path;

// in some function:
final Path location = Whicher.system.which("foo");
// location contains path to foo executable (e.g. "/usr/bin/foo")
// which returns null when the executable is not found
```

The Whicher does not depend on the `which` program and might do
some things differently.

## Build Status

Branch | Status
--- | ---
develop | [![Build Status](https://travis-ci.org/Drako/whicher.svg?branch=develop)](https://travis-ci.org/Drako/whicher)
master | [![Build Status](https://travis-ci.org/Drako/whicher.svg?branch=master)](https://travis-ci.org/Drako/whicher)
