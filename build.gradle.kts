plugins {
    kotlin("jvm") version "1.3.11"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", "1.3.11"))
    implementation("io.reactivex.rxjava2:rxkotlin:2.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")
}

tasks.withType<Test> {
    useJUnitPlatform {}
}
