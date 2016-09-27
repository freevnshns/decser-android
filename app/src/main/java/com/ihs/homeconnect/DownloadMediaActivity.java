package com.ihs.homeconnect;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.loggingHandler;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class DownloadMediaActivity extends AppCompatActivity {

    public static Session session = null;
    public static Channel channel = null;
    public static ChannelSftp channelSftp = null;
    String DOWNLOAD_PATH = Environment.getExternalStorageDirectory().getPath() + "/ihs/server_downloads/";

    public DownloadMediaActivity() throws JSchException {
        channel = session.openChannel("sftp");
        channel.connect();
        channelSftp = (ChannelSftp) channel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_media);

        ListView lvMediaFileList;
        lvMediaFileList = populateList("/home/user/downloads/");
        lvMediaFileList.setClickable(true);
        lvMediaFileList.setLongClickable(true);
        lvMediaFileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return populateList(parent.getItemAtPosition(position).toString()) != null;
            }
        });
        lvMediaFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String filename = parent.getItemAtPosition(position).toString();
                new sftpGetFileTask().execute(filename);
            }
        });
    }

    public ListView populateList(String path) {
        Vector fileList = null;
        try {
            channelSftp.cd(path);
            fileList = channelSftp.ls(".");
        } catch (SftpException e) {
            loggingHandler l = new loggingHandler();
            l.addLog(e.toString());
            e.printStackTrace();
        }
        if (fileList != null) {
            ArrayList<String> arrayList = new ArrayList<>();
            int i = 0;
            String filename;
            while (i < fileList.size()) {
                filename = ((ChannelSftp.LsEntry) fileList.get(i)).getFilename();
                if (!filename.startsWith(".")) {
                    arrayList.add(filename);
                }
                i++;
            }
            ArrayAdapter<String> mediaFileListAdapter = new ArrayAdapter<>(this, R.layout.activity_download_media, R.id.tvDownloadLabel, arrayList);
            final ListView lvMediaFileList = (ListView) findViewById(R.id.lvServerMediaFileList);
            lvMediaFileList.setAdapter(mediaFileListAdapter);
            return lvMediaFileList;

        } else {
            return null;
        }
    }

    class sftpGetFileTask extends AsyncTask<String, Integer, Void> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            this.progressDialog = new ProgressDialog(DownloadMediaActivity.this);
            progressDialog.setMessage("Downloading file");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Void doInBackground(String... strings) {
            if (strings.length == 1) {
                try {
                    channelSftp.get(strings[0], DOWNLOAD_PATH + strings[0], new advancedProgressMonitor());
                } catch (SftpException e) {
                    if (e.id == 4) {
                        recursiveDownload(strings[0]);
                        Toast.makeText(DownloadMediaActivity.this, "Folder Download Successful", Toast.LENGTH_SHORT).show();
                    } else {
                        e.printStackTrace();
                        Toast.makeText(DownloadMediaActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                try {
                    channelSftp.get(strings[0] + "/" + strings[1], DOWNLOAD_PATH + strings[0] + "/" + strings[1], new advancedProgressMonitor());
                } catch (SftpException se) {
                    se.printStackTrace();
                    Toast.makeText(DownloadMediaActivity.this, "Download Failed", Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }

        void recursiveDownload(String filename) {
            Vector filesInDirectory = null;
            try {
                filesInDirectory = channelSftp.ls(filename);
            } catch (SftpException e) {
                e.printStackTrace();
            }
            if (filesInDirectory != null) {
                File dir = new File(DOWNLOAD_PATH + filename + "/");
                if (!dir.exists()) {
                    System.out.print(dir.mkdirs());
                }
                int i = 0;
                String fName;
                while (i < filesInDirectory.size()) {
                    fName = ((ChannelSftp.LsEntry) filesInDirectory.get(i)).getFilename();
                    if (!fName.startsWith(".")) {
                        String dirCheck = ((ChannelSftp.LsEntry) filesInDirectory.get(i)).getAttrs().toString();
                        if (dirCheck.startsWith("d")) {
                            recursiveDownload(filename + "/" + fName);
                        }
                        new sftpGetFileTask().execute(filename, fName);
                    }
                    i++;
                }
            }
        }

        class advancedProgressMonitor implements SftpProgressMonitor {

            long totalFileSize;
            long totalTransferred;

            @Override
            public void init(int op, String src, String dest, long max) {
                totalFileSize = max;
                totalTransferred = 0;
            }

            @Override
            public boolean count(long count) {
                if (totalTransferred >= totalFileSize)
                    return false;
                else {
                    totalTransferred += count;
                    publishProgress((int) (((float) totalTransferred / (float) totalFileSize) * 100));
                    return true;
                }
            }

            @Override
            public void end() {

            }
        }
    }
}
