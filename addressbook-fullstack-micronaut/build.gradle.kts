import io.kvision.gradle.KVisionPlugin
import io.kvision.gradle.tasks.KVWorkerBundleTask
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
//import com.github.node-gradle.node.task.NodeTask

buildscript {
    dependencies {
        classpath("com.github.node-gradle:gradle-node-plugin:3.0.0")
    }
}

plugins {
    kotlin("plugin.serialization")
    kotlin("multiplatform")
    kotlin("plugin.allopen")
    kotlin("kapt")
    id("com.github.johnrengelman.shadow")
    id("io.kvision")
    id("io.micronaut.application")
    id("io.micronaut.aot")
}

pluginManager.withPlugin("io.kvision") {
    println("hejhejhej\n\n")
    //val kvp = this as KVisionPlugin
    when (this) {
        is KVisionPlugin -> println("kakakkakaka")
        else -> println("kukukuku")
    }
}

// Versions
val kotlinVersion: String by System.getProperties()
val kvisionVersion: String by System.getProperties()
val micronautVersion: String by project
val coroutinesVersion: String by project
val springSecurityCryptoVersion: String by project
val springDataR2dbcVersion: String by project
val r2dbcPostgresqlVersion: String by project
val r2dbcH2Version: String by project
val reactorKotlinExtensionsVersion: String by project
val reactorAdapterVersion: String by project
val kweryVersion: String by project
val h2DatabaseVersion: String by project

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "io.r2dbc") {
            useVersion(r2dbcH2Version)
        }
    }
}

val mainClassNameVal = "com.example.MainKt"
application {
    mainClass.set(mainClassNameVal)
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

tasks.register("installNpmDependencies") {
    // Specify the command to execute
    val commands = mapOf(
        "mkdir" to "mkdir",
        "gettext" to "npm")

    // Specify the arguments for the command
    val argumentsMap = mapOf(
        "mkdir" to listOf("-p", rootProject.buildDir.absolutePath + "/js"),
        "gettext" to listOf("install", "--prefix", rootProject.buildDir.absolutePath + "/js", "gettext.js"))

    // Define the task action
    doLast {
        commands.forEach { name, command ->
            val arguments = argumentsMap[name]!!
            // Create ProcessBuilder for running the shell command
            val processBuilder = ProcessBuilder(command, *arguments.toTypedArray())

            // Set the working directory if necessary
            processBuilder.directory(File(rootProject.projectDir, ""))

            // Redirect standard output and error to Gradle's logger
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE)
            processBuilder.redirectError(ProcessBuilder.Redirect.PIPE)

            // Start the process
            val process = processBuilder.start()

            // Wait for the process to finish and capture the exit code
            val exitCode = process.waitFor()

            // Log the output
            val output = process.inputStream.bufferedReader().readText()
            val errorOutput = process.errorStream.bufferedReader().readText()
            if ("" != output)
                logger.lifecycle("Script output:\n$output")
            if ("" != errorOutput)
                logger.log(LogLevel.ERROR, "Script error output:\n$errorOutput")

            // Check the exit code and throw an exception if non-zero
            if (exitCode != 0) {
                throw GradleException("Script execution failed with exit code $exitCode")
            }
        }
    }
}
tasks.getByName("convertPoToJson").dependsOn("installNpmDependencies")


tasks.register("copyFilesNeededForWebpack") {
    // Specify the command to execute
    val commands = mapOf(
        "mkdir" to "mkdir",
        "cp" to "cp")

    // Specify the arguments for the command
    val baseDestPath = rootProject.buildDir.absolutePath + "/js/packages/" + rootProject.name + "-" + project.name + "/kotlin"
    val baseSourcePath = project.projectDir.absolutePath + "/src/jsMain/resources"
    val argumentsMap = mapOf(
        "mkdir" to listOf("-p", "$baseDestPath/i18n $baseDestPath/css"),
        "cp" to listOf("$baseSourcePath/i18n/messages-en.json", "$baseDestPath/i18n/"))

    // Define the task action
    doLast {
        commands.forEach { (name, command) ->
            logger.log(LogLevel.ERROR, "name $name:")
            val arguments = argumentsMap[name]!!
            // Create ProcessBuilder for running the shell command
            val processBuilder = ProcessBuilder(command, *arguments.toTypedArray())

            // Set the working directory if necessary
            processBuilder.directory(File(rootProject.projectDir, ""))

            // Redirect standard output and error to Gradle's logger
            processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE)
            processBuilder.redirectError(ProcessBuilder.Redirect.PIPE)

            // Start the process
            val process = processBuilder.start()

            // Wait for the process to finish and capture the exit code
            val exitCode = process.waitFor()

            // Log the output
            val output = process.inputStream.bufferedReader().readText()
            val errorOutput = process.errorStream.bufferedReader().readText()
            if ("" != output)
                logger.lifecycle("Script output:\n$output")
            if ("" != errorOutput)
                logger.log(LogLevel.ERROR, "Script error output:\n$errorOutput")

            // Check the exit code and throw an exception if non-zero
            if (exitCode != 0) {
                throw GradleException("Script execution failed with exit code $exitCode")
            }
        }
    }
}
//tasks.getByName("build").dependsOn("copyFilesNeededForWebpack")

