Java Core Models Library
========================

This repository contains a version of the Java Class Library (`rt.jar`) that can
be used together with
[JBMC](https://github.com/diffblue/cbmc/tree/develop/jbmc) to
model-check Java code that calls the standard library.

## Usage

Put the `core-models.jar` on the classpath of the application that you
model-check using JBMC, e.g.
```
jbmc -cp .:cprover-api.jar:core-models.jar com.examples.MyClass.foo
```

## License

[OpenJDK GPLv2 + Classpath Exception](https://github.com/openjdk/jdk/blob/master/LICENSE)
