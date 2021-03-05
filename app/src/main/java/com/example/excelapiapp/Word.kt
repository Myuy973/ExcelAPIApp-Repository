package com.example.excelapiapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Word: RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var sheetId: Long = 0
    var sheetIndex: Int = 0
    var vocabulary: String = ""
    var description: String = ""
}