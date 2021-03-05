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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.excelapiapp.databinding.FragmentFirstBinding
import io.realm.*
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Sheet

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

            sheetNameList.clear()

            val xlsFile = data?.data
            val inputStream = context?.contentResolver?.openInputStream(xlsFile as Uri)
            val wb = WorkbookFactory.create(inputStream)
//            val sheet = wb.getSheetAt(sheetIndex)

            // シートセレクト画面のボタン更新
            for (sheetIndex in 0 until wb.numberOfSheets) {
                var sheetName = wb.getSheetAt(sheetIndex).sheetName
                sheetNameList.add(sheetName)
                Log.d("value", "sheet Name: ${sheetNameList[sheetIndex]}")
            }
            sheetChange()

            for (sheetIndex in 0 until wb.numberOfSheets) {
                val sheet = wb.getSheetAt(sheetIndex)
                for (row in 0..sheet.lastRowNum) {
                    wordSet(sheet, row, sheetIndex)
                }
            }

            // 既存のシート読み込み、またadapterの設定
            sheetRead()

            Log.d("value", "word size: ${realm.where<Word>().findAll().size}")

        }
    }

    private fun wordSet(sheet: Sheet, row: Int, sheetIndex: Int) {
        realm.executeTransaction { db: Realm ->

            val maxId_word = db.where<Word>().max("id")
            val nextId_word = (maxId_word?.toLong() ?: 0L) + 1L
            val word = db.createObject<Word>(nextId_word)
            word.sheetId = row.toLong()
            word.sheetIndex = sheetIndex
            word.vocabulary = sheet.getRow(row).getCell(0).stringCellValue
            word.description = sheet.getRow(row).getCell(1).stringCellValue

            Log.d("value", "sheetindex: ${word.sheetIndex},sheetId: ${word.sheetId}, voc: ${word.vocabulary}")

        }
    }

    private fun sheetRead() {
        try {
            var sheetList = realm.where<DataStorage>().findFirst()?.sheetNameList as List<String>
//            Log.d("value", "sheetList: ${sheetList[0]}")
            binding.sheetsRecyclerView.layoutManager = LinearLayoutManager(context)
            val adapter = SheetListAdapter(sheetList)
            Log.d("value", "adapter: $adapter")
            binding.sheetsRecyclerView.adapter = adapter

            adapter.setOnItemClickListener { id ->
                id?.let {
                    val action =
                        FirstFragmentDirections.actionFirstFragmentToSecondFragment(it)
                    findNavController().navigate(action)
                }
            }

        } catch (e: NullPointerException) {
            Log.d("value", "have not List")
        }

    }

    private fun sheetChange() {
        realm.executeTransaction { db: Realm ->

            val data_size = db.where<DataStorage>().findAll().size
            Log.d("value", "size: $data_size")
            if (data_size == 1) {
                Log.d("value", "data_size - 1: ${data_size - 1}")
                Log.d("value", "data_size - 1 db: ${db.where<DataStorage>().equalTo("id", data_size - 1)}")
                db.where<DataStorage>().equalTo("id", data_size - 1)
                        ?.findFirst()
                        ?.deleteFromRealm()

                db.where<Word>().findAll().deleteAllFromRealm()
                Log.d("value", "reset after word size: ${db.where<Word>().findAll().size}")
            }
            val data = db.createObject<DataStorage>(0)
            data.sheetNameList = sheetNameList

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