package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.extensions.createDirectoryIfNotExists
import com.atiurin.ultron.log.UltronLog
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.Description
import org.junit.runner.Result
import java.io.File
import kotlin.system.measureTimeMillis

/**
 * Transfer files from original Allure results directory [sourceDir]
 * to custom directory [targetDir] provided by user [UltronAllureConfig.setAllureResultsDirectory]
 */
class UltronAllureResultsTransferListener(private val sourceDir: File, private val targetDir: File) : UltronRunListener() {
    override fun testFinished(description: Description) {
        transferFiles()
    }

    override fun testRunFinished(result: Result) {
        transferFiles()
    }

    private fun transferFiles(){
        UltronLog.info("Copy Allure results from '${sourceDir.absolutePath}' to '${targetDir.absolutePath}'")
        targetDir.createDirectoryIfNotExists()
        var isSuccessfullyCopied = true
        val time = measureTimeMillis {
            sourceDir.copyRecursively(targetDir, true, onError = { file, ioException ->
                UltronLog.error("""
                |Unable to copy Allure results file '${file.absolutePath}' to '${targetDir.absolutePath}'.
                |Got exception : '${ioException.message}'.
                |Source directory '${sourceDir.absolutePath}' won't be deleted.
                """.trimMargin()
                )
                isSuccessfullyCopied = false
                OnErrorAction.SKIP
            })
            if (isSuccessfullyCopied) sourceDir.deleteRecursively()
        }
        UltronLog.info("Allure results files transfer time = $time ms")
    }
}

