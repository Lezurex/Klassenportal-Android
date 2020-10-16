package com.lezurex.ap20bklassenportal.tasks;

import android.widget.Toast;

import com.lezurex.ap20bklassenportal.PreloadActivity;
import com.lezurex.ap20bklassenportal.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class Task {

    private int id;
    private String title;
    private String subject;
    private String date;
    private String descripion;

    public Task(int id, String title, String subject, String date, String descripion) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.date = date;
        this.descripion = descripion;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getDescripion() {
        return descripion;
    }

    public String getDateFormatted() {
        Timestamp ts = new Timestamp(Long.parseLong(getDate()) * 1000);
        System.out.println("[DEBUG] Timestamp: " + ts.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(PreloadActivity.resources.getString(R.string.tasks_date_format));
        String date = simpleDateFormat.format(ts);
        simpleDateFormat.applyPattern(PreloadActivity.resources.getString(R.string.tasks_time_format));
        String time = simpleDateFormat.format(ts);
        return date + " " + PreloadActivity.resources.getString(R.string.tasks_time_at) + " " + time;
    }
}
