enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    includeBuild("gradle/build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots/") {
            name = "sonatypeOssSnapshots"
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}

rootProject.name = "cloud-processors-parent"

include(":cloud-processors-common")
include(":cloud-processors-confirmation")
include(":cloud-processors-cooldown")
include(":cloud-processors-requirements")
