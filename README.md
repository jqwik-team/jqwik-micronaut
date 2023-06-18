# jqwik Micronaut Support

This project provides an extension to support testing of Micronaut applications
with [jqwik](https://jqwik.net).

```mermaid
---
title: "Micronaut Test Extension for Jqwik: System Context"
---

flowchart TB
    User["Software Engineer 
    [Person]

A software engineer willing to use
property-based testing along with
Java IoC frameworks"]

MTEJ["Micronaut Test Extension for Jqwik
[Software System]

Enables property-based testing
using Jqwik with Micronaut Test"]

MF["Micronaut Framework
[Software System]

Provides Micronaut
TestContext API"]

JQ["Jqwik
[Software System]

Provides test execution
lifecycle hooks API"]

User --"Uses"--> MTEJ
MTEJ --"Consumes API"--> MF
MTEJ --"Consumes API" --> JQ

classDef focusSystem fill: #1168bd,stroke: #0b4884, color: #ffffff
classDef supportingSystem fill: #666, stroke: #0b4884, color:#ffffff
classDef person fill: #08427b, stroke:#052e56, color: #ffffff

class MTEJ focusSystem
class User person
class MF,JQ supportingSystem

click JQ"https://github.com/jqwik-team/jqwik"_blank
click MTEJ"https://github.com/jqwik-team/jqwik-micronaut"_blank
click MF"https://micronaut-projects.github.io/micronaut-test/latest/guide"_blank
```

<!-- use `doctoc --maxlevel 3 README.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

### Table of Contents

- [How to Install](#how-to-install)
    - [Gradle](#gradle)
    - [Maven](#maven)
    - [Supported Micronaut Versions](#supported-micronaut-versions)
- [Standard Usage](#standard-usage)
    - [Lifecycle](#lifecycle)
    - [Parameter Resolution of Autowired Beans](#parameter-resolution-of-autowired-beans)
    - [Micronaut JUnit Jupiter Testing Annotations](#micronaut-junit-jupiter-testing-annotations)
- [Shortcomings](#shortcomings)
- [Release Notes](#release-notes)
    - [1.0.0](#100)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## How to Install

### Gradle

Follow the
[instructions here](https://jqwik.net/docs/current/user-guide.html#gradle)
and add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation("io.micronaut:micronaut-context:3.8.9")
    // ...
    testImplementation("net.jqwik:jqwik-micronaut:1.0.0")
    testImplementation("io.micronaut.test:micronaut-test-core:3.9.2")
}
```

You can look at a
[sample project](https://github.com/jlink/jqwik-samples/tree/master/jqwik-micronaut-gradle)
using Jqwik, Micronaut and Gradle.

### Maven

Follow the
[instructions here](https://jqwik.net/docs/current/user-guide.html#maven)
and add the following dependency to your `pom.xml` file:

```xml

<xml>
    <dependency>
        <groupId>io.micronaut</groupId>
        <artifactId>micronaut-context</artifactId>
        <version>3.8.9</version>
    </dependency>

    <!--...-->

    <dependency>
        <groupId>net.jqwik</groupId>
        <artifactId>jqwik-micronaut</artifactId>
        <version>1.0.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>io.micronaut.test</groupId>
        <artifactId>micronaut-test-core</artifactId>
        <version>3.9.2</version>
        <scope>test</scope>
    </dependency>
</xml>
```

### Supported Micronaut Versions

You have to provide your own version of Micronaut through Gradle or Maven. The
_jqwik-micronaut_ library has been tested with versions:

- `3.8.9`.

Please report any compatibility issues you stumble upon.

## Standard Usage

To enable autowiring of a Micronaut application context or beans you just have
to add `@JqwikMicronautTest` to your test container class:

```java
import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;

@JqwikMicronautTest
class MyMicronautProperties {
    @Inject
    private MyMicronautBean myMicronaut;

    @Property
    void nameIsAddedToHello(@ForAll @AlphaChars @StringLength(min = 1) String name) {
        String greeting = myMicronaut.sayHello(name);
        Assertions.assertTrue(greeting.contains(name));
    }
}
```

Configuration and autowiring of values is delegated to Micronaut's own test
framework. Therefore, all [integration testing annotations]() can be used. This
is also true for [standard annotation support]().

### Lifecycle

Micronaut will recreate its application context for each annotated class.
That means, that

- Singleton beans will only be created once for all tests of one test container
  class.
- Properties and tries within the same class _share mutual state_ of all
  Micronaut-controlled beans.

If you want a property to recreate the app context for each try, you have to
use the property parameter _perTry = true_.
Compare the following two properties:

```java

@JqwikMicronautSupport
@ContextConfiguration(classes = MyMicronautConfig.class)
class MyMicronautProperties {

    @Property(tries = 10)
    void counterIsCountingUp(@Autowired MyCounter counter) {
        counter.inc();
        // Prints out 1, 2, 3 ... 10
        System.out.println(counter.value());
    }

    @Property(tries = 10)
    @DirtiesContext
    void counterIsAlways1(@Autowired MyCounter counter) {
        counter.inc();
        // Prints out 1, 1, 1 ... 1
        System.out.println(counter.value());
    }
}
```

### Parameter Resolution of Autowired Beans

Autowired beans will be injected as parameters in example and property methods,
in all
[lifecycle methods](https://jqwik.net/docs/current/user-guide.html#annotated-lifecycle-methods)
and also in the test container class's constructor - if there is only one:

```java

@JqwikMicronautSupport
@ContextConfiguration(classes = MyMicronautConfig.class)
class MyOtherMicronautProperties {
    @Autowired
    MyOtherMicronautProperties(MyMicronautBean micronautBean) {
        Assertions.assertNotNull(micronautBean);
    }

    @BeforeProperty
    void beforeProperty(@Autowired MyMicronautBean micronautBean) {
        Assertions.assertNotNull(micronautBean);
    }

    @Property
    void beanIsInjected(@Autowired MyMicronautBean micronautBean) {
        Assertions.assertNotNull(micronautBean);
    }
}
```

### Micronaut JUnit Jupiter Testing Annotations

_jqwik_'s Micronaut support is trying to mostly simulate how Micronaut's native
Jupiter support works. Therefore, some of that stuff also works, but a few
things do not.

## Shortcomings

We are not aware of any at this time.

## Release Notes

### 1.0.0

- Uses jqwik 1.7.4.
- Tested with Micronaut 3.8.9.