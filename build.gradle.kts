plugins {
  kotlin("multiplatform") version "1.7.21"
  id("org.jetbrains.dokka") version "1.7.20"
  `maven-publish`
  signing
}

group = "io.foxcapades.lib"
version = "0.1.0"
description = "A simple stack implementation."

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
  jvm {
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    withJava()
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
    }
  }
  js(IR) {
    compilations.all {
      packageJson {
        customField("description", project.description!!)
      }
      browser()
      nodejs()
      binaries.executable()
    }
  }
  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux"    -> linuxX64("native")
    isMingwX64           -> mingwX64("native")
    else                 -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }
    val jvmMain by getting
    val jvmTest by getting
    val jsMain by getting
    val jsTest by getting
    val nativeMain by getting
    val nativeTest by getting
  }
}

tasks.dokkaHtml {
  outputDirectory.set(file("docs/dokka/${project.version}"))
}

val javadocJar = tasks.register<Jar>("javadocJar") {
  dependsOn(tasks.dokkaHtml)
  archiveClassifier.set("javadoc")
  from(file("docs/dokka/${project.version}"))
}

publishing {
  repositories {
    maven {
      name = "GitHub"
      url = uri("https://maven.pkg.github.com/k-libs/k-stack")
      credentials {
        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
      }
    }

    maven {
      name = "Sonatype"
      url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
      credentials {
        username = project.findProperty("nexus.user") as String? ?: System.getenv("NEXUS_USER")
        password = project.findProperty("nexus.pass") as String? ?: System.getenv("NEXUS_PASS")
      }
    }
  }

  publications {
    create<MavenPublication>("gpr") {
      from(components["java"])
      artifact(javadocJar)
      pom {
        name.set("MD5")
        description.set("Convenient MD5 hashing.")
        url.set("https://github.com/k-libs/k-stack")

        licenses {
          license {
            name.set("MIT")
          }
        }

        developers {
          developer {
            id.set("epharper")
            name.set("Elizabeth Paige Harper")
            email.set("foxcapades.io@gmail.com")
            url.set("https://github.com/foxcapades")
          }
        }

        scm {
          connection.set("scm:git:git://github.com/k-libs/k-stack.git")
          developerConnection.set("scm:git:ssh://github.com/k-libs/k-stack.git")
          url.set("https://github.com/k-libs/k-stack")
        }
      }
    }
  }
}

signing {
  useGpgCmd()

  sign(configurations.archives.get())
  sign(publishing.publications["gpr"])
}
