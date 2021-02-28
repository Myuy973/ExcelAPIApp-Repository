package com.example.excelapiapp

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
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
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class FirstFragment : Fragment() {

    private val REQUEST_XLS = 0

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

        var result = ""
        var file = resources.assets.open("words.xls")
        val wb = WorkbookFactory.create(file)
        val sheet = wb.getSheet("未分類")
        Log.d("value", "first: ${sheet.firstRowNum}, last: ${sheet.lastRowNum}")
        for (i in 0..sheet.lastRowNum) {
            var rightCell = sheet.getRow(i).getCell(0).stringCellValue
            var leftCell = sheet.getRow(i).getCell(1).stringCellValue
            result += "$rightCell :$leftCell \n"
        }
//        binding.textviewFirst.text = result

        binding.button.setOnClickListener {
            var intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.type = "application/vnd.openxmlformats-"
            startActivityForResult(Intent.createChooser(intent, "Pick a source"), REQUEST_XLS)

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_XLS && resultCode == RESULT_OK) {

            val xlsFile = data?.data
            Log.d("value", "xlsFile: $xlsFile")

            val inputStream = context?.contentResolver?.openInputStream(xlsFile as Uri)
            Log.d("value", "inputStream: $inputStream")
            val wb = WorkbookFactory.create(inputStream)
            val sheet = wb.getSheet("未分類")
            Log.d("value", "first: ${sheet.firstRowNum}, last: ${sheet.lastRowNum}")
            var result = ""
//            for (i in 0..sheet.lastRowNum) {
//                result += "${ sheet.getRow(i).getCell(0).stringCellValue } :" +
//                        " ${ sheet.getRow(i).getCell(1).stringCellValue}\n"
//            }
        binding.textviewFirst.text = sheet.getRow(0).getCell(0).stringCellValue


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}