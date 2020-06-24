package com.verygoodsecurity.vgscollect.card.validator.luhn

import com.verygoodsecurity.vgscollect.view.card.validation.card.brand.CardBrandDelegate
import org.junit.Assert
import org.junit.Test

class HipercardDelegateTest {

    @Test
    fun detectHipercard1() {
        val hipercard = CardBrandDelegate()

        val state = hipercard.isValid("6062828888666688")

        Assert.assertTrue(state)
    }

    @Test
    fun detectHipercard2() {
        val hipercard = CardBrandDelegate()

        val state = hipercard.isValid("6062826786276634")

        Assert.assertTrue(state)
    }

    @Test
    fun detectHipercard3() {
        val hipercard = CardBrandDelegate()

        val state = hipercard.isValid("6062826786276634")

        Assert.assertTrue(state)
    }

    @Test
    fun detectHipercard4() {
        val hipercard = CardBrandDelegate()

        val state = hipercard.isValid("6062828888666688")

        Assert.assertTrue(state)
    }
}