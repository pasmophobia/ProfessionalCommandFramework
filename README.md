# ProfessionalCommandFramework
Annotation->Brigadier framework
# Gradle
```
plugins {
  id 'com.github.johnrengelman.shadow' version '6.0.0'
}
repositories {
  maven { url="jitpack.io }
}
dependencies {
  implementation 'com.github.propromp:ProfessionalCommandFramework:2.0'
}
compileJava.options.compilerArgs.add("-parameters")
compileKotlin.kotlinOptions.javaParameters=true
```
# Gradle kts
```
plugins {
  id("com.github.johnrengelman.shadow") version "5.2.0"
}
repositories {
  maven("jitpack.io")
}
dependencies {
  implementation("com.github.propromp:ProfessionalCommandFramework:2.0")
}
tasks {
  compileJava {
    options.compilerArgs.add("-parameters")
  }
  compileKotlin {
    kotlinOptions.javaParameters = true
  }
}
```
# Maven
