package de.abg.pamf.ui.centergravity

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModelProviders
import de.abg.pamf.R


class CenterGravityFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fav : MenuItem

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cog, container, false)

        setHasOptionsMenu(true)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cog, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_cog_settings -> {
/*                var nextFrag : CogSettingsFragment = CogSettingsFragment()
                activity!!.supportFragmentManager.beginTransaction()
                .hide(this)
                .replace(R.id.nav_host_fragment, nextFrag, "cog_settings_fragment")
                .addToBackStack(null)
                .commit()*/
                val intent = Intent(this.activity, CogSettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        this.view!!.findViewById<ImageView>(R.id.cog_iv_type).setImageResource(
            if(CogData.type == 1) R.drawable.img_sc_bugfahrwerk else R.drawable.img_sc_spornfahrwerk
        )

        this.view!!.findViewById<TextView>(R.id.cog_tv_cog_front).text = getString(R.string.cog_cog_front, "200")


    }
}