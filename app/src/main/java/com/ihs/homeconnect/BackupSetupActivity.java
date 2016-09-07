package com.ihs.homeconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ihs.homeconnect.helpers.dbHandler;

import java.util.ArrayList;

public class BackupSetupActivity extends AppCompatActivity {
    private int REQUEST_KEY_PATH = 1;
    private ArrayList<String> backup_paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_backup);
        backup_paths = new ArrayList<>();
        Intent intent = new Intent(this, ListFileActivity.class);
        startActivityForResult(intent, REQUEST_KEY_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_KEY_PATH && resultCode == RESULT_OK) {
            String path, dir_path;
            path = data.getStringExtra("filepath");
            if (!path.equals("")) {
                dir_path = path.substring(0, path.lastIndexOf("/")) + "/";
                backup_paths.add(dir_path);
                dbHandler dbHandler = new dbHandler(BackupSetupActivity.this, null);
                dbHandler.insertBackupPaths(dir_path, 1);
                dbHandler.setColumnBackupSet();
            } else
                startActivityForResult(new Intent(BackupSetupActivity.this, ListFileActivity.class), REQUEST_KEY_PATH);
        }
    }
}
