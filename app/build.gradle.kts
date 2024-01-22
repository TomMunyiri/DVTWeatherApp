import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.ktlint)
}

tasks.check {
    dependsOn("ktlintCheck")
}

ktlint {
    version = "0.42.1"
    ignoreFailures = false
}

android {
    val properties = Properties()
    properties.load(rootProject.file("local.properties").inputStream())
    namespace = "com.tommunyiri.dvtweatherapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tommunyiri.dvtweatherapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            // arg(RoomSchemaArgProvider(File(projectDir, "schemas")))
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
        buildConfigField("String", "ALGOLIA_APP_ID", properties.getProperty("ALGOLIA_APP_ID"))
        buildConfigField("String", "ALGOLIA_API_KEY", properties.getProperty("ALGOLIA_API_KEY"))
        buildConfigField(
            "String",
            "ALGOLIA_INDEX_NAME",
            properties.getProperty("ALGOLIA_INDEX_NAME")
        )
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
        buildConfig = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.swiperefreshlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    // work manager
    implementation(libs.work.runtime.ktx)
    // Optional testing dependency
    androidTestImplementation(libs.work.testing)
    // coroutines
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    // network and serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    debugImplementation(libs.chuck)
    releaseImplementation(libs.chuck.no.op)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.gson)
    // room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // preferences
    implementation(libs.androidx.preference)
    // gms location services
    implementation(libs.gms.play.location)
    // timber
    implementation(libs.timber)
    // Navigation Components
    implementation(libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    // lifecycle livedata ktx
    implementation(libs.lifecycle.livedata.ktx)
    // algolia search
    implementation(libs.algolia)
    // paging
    implementation(libs.androidx.paging)
    // google-maps
    implementation(libs.google.maps)
}

class RoomSchemaArgProvider(
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    val schemaDir: File
) : CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}