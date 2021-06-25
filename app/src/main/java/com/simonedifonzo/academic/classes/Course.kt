package com.simonedifonzo.academic.classes

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import java.io.Serializable

class Course : Serializable {
    var id          = "null"
    var name        = "null"
    var professor   = "null"
    var semester    = "null"

    companion object {
        fun generateCourse(service: GoogleService, courseCode: String): Course {
            var course = Course()

            val result = courseCode.split(" ").toTypedArray()

            val documentReference: DocumentReference =
                service.firestore.collection("universities")
                    .document(result[0])
                    .collection("faculties")
                    .document(result[1])
                    .collection(result[2])
                    .document(result[3])

            documentReference.get().addOnCompleteListener { task: Task<DocumentSnapshot?> ->
                val document = task.result
                if (task.isSuccessful && document != null) {
                    course.id           = result[3]
                    course.name         = document.getString("name").toString()
                    course.professor    = document.getString("professor").toString()
                    course.semester     = document.getString("semester").toString()

//                    Log.e(TAG, "generateCourse: " + course.id)
//                    Log.e(TAG, "generateCourse: " + course.name)
//                    Log.e(TAG, "generateCourse: " + course.professor)
//                    Log.e(TAG, "generateCourse: " + course.semester)
                }
            }

            return course
        }
    }
}