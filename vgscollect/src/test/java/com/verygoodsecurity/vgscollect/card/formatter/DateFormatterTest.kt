package com.verygoodsecurity.vgscollect.card.formatter

import android.text.Editable
import android.text.TextWatcher
import com.verygoodsecurity.vgscollect.view.card.formatter.Formatter
import com.verygoodsecurity.vgscollect.view.card.formatter.date.StrictDateFormatter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class DateFormatterTest {

    companion object {
        private const val TEST_POSITIVE_VALUE_1 =  "03/01"
        private const val TEST_POSITIVE_VALUE_2 =  "12/1990"
        private const val TEST_POSITIVE_VALUE_3 =  "12-2034"
    }

    private lateinit var formatter: Formatter
    private lateinit var textWatcher: TextWatcher

    @Before
    fun setupFormatter() {
        with(StrictDateFormatter()) {
            formatter = this
            textWatcher = this
        }
    }

    @Test
    fun set_mask() {
        val c = StrictDateFormatter()
        assertEquals("##/####", c.getMask())
        c.setMask("yyyy MM")
        assertEquals("#### ##", c.getMask())
        c.setMask("yyyy mm")
        assertEquals("#### ##", c.getMask())
        c.setMask("mm-yy")
        assertEquals("##-##", c.getMask())
        c.setMask("yy/mm")
        assertEquals("##/##", c.getMask())
    }

    @Test
    fun test_default_text_full() {
        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_2, 0,0,7)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_POSITIVE_VALUE_2)
    }

    @Test
    fun test_default_set_mask_text_full() {
        formatter.setMask("MM-yyyy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_3, 0,0,7)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_POSITIVE_VALUE_3)
    }

    @Test
    fun test_set_default_short() {
        formatter.setMask("MM/yy")

        textWatcher.onTextChanged(TEST_POSITIVE_VALUE_1, 0,0,5)

        val e = mock(Editable::class.java)
        textWatcher.afterTextChanged(e)

        verify(e).replace(0, 0, TEST_POSITIVE_VALUE_1)
    }
}