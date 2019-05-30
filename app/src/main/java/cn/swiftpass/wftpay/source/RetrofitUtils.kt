package com.dome.mylibrary.data

import com.pay.dome.source.BaseInfo
import com.pay.dome.source.HttpInfo
import com.pay.dome.source.Query
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitUtils : Service {
    override fun refuned(resp: RequestBody): Observable<HttpInfo<List<Query>>> {
        return mService.refuned( resp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun tk(url: String, resp: RequestBody): Observable<BaseInfo> {
        return mService.tk(url, resp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())     }


    override fun query(resp: RequestBody): Observable<HttpInfo<List<Query>>> {
        return mService.query(resp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun upDataData(resp: RequestBody): Observable<BaseInfo> {
        return mService.upDataData(resp)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private lateinit var mService: Service

    companion object {
        fun getInstance(): RetrofitUtils {
            return INSTANCE.retrofitUtils
        }
    }

    object INSTANCE {
        val retrofitUtils = RetrofitUtils()
    }

    init {
        mService = buildService()
    }

    private fun buildService(): Service {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(3000, TimeUnit.SECONDS)
                .writeTimeout(3000, TimeUnit.SECONDS)
                .readTimeout(3000, TimeUnit.SECONDS)
                .build()
        var retrofit = Retrofit.Builder().run {
            baseUrl("https://pay.9617777.com/")
            addConverterFactory(GsonConverterFactory.create())
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            client(okHttpClient)
            build()
        }
        return retrofit.create(Service::class.java)
    }


}