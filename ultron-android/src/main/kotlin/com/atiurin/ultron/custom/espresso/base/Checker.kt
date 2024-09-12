package com.atiurin.ultron.custom.espresso.base

import android.os.Looper


object Checker {
    fun checkMainThread() {
        checkState(
            Thread.currentThread() == Looper.getMainLooper().thread,
            "Method cannot be called off the main application thread (on: %s)",
            Thread.currentThread().name
        )
    }

    fun checkState(
        expression: Boolean, errorMessageTemplate: String, vararg errorMessageArgs: Any,
    ) {
        check(expression) { format(errorMessageTemplate, *errorMessageArgs) }
    }

    private fun format(template: String?, vararg args: Any): String {
        var templ = template
        templ = templ.toString()

        // start substituting the arguments into the '%s' placeholders
        val builder = StringBuilder(templ.length + 16 * args.size)
        var templateStart = 0
        var i = 0
        while (i < args.size) {
            val placeholderStart = templ.indexOf("%s", templateStart)
            if (placeholderStart == -1) {
                break
            }
            builder.append(templ.substring(templateStart, placeholderStart))
            builder.append(args[i++])
            templateStart = placeholderStart + 2
        }
        builder.append(templ.substring(templateStart))

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.size) {
            builder.append(" [")
            builder.append(args[i++])
            while (i < args.size) {
                builder.append(", ")
                builder.append(args[i++])
            }
            builder.append(']')
        }
        return builder.toString()
    }
}