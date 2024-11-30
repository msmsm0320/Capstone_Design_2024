package com.example.app_project

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // FCM으로부터 메시지를 수신할 때 호출
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // 메시지가 알림 형태로 오는 경우 처리
        remoteMessage.notification?.let {
            Log.d("FCM", "알림 메시지 수신: ${it.title} - ${it.body}")
            sendNotification(it.title, it.body)
        }
    }

    // 새로운 FCM 토큰이 생성될 때 호출
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "새로운 FCM 토큰: $token")
        // 서버로 토큰을 전송하는 로직을 추가할 수 있습니다.
    }

    // 알림을 생성하고 표시하는 함수
    private fun sendNotification(title: String?, message: String?) {
        // 알림 클릭 시 이동할 액티비티 설정
        val intent = Intent(this, LiveStreamActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        // PendingIntent 설정 (사용자가 알림을 클릭했을 때 실행)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 알림 채널 ID
        val channelId = "fall_alerts_channel"

        // 알림 빌더 구성
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_alert)  // 알림 아이콘 설정
            .setContentTitle(title)  // 알림 제목
            .setContentText(message)  // 알림 내용
            .setAutoCancel(true)  // 클릭 시 자동으로 알림 제거
            .setContentIntent(pendingIntent)  // 알림 클릭 시 동작할 PendingIntent 설정

        // 알림 매니저 가져오기
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // 안드로이드 Oreo(API 26) 이상에서는 알림 채널이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Fall Alerts",  // 채널 이름
                NotificationManager.IMPORTANCE_DEFAULT  // 알림 중요도
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 알림 표시
        notificationManager.notify(0, notificationBuilder.build())
    }
}
