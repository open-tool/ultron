package com.atiurin.sampleapp.tests.testlifecycle

import com.atiurin.sampleapp.framework.Log
import com.atiurin.sampleapp.framework.step
import com.atiurin.ultron.core.config.UltronConfig
import com.atiurin.ultron.testlifecycle.rulesequence.RuleSequence
import com.atiurin.ultron.testlifecycle.setupteardown.*
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class SetUpTearDownRuleTest {
    val counter = AtomicInteger(0)
    val conditionsOrderMap = mutableMapOf<Int, String>()

    init {
        UltronConfig.Conditions.conditionExecutorWrapper = CustomConditionExecutorWrapper()
        UltronConfig.Conditions.conditionsExecutor = CustomConditionsExecutor()
    }

    class CustomConditionExecutorWrapper : ConditionExecutorWrapper {
        override fun execute(condition: Condition) {
            step(condition.name) { condition.actions() }
        }
    }
    class CustomConditionsExecutor : DefaultConditionsExecutor(){
        var lastRuleName: String = ""
        override val conditionExecutor: ConditionExecutorWrapper = CustomConditionExecutorWrapper()
        override fun before(name: String, ruleClass: KClass<*>) {
            this.lastRuleName = name
            super.before(name, ruleClass)
        }
        override fun execute(conditions: List<Condition>, keys: List<String>, description: String) {
            step(lastRuleName){ super.execute(conditions, keys, description) }
        }
    }

    companion object {
        const val firstSetUpKey = "firstSetUp"
        const val firstTearDownKey = "firstTearDown"
        const val setUp1Key = "setUp1"
        const val setUp2Key = "setUp2"
        const val tearDown1Key = "tearDown1"
        const val tearDown2Key = "tearDown2"
        const val lastSetUpKey = "lastSetUp"
        const val lastTearDownKey = "lastTearDown"
    }

    val firstSetUp = SetUpRule("firstSetUp").add {
        conditionsOrderMap.put(counter.incrementAndGet(), firstSetUpKey)
    }
    val firstTearDown = TearDownRule("firstTearDown").add {
        conditionsOrderMap.put(counter.incrementAndGet(), firstTearDownKey)
    }

    val setUp1 = SetUpRule("setUpRule1")
        .add(name = "setUp1") {
            conditionsOrderMap.put(counter.incrementAndGet(), setUp1Key)
        }
        .add(name = "setUp1_2") {
        }
    val tearDown1 = TearDownRule("tearDown1").add {
        conditionsOrderMap.put(counter.incrementAndGet(), tearDown1Key)
    }

    val setUp2 = SetUpRule("setUp2")
        .add {
            conditionsOrderMap.put(counter.incrementAndGet(), setUp2Key)
        }
    val tearDown2 = TearDownRule("tearDown2").add {
        conditionsOrderMap.put(counter.incrementAndGet(), tearDown2Key)
    }

    val lastSetUp = SetUpRule()
        .add {
            conditionsOrderMap.put(counter.incrementAndGet(), lastSetUpKey)
//            throw Exception("asd")
        }
    val lastTearDown = TearDownRule()
        .add {
            conditionsOrderMap.put(counter.incrementAndGet(), lastTearDownKey)
        }
    val controlTearDown = TearDownRule()
        .add {
            Log.info(conditionsOrderMap.toString())
            Assert.assertEquals(firstSetUpKey, conditionsOrderMap[1])
            Assert.assertEquals(setUp1Key, conditionsOrderMap[2])
            Assert.assertEquals(setUp2Key, conditionsOrderMap[3])
            Assert.assertEquals(lastSetUpKey, conditionsOrderMap[4])
            Assert.assertEquals(firstTearDownKey, conditionsOrderMap[5])
            Assert.assertEquals(tearDown1Key, conditionsOrderMap[6])
            Assert.assertEquals(tearDown2Key, conditionsOrderMap[7])
            Assert.assertEquals(lastTearDownKey, conditionsOrderMap[8])

        }

    @get:Rule
    val ruleSequence =
        RuleSequence(setUp1, tearDown1)
            .add(tearDown2, setUp2)
            .addFirst(firstSetUp, firstTearDown)
            .addLast(lastTearDown, lastSetUp, controlTearDown)

    @Test
    fun mockTestConditions() {
        Log.info(">>>> test")
//        throw Exception("asd")
    }
}