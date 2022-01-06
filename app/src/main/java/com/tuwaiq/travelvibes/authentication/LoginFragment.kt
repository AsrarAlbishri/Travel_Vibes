package com.tuwaiq.travelvibes.authentication

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tuwaiq.travelvibes.*
import com.tuwaiq.travelvibes.commentFragment.CommentFragmentArgs
import com.tuwaiq.travelvibes.data.Post

class LoginFragment : Fragment() {

    private lateinit var userNameET: EditText
    private lateinit var passwordET: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUp:TextView

    //private val args: LoginFragmentArgs by navArgs()

    private lateinit var post: Post


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        post = Post()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)

        userNameET = view.findViewById(R.id.username_et)
        passwordET = view.findViewById(R.id.password_et)
        loginBtn = view.findViewById(R.id.login_btn)
        signUp = view.findViewById(R.id.register_btn)



        return view
    }

    override fun onStart() {
        super.onStart()
         signUp.setOnClickListener {
            val navCon = findNavController()
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            navCon.navigate(action)
        }

        val (builder, nm) = generateNotification()





        loginBtn.setOnClickListener {
            RegisterFragment.auth.signInWithEmailAndPassword(
                userNameET.text.toString(),
                passwordET.text.toString()
            )
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("login successful")
                        if (builder != null) {
                            nm?.notify(0,builder)
                        }                    } else {
                        showToast("login failed")
                    }
                }

            val navCon = findNavController()
            val action = LoginFragmentDirections.actionLoginFragmentToPostListFragment(post.postId)
            navCon.navigate(action)

        }

    }


    private fun generateNotification(): Pair<Notification?, NotificationManagerCompat?> {

        val notificationChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)

        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        //val intent = Intent(context, MainActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // val pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT)

        val builder = context?.let {
            NotificationCompat.Builder(it, channelId)
        }

            ?.setContentTitle("Welcome to Travel Vibes")
            ?.setContentText("Where you can share your travel experience")
            ?.setSmallIcon(R.drawable.travel)
          //  ?.setSmallIcon(R.id.logo_notification,R.drawable.travel)
           // ?.setColor(0x169AB9)
            ?.setAutoCancel(true)
            ?.setPriority(NotificationCompat.PRIORITY_HIGH)
            ?.setVibrate(longArrayOf(5000, 5000, 5000, 5000))
            ?.setOnlyAlertOnce(true)
            ?.build()

        val nm = context?.let {
            NotificationManagerCompat.from(it)
        }

        return Pair(builder, nm)
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}


