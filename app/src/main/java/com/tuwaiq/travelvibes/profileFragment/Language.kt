package com.tuwaiq.travelvibes.profileFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.res.Configuration
import androidx.preference.PreferenceManager
import com.tuwaiq.travelvibes.MainActivity
import java.util.*


private const val PREF_CHANGE_LANG_KEY = "my_lang"

object Language {

    fun showChangeLang(activity: Activity) {
        val listLanguage = arrayOf("Arabic", "English")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Choose Language")
        builder.setSingleChoiceItems(listLanguage, -1) { dialog, which ->
            when (which) {
                0 -> {
                    setLocate("ar", activity)
                  activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()

                }

                1 -> {
                    setLocate("en", activity)
                    activity.startActivity(Intent(activity, MainActivity::class.java))
                    activity.finish()
                }
            }
            dialog.dismiss()

        }

        val mDialog = builder.create()
        mDialog.show()
    }


    fun setLocate(Lang: String, activity: Activity) {
        val locale = Locale(Lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale

        activity?.resources?.updateConfiguration(config, activity?.resources?.displayMetrics)

        PreferenceManager.getDefaultSharedPreferences(activity).edit()
            .putString(PREF_CHANGE_LANG_KEY, Lang)
            .apply()
    }

    fun loadLocate(activity: Activity) {
        val pref = PreferenceManager.getDefaultSharedPreferences(activity)
        val language = pref.getString(PREF_CHANGE_LANG_KEY, "")!!
        setLocate(language, activity)

    }

}

