package com.hfut.schedule.ui.nav.destination

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.DataStoreManager
import com.hfut.schedule.logic.util.sys.AppNotificationManager
import com.hfut.schedule.logic.util.sys.PermissionSet
import com.hfut.schedule.ui.component.button.NoPadding
import com.hfut.schedule.ui.component.button.TopBarNavigationIcon
import com.hfut.schedule.ui.component.container.CARD_NORMAL_DP
import com.hfut.schedule.ui.component.container.CardListItem
import com.hfut.schedule.ui.component.text.DividerTextExpandedWith
import com.hfut.schedule.ui.nav.destination.base.NavDestination
import com.hfut.schedule.ui.screen.home.search.function.other.life.RoomMap
import com.hfut.schedule.ui.style.special.backDropSource
import com.hfut.schedule.ui.style.special.topBarBlur
import com.hfut.schedule.viewmodel.network.NetWorkViewModel
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import com.xah.common.ui.style.APP_HORIZONTAL_DP
import com.xah.common.ui.style.align.RowHorizontal
import com.xah.common.ui.style.color.topBarTransplantColor
import com.xah.common.ui.style.padding.InnerPaddingHeight
import com.xah.common.ui.util.text
import com.xah.navigation.util.LocalNavDependencies
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

object TestDestination : NavDestination() {
    override val key = "test"
    override val title = text("开发者调试页面")
    override val icon = R.drawable.build

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val blur by DataStoreManager.enableHazeBlur.collectAsState(initial = true)
        val hazeState = rememberHazeState(blurEnabled = blur)
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val backdrop = rememberLayerBackdrop()

        val vm = LocalNavDependencies.current.get<NetWorkViewModel>()
        val context = LocalContext.current
        val activity = LocalActivity.current
        val scope = rememberCoroutineScope()

