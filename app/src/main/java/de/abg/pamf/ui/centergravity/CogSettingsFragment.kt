package de.abg.pamf.ui.centergravity

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import de.abg.pamf.R

class CogSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cog_settings, container, false)
/*        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(this, Observer {
            textView.text = it
        })*/

        // Setzt das Mnu (Zurück bzw. Speichern Button)
        setHasOptionsMenu(true)

        // Zeigt die gespeicherten Werte an
        showValues(root)

        // Wechsel des Fahrwerks
        root.findViewById<RadioGroup>(R.id.cog_s_rg_type).setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener{ group, checkedId ->
                if(checkedId == R.id.cog_s_rb_type_1)
                   CogData.type = 1
                else
                   CogData.type = 2
                showValues(root)
            }
        )

        return root
    }

    fun showValues(root : View) {
        if(CogData.distance_1_2 != 0)
            root.findViewById<EditText>(R.id.cog_s_et_scalesdistance).setText("" + CogData.distance_1_2)
        if(CogData.distance_front != 0)
            root.findViewById<EditText>(R.id.cog_s_et_distancefront).setText("" + CogData.distance_front)
        if(CogData.distance_target != 0)
            root.findViewById<EditText>(R.id.cog_s_et_distancetarget).setText("" + CogData.distance_target)

        // Unterschiedliche Texte und Bilder zeigen, je nachdem welches Fahrwerk ausgewählt ist
        if(CogData.type == 1) {
            root.findViewById<TextView>(R.id.cog_s_tv_distancefront).setText(R.string.cog_s_distancefront_1)
            root.findViewById<ImageView>(R.id.cog_s_iv_type).setImageResource(R.drawable.img_sc_bugfahrwerk)
            root.findViewById<RadioGroup>(R.id.cog_s_rg_type).check(R.id.cog_s_rb_type_1)
        } else {
            root.findViewById<TextView>(R.id.cog_s_tv_distancefront).setText(R.string.cog_s_distancefront_2)
            root.findViewById<ImageView>(R.id.cog_s_iv_type).setImageResource(R.drawable.img_sc_spornfahrwerk)
            root.findViewById<RadioGroup>(R.id.cog_s_rg_type).check(R.id.cog_s_rb_type_2)
        }
    }

    fun saveValues(){
        CogData.distance_1_2 = this.view!!.findViewById<EditText>(R.id.cog_s_et_scalesdistance).text.toString().toIntOrNull() ?: 0
        CogData.distance_front = this.view!!.findViewById<EditText>(R.id.cog_s_et_distancefront).text.toString().toIntOrNull() ?: 0
        CogData.distance_target = this.view!!.findViewById<EditText>(R.id.cog_s_et_distancetarget).text.toString().toIntOrNull() ?: 0
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_cog_settings -> {
                // Eingaben speichern
                saveValues()
//                activity!!.supportFragmentManager.popBackStack()
                activity!!.finish()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        saveValues()
    }


}