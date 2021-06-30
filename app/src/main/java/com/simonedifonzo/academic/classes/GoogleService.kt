package com.simonedifonzo.academic.classes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class GoogleService {
    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    var storage: StorageReference? = FirebaseStorage.getInstance().reference
}