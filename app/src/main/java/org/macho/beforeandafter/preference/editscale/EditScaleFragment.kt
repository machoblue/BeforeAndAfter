package org.macho.beforeandafter.preference.editscale

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.edit_scale_frag.*
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.util.HeightUnitType
import org.macho.beforeandafter.shared.util.WeightUnitType
import javax.inject.Inject

class EditScaleFragment @Inject constructor(): DaggerFragment(), EditScaleContract.View {

    interface EditScaleFragmentListener {
        fun onComplete()
    }

    private var listener: EditScaleFragmentListener? = null

    @Inject
    override lateinit var presenter: EditScaleContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.edit_scale_frag, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).also {
            it.addAll(WeightUnitType.values().map { requireContext().getString(it.stringResourceId) })
            weightUnitSpinner.adapter = it
        }

        ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item).also {
            it.addAll(HeightUnitType.values().map { requireContext().getString(it.stringResourceId) })
            heightUnitSpinner.adapter = it
        }

        setHasOptionsMenu(true); // for save button on navBar
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                presenter.save(weightUnitSpinner.selectedItemPosition, heightUnitSpinner.selectedItemPosition)

                Toast.makeText(context, R.string.toast_saved, Toast.LENGTH_LONG).show()

                this.listener?.let {
                    it.onComplete()
                } ?: let {
                    findNavController().popBackStack()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.listener = context as? EditScaleFragmentListener
    }

    override fun updateViews(weightUnitIndex: Int, heightUnitIndex: Int) {
        weightUnitSpinner.setSelection(weightUnitIndex)
        heightUnitSpinner.setSelection(heightUnitIndex)
    }
}