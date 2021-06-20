package com.rsschool.quiz

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rsschool.question.Question
import com.rsschool.quiz.databinding.FragmentQuizBinding
import java.io.Serializable

//Класс фрагмента с вопросами
class FragmentQuiz : Fragment() {
    //текущая страница
    private var page:Int=0
    //id Темы
    private var theme:Int=0
    private  var _binding: FragmentQuizBinding?=null
    private val binding get() = requireNotNull(_binding)
    //интерфейс для вызова действия для next, previous и submit кнопок
    private var actionCallBack:FragmentAction?=null
    private var statusBarColor:Int=0


     interface FragmentAction {
        fun nextAction(answerResult:Pair<String,Int>)
        fun previousAction()
        fun submitAction(resultAnswer:Pair<String,Int>)
        fun pageCount():Int
    }

    override fun onAttach(context: Context) {
        if (context is FragmentAction) {
        actionCallBack=context as FragmentAction}else {
            throw RuntimeException("$context must implement FragmentQuiz.FragmentAction")
        }
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //получаем текущую страницу
        page=arguments?.getInt(PAGE) ?: 0
        //применяем тему
        theme=arguments?.getInt(THEME) ?: 0
        requireContext().theme.applyStyle(theme, true);
        //запоминаем цвет статус бара для этой темы.
        statusBarColor=getStatusBarColor()
        _binding= FragmentQuizBinding.inflate(inflater,container,false)
        return binding.root
    }
//функция получения темы статус бара
    fun getStatusBarColor():Int {
        val typedValue= TypedValue()
        val currentTheme = requireContext().theme
        currentTheme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        return typedValue.resourceId
    }
    //обновить тему статус бара
    fun updateStatusBar(){
        val window =  requireActivity().window
        window.setStatusBarColor(ContextCompat.getColor(requireContext(),statusBarColor))
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pageCount=actionCallBack?.pageCount() ?:0
        var question = arguments?.getSerializable(QUESTION) as Question
        val previous = binding.previousButton
        val next = binding.nextButton
        val questionTextView=binding.question
        val radioGroup = binding.radioGroup
        binding.toolbar.title="Qestion $page"
        //если первая страница отключаем кнопку preivious и прячем иконку navigationIcon
        if(page==1){
            previous?.isEnabled=false
            binding.toolbar.navigationIcon=null
        }
        //Если старница с результатом, то меняем название кнопки на Submit
        if(page==pageCount-1)next.text="Submit"

        binding.toolbar.setNavigationOnClickListener {actionCallBack?.previousAction()}

        previous.setOnClickListener{ actionCallBack?.previousAction()}

        next.setOnClickListener{
            val checkedId: Int = radioGroup.checkedRadioButtonId
            val radioButton = view.findViewById<RadioButton>(checkedId)
            val index =radioGroup.indexOfChild(radioButton)
            if(page==pageCount-1)actionCallBack?.submitAction(question.answers[index])else
                actionCallBack?.nextAction(question.answers[index])

        }
        //присваиваем текст вопроса
        questionTextView.text=question.textQuestion
//Присваиваем текст ответов RadioButton
        for (i in 0 until radioGroup.getChildCount()) {
            val radioButton: View = radioGroup.getChildAt(i)
            if (radioButton is RadioButton) {
                radioButton.text=question.answers[i].first
            }
        }

        //Если мы выбрали ответ разблокируем кнопку next
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            next?.isEnabled=true
        }
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        fun newInstance(page:Int, question: Question, theme:Int): FragmentQuiz {
            val fragment = FragmentQuiz()
            val args = Bundle()
            //Передаем id Темы
            args.putInt(THEME, theme)
            //Передаем страницу
            args.putInt(PAGE, page)
            //Передаем ответы
            args.putSerializable(QUESTION,question as Serializable)
            fragment.arguments = args
            return fragment
        }
        private const val PAGE = "PAGE"
        private const val THEME = "THEME"
        private const val QUESTION="QUESTION"
    }

    override fun onDestroyView() {
        _binding =null
        super.onDestroyView()
    }

}