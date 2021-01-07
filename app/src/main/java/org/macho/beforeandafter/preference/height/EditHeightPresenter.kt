package org.macho.beforeandafter.preference.height

import android.content.Context
import org.macho.beforeandafter.shared.util.HeightScale
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject
import kotlin.math.roundToInt

class EditHeightPresenter @Inject constructor(): EditHeightContract.Presenter {
    var view: EditHeightContract.View? = null

    @Inject
    lateinit var context: Context

    lateinit var heightScale: HeightScale

    override fun save(heightText: String) {
        val height = heightText.toFloatOrNull()?.let {
            heightScale.convertToCm(it)
        } ?: 0f
        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.HEIGHT, height)
        view?.finish()
    }

    override fun takeView(view: EditHeightContract.View) {
        this.view = view

        this.heightScale = HeightScale(context)

        val goalHeightInCm = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.HEIGHT)
        val heightText = if (goalHeightInCm != 0f) {
            val height = heightScale.convertFromCm(goalHeightInCm)
            val roundedHeight = (height * 100).roundToInt() / 100f
            String.format("%.2f", roundedHeight)
        } else {
            ""
        }
        view.update(heightText, heightScale.heightUnitText)
    }

    override fun dropView() {
        this.view = null
    }
}