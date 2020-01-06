package de.abg.pamf.ui.rudder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.abg.pamf.R

class RudderFragment : Fragment() {

    private lateinit var rudderViewModel: RudderViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rudderViewModel =
            ViewModelProviders.of(this).get(RudderViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rudder, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        rudderViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}