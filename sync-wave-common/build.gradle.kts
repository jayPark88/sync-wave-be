tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

dependencies {
    //spring
    //api로 선언된 의존성은 컴파일 시 클래스 경로에 포함되며, 다른 모듈에서 사용 가능합니다.
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")

    api("org.springframework.boot:spring-boot-starter-mail")
    api("jakarta.mail:jakarta.mail-api:2.1.2")

    api(group = "javax.validation", name = "validation-api", version = "2.0.1.Final")

    //jwt
    implementation(group = "io.jsonwebtoken", name = "jjwt-api", version = "0.11.5")
    runtimeOnly(group = "io.jsonwebtoken", name = "jjwt-impl", version = "0.11.5")
    runtimeOnly(group = "io.jsonwebtoken", name = "jjwt-jackson", version = "0.11.5")

    //slack
    implementation("com.slack.api:slack-api-client:1.44.1")

    // jackson
    //implementation으로 선언된 의존성은 컴파일 시 클래스 경로에 포함되지만, 해당 모듈을 사용하는 다른 모듈에서는 사용할 수 없습니다.
    api(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.15.3")
    api(group = "com.fasterxml.jackson.core", name = "jackson-core", version = "2.15.3")
    api(group = "com.fasterxml.jackson.core", name = "jackson-annotations", version = "2.15.3")
}
