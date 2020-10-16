package com.lezurex.ap20bklassenportal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lezurex.ap20bklassenportal.login.LoginActivity;
import com.lezurex.ap20bklassenportal.tasks.Task;
import com.lezurex.ap20bklassenportal.tasks.TaskRecViewAdapter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView tasksRecView;
    private TaskRecViewAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton btnRefresh, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new TaskRecViewAdapter(this, "MainActivity");
        tasksRecView = findViewById(R.id.tasksRecView);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnLogout = findViewById(R.id.btnLogout);

        tasksRecView.setAdapter(adapter);
        tasksRecView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setTasks(Utils.getInstance(this).getTasks());

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.pop_logout_title));
                builder.setMessage(getResources().getString(R.string.pop_logout_desc));
                builder.setPositiveButton(getResources().getString(R.string.pop_logout_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.getInstance(MainActivity.this).setAccessToken(null);
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.pop_logout_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void refresh() {
        swipeRefresh.setRefreshing(true);
        ArrayList<Task> newTasks = Utils.getInstance(this).refreshTasks();
        adapter.setTasks(newTasks);
        swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        super.onBackPressed();
    }
}