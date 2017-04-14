package com.example.jeny.mypushclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String urlStr = "http://betheracer.com/a0_android_PushID.php";

    public String regId;

    TextView messageOutput;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 수신할 메시지 출력 박스
        messageOutput = (TextView) findViewById(R.id.messageOutput);


        // 서버 : 전송하기 버튼
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 단말 등록하고 등록 ID 받기
                    registerDevice();
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

    }


    /**
     * 단말 등록
     */
    private void registerDevice() {

        RegisterThread registerObj = new RegisterThread();
        registerObj.start();
    }


    class RegisterThread extends Thread {
        public void run() {

            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                regId = gcm.register(GCMInfo.PROJECT_ID);
                println("푸시 서비스를 위해 단말을 등록했습니다.");
                println("등록 ID : " + regId);

                // 등록 ID 리스트에 추가 (현재는 1개만)
                //idList.clear();
                //idList.add(regId);

                String output = requestDBregister(urlStr);
                Log.d(TAG, output);

            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }


        private String requestDBregister(String urlStr) {
            StringBuilder output = new StringBuilder();

            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String host_No = manager.getLine1Number();

            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    //conn.setRequestMethod("GET");
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    StringBuffer param = new StringBuffer();
                    param.append("regId="+regId+"&phoneNo="+host_No);

                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
                    pw.write(param.toString());
                    pw.flush();


                    int resCode = conn.getResponseCode();
                    if (resCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR")) ;
                        String line = null;
                        while(true) {
                            line = reader.readLine();
                            if (line == null) {
                                break;
                            }
                            output.append(line + "\n");
                        }

                        reader.close();
                        conn.disconnect();
                    }
                }
            } catch(Exception ex) {
                Log.e("SampleHTTP", "Exception in processing response.", ex);
                ex.printStackTrace();
            }


            return output.toString();
        }


    }


    private void println(String msg) {
        final String output = msg;
        handler.post(new Runnable() {
            public void run() {
                Log.d(TAG, output);
                Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
            }
        });
    }



    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() called.");

        processIntent(intent);

        super.onNewIntent(intent);
    }


    /**
     * 수신자로부터 전달받은 Intent 처리
     *
     * @param intent
     */
    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            Log.d(TAG, "from is null.");
            return;
        }

        String command = intent.getStringExtra("command");
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");

        println("DATA : " + command + ", " + type + ", " + data);
        messageOutput.setText("[" + from + "]로부터 수신한 데이터 : " + data);
    }

}
