import de.undercouch.gradle.tasks.download.Download

plugins {
    `maven-publish`
}

val name = "pcf-api"
val githubUsername = System.getenv("GITHUB_USERNAME")
val githubToken = System.getenv("GITHUB_TOKEN")
group = "net.propromp.professionalcommandframework"
version = "1.0"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/Propromp/ProfessionalCommandFramework")
            credentials {
                username = githubUsername
                password = githubToken
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            artifactId="pcf"
            from(components["java"])
        }
    }
}


dependencies {
    compileOnly("com.mojang:brigadier:1.0.17")
}

tasks {
    compileJava {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.javaParameters=true
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("$name v$archiveVersion.jar")
    }
}