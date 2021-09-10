package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.psw9999.car2smarthome.Adapter.ModeAdapter
import com.psw9999.car2smarthome.Adapter.SchedulesAdapter
import com.psw9999.car2smarthome.data.mode
import com.psw9999.car2smarthome.data.scheduleData
import com.psw9999.car2smarthome.databinding.FragmentSecondBinding
import com.psw9999.car2smarthome.databinding.FragmentThirdBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

private lateinit var schedulesAdapter: SchedulesAdapter
private lateinit var binding: FragmentThirdBinding

/**
 * A simple [Fragment] subclass.
 * Use the [ThirdFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThirdFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        binding = FragmentThirdBinding.inflate(inflater, container, false)
        val context : Context = requireContext()
        initRecyclerView(context)
        return binding.root
        //return inflater.inflate(R.layout.fragment_third, container, false)
    }

    private fun initRecyclerView(context : Context) {
        schedulesAdapter = SchedulesAdapter(context)
        schedulesAdapter.schedules = scheduleDatas
        schedulesAdapter.notifyDataSetChanged()
        binding.recyclerViewScheduleList.adapter = schedulesAdapter
    }

    companion object {

        var scheduleDatas = mutableListOf<scheduleData>()


        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThirdFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}