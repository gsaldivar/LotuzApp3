plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    // ESTO DEBE COINCIDIR CON TUS CARPETAS
    namespace = "com.miapp.lotuz_2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.miapp.lotuz_2"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // URL base Xano (workspace api:XPPncXe7)
        buildConfigField("String", "XANO_BASE_URL", "\"https://x8ki-letl-twmt.n7.xano.io/api:XPPncXe7/\"")
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "DEMO_AUTO_LOGIN", "true")
            buildConfigField("String", "DEMO_ADMIN_USER", "\"admin@lotuz.cl\"")
            buildConfigField("String", "DEMO_ADMIN_PASS", "\"123456\"")
            buildConfigField("String", "DEMO_CLIENT_USER", "\"cliente@lotuz.cl\"")
            buildConfigField("String", "DEMO_CLIENT_PASS", "\"123456\"")
        }
        release {
            isMinifyEnabled = false
            buildConfigField("boolean", "DEMO_AUTO_LOGIN", "false")
            buildConfigField("String", "DEMO_ADMIN_USER", "\"\"")
            buildConfigField("String", "DEMO_ADMIN_PASS", "\"\"")
            buildConfigField("String", "DEMO_CLIENT_USER", "\"\"")
            buildConfigField("String", "DEMO_CLIENT_PASS", "\"\"")
        }
    }
}

dependencies {
    // Red
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Imágenes
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

    // UI
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Seguridad: almacenamiento cifrado
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
