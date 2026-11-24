import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    jacoco
}

android {
    namespace = "cl.duoc.albalab"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "cl.duoc.albalab"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // api-gateway del backend. 10.0.2.2 es el "localhost" del PC visto desde el emulador.
        // En un teléfono físico se reemplaza por la IP del PC en la red (ej: http://192.168.1.10:9090/).
        buildConfigField("String", "API_BASE_URL", "\"http://10.0.2.2:9090/\"")
    }

    // Firma del APK release con app/albalab.jks; los datos vienen de keystore.properties (raíz del proyecto).
    val firma = Properties().apply {
        val archivo = rootProject.file("keystore.properties")
        if (archivo.exists()) archivo.inputStream().use { load(it) }
    }
    signingConfigs {
        create("release") {
            storeFile = file(firma.getProperty("storeFile", "albalab.jks"))
            storePassword = firma.getProperty("storePassword")
            keyAlias = firma.getProperty("keyAlias")
            keyPassword = firma.getProperty("keyPassword")
        }
    }

    buildTypes {
        debug {
            // Genera el .exec de cobertura al correr los tests unitarios (lo usa la tarea jacocoLogica).
            enableUnitTestCoverage = true
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            // Sin R8: ofuscar rompería los nombres de campos que Gson usa para leer el JSON de las APIs.
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil.compose)
    implementation("androidx.biometric:biometric:1.1.0")
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    testImplementation(libs.mockk)
}
// Cobertura de la LÓGICA de la app (lo que pide la rúbrica: ViewModels y repositorios, más la capa remota y los formatos).
// Se excluye la UI de Compose, el código generado (Room, BuildConfig, R), las DTOs (solo datos), las factories que
// necesitan un dispositivo y RedAlbaLab (solo arma los clientes Retrofit).
// Uso: ./gradlew jacocoLogica  →  app/build/reports/jacoco/jacocoLogica/html/index.html
tasks.register<JacocoReport>("jacocoLogica") {
    dependsOn("testDebugUnitTest")
    group = "verification"
    description = "Reporte de cobertura de ViewModels, repositorios y capa remota"
    reports {
        html.required.set(true)
        xml.required.set(true)
    }
    val clases = fileTree(layout.buildDirectory.dir("intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes")) {
        include(
            "cl/duoc/albalab/viewmodel/**",
            "cl/duoc/albalab/repository/**",
            "cl/duoc/albalab/data/remote/**",
            "cl/duoc/albalab/utils/FormatoClp*"
        )
        exclude("**/*\$Companion*", "**/dto/**", "**/RedAlbaLab*", "**/R.class", "**/R$*.class", "**/BuildConfig*")
    }
    classDirectories.setFrom(clases)
    sourceDirectories.setFrom(files("src/main/java"))
    executionData.setFrom(layout.buildDirectory.file("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec"))
}
