package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.ForbrugsforeningenDelegate
import org.junit.Assert
import org.junit.Test

class ForbrugsforeningenDelegateTest {

    @Test
    fun detectForbrugsforeningen1() {
        val forb = ForbrugsforeningenDelegate()

        val state = forb.isValid("6007220000000004")

        Assert.assertTrue(state)
    }

}