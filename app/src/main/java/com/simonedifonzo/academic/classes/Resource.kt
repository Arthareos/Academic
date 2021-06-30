package com.simonedifonzo.academic.classes

import java.io.Serializable

class Resource : Serializable {
    var id = "null"
    var name = "null"
    var description = "null"
    var type = "null" //web, youtube, pdf, word
    var link = "null"
    var uploaderID = "null"
    var uploadedTime = "null"
}