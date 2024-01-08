plugins {
    id("cloud-processors.base-conventions")
    id("cloud-processors.publishing-conventions")
}

dependencies {
    api(projects.cloudProcessorsCommon)

    compileOnly(libs.cloud.annotations)
}

// TODO(City): Disable this
// we're getting errors on generated files due to -Werror :(
tasks.withType<JavaCompile> {
    options.compilerArgs.remove("-Werror")
}
