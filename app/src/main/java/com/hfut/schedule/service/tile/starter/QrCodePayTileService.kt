package com.hfut.schedule.service.tile.starter

import com.hfut.schedule.R
import com.hfut.schedule.logic.util.storage.kv.SharedPrefs.prefs
import com.hfut.schedule.network.api.model.Constant
import com.hfut.schedule.service.tile.base.BaseWebViewTileService

class QrCodePayTileService : BaseWebViewTileService(
    url = Constant.HUI_XIN_URL + "plat/pay" + "?synjones-auth=" + prefs.getString("auth",""),
    title = "付款码",
    icon = R.drawable.barcode
)