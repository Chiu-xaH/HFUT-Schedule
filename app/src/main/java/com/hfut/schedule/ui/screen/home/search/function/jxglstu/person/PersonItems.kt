package com.hfut.schedule.ui.screen.home.search.function.jxglstu.person

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.network.state.UiState
import com.hfut.schedule.logic.util.other.AppVersion
import com.hfut.schedule.logic.util.sys.ClipBoardUtils
import com.hfut.schedule.logic.util.sys.datetime.DateTimeManager
import com.hfut.schedule.logic.util.storage.SharedPrefs.prefs
import com.hfut.schedule.logic.util.parse.formatDecimal
import com.hfut.schedule.logic.util.storage.DataStoreManager
import com.hfut.schedule.ui.component.APP_HORIZONTAL_DP
import com.hfut.schedule.ui.screen.home.search.function.huiXin.loginWeb.getCardPsk
 
import com.hfut.schedule.ui.component.DepartmentIcons
import com.hfut.schedule.ui.component.DividerTextExpandedWith
import com.hfut.schedule.ui.component.custom.HazeBottomSheetTopBar
import com.hfut.schedule.logic.util.sys.showToast
import com.hfut.schedule.ui.component.CommonNetworkScreen
import com.hfut.schedule.ui.component.TransplantListItem
import com.hfut.schedule.ui.style.appBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import kotlinx.coroutines.flow.first
import org.jsoup.Jsoup
import kotlin.collections.get


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PersonItems(vm : NetWorkViewModel) {

    val photo = prefs.getString("photo",null)

    val studentnumber = getPersonInfo().username
    val name = getPersonInfo().name
    val chineseid = getPersonInfo().chineseID


    val studyType = getPersonInfo().benshuo
    var yuanxi = getPersonInfo().department
    if (yuanxi != null) {
        if(yuanxi.contains("("))yuanxi = yuanxi.substringBefore("(")
        if(yuanxi.contains("（"))yuanxi = yuanxi.substringBefore("（")
    }
    val major = getPersonInfo().major
    val classes = getPersonInfo().classes
    val school = getPersonInfo().school
    val home = getPersonInfo().home
    val xueJiStatus = getPersonInfo().status
    val program = getPersonInfo().program
    val startDate = getPersonInfo().startDate
    val endDate = getPersonInfo().endDate
    val majorDirection = getPersonInfo().majorDirection
    val studyTime = getPersonInfo().studyTime

    var show by remember { mutableStateOf(false) }
    var show2 by remember { mutableStateOf(false) }
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
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            HazeBottomSheetTopBar("个人信息")
        },) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
        ) {
            DividerTextExpandedWith(text = "账号信息") {
                name?.let {
                    TransplantListItem(
                        headlineContent = { Text(text = it) },
                        overlineContent = { Text(text ="姓名" )},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.signature),
                                contentDescription = "Localized description",
                            )
                        }
                    )
                }

                studentnumber?.let {
                    TransplantListItem(
                        headlineContent = {   Text(text = it)  },
                        overlineContent = { Text(text ="学号" )},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.badge),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                             ClipBoardUtils.copy(it)
                        }
                    )
                }

                Box() {
                    FilledTonalIconButton(onClick = { show2 = !show2 }, modifier = Modifier.zIndex(1f).align(Alignment.CenterEnd).padding(horizontal = APP_HORIZONTAL_DP)) {
                        Icon(painter = painterResource(id = if(show2)R.drawable.visibility else R.drawable.visibility_off), contentDescription = "")
                    }
                    Column(modifier = appBlur(!show2)) {
                        chineseid?.let {
                            TransplantListItem(
                                headlineContent = { Text(text = it)  },
                                overlineContent = { Text(text = "身份证号")},
                                leadingContent = {
                                    Icon(
                                        painterResource(R.drawable.tag),
                                        contentDescription = "Localized description",
                                    )
                                },
                                modifier = Modifier.clickable {
                                    ClipBoardUtils.copy(it)
                                }
                            )
                        }
                    }
                }
            }

            DividerTextExpandedWith(text = "就读信息") {
                school?.let {
                    TransplantListItem(
                        headlineContent = { Text(text = it) },
                        overlineContent = { Text(text = "校区")},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.near_me),
                                contentDescription = "Localized description",
                            )
                        },
                    )
                }

                yuanxi?.let {
                    TransplantListItem(
                        headlineContent = { Text(text = it)  },
                        overlineContent = { Text(text = "学院")},
                        leadingContent = {
                            DepartmentIcons(name = it)
                        },
                        modifier = Modifier.clickable {
                             ClipBoardUtils.copy(it)
                        }
                    )
                }


                major?.let {
                    TransplantListItem(
                        headlineContent = {
                            Text(text = it)
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
                           ClipBoardUtils.copy(it)
                        }
                    )
                }

                classes?.let {
                    TransplantListItem(
                        headlineContent = {  Text(text = it) },
                        overlineContent = { Text(text = "班级")},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.sensor_door),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        }
                    )
                }

            }

            DividerTextExpandedWith(text = "密码信息") {
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
                Column(modifier = appBlur(!show)) {
                    val pwd= prefs.getString("Password","")
                    pwd?.let {
                        TransplantListItem(
                            headlineContent = { Text(text = it) },
                            overlineContent = { Text(text = "CAS统一认证密码")},
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            }
                        )
                    }
                    chineseid?.let {
                        val p = it.takeLast(6)
                        val d = "Hfut@#\$%${p}"
                        TransplantListItem(
                            headlineContent = {
                                Text(text = d)
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
                            overlineContent = { Text(text = "一卡通&校园网密码")},
                            modifier = Modifier.clickable {
                                ClipBoardUtils.copy(it)
                            }
                        )
                    }
                }
            }

            DividerTextExpandedWith(text = "学籍信息") {
                studyType?.let {
                    TransplantListItem(
                        headlineContent = { Text(text = it)  },
                        overlineContent = { Text(text = "类型")},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.school),
                                contentDescription = "Localized description",
                            )
                        },
                    )
                }
                xueJiStatus?.let {
                    TransplantListItem(
                        headlineContent = {  Text(text = it) },
                        overlineContent = { Text(text = "学籍状态")},
                        leadingContent = {
                            Icon(
                                painterResource(
                                    if(xueJiStatus.contains("正常")) {
                                        R.drawable.check_circle
                                    } else if(xueJiStatus.contains("转专业")) {
                                        R.drawable.compare_arrows
                                    } else if(xueJiStatus.contains("毕业")) {
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
                    headlineContent = { Text(text = "$startDate\n$endDate") },
                    overlineContent = { Text(text = "学制 $studyTime")},
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
                    headlineContent = { program?.let { Text(text = it) } },
                    overlineContent = { Text(text = "培养方案")},
                    leadingContent = {
                        Icon(
                            painterResource(R.drawable.conversion_path),
                            contentDescription = "Localized description",
                        )
                    },
                    modifier = Modifier.clickable {
                        showToast("前往 查询中心-培养方案查看详情")
                    }
                )
                home?.let {
                    TransplantListItem(
                        headlineContent = {  Text(text = it)  },
                        overlineContent = { Text(text = "来源")},
                        leadingContent = {
                            Icon(
                                painterResource(R.drawable.location_on),
                                contentDescription = "Localized description",
                            )
                        },
                        modifier = Modifier.clickable {}
                    )
                }
                photo?.let {
                    var showPhoto by remember { mutableStateOf(false) }
                    TransplantListItem(
                        headlineContent = { Text(text = "学籍照") },
                        trailingContent = {
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
                            showPhoto = !showPhoto
                        }
                    )
                }
            }

            DividerTextExpandedWith("私人信息") {
                getPersonInfo().gender?.let {
                    TransplantListItem(
                        headlineContent = {  Text(it)  },
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
                getPersonInfo().politicalStatus?.let {
                    TransplantListItem(
                        headlineContent = { Text(it)  },
                        overlineContent = { Text("政治面貌") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.groups),null)
                        },
                    )
                }
                val mobile = getPersonInfo().mobile
                val phone = getPersonInfo().phone
                if(mobile != null && phone != null && mobile == phone) {
                    TransplantListItem(
                        headlineContent = {  Text(mobile) },
                        overlineContent = { Text("手机号") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.call),null)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(mobile)
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

                getPersonInfo().email?.let {
                    TransplantListItem(
                        headlineContent = {  Text(it) },
                        overlineContent = { Text("电子邮件") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.alternate_email),null)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        }
                    )
                }
                getPersonInfo().address?.let {
                    TransplantListItem(
                        headlineContent = {  Text(it)  },
                        overlineContent = { Text("地址") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.home),null)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        }
                    )
                }

                getPersonInfo().postalCode?.let {
                    TransplantListItem(
                        headlineContent = { Text(it) } ,
                        overlineContent = { Text("邮编") },
                        leadingContent = {
                            Icon(painterResource(R.drawable.mail),null)
                        },
                        modifier = Modifier.clickable {
                            ClipBoardUtils.copy(it)
                        }
                    )
                }

            }

            DividerTextExpandedWith("寝室信息") {
                CommonNetworkScreen(uiState,isFullScreen = false, onReload = refreshNetwork) {
                    val data = (uiState as UiState.Success).data
                    Column {
                        TransplantListItem(
                            headlineContent = { Text(data.campus_dictText + " " + data.dormitory.substringBefore("（") + " " + data.room)},
                            overlineContent = { Text("所在寝室") },
                            leadingContent = {
                                Icon(painterResource(R.drawable.bed),null)
                            }
                        )
                        CommonNetworkScreen(uiState2,isFullScreen = false, onReload = refreshNetwork) {
                            val data2 = (uiState2 as UiState.Success).data
                            Column {
                                for(i in data2) {
                                    TransplantListItem(
                                        headlineContent = {
                                            Text(i.realname + " | " + i.username)
                                        },
                                        overlineContent = { Text("寝室成员") },
                                        leadingContent = {
                                            Icon(painterResource(R.drawable.group),null)
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
                      val username: String?,
                      val chineseID : String?,
                      val classes : String?,
                      val major : String?,
                      val department : String?,
                      val school : String?,
                      val benshuo : String?,
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


fun extractPassword(html: String): String? {
    // 解析 HTML 内容
    val document = Jsoup.parse(html)

    // 查找包含密码的 input 元素
    val passwordElement = document.select("input#plainPassword").first()

    // 获取密码值
    return passwordElement?.attr("value")
}
