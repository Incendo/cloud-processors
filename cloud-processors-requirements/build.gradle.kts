plugins {
    id("cloud-processors.base-conventions")
    id("cloud-processors.publishing-conventions")
}

dependencies {
    implementation(libs.cloud.core)
    implementation(libs.cloud.annotations)
}
