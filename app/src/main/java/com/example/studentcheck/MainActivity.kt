package com.example.studentcheck

import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var clientBuilder: OkHttpClient.Builder
    lateinit var retrofit: Retrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_main)


        clientBuilder = provideHttpClientBuilder(provideHttpLoggingInterceptor())
        retrofit = provideRetrofit(provideGson(), clientBuilder.build())

        val strBarcode = intent.getStringExtra("barcode")

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(strBarcode, BarcodeFormat.CODE_128, imageView.width,200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            imageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        myTimer().start()

    }

    fun myTimer() : CountDownTimer {
        return object : CountDownTimer(30000, 30000){
            override fun onFinish() {
                finish()
            }

            override fun onTick(millisUntilFinished: Long) {

            }

        }
    }


    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Timber.tag("network").v(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    fun provideHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder {

        return OkHttpClient.Builder()
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addNetworkInterceptor(loggingInterceptor)
    }

    fun provideGson(): Gson =
        GsonBuilder()
            .create()

    fun provideService(retrofit: Retrofit): StudentCheckService =
        retrofit.create(StudentCheckService::class.java)

    fun provideRetrofit(gson: Gson, client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(client)
            .build()

}
