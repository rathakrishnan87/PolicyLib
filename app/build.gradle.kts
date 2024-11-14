plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.cbre.privacyprompt"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }
    defaultConfig {
        minSdk = 24


        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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


publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.cbre.privacyprompt"
            artifactId = "privacy-prompt"
            version = "1.0.0"
            //artifact(tasks["sourcesJar"])
            //artifact(tasks["javadocJar"])
        }
    }
    repositories {
        maven {
//            url = uri("https://github.com/rathakrishnan87/PrivacyPrompt")

            url = layout.buildDirectory.dir("repo").get().asFile.toURI()
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}