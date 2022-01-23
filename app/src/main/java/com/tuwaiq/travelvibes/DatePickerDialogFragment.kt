package com.tuwaiq.travelvibes

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.tuwaiq.travelvibes.postFragment.POST_DATE_KEY
import java.util.*

class DatePickerDialogFragment : DialogFragment() {

    interface DatePickerCallback {
        fun onDateSelected(date: Date)
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val date = arguments?.getSerializable(POST_DATE_KEY) as String


        val calendar = Calendar.getInstance()
        if (date.isNotEmpty()) {
            val realdate = Date(date.toLong())
            calendar.time = realdate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dat ->
            val resultDate = GregorianCalendar(year, month, dat).time

            targetFragment?.let {
                (it as DatePickerCallback).onDateSelected(resultDate)
            }
        }

        return DatePickerDialog(
            requireContext(),
            dateListener,
            year,
            month,
            day
        )

    }
}