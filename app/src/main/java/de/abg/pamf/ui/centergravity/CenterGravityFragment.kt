package de.abg.pamf.ui.centergravity

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import de.abg.pamf.MainActivity
import de.abg.pamf.R


class CenterGravityFragment : Fragment() {

    val TAG = "COG_FRAGMENT"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        Log.e(TAG, "onCreateView")
        val root = inflater.inflate(R.layout.fragment_cog, container, false)

        setHasOptionsMenu(true)

//        val aa = ArrayAdapter<String>(context, R.layout.custom_spinner, resources.getStringArray(R.array.cog_spinner_values))
//        val aa = ArrayAdapter<String>()

        val scale1 = root.findViewById<Spinner>(R.id.cog_sp_scale1)
//        scale1.adapter  = aa
//        scale1.adapter
        scale1.setSelection(when(CogData.scale_1){1000 -> 0 5000 -> 1 10000 -> 2 else -> 0}, false)

        scale1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                Log.e(TAG, "something")
                CogData.scale_1 = when(pos){
                    0 -> 1000
                    1 -> 5000
                    2 -> 10000
                    else -> 1000
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val scale2 = root.findViewById<Spinner>(R.id.cog_sp_scale2)
        scale2.setSelection(when(CogData.scale_2){1000 -> 0 5000 -> 1 10000 -> 2 else -> 0}, false)
        scale2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                CogData.scale_2 = when(pos){
                    0 -> 1000
                    1 -> 5000
                    2 -> 10000
                    else -> 1000
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        val scale3 = root.findViewById<Spinner>(R.id.cog_sp_scale3)
        scale3.setSelection(when(CogData.scale_3){1000 -> 0 5000 -> 1 10000 -> 2 else -> 0}, false)
        scale3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                CogData.scale_3 = when(pos){
                    0 -> 1000
                    1 -> 5000
                    2 -> 10000
                    else -> 1000
                }
//                BluetoothCommunicator.sendMessage("1")
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
/*        (scale3.getChildAt(0) as TextView).ellipsize = null
        (scale3.getChildAt(0) as TextView).minEms = 5
        (scale3.getChildAt(0) as TextView).width = 350*/
//        (scale3.getChildAt(0) as TextView).setBackgroundResource(R.drawable.bg_box)
//        (scale3.getChildAt(0) as TextView).layoutParams.width = 350


/*        BluetoothCommunicator.sendMessage("EWD#0#START")
        BluetoothCommunicator.sendMessage("EWD#0#START")*/

        CogData.weight_1.observe(this, Observer<Int> {
            root.findViewById<TextView>(R.id.cog_tv_weight1).text = "" + it + "gr"
        })
        CogData.weight_2.observe(this, Observer<Int> {
            root.findViewById<TextView>(R.id.cog_tv_weight2).text = "" + it + "gr"
        })
        CogData.weight_3.observe(this, Observer<Int> {
            root.findViewById<TextView>(R.id.cog_tv_weight3).text = "" + it + "gr"
        })
        CogData.weight_sum.observe(this, Observer<Int> {
            root.findViewById<TextView>(R.id.cog_tv_weight_sum).text = "Gesamt: " + it + "gr"
        })
        CogData.center_of_gravity.observe(this, Observer<Int> {
            root.findViewById<TextView>(R.id.cog_tv_cog_front).text = getString(R.string.cog_cog_front, it)
        })
        CogData.center_of_gravity_diff.observe(this, Observer<Int> {
            if(it == 0){
                root.findViewById<TextView>(R.id.cog_tv_diff).text = ""
                root.findViewById<TextView>(R.id.cog_tv_diff_text).text = getString(R.string.cog_cog_diff_text_right)
            } else if(it < 0){
                root.findViewById<TextView>(R.id.cog_tv_diff).text = getString(R.string.cog_cog_diff, -it)
                root.findViewById<TextView>(R.id.cog_tv_diff_text).text = getString(R.string.cog_cog_diff_text_front)

            } else {
                root.findViewById<TextView>(R.id.cog_tv_diff).text = getString(R.string.cog_cog_diff, it)
                root.findViewById<TextView>(R.id.cog_tv_diff_text).text = getString(R.string.cog_cog_diff_text_back)
            }
        })

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cog, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.navigation_cog_settings -> {
                Log.d("Fragment", "Click")
/*                var nextFrag : CogSettingsFragment = CogSettingsFragment()
                activity!!.supportFragmentManager.beginTransaction()
                .hide(this)
                .replace(R.id.nav_host_fragment, nextFrag, "cog_settings_fragment")
                .addToBackStack(null)
                .commit()*/

                (activity as MainActivity).navController.navigate(R.id.navigation_cog_settings)
//                (activity as MainActivity).navController.navigateUp()

//                val intent = Intent(this.activity, CogSettingsActivity::class.java)
//                startActivity(intent)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        this.view!!.findViewById<ImageView>(R.id.cog_iv_type).setImageResource(
            if(CogData.type == 1) R.drawable.img_cg_spornfahrwerk_2 else R.drawable.img_cg_bugfahrwerk_2
        )

//        this.view!!.findViewById<TextView>(R.id.cog_tv_cog_front).text = getString(R.string.cog_cog_front, "200")


        CogData.requestWeights()
    }

    override fun onPause() {
        super.onPause()
        CogData.stopRequestingWeights()
    }
}