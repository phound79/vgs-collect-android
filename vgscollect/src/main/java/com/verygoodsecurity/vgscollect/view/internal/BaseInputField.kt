package com.verygoodsecurity.vgscollect.view.internal

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextWatcher
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.verygoodsecurity.vgscollect.R
import com.verygoodsecurity.vgscollect.core.OnVgsViewStateChangeListener
import com.verygoodsecurity.vgscollect.core.model.state.Dependency
import com.verygoodsecurity.vgscollect.core.model.state.FieldContent
import com.verygoodsecurity.vgscollect.core.model.state.VGSFieldState
import com.verygoodsecurity.vgscollect.core.storage.DependencyListener
import com.verygoodsecurity.vgscollect.view.InputFieldView
import com.verygoodsecurity.vgscollect.view.card.FieldType
import com.verygoodsecurity.vgscollect.view.card.InputRunnable

/** @suppress */
internal abstract class BaseInputField(context: Context) : TextInputEditText(context),
    DependencyListener {

    companion object {
        fun getInputField(context: Context, type:FieldType):BaseInputField {
            return when(type) {
                FieldType.CARD_NUMBER -> CardInputField(context)
                FieldType.CVC -> CVCInputField(context)
                FieldType.CARD_EXPIRATION_DATE -> DateInputField(context)
                FieldType.CARD_HOLDER_NAME -> PersonNameInputField(context)
                FieldType.INFO -> InfoInputField(context)
            }
        }
    }

    protected abstract var fieldType: FieldType

    protected var inputConnection: InputRunnable? = null

    internal var stateListener: OnVgsViewStateChangeListener? = null
        set(value) {
            field = value
            inputConnection?.setOutputListener(value)
        }

    protected var isListeningPermitted = true
    private var isBackgroundVisible = true

    protected var hasRTL = false

    var isRequired:Boolean = true
        set(value) {
            field = value
            inputConnection?.getOutput()?.isRequired = value
            inputConnection?.run()
        }

    private var activeTextWatcher: TextWatcher? = null

    internal fun setIsListeningPermitted(state:Boolean) {
        isListeningPermitted = state
    }
    init {
        isListeningPermitted = true
        setupFocusChangeListener()
        setupInputConnectionListener()
        isListeningPermitted = false

        setupViewAttributes()
    }

    private fun setupViewAttributes() {
        id = ViewCompat.generateViewId()

        compoundDrawablePadding = resources.getDimension(R.dimen.half_default_padding).toInt()
    }

    private fun setupFocusChangeListener() {
        onFocusChangeListener = OnFocusChangeListener { _, f ->
            inputConnection?.getOutput()?.isFocusable = f
            inputConnection?.run()
        }
    }

    protected open fun setupInputConnectionListener() {
        val handler = Handler(Looper.getMainLooper())
        addTextChangedListener {
            inputConnection?.getOutput()?.content?.data =  it.toString()

            handler.removeCallbacks(inputConnection)
            handler.postDelayed(inputConnection, 200)
        }
    }

    override fun onAttachedToWindow() {
        isListeningPermitted = true
        applyFieldType()
        super.onAttachedToWindow()
        isListeningPermitted = false
    }

    protected fun refreshInputConnection() {
        isListeningPermitted = true
        applyFieldType()
        isListeningPermitted = false
    }

    protected abstract fun applyFieldType()

    protected fun applyNewTextWatcher(textWatcher: TextWatcher?) {
        activeTextWatcher?.let { removeTextChangedListener(activeTextWatcher) }
        textWatcher?.let { addTextChangedListener(textWatcher) }
        activeTextWatcher = textWatcher
    }

    protected fun collectCurrentState(stateContent: FieldContent): VGSFieldState {
        val state = VGSFieldState().apply {
            isRequired = this@BaseInputField.isRequired
            isFocusable = this@BaseInputField.hasFocus()
            type = this@BaseInputField.fieldType
            content = stateContent

            fieldName = this@BaseInputField.tag as? String
        }

        return state
    }

    internal fun setHasBackground(state:Boolean) {
        isBackgroundVisible = state
        if(isBackgroundVisible) {
            setBackgroundResource(android.R.color.transparent)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        setSelection(text?.length?:0)
    }

    protected fun isRTL():Boolean {
        val direction = getResolvedLayoutDirection()
        return direction == View.LAYOUT_DIRECTION_RTL
                || direction == View.TEXT_DIRECTION_ANY_RTL
                || direction == View.TEXT_DIRECTION_FIRST_STRONG_RTL
                || direction == View.TEXT_DIRECTION_RTL
    }

    private fun getResolvedLayoutDirection():Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            layoutDirection
        } else {
            View.LAYOUT_DIRECTION_LTR
        }
    }

    protected fun refreshInput() {
        setText(text)
    }

    override fun setTag(tag: Any?) {
        tag?.run {
            super.setTag(tag)
            inputConnection?.getOutput()?.fieldName = this as String
        }
    }

    override fun addTextChangedListener(watcher: TextWatcher?) {
        if(isListeningPermitted) {
            super.addTextChangedListener(watcher)
        }
    }

    private var minH:Int = 0
    private var minW:Int = 0
    internal fun setMinimumPaddingLimitations(w:Int, h:Int) {
        minH = h
        minW = w
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        val l = if(left < minW) minW else left
        val r = if(right < minW) minW else right
        val t = if(top < minH) minH else top
        val b = if(bottom < minH) minH else bottom
        super.setPadding(l, t, r, b)
    }

    override fun setCompoundDrawables(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        if(isRTL()) {
            super.setCompoundDrawables(right, top, left, bottom)
        } else {
            super.setCompoundDrawables(left, top, right, bottom)
        }
    }

    override fun dispatchDependencySetting(dependency: Dependency) {}

    private fun requestFocusOnView(id:Int) {
        val nextView = rootView?.findViewById<View>(id)

        when (nextView) {
            null -> return
            is InputFieldView -> nextView.getView().requestFocus()
            is BaseInputField -> nextView.requestFocus()
            else -> nextView.requestFocus()
        }
    }

    override fun onEditorAction(actionCode: Int) {
        when {
            actionCode == EditorInfo.IME_ACTION_NEXT
                    && nextFocusDownId != View.NO_ID -> requestFocusOnView(nextFocusDownId)
            actionCode == EditorInfo.IME_ACTION_PREVIOUS
                    && nextFocusUpId != View.NO_ID -> requestFocusOnView(nextFocusUpId)
            else -> super.onEditorAction(actionCode)
        }
    }
}