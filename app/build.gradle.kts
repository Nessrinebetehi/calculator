plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}


android {
    namespace = "com.example.calculator"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.calculator"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}


dependencies {


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.jjoe64:graphview:4.2.2")
    // Firebase
    implementation ("com.google.firebase:firebase-database:20.3.0")
    implementation ("com.google.firebase:firebase-core:21.1.1")

    implementation ("org.mariuszgromada.math:MathParser.org-mXparser:5.0.6")
    implementation ("com.jjoe64:graphview:4.2.2")

    implementation ("org.mariuszgromada.math:MathParser.org-mXparser:5.2.1' // Pour évaluer les expressions")
    implementation ("com.google.firebase:firebase-database:20.3.0' // Pour Firebase")
    implementation ("androidx.fragment:fragment:1.5.7")

    implementation ("com.google.android.material:material:1.9.0")

    implementation ("org.mariuszgromada.math:MathParser.org-mXparser:5.2.1")


        implementation ("androidx.cardview:cardview:1.0.0")




    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}