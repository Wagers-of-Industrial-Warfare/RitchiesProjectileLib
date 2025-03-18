import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.expand
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.*

val Project.mod: ModData get() = ModData(this)
val Project.buildData: BuildData get() = BuildData(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()
fun String.upperCaseFirst() = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

fun RepositoryHandler.strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
    forRepository { maven(url) { name = alias } }
    filter { groups.forEach(::includeGroup) }
}

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    for ((name, value) in properties) inputs.property(name, value)
    filesMatching(files) {
        expand(properties.toMap())
    }
}

@JvmInline
value class ModData(private val project: Project) {
    val id: String get() = requireNotNull(project.prop("mod.id")) { "Missing 'mod.id'" }
    val version: String get() = requireNotNull(project.prop("mod.version")) { "Missing 'mod.version'" }
    val group: String get() = requireNotNull(project.prop("mod.group")) { "Missing 'mod.group'" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key'" }
    fun dep(key: String) = requireNotNull(project.prop("deps.$key")) { "Missing 'deps.$key'" }
}

@JvmInline
value class BuildData(private val project: Project) {
    val ci: Boolean get() = System.getenv("CI")?.toBoolean() ?: false
    val release: Boolean get() = System.getenv("RELEASE")?.toBoolean() ?: false
    val nightly: Boolean get() = ci && !release
    val buildNumber: Int? get() = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull()
}
