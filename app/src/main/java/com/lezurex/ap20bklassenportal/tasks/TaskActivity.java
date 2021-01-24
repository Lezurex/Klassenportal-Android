package com.lezurex.ap20bklassenportal.tasks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lezurex.ap20bklassenportal.PreloadActivity;
import com.lezurex.ap20bklassenportal.R;
import com.lezurex.ap20bklassenportal.Utils;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;

public class TaskActivity extends AppCompatActivity {

    public static final String TASK_ID_KEY = "taskId";

    private FloatingActionButton btnBack;
    private TextView txtTaskTitle, txtSubject, txtDate;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        btnBack = findViewById(R.id.btnBack);
        txtTaskTitle = findViewById(R.id.txtTaskTitle);
        txtSubject = findViewById(R.id.txtSubject);
        txtDate = findViewById(R.id.txtDue);
        webView = findViewById(R.id.webView);

        Intent intent = getIntent();
        if (null != intent) {
            int taskId = intent.getIntExtra(TASK_ID_KEY, -1);
            if (taskId != -1) {
                ArrayList<Task> tasks = Utils.getInstance(this).getTasks();
                for (Task task: tasks) {
                    if (task.getId() == taskId) {
                        setData(task);
                    }
                }
            }
        }


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setData(Task task) {
        txtTaskTitle.setText(task.getTitle());
        txtSubject.setText(task.getSubject());
        txtDate.setText(task.getDateFormatted());

        String style = "";

        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                style = "<style>html { filter: invert(1) hue-rotate(180deg); font-family: Roboto;} img { filter: invert(1) hue-rotate(180deg); }</style>";
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                style = "<style>html { color: black; font-family: Roboto; }</style>";
                break;
        }

        webView.setBackgroundColor(Color.TRANSPARENT);
        String html = StringEscapeUtils.unescapeJson(task.getDescripion().replaceAll("\n", "<br>")) + style;
        webView.loadDataWithBaseURL("localhost", html, "text/html; charset=utf8", "UTF-8", "");
    }

}