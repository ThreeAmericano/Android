package com.psw9999.car2smarthome

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.car2smarthome.data.Appliance

// 어댑터 및 뷰홀더 상속/구현
// 뷰 홀더 : 스크롤을 밑으로 내리면 리사이클러뷰는 가장 위에 있던 뷰를 가장 아래쪽으로 이동시켜 재활용한다.
// 즉, 생성과 동시에 정의된 뷰 객체들만 계속해서 위에서 아래로 이동하면서 재사용되는 것이다.
class ApplianceAdapter(val context : Context) : RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder>() {

    private var appliances = listOf<Appliance>()
    //var breads = listOf<Bread>()
    var itemIds = listOf<Int>()

//    public ApplianceAdapter(var data : kotlin.collections.listOf<Appliance>) {
//        appliances = data
//    }

    // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        if(habits === null) return 0
        else return habits!!.size
    }

    override fun onBindViewHolder(holder: HabitListViewHolder, position: Int) {
        holder.bind(habits!![position])
    }


    inner class ApplianceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }
}