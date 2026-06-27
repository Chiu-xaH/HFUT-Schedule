package com.hfut.schedule.logic.network.util

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.xah.common.logic.util.LogUtil

/**
 * Classifies electric fee API responses as business success or failure.
 * Uses Gson for proper JSON parsing instead of fragile regex matching.
 *
 * The HuiXin electric fee API returns JSON with a "map" object containing "showData"
 * on success. Some responses also include a "msg":"success" field or "success":true.
 */
object ElectricFeeResponseClassifier {

    /**
     * Determines if the raw response body represents a successful business response.
     *
     * Classification priority:
     * 1. "success":false → explicit failure (returns false)
     * 2. "msg":"success" (case insensitive) → success
     * 3. "success":true → success
     * 4. "map":{"showData":{...}} exists → success
     * 5. JSON parse failure → false
     *
     * @return true if the response indicates business success, false otherwise
     */
    fun isBusinessSuccess(rawBody: String?): Boolean {
        if (rawBody.isNullOrBlank()) return false
        return try {
            val rootElement = JsonParser().parse(rawBody)
            if (!rootElement.isJsonObject) return false
            classify(rootElement.asJsonObject)
        } catch (e: Exception) {
            LogUtil.error(e, "解析电费业务响应失败")
            false
        }
    }

    private fun classify(root: JsonObject): Boolean {
        val successElement = root.get("success")
        val msgElement = root.get("msg")

        // If "success" field exists, it must be a boolean primitive
        if (successElement != null) {
            if (!successElement.isJsonPrimitive ||
                !successElement.asJsonPrimitive.isBoolean
            ) {
                // success field has wrong type (number, string, null, etc.) → failure
                return false
            }
            if (!successElement.asBoolean) {
                return false
            }
        }

        // If "msg" field exists, it must be a string primitive
        if (msgElement != null) {
            if (!msgElement.isJsonPrimitive ||
                !msgElement.asJsonPrimitive.isString
            ) {
                // msg field has wrong type (number, null, etc.) → failure
                return false
            }
            return msgElement.asString.equals("success", ignoreCase = true)
        }

        // success:true (already validated as boolean above)
        if (successElement?.asBoolean == true) {
            return true
        }

        // Fallback: check for "map":{"showData":{...}} structure
        val mapElement = root.get("map")
        if (mapElement == null || !mapElement.isJsonObject) {
            return false
        }
        val map = mapElement.asJsonObject
        val showData = map.get("showData")
        return showData != null && showData.isJsonObject
    }
}
