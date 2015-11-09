# senjin
SimpleENGINe, current version 0.13-SNAPSHOT

[![Join the chat at https://gitter.im/kravchik/senjin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kravchik/senjin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Write glsl shaders on Groovy and Java with debugging, syntax highlighting, unit-testing, refactorings and full IDE support.

[Read more at Habrahabr.ru](http://habrahabr.ru/post/269591/)

Recent updates:

* glsl swizzles ```v.xz = v2.yy```
* (almost) all glsl functions
* run-time shader reloading
* custom shader functions

##mvn artifact
```xml
<dependencies>
    <dependency>
        <groupId>yk</groupId>
        <artifactId>senjin</artifactId>
        <version>0.13-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>yk</groupId>
        <artifactId>extmodule</artifactId>
        <version>0.1-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories>
    <repository>
        <id>yk.jcommon</id>
        <url>https://github.com/kravchik/mvn-repo/raw/master</url>
    </repository>
</repositories>
```


