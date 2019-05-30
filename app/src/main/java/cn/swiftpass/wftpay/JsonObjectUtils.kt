package com.pay.dome

import android.text.TextUtils
import android.util.Log
import cn.swiftpass.wftpay.EncryptionUtils
import com.switfpass.pay.utils.MD5
import org.json.JSONObject

import java.util.HashMap

object JsonObjectUtils {
    var DES_KEY = "GDgLwwdK270Qj1w4xho8lyTp"
    fun getJsonObject(json: String): JSONObject? {
        var encrypt3DESJson: String?
        try {
            val jsonObject = JSONObject(json)
            val appId = jsonObject.getString("appId")
            if (TextUtils.isEmpty(appId)) {
                encrypt3DESJson = EncryptionUtils.encrypt3DES(json.toByteArray(), DES_KEY.toByteArray())
            } else {
                encrypt3DESJson =
                    EncryptionUtils.encrypt3DES(json.toByteArray(), "a31f4e78161be73177185f16".toByteArray())
                Log.e("appId", appId + "---" + MD5.md5s(appId).substring(0, 24))
            }
            val orderMap = HashMap<String, String>()
            orderMap["data"] = encrypt3DESJson
            return JSONObject(orderMap)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
