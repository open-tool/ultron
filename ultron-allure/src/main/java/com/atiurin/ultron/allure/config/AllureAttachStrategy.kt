package com.atiurin.ultron.allure.config

enum class AllureAttachStrategy {
    TEST_FAILURE,
    OPERATION_FAILURE, // attach artifact for failed operation
    OPERATION_SUCCESS, // attach artifact for each succeeded operation
    OPERATION_FINISH, // attach artifact for each operation
    NONE
}