package com.rsschool.quiz

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.rsschool.quiz.databinding.FragmentResultBinding

//класс с выводом результата и кнопками отпавить по почте, начать с начала, закрыть пложение
class FragmentResult : Fragment() {

    private  var _binding: FragmentResultBinding?=null
    private val binding get() = requireNotNull(_binding)
    //Переменая для хранения цвета статус бара
    private var statusBarColor:Int=0
    //Интерфейс для действия перезапуска и отправки сообщения, который реализует MainAcivity
    private var actionCallBack: FragmentResultAction?=null

    interface FragmentResultAction {
        fun reboot()
        fun sendMessage()
    }

    override fun onAttach(context: Context) {

        if (context is FragmentResultAction) {
            actionCallBack=context
        }else {
            throw RuntimeException("$context must implement FragmentResult.FragmentResultAction")
        }
            super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val  theme=arguments?.getInt(THEME) ?: 0
        requireContext().theme.applyStyle(theme, true);
        statusBarColor=getStatusBarColor()
        _binding= FragmentResultBinding.inflate(inflater,container,false)
        return binding.root
    }

    //функция получения темы статус бара
    fun getStatusBarColor():Int {
        val typedValue= TypedValue()
        val currentTheme = requireContext().theme
        currentTheme.resolveAttribute(android.R.attr.statusBarColor, typedValue, true)
        return typedValue.resourceId
    }
    fun updateStatusBar(){
        val window =  requireActivity().window
        window.setStatusBarColor(ContextCompat.getColor(requireContext(),statusBarColor))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rebootApp.setOnClickListener{
            actionCallBack?.reboot()
        }

        binding.closeApp.setOnClickListener{
            requireActivity().finish();
            System.exit(0);
        }

        binding.sendMessage.setOnClickListener{
            actionCallBack?.sendMessage()
        }


    }
    fun update(){
        val result = arguments?.getInt(RESULT)
       binding.resultTextView.text="Your result is ${result.toString()}%"

    }


    companion object {
        fun newInstance(result:Int, theme:Int): FragmentResult {
            val fragment = FragmentResult()
            val args = Bundle()
            args.putInt(THEME, theme)
            args.putInt(RESULT, result)
            fragment.arguments=args
            return fragment
        }
        private const val RESULT = "RESULT"
        private const val THEME = "THEME"
    }

    override fun onDestroyView() {
        _binding =null
        super.onDestroyView()
    }
}
