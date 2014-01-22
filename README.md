java-preferences-hiera
======================

[![Build Status](https://travis-ci.org/wavesoftware/java-preferences-hiera.png?branch=master)](https://travis-ci.org/wavesoftware/java-preferences-hiera)

Java Preferences API backend for Puppetlabs Hiera

Instalation
-----------

With maven dependency:

```xml
<dependency>
    <groupId>pl.wavesoftware</groupId>
	<artifactId>preferences-hiera</artifactId>
	<version>0.3.1</version>
</dependency>
```

Usage
-----

```java
// Set system properties as soon as you can
HieraPreferencesFactory.activate();
Preferences prefs = Preferences.systemRoot();
boolean production = prefs.getBoolean("production", false);
```

Changelog
---------

#### v0.3.1 ####
- Bug fixes for transient and miltithreaded maven build

#### v0.3.0 ####

- Fallback to standard Java preferences for method not supported by hiera
- Caching execution of hiera command line tool
- Bugfixes

#### v0.2.0 ####

- Major rewrite
- More test
- CI support

#### v0.1.0 ####

- First version