        Scaffold (
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                Column(
                    modifier = Modifier.topBarBlur(hazeState),
                ) {
                    MediumTopAppBar(
                        scrollBehavior = scrollBehavior,
                        colors = topBarTransplantColor(),
                        title = { Text(title.asString()) },
                        navigationIcon = {
                            TopBarNavigationIcon()
                        },
                    )
                }
            },
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .backDropSource(backdrop)
                    .hazeSource(hazeState)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                InnerPaddingHeight(innerPadding,true)
                LaunchedEffect(activity) {
                    if(activity == null) {
                        return@LaunchedEffect
                    }
                    PermissionSet.checkAndRequestNotificationPermission(activity)
                }
                CardListItem(
                    headlineContent = {
                        Text("发送实时通知")
                    },
                    modifier = Modifier.clickable {
                        AppNotificationManager.updateCourseProgress("课程","08:00","12:00")
                    }
                )
/*

                val buildings = remember {
                    parseXmlToRooms("""
                        <annotation>
                        	<folder>my-project-name</folder>
                        	<filename>XA_4.jpg</filename>
                        	<path>/my-project-name/XA_4.jpg</path>
                        	<source>
                        		<database>Unspecified</database>
                        	</source>
                        	<size>
                        		<width>4096</width>
                        		<height>2896</height>
                        		<depth>3</depth>
                        	</size>
                        	<object>
                        		<name>420</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>505</xmin>
                        			<ymin>670</ymin>
                        			<xmax>775</xmax>
                        			<ymax>985</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>422</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>777</xmin>
                        			<ymin>670</ymin>
                        			<xmax>1058</xmax>
                        			<ymax>981</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Toliet</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1252</xmin>
                        			<ymin>724</ymin>
                        			<xmax>1441</xmax>
                        			<ymax>934</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>418</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>447</xmin>
                        			<ymin>1081</ymin>
                        			<xmax>598</xmax>
                        			<ymax>1202</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>416</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>371</xmin>
                        			<ymin>1215</ymin>
                        			<xmax>596</xmax>
                        			<ymax>1621</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>414</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>366</xmin>
                        			<ymin>1625</ymin>
                        			<xmax>597</xmax>
                        			<ymax>1816</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Toliet</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>442</xmin>
                        			<ymin>1826</ymin>
                        			<xmax>592</xmax>
                        			<ymax>2025</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>427</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>406</xmin>
                        			<ymin>2104</ymin>
                        			<xmax>496</xmax>
                        			<ymax>2347</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>424</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1062</xmin>
                        			<ymin>668</ymin>
                        			<xmax>1250</xmax>
                        			<ymax>980</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>412</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>864</xmin>
                        			<ymin>1813</ymin>
                        			<xmax>1146</xmax>
                        			<ymax>2026</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>410</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1147</xmin>
                        			<ymin>1816</ymin>
                        			<xmax>1433</xmax>
                        			<ymax>2027</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>408</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1435</xmin>
                        			<ymin>1786</ymin>
                        			<xmax>1623</xmax>
                        			<ymax>2029</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Toliet</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2369</xmin>
                        			<ymin>1779</ymin>
                        			<xmax>2569</xmax>
                        			<ymax>2027</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>432</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3555</xmin>
                        			<ymin>1202</ymin>
                        			<xmax>3717</xmax>
                        			<ymax>1507</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>434</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3556</xmin>
                        			<ymin>1508</ymin>
                        			<xmax>3717</xmax>
                        			<ymax>1815</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Toliet</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2579</xmin>
                        			<ymin>720</ymin>
                        			<xmax>2774</xmax>
                        			<ymax>932</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2577</xmin>
                        			<ymin>1069</ymin>
                        			<xmax>2682</xmax>
                        			<ymax>1313</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1339</xmin>
                        			<ymin>1068</ymin>
                        			<xmax>1444</xmax>
                        			<ymax>1318</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>417</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1435</xmin>
                        			<ymin>2101</ymin>
                        			<xmax>1612</xmax>
                        			<ymax>2264</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1610</xmin>
                        			<ymin>2102</ymin>
                        			<xmax>1719</xmax>
                        			<ymax>2359</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2272</xmin>
                        			<ymin>2101</ymin>
                        			<xmax>2382</xmax>
                        			<ymax>2362</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>409</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2382</xmin>
                        			<ymin>2100</ymin>
                        			<xmax>2565</xmax>
                        			<ymax>2264</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>409</xmin>
                        			<ymin>667</ymin>
                        			<xmax>505</xmax>
                        			<ymax>925</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>689</xmin>
                        			<ymin>1907</ymin>
                        			<xmax>855</xmax>
                        			<ymax>2027</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3265</xmin>
                        			<ymin>1892</ymin>
                        			<xmax>3457</xmax>
                        			<ymax>2023</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>401</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3354</xmin>
                        			<ymin>2106</ymin>
                        			<xmax>3646</xmax>
                        			<ymax>2351</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>403</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3063</xmin>
                        			<ymin>2102</ymin>
                        			<xmax>3348</xmax>
                        			<ymax>2351</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>405</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2761</xmin>
                        			<ymin>2104</ymin>
                        			<xmax>3058</xmax>
                        			<ymax>2347</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>407</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2562</xmin>
                        			<ymin>2101</ymin>
                        			<xmax>2758</xmax>
                        			<ymax>2349</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>402</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2963</xmin>
                        			<ymin>1810</ymin>
                        			<xmax>3262</xmax>
                        			<ymax>2021</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>404</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2766</xmin>
                        			<ymin>1812</ymin>
                        			<xmax>2960</xmax>
                        			<ymax>2021</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>406</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2569</xmin>
                        			<ymin>1810</ymin>
                        			<xmax>2761</xmax>
                        			<ymax>2020</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>411</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2085</xmin>
                        			<ymin>2099</ymin>
                        			<xmax>2271</xmax>
                        			<ymax>2303</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>413</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1898</xmin>
                        			<ymin>2101</ymin>
                        			<xmax>2084</xmax>
                        			<ymax>2303</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>415</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1721</xmin>
                        			<ymin>2098</ymin>
                        			<xmax>1896</xmax>
                        			<ymax>2302</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>Exit</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3558</xmin>
                        			<ymin>662</ymin>
                        			<xmax>3664</xmax>
                        			<ymax>917</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>426</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2777</xmin>
                        			<ymin>665</ymin>
                        			<xmax>2968</xmax>
                        			<ymax>982</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>428</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>2970</xmin>
                        			<ymin>666</ymin>
                        			<xmax>3269</xmax>
                        			<ymax>979</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>430</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>3270</xmin>
                        			<ymin>663</ymin>
                        			<xmax>3555</xmax>
                        			<ymax>975</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>425</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>496</xmin>
                        			<ymin>2102</ymin>
                        			<xmax>680</xmax>
                        			<ymax>2347</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>423</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>680</xmin>
                        			<ymin>2102</ymin>
                        			<xmax>958</xmax>
                        			<ymax>2349</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>421</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>952</xmin>
                        			<ymin>2103</ymin>
                        			<xmax>1244</xmax>
                        			<ymax>2349</ymax>
                        		</bndbox>
                        	</object>
                        	<object>
                        		<name>419</name>
                        		<pose>Unspecified</pose>
                        		<truncated>0</truncated>
                        		<difficult>0</difficult>
                        		<bndbox>
                        			<xmin>1239</xmin>
                        			<ymin>2102</ymin>
                        			<xmax>1432</xmax>
                        			<ymax>2344</ymax>
                        		</bndbox>
                        	</object>
                        </annotation>
                """.trimIndent())!!
                }

                val selectedRooms = remember { mutableStateSetOf<String>() }

                DividerTextExpandedWith("楼层地图测试") {
                    RowHorizontal {
                        NoPadding {
                            FlowRow(
                                modifier = Modifier
                                    .padding(horizontal = APP_HORIZONTAL_DP)
                                    .padding(bottom = APP_HORIZONTAL_DP - CARD_NORMAL_DP * 3),
                            ) {
                                val rooms = buildings.rooms.map { it.id }.distinct()
                                rooms.forEach {
                                    val selected = selectedRooms.contains(it)
                                    FilterChip(
                                        selected = selected,
                                        onClick = {
                                            if(selected) {
                                                selectedRooms.remove(it)
                                            } else {
                                                selectedRooms.add(it)
                                            }
                                        },
                                        label = { Text(it) },
                                        modifier = Modifier
                                            .padding(end = CARD_NORMAL_DP * 3)
                                            .padding(bottom = CARD_NORMAL_DP * 3)
                                    )
                                }
                            }
                        }
                    }

                    Box() {
                        Image(
                            painterResource(R.drawable.jpg),
                            contentDescription = "",
                            contentScale = ContentScale.FillWidth
                        )

                        RoomMap(buildings, selectedIds = selectedRooms)
                    }
                }


 */


                InnerPaddingHeight(innerPadding,false)
            }
        }
    }
}