package org.macho.beforeandafter.preference.height

import android.content.Context
import org.macho.beforeandafter.shared.util.SharedPreferencesUtil
import javax.inject.Inject

class EditHeightPresenter @Inject constructor(): EditHeightContract.Presenter {
    var view: EditHeightContract.View? = null

    @Inject
    lateinit var context: Context

    override fun save(heightText: String) {
        SharedPreferencesUtil.setFloat(context, SharedPreferencesUtil.Key.HEIGHT, heightText.toFloatOrNull() ?: 0f)
        view?.finish()
    }

    override fun takeView(view: EditHeightContract.View) {
        this.view = view

        val height = SharedPreferencesUtil.getFloat(context, SharedPreferencesUtil.Key.HEIGHT)
        view.update(if (height == 0f) "" else height.toString())
    }

    override fun dropView() {
        this.view = null
    }
}