package com.hfut.schedule.service.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.hfut.schedule.logic.network.repo.Repository
import com.hfut.schedule.logic.util.network.StateHolder
import com.hfut.schedule.ui.component.onListenStateHolder
import com.hfut.schedule.ui.component.showToast
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.Campus.XUANCHENG
import com.hfut.schedule.ui.screen.home.search.function.jxglstu.transfer.getCampus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class LoginXuanChengSchoolNetTileService : LoginSchoolNetTileService(campus = Campus.XUANCHENG)