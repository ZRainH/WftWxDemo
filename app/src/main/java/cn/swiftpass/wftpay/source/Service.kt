package com.dome.mylibrary.data

import com.pay.dome.source.BaseInfo
import com.pay.dome.source.HttpInfo
import com.pay.dome.source.Query
import io.reactivex.Observable
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface Service {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("Pays/PApi/data_pay")
    fun upDataData(
        @Body resp: RequestBody
    ): Observable<BaseInfo>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("Pays/PApi/data_query")
    fun query(
        @Body resp: RequestBody
    ): Observable<HttpInfo<List<Query>>>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST
    fun tk(@Url url : String,@Body resp: RequestBody) : Observable<BaseInfo>

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("Pays/PApi/data_refund")
    fun refuned( @Body resp: RequestBody
    ): Observable<HttpInfo<List<Query>>>
}