tasks.register("runScript") {
    // Specify the command to execute
    val command = "./copyFilesNeededForWebpack.sh"

    // Specify the arguments for the command
    //val destPath = rootProject.buildDir.absolutePath + "/js/packages/" + rootProject.name + "-" + project.name
    //val sourcePath = project.projectDir.absolutePath
    val arguments = listOf(rootProject.projectDir.absolutePath)

    // Define the task action
    doLast {
        // Create ProcessBuilder for running the shell command
        val processBuilder = ProcessBuilder(command, *arguments.toTypedArray())
        // Set the working directory if necessary
        processBuilder.directory(File(rootProject.projectDir, "/scripts"))

        // Redirect standard output and error to Gradle's logger
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE)
        processBuilder.redirectError(ProcessBuilder.Redirect.PIPE)

        // Start the process
        val process = processBuilder.start()

        // Wait for the process to finish and capture the exit code
        val exitCode = process.waitFor()

        // Log the output
        val output = process.inputStream.bufferedReader().readText()
        val errorOutput = process.errorStream.bufferedReader().readText()
        logger.lifecycle("Script output:\n$output")
        logger.lifecycle("Script error output:\n$errorOutput")

        // Check the exit code and throw an exception if non-zero
        if (exitCode != 0) {
            throw GradleException("Script execution failed with exit code $exitCode")
        }
    }
}


kotlin {
    jvmToolchain(17)
    jvm {
        withJava()
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = listOf("-Xjsr305=strict")
                javaParameters = true
            }
        }
    }
    js(IR) {
        browser {
            runTask(Action {
                mainOutputFileName = "main.bundle.js"
                sourceMaps = false
                devServer = KotlinWebpackConfig.DevServer(
                    open = false,
                    port = 3000,
                    proxy = mutableMapOf(
                        "/kv/*" to "http://localhost:8080",
                        "/login" to "http://localhost:8080",
                        "/logout" to "http://localhost:8080",
                        "/kvws/*" to mapOf("target" to "ws://localhost:8080", "ws" to true)
                    ),
                    static = mutableListOf("${layout.buildDirectory.asFile.get()}/processedResources/js/main")
                )
            })
            webpackTask(Action {
                mainOutputFileName = "main.bundle.js"
                //inputFilesDirectory.dir("test")

            })
            testTask(Action {
                useKarma {
                    useChromeHeadless()
                }
            })
        }
        binaries.executable()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.kvision:kvision-server-micronaut:$kvisionVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("reflect"))
                implementation("io.micronaut:micronaut-inject")
                implementation("io.micronaut.validation:micronaut-validation")
                implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
                implementation("io.micronaut:micronaut-runtime")
                implementation("io.micronaut:micronaut-http-server-netty")
                implementation("io.micronaut.session:micronaut-session")
                implementation("io.micronaut.security:micronaut-security-session")
                implementation("io.micronaut:micronaut-jackson-databind")
                implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
                implementation("jakarta.validation:jakarta.validation-api")
                implementation("ch.qos.logback:logback-classic")
                implementation("org.yaml:snakeyaml")
                implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:$reactorKotlinExtensionsVersion")
                implementation("io.projectreactor.addons:reactor-adapter:$reactorAdapterVersion")
                implementation("org.springframework.security:spring-security-crypto:$springSecurityCryptoVersion")
                implementation("org.springframework.data:spring-data-r2dbc:$springDataR2dbcVersion")
                implementation("org.postgresql:r2dbc-postgresql:$r2dbcPostgresqlVersion")
                implementation("io.r2dbc:r2dbc-h2:$r2dbcH2Version")
                implementation("com.github.andrewoma.kwery:core:$kweryVersion")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("org.junit.jupiter:junit-jupiter-api")
                implementation("io.micronaut.test:micronaut-test-junit5")
                implementation("org.junit.jupiter:junit-jupiter-engine")
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("io.kvision:kvision:$kvisionVersion")
                implementation("io.kvision:kvision-bootstrap:$kvisionVersion")
                implementation("io.kvision:kvision-state:$kvisionVersion")
                implementation("io.kvision:kvision-fontawesome:$kvisionVersion")
                implementation("io.kvision:kvision-i18n:$kvisionVersion")
            }
        }
        //jsMain.resources.srcDir("src/jsMain/web")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
                implementation("io.kvision:kvision-testutils:$kvisionVersion")
            }
        }
    }
}

tasks.getByName("jsBrowserProductionWebpack").dependsOn("runScript")
// org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

val rootNodeModulesDir = objects.directoryProperty().convention(rootProject.layout.buildDirectory.dir("js/node_modules/"))

tasks.withType<KVWorkerBundleTask>().configureEach {
    webpackJs.set(
        rootNodeModulesDir.file("./webpack/bin/webpack.js")
    )
    webpackConfigJs.set(
        rootProject.layout.buildDirectory.file("js/packages/${rootProject.name}-worker/webpack.config.js")
    )
    args(
        webpackJs.get(),
        "--config",
        webpackConfigJs.get(),
    )
}

micronaut {
    runtime("netty")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    aot {
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}

tasks {
    withType<JavaExec> {
        jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
        if (gradle.startParameter.isContinuous) {
            systemProperties(
                mapOf(
                    "micronaut.io.watch.restart" to "true",
                    "micronaut.io.watch.enabled" to "true",
                    "micronaut.io.watch.paths" to "src/jvmMain"
                )
            )
        }
    }
}

kapt {
    arguments {
        arg("micronaut.processing.incremental", true)
        arg("micronaut.processing.annotations", "com.example.*")
        arg("micronaut.processing.group", "com.example")
        arg("micronaut.processing.module", "template-fullstack-micronaut")
    }
}

dependencies {
    "kapt"(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    "kapt"("io.micronaut:micronaut-inject-java")
    "kapt"("io.micronaut.validation:micronaut-validation")
    "kaptTest"(platform("io.micronaut.platform:micronaut-platform:$micronautVersion"))
    "kaptTest"("io.micronaut:micronaut-inject-java")
}

