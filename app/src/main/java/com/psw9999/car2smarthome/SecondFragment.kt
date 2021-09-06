package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.chip.ChipGroup
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.psw9999.car2smarthome.Adapter.ModeAdapter
import com.psw9999.car2smarthome.data.Appliance
import com.psw9999.car2smarthome.data.mode
import com.psw9999.car2smarthome.databinding.FragmentSecondBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SecondFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SecondFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var modeAdapter: ModeAdapter
    private lateinit var binding: FragmentSecondBinding

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.switchAircon.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.seekBarAirconPower.isEnabled = isChecked
        }
        binding.switchLight.setOnCheckedChangeListener { compoundButton, isChecked ->
            binding.seekBarLightBrightness.isEnabled = isChecked
            binding.chipGroupColor.isClickable = isChecked
            binding.chipGroupLightMode.isClickable = isChecked
        }
        Log.d("secondFragment","${modeDatas[0]}")
        binding.switchAircon.isChecked = modeDatas[0].airconEnable
        binding.seekBarAirconPower.progress = (modeDatas[0].airconWindPower)*10
        binding.switchLight.isChecked = modeDatas[0].lightEnable
        binding.seekBarLightBrightness.progress = (modeDatas[0].lightBirghtness)*10
        binding.chipGroupColor.check(modeDatas[0].lightColor)
        binding.chipGroupLightMode.check(modeDatas[0].lightMode)
        binding.switchGasValve.isChecked = modeDatas[0].gasValveEnable
        binding.switchWindow.isChecked = modeDatas[0].windowOpen
        return binding.root
    }

    private fun initRecyclerView(context : Context) {
        modeAdapter = ModeAdapter(context)

        modeAdapter.modes = modeDatas
        modeAdapter.notifyDataSetChanged()

        modeAdapter.setOnItemClickListener(object : ModeAdapter.OnItemClickListener {
            override fun onItemClick(pos: Int) {
                Log.d("recycleview","$pos")
                db.collection("modes")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.d("firebaseStore", "${document.id} => ${document.data}")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("firebaseStore", "Error getting documents: ", exception)
                    }
            }
        })

        binding.recyclerViewAppliance.adapter = modeAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context : Context = requireContext()
        initRecyclerView(context)
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SecondFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic

        var modeDatas = mutableListOf<mode>()

        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
