plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "jp.pois.crowpay"
        minSdkVersion(24)
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(androidX("core", "core-ktx", "1.3.2"))
    implementation(androidX("appcompat", "1.2.0"))
    implementation("com.google.android.material:material:1.3.0")
    implementation(androidX("constraintlayout", "2.0.4"))
    implementation(androidX("navigation", "navigation-fragment-ktx", "2.2.2"))
    implementation(androidX("navigation", "navigation-ui-ktx", "2.2.2"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation(androidX("test.ext", "junit", "1.1.2"))
    androidTestImplementation(androidX("test.espresso", "espresso-core", "3.3.0"))
}

fun androidX(group: String, name: String, version: String) = "androidx.$group:$name:$version"

fun androidX(group: String, version: String) = "androidx.$group:$group:$version"
