package com.hfut.schedule.logic.util.shortcut

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import androidx.core.net.toUri
import com.hfut.schedule.BuildConfig
import com.hfut.schedule.R
import com.hfut.schedule.activity.MainActivity
import com.hfut.schedule.logic.util.sys.Starter
import com.hfut.schedule.network.util.Constant
import com.hfut.schedule.ui.nav.destination.ScanQrCodeDestination
import com.hfut.schedule.ui.nav.destination.SchoolCardDestination
import com.xah.common.ui.model.text.UiText
import com.xah.common.ui.util.text
import com.xah.common.logic.util.LogUtil

/**
 * 桌面长按图标快捷菜单
 * 【修复】作为第一个项目并常驻，配置在shortcut.xml中，其余三个可自由定制
 */
object AppShortcutManager {

    data class ShortcutConfig(
        val id : String,
        val label : UiText,
        val icon : Int,
        val intent : Intent,
        val longLabel : UiText = label,
    )

    private fun ShortcutConfig.createShortcut(context: Context) : ShortcutInfo {
        return ShortcutInfo.Builder(context, "${this.id}_shortcut")
            .setShortLabel(this.label.asString(context))
            .setLongLabel(this.longLabel.asString(context))
            .setIcon(Icon.createWithResource(context, this.icon))
            .setIntent(this.intent)
            .build()
    }

    private fun createCardShortcut(): ShortcutConfig {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(
                BuildConfig.APPLICATION_ID,
                MainActivity::class.java.name
            )
            putExtra("route", SchoolCardDestination::class.java.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return ShortcutConfig(
            "card",
            text("校园卡"),
            R.drawable.credit_card,
            intent
        )
    }

    private fun createScanShortcut(): ShortcutConfig {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setClassName(
                BuildConfig.APPLICATION_ID,
                MainActivity::class.java.name
            )
            putExtra("route", ScanQrCodeDestination::class.java.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return ShortcutConfig(
            "scan",
            text("CAS扫码"),
            R.drawable.qr_code_scanner,
            intent
        )
    }

    private fun buildAppUrlIntent(url: String): Intent {
        return Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun createHotWaterShortcut(): ShortcutConfig {
        val intent = buildAppUrlIntent(Constant.ALIPAY_HOT_WATER_URL)
        return ShortcutConfig(
            "hot_water",
            text("热水"),
            R.drawable.water_voc,
            intent,
        )
    }

    private fun createRechargeShortcut(): ShortcutConfig {
        val intent = buildAppUrlIntent(Constant.ALIPAY_CARD_URL)
        return ShortcutConfig(
            "recharge",
            text("校园卡充值"),
            R.drawable.add_card,
            intent,
        )
    }

    private fun createExpressPddShortcut(): ShortcutConfig {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            component = ComponentName(
                Starter.AppPackages.PDD.packageName,
                "${Starter.AppPackages.PDD.packageName}.ui.activity.MainFrameActivity"
            )
            data = Constant.PDD_PACKAGE_URL.toUri()
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return ShortcutConfig(
            "express_pdd",
            text("快递"),
            R.drawable.pdd_icon,
            intent,
            text("快递(拼多多)")
        )
    }

    private fun createExpressTaoBaoShortcut(): ShortcutConfig {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Constant.TAO_BAO_PACKAGE_ID_URL.toUri()
            setPackage(Starter.AppPackages.TAO_BAO.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return ShortcutConfig(
            "express_taobao",
            text("快递"),
            R.drawable.taobao_icon,
            intent,
            text("快递(淘宝)")
        )
    }

    private val shortcuts = listOf(
        createScanShortcut(),
        createCardShortcut(),
        createHotWaterShortcut(),
        createRechargeShortcut(),
        createExpressPddShortcut(),
        createExpressTaoBaoShortcut(),
    )

    fun getStorageStr() = shortcuts.joinToString(",") { it.id }

    // 按字符串排序
    private fun MutableList<ShortcutConfig>.reorderByIdsStr(idOrder: String): MutableList<ShortcutConfig> {
        return try {
            val order = idOrder.split(",").map { it.trim() }
            reorderByIds(order)
        } catch (e: Exception) {
            LogUtil.error(e)
            // 出错时恢复默认顺序
            shortcuts.toMutableList()
        }
    }

    // 按 List<Int> 排序，并把未出现的新元素追加到末尾
    private fun MutableList<ShortcutConfig>.reorderByIds(idOrder: List<String>): MutableList<ShortcutConfig> {
        val map = this.associateBy { it.id }

        // 按顺序取出原有元素
        val sorted = idOrder.mapNotNull { map[it] }.toMutableList()

        // 追加未在 idOrder 中的新元素
        val remaining = this.filter { it.id !in idOrder }
        sorted.addAll(remaining)

        this.clear()
        this.addAll(sorted)
        return this
    }

    fun getFinalList(customSort : String? = null) : List<ShortcutConfig> {
        val list = customSort?.let {
            shortcuts.toMutableList().reorderByIdsStr(it)
        } ?: shortcuts
        return list
    }

    const val MAX_SIZE = 3

    fun init(context : Context,customSort : String? = null) {
        // 只能显示前3个
        try {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            val list = getFinalList(customSort).take(MAX_SIZE)
            LogUtil.debug("shortcuts = [${list.joinToString(",") { it.id }}]")
            shortcutManager?.dynamicShortcuts = list.map { it.createShortcut(context) }
        } catch (e : Exception) {
            LogUtil.error(e)
        }
    }
}