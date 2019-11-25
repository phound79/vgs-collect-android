package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.InputFieldView

internal class TextInputLayoutWrapper(context: Context) : TextInputLayout(context) {

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        val v = handleNewChild(child)
        super.addView(v, params)
    }

    override fun addView(child: View?, index: Int) {
        val v = handleNewChild(child)
        super.addView(v, index)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        val v = handleNewChild(child)
        super.addView(v, width, height)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        val v = handleNewChild(child)
        super.addView(v, index, params)
    }

    private  fun handleNewChild(child: View?):View? {
        return child?.run {
            when(this) {
                is EditTextWrapper -> this
                is InputFieldView -> {
                    (this as? InputFieldView)?.getEditTextWrapper()
                }
                is FrameLayout -> this
                else -> {
                    Logger.i("VGSTextInputLayout", "${this::class.java.name} is not VGSEditText")
                    null
                }
            }
        }
    }
}