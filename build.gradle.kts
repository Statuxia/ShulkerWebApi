plugins {
    id("java")
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
}

group = "me.statuxia"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.postgresql:postgresql")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.testcontainers:testcontainers")
    implementation("org.testcontainers:postgresql")
    implementation("org.testcontainers:junit-jupiter")
    implementation("org.liquibase:liquibase-core")
    implementation("joda-time:joda-time:2.14.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.14.0")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Checkstyle> {
    source = fileTree("src/main/java")
}

checkstyle {
    toolVersion = "10.12.4"
}