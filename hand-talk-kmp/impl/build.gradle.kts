plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget {
        publishLibraryVariants("release")
        /*compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }*/
    }

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":hand-talk-kmp:domain"))
            implementation(project(":hand-talk-kmp:utils"))
            implementation(libs.kotlinx.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.mediapipe.tasks.vision)
            implementation(libs.androidx.camera.core)
            implementation(libs.androidx.camera.lifecycle)
            implementation(libs.androidx.camera.view)
            implementation(libs.androidx.camera.camera2)
            implementation(libs.androidx.core.ktx)
        }
        iosMain.dependencies {
        }
    }
}

android {
    namespace = "com.typ.handtalk.impl"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}
