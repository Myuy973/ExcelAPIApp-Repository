package com.example.excelapiapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.excelapiapp.databinding.FragmentSecondBinding
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import io.realm.kotlin.where
import kotlinx.coroutines.selects.select
import org.bson.types.ObjectId
import java.util.zip.DataFormatException

class SecondFragment : Fragment() {

    private lateinit var realm: Realm

    private var _binding: FragmentSecondBinding? = null
    private val binding get() = _binding!!

    private val args: SecondFragmentArgs by navArgs()
    private var restQuestion = 1
    private var correctAnswer = 0
    private var answerButtonName = ""
    private var sheetIndex = 0
    private var sheetDataSize = 0

    private var resultList: MutableList<String> = mutableListOf()
    private var resultList_Sub: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.sheetId == -1) {
            Log.d("value", "args.sheetId is -1L")
        }else {
            Log.d("value", "args.sheetId: ${args.sheetId}")
            Log.d("value", "args: $args")
            sheetIndex = args.sheetId
        }
        sheetDataSize = realm.where<Word>().equalTo("sheetIndex", sheetIndex).findAll().size
        Log.d("value", "word data size: $sheetDataSize")
        Log.d("value", "Second sheet Index: $sheetIndex")


        binding.quizStartButton.setOnClickListener {
            binding.apply{
                quizVocabulary.visibility = View.VISIBLE
                choiceScroll.visibility = View.VISIBLE
                restQuestionCount.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                quizStartButton.visibility = View.INVISIBLE
                toContentCreatePageButton.visibility = View.INVISIBLE
            }
            quizSet()
        }

        binding.toContentCreatePageButton.setOnClickListener {
            val allData = realm.where<DataStorage>().findFirst()
            val filePath = allData?.filePath as String
            val sheetName = allData
                    .sheetNameList[sheetIndex]
                    ?.split(",")
                    ?.get(0) as String

            Log.d("value", "filePath: $filePath, sheetName: $sheetName")
            val action = SecondFragmentDirections
                    .actionSecondFragmentToContentCreateFragment(filePath, sheetName)
            findNavController().navigate(action)
        }

        binding.apply {
            firstChoice.setOnClickListener { quizSetandAnswerCount("firstChoice")}
            secondChoice.setOnClickListener { quizSetandAnswerCount("secondChoice")}
            thirdChoice.setOnClickListener { quizSetandAnswerCount("thirdChoice")}
        }
    }

    private fun quizSetandAnswerCount(buttonName: String) {
        if (answerButtonName == buttonName) correctAnswer++
        var selectButtonChoice = ""
        when(buttonName) {
            "firstChoice" -> selectButtonChoice =  binding.firstChoice.text.toString()
            "secondChoice" -> selectButtonChoice =  binding.secondChoice.text.toString()
            "thirdChoice" -> selectButtonChoice =  binding.thirdChoice.text.toString()
        }
        resultList_Sub += ",${selectButtonChoice}"
        resultList.add(resultList_Sub)
        resultList_Sub = ""

        if (restQuestion > 10) {
            Log.d("value", "Quiz End, correntAnswer: $correctAnswer")
            Log.d("value", "resultList: $resultList")
            val actionResult = (resultList).toTypedArray()
            val action = SecondFragmentDirections
                        .actionSecondFragmentToResultFragment(actionResult, correctAnswer)
            findNavController().navigate(action)

            reset()
        } else {
            quizSet()
        }
    }

    private fun quizSet() {


        binding.restQuestionCount.text = "${restQuestion++} / 10"
        binding.textView.text = "${correctAnswer}問正解"

        val randomNum = getRandom()
        val randomWord = getWordRandom(randomNum)
        resultList_Sub += randomWord.vocabulary
        resultList_Sub += ",${randomWord.description}"

        binding.quizVocabulary.text = randomWord.vocabulary

        var choice1ID: Long
        var choice2ID: Long
        do {
            choice1ID = getRandom()
            choice2ID = getRandom()
        } while (choice1ID == choice2ID || randomNum == choice2ID || randomNum == choice1ID)

        val choice1 = getWordRandom(choice1ID).description
        val choice2 = getWordRandom(choice2ID).description
        val answerChoice = randomWord.description

        answerButtonName = buttonRandomSet(listOf(choice1, choice2, answerChoice))

    }

    private fun getRandom(): Long {
        return (0 until sheetDataSize).random().toLong()
    }

    private fun buttonRandomSet(choiceList: List<String?>): String {

        val shuffleChoiceList = choiceList.shuffled()

        binding.firstChoice.text = shuffleChoiceList[0]
        binding.secondChoice.text = shuffleChoiceList[1]
        binding.thirdChoice.text = shuffleChoiceList[2]

        return when(choiceList[2]) {
            shuffleChoiceList[0] -> "firstChoice"
            shuffleChoiceList[1] -> "secondChoice"
            shuffleChoiceList[2] -> "thirdChoice"
            else -> ""
        }
    }

    private fun reset() {
        restQuestion = 1
        correctAnswer = 0
        answerButtonName = ""
        sheetIndex = 0
        resultList = mutableListOf()
        resultList_Sub = ""

        Log.d("value", "RESET CLEAR")
    }

    private fun getWordRandom(randomNum: Long): Word {
        Log.d("value", "sheetIndex: $sheetIndex, sheetId: $randomNum")
        return realm.where<Word>()
            .equalTo("sheetIndex", sheetIndex)
            .equalTo("sheetId", randomNum)
            .findFirst() as Word
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }
}