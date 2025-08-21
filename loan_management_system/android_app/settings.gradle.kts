// The settings.gradle.kts file is where you specify the modules to include in your build.
// It's also the place to configure project-wide settings for Gradle.

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Loan Management App"
include(":app")
