package info.nightscout.androidaps.plugins.general.automation.triggers

import android.widget.LinearLayout
import com.google.common.base.Optional
import info.nightscout.androidaps.MainApp
import info.nightscout.androidaps.R
import info.nightscout.androidaps.logging.LTag
import info.nightscout.androidaps.plugins.general.automation.elements.Comparator
import info.nightscout.androidaps.plugins.general.automation.elements.InputInsulin
import info.nightscout.androidaps.plugins.general.automation.elements.LabelWithElement
import info.nightscout.androidaps.plugins.general.automation.elements.LayoutBuilder
import info.nightscout.androidaps.plugins.general.automation.elements.StaticLabel
import info.nightscout.androidaps.utils.DateUtil
import info.nightscout.androidaps.utils.JsonHelper
import org.json.JSONObject

class TriggerIob(mainApp: MainApp) : Trigger(mainApp) {
    private var insulin = InputInsulin(mainApp)
    var comparator: Comparator = Comparator(mainApp)

    constructor(mainApp: MainApp, triggerIob: TriggerIob) : this(mainApp) {
        insulin = InputInsulin(mainApp, triggerIob.insulin)
        comparator = Comparator(mainApp, triggerIob.comparator.value)
    }

    val value: Double = 0.0

    override fun shouldRun(): Boolean {
        val profile = profileFunction.getProfile() ?: return false
        val iob = iobCobCalculatorPlugin.calculateFromTreatmentsAndTempsSynchronized(DateUtil.now(), profile)
        if (comparator.value.check(iob.iob, value)) {
            aapsLogger.debug(LTag.AUTOMATION, "Ready for execution: " + friendlyDescription())
            return true
        }
        aapsLogger.debug(LTag.AUTOMATION, "NOT ready for execution: " + friendlyDescription())
        return false
    }

    @Synchronized override fun toJSON(): String {
        val data = JSONObject()
            .put("insulin", value)
            .put("comparator", comparator.value.toString())
        return JSONObject()
            .put("type", this::class.java.name)
            .put("data", data)
            .toString()
    }

    override fun fromJSON(data: String): Trigger {
        val d = JSONObject(data)
        insulin.value = JsonHelper.safeGetDouble(d, "insulin")
        comparator.setValue(Comparator.Compare.valueOf(JsonHelper.safeGetString(d, "comparator")!!))
        return this
    }

    override fun friendlyName(): Int = R.string.iob

    override fun friendlyDescription(): String =
        resourceHelper.gs(R.string.iobcompared, resourceHelper.gs(comparator.value.stringRes), value)

    override fun icon(): Optional<Int?> = Optional.of(R.drawable.ic_keyboard_capslock)

    override fun duplicate(): Trigger = TriggerIob(mainApp, this)

    override fun generateDialog(root: LinearLayout) {
        LayoutBuilder()
            .add(StaticLabel(mainApp, R.string.iob))
            .add(comparator)
            .add(LabelWithElement(mainApp, resourceHelper.gs(R.string.iob_u), "", insulin))
            .build(root)
    }
}