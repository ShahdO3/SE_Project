package com.example.thelingo_projectshahdosman

class TutorsData():java.io.Serializable {
    var name:String? = null
    var biography:String? = null
    var image: String? = null
    var zoom:String? = null
    var availability: Availability? = null
    var languages: HashMap<String, String>? = null
    var rates: HashMap<String, Int>? = null
    var slots: HashMap<String, ArrayList<Int>>? = null
    var slotsReserved: List<String>? = null

    constructor(name:String,
                bio:String,
                image: String,
                zoomLink:String,
                availability: Availability,
                languages: HashMap<String, String>,
                rates:  HashMap<String, Int>,
                slots: HashMap<String, ArrayList<Int>>,
                slotsReserved: List<String>
    ) : this() {
        this.name = name
        this.biography = bio
        this.image = image
        this.zoom = zoomLink
        this.availability = availability
        this.languages = languages
        this.rates = rates
        this.slots = slots
        this.slotsReserved = slotsReserved
    }

    class Availability(
    ):java.io.Serializable {

        var day:  HashMap<String, String>? = null
        var duration: Int? = null
        var time: HashMap<String, Int>? = null

        constructor(
            days:  HashMap<String, String>,
            durationHrs: Int,
            time: HashMap<String, Int>
        ):this() {
            this.day = days
            this.time = time
            this.duration = durationHrs
        }
    }
}
