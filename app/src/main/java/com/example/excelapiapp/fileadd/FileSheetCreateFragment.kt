package com.example.excelapiapp.fileadd

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.excelapiapp.databinding.FragmentFileSheetCreateBinding
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.openxml4j.opc.PackagingURIHelper.getPath
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder.decode


class FileSheetCreateFragment : Fragment() {

    private val DIRECTORYSELECT = 1

    private var _binding: FragmentFileSheetCreateBinding? = null
    private val binding get() = _binding!!
    private var sheetNameList: MutableList<String> = mutableListOf()

    private var savePath: String = ""
    private val editTextExcel: EditText? = null
    private val selectSheet: String = ""


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFileSheetCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.savePathSelectButton.setOnClickListener {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            }
            startActivityForResult(intent, DIRECTORYSELECT)

        }

        binding.addSheetButton.setOnClickListener {
            val sheetName = binding.sheetNameEditText.text.toString()
            sheetNameList.add(sheetName)
            sheetNameListUpdate()
        }


        binding.moveToVocaddButton.setOnClickListener {

            if (isExternalStorageWriteable() &&
                checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                val fileName = binding.newFileNameEditText.text.toString()
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
                    val fos = FileOutputStream(myExternalFile, true)
                    hssfWorkbook.write(myExternalFile)
                    fos.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                val selectSheetName = binding.sheetSpinner.selectedItem as String

                Log.d("value", "selectSheetName: $selectSheetName")

                val action = FileSheetCreateFragmentDirections
                    .actionFileSheetCreateFragmentToContentCreateFragment("${savePath}/${fileName}", selectSheetName)
                findNavController().navigate(action)
            }

        }


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

    private fun isExternalStorageWriteable(): Boolean {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("value", "msg: Yes, it is writable")
            return true
        } else {
            return false
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val check: Int = ContextCompat.checkSelfPermission(context as Context, permission)
        Log.d("value", "check: ${check == PackageManager.PERMISSION_GRANTED}")
        return (check == PackageManager.PERMISSION_GRANTED)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DIRECTORYSELECT && resultCode == Activity.RESULT_OK) {
            if (isExternalStorageWriteable() && checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                val savePathMain = decode(pathAll, "utf-8")
//                Log.d("value", "decode(pathAll, \"utf-8\"): ${savePathMain}")

                Log.d("value", "Environment.getExternalStorageDirectory: ${Environment.getExternalStorageDirectory()}")
                val pathBefore = Environment.getExternalStorageDirectory()
                Log.d("value", "data?.data?.path: ${data?.data?.path}")
                val pathAfter = data?.data?.path?.split(":")?.get(1)

                savePath = "$pathBefore/$pathAfter"

                binding.savePathText.text = savePath

            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}