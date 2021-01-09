package org.macho.beforeandafter.preference.editscale

import android.content.Context
import org.macho.beforeandafter.shared.di.ActivityScoped
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

@ActivityScoped
class EditScalePresenter @Inject constructor(): EditScaleContract.Presenter {

    @Inject
    lateinit var context: Context

    private var view: EditScaleContract.View? = null

    override fun takeView(view: EditScaleContract.View) {
        this.view = view
        val weightUnitIndex = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.WEIGHT_UNIT)
        val heightUnitIndex = SharedPreferencesUtil.getInt(context, SharedPreferencesUtil.Key.HEIGHT_UNIT)
        view.updateViews(weightUnitIndex, heightUnitIndex)
    }

    override fun dropView() {
        this.view = null
    }

    override fun save(weightUnitIndex: Int, heightUnitIndex: Int) {
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.WEIGHT_UNIT, weightUnitIndex)
        SharedPreferencesUtil.setInt(context, SharedPreferencesUtil.Key.HEIGHT_UNIT, heightUnitIndex)
    }
}