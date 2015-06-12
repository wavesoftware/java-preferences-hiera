java-preferences-hiera
======================

[![Build Status](https://travis-ci.org/wavesoftware/java-preferences-hiera.png?branch=master)](https://travis-ci.org/wavesoftware/java-preferences-hiera) [![Build Status](http://jenkins-ro.wavesoftware.pl/buildStatus/icon?job=Java-Hiera-Preferences)](http://jenkins-ro.wavesoftware.pl/job/Java-Hiera-Preferences)

Java Preferences API backend for Puppetlabs Hiera

Instalation
-----------

With maven dependency:

```xml
<dependency>
    <groupId>pl.wavesoftware</groupId>
	<artifactId>preferences-hiera</artifactId>
	<version>0.3.2</version>
</dependency>
```

Usage
-----

If using as a plugin into Application Server or standalone application set system properties as soon as you can:
```java
// Inside a plugin
HieraPreferencesFactory.activate();

// Aplications use default Java syntax
Preferences prefs = Preferences.userRoot();
boolean production = prefs.getBoolean("production", false);
```

If using hiera preferences inside a Java EE Application user rather direct aproch:
```java
Preferences prefs = HieraPreferencesFactory.createUserRoot();
boolean production = prefs.getBoolean("production", false);
```

Changelog
---------

#### v0.3.2 ####

- more bug fixes

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
