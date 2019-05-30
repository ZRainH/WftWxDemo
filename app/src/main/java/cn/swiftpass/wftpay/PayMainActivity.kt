package cn.swiftpass.wftpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.dome.mylibrary.data.RetrofitUtils
import com.google.gson.Gson
import com.pay.dome.paylist.PayListActivity
import com.pay.dome.source.BaseInfo
import com.soonchina.pay.bean.Order
import com.soonchina.pay.em.LimitPay
import com.soonchina.pay.em.SignType
import com.soonchina.pay.ui.cashier.CashierActivity
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.pay_main.*
import okhttp3.RequestBody


class PayMainActivity : Activity() {

    private var limitPay: LimitPay? = null

    private val list1 = mutableListOf("请选择", "4474", "4475")

    private val list2 = mutableListOf("请选择", "4450", "4458")

    private var pp: String = ""

    private var dp: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_main)

        rg!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_ali -> limitPay = LimitPay.NO_ALI
                R.id.rb_tb -> limitPay = LimitPay.NO_JYCOIN
                R.id.rb_tbj -> limitPay = LimitPay.NO_TBJ
                R.id.rb_union -> limitPay = LimitPay.NO_UNION
                R.id.rb_wechat -> limitPay = LimitPay.NO_WX
                R.id.rb_yu -> limitPay = LimitPay.NO_BALANCE
                R.id.rb_no -> limitPay = null
            }
        }
        var s = resources.getStringArray(R.array.spingarr)
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                pp = s[position]
                dp = ""
                a()
            }

        }
    }

    fun a() {
        //适配器 )
        var arr_adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                if (TextUtils.equals("rA22582ddk", pp)) (list2) else (list1))
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //加载适配器
        spinner3.adapter = arr_adapter
        spinner3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dp = if (TextUtils.equals("rA22582ddk", pp)) {
                    list2[position]

                } else {
                    list1[position]
                }
            }

        }
    }

    fun play(view: View) {
        startActivity(Intent(this@PayMainActivity, PayListActivity::class.java))
    }

    fun sc(view: View) {
        et_ordernum!!.setText("${System.currentTimeMillis()}")
    }

    fun pay(view: View) {
        val s = et_ordernum!!.text.toString()
        if (TextUtils.isEmpty(et_orderamount!!.text.toString())) {
            Toast.makeText(this, "请输入金额", Toast.LENGTH_SHORT).show()
            return
        }
        val aDouble = java.lang.Double.parseDouble(et_orderamount!!.text.toString())

        val time = TimeUtils.getOrderAndExpireTime()
        val order = Order.Builder()
                .setStoreId(dp)
                .setAppId(pp)
                .setLimitPay(limitPay)
                .setVersion("1.0")
                .setRequestTime(TimeUtils.getTime())
                .setSignType(SignType.MD5)
                .setOrderId(s)
                .setOrderTime(time[0])
                .setTimeExpire(time[1])
                .setOrderAmount(aDouble)
                .setTotalFee(aDouble)
                .setCallbackUrl("https://192.168.10.24/Pays/PApi/data_callback")
                .setCustomid("MDA2MDQ5NDcO0O0O")
                .setOrderList("[{\"outOrderNo\":\"sw20190417001013222\",\"outOrderAmount\":${aDouble / 2},\"storeId\":4450},{\"outOrderNo\":\"sw20190417001013332\",\"outOrderAmount\":${aDouble / 2},\"storeId\":4458}]")
                .setOrderName(et_ordername!!.text.toString())
                .setBody("商品BODY")
                .setAttach("附加数据，在查询接口和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据")
                .setMchCreateIp("127.0.0.1")
                .setSubject("支付宝使用")
                .build()

        var map = mutableMapOf<String, String>()
        map["orderInfo"] = Gson().toJson(order)
        map["customid"] = "02"
        map["orderList"] = order.orderList
        map["outOrderId"] = s

        var mapData = mutableMapOf<String, String>()
        mapData["data"] = Gson().toJson(map)

        val requestBody =
                RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), Gson().toJson(mapData))

        RetrofitUtils.getInstance().upDataData(requestBody)
                .subscribe(object : Observer<BaseInfo> {
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: BaseInfo) {
                        Toast.makeText(this@PayMainActivity, t.toString(), Toast.LENGTH_SHORT).show()
                        CashierActivity.startCashierActivity(this@PayMainActivity, order)
                    }

                    override fun onError(e: Throwable) {
                    }
                })


    }

}
