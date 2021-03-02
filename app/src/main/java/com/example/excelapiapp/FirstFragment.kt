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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.excelapiapp.databinding.FragmentFirstBinding
import io.realm.*
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
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
    private lateinit var realm: Realm

    private var sheetNameList: RealmList<String> = RealmList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sheetRead()

        binding.addFileFab.setOnClickListener() {
            var intent = Intent(Intent.ACTION_GET_CONTENT);
            intent.type = "application/vnd.ms-excel"
            startActivityForResult(intent, REQUEST_XLS)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_XLS && resultCode == RESULT_OK) {

            var sheetIndex = 0

            val xlsFile = data?.data
            val inputStream = context?.contentResolver?.openInputStream(xlsFile as Uri)
            val wb = WorkbookFactory.create(inputStream)
            val sheet = wb.getSheetAt(sheetIndex)

            for (i in 0 until wb.numberOfSheets) {
                var sheetName = wb.getSheetAt(i).sheetName
                sheetNameList.add(sheetName)
            }

            for (row in 0..sheet.lastRowNum) {
                wordSet(sheet, row, sheetIndex)
            }

            for (i in 0 until sheetNameList.size) {
                Log.d("value", "sheetname: ${sheetNameList[i]}")
            }
            sheetRead()
        }
    }

    private fun wordSet(sheet: Sheet, row: Int, sheetIndex: Int) {
        realm.executeTransaction { db: Realm ->
            val maxId_word = db.where<Word>().max("id")
            val nextId_word = (maxId_word?.toLong() ?: 0L) + 1L
            val word = db.createObject<Word>(nextId_word)
            word.sheetIndex = sheetIndex
            word.vocabulary = sheet.getRow(row).getCell(0).stringCellValue
            word.description = sheet.getRow(row).getCell(1).stringCellValue

            val maxId_data = db.where<Word>().max("id")
            val nextId_data = (maxId_data?.toLong() ?: 0L) + 1L
            val data = db.createObject<DataStorage>(nextId_data)
            data.sheetNameList = sheetNameList

        }
    }

    private fun sheetRead() {
        try {
            var sheetList = realm.where<DataStorage>().findFirst()?.sheetNameList as RealmList
            Log.d("value", "sheetList: $sheetList")
            binding.sheetsRecyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = SheetListAdapter(sheetList)
            binding.sheetsRecyclerView.adapter = adapter
        } catch (e: NullPointerException) {
            Log.d("value", "have not List")
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