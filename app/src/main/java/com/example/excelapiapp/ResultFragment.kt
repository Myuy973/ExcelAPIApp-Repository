package com.example.excelapiapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.excelapiapp.databinding.FragmentResultBinding
import io.realm.Realm

class ResultFragment : Fragment() {


    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!

//    private val args: SecondFragmentArgs by navArgs()
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

        Log.d("value", "args.total: ${args.totalCount}")
        Log.d("value", "args: ${args}")

        binding.resultText.text = "${args.totalCount} / 10\n正解！"

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