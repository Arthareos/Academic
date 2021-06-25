package com.simonedifonzo.academic.classes

import java.io.Serializable
import java.util.*

class User : Serializable {
    var first                               = "null"
    var last                                = "null"
    var email                               = "null"
    var profilePic                          = "null"
    var starredCourses: ArrayList<Course>   = ArrayList()
    var specialization: Specialization      = Specialization()
    var lastChange: String                  = "null"

}