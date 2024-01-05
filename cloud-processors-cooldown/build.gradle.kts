plugins {
    id("cloud-processors.base-conventions")
    id("cloud-processors.publishing-conventions")
}

dependencies {
    api(projects.cloudProcessorsCommon)

    compileOnly(libs.cloud.annotations)
}
