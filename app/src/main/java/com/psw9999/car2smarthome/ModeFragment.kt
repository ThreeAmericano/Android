
package com.psw9999.car2smarthome

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import com.google.android.material.chip.Chip
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
class SecondFragment : Fragment(){
    private lateinit var Secondview: View
    private var curPos = 0
    private lateinit var modeAdapter: ModeAdapter
    private lateinit var binding: FragmentSecondBinding

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun ChipGroup.setChildrenEnabled(enable:Boolean) {
        children.forEach { it.isEnabled = enable }
    }

    fun ChipGroup.getChildrenTag() : Int? {
        children.forEach {
            if(it is Chip) {
                if(it.isChecked) return it.tag.toString().toInt()
            }
        }
        return null
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
            Log.d("secondFragment","changse")
            binding.seekBarLightBrightness.isEnabled = isChecked
            binding.chipGroupLightMode.setChildrenEnabled(isChecked)
            binding.chipGroupColor.setChildrenEnabled(isChecked)
        }

        // 모드 저장을 누르면 현재 뷰에 있는 상태의 뷰를 가져와 파이어베이스에 송신함.
        binding.buttonModeSave.setOnClickListener {
            modeDatas[curPos].airconEnable = binding.switchAircon.isChecked
            modeDatas[curPos].airconWindPower = ((binding.seekBarAirconPower.progress)/10)
            modeDatas[curPos].lightEnable = binding.switchLight.isChecked
            modeDatas[curPos].lightBrightness = (binding.seekBarLightBrightness.progress)/10
            modeDatas[curPos].gasValveEnable = binding.switchGasValve.isChecked
            modeDatas[curPos].windowOpen = binding.switchWindow.isChecked
            // TODO : null일 경우 default로 0 보내도록 설정
            modeDatas[curPos].lightColor = binding.chipGroupColor.getChildrenTag()!!-7
            modeDatas[curPos].lightMode = binding.chipGroupLightMode.getChildrenTag()!!-15
            db.collection("modes").document("${modeDatas[curPos].modeNum}"+". "+"${modeDatas[curPos].modeName}").set(modeDatas[curPos])
        }
        return binding.root
    }

    private fun initRecyclerView(context : Context) {
        modeAdapter = ModeAdapter(context)
        modeAdapter.modes = modeDatas
        modeAdapter.setOnItemClickListener(object : ModeAdapter.OnItemClickListener {
            override fun onItemClick(view: View, pos: Int) {
                curPos = pos
                binding.switchAircon.isChecked = modeDatas[curPos].airconEnable
                binding.seekBarAirconPower.progress = (modeDatas[curPos].airconWindPower)*10
                binding.switchLight.isChecked = modeDatas[curPos].lightEnable
                binding.seekBarLightBrightness.progress = (modeDatas[curPos].lightBrightness)*10
                binding.switchGasValve.isChecked = modeDatas[curPos].gasValveEnable
                binding.switchWindow.isChecked = modeDatas[curPos].windowOpen
                binding.chipGroupColor.clearCheck()
                binding.chipGroupLightMode.clearCheck()
                Secondview.findViewWithTag<Chip>(modeDatas[curPos].lightColor!!.plus(7).toString())?.isChecked = true
                Secondview.findViewWithTag<Chip>(modeDatas[curPos].lightMode!!.plus(15).toString())?.isChecked = true
            }
        })
        modeAdapter.notifyDataSetChanged()
        binding.recyclerViewAppliance.adapter = modeAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context : Context = requireContext()
        Secondview = view
        binding.switchAircon.isChecked = modeDatas[0].airconEnable
        binding.seekBarAirconPower.progress = (modeDatas[0].airconWindPower)*10
        binding.switchLight.isChecked = modeDatas[0].lightEnable
        binding.seekBarLightBrightness.progress = (modeDatas[0].lightBrightness)*10
        binding.switchGasValve.isChecked = modeDatas[0].gasValveEnable
        binding.switchWindow.isChecked = modeDatas[0].windowOpen
        view.findViewWithTag<Chip>(modeDatas[0].lightColor!!.plus(7).toString())?.isChecked = true
        view.findViewWithTag<Chip>(modeDatas[0].lightMode!!.plus(15).toString())?.isChecked = true
        initRecyclerView(context)
    }



    companion object {
        var modeDatas = mutableListOf<mode>()
    }
}
