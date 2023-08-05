allprojects {
    group = "mx.com.inftel.codegen2"
    version = "2.1.0-SNAPSHOT"
}

plugins {
    kotlin("jvm") version "1.8.20"
    `maven-publish`
    signing
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
    //withJavadocJar()
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

val kotlinJavadoc by tasks.registering(Jar::class) {
    archiveBaseName.set(project.name)
    archiveClassifier.set("javadoc")
    from(file("$projectDir/javadoc/README"))
}

publishing {
    repositories {
        maven {
            setUrl(file("$projectDir/build/repo"))
        }
    }

    publications {
        create<MavenPublication>("codegen2") {
            artifact(kotlinJavadoc)
            from(components["java"])
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name.set("${project.group}:${project.name}")
            description.set("Codegen2 APT")
            url.set("https://github.com/santoszv/codegen2")
            inceptionYear.set("2022")
            licenses {
                license {
                    name.set("Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
            developers {
                developer {
                    id.set("santoszv")
                    name.set("Santos Zatarain Vera")
                    email.set("santoszv@inftel.com.mx")
                    url.set("https://www.inftel.com.mx")
                }
            }
            scm {
                connection.set("scm:git:https://github.com/santoszv/codegen2")
                developerConnection.set("scm:git:https://github.com/santoszv/codegen2")
                url.set("https://github.com/santoszv/codegen2")
            }
        }
        signing.sign(this)
    }
}

signing {
    useGpgCmd()
}