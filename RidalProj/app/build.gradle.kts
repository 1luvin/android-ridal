plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
//    id("org.jetbrains.kotlin.jvm")
}


android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId("tv.ridal")
        minSdkVersion(23)
        targetSdkVersion(30)
        versionCode(1)
        versionName("1.0")

        testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
    }

    buildTypes {
        getByName("release") {
            minifyEnabled(false)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val kotlinVersion = "1.6.10"
    val roomVersion = "2.3.0"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.appcompat:appcompat:1.3.1")

    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.fragment:fragment:1.2.0")
    
    implementation("com.google.android.material:material:1.4.0")

    // jsoup
    implementation("org.jsoup:jsoup:1.13.1")
    // multi back stack navigation
    implementation("com.tunjid.androidx:navigation:1.0.0-rc03")
    // volley
    implementation("com.android.volley:volley:1.2.1")
    // Coil Image Loader
    implementation("io.coil-kt:coil:1.3.2")
    // Room
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    // lifecycleScope:
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha04")
    // serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")

}
































//
repositories {
    mavenCentral()
}