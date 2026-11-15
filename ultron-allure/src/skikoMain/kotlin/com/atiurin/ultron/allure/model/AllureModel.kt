package com.atiurin.ultron.allure.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Subset of the Allure 2 result schema that Allure Report reads from `{uuid}-result.json`.
 * Field order follows the schema so generated files read naturally.
 */
@Serializable
data class AllureTestResult(
    val uuid: String,
    val historyId: String,
    val fullName: String,
    val name: String,
    val status: AllureStatus,
    val statusDetails: AllureStatusDetails? = null,
    val stage: String = AllureStage.FINISHED,
    val start: Long,
    val stop: Long,
    val steps: List<AllureStep> = emptyList(),
    val attachments: List<AllureAttachment> = emptyList(),
    val parameters: List<AllureParameter> = emptyList(),
    val labels: List<AllureLabel> = emptyList(),
)

@Serializable
data class AllureStep(
    val name: String,
    val status: AllureStatus,
    val statusDetails: AllureStatusDetails? = null,
    val stage: String = AllureStage.FINISHED,
    val start: Long,
    val stop: Long,
    val steps: List<AllureStep> = emptyList(),
    val attachments: List<AllureAttachment> = emptyList(),
)

@Serializable
data class AllureAttachment(
    val name: String,
    val source: String,
    val type: String,
)

@Serializable
data class AllureStatusDetails(
    val message: String? = null,
    val trace: String? = null,
)

@Serializable
data class AllureLabel(
    val name: String,
    val value: String,
)

@Serializable
data class AllureParameter(
    val name: String,
    val value: String,
)

@Serializable
enum class AllureStatus {
    @SerialName("passed") PASSED,
    @SerialName("failed") FAILED,
    @SerialName("broken") BROKEN,
    @SerialName("skipped") SKIPPED,
    @SerialName("unknown") UNKNOWN,
}

object AllureStage {
    const val FINISHED = "finished"
}
