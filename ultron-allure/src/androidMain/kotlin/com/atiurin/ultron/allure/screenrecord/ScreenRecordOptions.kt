package com.atiurin.ultron.allure.screenrecord

/**
 * Arguments of the on-device `screenrecord` binary.
 *
 * @param timeLimitSec recording stops by itself after this many seconds; the binary caps it at 180 on
 *   API < 34, newer releases accept `0` for "no limit".
 * @param bitRate H.264 bit rate in bits per second.
 * @param size `WIDTHxHEIGHT`; `null` keeps the display resolution. Smaller sizes give smaller files.
 */
data class ScreenRecordOptions(
    val timeLimitSec: Int = 180,
    val bitRate: Int = 2_000_000,
    val size: String? = null,
)
