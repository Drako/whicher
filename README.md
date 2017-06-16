# Whicher

> which -- locate a program file in the user's path

This class basically does what the `which` program does.

## Usage

```kotlin
import guru.drako.which.Whicher

// in some fun:
val foo = Whicher.which("foo")
// foo contains path to foo executable (e.g. "/usr/bin/foo")
```

The Whicher does not depend on the `which` program and might do
some things differently.
