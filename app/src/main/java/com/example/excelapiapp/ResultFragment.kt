package com.example.excelapiapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.excelapiapp.databinding.FragmentResultBinding
import io.realm.Realm

class ResultFragment : Fragment() {


    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

    private val args: ResultFragmentArgs by navArgs()

    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resultList: List<List<String>> = args.totalList.map {
            it.split(",")
        }

        Log.d("value", "args.total: ${args.totalList}")
        Log.d("value", "resultList: $resultList")

        if (args.totalScore != -1) {
            binding.resultText.text = "${args.totalScore} / 10\n正解！"
        } else {
            binding.resultText.text = "エラー"
        }

        Log.d("value", "after resultList: $resultList")

        binding.resultList.layoutManager = LinearLayoutManager(context)
        val adapter = ResultListAdapter(resultList)
        binding.resultList.adapter = adapter

        binding.returnSelectButton.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_FirstFragment)
        }

        binding.restartButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}