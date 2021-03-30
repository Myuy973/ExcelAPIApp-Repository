package com.example.excelapiapp.fileadd

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.excelapiapp.R
import com.example.excelapiapp.databinding.FragmentContentCreateBinding
import com.example.excelapiapp.databinding.FragmentSecondBinding
import io.realm.Realm
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.Exception

class ContentCreateFragment : Fragment() {

    private val args: ContentCreateFragmentArgs by navArgs()
    private var _binding: FragmentContentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContentCreateBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sheetName = args.SheetName
        val filePath = args.FilePath

        Log.d("value", "sheetname: $sheetName")
        Log.d("value", "filepath: $filePath")

        binding.vocAddButton.setOnClickListener { addVocabulary(sheetName, filePath) }

        binding.backToMainPageButton.setOnClickListener {
            findNavController().navigate(R.id.action_contentCreateFragment_to_FirstFragment2)
        }


    }

    private fun addVocabulary(sheetName: String, filePath: String) {
        lateinit var fis: FileInputStream
        var wb: Workbook? = null

        try {
            if (File(filePath).exists()) Log.d("value", "file exist!!!!!")

            fis = FileInputStream(File(filePath))
            wb = WorkbookFactory.create(fis)

            val sheet = wb?.getSheet(sheetName)
            Log.d("value", "sheet: $sheet")
            val lastRow = sheet?.lastRowNum as Int
            Log.d("value", "lastrownum: $lastRow")
            val lastRowIndex = lastRow + 1
            Log.d("value", "lastRowIndex: $lastRowIndex")
            val nextRow = sheet.createRow(lastRowIndex)
            Log.d("value", "nextRow: $nextRow")
            val vocCell = nextRow.createCell(0)
            val expCell = nextRow.createCell(1)

            val voc = binding.vocabularyText.text.toString()
            val exp = binding.explanationText.text.toString()

            vocCell.setCellValue(voc)
            expCell.setCellValue(exp)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        var output: FileOutputStream? = null
        try {
            output = FileOutputStream(File(filePath))
            wb?.write(output)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            output?.close()
            wb?.close()
        }

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