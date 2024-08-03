package  com.example.flagchallenge.ui

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.flagchallenge.R
import com.example.flagchallenge.utility.Constants
import com.example.flagchallenge.dataclass.Country
import com.example.flagchallenge.dataclass.Question
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class QuestionsActivity : AppCompatActivity() {

    lateinit var questionNumber: TextView
    private lateinit var timerText: TextView
    lateinit var tvQuestion: TextView
    lateinit var ivImage: ImageView
    private lateinit var progressBar: ProgressBar
    lateinit var tvProgress: TextView
    lateinit var btnSubmit: Button
    lateinit var tvAlternatives: ArrayList<TextView>

    var questionsList: ArrayList<Question> = ArrayList()
    private var currentQuestionIndex: Int = 0
    private var isAnswerChecked: Boolean = false
    private var selectedAlternativeIndex: Int = -1
    private var totalScore: Int = 0
    private var timer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AppTheme)
        setContentView(R.layout.activity_question)
        initializeUI()
        fetchQuestionsData()
    }

    private fun initializeUI() {
        questionNumber = findViewById(R.id.questionNumber)
        timerText = findViewById(R.id.timerText)
        tvQuestion = findViewById(R.id.tvQuestion)
        ivImage = findViewById(R.id.ivImage)
        progressBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tvProgress)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvAlternatives = arrayListOf(
            findViewById(R.id.optionOne),
            findViewById(R.id.optionTwo),
            findViewById(R.id.optionThree),
            findViewById(R.id.optionFour)
        )

        btnSubmit.setOnClickListener {
            handleAnswerSubmission()
        }

        tvAlternatives.forEachIndexed { index, option ->
            option.setOnClickListener {
                if (!isAnswerChecked) {
                    selectedAlternativeView(it as TextView, index)
                }
            }
        }
    }

    private fun fetchQuestionsData() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://gist.github.com/simielezabeth/93291f9e00d31061b80551d8ebaed0a7/raw/questions.json")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@QuestionsActivity,
                        getString(R.string.failed_to_load), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                response.body?.string()?.let { jsonString ->
                    parseJsonData(jsonString)
                    runOnUiThread {
                        updateQuestion()
                    }
                }
            }
        })
    }

    fun parseJsonData(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val questionsArray = jsonObject.getJSONArray("questions")

        questionsList = ArrayList()
        for (i in 0 until questionsArray.length()) {
            val questionObj = questionsArray.getJSONObject(i)
            val answerId = questionObj.getInt("answer_id")
            val countryCode = questionObj.getString("country_code")

            val countriesArray = questionObj.getJSONArray("countries")
            val countriesList = ArrayList<Country>()
            for (j in 0 until countriesArray.length()) {
                val countryObj = countriesArray.getJSONObject(j)
                val countryName = countryObj.getString("country_name")
                val id = countryObj.getInt("id")
                countriesList.add(Country(countryName, id))
            }

            val questionText = getString(R.string.question)
            val image = getFlagDrawableResource(countryCode)
            val alternatives = countriesList.map { it.countryName }

            questionsList.add(Question(answerId, questionText, image, alternatives, countriesList.indexOfFirst { it.id == answerId }))
        }
    }

    fun getFlagDrawableResource(countryCode: String): Int {
        return when (countryCode) {
            "NZ" -> R.drawable.new_zealand__nz
            "AW" -> R.drawable.aruba_ar
            "EC" -> R.drawable.ecuador__ec
            "PY" -> R.drawable.paraguay__py
            "KG" -> R.drawable.kyrgyzstan_kg
            "PM" -> R.drawable.saint_pierre_and_miquelon__pm
            "JP" -> R.drawable.japan__jp
            "TM" -> R.drawable.turkmenistan__tm
            "GA" -> R.drawable.gabon__ga
            "MQ" -> R.drawable.martinique__mq
            "BZ" -> R.drawable.belize__bz
            "CZ" -> R.drawable.czech_republic__cz
            "AE" -> R.drawable.united_arab_emirates__ae
            "JE" -> R.drawable.jersey__je
            "LS" -> R.drawable.lesotho__ls
            else -> R.drawable.new_zealand__nz
        }
    }

    fun updateQuestion() {
        defaultAlternativesView()

        tvQuestion.text = questionsList[currentQuestionIndex].questionText
        questionNumber.text = getString(R.string.question_text)+"${currentQuestionIndex + 1}"
        ivImage.setImageResource(questionsList[currentQuestionIndex].image)

        progressBar.progress = currentQuestionIndex + 1
        tvProgress.text = "${currentQuestionIndex + 1}/${questionsList.size}"

        for (alternativeIndex in questionsList[currentQuestionIndex].alternatives.indices) {
            tvAlternatives[alternativeIndex].text = questionsList[currentQuestionIndex].alternatives[alternativeIndex]
        }
        btnSubmit.text = if (currentQuestionIndex == questionsList.size - 1) getString(R.string.finish) else getString(
            R.string.submit
        )
        startQuestionTimer()
    }

    private fun handleAnswerSubmission() {
        if (!isAnswerChecked) {
            val anyAnswerIsChecked = selectedAlternativeIndex != -1
            if (!anyAnswerIsChecked) {
                Toast.makeText(this, getString(R.string.please_select_an_option), Toast.LENGTH_SHORT).show()
            } else {
                val currentQuestion = questionsList[currentQuestionIndex]
                if (selectedAlternativeIndex == currentQuestion.correctAnswerIndex) {
                    // Correct Answer
                    answerView(tvAlternatives[selectedAlternativeIndex], R.drawable.correct_option_border_bg, true)
                    totalScore++
                } else {
                    // Wrong Answer
                    answerView(tvAlternatives[selectedAlternativeIndex], R.drawable.wrong_option_border_bg, false)
                    answerView(tvAlternatives[currentQuestion.correctAnswerIndex], R.drawable.correct_option_border_bg, true)
                }

                isAnswerChecked = true
                btnSubmit.text = if (currentQuestionIndex == questionsList.size - 1) "FINISH" else "GO TO NEXT QUESTION"
                selectedAlternativeIndex = -1
            }
        } else {
            if (currentQuestionIndex < questionsList.size - 1) {
                currentQuestionIndex++
                updateQuestion()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(Constants.TOTAL_QUESTIONS, questionsList.size)
                intent.putExtra(Constants.SCORE, totalScore)
                startActivity(intent)
                finish()
            }

            isAnswerChecked = false
        }
    }


    private fun updateResultMessages(selectedIndex: Int, correctIndex: Int) {
        tvAlternatives.forEachIndexed { index, textView ->
            val resultTextViewId = when (textView.id) {
                R.id.optionOne -> R.id.optionOneResult
                R.id.optionTwo -> R.id.optionTwoResult
                R.id.optionThree -> R.id.optionThreeResult
                R.id.optionFour -> R.id.optionFourResult
                else -> return@forEachIndexed
            }
            val resultTextView = findViewById<TextView>(resultTextViewId)
            resultTextView.visibility = TextView.GONE
        }

        if (selectedIndex != -1) {
            val isCorrect = selectedIndex == correctIndex
            answerView(tvAlternatives[selectedIndex], if (isCorrect) R.drawable.correct_option_border_bg else R.drawable.wrong_option_border_bg, isCorrect)
        }

        answerView(tvAlternatives[correctIndex], R.drawable.correct_option_border_bg, true)
    }

    private fun defaultAlternativesView() {
        for (alternativeTv in tvAlternatives) {
            alternativeTv.typeface = Typeface.DEFAULT
            alternativeTv.setTextColor(Color.parseColor(getString(R.string._7a8089)))
            alternativeTv.background = ContextCompat.getDrawable(
                this@QuestionsActivity,
                R.drawable.default_option_border_bg
            )
            val resultTextViewId = when (alternativeTv.id) {
                R.id.optionOne -> R.id.optionOneResult
                R.id.optionTwo -> R.id.optionTwoResult
                R.id.optionThree -> R.id.optionThreeResult
                R.id.optionFour -> R.id.optionFourResult
                else -> return
            }
            val resultTextView = findViewById<TextView>(resultTextViewId)
            resultTextView.visibility = TextView.GONE
        }
    }

    private fun selectedAlternativeView(option: TextView, index: Int) {
        defaultAlternativesView()
        selectedAlternativeIndex = index

        option.setTextColor(Color.parseColor(getString(R.string._363a43)))
        option.setTypeface(option.typeface, Typeface.BOLD)
        option.background = ContextCompat.getDrawable(
            this@QuestionsActivity,
            R.drawable.selected_option_border_bg
        )
    }


    private fun answerView(view: TextView, drawableId: Int, isCorrect: Boolean) {
        view.background = ContextCompat.getDrawable(this@QuestionsActivity, drawableId)
        view.setTextColor(Color.parseColor("#FFFFFF"))

        // Show result message
        val resultTextViewId = when (view.id) {
            R.id.optionOne -> R.id.optionOneResult
            R.id.optionTwo -> R.id.optionTwoResult
            R.id.optionThree -> R.id.optionThreeResult
            R.id.optionFour -> R.id.optionFourResult
            else -> return
        }
        val resultTextView = findViewById<TextView>(resultTextViewId)
        resultTextView.text = if (isCorrect) "Correct" else "Incorrect"
        resultTextView.setTextColor(if (isCorrect) Color.parseColor("#4CAF50") else Color.parseColor("#F44336"))
        resultTextView.visibility = TextView.VISIBLE
    }


    private fun startQuestionTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                timerText.text = String.format("00:%02d", secondsRemaining)
            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onFinish() {
                // Mark unanswered question as wrong
                val currentQuestion = questionsList[currentQuestionIndex]
                updateResultMessages(selectedAlternativeIndex, currentQuestion.correctAnswerIndex)
                isAnswerChecked = true
                btnSubmit.text = if (currentQuestionIndex == questionsList.size - 1) getString(R.string.finish) else getString(R.string.go_to_next_question)
                selectedAlternativeIndex = -1
            }
        }.start()
    }
}

