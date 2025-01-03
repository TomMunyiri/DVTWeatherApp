import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinParcelize)
    //alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    alias(libs.plugins.ktlint)
    id("org.sonarqube")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
    alias(libs.plugins.kotlin.compose)
}

tasks.check {
    dependsOn("ktlintCheck")
}

ktlint {
    android = true
    ignoreFailures = false
    reporters {
        reporter(ReporterType.JSON)
    }
}

sonar {
    val properties = Properties()
    properties.load(rootProject.file("local.properties").inputStream())
    val sonarqubeToken = properties.getProperty("SONAR_TOKEN")
    val sonarqubeUrl = properties.getProperty("SONAR_HOST_URL")
    properties {
        property("sonar.projectKey", "DVT-Weather-App")
        property("sonar.projectName", "DVT Weather App")
        property("sonar.host.url", sonarqubeUrl)
        property("sonar.language", "kotlin")
        property("sonar.sources", "src/main/java")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.token", sonarqubeToken)
        property(
            "sonar.coverage.exclusions",
            "**/*Test*/**,' +\n" + "'*.json,' +\n" + "'**/*test*/**,' +\n" + "'**/.gradle/**,' +\n" + "'**/R.class,",
        )
        property("sonar.androidLint.reportPaths", "lint-results-debug.xml")
    }
}

android {
    val properties = Properties()
    properties.load(rootProject.file("local.properties").inputStream())
    namespace = "com.tommunyiri.dvtweatherapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.tommunyiri.dvtweatherapp"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }

        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
        buildConfigField("String", "ALGOLIA_APP_ID", properties.getProperty("ALGOLIA_APP_ID"))
        buildConfigField("String", "ALGOLIA_API_KEY", properties.getProperty("ALGOLIA_API_KEY"))
        buildConfigField(
            "String",
            "ALGOLIA_INDEX_NAME",
            properties.getProperty("ALGOLIA_INDEX_NAME"),
        )
        buildConfigField("String", "BASE_URL", properties.getProperty("BASE_URL"))
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = true
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
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
        //compose = true
    }
    lint {
        baseline = file("lint-baseline.xml")
    }

    //Commented this after upgrading to kotlin 2.0.0+ coz it's no longer needed
    /*composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }*/

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // hilt
    implementation(libs.hilt.android)
    androidTestImplementation(platform(libs.compose.bom))
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    // work manager
    implementation(libs.work.runtime.ktx)
    // Optional testing dependency
    androidTestImplementation(libs.work.testing)
    // coroutines
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.coroutines.android)
    testImplementation(libs.kotlinx.coroutines.test)
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
    implementation(libs.algolia.instantsearch.compose)
    implementation(libs.algolia.instantsearch.android.paging3)
    // implementation(libs.algoliasearch.client.kotlin)
    // paging
    implementation(libs.androidx.paging)
    // google-maps
    implementation(libs.google.maps)
    // unit-testing
    implementation(libs.mockito.core)
    // room-testing
    // testImplementation(libs.room.testing)
    // hamcrest
    testImplementation(libs.hamcrest)
    // roboelectric
    testImplementation(libs.roboelectric)
    // jetpack-compose
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.material3.window.size)
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.runtime.livedata)
    // accompanist-permissions
    implementation(libs.google.accompanist.permissions)
    // accompanist-swipe-refresh
    implementation(libs.google.accompanist.swipe.refresh)
    // sweet compose
    implementation(libs.composable.sweet.toast)
    // lottie compose
    implementation(libs.lottie.compose)
    // maps compose
    implementation(libs.maps.compose)
}

class RoomSchemaArgProvider(
    @get:InputDirectory @get:PathSensitive(PathSensitivity.RELATIVE) val schemaDir: File,
) :
    CommandLineArgumentProvider {
    override fun asArguments(): Iterable<String> {
        return listOf("room.schemaLocation=${schemaDir.path}")
    }
}
