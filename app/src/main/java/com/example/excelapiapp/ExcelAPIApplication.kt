package com.example.excelapiapp

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class ExcelAPIApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}