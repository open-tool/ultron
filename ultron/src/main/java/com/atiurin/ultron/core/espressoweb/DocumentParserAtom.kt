package com.atiurin.ultron.core.espressoweb

import androidx.test.espresso.remote.annotation.RemoteMsgConstructor
import androidx.test.espresso.web.assertion.TagSoupDocumentParser
import androidx.test.espresso.web.model.Evaluation
import androidx.test.espresso.web.model.TransformingAtom
import org.w3c.dom.Document
import org.xml.sax.SAXException
import java.io.IOException

class DocumentParserAtom : TransformingAtom.Transformer<Evaluation, Document> {

    override fun apply(eval: Evaluation): Document? {
        if (eval.value is String) {
            return try {
                TagSoupDocumentParser.newInstance().parse(eval.value as String)
            } catch (se: SAXException) {
                throw RuntimeException("Parse failed: " + eval.value, se)
            } catch (ioe: IOException) {
                throw RuntimeException("Parse failed: " + eval.value, ioe)
            }
        }
        throw RuntimeException("Value should have been a string: $eval")
    }
}