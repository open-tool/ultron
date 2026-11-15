package com.atiurin.ultron.allure.writer

import com.atiurin.ultron.allure.model.AllureIds
import com.atiurin.ultron.allure.model.AllureJson
import com.atiurin.ultron.allure.model.AllureTestResult

class AllureResultsWriter(val sink: AllureResultsSink) {

    /** @return the file name written */
    fun writeResult(result: AllureTestResult): String {
        val fileName = "${result.uuid}-result.json"
        sink.write(fileName, AllureJson.encode(result))
        return fileName
    }

    /** @return the attachment `source` to reference from a result or step */
    fun writeAttachment(bytes: ByteArray, extension: String): String {
        val source = "${AllureIds.uuid()}-attachment.${extension.trimStart('.')}"
        sink.write(source, bytes)
        return source
    }
}
