package com.psw9999.car2smarthome

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class FragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    // 뷰페이저의 화면 아이템은 중간에 개수가 늘거나 줄지 않고, 처음에 정해진 개수만큼 사용하므로 mutableListOf 대신 listOf를 사용
    var fragmentList = listOf<Fragment>()

    // 페이지의 개수를 결정하기 위해 getItemCount 메서드에서 프래그먼트의 개수를 리턴함.
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    //페이지 요청시 getItem으로 요청되는 페이지의 프래그먼트 1개를 리턴함.
    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}