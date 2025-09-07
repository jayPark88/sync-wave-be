plugins {
    java
    id("org.springframework.boot") version "3.2.3"
    id("io.spring.dependency-management") version "1.1.4"
    idea
    eclipse
}

repositories {
    mavenCentral()
}

tasks.bootJar {
    enabled = false
}

subprojects {
    group = "com.parker"
    version = "0.0.1-SNAPSHOT"
    
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "eclipse")
    // build.gradle에서 api() 를 사용하려면 java-library 사용
    apply(plugin = "java-library")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    configurations {
        named("compileOnly") {
            extendsFrom(configurations.named("annotationProcessor").get())
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        //lombok (core, web둘다 사용을 해야 되서 root에 선언)
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        testCompileOnly("org.projectlombok:lombok") // 테스트 환경애서 lombok 사용을 위해서 의존성 추가
        testAnnotationProcessor("org.projectlombok:lombok") // 테스트 코드에서 Lombok 어노테이션을 사용할 때 Lombok 어노테이션 프로세서를 활성화하기 위한 의존성을 추가
        testImplementation("org.springframework.boot:spring-boot-starter-test")//  테스트 코드를 컴파일하고 실행하는 데 필요한 의존성을 나타냅니다. Spring Boot 프로젝트의 테스트 관련 기능을 사용하기 위해 해당 라이브러리가 필요합니다.

        //retrofit2(core, web둘다 사용을 해야 되서 root에 선언)
        implementation("com.squareup.retrofit2:retrofit:2.7.2")
        //요청,응답 객체를 파싱해주는 라이브러리(gson을 이용함)
        implementation("com.squareup.retrofit2:converter-gson:2.7.2")
    }

    tasks.test {
        useJUnitPlatform()
    }

    configure<org.gradle.plugins.ide.idea.model.IdeaModel> {
        module {
            inheritOutputDirs = false
            outputDir = file("${buildDir}/classes/java/main/")
            testOutputDir = file("${buildDir}/classes/java/test/")
        }
    }
}
