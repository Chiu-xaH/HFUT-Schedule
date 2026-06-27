@file:Suppress("DEPRECATION")

package com.hfut.schedule.ui.screen.home.search.function.huiXin.electric

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

import com.hfut.schedule.R
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.logic.database.repository.ElectricHistoryRepository
import com.hfut.schedule.logic.database.util.ElectricBalanceParser
import com.hfut.schedule.logic.database.util.ElectricMeterKeyFactory
import com.hfut.schedule.logic.enumeration.Campus
import com.hfut.schedule.logic.enumeration.CampusRegion
import com.hfut.schedule.logic.enumeration.getCampus
import com.hfut.schedule.logic.enumeration.getCampusRegion
import com.hfut.schedule.logic.model.HuiXinHefeiBuildingBean
import com.hfut.schedule.logic.model.huixin.FeeResponse
import com.hfut.schedule.logic.model.huixin.FeeType
import com.hfut.schedule.logic.network.exception.EmptyElectricResponseException
import com.hfut.schedule.logic.network.exception.ElectricResponseReadException
import com.hfut.schedule.logic.network.util.ElectricFeeResponseClassifier
import com.xah.common.logic.state.NetworkUiState

import com.hfut.schedule.logic.util.parse.roundOffString
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.HefeiElectricStorage
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager.getHefeiElectric
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.network.util.GsonInstance
import com.hfut.schedule.ui.component.input.WheelPicker
import com.hfut.schedule.ui.component.button.BottomButton
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.LoadingLargeCard
import com.hfut.schedule.ui.component.container.ShareTwoContainer2D
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.dialog.MenuChip
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.LoadingIcon
import com.hfut.schedule.ui.component.screen.pager.CustomTabRow
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.component.text.HazeBottomSheetTopBar
import com.hfut.schedule.ui.style.special.HazeBottomSheet
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.viewmodel.ui.ElectricHistoryViewModel
import com.xah.common.ui.component.text.BottomTip
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.logic.util.LogUtil
import dev.chrisbanes.haze.HazeState
import retrofit2.HttpException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject
import kotlin.time.Duration.Companion.milliseconds

private const val HEFEI_TAB = 0
private const val XUANCHENG_TAB = 1
private const val ELECTRIC_QUERY_TIMEOUT_MS = 15_000L


