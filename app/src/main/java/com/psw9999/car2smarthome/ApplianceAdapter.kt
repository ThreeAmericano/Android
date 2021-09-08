package com.psw9999.car2smarthome

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psw9999.car2smarthome.data.Appliance

// 어댑터 및 뷰홀더 상속/구현
// 뷰 홀더 : 스크롤을 밑으로 내리면 리사이클러뷰는 가장 위에 있던 뷰를 가장 아래쪽으로 이동시켜 재활용한다.
// 즉, 생성과 동시에 정의된 뷰 객체들만 계속해서 위에서 아래로 이동하면서 재사용되는 것이다.
class ApplianceAdapter(val context : Context) : RecyclerView.Adapter<ApplianceAdapter.ApplianceViewHolder>() {

    var appliances = mutableListOf<Appliance>()
    var itemIds = listOf<Int>()

    // RecyclerView에는 ListView와는 다르게 클릭리스터가 내장되어 있지 않다.
    // 그래서 추가로 ClickListener 역할을 하는 interface를 만들어주어야 한다.
    interface OnItemClickListener{
        //fun onItemClick(v:View, data: Appliance, pos : Int)
        fun onItemClick(pos : Int)
    }


    // 클릭 리스너 선언
    lateinit var mItemClickListener: OnItemClickListener
    // 클릭 리스너 등록 메서드 (메인 액티비티에서 람다식 혹은 inner 클래스로 호출)
    fun setOnItemClickListener(itemClickListener : OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    // onCreateViewHolder : 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplianceViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.item_appliance, parent, false)
        return ApplianceViewHolder(itemView)
    }

    // 전체 데이터의 개수를 리턴
    override fun getItemCount(): Int {
        return appliances.size
    }

    // onBindViewHolder : position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    override fun onBindViewHolder(holder: ApplianceViewHolder, position: Int) {
        holder.bind(appliances[position])
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    inner class ApplianceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val applianceName : TextView = itemView.findViewById(R.id.textView_appliance)
        val applianceImage : ImageView = itemView.findViewById(R.id.imageView_appliance)
        //val pos = adapterPosition

        init {
            itemView.setOnClickListener {
                mItemClickListener.onItemClick(adapterPosition)
            }
        }


        fun bind(item : Appliance) {
            applianceName.text = item.applianceName
            applianceImage.setImageResource(item.applianceDrawable)
//            val pos = adapterPosition
//            if(pos != RecyclerView.NO_POSITION)
//            {
//                Log.d("recyclerview","OK")
//                itemView.setOnClickListener {
//                    Log.d("recyclerview","OK")
//                    listner?.onItemClick(itemView,item,pos)
//                }
//            }
        }
    }
}