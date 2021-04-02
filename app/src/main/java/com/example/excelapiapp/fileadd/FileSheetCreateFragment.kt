package com.example.excelapiapp.fileadd

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.excelapiapp.interfaceSummary.PermissionCheck
import com.example.excelapiapp.databinding.FragmentFileSheetCreateBinding
import com.google.android.material.snackbar.Snackbar
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import java.io.File
import java.io.FileOutputStream


class FileSheetCreateFragment : Fragment(), PermissionCheck {

    private val DIRECTORYSELECT = 1

    private var _binding: FragmentFileSheetCreateBinding? = null
    private val binding get() = _binding!!
    private var sheetNameList: MutableList<String> = mutableListOf()

    private var savePath: String = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileSheetCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.sheetNameList.apply {
            layoutManager = LinearLayoutManager(context)
            val sheetAdapter = SheetAddAdapter(sheetNameList)
            adapter = sheetAdapter
            sheetAdapter.setOnItemClickListener { position ->
                sheetNameList.removeAt(position)
                sheetNameListUpdate()
            }
        }


        binding.savePathSelectButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }
            startActivityForResult(intent, DIRECTORYSELECT)

        }

        binding.addSheetButton.setOnClickListener {
            val sheetName = binding.sheetNameEditText.text.toString()
            if (sheetName.isEmpty()) {
                Snackbar.make(view as View,
                        "シートの名前を入力してください",
                        Snackbar.LENGTH_SHORT)
                        .show()
            } else {
                sheetNameList.add(sheetName)
                sheetNameListUpdate()
            }
        }


        binding.moveToVocaddButton.setOnClickListener { fileSheetCreate() }


    }

    private fun fileSheetCreate() {
        var fileName = binding.newFileNameEditText.text.toString()
        if (!fileName.contains(Regex("(.+)\\.(xls)"))) fileName += ".xls"
        Log.d("value", "fileName edit: $fileName")

        var saveAble = true

        if (binding.savePathText.text.isEmpty()) saveAble = fileInfoCheck("保存場所を選択してください")
        if (fileName.isEmpty()) saveAble = fileInfoCheck("ファイルの名前を入力してください")
        if (sheetNameList.isEmpty()) saveAble = fileInfoCheck("シートを追加してください")


        if (isExternalStorageWriteable() &&
                checkPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                && saveAble) {

            val hssfWorkbook = HSSFWorkbook()
            for (i in 0 until sheetNameList.size) {
                val s = hssfWorkbook.createSheet()
                hssfWorkbook.setSheetName(i, sheetNameList[i])
            }

//                val hssfRow = hssfSheet.createRow(0)
//                val hssfCell = hssfRow.createCell(0)
//                hssfCell.setCellValue("hello!!!")

            try {
                val myExternalFile = File(savePath, fileName)
                Log.d("value", "Dir path:$savePath")
                val fos = FileOutputStream(myExternalFile)
                hssfWorkbook.write(myExternalFile)
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val selectSheetName = binding.sheetSpinner.selectedItem as String

            Log.d("value", "selectSheetName: $selectSheetName")

            val action = FileSheetCreateFragmentDirections
                    .actionFileSheetCreateFragmentToContentCreateFragment(
                            "${savePath}/${fileName}",
                            selectSheetName)
            findNavController().navigate(action)
        }

    }

    private fun fileInfoCheck(message: String) : Boolean {
        Snackbar.make(view as View, message, Snackbar.LENGTH_SHORT).show()
        return false
    }

    private fun sheetNameListUpdate() {
        binding.sheetNameList.apply {
            layoutManager = LinearLayoutManager(context)
            val sheetAdapter = SheetAddAdapter(sheetNameList)
            adapter = sheetAdapter
            sheetAdapter.setOnItemClickListener { position ->
                sheetNameList.removeAt(position)
                sheetNameListUpdate()
            }
        }

        binding.sheetSpinner.adapter = ArrayAdapter<String>(
                context as Context,
                android.R.layout.simple_spinner_item,
                sheetNameList
        )

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DIRECTORYSELECT && resultCode == Activity.RESULT_OK) {
            if (isExternalStorageWriteable() &&
                    checkPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                val savePathMain = decode(pathAll, "utf-8")
//                Log.d("value", "decode(pathAll, \"utf-8\"): ${savePathMain}")

                var pathBefore: File = Environment.getExternalStorageDirectory()
                var pathAfter: String? = data?.data?.path?.split(":")?.get(1)
                when(Build.VERSION.SDK_INT) {
                    28 -> {
                        savePath = pathAfter as String
                    }
                    29 -> {
                        Log.d("value", "Environment.getExternalStorageDirectory: ${Environment.getExternalStorageDirectory()}")
                        Log.d("value", "data?.data?.path: ${data?.data?.path}")
                        savePath = "$pathBefore/$pathAfter"
                    }
                }


                binding.savePathText.text = savePath

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}