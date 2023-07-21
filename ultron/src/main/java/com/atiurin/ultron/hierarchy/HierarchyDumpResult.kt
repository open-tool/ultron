package com.atiurin.ultron.hierarchy

import com.atiurin.ultron.file.MimeType
import java.io.File

data class HierarchyDumpResult(
    val isSuccess: Boolean,
    val file: File,
    val mimeType: MimeType = MimeType.XML
)
