package  com.example.flagchallenge.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.flagchallenge.R
import java.util.*

class MainActivity : AppCompatActivity() ,OnClickListener{

    private lateinit var timePicker: TimePicker
    private lateinit var saveButton: Button
    private lateinit var header: TextView
    private lateinit var countdownTimer: TextView
    private lateinit var countdownMessage: TextView
    private var scheduledHour: Int = 0
    private var scheduledMinute: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeUI()

    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun initializeUI() {
        timePicker = findViewById(R.id.timePicker)
        saveButton = findViewById(R.id.btnStart)
        header = findViewById(R.id.header)
        countdownTimer = findViewById(R.id.countdownTimer)
        countdownMessage = findViewById(R.id.countdownMessage)

        saveButton.visibility = VISIBLE
        timePicker.visibility = VISIBLE
        countdownTimer.visibility = TextView.GONE
        countdownMessage.visibility = TextView.GONE

        timePicker.setIs24HourView(false)
        saveButton.setOnClickListener(this)

    }

    private fun saveScheduledTime(hour: Int, minute: Int) {
        val sharedPref: SharedPreferences = getSharedPreferences("ChallengePreferences", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("scheduledHour", hour)
            putInt("scheduledMinute", minute)
            apply()
        }
        val scheduledTime = String.format("%02d:%02d", hour, minute)
        header.text = "Challenge scheduled for $scheduledTime"
    }

    private fun startCountdownTimer() {
        val currentTime = Calendar.getInstance()
        val scheduledTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, scheduledHour)
            set(Calendar.MINUTE, scheduledMinute)
            set(Calendar.SECOND, 0)
        }

        var timeDifference = scheduledTime.timeInMillis - currentTime.timeInMillis

        // If the timeDifference is negative, it means the time has already passed for today
        if (timeDifference <= 0) {
            // Schedule it for the next day
            scheduledTime.add(Calendar.DAY_OF_YEAR, 1)
            timeDifference = scheduledTime.timeInMillis - currentTime.timeInMillis
        }
        saveButton.visibility = GONE
        timePicker.visibility = GONE
        countdownTimer.visibility = TextView.VISIBLE
        countdownMessage.visibility = TextView.VISIBLE

        object : CountDownTimer(timeDifference, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                val minutesRemaining = secondsRemaining / 60
                val hoursRemaining = minutesRemaining / 60
                countdownMessage.text = String.format(" %02d:%02d:%02d", hoursRemaining, minutesRemaining % 60, secondsRemaining % 60)
            }

            override fun onFinish() {
                navigateToChallenge()
            }
        }.start()
    }

    private fun navigateToChallenge() {
        val intent = Intent(this, QuestionsActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        if (v!!.id ==R.id.btnStart){
            scheduledHour = timePicker.hour
            scheduledMinute = timePicker.minute
            saveScheduledTime(scheduledHour, scheduledMinute)
            startCountdownTimer()
        }else{

        }
    }
}
