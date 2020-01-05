package info.nightscout.androidaps.plugins.general.automation.elements

import android.widget.LinearLayout
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.R
import info.nightscout.androidaps.utils.NumberPicker
import java.text.DecimalFormat

class InputPercent(mainApp: MainApp) : Element(mainApp) {
    var value: Double = 100.0

    constructor(mainApp: MainApp, value: Double) : this(mainApp) {
        this.value = value
    }

    override fun addToLayout(root: LinearLayout) {
        val numberPicker = NumberPicker(root.context, null)
        numberPicker.setParams(100.0, MIN, MAX, 5.0, DecimalFormat("0"), true, root.findViewById(R.id.ok))
        numberPicker.value = value
        numberPicker.setOnValueChangedListener { value: Double -> this.value = value }
        root.addView(numberPicker)
    }

    companion object {
        const val MIN = 70.0
        const val MAX = 130.0
    }
}