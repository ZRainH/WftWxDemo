package com.pay.dome.paylist

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import cn.swiftpass.wftpay.MapUtils
import cn.swiftpass.wftpay.R
import com.dome.mylibrary.data.RetrofitUtils
import com.google.gson.Gson
import com.pay.dome.JsonObjectUtils
import com.pay.dome.TimeUtils
import com.pay.dome.source.BaseInfo
import com.pay.dome.source.HttpInfo
import com.pay.dome.source.Query
import com.soonchina.pay.bean.Order
import com.soonchina.pay.ui.cashier.CashierActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_pay_list.*
import okhttp3.RequestBody

class PayListActivity : AppCompatActivity() {

    private val tk_url = "http://47.93.5.28/pay/refund/gateway"

    private lateinit var queryBean: MutableList<Query>

    private val gson: Gson by lazy {
        Gson()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pay_list)

    }

    private inner class MyRecyclerAdapter : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {

            return ViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.layout, p0, false))
        }

        override fun getItemCount(): Int = queryBean.size

        override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
            var orderInfo = gson.fromJson<Order>(queryBean[p1].msg, Order::class.java)
            p0.tv_order_name.text = "订单号：${queryBean[p1].outorderid}"

            when (queryBean[p1].status) {
                "0" -> {
                    //未付款
                    p0.btn_status.text = "未付款"
                    p0.btn_status.setTextColor(ContextCompat.getColor(this@PayListActivity,android.R.color.holo_red_dark))
                    p0.btn_click.text = "去付款"
                    p0.btn_click.visibility = View.VISIBLE
                }
                "1" -> {
                    //已付款
                    p0.btn_status.text = "已付款"
                    p0.btn_status.setTextColor(ContextCompat.getColor(this@PayListActivity,android.R.color.holo_green_dark))
                    p0.btn_click.text = "申请退款"
                    p0.btn_click.visibility = View.VISIBLE
                }
                "2" -> {
                    //已全额退款
                    p0.btn_status.text = "已全额退款"
                    p0.btn_status.setTextColor(ContextCompat.getColor(this@PayListActivity,android.R.color.darker_gray))
                    p0.btn_click.visibility = View.GONE
                }
                "3" -> {
                    //部分退款
                    p0.btn_status.text = "已部分退款"
                    p0.btn_status.setTextColor(ContextCompat.getColor(this@PayListActivity,android.R.color.holo_orange_dark))
                    p0.btn_click.text = "申请退款"
                    p0.btn_click.visibility = View.VISIBLE
                }

            }

            p0.btn_click.setOnClickListener {

                when (queryBean[p1].status) {
                    "0" -> {
                        //未付款
                        CashierActivity.startCashierActivity(this@PayListActivity, orderInfo)
                    }
                    "1" -> {
                        //已付款
                        var intent = Intent(this@PayListActivity, OrderInfoActivity::class.java)
                        intent.putExtra("orderInfo", queryBean[p1])
                        intent.putExtra("status",0)
                        startActivity(intent)
                    }
                    "3" -> {
                        //部分退款

                        var intent = Intent(this@PayListActivity, OrderInfoActivity::class.java)
                        intent.run {
                            this.putExtra("orderInfo", queryBean[p1])
                            this.putExtra("status",1)
                        }
                        startActivity(intent)
                    }

                }
            }
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_order_name: TextView = itemView.findViewById<TextView>(R.id.tv_order_name)
            var btn_status: TextView = itemView.findViewById<TextView>(R.id.btn_status)
            var btn_click = itemView.findViewById<Button>(R.id.btn_click)
        }

    }

    override fun onResume() {
        super.onResume()
        query()
    }

    private fun tk(order: Order) {
        progressBar2.visibility = View.VISIBLE
        var map = mutableMapOf<String, String>()
        map["appId"] = order.appId
        map["requestTime"] = order.requestTime
        map["signType"] = order.signType
        map["version"] = order.version
        map["outOrderId"] = order.orderId
        map["outOrderAmount"] = order.orderAmount
        map["transactionId"] = ""
        map["refundFee"] = order.totalFee
        map["orderList"] = order.orderList
        map["callbackUrl"] = order.callbackUrl
        map["outRefundNo"] = System.currentTimeMillis().toString()
        map["outRefundTime"] = TimeUtils.time
        map["service"] = "app_upay"

        var signMap = mutableMapOf<String, String>()
        signMap["appId"] = order.appId
        signMap["requestTime"] = order.requestTime
        signMap["signType"] = order.signType
        signMap["version"] = order.version
        signMap["sign"] = MapUtils.getSign(map, order.appId)
        signMap["outOrderId"] = order.orderId
        signMap["outOrderAmount"] = order.orderAmount
        signMap["transactionId"] = ""
        signMap["refundFee"] = order.totalFee
        signMap["orderList"] = order.orderList
        signMap["callbackUrl"] = order.callbackUrl
        signMap["outRefundNo"] = System.currentTimeMillis().toString()
        signMap["outRefundTime"] = TimeUtils.time
        signMap["service"] = "app_upay"

        var json = JsonObjectUtils.getJsonObject(gson.toJson(signMap))!!.toString()

        val requestBody =
            RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), json)

        RetrofitUtils.getInstance().tk(tk_url, requestBody)
            .subscribe(object : Observer<BaseInfo> {
                override fun onComplete() {
                    progressBar2.visibility = View.GONE
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: BaseInfo) {
                    query()
                }

                override fun onError(e: Throwable) {
                }

            })
    }

    private fun query() {
        progressBar2.visibility = View.VISIBLE
        var queryData = mutableMapOf<String, String>()
        queryData["customid"] = "02"

        var mapData = mutableMapOf<String, String>()
        mapData["data"] = Gson().toJson(queryData)

        val requestBody =
            RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), Gson().toJson(mapData))

        RetrofitUtils.getInstance().query(requestBody)
            .subscribe(object : Observer<HttpInfo<List<Query>>> {
                override fun onComplete() {
                    progressBar2.visibility = View.GONE
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: HttpInfo<List<Query>>) {
                    queryData.clear()
                    queryBean = t.data.toMutableList()
                    if (queryBean.size == 0 || !t.status.equals("1")) {
                        Toast.makeText(this@PayListActivity, "没有数据", Toast.LENGTH_SHORT).show()
                    }
                    recycle_view.run {
                        layoutManager = LinearLayoutManager(this@PayListActivity)
                        adapter = MyRecyclerAdapter()
                    }

                }

                override fun onError(e: Throwable) {
                }
            })
    }
}