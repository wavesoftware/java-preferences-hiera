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
	<version>0.2.0</version>
</dependency>
```

Download jar:

https://bitbucket.org/wavesoftware/java-preferences-hiera/downloads/preferences-hiera-0.2.0.jar

Usage
-----

```java
// Set system properties as soon as you can
HieraPreferencesFactory.activate();
Preferences prefs = Preferences.systemRoot();
boolean production = prefs.getBoolean("production", false);
```