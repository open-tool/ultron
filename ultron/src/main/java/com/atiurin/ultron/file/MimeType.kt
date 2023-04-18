package com.atiurin.ultron.file

enum class MimeType(val extension: String, val value: String) {
    /** JPEG images */
    JPEG(".jpeg", "image/jpeg"),

    /** Portable Network Graphics */
    PNG(".png", "image/png"),

    /** WEBP image */
    WEBP(".webp", "image/webp"),

    JSON(".json", "application/json"),

    /** Adobe Portable Document Format (PDF) */
    PDF(".pdf", "application/pdf"),

    /** MP4 audio */
    MP4(".mp4", "audio/mp4"),

    /** AVI: Audio Video Interleave */
    AVI(".avi", "video/x-msvideo"),

    /** MPEG Video */
    MPEG(".mpeg", "video/mpeg"),

    /** Cascading Style Sheets (CSS) */
    CSS(".css", "text/css"),

    /** Comma-separated values (CSV) */
    CSV(".csv", "text/csv"),

    /** HyperText Markup Language (HTML) */
    HTML(".html", "text/html"),

    /** Text, (generally ASCII or ISO 8859-n) */
    PLAIN_TEXT(".txt", "text/plain"),

    /** YAML */
    YAML(".yaml", "text/yaml"),
}
