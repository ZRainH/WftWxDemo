package com.pay.dome.source

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

open class BaseInfo {
    var status: String? = ""
    var codemsg: String? = ""
}

data class HttpInfo<T>(
    var data: T
) : BaseInfo()

@Parcelize
data class Query(
    var id: String,
    var userid: String,
    var orderlist: String,
    var msg: String,
    var refundlist : String?,
    var status: String,
    var outorderid: String
) : Parcelable

data class OrderList(
    var outOrderNo: String,
    var outOrderAmount: String,
    var storeId: String,
    var status: Int = 1
)

data class Refund(
        var orderList : String
) : BaseInfo()