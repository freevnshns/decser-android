package com.ihs.homeconnect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.cups4j.CupsClient;
import org.cups4j.CupsPrinter;
import org.cups4j.PrintJob;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;


public class PrintActivity extends AppCompatActivity {
    int REQUEST_PRINT_FILE_CODE = 101;
    String print_file_path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Intent intent = new Intent(this, ListFileActivity.class);
        startActivityForResult(intent, REQUEST_PRINT_FILE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_PRINT_FILE_CODE) {
            print_file_path = data.getStringExtra("filepath");
            cupsHandler cph = new cupsHandler();
            cph.execute();
        }
    }

    private class cupsHandler extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String[] objects) {
            try {
                CupsClient cupsClient = new CupsClient(new URL("http://127.0.0.1:9631"));
                CupsPrinter printer = cupsClient.getDefaultPrinter();
                if (print_file_path != null) {
                    InputStream inputStream = new FileInputStream(print_file_path);
                    PrintJob.Builder builder = new PrintJob.Builder(inputStream).userName("user").copies(1);
                    printer.print(builder.build());
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

