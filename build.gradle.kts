// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    kotlin("plugin.serialization") version "2.0.0"
    alias(libs.plugins.compose.compiler) apply false

}

buildscript {
    dependencies {
        classpath(libs.kotlin.gradle.plugin)
    }
}
