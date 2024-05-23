package com.atiurin.ultron.hierarchy

import java.io.File

interface HierarchyDumper {
    fun dumpFullWindowHierarchy(file: File): HierarchyDumpResult
}