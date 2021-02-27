package com.example.excelapiapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.excelapiapp.databinding.FragmentFirstBinding
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.file.Paths

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var file = resources.assets.open("words.xls")
        val wb = WorkbookFactory.create(file)
        val sheet = wb.getSheet("未分類")
        Log.d("value", "first: ${sheet.firstRowNum}, last: ${sheet.lastRowNum}")
        var result = ""
        for (i in 0..sheet.lastRowNum) {
            result += "${ sheet.getRow(i).getCell(0).stringCellValue } : ${ sheet.getRow(i).getCell(1).stringCellValue}\n"
        }
        binding.textviewFirst.text = result


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}