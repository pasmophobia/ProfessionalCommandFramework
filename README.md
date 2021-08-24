# ProfessionalCommandFramework
Annotation->Brigadier framework
```gradle
plugins {
  id 'com.github.johnrengelman.shadow' version '6.0.0'
}
repositories {
  maven { url="jitpack.io }
}
dependencies {
  implementation 'com.github.propromp:ProfessionalCommandFramework:(latest-version)'
}
compileJava.options.compilerArgs.add("-parameters")
compileKotlin.kotlinOptions.javaParameters=true
```
# Gradle kts
```kts
plugins {
  id 'com.github.johnrengelman.shadow' version '6.0.0'
}
repositories {
  maven { url="jitpack.io }
}
dependencies {
  implementation 'com.github.propromp:ProfessionalCommandFramework:(latest-version)'
}
compileJava.options.compilerArgs.add("-parameters")
compileKotlin.kotlinOptions.javaParameters=true
```
# Maven
```xml
<repositories>
  <repository>
     <id>jitpack</id>
     <url>https://jitpack.io/</url>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>com.github.propromp</groupId>
    <artifactId>ProfessionalCommandFramework</artifactId>
    <version>(latest-version)</version>
    <scope>system</scope>
  </dependency>
</dependencies>

<plugins>
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.2.1</version>
    <configuration>
      <createDependencyReducedPom>false</createDependencyReducedPom>
    </configuration>
    <executions>
      <execution>
        <phase>package</phase>
        <goals>
          <goal>shade</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
</plugins>
