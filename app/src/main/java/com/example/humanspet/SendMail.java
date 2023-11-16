package com.example.humanspet;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import javax.mail.MessagingException;
import javax.mail.SendFailedException;


public class SendMail extends AppCompatActivity {

    String user = "humanspetmail@gmail.com"; // 보내는 계정의 id
    String password = "gzaw yoma uino tcfz"; // 보내는 계정의 pw

    GMailSender gMailSender = new GMailSender(user, password);
    String emailCode = gMailSender.getEmailCode();
    private SharedPreferences preferences;

    public void sendSecurityCode(final Context context, final String sendTo) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    gMailSender.sendMail("Human's Pet 인증 메일입니다.", emailCode, sendTo);
                    // preferences를 UI 업데이트와 관련된 코드 앞으로 이동
                } catch (SendFailedException e) {
                    showToastOnMainThread(context, "이메일 형식이 잘못되었습니다.");
                } catch (MessagingException e) {
                    showToastOnMainThread(context, "인터넷 연결을 확인해주십시오");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // UI 업데이트 코드를 여기에 추가
                preferences = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("CODE", emailCode);
                editor.apply();
                super.onPostExecute(result);
            }
        }.execute();
    }


    private void showToastOnMainThread(final Context context, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
