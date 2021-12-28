package br.com.luanadev.calculacompra.util

import android.app.Application
import androidx.viewbinding.BuildConfig
import timber.log.Timber

class CompraAiApplicationclass : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}