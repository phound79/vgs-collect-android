package com.verygoodsecurity.vgscollect.view

import android.content.Context
import android.os.Handler
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.view.text.validation.card.*
import android.os.Looper
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.verygoodsecurity.vgscollect.view.text.validation.card.VGSTextInputType

internal class EditTextWrapper(context: Context): TextInputEditText(context) {

    private var vgsInputType: VGSTextInputType = VGSTextInputType.CardOwnerName
    private val state = VGSFieldState()

    private var activeTextWatcher: TextWatcher? = null
    internal var stateListener: OnVgsViewStateChangeListener? = null
        internal set(value) {
            field = value
            field?.emit(id, state)
        }

    internal var isRequired:Boolean = true
        set(value) {
            field = value
            state.isRequired = value
            stateListener?.emit(id, state)
        }

    private val inputStateRunnable = Runnable {
        vgsInputType.validate(state.content)
        state.type = vgsInputType
        stateListener?.emit(id, state)
    }

    init {
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            state.isFocusable = f
            stateListener?.emit(id, state)
        }

        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            if(vgsInputType is VGSTextInputType.CardNumber) {
                state.content = it.toString().replace(" ".toRegex(), "")
            } else {
                state.content = it.toString()
            }

            handler.removeCallbacks(inputStateRunnable)
            handler.postDelayed(inputStateRunnable, 500)
        }
        id = ViewCompat.generateViewId()
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if(vgsInputType is VGSTextInputType.CardExpDate) setSelection(text?.length?:0)
    }

    fun setInputFormatType(inputType: VGSTextInputType) {
        vgsInputType = inputType
        when(inputType) {
            is VGSTextInputType.CardNumber -> {
                applyNewTextWatcher(CardNumberTextWatcher)
                val filter = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filter)
                setInputType(InputType.TYPE_CLASS_PHONE)
            }
            is VGSTextInputType.CVVCardCode -> {
                applyNewTextWatcher(null)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(CVVValidateFilter(), filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
            is VGSTextInputType.CardOwnerName -> {
                applyNewTextWatcher(null)
                filters = arrayOf()
                setInputType(InputType.TYPE_CLASS_TEXT)
            }
            is VGSTextInputType.CardExpDate -> {
                applyNewTextWatcher(ExpirationDateTextWatcher)
                val filterLength = InputFilter.LengthFilter(inputType.length)
                filters = arrayOf(filterLength)
                setInputType(InputType.TYPE_CLASS_DATETIME)
            }
        }
        state.type = vgsInputType
        stateListener?.emit(id, state)
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            state.alias = this as String
        }
    }

    private fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

//    internal fun setVGSPlaceHolderText(text:String?) {
//        hint = text
//        state.placeholder = text
//        stateListener?.emit(id, state)
//    }
}