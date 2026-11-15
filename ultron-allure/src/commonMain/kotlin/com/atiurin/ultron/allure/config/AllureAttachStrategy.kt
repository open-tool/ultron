package com.atiurin.ultron.allure.config

/** Same vocabulary as `ultron-allure` (Android), so policies read the same in both modules. */
enum class AllureAttachStrategy {
    TEST_FAILURE,
    OPERATION_FAILURE,
    OPERATION_SUCCESS,
    OPERATION_FINISH,
    NONE,
    /** Attach at the end of every test, passed or failed (used by the video policy). Last, so earlier ordinals stay. */
    TEST_FINISH,
}

internal fun Set<AllureAttachStrategy>.allows(strategy: AllureAttachStrategy): Boolean =
    AllureAttachStrategy.NONE !in this && strategy in this

/**
 * Whether a video policy records tests at all: only `TEST_FAILURE` and `TEST_FINISH` ever attach a recording,
 * so a set of `OPERATION_*` values alone records nothing. Shared by the Android `screenrecord` listener and the
 * Skiko frame recorder.
 */
internal fun Set<AllureAttachStrategy>.recordsTests(): Boolean =
    allows(AllureAttachStrategy.TEST_FAILURE) || allows(AllureAttachStrategy.TEST_FINISH)
