package org.macho.beforeandafter.preference.height

import android.content.Context
import org.macho.beforeandafter.shared.util.HeightScale
import org.macho.beforeandafter.shared.util.HeightUnitType
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject
import kotlin.math.roundToInt

class EditHeightPresenter @Inject constructor(): EditHeightContract.Presenter {
    var view: EditHeightContract.View? = null

    @Inject
    lateinit var context: Context

    lateinit var heightScale: HeightScale

    override fun save(heightForm: HeightForm) {
        val heightInCm = when (heightForm) {
            is CentimeterForm -> {
                heightForm.centimeterText.toFloatOrNull()?.let { heightScale.convertToCm(it) } ?: 0f
            }

            is FeetForm -> {
                val feet = heightForm.feetText.toIntOrNull() ?: 0
                val inches = heightForm.inchText.toFloatOrNull() ?: 0f
                val heightInInch = feet * 12 + inches
                heightScale.convertToCm(heightInInch)
            }

            else -> { throw RuntimeException("This line shouldn't be reached.") }
        }
        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.HEIGHT, heightInCm)
        view?.finish()
    }

    override fun takeView(view: EditHeightContract.View) {
        this.view = view

        this.heightScale = HeightScale(context)

        val goalHeightInCm = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.HEIGHT)
        val height = heightScale.convertFromCm(goalHeightInCm)
        val heightForm: HeightForm = when(heightScale.heightUnitType) {
            HeightUnitType.CM -> {
                if (height == 0f) {
                    CentimeterForm("")

                } else {
                    val roundedHeight = (height * 100).roundToInt() / 100f
                    CentimeterForm(String.format("%.2f", roundedHeight))
                }
            }

            HeightUnitType.IN -> {
                if (height == 0f) {
                    FeetForm("", "")

                } else {
                    val feet: Int = (height / 12).toInt()
                    val inches: Float = height - feet * 12
                    val roundedInches = (inches * 10).roundToInt() / 10f
                    FeetForm(feet.toString(), String.format("%.1f", roundedInches))
                }
            }
        }
        view.update(heightForm)
    }

    override fun dropView() {
        this.view = null
    }
}