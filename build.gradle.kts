@file:Suppress("SpellCheckingInspection", "GradleDependency", "AndroidGradlePluginVersion")

/*
 * This file configures the build system that creates your Android app.
 * The syntax is Kotlin, not Java.
 * You do not need to understand the contents of this file, nor should you modify it.
 * ALL CHANGES TO THIS FILE WILL BE OVERWRITTEN DURING OFFICIAL GRADING.
 */

plugins {
    id("com.android.application") version "8.1.2" apply false
    id("com.diffplug.spotless") version "6.22.0"
    java
}
spotless {
    java {
        googleJavaFormat("1.18.0")
        target("app/src/*/java/**/*.java")
    }
    kotlinGradle {
        ktlint("1.0.0")
        target("**/*.gradle.kts")
    }
}
