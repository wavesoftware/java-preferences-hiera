java-preferences-hiera
======================

Java Preferences API backend for Puppetlabs Hiera

Instalation
-----------

With maven dependency:

    <dependency>
        <groupId>pl.wavesoftware</groupId>
        <artifactId>preferences-hiera</artifactId>
        <version>0.1.0</version>
    </dependency>

Download jar:

dd

Usage
-----

    // Set system properties as soon as you can
    System.setProperty("java.util.prefs.PreferencesFactory", PreferencesFactoryImpl.class.getName());
    Preferences prefs = Preferences.systemRoot();
    boolean production = prefs.getBoolean("production", false);