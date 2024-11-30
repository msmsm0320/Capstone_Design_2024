package com.example.app_project

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LiveStreamActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로컬 웹 스트리밍 페이지로 이동
        val webUrl = "http://10.0.2.2:5001"  // 웹 페이지의 로컬 주소
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        startActivity(browserIntent)

        // 현재 Activity 종료
        finish()
    }
}