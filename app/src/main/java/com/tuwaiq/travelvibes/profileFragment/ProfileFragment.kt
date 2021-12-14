package com.tuwaiq.travelvibes.profileFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tuwaiq.travelvibes.data.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class ProfileFragment : Fragment() {

    private val personCollectionRef = Firebase.firestore.collection("persons")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun savePerson(person: User) = CoroutineScope(Dispatchers.IO).launch {
        try {

            personCollectionRef.add(person).await()

        }catch (e:Exception){
            withContext(Dispatchers.Main){
                //Toast.makeText(this, e.message,Toast.LENGTH_LONG).show()
            }
        }
    }




}