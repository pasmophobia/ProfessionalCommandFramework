group = "net.propromp.pcf-example"
version = "1.0"

val implementation by configurations
dependencies {
    implementation(project(":api"))
}
tasks {
    compileJava {
        options.compilerArgs.add("-parameters")
    }
    compileKotlin {
        kotlinOptions.javaParameters = true
    }
}