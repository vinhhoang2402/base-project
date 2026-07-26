plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.test) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register("generateStringsFromCsv") {
    description = "Generate Android strings.xml from CSV"
    doLast {
        val csvFile = file("translations.csv")
        val outputDir = file("app/src/main/res")
        
        if (!csvFile.exists()) {
            throw GradleException("translations.csv not found!")
        }
        
        outputDir.mkdirs()
        
        val lines = csvFile.readLines()
        if (lines.isEmpty()) return@doLast
        
        val headers = lines[0].split(",").map { it.trim() }
        val keyIndex = headers.indexOf("key")
        
        val translations = mutableMapOf<String, MutableMap<String, String>>()
        headers.withIndex().filter { it.value != "key" }.forEach { (index, lang) ->
            translations[lang] = mutableMapOf()
        }
        
        lines.drop(1).forEach { line ->
            val values = line.split(",").map { it.trim() }
            if (values.size > keyIndex) {
                val key = values[keyIndex]
                headers.withIndex().filter { it.value != "key" }.forEach { (index, lang) ->
                    if (index < values.size) {
                        translations[lang]!![key] = values[index]
                    }
                }
            }
        }
        
        translations.forEach { (lang, strings) ->
            val langDir = if (lang == headers[1]) File(outputDir, "values") else File(outputDir, "values-$lang")
            langDir.mkdirs()
            
            val xml = buildString {
                appendLine("<?xml version=\"1.0\" encoding=\"utf-8\"?>")
                appendLine("<resources>")
                strings.forEach { (key, value) ->
                    val escaped = value
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("\"", "&quot;")
                    appendLine("    <string name=\"$key\">$escaped</string>")
                }
                appendLine("</resources>")
            }
            
            File(langDir, "strings.xml").writeText(xml)
            println("✓ Generated $lang")
        }
    }
}
