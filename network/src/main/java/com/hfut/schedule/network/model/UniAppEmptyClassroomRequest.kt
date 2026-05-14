package com.hfut.schedule.network.model

import com.hfut.schedule.network.util.Constant

data class UniAppEmptyClassroomRequest(
    val currentPage : Int,
    // 从date向后7天，YYYY-MM-DD
    val date : String,
    // 校区ID
    val campusAssoc : Int?,
    // 建筑ID
    val buildingIds : List<Int>?,
    // 楼层
    val floors : List<Int>?,
    val pageSize : Int = Constant.DEFAULT_PAGE_SIZE,
)