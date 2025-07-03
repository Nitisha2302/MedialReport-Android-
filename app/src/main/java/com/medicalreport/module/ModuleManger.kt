package com.medicalreport.module

import android.content.ContextWrapper
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.medicalreport.BuildConfig
import com.medicalreport.R
import com.medicalreport.base.MainApplication
import com.medicalreport.datasource.auth.AuthDataSource
import com.medicalreport.datasource.auth.AuthDataSourceImpl
import com.medicalreport.datasource.home.HomeDataSource
import com.medicalreport.datasource.home.HomeDataSourceImpl
import com.medicalreport.network.ApiService
import com.medicalreport.repository.auth.AuthRepository
import com.medicalreport.repository.auth.AuthRepositoryImpl
import com.medicalreport.repository.home.HomeRepository
import com.medicalreport.repository.home.HomeRepositoryImpl
import com.medicalreport.resource.ResourcesProvider
import com.medicalreport.resource.ResourcesProviderImpl
import com.medicalreport.utils.Constants
import com.medicalreport.utils.Prefs
import com.medicalreport.utils.runWithDelay
import com.medicalreport.utils.showToast
import com.medicalreport.view.auth.AuthActivity
import com.medicalreport.viewmodel.AuthViewModel
import com.medicalreport.viewmodel.HomeViewModel
import com.medicalreport.viewmodel.PatientViewModel
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.apply
import kotlin.jvm.Throws
import kotlin.jvm.java
import kotlin.let


val repoModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }

}

val dataSourceModule = module {
    single<AuthDataSource> { AuthDataSourceImpl(get(), get()) }
    single<HomeDataSource> { HomeDataSourceImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { PatientViewModel(get()) }

}

val providerModule = module {
    single<ResourcesProvider> { ResourcesProviderImpl(get()) }
}

val apiModule = module {
    fun provideMyApi(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    single { provideMyApi(get()) }
}

val networkModule = module {
    fun provideGson(): Gson {
        return GsonBuilder().setLenient().create()
    }

    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return if (BuildConfig.DEBUG) {
            OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS) // 30 seconds
                .readTimeout(30, TimeUnit.SECONDS) // 30 seconds
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .addInterceptor(ForbiddenInterceptor())
                .build()
        } else {
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .addInterceptor(ForbiddenInterceptor())
                .build()
        }
    }


    fun provideRetrofit(factory: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(okHttpClient)
            .build()
    }

    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val accessToken: String = Prefs.init().currentToken
            if (!TextUtils.isEmpty(accessToken)) {
                val request: Request = chain.request().newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $accessToken").build()
                chain.proceed(request)
            } else {
                val request: Request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json").build()
                chain.proceed(request)
            }
        }
    }

    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Log.d("API Logging", "response => $message") }
        httpLoggingInterceptor.setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }

    single { provideHttpLoggingInterceptor() }
    single { provideHeaderInterceptor() }
    single { provideGson() }
    single { provideOkHttpClient(get(), get()) }
    single { provideRetrofit(get(), get()) }


}

//*ForbiddenInterceptor
class ForbiddenInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (response.code == 401 || response.code == 402) {
            MainApplication.get().apply {
                val context = applicationContext
                Prefs.init().apply {
                    currentToken = ""
                }

                context.startActivity(
                    Intent(context, AuthActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("navigate", "navigate")
                    })

                getCurrentActivity()?.finish()
                if (response.code == 401) {
                    runWithDelay(200) {
                        getCurrentActivity()?.let {
                            showToast(
                                context,
                                context.getString(R.string.account_logged_on_another_device)
                            )

                        }
                    }
                } else if (response.code == 402) {
                    runWithDelay(200) {
                        getCurrentActivity()?.let {
                            showToast(context, context.getString(R.string.account_block))

                        }
                    }
                }
            }
        }
        return response
    }
}
