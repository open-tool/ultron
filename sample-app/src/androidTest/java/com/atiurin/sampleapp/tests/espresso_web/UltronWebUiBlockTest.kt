package com.atiurin.sampleapp.tests.espresso_web

import com.atiurin.sampleapp.pages.uiblock.WebElementUiBlockScreen
import org.junit.Test

class UltronWebUiBlockTest : BaseWebViewTest() {
    @Test
    fun webUiBlock(){
        WebElementUiBlockScreen {
            teacherBlock.name.exists().hasText("Socrates")
            teacherBlock.uiBlock.exists()
            studentWithoutDesc.name.exists().hasText("Plato")
        }
    }

    @Test
    fun uiBlockFactoryTest(){
        WebElementUiBlockScreen {
            persons.student.name.hasText("Plato")
        }
    }

    @Test
    fun childUiBlockCreation(){
        WebElementUiBlockScreen {
            persons.teacher.name.hasText("Socrates")
            persons.studentWithoutDesc.name.hasText("Plato")
        }
    }
}