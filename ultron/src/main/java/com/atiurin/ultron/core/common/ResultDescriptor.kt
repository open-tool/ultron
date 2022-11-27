package com.atiurin.ultron.core.common

class ResultDescriptor {
    private var nestingLevel = 1

    fun <T> nestedOperation(block: () -> T): T {
        nestingLevel++
        val result = block()
        nestingLevel--
        return result
    }

    fun increaseLevel() {
        nestingLevel += 1
    }

    fun decreaseLevel() {
        nestingLevel -= 1
    }

    fun describeResult(descBuilder: StringBuilder, isSuccess: Boolean, execTimeMs: Long, exceptions: List<Throwable>) = apply {
        if (!isSuccess && exceptions.isNotEmpty()) {
            descBuilder
                .prefixAllLinesWithTab(nestingLevel, "Result = FAILED (${execTimeMs} ms) ")
                .prefixAllLinesWithTab(
                    nestingLevel,
                    if (exceptions.size > 1) {
                        """
                    |Errors were caught: 
                    |${exceptions.map { "- '${it.javaClass.simpleName}', message: '${it.message}' cause: '${it.cause}'\n" }}
                    |Last error is ${exceptions.last()::class.java.canonicalName}
                    |message: ${exceptions.last().message}
                    """.trimMargin()
                    } else {
                        """
                    |exception: ${exceptions.last()::class.java.canonicalName} 
                    |message: ${exceptions.last().message}
                    """.trimMargin()
                    }
                )
        } else {
            descBuilder.prefixAllLinesWithTab(nestingLevel, "Result = PASSED ($execTimeMs ms)")
        }
    }

    fun append(descBuilder: StringBuilder, text: String): StringBuilder {
        return descBuilder.prefixWithTab(nestingLevel, text)
    }

    fun appendLine(descBuilder: StringBuilder, text: String): StringBuilder {
        return descBuilder.prefixAllLinesWithTab(nestingLevel, text)
    }


    private fun StringBuilder.prefixAllLinesWithTab(tabCount: Int, text: String) = apply {
        val prefix = StringBuilder("")
        repeat(tabCount) { prefix.append("\t") }
        text.lines().forEach {
            this.appendLine("$prefix$it")
        }
    }

    private fun StringBuilder.prefixWithTab(tabCount: Int, text: String) = apply {
        val prefix = StringBuilder("")
        repeat(tabCount) { prefix.append("\t") }
        this.append("$prefix$text")
    }
}

fun String.prefixTab() = "\t$this"

fun String.prefixTabForAllLines(): String {
    val result = StringBuilder()
    val lines = this.lines()
    lines.forEachIndexed { index, line ->
        if (lines.lastIndex == index) result.append("\t$line")
        else result.appendLine("\t$line")
    }
    return result.toString()
}

