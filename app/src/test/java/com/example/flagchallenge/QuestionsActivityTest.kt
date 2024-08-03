package com.example.flagchallenge

import android.os.Build
import com.example.flagchallenge.ui.QuestionsActivity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.flagchallenge.dataclass.Question
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class QuestionsActivityTest {

    private lateinit var activity: QuestionsActivity

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(QuestionsActivity::class.java).create().resume().get()
    }

    @Test
    fun testGetFlagDrawableResource() {
        assertEquals(R.drawable.new_zealand__nz, activity.getFlagDrawableResource("NZ"))
        assertEquals(R.drawable.aruba_ar, activity.getFlagDrawableResource("AW"))
        assertEquals(R.drawable.ecuador__ec, activity.getFlagDrawableResource("EC"))
        assertEquals(R.drawable.paraguay__py, activity.getFlagDrawableResource("PY"))
        assertEquals(R.drawable.kyrgyzstan_kg, activity.getFlagDrawableResource("KG"))
        assertEquals(R.drawable.saint_pierre_and_miquelon__pm, activity.getFlagDrawableResource("PM"))
        assertEquals(R.drawable.japan__jp, activity.getFlagDrawableResource("JP"))
        assertEquals(R.drawable.turkmenistan__tm, activity.getFlagDrawableResource("TM"))
        assertEquals(R.drawable.gabon__ga, activity.getFlagDrawableResource("GA"))
        assertEquals(R.drawable.martinique__mq, activity.getFlagDrawableResource("MQ"))
        assertEquals(R.drawable.belize__bz, activity.getFlagDrawableResource("BZ"))
        assertEquals(R.drawable.czech_republic__cz, activity.getFlagDrawableResource("CZ"))
        assertEquals(R.drawable.united_arab_emirates__ae, activity.getFlagDrawableResource("AE"))
        assertEquals(R.drawable.jersey__je, activity.getFlagDrawableResource("JE"))
        assertEquals(R.drawable.lesotho__ls, activity.getFlagDrawableResource("LS"))
        assertEquals(R.drawable.new_zealand__nz, activity.getFlagDrawableResource("UNKNOWN"))
    }

    @Test
    fun testParseJsonData() {
        val jsonString = """
            {
              "questions": [
                {
                  "answer_id": 1,
                  "country_code": "NZ",
                  "countries": [
                    {"country_name": "New Zealand", "id": 1},
                    {"country_name": "Australia", "id": 2},
                    {"country_name": "Canada", "id": 3},
                    {"country_name": "USA", "id": 4}
                  ]
                }
              ]
            }
        """.trimIndent()

        activity.parseJsonData(jsonString)
        val questionsList = activity.questionsList

        assertNotNull(questionsList)
        assertEquals(1, questionsList.size)

        val question = questionsList[0]
        assertEquals(1, question.answerId)
        assertEquals(activity.getString(R.string.question), question.questionText)
        assertEquals(R.drawable.new_zealand__nz, question.image)
        assertEquals(4, question.alternatives.size)
        assertEquals("New Zealand", question.alternatives[0])
        assertEquals("Australia", question.alternatives[1])
        assertEquals("Canada", question.alternatives[2])
        assertEquals("USA", question.alternatives[3])
    }

    @Test
    fun testUpdateQuestion() {
        val questionsList = arrayListOf(
            Question(1, "Guess the country by the flag", R.drawable.new_zealand__nz, listOf("New Zealand", "Australia", "Canada", "USA"), 0)
        )
        activity.questionsList = questionsList
        activity.updateQuestion()

        assertEquals("Guess the country by the flag", activity.tvQuestion.text.toString())
        assertEquals("Question 1", activity.questionNumber.text.toString())
        assertEquals(R.drawable.new_zealand__nz, activity.ivImage.drawable)
        assertEquals("1/1", activity.tvProgress.text.toString())
        assertEquals("New Zealand", activity.tvAlternatives[0].text.toString())
        assertEquals("Australia", activity.tvAlternatives[1].text.toString())
        assertEquals("Canada", activity.tvAlternatives[2].text.toString())
        assertEquals("USA", activity.tvAlternatives[3].text.toString())
        assertEquals("FINISH", activity.btnSubmit.text.toString())
    }
}
