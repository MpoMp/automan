plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.tinylog:tinylog-api:2.4.1")

    runtimeOnly("org.tinylog:tinylog-impl:2.4.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
}

application {
    mainClass.set("automan.AutomanEngine")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}