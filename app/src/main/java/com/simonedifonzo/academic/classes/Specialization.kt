package com.simonedifonzo.academic.classes

import java.io.Serializable

class Specialization : Serializable {
    var university = "null"
    var faculty = "null"
    var year = "null"

    constructor() {
        this.university = "null"
        this.faculty = "null"
        this.year = "null"
    }

    constructor(university: String, faculty: String, year: String) {
        this.university = university
        this.faculty = faculty
        this.year = year
    }

    companion object {
        fun createSpecialization(remoteSpecialization: String): Specialization {
            val result = remoteSpecialization.split(" ").toTypedArray()
            return Specialization(result[0], result[1], result[2])
        }
    }
}