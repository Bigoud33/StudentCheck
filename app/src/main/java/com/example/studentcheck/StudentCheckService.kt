package com.example.studentcheck

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StudentCheckService {
    @POST("barcode/")
    fun getBarcode(@Body userId: UserId):
            Single<BarCode>

    @POST("studentsignin")
    fun studentSignin(@Body studentSignin: StudentSignin):
            Single<StudentSigninResponse>
}
