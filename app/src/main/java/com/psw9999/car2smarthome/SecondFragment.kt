package com.psw9999.car2smarthome

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.psw9999.car2smarthome.data.Appliance
import com.psw9999.car2smarthome.databinding.FragmentMainBinding
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

    private lateinit var applianceAdapter : ApplianceAdapter
    private lateinit var binding: FragmentSecondBinding
    //private val activity = context as Activity
    val applianceDatas = mutableListOf<Appliance>()

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSecondBinding.inflate(inflater, container, false)
        binding.switchAircon.setOnCheckedChangeListener { compoundButton, ischedked ->
            binding.seekBarAirconPower.isEnabled = ischedked
        }
        return binding.root
    }

    private fun initRecyclerView(context : Context) {
        applianceAdapter = ApplianceAdapter(context)
        //binding.recyclerViewAppliance.adapter = applianceAdapter

        applianceDatas.apply {
            add(Appliance(applianceName = "에어컨", applianceDrawable = R.drawable.aircon))
            add(Appliance(applianceName = "전등", applianceDrawable = R.drawable.light))
            applianceAdapter.appliances = applianceDatas
            applianceAdapter.notifyDataSetChanged()
        }

        applianceAdapter.setOnItemClickListener(object : ApplianceAdapter.OnItemClickListener {
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

        binding.recyclerViewAppliance.adapter = applianceAdapter
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
        fun newInstance(param1: String, param2: String) =
            SecondFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
