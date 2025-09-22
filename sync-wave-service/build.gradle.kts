apply(plugin = "java")
apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")
apply(plugin = "idea")
apply(plugin = "eclipse")

group = "com.parker"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    // core-module dependency
    implementation(project(":sync-wave-common"))
    //mysql
    implementation("mysql:mysql-connector-java:8.0.33")
    //swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    testImplementation("com.h2database:h2")
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
        resources {
            srcDirs("src/main/resources")
        }
    }
    test {
        java {
            srcDirs("src/test/java")
        }
        resources {
            srcDirs("src/test/resources")
        }
    }
}

tasks.bootJar {
    enabled = true
    mainClass.set("com.parker.service.SyncWaveServiceApplication")
}

tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
    module {
        sourceDirs.plusAssign(file("src/main/java"))
        testSourceDirs.plusAssign(file("src/test/java"))
        resourceDirs.plusAssign(file("src/main/resources"))
        testResourceDirs.plusAssign(file("src/test/resources"))
    }
}
