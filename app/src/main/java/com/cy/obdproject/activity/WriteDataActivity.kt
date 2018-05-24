package com.cy.obdproject.activity

import android.content.Intent
import android.os.Bundle
import com.cy.obdproject.R
import com.cy.obdproject.adapter.WriteDataAdapter
import com.cy.obdproject.base.BaseActivity
import com.cy.obdproject.bean.WriteDataBean
import com.cy.obdproject.tools.NetTools
import com.cy.obdproject.url.Urls
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_write_data.*
import org.jetbrains.anko.toast

class WriteDataActivity : BaseActivity(), BaseActivity.ClickMethoListener {

    private var list: ArrayList<WriteDataBean>? = null
    private var adapter: WriteDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_data)
        initView()
        // net_fileList()


        val mIntent = Intent(this, WriteData2Activity::class.java)
        mIntent.putExtra("url", "http://p.gdown.baidu.com/60332a6f13574428559b663d5adad887219fa2e6070da7e66162d8d25561f8ffe6e9c60e6c68be502a45303ce87f36228a02825f92b8d9e5426f684e60de79128148cede56f7b1bc1a1bf927b5342d1c387caa1a980a0b4a1ed8501d01d454b35a151cde03b4abe3261361f02d9a87b531e25ce730a2c48b3a1253258e892f01bdc4ca836f83f382052d694050d08dbb1f80f193d1f58286ebb23d0a9e9b5e9199106b7d77cb43008bc4c220733a85e56e1d1101ac59f1a1541243c5a95e16e171b5bc1df2529ab2501183ae227166a9dc61601287ed5f695c9c5ae3eceebaf8ba791b3302edaef3c58f29b5df2eeb110c4fc32710c7b78263f4c6253581d960e8a1edb3d0229517ec1f579b1aa73b31eef65ecadba96d67c13cbbd216039347342b1370a7273a125009e4f902c86523aa14265001ac9f263b33cd0e8ba7daf69bc3fdf7e85ac556")
        startActivity(mIntent)
    }


    private fun initView() {
        setClickMethod(iv_back)
        list = ArrayList()
        listView.setOnItemClickListener { parent, view, position, id ->
            val mIntent = Intent(this, WriteData2Activity::class.java)
            // mIntent.putExtra("url", "http://p.gdown.baidu.com/60332a6f13574428559b663d5adad887219fa2e6070da7e66162d8d25561f8ffe6e9c60e6c68be502a45303ce87f36228a02825f92b8d9e5426f684e60de79128148cede56f7b1bc1a1bf927b5342d1c387caa1a980a0b4a1ed8501d01d454b35a151cde03b4abe3261361f02d9a87b531e25ce730a2c48b3a1253258e892f01bdc4ca836f83f382052d694050d08dbb1f80f193d1f58286ebb23d0a9e9b5e9199106b7d77cb43008bc4c220733a85e56e1d1101ac59f1a1541243c5a95e16e171b5bc1df2529ab2501183ae227166a9dc61601287ed5f695c9c5ae3eceebaf8ba791b3302edaef3c58f29b5df2eeb110c4fc32710c7b78263f4c6253581d960e8a1edb3d0229517ec1f579b1aa73b31eef65ecadba96d67c13cbbd216039347342b1370a7273a125009e4f902c86523aa14265001ac9f263b33cd0e8ba7daf69bc3fdf7e85ac556")
            mIntent.putExtra("url", list!![position].filePath)
            startActivity(mIntent)
        }

    }

    override fun doMethod(string: String?) {
        when (string) {
            "iv_back" -> {
                finish()
            }
        }
    }

    private fun net_fileList() {
        val map = hashMapOf<String, String>()
        NetTools.net(map, Urls().fileList, this, { response ->
            if (response.code == "0") {
                val beans = Gson().fromJson<List<WriteDataBean>>(response.data, object : TypeToken<ArrayList<WriteDataBean>>() {}.type) as ArrayList<WriteDataBean>?
                list!!.clear()
                list!!.addAll(beans!!)
                if (adapter == null) {
                    adapter = WriteDataAdapter(list!!, this)
                    listView!!.adapter = adapter
                } else {
                    adapter!!.notifyDataSetChanged()
                }
            } else {
                toast(response.msg!!)
            }
        }, "正在加载...", true, true)
    }

}
