package com.atiurin.ultron.allure.writer

import kotlin.test.Test
import kotlin.test.assertEquals

class AllureResultsLocationTest {
    @Test
    fun `configured directory wins over environment`() {
        assertEquals("/cfg", AllureResultsLocation.resolve(configured = "/cfg", environment = "/env"))
    }

    @Test
    fun `environment wins over default`() {
        assertEquals("/env", AllureResultsLocation.resolve(configured = null, environment = "/env"))
    }

    @Test
    fun `default is build allure-results`() {
        assertEquals("build/allure-results", AllureResultsLocation.resolve(configured = null, environment = null))
    }

    @Test
    fun `blank values count as absent`() {
        assertEquals("/env", AllureResultsLocation.resolve(configured = "  ", environment = "/env"))
        assertEquals("build/allure-results", AllureResultsLocation.resolve(configured = "", environment = ""))
    }
}
