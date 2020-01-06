package de.abg.pamf.ui.ewd

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import de.abg.pamf.R

class EwdFragment : Fragment() {

    private lateinit var ewdViewModel: EwdViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ewdViewModel =
            ViewModelProviders.of(this).get(EwdViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_rudder, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        ewdViewModel.text.observe(this, Observer {
            textView.text = it
        })
        return root
    }
}