plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.bulkupcoach"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.bulkupcoach"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")

    // Amplify API and Datastore dependencies
    implementation ("com.amplifyframework:core:2.14.10")
    implementation ("com.amplifyframework:aws-analytics-pinpoint:2.14.9")
    implementation ("com.amplifyframework:aws-api:2.14.9")
    implementation ("com.amplifyframework:aws-auth-cognito:2.14.10")
    implementation ("com.amplifyframework:aws-datastore:2.14.9")
    implementation ("com.amplifyframework:aws-predictions:2.14.9")
    implementation ("com.amplifyframework:aws-storage-s3:2.14.9")
    implementation ("com.amplifyframework:aws-geo-location:2.14.9")
    implementation ("com.amplifyframework:aws-push-notifications-pinpoint:2.14.9")
    implementation ("com.amplifyframework:core-kotlin:2.14.6")


    //google login & calendar
    implementation ("com.google.oauth-client:google-oauth-client-jetty:1.23.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.apis:google-api-services-calendar:v3-rev305-1.23.0")

    // Authenticator dependency
    implementation ("com.amplifyframework.ui:authenticator:1.0.1")
    implementation ("com.amazonaws:aws-android-sdk-auth-userpools:2.29.0")

    //to avoid conflicts in libraries
    implementation ("com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude( group="org.apache.httpcomponents")
    }
    // Support for Java 8 features
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}