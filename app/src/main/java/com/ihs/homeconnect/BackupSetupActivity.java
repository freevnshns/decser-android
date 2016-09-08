package com.ihs.homeconnect;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.dbHandler;
import com.owncloud.android.lib.resources.files.FileUtils;

public class BackupSetupActivity extends AppCompatActivity {
    private int REQUEST_KEY_PATH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, ListFileActivity.class);
        startActivityForResult(intent, REQUEST_KEY_PATH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_KEY_PATH && resultCode == RESULT_OK) {
            String path, dir_path;
            path = data.getStringExtra("filepath");
            dir_path = path.substring(0, path.lastIndexOf(FileUtils.PATH_SEPARATOR)) + FileUtils.PATH_SEPARATOR;
            dbHandler dbHandler = new dbHandler(BackupSetupActivity.this, null);
            try {
                dbHandler.insertBackupPaths(dir_path, 1);
            } catch (SQLiteConstraintException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BackupSetupActivity.this, "This path gets a backup already", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        if (resultCode == RESULT_CANCELED) {
            finish();
        }
    }
}
