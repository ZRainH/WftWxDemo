package com.pay.dome.paylist

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import cn.swiftpass.wftpay.R
import com.dome.mylibrary.data.RetrofitUtils
import com.google.gson.Gson
import com.pay.dome.source.BaseInfo
import com.pay.dome.source.OrderList
import com.pay.dome.source.Query
import com.pay.dome.source.Refund
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_order_info.*
import okhttp3.RequestBody
import java.lang.StringBuilder

class OrderInfoActivity : AppCompatActivity() {

    private val mData: Query by lazy {
        intent.getParcelableExtra<Query>("orderInfo")
    }

    private val status: Int by lazy {
        intent.getIntExtra("status", -1)
    }

    private val refund: String? by lazy {
        mData.refundlist
    }

    private val mAdapter: MyRecyclerAdapters by lazy {
        MyRecyclerAdapters()
    }

    private val orderList: MutableList<OrderList> by lazy {
        Gson().fromJson<Array<OrderList>>(mData.orderlist, Array<OrderList>::class.java).toMutableList()
    }

    private val refundlist: List<String> by lazy {
        Gson().fromJson<Array<String>>(refund, Array<String>::class.java).toMutableList()
    }

    private val gson: Gson by lazy { Gson() }

    private val refundBeanList = mutableListOf<Refund>()

    private val refundOrderNum = mutableListOf<String>()

    private var refundOrderList = mutableListOf<OrderList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_info)
        if (status == 1)
            for (i in 0 until refundlist.size) {
                var refund = gson.fromJson<Refund>(refundlist[i], Refund::class.java)
                refundBeanList.add(refund)
            }

        for (i in 0 until refundBeanList.size) {
            refundOrderList = gson.fromJson<Array<OrderList>>(refundBeanList[i].orderList, Array<OrderList>::class.java)
                .toMutableList()
        }

        for (i in 0 until refundOrderList.size) {
            refundOrderNum.add(refundOrderList[i].outOrderNo)
        }


        recycle_view.run {
            layoutManager = LinearLayoutManager(this@OrderInfoActivity)
            adapter = mAdapter
        }


        fab.setOnClickListener {
            if (refundOrderNum.size > 0) {
                var data = mutableListOf<OrderList>()
                for (i in 0 until orderList.size) {
                    if (!refundOrderNum.contains(orderList[i].outOrderNo)) {
                        data.add(orderList[i])
                    }
                }
                refund("3", gson.toJson(data), -1)
            } else {
                refund("2", mData.orderlist, -1)
            }
        }
    }

    private inner class MyRecyclerAdapters : RecyclerView.Adapter<MyRecyclerAdapters.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

            return ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.layout_order, p0, false))
        }

        override fun getItemCount(): Int = orderList.size

        @SuppressLint("SetTextI18n", "Range")
        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            p0.tv_amount.text = "金额：${orderList[p1].outOrderAmount}"
            p0.tv_order_num.text = "订单号：${orderList[p1].outOrderNo}"
            if (orderList[p1].status == 0) {
                //申请退款
                if (refundOrderNum.contains(orderList[p1].outOrderNo)) {
                    //存在
                    p0.tv_request_refund.text = "已退款"
                    p0.tv_request_refund.isEnabled = false
                    p0.tv_request_refund.setTextColor(
                        ContextCompat.getColor(
                            this@OrderInfoActivity,
                            android.R.color.darker_gray
                        )
                    )
                } else {
                    p0.tv_request_refund.text = "申请退款"
                    p0.tv_request_refund.isEnabled = true
                    p0.tv_request_refund.setTextColor(Color.parseColor("#5c5cff"))
                }
            } else {
                // 申请退款成功
                if (refundOrderNum.contains(orderList[p1].outOrderNo)) {
                    //存在
                    p0.tv_request_refund.text = "已退款"
                } else {
                    p0.tv_request_refund.text = "申请退款成功"

                }

                p0.tv_request_refund.isEnabled = false
                p0.tv_request_refund.setTextColor(
                    ContextCompat.getColor(
                        this@OrderInfoActivity,
                        android.R.color.darker_gray
                    )
                )


            }
            p0.tv_request_refund.setOnClickListener {
                var list = mutableListOf<OrderList>()
                list.add(orderList[p1])
                refund("3", Gson().toJson(list), p1)
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_amount = itemView.findViewById<TextView>(R.id.tv_amount)
            var tv_order_num = itemView.findViewById<TextView>(R.id.tv_order_num)
            var tv_request_refund = itemView.findViewById<TextView>(R.id.tv_request_refund)
        }

    }

    fun refund(status: String, json: String, position: Int) {


        Toast.makeText(this@OrderInfoActivity, "正在申请退款中...", Toast.LENGTH_LONG).show()
        var map = mutableMapOf<String, String>()
        map["outorderid"] = mData.outorderid
        map["status"] = status
        map["orderlist"] = json

        var mapData = mutableMapOf<String, String>()
        mapData["data"] = Gson().toJson(map)

        val requestBody =
            RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), Gson().toJson(mapData))

        RetrofitUtils.getInstance().refuned(resp = requestBody)
            .subscribe(object : Observer<BaseInfo> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseInfo) {
                    var stringBuilder = StringBuilder()
                    stringBuilder.append("request:::\n$json\n\n\n")
                    stringBuilder.append("request:::\n${gson.toJson(t)}")
                    tv_content.text = stringBuilder.toString()
                    if (position == -1) {
                        for (i in 0 until orderList.size) {
                            orderList[i].status = 1
                        }
                    } else {
                        orderList[position].status = 1
                    }
                    mAdapter.notifyDataSetChanged()

                }

                override fun onError(e: Throwable) {
                    Toast.makeText(this@OrderInfoActivity, "申请退款失败", Toast.LENGTH_SHORT).show()
                }

            })
    }
}