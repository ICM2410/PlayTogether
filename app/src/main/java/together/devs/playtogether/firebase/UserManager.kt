package together.devs.playtogether.firebase

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import together.devs.playtogether.model.User

class UserManager(private val database: FirebaseDatabase) {

    private val userReference: DatabaseReference = database.reference.child("users")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getUser(userId: String, callback: (User?) -> Unit) {
        userReference.child(userId).get().addOnSuccessListener { dataSnapshot ->
            val user = dataSnapshot.getValue(User::class.java)
            callback(user)
        }.addOnFailureListener { exception ->
            Log.e(ContentValues.TAG, "Error getting user data: ${exception.message}")
            callback(null)
        }
    }

    fun toggleUserAvailability() {
        val user = auth.currentUser
        user?.let { currentUser ->
            val userReference = userReference.child(currentUser.uid).child("available")
            userReference.get().addOnSuccessListener { dataSnapshot ->
                val currentAvailability = dataSnapshot.getValue(Boolean::class.java) ?: false
                val newAvailability = !currentAvailability
                userReference.setValue(newAvailability).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(
                            ContentValues.TAG,
                            "Availability updated to $newAvailability for user ${currentUser.uid}"
                        )
                    } else {
                        Log.e(
                            ContentValues.TAG,
                            "Failed to update availability for user ${currentUser.uid}: ${task.exception?.message}"
                        )
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting user availability: ${exception.message}")
            }
        } ?: Log.e(ContentValues.TAG, "No user logged in")
    }

    fun saveUser(userId: String, user: User) {
        userReference.child(userId).setValue(user).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User data saved successfully")
            } else {
                Log.e(ContentValues.TAG, "Failed to save user data: ${task.exception?.message}")
            }
        }
    }

    fun updateUserProfile(userId: String, imageUrl: String, userName: String) {
        val updateMap = mapOf(
            "profileImageUrl" to imageUrl,
            "userName" to userName
        )
        userReference.child(userId).updateChildren(updateMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(ContentValues.TAG, "User profile updated successfully")
            } else {
                Log.e(ContentValues.TAG, "Failed to update user profile: ${task.exception?.message}")
            }
        }
    }

    fun uploadProfileImage(userId: String, imageUri: Uri, userName: String) {
        val storageRef = FirebaseStorage.getInstance().getReference("profile_images/$userId.jpg")
        storageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                updateUserProfile(userId, downloadUri.toString(), userName)
            }.addOnFailureListener { e ->
                Log.e(ContentValues.TAG, "Getting download URL failed: ${e.message}")
            }
        }.addOnFailureListener { e ->
            Log.e(ContentValues.TAG, "Uploading profile image failed: ${e.message}")
        }
    }
}
