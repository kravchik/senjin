# senjin
## SimpleENGINe
developer version: 0.13-SNAPSHOT

[![Join the chat at https://gitter.im/kravchik/senjin](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kravchik/senjin?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
(welcome to chat if you have any questions!)

Write glsl shaders on Groovy and Java with debugging, syntax highlighting, unit-testing, refactorings and full IDE support.

The whole engine is built, actually, as a big library. For example, in your project, you can use only the texture element or only VBO. They work fine together but not bound. Even shader - can be used alone.
There is also examples of each element usage, each one no more than a few dozens of lines ([src/main/java/yk/senjin/examples/simple](https://github.com/kravchik/senjin/tree/master/src/main/java/yk/senjin/examples/simple)).

### Shaders

Each shader can be represented as a groovy class. Also, you are defining an input structure as a separate class. From here - you have full IDE support as they are fully valid groovy classes (refactorings, syntax check, etc). You can debug them (TODO example) and unit-test them. Senjine, eventually, converts them into shader src which also can be done at compile time to avoid spending time on startup and neediness to supply groovy runtime with a client.


## Simple examples:

*Vertex shader (groovy)*
```groovy
class AuiVS extends VertexShaderParent<Input, AuiFS.Input> {

    public Matrix4 modelViewProjectionMatrix;

    @Override
    void main(Input i, AuiFS.Input o) {
        o.gl_Position = modelViewProjectionMatrix * Vec4f(i.pos, 1)
        o.color = i.color
    }

    static class Input extends StandardVertexData {
        public Vec3f pos;
        public Vec4f color;
    }
}
```

*Fragment shader (groovy)*
```groovy
class AuiFS extends FragmentShaderParent<Input, StandardFSOutput> {

    @Override
    void main(Input i, StandardFSOutput o) {
        o.gl_FragColor = i.color;
    }

    static class Input extends StandardFragmentData {
        public Vec4f color;
    }
}
```
*Texture (can be used separately from shader and other systems)*
```java
    SomeTexture texture = new SomeTexture(image);
    texture.enable(1);
    ...
    texture.disable();
```

*VBO (Vertex Buffer Object) and Indices  (can be used separately from shader and other systems)*
```java
        vbo = new AVboTyped(
                new Ikogl_8_Vd(v3(-w, -h, 0)), 
                new Ikogl_8_Vd(v3( w,  0, 0)), 
                new Ikogl_8_Vd(v3( 0,  h, 0)));
        indices = AVboShortIndices.simple(3, GL_TRIANGLES);

        ...
        vbo.enable();
        indices.enable();
        ...
        vbo.disable();

```

*Shader rendering*
```
```
               
### no lwjgl64 in java.library.path
In order for native libs to become visible, you should either include native_libs directory in your IDE as a native library dependency, or define -Dorg.lwjgl.librarypath, or define -Djava.library.path.

### Note on debugging and unit testing
** TODO **

## Recent updates:

0.15
* removed some GProgram redundant init methods

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

## mvn artifact
```xml
<dependencies>
    <dependency>
        <groupId>yk</groupId>
        <artifactId>senjin</artifactId>
        <version>0.16-SNAPSHOT</version>
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


