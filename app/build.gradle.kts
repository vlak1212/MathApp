plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.ocr2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ocr2"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    viewBinding {
        var enabled = true
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
}

dependencies {
    implementation ("com.google.mlkit:text-recognition:16.0.0")
    // implementation ("com.google.android.gms:play-services-gemini:1.0.0")
    // implementation("com.google.mlkit:ocr:16.0.0")
    // implementation("com.google.android.gms:play-services-mlkit-text-recognition:18.0.0")
    // implementation("org.tensorflow:tensorflow-lite:0.0.0-nightly")
    implementation ("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.jjoe64:graphview:4.2.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.rmtheis:tess-two:9.0.0")
    implementation("net.objecthunter:exp4j:0.4.8")
    implementation("com.google.ai.client.generativeai:generativeai:0.6.0")
    implementation("com.google.guava:guava:31.0.1-android")
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("androidx.room:room-runtime:2.5.0")

    annotationProcessor("androidx.room:room-compiler:2.5.0")

}
