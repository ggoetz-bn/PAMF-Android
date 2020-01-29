package de.abg.pamf.ui.calibrate


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import de.abg.pamf.R

/**
 * A simple [Fragment] subclass.
 */
class CalibrateScaleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calibrate_scale, container, false)
    }


}
