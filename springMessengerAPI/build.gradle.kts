import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.3-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.0"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.8.20-Beta"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"

}

group = "com.messenger"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
	//For firebase notifications
	google()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//Firebase notifications
	implementation("com.google.firebase:firebase-admin:9.1.1")

	// Postgres JDBC driver
	runtimeOnly("org.postgresql:postgresql:42.3.8")

	//Liquibase
	implementation("org.liquibase:liquibase-core:4.17.2")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


