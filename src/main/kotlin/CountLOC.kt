import java.io.File

fun File.getLOC(): Int {
    return this.walkTopDown()
            .asSequence()
            .filter { it.name.endsWith(".kt") }
            .map {
                it
                        .readLines()
                        .filterNot { it.trim().startsWith("/") }
                        .filterNot { it.trim().startsWith("*") }
                        .count()
            }.sum()
}

fun main() {
    File("src/main/kotlin/y2018")
            .listFiles()
            .sortedBy { it.name }
            .forEach { println("Package ${it.name} : ${it.getLOC()} lines.") }
}