package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: MemoListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.memo_list)
        adapter = MemoListAdapter()
        recyclerView.adapter = adapter

        val editText = findViewById<EditText>(R.id.memo_edit_text)
        val addButton = findViewById<Button>(R.id.add_button)

        val realm = Realm.getDefaultInstance()

        addButton.setOnClickListener {
            val text = editText.text.toString()
            if (text.isEmpty()) {
                // テキストが空の場合には無視
                return@setOnClickListener
            }
            // Realmのトランザクション
            realm.executeTransactionAsync {
                // Memoのオブジェクトを作成
                val memo = it.createObject(Memo::class.java)
                // nameに入力してあったtextを代入
                memo.name = text
                // 上書きする
                it.copyFromRealm(memo)
            }
            // テキストを空にする
            editText.text.clear()
        }
        // DBに変更があった時に通知がくる
        realm.addChangeListener {
            // 変更があった時にリストをアップデートする
            val memoList = it.where(Memo::class.java).findAll().map { it.name }
            // UIスレッドで更新する
            recyclerView.post {
                adapter.updateMemoList(memoList)
            }
        }
        // 初回表示時にリストを表示
        realm.executeTransactionAsync {
            val memoList = it.where(Memo::class.java).findAll().map { it.name }
            // UIスレッドで更新する
            recyclerView.post {
                adapter.updateMemoList(memoList)
            }
        }
    }
}