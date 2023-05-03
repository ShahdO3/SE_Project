package com.example.thelingo_projectshahdosman

import android.net.Uri
import java.util.*

class RegisteredClassesInfo() {
    var tutorsData: TutorsData? = null
    var langChosen: String? = null
    var dayChosen: String? = null
    var hourChosen: Int? = null
    var id:String? = null

    constructor(tutorsData: TutorsData,
                langChosen: String,
                dayChosen: String,
                hourChosen: Int) : this() {
        this.tutorsData = tutorsData
        this.langChosen = langChosen
        this.dayChosen = dayChosen
        this.hourChosen = hourChosen
        this.id = UUID.randomUUID().toString()

    }
}
