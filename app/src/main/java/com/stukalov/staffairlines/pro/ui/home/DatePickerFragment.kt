package com.stukalov.staffairlines.pro.ui.home

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.stukalov.staffairlines.pro.GlobalStuff
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Create a new instance of DatePickerDialog and return it
        val ddiag = DatePickerDialog(requireActivity(), this, year, month, day)
        c.add(Calendar.DATE, 0)
        ddiag.datePicker.minDate = c.timeInMillis
        return ddiag
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        var SearchDT = LocalDate.of(year, month+1, day)
        var formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val text = SearchDT.format(formatter)
        GlobalStuff.dtSearch.text = text
        GlobalStuff.SearchDT = SearchDT
    }
}