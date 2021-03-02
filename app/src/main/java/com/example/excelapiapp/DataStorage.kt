package com.example.excelapiapp

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataStorage(
    @PrimaryKey
    var id: Long = 0,
    var sheetNameList: RealmList<String> = RealmList()

): RealmObject()