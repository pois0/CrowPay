import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.5.21"
    id("dagger.hilt.android.plugin")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
}

object Versions {
    object AndroidX {
        const val navigation = "2.3.5"
        const val room = "2.3.0"
        const val work = "2.5.0"
        const val lifecycle = "2.2.0"
        const val paging = "3.0.0"
        const val appcompat = "1.3.0"
        const val constraintlayout = "2.0.4"
        const val core = "1.5.0"
    }

    object KotlinX {
        const val coroutines = "1.5.1"
        const val serialization = "1.2.2"
    }

    const val hilt = "2.37"
    const val materialDesign = "1.4.0"
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "jp.pois.crowpay"
        minSdkVersion(26)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "alpha1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    lintOptions {
        isAbortOnError = false
    }

    sourceSets.all {
        java.srcDir("src/$name/kotlin")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlinX("coroutines-core", Versions.KotlinX.coroutines))
    implementation(kotlinX("coroutines-android", Versions.KotlinX.coroutines))
    implementation(kotlinX("serialization-protobuf", Versions.KotlinX.serialization))

    testImplementation("junit:junit:4.13.2")

    implementation("com.google.dagger:hilt-android:${Versions.hilt}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hilt}")
    androidTestImplementation("com.google.dagger:hilt-android-testing:${Versions.hilt}")

    implementation(androidX("core", "core-ktx", Versions.AndroidX.core))
    implementation(androidX("appcompat", Versions.AndroidX.appcompat))
    implementation("com.google.android.material:material:${Versions.materialDesign}")
    implementation(androidX("constraintlayout", Versions.AndroidX.constraintlayout))
    implementation(androidX("navigation", "navigation-fragment", Versions.AndroidX.navigation))
    implementation(androidX("navigation", "navigation-fragment-ktx", Versions.AndroidX.navigation))
    implementation(androidX("navigation", "navigation-ui", Versions.AndroidX.navigation))
    implementation(androidX("navigation", "navigation-ui-ktx", Versions.AndroidX.navigation))
    implementation(androidX("work", "work-runtime-ktx", Versions.AndroidX.work))
    implementation(androidX("lifecycle", "lifecycle-extensions", Versions.AndroidX.lifecycle))
    implementation(androidX("lifecycle", "lifecycle-livedata-ktx", Versions.AndroidX.lifecycle))
    implementation(androidX("lifecycle", "lifecycle-viewmodel-ktx", Versions.AndroidX.lifecycle))

    implementation(androidX("room", "room-ktx", Versions.AndroidX.room))
    implementation(androidX("room", "room-runtime", Versions.AndroidX.room))
    kapt(androidX("room", "room-compiler", Versions.AndroidX.room))
    annotationProcessor(androidX("room", "room-compiler", Versions.AndroidX.room))
    androidTestImplementation(androidX("room", "room-testing", Versions.AndroidX.room))

    implementation("com.google.android.gms:play-services-nearby:18.0.0")

    androidTestImplementation(androidX("test.ext", "junit", "1.1.2"))
    androidTestImplementation(androidX("test.espresso", "espresso-core", "3.3.0"))
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

fun androidX(group: String, name: String, version: String) = "androidx.$group:$name:$version"

fun androidX(group: String, version: String) = "androidx.$group:$group:$version"

fun kotlinX(name: String, version: String) = "org.jetbrains.kotlinx:kotlinx-$name:$version"
