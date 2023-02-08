package com.example.myapplication
import io.realm.RealmObject

open class Memo(
    open var name: String = ""
) : RealmObject()