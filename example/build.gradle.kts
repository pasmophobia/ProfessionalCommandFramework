group = "net.propromp.pcf-example"
version = "1.0"

val implementation by configurations
dependencies {
    implementation(project(":api"))
}
tasks {
    processResources {
        filesMatching("*.yml") {
            expand(project.properties)
        }

        from(sourceSets.main.get().resources.srcDirs) {
            filter {
                it.replace("@version@","1.16.5-R0.1-SNAPSHOT")
            }
        }
    }
    shadowJar {
        archiveFileName.set("pcf-example-v$version.jar")
    }
    create<Copy>("buildToPluginsDirectory") {
        File("server/plugins").listFiles()?.forEach{
            if(it.name.contains("pcf-example-v")&&it.isFile){
                it.delete()
            }
        }
        from(shadowJar)
        into("server/plugins")
    }
}