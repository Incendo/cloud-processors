plugins {
    id("cloud-processors.base-conventions")
    id("cloud-processors.publishing-conventions")
}

dependencies {
    api(libs.cloud.core)
    compileOnly(libs.cloud.annotations)

    compileOnly(libs.caffeine)
    compileOnly(libs.guava)
}