private fun getUrl(page : Int) : String {
    val auth = prefs.getString("auth","")
    return  Constant.HUI_XIN_URL +
            "charge-app/?name=pays&appsourse=ydfwpt&id=${
                if(page == XUANCHENG_TAB)
                    FeeType.ELECTRIC_XUANCHENG.code
                else 
                    FeeType.ELECTRIC_HEFEI_UNDERGRADUATE.code
            }&name=pays&paymentUrl=${Constant.HUI_XIN_URL}plat&token=" + auth
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EleUI(vm : NetWorkViewModel, hazeState: HazeState) {
    val titles = remember { listOf("合肥","宣城") }
    val context = LocalContext.current

    val pagerState = rememberPagerState(pageCount = { titles.size }, initialPage =
        when(getCampusRegion()) {
            CampusRegion.XUANCHENG -> XUANCHENG_TAB
            CampusRegion.HEFEI -> HEFEI_TAB
        }
    )
    val auth = prefs.getString("auth","")

    var buildingsNumber by remember { mutableStateOf("") }
    var roomNumber by remember { mutableStateOf("") }
    var endNumber by remember { mutableStateOf("") }
    var restoredXuanchengElectric by remember { mutableStateOf(false) }

    var region by remember { mutableStateOf("选择南北") }

    var showitem by remember { mutableStateOf(false) }
    var showitem2 by remember { mutableStateOf(false) }
    var showitem3 by remember { mutableStateOf(false) }
    var showitem4 by remember { mutableStateOf(false) }

    // Xuancheng display state — all set in coroutine, never in Composition
    var xuanchengRoomCodeText by remember { mutableStateOf("") }
    var xuanchengBalanceText by remember { mutableStateOf("") }
    var xuanchengResultVisible by remember { mutableStateOf(false) }
    var xuanchengQueryLoading by remember { mutableStateOf(false) }

    var showAdd by remember { mutableStateOf(false) }
    var payNumber by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    var json by remember { mutableStateOf("") }

    // Xuancheng success context
    var queriedMeterKey by remember { mutableStateOf<String?>(null) }
    var queriedRoomName by remember { mutableStateOf("") }

    // Query jobs for cancellation on rapid re-click
    var xuanchengQueryJob by remember { mutableStateOf<Job?>(null) }
    var xuanchengRequestId by remember { mutableLongStateOf(0L) }

    if (showBottomSheet) {

        HazeBottomSheet (
            onDismissRequest = { showBottomSheet = false },
            showBottomSheet = showBottomSheet,
        ) {
                Column(

                ) {
                    HazeBottomSheetTopBar("支付订单确认", isPaddingStatusBar = false)
                    val roomInfo by remember { mutableStateOf("${buildingsNumber}号楼${roomNumber}寝室${region}") }
                    val int by remember { mutableStateOf(payNumber.toFloat()) }
                    if(int > 0) {
                        PayFor(vm,int,roomInfo,json,FeeType.ELECTRIC_XUANCHENG,hazeState)
                    } else showToast("输入数值")
                }
        }
    }

    region = when(endNumber) {
        "11"-> if(buildingsNumber.toIntOrNull() ?: 0 > 5 )"南边照明" else "南边"
        "12" -> "南边空调"
        "21" -> if(buildingsNumber.toIntOrNull() ?: 0 > 5 )"北边照明" else "北边"
        "22" -> "北边空调"
        else -> "选择南北"
    }
    LaunchedEffect(Unit) {
        try {
            DataStoreManager.migrateXuanchengElectricIfNeeded()

            val saved = DataStoreManager.getXuanchengElectric() ?: return@LaunchedEffect
            buildingsNumber = saved.buildingNumber
            roomNumber = saved.roomNumber
            endNumber = saved.endNumber
            queriedMeterKey = ElectricMeterKeyFactory.xuancheng(
                "300${saved.buildingNumber}${saved.roomNumber}${saved.endNumber}"
            )
            queriedRoomName = saved.name
            restoredXuanchengElectric = true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LogUtil.error(e, "恢复宣城电费配置失败")
        }
    }

    var showDialog2 by remember { mutableStateOf(false) }
    if(showDialog2) {
        Dialog(onDismissRequest = { showDialog2 = false }) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedCard {
                        LazyColumn(modifier = Modifier.padding(horizontal = 10.dp)) {
                            item {
                                Text(
                                    text = "选取金额 ￥${payNumber}",
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += items.toString()
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "最高999元",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { Text(text = items.toString()) }
                                    }
                                }
                            }
                            item {
                                LazyRow {
                                    items(5) { items ->
                                        val num = items + 5
                                        IconButton(onClick = {
                                            if (payNumber.length < 3)
                                                payNumber += num
                                            else Toast.makeText(
                                                MyApplication.context,
                                                "最高999元",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }) { Text(text = num.toString()) }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    FilledTonalIconButton(
                        onClick = { payNumber = payNumber.replaceFirst(".$".toRegex(), "") },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.backspace),
                            contentDescription = "description"
                        )
                    }

                    FilledTonalIconButton(
                        onClick = {
                            showDialog2 = false
                            if (payNumber != "" && payNumber != "0" && payNumber != "00" && payNumber != "000")
                                showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Icon(painterResource(R.drawable.check), contentDescription = "description")
                    }

                    FilledTonalButton(
                        onClick = {
                            showDialog2 = false
                            payNumber = "0.01"
                            showBottomSheet = true
                        },
                        modifier = Modifier.padding(horizontal = 5.dp)
                    ) {
                        Text("尝试充值0.01")
                    }
                }
            }
        }
    }



    var menuOffset by remember { mutableStateOf<DpOffset?>(null) }


    menuOffset?.let {
        DropdownMenu(expanded = showitem, onDismissRequest = { showitem = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "北一号楼") }, onClick = { buildingsNumber =  "1"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北二号楼") }, onClick = {  buildingsNumber =  "2"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北三号楼") }, onClick = {  buildingsNumber =  "3"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北四号楼") }, onClick = {  buildingsNumber =  "4"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "北五号楼") }, onClick = {  buildingsNumber =  "5"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南六号楼") }, onClick = {  buildingsNumber =  "6"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南七号楼") }, onClick = {  buildingsNumber =  "7"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南八号楼") }, onClick = {  buildingsNumber =  "8"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南九号楼") }, onClick = {  buildingsNumber =  "9"
                showitem = false})
            DropdownMenuItem(text = { Text(text = "南十号楼") }, onClick = {  buildingsNumber = "10"
                showitem = false})
        }
        DropdownMenu(expanded = showitem2, onDismissRequest = { showitem2 = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "南边照明") }, onClick = { endNumber = "11"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "南边空调") }, onClick = { endNumber = "12"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "北边照明") }, onClick = { endNumber = "21"
                showitem2 = false})
            DropdownMenuItem(text = { Text(text = "北边空调") }, onClick = { endNumber = "22"
                showitem2 = false})
        }
        DropdownMenu(expanded = showitem3, onDismissRequest = { showitem3 = false }, offset = it) {
            DropdownMenuItem(text = { Text(text = "南边") }, onClick = { endNumber = "11"
                showitem3 = false })
            DropdownMenuItem(text = { Text(text = "北边") }, onClick = { endNumber = "21"
                showitem3 = false })
        }
    }

    val scope = rememberCoroutineScope()

    // Hefei success context and state
    var hefeiQueriedMeterKey by remember { mutableStateOf<String?>(null) }
    var hefeiQueriedRoomName by remember { mutableStateOf("") }
    var hefeiResultVisible by remember { mutableStateOf(false) }
    var hefeiQueryLoading by remember { mutableStateOf(false) }
    var hefeiQueryJob by remember { mutableStateOf<Job?>(null) }
    var hefeiRequestId by remember { mutableLongStateOf(0L) }
    var showHistoryPage by remember { mutableStateOf(false) }
    var historyPageTab by remember { mutableIntStateOf(pagerState.currentPage) }
    val hefeiHistoryViewModel = viewModel<ElectricHistoryViewModel>(key = "electric_history_hefei")
    val xuanchengHistoryViewModel = viewModel<ElectricHistoryViewModel>(key = "electric_history_xuancheng")

    fun searchXuancheng() {
        xuanchengQueryJob?.cancel()
        val requestId = ++xuanchengRequestId
        xuanchengQueryJob = scope.launch {
            if (requestId == xuanchengRequestId) xuanchengQueryLoading = true
            showitem4 = false
            // Snapshot immutable params
            val queryBuildingsNumber = buildingsNumber
            val queryRoomNumber = roomNumber
            val queryEndNumber = endNumber
            val queryRegion = region
            val queryInput = "300$queryBuildingsNumber$queryRoomNumber$queryEndNumber"
            val queryRoomName = "${queryBuildingsNumber}号楼${queryRoomNumber}寝室${queryRegion}"

            try {
                val result = withTimeoutOrNull(ELECTRIC_QUERY_TIMEOUT_MS.milliseconds) {
                    vm.queryElectricFee(
                        auth = "bearer $auth",
                        type = FeeType.ELECTRIC_XUANCHENG,
                        room = queryInput
                    )
                }
                if (requestId != xuanchengRequestId) return@launch
                if (result == null) {
                    showToast("查询超时，请稍后重试")
                    return@launch
                }
                if (!ElectricFeeResponseClassifier.isBusinessSuccess(result)) {
                    showToast("未能获取电费信息")
                    LogUtil.error("电费接口业务失败")
                    return@launch
                }
                try {
                    val jsons = GsonInstance.fromJson(result, FeeResponse::class.java).map
                    val data = jsons.showData
                    if (data.isEmpty()) {
                        if (requestId == xuanchengRequestId) showToast("未获取到电费数据")
                        return@launch
                    }
                    var hasValidBalance = false
                    for ((_, value) in data) {
                        val balance = ElectricBalanceParser.parseXuanchengBalance(value)
                        if (balance == null) {
                            LogUtil.error("宣城余额解析失败")
                            continue
                        }
                        if (requestId != xuanchengRequestId) return@launch
                        hasValidBalance = true
                        val meterKey = ElectricMeterKeyFactory.xuancheng(queryInput)
                        val displayRoomCode = value.substringBefore("剩余金额").replace(":", "").trim()
                        val displayBalance = balance.roundOffString(2)
                        xuanchengRoomCodeText = displayRoomCode
                        xuanchengBalanceText = displayBalance
                        xuanchengResultVisible = true
                        queriedMeterKey = meterKey
                        queriedRoomName = queryRoomName
                        DataStoreManager.saveXuanchengElectric(
                            DataStoreManager.XuanchengElectricStorage(
                                buildingNumber = queryBuildingsNumber,
                                roomNumber = queryRoomNumber,
                                endNumber = queryEndNumber,
                                name = queryRoomName
                            )
                        )
                        // Re-check before suspend call
                        if (requestId != xuanchengRequestId) return@launch
                        ElectricHistoryRepository.recordSnapshot(
                            meterKey = meterKey,
                            campusRegion = CampusRegion.XUANCHENG.description,
                            roomName = queryRoomName,
                            balance = balance
                        )
                    }
                    if (!hasValidBalance && requestId == xuanchengRequestId) {
                        showToast("未能解析电费余额")
                        return@launch
                    }
                } catch (e : CancellationException) {
                    throw e
                } catch (e : Exception) {
                    if (requestId != xuanchengRequestId) return@launch
                    LogUtil.error(e)
                    showToast("解析错误")
                }
                if (requestId != xuanchengRequestId) return@launch
                try {
                    val jsonObject = JSONObject(result)
                    val dataObject = jsonObject.getJSONObject("map").getJSONObject("data")
                    dataObject.put("myCustomInfo", "房间：$queryInput")
                    val paymentJson = dataObject.toString()
                    if (requestId != xuanchengRequestId) return@launch
                    json = paymentJson
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    if (requestId != xuanchengRequestId) return@launch
                    LogUtil.error(e)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (requestId != xuanchengRequestId) return@launch
                val msg = when (e.code()) {
                    401, 403 -> "登录状态已失效，请重新登录"
                    else -> "服务器错误：${e.code()}"
                }
                showToast(msg)
                LogUtil.error(e)
            } catch (e: EmptyElectricResponseException) {
                if (requestId != xuanchengRequestId) return@launch
                showToast("服务器未返回电费数据")
                LogUtil.error(e)
            } catch (e: ElectricResponseReadException) {
                if (requestId != xuanchengRequestId) return@launch
                showToast("电费数据读取失败")
                LogUtil.error(e)
            } catch (e: java.io.IOException) {
                if (requestId != xuanchengRequestId) return@launch
                showToast("网络连接失败")
                LogUtil.error(e)
            } catch (e: Exception) {
                if (requestId != xuanchengRequestId) return@launch
                LogUtil.error(e)
                showToast("电费查询失败")
            } finally {
                if (requestId == xuanchengRequestId) {
                    xuanchengQueryLoading = false
                    xuanchengQueryJob = null
                }
            }
        }
    }

    fun searchHefei() {
        hefeiQueryJob?.cancel()
        val requestId = ++hefeiRequestId
        hefeiQueryJob = scope.launch {
            if (requestId == hefeiRequestId) hefeiQueryLoading = true
            try {
                val data = getHefeiElectric()
                if (data == null) {
                    if (requestId == hefeiRequestId) showToast("请先选择寝室")
                    return@launch
                }
                val queryBuildingNumber = data.buildingNumber
                val queryRoomNumber = data.roomNumber
                val queryRoomName = data.name
                if (
                    queryBuildingNumber.isBlank() ||
                    queryRoomNumber.isBlank() ||
                    queryRoomName.isBlank()
                ) {
                    if (requestId == hefeiRequestId) showToast("寝室配置不完整，请重新选择")
                    return@launch
                }
                val queryMeterKey = ElectricMeterKeyFactory.hefei(
                    queryBuildingNumber,
                    queryRoomNumber
                )
                val result = withTimeoutOrNull(ELECTRIC_QUERY_TIMEOUT_MS.milliseconds) {
                    vm.queryElectricFee(
                        auth = "bearer $auth",
                        type = FeeType.ELECTRIC_HEFEI_UNDERGRADUATE,
                        room = queryRoomNumber,
                        building = queryBuildingNumber
                    )
                }
                if (requestId != hefeiRequestId) return@launch
                if (result == null) {
                    showToast("查询超时，请稍后重试")
                    return@launch
                }
                if (!ElectricFeeResponseClassifier.isBusinessSuccess(result)) {
                    showToast("未能获取电费信息")
                    LogUtil.error("电费接口业务失败")
                    return@launch
                }
                try {
                    val jsons = GsonInstance.fromJson(result, FeeResponse::class.java).map
                    val showData = jsons.showData
                    if (showData.isEmpty()) {
                        if (requestId == hefeiRequestId) showToast("未获取到电费数据")
                        return@launch
                    }
                    var hasValidBalance = false
                    for ((_, value) in showData) {
                        val balance = ElectricBalanceParser.parseHefeiBalance(value)
                        if (balance == null) {
                            LogUtil.error("合肥余额解析失败")
                            continue
                        }
                        if (requestId != hefeiRequestId) return@launch
                        hasValidBalance = true
                        hefeiResultVisible = true
                        hefeiQueriedMeterKey = queryMeterKey
                        hefeiQueriedRoomName = queryRoomName
                        DataStoreManager.saveHefeiElectricFee(value)
                        // Re-check after suspend call
                        if (requestId != hefeiRequestId) return@launch
                        ElectricHistoryRepository.recordSnapshot(
                            meterKey = queryMeterKey,
                            campusRegion = CampusRegion.HEFEI.description,
                            roomName = queryRoomName,
                            balance = balance
                        )
                    }
                    if (!hasValidBalance && requestId == hefeiRequestId) {
                        showToast("未能解析电费余额")
                        return@launch
                    }
                } catch (e : CancellationException) {
                    throw e
                } catch (e : Exception) {
                    if (requestId != hefeiRequestId) return@launch
                    LogUtil.error(e)
                    showToast("解析错误")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                if (requestId != hefeiRequestId) return@launch
                val msg = when (e.code()) {
                    401, 403 -> "登录状态已失效，请重新登录"
                    else -> "服务器错误：${e.code()}"
                }
                showToast(msg)
                LogUtil.error(e)
            } catch (e: EmptyElectricResponseException) {
                if (requestId != hefeiRequestId) return@launch
                showToast("服务器未返回电费数据")
                LogUtil.error(e)
            } catch (e: ElectricResponseReadException) {
                if (requestId != hefeiRequestId) return@launch
                showToast("电费数据读取失败")
                LogUtil.error(e)
            } catch (e: java.io.IOException) {
                if (requestId != hefeiRequestId) return@launch
                showToast("网络连接失败")
                LogUtil.error(e)
            } catch (e: Exception) {
                if (requestId != hefeiRequestId) return@launch
                showToast("电费查询失败")
                LogUtil.error(e)
            } finally {
                if (requestId == hefeiRequestId) {
                    hefeiQueryLoading = false
                    hefeiQueryJob = null
                }
            }
        }
    }

    // Auto-query once on page entry if config is complete
    var hasAutoQueried by remember { mutableStateOf(false) }
    LaunchedEffect(restoredXuanchengElectric, pagerState.currentPage) {
        if (hasAutoQueried) return@LaunchedEffect
        when (pagerState.currentPage) {
            HEFEI_TAB -> {
                hasAutoQueried = true
                searchHefei()
            }
            XUANCHENG_TAB -> {
                if (!restoredXuanchengElectric) return@LaunchedEffect
                hasAutoQueried = true
                if (buildingsNumber.isNotBlank() && roomNumber.isNotBlank() && endNumber.isNotBlank()) {
                    searchXuancheng()
                }
            }
        }
    }

    Column(modifier = Modifier) {
        HazeBottomSheetTopBar(if(showHistoryPage) "余额记录" else "寝室电费" , isPaddingStatusBar = false) {
            if(showHistoryPage) {
                IconButton(onClick = { showHistoryPage = false }) {
                    Icon(painter = painterResource(R.drawable.arrow_back), contentDescription = "back")
                }
            } else {
                Row() {
                    if(showitem4)
                        IconButton(onClick = {roomNumber = roomNumber.replaceFirst(".$".toRegex(), "")}) {
                            Icon(painter = painterResource(R.drawable.backspace), contentDescription = "description") }
                    FilledTonalIconButton(onClick = {
                        when(pagerState.currentPage) {
                            HEFEI_TAB -> {
                                searchHefei()
                            }
                            XUANCHENG_TAB -> {
                                searchXuancheng()
                            }
                        }
                    }) { Icon(painter = painterResource(R.drawable.search), contentDescription = "description") }

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                Starter.startWebUrlInner(context,getUrl(pagerState.currentPage), title = "慧新易校")
                            }
                        }
                    ) {
                        Text("官方充值")
                    }
                }
            }
        }
        if (buildingsNumber == "0") buildingsNumber = ""
        if(!showHistoryPage) {
            // Pager switch does NOT clear either campus result
            CustomTabRow(pagerState,titles)
        }
        ShareTwoContainer2D(
            modifier = Modifier.fillMaxWidth(),
            show = showHistoryPage,
            defaultContent = {
        Column(
        ) {
            HorizontalPager(state = pagerState) { page ->
                when(page) {
                    HEFEI_TAB -> {
                        LazyColumn {
                            item {
                                ElectricHefei(
                                    vm,
                                    hefeiResultVisible,
                                    hefeiQueryLoading,
                                    hefeiQueriedMeterKey,
                                    hefeiQueriedRoomName,
                                    hefeiHistoryViewModel,
                                    onOpenRecords = {
                                        historyPageTab = HEFEI_TAB
                                        showHistoryPage = true
                                    }
                                ) {
                                    searchHefei()
                                }
                            }
                        }
                    }
                    XUANCHENG_TAB -> {
                        LazyColumn {
                            item {
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = APP_HORIZONTAL_DP, vertical = 0.dp), horizontalArrangement = Arrangement.Start) {

                                MenuChip(
                                    label = { Text(text = "楼栋 $buildingsNumber") },
                                ) {
                                    menuOffset = it
                                    showitem = true
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                MenuChip(
                                    label = { Text(text = region) },
                                    //    leadingIcon = { Icon(painter = painterResource(R.drawable.calendar), contentDescription = "description") }
                                ) {
                                    menuOffset = it
                                    when {
                                        buildingsNumber.toIntOrNull() != null -> {
                                            when {
                                                buildingsNumber.toInt() > 5 -> showitem2 = true
                                                buildingsNumber.toInt() in 1..5 -> showitem3 = true
                                            }
                                        }
                                        else -> Toast.makeText(MyApplication.context,"请选择楼栋", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))
                                AssistChip(
                                    onClick = { showitem4 = !showitem4 },
                                    label = { Text(text = "寝室 $roomNumber") },
                                    //leadingIcon = { Icon(painter = painterResource(R.drawable.add), contentDescription = "description") }
                                )
                            }
                            }
                            item {
                            AnimatedVisibility(
                                visible = showitem4,
                                enter = slideInVertically(
                                    initialOffsetY = { -40 }
                                ) + expandVertically(
                                    expandFrom = Alignment.Top
                                ) + scaleIn(
                                    // Animate scale from 0f to 1f using the top center as the pivot point.
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                ) + fadeIn(initialAlpha = 0.3f),
                                exit = slideOutVertically() + shrinkVertically() + fadeOut() + scaleOut(targetScale = 1.2f)
                            ){
                                Row (modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)){
                                    OutlinedCard{
                                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                            Text(text = " 选取寝室号", modifier = Modifier.padding(10.dp))
                                            LazyRow {
                                                items(5) { items ->
                                                    IconButton(onClick = {
                                                        if (roomNumber.length < 3)
                                                            roomNumber += items.toString()
                                                        else Toast.makeText(
                                                            MyApplication.context,
                                                            "三位数",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }) { Text(text = items.toString()) }
                                                }
                                            }
                                            LazyRow {
                                                items(5) { items ->
                                                    val num = items + 5
                                                    IconButton(onClick = {
                                                        if (roomNumber.length < 3)
                                                            roomNumber += num
                                                        else Toast.makeText(MyApplication.context, "三位数", Toast.LENGTH_SHORT).show()
                                                    }) { Text(text = num.toString()) }
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }
                                }
                            }
                            }
                            item {
                            DividerTextExpandedWith(text = "查询结果",openBlurAnimation = false) {
                                LoadingLargeCard(
                                    title = if(!xuanchengResultVisible)"￥XX.XX"
                                    else
                                        "￥${xuanchengBalanceText}",
                                    loading = xuanchengQueryLoading ,
                                    prepare = true,
                                    rightTop = {
                                        FilledTonalButton(
                                            enabled = xuanchengResultVisible,
                                            onClick = {
                                                if(showAdd && payNumber != "")
                                                    showBottomSheet = true
                                                else showDialog2 = true
                                            }
                                        ) {
                                            Text(text =
                                                if(showAdd && payNumber != "")
                                                    "提交订单"
                                                else
                                                    "快速充值"
                                            )
                                        }
                                    }
                                ) {
                                    TransplantListItem(
                                        overlineContent = {Text( text = if(!xuanchengResultVisible)"房间号 " + " 300XXXXX1" else "房间号 $xuanchengRoomCodeText" )},
                                        headlineContent = { (if(!xuanchengResultVisible)"X号楼XXX寝室方向设施" else queriedRoomName.ifEmpty { null })?.let { Text(text = it) } },
                                        leadingContent = { Icon(painter = painterResource(id = R.drawable.info), contentDescription = "")}
                                    )
                                }
                                Spacer(modifier = Modifier.height(APP_HORIZONTAL_DP/2))
                            }
                            }
                            item {
                            DividerTextExpandedWith("使用说明", defaultIsExpanded = false) {
                                CustomCard(
                                    color = cardNormalColor()
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("夜间透支") },
                                        supportingContent = { Text("每日23:00之前缴费，最迟23:00到账，23:00之后缴费，次日6点到账，在23:01-6:00时间段，账户如果欠费不会断电")},
                                        leadingContent = { Icon(painterResource(R.drawable.dark_mode),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("最大功率") },
                                        supportingContent = { Text("超出800W将自动断电，5分钟后自动恢复")},
                                        leadingContent = { Icon(painterResource(R.drawable.hvac_max_defrost),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("月末补贴") },
                                        supportingContent = { Text("照明空调各￥15，约下旬初到账")},
                                        leadingContent = { Icon(painterResource(R.drawable.paid),null)}
                                    )
                                    PaddingHorizontalDivider()
                                    TransplantListItem(
                                        headlineContent = { Text("寝室缴费实测存在一定延迟") },
                                        leadingContent = { Icon(painterResource(R.drawable.schedule),null)}
                                    )
                                }
                            }
                            }
                            item {
                            ElectricHistorySection(
                                meterKey = queriedMeterKey,
                                roomName = queriedRoomName,
                                viewModel = xuanchengHistoryViewModel,
                                onOpenRecords = {
                                    historyPageTab = XUANCHENG_TAB
                                    showHistoryPage = true
                                }
                            )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier
                .height(APP_HORIZONTAL_DP)
                .navigationBarsPadding())
        }
            },
            secondContent = {
            ElectricBalanceRecordPage(
                viewModel = if(historyPageTab == HEFEI_TAB) hefeiHistoryViewModel else xuanchengHistoryViewModel
            )
            }
        )
    }
}

private enum class ExpandState {
    NONE, CAMPUS, BUILDING, TYPE
}


@Composable
fun ElectricHefei(
    vm : NetWorkViewModel,
    hefeiResultVisible : Boolean,
    hefeiQueryLoading : Boolean,
    hefeiQueriedMeterKey : String?,
    hefeiQueriedRoomName : String,
    hefeiHistoryViewModel: ElectricHistoryViewModel,
    onOpenRecords: () -> Unit,
    search : () -> Unit
) {
    var expandState by remember { mutableStateOf(ExpandState.NONE) }
    var campus by remember { mutableStateOf(getCampus() ?: Campus.TXL) }
    var buildingCode by remember { mutableIntStateOf(0) }
    var typeCode by remember { mutableStateOf<Type?>(null) }
    val showType = expandState == ExpandState.TYPE
    val showCampus = expandState == ExpandState.CAMPUS
    val showBuildings = expandState == ExpandState.BUILDING


    Row(modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)) {
        AnimatedVisibility(
            visible = !showType,
            enter = expandIn(),
            exit = shrinkOut()
        ) {
            Row {
                AnimatedVisibility(
                    visible = !showBuildings,
                    enter = expandIn(),
                    exit = shrinkOut(),
                ) {
                    AssistChip(
                        onClick = { expandState = if(showCampus) ExpandState.NONE else ExpandState.CAMPUS },
                        label = {
                            Text(if(!showCampus) campus.description + "校区" else "选择校区")
                            AnimatedVisibility(
                                visible = showCampus,
                                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                            ) {
                                WheelPicker(
                                    data = Campus.entries,
                                    initialSelectedIndex = Campus.entries.indexOf(campus),
                                    modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                                    onSelect = { index, element ->
                                        campus = element
                                    }
                                ) {
                                    Text(it.description.toString() + "校区")
                                }
                            }
                        }
                    )
                }
                AnimatedVisibility(
                    visible = !showCampus,
                    enter = expandIn(),
                    exit = shrinkOut(),
                    modifier = Modifier.padding(start = if(showBuildings) 0.dp else CARD_NORMAL_DP*2)
                ) {
                    AssistChip(
                        onClick = { expandState = if(showBuildings) ExpandState.NONE else ExpandState.BUILDING },
                        label = {
                            Text(if(showBuildings)"选择楼栋" else getBuildingStr(buildingCode,campus))
                            AnimatedVisibility(
                                visible = showBuildings,
                                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                            ) {
                                WheelPicker(
                                    data = when(campus) {
                                        Campus.XC -> IntArray(10) { it+1 }
                                        Campus.TXL -> IntArray(14) { it+1 }
                                        Campus.FCH -> IntArray(13) { it+1 }
                                    }.toList(),
                                    initialSelectedIndex = if(buildingCode <= 0) 0 else buildingCode-1,
                                    modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                                    onSelect = { index, element ->
                                        buildingCode = element
                                    }
                                ) {
                                    val description = getBuildingStr(it,campus)
                                    Text(description)
                                }
                            }
                        }
                    )
                }
            }
        }
        AnimatedVisibility(
            visible = !showCampus && !showBuildings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.padding(start = if(showType) 0.dp else CARD_NORMAL_DP*2)
        ) {
            AssistChip(
                onClick = {
                    expandState = if (showType) ExpandState.NONE else ExpandState.TYPE
                },
                label = {
                    Text(if (showType) "选择区域" else typeCode?.description ?: "选择区域")
                    AnimatedVisibility(
                        visible = showType,
                        enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                        exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
                    ) {
                        WheelPicker(
                            data = getType(campus, buildingCode),
                            initialSelectedIndex = 0,
                            modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                            onSelect = { index, element ->
                                typeCode = element
                            }
                        ) {
                            Text(it.description)
                        }
                    }
                }
            )
        }
    }

    val finalRegion = typeCode?.let {
        campus.description + getBuildingStr(buildingCode,campus) + it.description
    }
    val buildingResponse by vm.hefeiBuildingsResp.state.collectAsState()
    val getBuildings = suspend m@ {
        if(buildingResponse is NetworkUiState.Success) {
            return@m
        }
        val auth = prefs.getString("auth","")
        auth?.let {
            vm.hefeiBuildingsResp.clear()
            vm.getHefeiBuildings("bearer $it")
        }
    }
    val roomResponse by vm.hefeiRoomsResp.state.collectAsState()
    val getRooms : suspend(String) -> Unit =  m@ { building : String ->
        val auth = prefs.getString("auth","")
        auth?.let {
            vm.hefeiRoomsResp.clear()
            vm.getHefeiRooms("bearer $it",building)
        }
    }
    LaunchedEffect(Unit) {
        vm.hefeiRoomsResp.emitPrepare()
    }
    val scope = rememberCoroutineScope()
    var buildingNumber by remember { mutableStateOf<HuiXinHefeiBuildingBean?>(null) }
    LaunchedEffect(finalRegion) {
        finalRegion?.let { final ->
            getBuildings()
            val data = (buildingResponse as? NetworkUiState.Success)?.data ?: return@LaunchedEffect
            val bean = data.find {
                val name = it.name
                val isCampus = name.startsWith(campus.description)
                // 判断校区
                if(isCampus) {
                    if(name.contains("研")) {
                        name == final
                    } else {
                        val buildingStr = name.substringBefore("号").substringAfter(campus.description)
                        // 判断楼栋
                        val isBuilding = getBuildingStr(buildingCode,campus).startsWith(buildingStr)
                        if(isBuilding) {
                            // 判断区域
                            val isRegion = name.endsWith(typeCode!!.description) || name.endsWith(typeCode!!.description.replace("楼",""))
                            isRegion
                        } else {
                            false
                        }
                    }
                } else {
                    false
                }
            }
            if(bean == null) {
                showToast("未找到此区域,可能是接口变更,请联系开发者")
            } else {
                buildingNumber = bean
//                showToast("正在获取${bean.name}的房间")
                getRooms(bean.value)
            }
        }
    }
    var showRoom by remember { mutableStateOf(false) }
    var roomNumber by remember { mutableStateOf<HuiXinHefeiBuildingBean?>(null) }
    LaunchedEffect(roomNumber) {
        roomNumber?.let {
            buildingNumber?.let { it1 ->
                DataStoreManager.saveHefeiElectric(HefeiElectricStorage(
                    roomNumber = it.value,
                    name = it.name,
                    buildingNumber = it1.value
                ))
            }
        }
    }
    AssistChip(
        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
        onClick = { showRoom = !showRoom },
        leadingIcon = {
            if(roomResponse is NetworkUiState.Loading) {
                LoadingIcon()
            }
        },
        enabled = roomResponse is NetworkUiState.Success,
        label = {
            Text(
                if(showRoom) {
                    if(roomResponse is NetworkUiState.Loading) {
                        "载入房间列表"
                    } else {
                        "选择房间"
                    }
                } else {
                    roomNumber?.name ?: "选择房间"
                }
            )
            AnimatedVisibility(
                visible = showRoom,
                enter = expandIn(expandFrom = Alignment.Center) + scaleIn(),
                exit = shrinkOut(shrinkTowards = Alignment.Center) + scaleOut(),
            ) {
                val list = (roomResponse as? NetworkUiState.Success)?.data
                list?.let {
                    WheelPicker(
                        data = it,
                        initialSelectedIndex = 0,
                        modifier = Modifier.padding(start = APP_HORIZONTAL_DP),
                        onSelect = { index, element ->
                            roomNumber = element
                        }
                    ) {
                        Text(it.name)
                    }
                }
            }
        }
    )
    var useLocal by remember { mutableStateOf(false) }
    val savedData by produceState<HefeiElectricStorage?>(initialValue = null, key1 = hefeiResultVisible) {
        value = getHefeiElectric()
    }

    AnimatedVisibility(
        visible = !useLocal && finalRegion == null,
        enter = scaleIn(),
        exit = scaleOut()
    ) {
        savedData?.let {
            CustomCard (color = cardNormalColor()){
                TransplantListItem(
                    headlineContent = {
                        Text(it.name)
                    },
                    leadingContent = {
                        Icon(painterResource(R.drawable.info),null)
                    }
                )
                BottomButton(
                    onClick = {
                        useLocal = true
                        search()
                    },
                    text = "使用上一次的记录查询"
                )
            }
        }
    }

    val result by DataStoreManager.hefeiElectricFee.collectAsState(initial = "XX.XX")
    DividerTextExpandedWith("查询结果",openBlurAnimation = false) {
        LoadingLargeCard(
            title = if(!hefeiResultVisible)"￥XX.XX" else "￥$result",
            loading = hefeiQueryLoading ,
            prepare = true,
            rightTop = {
                FilledTonalButton(
                    enabled = hefeiResultVisible,
                    onClick = {
                        showToast("正在开发")
                    }
                ) {
                    Text(text = "快速充值")
                }
            }
        ) {
            TransplantListItem(
                headlineContent = {
                    Text(
                        savedData?.name ?: "校区X号X楼XXXXX"
                    )
                },
                leadingContent = {
                    Icon(painterResource(R.drawable.info),null)
                },
            )
        }
        BottomTip("快速充值开发中 请先使用官方充值")
    }

    ElectricHistorySection(
        meterKey = hefeiQueriedMeterKey,
        roomName = hefeiQueriedRoomName,
        viewModel = hefeiHistoryViewModel,
        onOpenRecords = onOpenRecords
    )
}

private fun getBuildingStr(content : Int, campus : Campus) : String {
    if(content <= 0) {
        return "选择楼栋"
    }
    return when(campus) {
        Campus.XC -> {
            content.toString() + "号楼"
        }
        Campus.FCH -> {
            content.toString() + "号楼"
        }
        Campus.TXL -> {
            if(content > 10) {
                "研${numToChinese(content-7)}"
            } else {
                content.toString() + "号楼"
            }
        }
    }
}
private fun numToChinese(num : Int) : String {
    return when(num) {
        4 -> "四"
        5 -> "五"
        6 -> "六"
        7 -> "七"
        else -> ""
    }
}

private fun getType( campus : Campus,buildingCode : Int) : List<Type> {
    return when(campus) {
        Campus.XC -> {
            if(buildingCode <= 5) {
                // 南楼 北楼
                Type1.entries
            } else {
                // 北楼空调 北楼空调 南楼空调 南楼照明
                Type2.entries
            }
        }
        Campus.FCH -> {
            if(buildingCode == 13) {
                // 北楼空调 北楼照明 南楼空调 南楼照明 中楼空调 中楼照明
                Type3.entries
            } else {
                // 北楼空调 北楼照明 南楼空调 南楼照明
                Type2.entries
            }
        }
        Campus.TXL -> {
            if(buildingCode in listOf(3,4,9)) {
                // 空调 照明
                Type4.entries
            } else {
                // 北楼空调 北楼照明 南楼空调 南楼照明
                Type2.entries
            }
        }
    }
}

private interface Type {
    val description: String
}

private enum class Type1(override val description: String) : Type {
    SOUTH("南楼"), NORTH("北楼")
}

private enum class Type2(override val description: String) : Type {
    SOUTH_AIR("南楼空调"), NORTH_AIR("北楼空调"),
    SOUTH_LIGHT("南楼照明"), NORTH_LIGHT("北楼照明"),
}

private enum class Type3(override val description: String) : Type {
    SOUTH_AIR("南楼空调"), NORTH_AIR("北楼空调"), MIDDLE_AIR("中楼空调"),
    SOUTH_LIGHT("南楼照明"), NORTH_LIGHT("北楼照明"), MIDDLE_LIGHT("中楼照明")
}

private enum class Type4(override val description: String) : Type {
    AIR("空调"), LIGHT("照明")
}
