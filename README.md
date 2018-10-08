# senjin
**SimpleENGINe**, developer version: 0.13-SNAPSHOT

[![Join the chat at https://gitter.im/kravchik/senjin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kravchik/senjin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
(welcome to chat if you have any questions!)

Write glsl shaders on Groovy and Java with debugging, syntax highlighting, unit-testing, refactorings and full IDE support.

### Simple examples:

Texture
```java
    SomeTexture texture = new SomeTexture(image);
    texture.enable(1);
    ...
    texture.disable();
```

Shader
```
```




### Recent updates:

10.18
+ AVbo, AVboTyped, AVbo indices
+ ARGB conversion and other SomeTexture improvements
+ ShaderUser 
* Numerous cleanups
* got rid of ReflectionVBO
* got rid of IndexBufferShort


12.17
+ edu mouse API
+ I Know OpenGL series of tutorials
+ VS and FS can be used separately
+ IndexBufferInt

[Read more at Habrahabr.ru](http://habrahabr.ru/post/269591/)

### mvn artifact
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
        <id>yk</id>
        <url>https://github.com/kravchik/mvn-repo/raw/master</url>
    </repository>
</repositories>
```


