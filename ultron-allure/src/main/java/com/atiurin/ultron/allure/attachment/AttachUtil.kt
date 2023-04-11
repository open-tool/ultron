package com.atiurin.ultron.allure.attachment

import com.atiurin.ultron.file.MimeType
import com.atiurin.ultron.log.UltronLog
import io.qameta.allure.kotlin.Allure
import io.qameta.allure.kotlin.AllureLifecycle
import java.io.File
import java.io.InputStream

object AttachUtil {
    /**
     * @return allure file name
     */
    fun attachFile(file: File, mimeType: MimeType) = attachFile(
        name = file.name,
        file = file,
        mimeType = mimeType
    )

    /**
     * @return allure file name
     */
    fun attachFile(name: String, file: File, mimeType: MimeType) = Allure.lifecycle.writeFile(
        name = name,
        stream = file.inputStream(),
        type = mimeType.value,
        fileExtension = mimeType.extension
    )
}

fun AllureLifecycle.writeFile(name: String, stream: InputStream, type: String?, fileExtension: String?): String {
    val source = prepareAttachment(
        name = name,
        type = type,
        fileExtension = fileExtension
    )
    writeAttachment(
        attachmentSource = source,
        stream = stream
    )
    return source
}