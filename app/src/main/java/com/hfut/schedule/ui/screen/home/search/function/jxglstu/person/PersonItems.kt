package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.sys.showToast
import com.xah.uicommon.style.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.component.container.CustomCard
import com.hfut.schedule.ui.component.container.TransplantListItem
import com.hfut.schedule.ui.component.container.cardNormalColor
import com.hfut.schedule.ui.component.container.mixedCardNormalColor
import com.hfut.schedule.ui.component.divider.PaddingHorizontalDivider
import com.hfut.schedule.ui.component.icon.DepartmentIcons
import com.hfut.schedule.ui.component.network.CommonNetworkScreen
import com.hfut.schedule.ui.component.screen.CustomTransitionScaffold
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.screen.AppNavRoute
import com.hfut.schedule.logic.enumeration.HazeBlurLevel
import com.hfut.schedule.logic.util.storage.file.LargeStringDataManager
import com.hfut.schedule.ui.component.button.LiquidButton
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
import com.xah.uicommon.style.padding.InnerPaddingHeight
import com.hfut.schedule.ui.style.special.coverBlur
import com.hfut.schedule.ui.style.special.topBarBlur
import com.xah.uicommon.style.color.topBarTransplantColor
import com.hfut.schedule.ui.util.navigation.navigateForTransition
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.screen.home.getJxglstuCookie
import com.hfut.schedule.ui.style.special.backDropSource
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.transition.component.containerShare
import com.xah.transition.state.LocalAnimatedContentScope
import com.xah.transition.state.LocalSharedTransitionScope
import com.xah.uicommon.component.status.LoadingScreen
import com.xah.uicommon.component.status.LoadingUI
import com.xah.uicommon.style.align.CenterScreen
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
    vm: NetWorkViewModel,
    navController : NavHostController,
) {
    val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
    val hazeState = rememberHazeState(blurEnabled = blur)
    val route = remember { AppNavRoute.Person.route }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val refreshNetwork  =  {
        scope.launch(Dispatchers.IO) {
            val cookie = getJxglstuCookie() ?: return@launch
            loading = true
            async {
                launch {
                    vm.getInfo(cookie)
                }
                launch {
                    vm.getPhoto(cookie)
                }
            }.await()
            loading = false
        }
    }

    val backdrop = rememberLayerBackdrop()
    CustomTransitionScaffold (
        route = route,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        navHostController = navController,
        topBar = {
            MediumTopAppBar(
                scrollBehavior = scrollBehavior,
                modifier = Modifier.topBarBlur(hazeState),
                colors = topBarTransplantColor(),
                title = { Text(AppNavRoute.Person.label) },
                navigationIcon = {
                    TopBarNavigationIcon(navController,route, AppNavRoute.Person.icon)
                },
                actions = {
                    LiquidButton(
                        onClick = {
                            refreshNetwork()
                        },
                        modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                        isCircle = true,
                        backdrop = backdrop
                    ) {
                        Icon(painterResource(R.drawable.rotate_right),null)
                    }
                }
            )
        },
    ) { innerPadding ->
        if(loading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .backDropSource(backdrop)
                    .hazeSource(hazeState)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize()
            ) {
                InnerPaddingHeight(innerPadding,true)
                PersonItems(vm,navController)
                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
//    }
}
@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalSharedTransitionApi::class
)
@Composable
private fun PersonItems(
    vm : NetWorkViewModel,
    navController : NavHostController,
) {
    val context = LocalContext.current
    val photo by produceState<String?>(initialValue = null) {
        value = LargeStringDataManager.read(context, LargeStringDataManager.PHOTO)
    }

    val info = getPersonInfo()

    val studentnumber = info.studentId
    val name = info.name
    val chineseid = info.chineseID


    val studyType = info.educationLevel
    var yuanxi = info.department
    if (yuanxi != null) {
        if(yuanxi.contains("("))yuanxi = yuanxi.substringBefore("(")
        if(yuanxi.contains("（"))yuanxi = yuanxi.substringBefore("（")
    }
    val major = info.major
    val classes = info.className
    val school = info.campus
    val home = info.home
    val xueJiStatus = info.status
    val program = info.program
    val startDate = info.startDate
    val endDate = info.endDate
    val majorDirection = info.majorDirection
    val studyTime = info.studyTime

    var show by rememberSaveable { mutableStateOf(false) }
    var show2 by rememberSaveable() { mutableStateOf(chineseid == null) }
    val cardPsk by produceState(initialValue = "") {
        value = getCardPsk() ?: ""
    }
    val uiState by vm.dormitoryFromCommunityResp.state.collectAsState()
    val uiState2 by vm.dormitoryInfoFromCommunityResp.state.collectAsState()
    val refreshNetwork : suspend () -> Unit = {
        prefs.getString("TOKEN","")?.let {
            vm.dormitoryFromCommunityResp.clear()
            vm.getDormitory(it)
            if(vm.dormitoryFromCommunityResp.state.first() is UiState.Success) {
                vm.getDormitoryInfo(it)
            }
        }
    }
    LaunchedEffect(Unit) {
        refreshNetwork()
    }
    Column() {
        DividerTextExpandedWith(text = "账号信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = name  ?: "---") },
                    overlineContent = { Text(text ="姓名" )},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.signature),
                            contentDescription = "Localized description",
                        )
                    }
                )

                TransplantListItem(
                    headlineContent = {   Text(text = studentnumber ?: "----------")  },
                    overlineContent = { Text(text ="学号" )},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.tag),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        studentnumber?.let { ClipBoardUtils.copy(it) }
                    }
                )

                Box() {
                    FilledTonalIconButton(onClick = { show2 = !show2 }, modifier = Modifier
                        .zIndex(1f)
                        .align(Alignment.CenterEnd)
                        .padding(horizontal = APP_HORIZONTAL_DP)) {
                        Icon(painter = painterResource(id = if(show2)R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
                    }
                    Column(modifier = Modifier.coverBlur(!show2)) {
                        TransplantListItem(
                            headlineContent = { Text(text = chineseid ?: "------------------")  },
                            overlineContent = { Text(text = "身份证号")},
                            leadingContent = {
                                Icon(
                                    painterResource(R.drawable.person),
                                    contentDescription = "Localized description",
                                )
                            },
                            modifier = Modifier.clickable {
                                chineseid?.let { ClipBoardUtils.copy(it) }
                            }
                        )
                    }
                }
            }

        }

        val route = remember { AppNavRoute.Classmates.route }
        DividerTextExpandedWith(text = "就读信息") {
            CustomCard(
                color = mixedCardNormalColor(),
                modifier = Modifier.containerShare(
                    route = route,
                    roundShape = MaterialTheme.shapes.medium
                )
            ) {
                TransplantListItem(
                    headlineContent = { Text(text = school ?: "----") },
                    overlineContent = { Text(text = "校区")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.near_me),
                            contentDescription = "Localized description",
                        )
                    },
                )
                TransplantListItem(
                    headlineContent = { Text(text = yuanxi ?: "--------")  },
                    overlineContent = { Text(text = "学院")},
                    leadingContent = {
                        DepartmentIcons(name = yuanxi ?: "")
                    },
                    modifier = Modifier.clickable {
                        yuanxi?.let { ClipBoardUtils.copy(it) }
                    }
                )


                TransplantListItem(
                    headlineContent = {
                        Text(text = major ?: "--------")
                    },
                    overlineContent = { Text(text = "专业")},
                    supportingContent = { majorDirection?.let { if(it != "") Text(text = "方向 $it") else null } },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.square_foot),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        major?.let { ClipBoardUtils.copy(it) }
                    }
                )

                TransplantListItem(
                    modifier = Modifier.clickable {
                        classes?.let { ClipBoardUtils.copy(it) }
                    },
                    headlineContent = {  Text(text = classes ?: "-------") },
                    overlineContent = { Text(text = "班级")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.sensor_door),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalButton(
                            onClick = {
                                navController.navigateForTransition(AppNavRoute.Classmates,route)
                            }
                        ) {
                            Text(AppNavRoute.Classmates.label)
                        }
                    },
                )
            }
        }

        DividerTextExpandedWith(text = "密码信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = "密码") },
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.key),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(onClick = { show = !show }) {
                            Icon(painter = painterResource(id = if(show)R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
                        }
                    }
                )
                PaddingHorizontalDivider()
                Column(modifier = Modifier.coverBlur(!show)) {
                    val pwd= prefs.getString("Password","")
                    pwd?.let {
                        TransplantListItem(
                            headlineContent = { Text(text = it) },
                            overlineContent = { Text(text = "CAS统一认证密码")},
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            },
                            trailingContent = {
                                FilledTonalButton(
                                    onClick = {
                                        showToast("前往 登陆界面中的选项 修改")
                                    }
                                ) {
                                    Text("修改")
                                }
                            },
                        )
                    }
                    chineseid.let {
                        val p = it?.takeLast(6) ?: "------"
                        val d = "Hfut@#$%${p}"
                        TransplantListItem(
                            headlineContent = {
                                Text(text = d)
                            },
                            trailingContent = {
                                FilledTonalButton(
                                    onClick = {
                                        showToast("前往 选项-网络-教务系统密码 修改")
                                    }
                                ) {
                                    Text("修改")
                                }
                            },
                            overlineContent = { Text(text = "教务系统初始密码")},
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(d)
                            }
                        )
                    }
                    cardPsk.let {
                        TransplantListItem(
                            headlineContent = {  Text(text = it) },
                            overlineContent = { Text(text = "一卡通及校园网初始密码")},
                            trailingContent = {
                                FilledTonalButton(
                                    onClick = {
                                        showToast("前往 选项-网络-一卡通密码 修改")
                                    }
                                ) {
                                    Text("修改")
                                }
                            },
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            }
                        )
                    }
                }
            }

        }

        DividerTextExpandedWith(text = "学籍信息") {
            CustomCard(color = cardNormalColor()) {
                TransplantListItem(
                    headlineContent = { Text(text = studyType ?: "-----")  },
                    overlineContent = { Text(text = "类型")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.school),
                            contentDescription = "Localized description",
                        )
                    },
                )
                xueJiStatus.let {
                    TransplantListItem(
                        headlineContent = {  Text(text = it ?: "--") },
                        overlineContent = { Text(text = "学籍状态")},
                        leadingContent = {
                            Icon(
                                painterResource(
                                    if(it == null) {
                                        R.drawable.help
                                    } else if(it.contains("正常")) {
                                        R.drawable.check_circle
                                    } else if(it.contains("转专业")) {
                                        R.drawable.compare_arrows
                                    } else if(it.contains("毕业")) {
                                        R.drawable.verified
                                    } else {
                                        R.drawable.help
                                    }
                                ),
                                contentDescription = "Localized description",
                            )
                        },
                    )
                }

                TransplantListItem(
                    headlineContent = { Text(text = "${startDate ?: "YYYY-MM-DD"}\n${endDate ?: "YYYY-MM-DD"}") },
                    overlineContent = { Text(text = "学制 ${studyTime ?: "-"} 年")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.schedule),
                            contentDescription = "Localized description",
                        )
                    },
                    trailingContent = {
                        if(startDate != null && endDate != null && startDate != "" && endDate != "") {
                            Text(text = "已过 ${formatDecimal(DateTimeManager.getPercent(startDate,endDate),1)}%")
                        } else { null }
                    },
                    modifier = Modifier.clickable {
                        xueJiStatus?.let { ClipBoardUtils.copy(it) }
                    }
                )
                TransplantListItem(
                    headlineContent = {  Text(text = program ?: "----------------")  },
                    overlineContent = { Text(text = "培养方案")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.conversion_path),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        showToast("前往 查询中心-培养方案")
                    }
                )
                TransplantListItem(
                    headlineContent = {  Text(text = home ?: "---省")  },
                    overlineContent = { Text(text = "来源")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.location_on),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {}
                )
                photo.let {
                    var showPhoto by remember { mutableStateOf(false) }
                    TransplantListItem(
                        headlineContent = { Text(text = "学籍照") },
                        trailingContent = m@ {
                            if(it == null) {
                                FilledTonalButton(
                                    onClick = { },
                                ) {
                                    Text("--")
                                }
                                return@m
                            }
                            if(showPhoto) {
                                val byteArray = Base64.decode(it, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(byteArray , 0, byteArray.size)
                                val imageBitmap = bitmap.asImageBitmap()
                                Image(bitmap = imageBitmap,
                                    contentDescription = "Displayed image",
                                    modifier = Modifier
                                        .size(130.dp)
                                        .padding(10.dp))
                            } else {
                                FilledTonalButton(
                                    onClick = { showPhoto = !showPhoto },
                                ) {
                                    Text("显示")
                                }
                            }
                        },
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.background_replace),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            if(it != null) {
                                showPhoto = !showPhoto
                            }
                        }
                    )
                }
            }

        }

        DividerTextExpandedWith("私人信息") {
            CustomCard(color = cardNormalColor()) {
                info.gender.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "-")  },
                        overlineContent = { Text("性别") },
                        leadingContent = {
                            Icon(painterResource(
                                when(it) {
                                    "男" -> R.drawable.male
                                    "女" -> R.drawable.female
                                    else -> R.drawable.help
                                }
                            ),null)
                        },
                    )
                }
                info.politicalStatus.let {
                    TransplantListItem(
                        headlineContent = { Text(it ?: "--")  },
                        overlineContent = { Text("政治面貌") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.groups),null)
                        },
                    )
                }
                val mobile = info.mobile
                val phone = info.phone
                if(mobile == phone) {
                    TransplantListItem(
                        headlineContent = {  Text(mobile ?: "-----------") },
                        overlineContent = { Text("手机号") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.call),null)
                        },
                        modifier = Modifier.clickable {
                            mobile?.let { ClipBoardUtils.copy(it) }
                        }
                    )
                } else {
                    mobile?.let {
                        TransplantListItem(
                            headlineContent = {  Text(it) },
                            overlineContent = { Text("手机") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.smartphone),null)
                            },
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            }
                        )
                    }
                    phone?.let {
                        TransplantListItem(
                            headlineContent = { Text(it) },
                            overlineContent = { Text("电话") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.call),null)
                            },
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            }
                        )
                    }
                }

                info.email.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "-------------") },
                        overlineContent = { Text("电子邮件") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.alternate_email),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardUtils.copy(str) }
                        }
                    )
                }
                info.address.let {
                    TransplantListItem(
                        headlineContent = {  Text(it ?: "----------------")  },
                        overlineContent = { Text("地址") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.home),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardUtils.copy(str) }
                        }
                    )
                }

                info.postalCode.let {
                    TransplantListItem(
                        headlineContent = { Text(it ?: "------") } ,
                        overlineContent = { Text("邮编") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.mail),null)
                        },
                        modifier = Modifier.clickable {
                            it?.let { str -> ClipBoardUtils.copy(str) }
                        }
                    )
                }
            }


        }

        DividerTextExpandedWith("寝室信息") {
            CommonNetworkScreen(uiState,isFullScreen = false, onReload = refreshNetwork) {
                val data = (uiState as UiState.Success).data
                CustomCard(color = cardNormalColor()) {
                    Column {
                        TransplantListItem(
                            headlineContent = { Text(data.campus_dictText + " " + data.dormitory.substringBefore("（") + " " + data.room)},
                            overlineContent = { Text("所在寝室") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.bed),null)
                            }
                        )
                        PaddingHorizontalDivider()
                        CommonNetworkScreen(uiState2,isFullScreen = false, onReload = refreshNetwork) {
                            val data2 = (uiState2 as UiState.Success).data
                            Column {
                                for(i in data2) {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(i.realname + " | " + i.username)
                                        },
                                        modifier = Modifier.clickable {
                                            ClipBoardUtils.copy(i.username)
                                        },
                                        overlineContent = { Text("寝室成员") },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.person),null)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class PersonInfo(val name : String?,
                      val studentId: String?,
                      val chineseID : String?,
                      val className : String?,
                      val major : String?,
                      val department : String?,
                      val campus : String?,
                      val educationLevel : String?,
                      val home: String?,
                      val status : String?,
                      val program : String?,
                      val startDate : String?,
                      val endDate : String?,
                      val majorDirection : String?,
                      val studyTime : String?,
                      val gender: String?,
                      val politicalStatus: String?, // 政治面貌
                      val email: String?,
                      val phone: String?,
                      val mobile: String?,
                      val address: String?,
                      val postalCode: String? // 邮编

)


fun getPersonInfo() : PersonInfo {
    try {
        val info = prefs.getString("info","")

        val doc = info?.let { Jsoup.parse(it) }
        val studentnumber = doc?.select("li.list-group-item.text-right:contains(学号) span")?.last()?.text()
        val name = doc?.select("li.list-group-item.text-right:contains(中文姓名) span")?.last()?.text()
        val chineseid = doc?.select("li.list-group-item.text-right:contains(证件号) span")?.last()?.text()
        val elements = doc?.select("dl dt, dl dd")

        val infoMap = mutableMapOf<String, String>()
        if (elements != null) {
            for (i in 0 until elements.size step 2) {
                val key = elements[i].text()
                val value = elements[i+1].text()
                infoMap[key] = value
            }
        }

        val benorsshuo =infoMap[elements?.get(8)?.text()]
        var department =infoMap[elements?.get(10)?.text()]
        if (department != null) {
            if(department.contains("("))department = department.substringBefore("(")
            if(department.contains("（"))department = department.substringBefore("（")
        }
        val zhuanye =infoMap[elements?.get(12)?.text()]
        val classes =infoMap[elements?.get(16)?.text()]
        val school =infoMap[elements?.get(18)?.text()]
        val home =infoMap[elements?.get(80)?.text()]


        val xueJiStatus =infoMap[elements?.get(20)?.text()]//学籍状态 正常 转专业
        val program =infoMap[elements?.get(22)?.text()]
        val startDate = infoMap[elements?.get(38)?.text()]
        val endDate = infoMap[elements?.get(86)?.text()]
        val majorDirection = infoMap[elements?.get(14)?.text()] //专业方向
        val studyTime = infoMap[elements?.get(36)?.text()]//学制 4.0

        val profileJson = prefs.getString("profile","") ?: ""
        val doc2 = Jsoup.parse(profileJson)
        val items = doc2.select(".list-group-item")

        val dataMap = mutableMapOf<String, String>()
        items.forEach { item ->
            val key = item.select("div.col-md-3 strong").text().trim() // 直接选 strong 避免干扰
            val value = item.select("div.col-md-6 span").first()?.ownText()?.trim() ?: "" // 只取第一个 span 并用 ownText()

            if (key.isNotEmpty() && value.isNotEmpty()) {
                dataMap[key] = value
            }
        }
        val gender = dataMap["性别"]
        val politicalStatus = dataMap["政治面貌"]
        val email = dataMap["邮箱"]
        val phone = dataMap["电话"]
        val mobile = dataMap["手机"]
        val address = dataMap["地址"]
        val postalCode = dataMap["邮编"]

        return PersonInfo(name,studentnumber,chineseid,classes,zhuanye,department, school,benorsshuo,home,xueJiStatus,program, startDate, endDate, majorDirection, studyTime,gender, politicalStatus, email, phone, mobile, address, postalCode)
    } catch (_:Exception) {
        return PersonInfo(null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null)
    }
}