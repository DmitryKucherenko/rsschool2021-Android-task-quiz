package com.rsschool.quiz

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rsschool.question.Question
import com.rsschool.question.QuestionsBase
import com.rsschool.quiz.databinding.ActivityMainBinding
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import kotlin.system.exitProcess


//Использую viewPager2
class MainActivity : AppCompatActivity(), FragmentQuiz.FragmentAction,
    FragmentResult.FragmentResultAction {
    private  var binding: ActivityMainBinding?=null
    private lateinit var viewPager: ViewPager2
    private  var resultAnswers= Array(5) {"" to 0}
    private var resultScore: Int = 0
    private  lateinit var fragmentList: List<Fragment>
    //присваиваем переменной функцию с диалогом
    private var exitDialog:AlertDialog.Builder?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //привязываем binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        fragmentList = getFragmentList()
        viewPager = requireNotNull(binding?.viewPager)
        // отключаем переключение страниц пальцем
        viewPager.also {
            it.isUserInputEnabled = false
            it.adapter = ViewPagerAdapter(this, fragmentList)
            it.offscreenPageLimit = fragmentList.size

            it.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    changeStatusBarColor(position)
                    super.onPageSelected(position)
                }
            })
        }
        exitDialog=AlertDialog.Builder(this)
            .setMessage("Do your want out from quiz?")
            .setPositiveButton(android.R.string.ok) { _, _ ->
                finish()
                exitProcess(0)
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
            }
        resultAnswers =  Array(5) {"" to 0}
    }

    //Логика нажатия системной кнопки back
    override fun onBackPressed() {
        when (viewPager.currentItem) {
            0 -> exitDialog?.show()
            fragmentList.size - 1 -> reboot()
            else -> --viewPager.currentItem
        }
    }

    //Функция возврата списка с созданными фрагментами
    private fun getFragmentList(): List<Fragment> {
        return listOf(
            FragmentQuiz.newInstance(1, QuestionsBase.questions[0], R.style.Theme_Quiz_First),
            FragmentQuiz.newInstance(2, QuestionsBase.questions[1], R.style.Theme_Quiz_Second),
            FragmentQuiz.newInstance(3, QuestionsBase.questions[2], R.style.Theme_Quiz_Third),
            FragmentQuiz.newInstance(4, QuestionsBase.questions[3], R.style.Theme_Quiz_Fourth),
            FragmentQuiz.newInstance(5, QuestionsBase.questions[4], R.style.Theme_Quiz_Fifth),
            FragmentResult.newInstance(0, R.style.Theme_Quiz_First)
        )
    }

    //внутрений класс ViewPagerAdapter для viewPager2
    private inner class ViewPagerAdapter(fa: FragmentActivity, var fragmentList: List<Fragment>) :
        FragmentStateAdapter(fa) {
        //перегружаем функцию возвата количества страниц
        override fun getItemCount(): Int {
            return fragmentList.size
        }

        //перегружаем функцию создания фрагмента
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> fragmentList[position]
                1 -> fragmentList[position]
                2 -> fragmentList[position]
                3 -> fragmentList[position]
                4 -> fragmentList[position]
                5 -> fragmentList[position]
                else -> throw IllegalArgumentException("wrong position!")
            }
        }
    }

    //Реализуем логику кнопки next
    override fun nextAction(answer: Pair<String, Int>) {
        resultAnswers[viewPager.currentItem] = answer
        ++viewPager.currentItem
    }

    //Реализуем логику кнопки previous
    override fun previousAction() {
        --viewPager.currentItem
    }

    //Меняем цвет статус бара
    fun changeStatusBarColor(page: Int) {
        val fragmentQuiz = fragmentList[page]
        if (fragmentQuiz is FragmentQuiz) {
            fragmentQuiz.updateStatusBar()
        } else
            if (fragmentQuiz is FragmentResult) {
                fragmentQuiz.updateStatusBar()
            }

    }

    //Реализуем логику после нажатия кнопки submit:  в функцию передаем ответы и переходим к старнице
    //с результатам обновив textView с результатом
    override fun submitAction(answer: Pair<String, Int>) {
        val fragmentResult = fragmentList[fragmentList.size - 1]
        //Записываем ответ на предпоследней странице
        resultAnswers[viewPager.currentItem] = answer
        //подсчитываем очки
        resultScore = resultAnswers.sumOf { it.second }
        if (fragmentResult is FragmentResult) {
            fragmentResult.arguments = bundleOf("RESULT" to resultScore)
            fragmentResult.update()
        }
        //Переключаем страницу
        ++viewPager.currentItem
    }

    override fun pageCount(): Int {
        return requireNotNull(viewPager.adapter).itemCount
    }

    //Начинаем Quiz заново. Персоздаем фрагменты и обнуляем массив с результатами
    override fun reboot() {
        fragmentList = getFragmentList()

        viewPager.also {
            it.adapter = ViewPagerAdapter(this, fragmentList)
            it.offscreenPageLimit = fragmentList.size
            it.currentItem = 0}

        resultAnswers =  Array(5) {"" to 0}
    }

    //функция отправки результатов
    override fun sendMessage() {
        //внутреняя функция формирующая текст письма
        fun getBodyMessage(
            score: Int,
            questions: Array<Question>,
            answers: Array<Pair<String, Int>>
        ): String {
            return with(StringBuilder()) {
                appendLine("Your result:$score %")
                for ((index, question) in questions.withIndex()) {
                    appendLine()
                    appendLine("${index + 1})${question.textQuestion}")
                    appendLine("Your answer: ${answers[index].first}")
                }
                toString()
            }

        }
//Создаем неявный интент. Фомируем заголовок письма и текст
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, "Quiz results")
            putExtra(
                Intent.EXTRA_TEXT,
                getBodyMessage(score = resultScore,
                    questions = QuestionsBase.questions, answers = resultAnswers)
            )
            type = "text/plain"
        }
        //передаем интент
        startActivity(emailIntent)
    }
}
