package com.example.studentcheck

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.view.View
import android.widget.Toast
import com.github.ajalt.timberkt.Timber
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class LoginActivity : AppCompatActivity() {
    private lateinit var service : StudentCheckService

    val PERMISSION_ALL = 1
    val PERMISSIONS = arrayOf(
        android.Manifest.permission.READ_PHONE_STATE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initTimber()
        service = provideService(provideRetrofit(provideGson(),provideHttpClientBuilder(provideHttpLoggingInterceptor(),this).build()))
        loginButton.setOnClickListener {

            displayLoader()
            val studentNumber = studentIdEditText.text.toString()
            val password = passwordEditText.text.toString()
            val user = StudentSignin(studentNumber, password)
            service.studentSignin(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy (
                    onError = {
                        hideLoader()
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    },
                    onSuccess = {
                        hideLoader()
                        if(it.error == null) {
                            val barcode = it.barCode
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("barcode",barcode)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                )

        }
    }



    fun displayLoader() {
        progressBar.visibility = View.VISIBLE
    }

    fun hideLoader() {
        progressBar.visibility = View.INVISIBLE
    }


    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            Timber.tag("network").v(message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    fun provideHttpClientBuilder(loggingInterceptor: HttpLoggingInterceptor, context: Context): OkHttpClient.Builder {

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


    fun initTimber() {
        Timber.uprootAll()
        Timber.plant(Timber.DebugTree())
    }

}
