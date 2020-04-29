package com.verygoodsecurity.vgscollect.view.material.internal

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import com.verygoodsecurity.vgscollect.util.Logger
import com.verygoodsecurity.vgscollect.view.AccessibilityStatePreparer
import com.verygoodsecurity.vgscollect.view.internal.BaseInputField

/** @suppress */
internal class TextInputLayoutWrapper @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {

    fun isReady():Boolean {
        return editText != null
    }

    private var state: InputLayoutState? = null
    fun restoreState(state: InputLayoutState) {
        this.state = state
    }

    override fun addView(child: View?) {
        val v = handleNewChild(child)
        super.addView(v)
        if(isReady()) {
            state?.restore(this)
        }
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

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        val v = handleNewChild(child)?:child
        super.addView(v, index, params)
    }

    private  fun handleNewChild(child: View?):View? {
        return child?.run {
            when(this) {
                is BaseInputField ->  {
                    this.setIsListeningPermitted(true)
                    this
                }
                is AccessibilityStatePreparer -> {
                    val v = (this as? AccessibilityStatePreparer)?.getView()
                    return applyAndReturnDefaultLayoutParams(child, v)
                }
                is ViewGroup -> this
                else -> {
                    Logger.i("VGSTextInputLayout", "${this::class.java.name} is not VGSEditText")
                    null
                }
            }
        }
    }

    private fun applyAndReturnDefaultLayoutParams(parentView: View, v: View?):View {
        return v?.apply {
            val LP = LayoutParams(parentView.layoutParams.width, parentView.layoutParams.height)
            LP.setMargins(0,0,0,0)
            if(LP.gravity == -1) {
                LP.gravity = Gravity.CENTER_VERTICAL
            }
            layoutParams = LP

            if(this is TextView &&
                this.gravity == Gravity.TOP or Gravity.START) {
                this.gravity = Gravity.CENTER_VERTICAL
            }
        }?:parentView
    }
}