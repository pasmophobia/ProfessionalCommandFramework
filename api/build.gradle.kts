group = "net.propromp"
version = "2.0"

dependencies {
    compileOnly("com.mojang:brigadier:1.0.17")
}

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("pcf-api-v$version.jar")
    }
}