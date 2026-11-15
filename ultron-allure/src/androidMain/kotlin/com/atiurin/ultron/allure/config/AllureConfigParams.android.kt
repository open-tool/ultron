package com.atiurin.ultron.allure.config

import com.atiurin.ultron.allure.screenrecord.ScreenRecordOptions

/**
 * Parameters on Android (instrumented tests, `allure-kotlin` pipeline).
 *
 * Up to 2.6.6 this was a `data class` with six constructor parameters. It is a plain class now (it is the `actual`
 * of a multiplatform `expect class`), and the data-class members are kept by hand for source and binary
 * compatibility: the six-parameter constructor with the same order and defaults, `copy`, `component1`..`component6`,
 * `equals`, `hashCode` and `toString`.
 */
actual class AllureConfigParams actual constructor() {
    actual var addScreenshotPolicy: MutableSet<AllureAttachStrategy> = defaultFailurePolicy()
    actual var addHierarchyPolicy: MutableSet<AllureAttachStrategy> = defaultFailurePolicy()
    actual var attachUltronLog: Boolean = true
    var attachLogcat: Boolean = true
    var addConditionsToReport: Boolean = true
    actual var detailedAllureReport: Boolean = true
    /**
     * Screen recording with the on-device `screenrecord` binary, attached as `video/mp4`.
     * `TEST_FAILURE` attaches only failed tests, `TEST_FINISH` every test; `NONE` (default) records nothing.
     */
    var recordVideoPolicy: MutableSet<AllureAttachStrategy> = mutableSetOf(AllureAttachStrategy.NONE)
    var screenRecordOptions: ScreenRecordOptions = ScreenRecordOptions()

    /**
     * The constructor of the former data class: same parameters, order and defaults. No `@JvmOverloads`, as before:
     * from Java this is the six-argument constructor, and `new AllureConfigParams()` is the no-argument one.
     */
    constructor(
        addScreenshotPolicy: MutableSet<AllureAttachStrategy> = defaultFailurePolicy(),
        addHierarchyPolicy: MutableSet<AllureAttachStrategy> = defaultFailurePolicy(),
        attachUltronLog: Boolean = true,
        attachLogcat: Boolean = true,
        addConditionsToReport: Boolean = true,
        detailedAllureReport: Boolean = true
    ) : this() {
        this.addScreenshotPolicy = addScreenshotPolicy
        this.addHierarchyPolicy = addHierarchyPolicy
        this.attachUltronLog = attachUltronLog
        this.attachLogcat = attachLogcat
        this.addConditionsToReport = addConditionsToReport
        this.detailedAllureReport = detailedAllureReport
    }

    /** A new instance with the given values; the others are taken from this one. */
    fun copy(
        addScreenshotPolicy: MutableSet<AllureAttachStrategy> = this.addScreenshotPolicy,
        addHierarchyPolicy: MutableSet<AllureAttachStrategy> = this.addHierarchyPolicy,
        attachUltronLog: Boolean = this.attachUltronLog,
        attachLogcat: Boolean = this.attachLogcat,
        addConditionsToReport: Boolean = this.addConditionsToReport,
        detailedAllureReport: Boolean = this.detailedAllureReport,
        recordVideoPolicy: MutableSet<AllureAttachStrategy> = this.recordVideoPolicy,
        screenRecordOptions: ScreenRecordOptions = this.screenRecordOptions
    ): AllureConfigParams = AllureConfigParams(
        addScreenshotPolicy, addHierarchyPolicy, attachUltronLog, attachLogcat, addConditionsToReport, detailedAllureReport
    ).also {
        it.recordVideoPolicy = recordVideoPolicy
        it.screenRecordOptions = screenRecordOptions
    }

    /** Binary compatibility: the `copy` of the former data class. Hidden from source, which resolves to the full `copy`. */
    @Deprecated("Use copy with all parameters", level = DeprecationLevel.HIDDEN)
    fun copy(
        addScreenshotPolicy: MutableSet<AllureAttachStrategy> = this.addScreenshotPolicy,
        addHierarchyPolicy: MutableSet<AllureAttachStrategy> = this.addHierarchyPolicy,
        attachUltronLog: Boolean = this.attachUltronLog,
        attachLogcat: Boolean = this.attachLogcat,
        addConditionsToReport: Boolean = this.addConditionsToReport,
        detailedAllureReport: Boolean = this.detailedAllureReport
    ): AllureConfigParams = copy(
        addScreenshotPolicy, addHierarchyPolicy, attachUltronLog, attachLogcat, addConditionsToReport, detailedAllureReport,
        recordVideoPolicy, screenRecordOptions
    )

    operator fun component1(): MutableSet<AllureAttachStrategy> = addScreenshotPolicy
    operator fun component2(): MutableSet<AllureAttachStrategy> = addHierarchyPolicy
    operator fun component3(): Boolean = attachUltronLog
    operator fun component4(): Boolean = attachLogcat
    operator fun component5(): Boolean = addConditionsToReport
    operator fun component6(): Boolean = detailedAllureReport

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AllureConfigParams) return false
        return addScreenshotPolicy == other.addScreenshotPolicy &&
            addHierarchyPolicy == other.addHierarchyPolicy &&
            attachUltronLog == other.attachUltronLog &&
            attachLogcat == other.attachLogcat &&
            addConditionsToReport == other.addConditionsToReport &&
            detailedAllureReport == other.detailedAllureReport &&
            recordVideoPolicy == other.recordVideoPolicy &&
            screenRecordOptions == other.screenRecordOptions
    }

    override fun hashCode(): Int {
        var result = addScreenshotPolicy.hashCode()
        result = 31 * result + addHierarchyPolicy.hashCode()
        result = 31 * result + attachUltronLog.hashCode()
        result = 31 * result + attachLogcat.hashCode()
        result = 31 * result + addConditionsToReport.hashCode()
        result = 31 * result + detailedAllureReport.hashCode()
        result = 31 * result + recordVideoPolicy.hashCode()
        result = 31 * result + screenRecordOptions.hashCode()
        return result
    }

    override fun toString(): String =
        "AllureConfigParams(addScreenshotPolicy=$addScreenshotPolicy, addHierarchyPolicy=$addHierarchyPolicy, " +
            "attachUltronLog=$attachUltronLog, attachLogcat=$attachLogcat, addConditionsToReport=$addConditionsToReport, " +
            "detailedAllureReport=$detailedAllureReport, recordVideoPolicy=$recordVideoPolicy, screenRecordOptions=$screenRecordOptions)"
}

private fun defaultFailurePolicy(): MutableSet<AllureAttachStrategy> =
    mutableSetOf(AllureAttachStrategy.TEST_FAILURE, AllureAttachStrategy.OPERATION_FAILURE)
