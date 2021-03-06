package com.example.a2020limhwang;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class ProfStudentActivity extends AppCompatActivity {

    ImageButton back, profile, btn_edit;
    ListView listView;
    TextView text_lectureName;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    String lecture, id_students, str_result, lectureName;
    String[] studentID, studentName;
    int numOfStu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profstu);

        Intent intent = getIntent();
        lecture = intent.getExtras().getString("lectureNum");
        lectureName = intent.getStringExtra("lectureName");

        back = findViewById(R.id.back);
        profile = findViewById(R.id.profile);
        btn_edit = findViewById(R.id.btn_edit);
        listView= findViewById(R.id.listStu);
        text_lectureName = findViewById(R.id.textView_lectureName);

        text_lectureName.setText(lectureName);

        sharedPreferences = getSharedPreferences("sFile", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        id_students = sharedPreferences.getString("id_students", "");

        //ip고치기
        new ProfStudentActivity.JSONTask().execute("http://192.168.0.76:3000/teaches/getStudents");

    }
    public class CustomList extends ArrayAdapter<String> {
        private final Activity context;
        public CustomList(Activity context ) {
            super(context, R.layout.item_profstu, studentID);
            this.context = context;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.item_profstu, null, true);
            TextView tv_stunumber = rowView.findViewById(R.id.student_number);
            TextView tv_stuname = rowView.findViewById(R.id.student_name);

            tv_stunumber.setText(studentID[position]);
            tv_stuname.setText(studentName[position]);

            return rowView;
        }
    }
    public void onClick(View v) {
        if (v.getId() == R.id.profile) {
            Intent intent = new Intent(this, ProfMypageActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == R.id.back) {
            finish();
        }
    }
    public class JSONTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.accumulate("LectureID", lecture);

                HttpURLConnection con = null;
                BufferedReader reader = null;

                try {
                    URL url = new URL(urls[0]);

                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Cache-Control", "no-cache");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("Accept", "text/html");
                    con.setDoOutput(true);
                    con.setDoInput(true);
                    con.connect();

                    OutputStream outStream = con.getOutputStream();

                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outStream));
                    writer.write(jsonObject.toString());
                    writer.flush();
                    writer.close();

                    InputStream stream = con.getInputStream();

                    reader = new BufferedReader(new InputStreamReader(stream));

                    StringBuffer buffer = new StringBuffer();

                    String line = "";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    return buffer.toString();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            str_result = result + "";
            try {
                JSONObject jsonObject = new JSONObject(str_result);
                JSONArray studentInfoArray = jsonObject.getJSONArray("studentInfo");
                numOfStu = studentInfoArray.length();

                studentID = new String[numOfStu];
                studentName = new String[numOfStu];

                for (int i = 0; i < numOfStu; i++) {
                    JSONObject tmp = (JSONObject) studentInfoArray.get(i);
                    studentID[i] = tmp.getString("id");
                    studentName[i] = tmp.getString("name");

                    Log.d("id", studentID[i]);
                    Log.d("name", studentName[i]);

                }


                CustomList adapter = new CustomList(ProfStudentActivity.this);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(ProfStudentActivity.this, ProfStateActivity.class);
                        intent.putExtra("studentID", studentID[position]);
                        intent.putExtra("studentName", studentName[position]);
                        intent.putExtra("lectureNum",lecture);
                        intent.putExtra("lectureName",lectureName);
                        startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
