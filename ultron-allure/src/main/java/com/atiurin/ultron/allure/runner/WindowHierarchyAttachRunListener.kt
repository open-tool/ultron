package com.atiurin.ultron.allure.runner

import com.atiurin.ultron.allure.config.AllureAttachStrategy
import com.atiurin.ultron.allure.hierarchy.AllureHierarchyDumper
import com.atiurin.ultron.extensions.fullTestName
import com.atiurin.ultron.runner.UltronRunListener
import org.junit.runner.notification.Failure

class WindowHierarchyAttachRunListener(val policies: Set<AllureAttachStrategy>) : UltronRunListener() {
    val dumper = AllureHierarchyDumper()

    override fun testFailure(failure: Failure) {
        if (policies.contains(AllureAttachStrategy.TEST_FAILURE)){
            dumper.dumpAndAttach("$prefix${failure.description.fullTestName()}")
        }
    }
    companion object{
        private const val prefix = "hierarchy_"
    }
}