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
	<version>0.1.0</version>
</dependency>
```

Download jar:

https://bitbucket.org/wavesoftware/java-preferences-hiera/downloads/preferences-hiera-0.1.0.jar

Usage
-----

```java
// Set system properties as soon as you can
System.setProperty("java.util.prefs.PreferencesFactory", PreferencesFactoryImpl.class.getName());
Preferences prefs = Preferences.systemRoot();
boolean production = prefs.getBoolean("production", false);
```