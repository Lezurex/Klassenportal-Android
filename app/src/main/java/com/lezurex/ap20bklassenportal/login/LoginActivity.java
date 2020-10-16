package com.lezurex.ap20bklassenportal.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.lezurex.ap20bklassenportal.MainActivity;
import com.lezurex.ap20bklassenportal.R;
import com.lezurex.ap20bklassenportal.Utils;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtTxtEmail, edtTxtPassword;
    private MaterialButton btnLogin;
    private ProgressBar progressBar;
    private TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtTxtEmail = findViewById(R.id.edtTxtEmail);
        edtTxtPassword = findViewById(R.id.edtTxtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        txtError = findViewById(R.id.txtError);

        String email;
        if ((email = Utils.getInstance(this).getEmail()) != null) {
            edtTxtEmail.setText(email);
        }
        
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtError.setText("");
                if (!edtTxtEmail.getText().toString().equals("")) {
                    if (!edtTxtPassword.getText().toString().equals("")) {
                        btnLogin.setEnabled(false);
                        progressBar.setVisibility(View.VISIBLE);
                        LoginError state = Utils.getInstance(LoginActivity.this).login(edtTxtEmail.getText().toString(), edtTxtPassword.getText().toString());
                        switch (state) {
                            case SUCCESS:
                                progressBar.setVisibility(View.GONE);
                                Utils.getInstance(LoginActivity.this).setEmail(edtTxtEmail.getText().toString());
                                Utils.getInstance(LoginActivity.this).refreshTasks();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            case NOT_PERMITTED:
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                txtError.setText(getResources().getString(R.string.err_not_permitted));
                                break;
                            case INVALID_CREDENTIALS:
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                txtError.setText(getResources().getString(R.string.err_invalid_credentials));
                                edtTxtPassword.setText("");
                                break;
                            case OTHER:
                                progressBar.setVisibility(View.GONE);
                                btnLogin.setEnabled(true);
                                txtError.setText(getResources().getString(R.string.err_login_other));
                                break;
                        }
                    } else
                        edtTxtPassword.setError(getResources().getString(R.string.login_not_filled));
                } else
                    edtTxtEmail.setError(getResources().getString(R.string.login_not_filled));
            }
        });
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