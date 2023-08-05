plugins {
    java
    `maven-publish`
    signing
}

dependencies {

    // Jakarta EE
    compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8"
}

publishing {
    repositories {
        maven {
            setUrl(file("${rootProject.projectDir}/build/repo"))
        }
    }

    publications {
        create<MavenPublication>("codegen2") {
            from(components["java"])
        }
    }

    publications.withType<MavenPublication> {
        pom {
            name.set("${project.group}:${project.name}")
            description.set("Codegen2 API")
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