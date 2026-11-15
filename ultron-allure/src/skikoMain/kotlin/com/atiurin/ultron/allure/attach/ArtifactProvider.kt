package com.atiurin.ultron.allure.attach

/** Produces one kind of attachment (screenshot, hierarchy). `null` means "not available now"; never throws. */
interface ArtifactProvider {
    /** Used in attachment names: `screenshot_<op>`, `test_failure_screenshot`. */
    val kind: String
    val extension: String
    val mimeType: String
    fun produce(): ByteArray?
}
