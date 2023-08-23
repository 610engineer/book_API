import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.0.9"
	id("io.spring.dependency-management") version "1.1.2"
	id("nu.studer.jooq") version "8.0"
	kotlin("jvm") version "1.7.22"
	kotlin("plugin.spring") version "1.7.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-jooq")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// kotest
	val kotest_version = "5.5.4"
	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest_version")
	testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.2")

	// jooq
	jooqGenerator("com.mysql:mysql-connector-j")
	jooqGenerator("jakarta.xml.bind:jakarta.xml.bind-api:4.0.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jooq {
	configurations {
		create("main") {
			jooqConfiguration.apply {
				jdbc.apply {
					url = System.getenv("MYSQL_URL")
					user = System.getenv("MYSQL_USER")
					password = System.getenv("MYSQL_PASSWORD")
				}
				generator.apply {
					name = "org.jooq.codegen.KotlinGenerator"
					database.apply {
						name = "org.jooq.meta.mysql.MySQLDatabase"
						inputSchema = System.getenv("MYSQL_DB_NAME")
						excludes = "flyway_schema_history"
					}
					generate.apply {
						isDeprecated = false
						isTables = true
					}
					target.apply {
						packageName = "com.example.ktknowledgeTodo.infra.jooq"
						directory = "${buildDir}/generated/source/jooq/main"
					}
				}
			}
		}
	}
}
