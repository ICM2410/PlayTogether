package together.devs.playtogether

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import together.devs.playtogether.databinding.ActivityBookingBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookingBinding
    private val calendar = Calendar.getInstance()

    private fun datePicker() {
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val dayMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(
            this, { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = dateFormat.format(selectedDate.time)
                binding.bookDate.setText(formattedDate)
            },
            currentYear, currentMonth, dayMonth
        )
        picker.show()
    }

    private fun timePicker() {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            binding.bookTime.setText(
                SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                    calendar.time
                )
            )
        }
        TimePickerDialog(this, timeSetListener, currentHour, currentMinute, true).show()
    }

    private fun goToFields() {
        startActivity(Intent(baseContext, FieldInfoActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addDateButton.setOnClickListener { datePicker() }
        binding.addTimeButton.setOnClickListener { timePicker() }

        binding.createBooking.setOnClickListener { goToFields() }
    }
}