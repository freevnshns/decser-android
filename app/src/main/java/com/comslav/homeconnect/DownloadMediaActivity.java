package com.comslav.homeconnect;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.comslav.homeconnect.helpers.loggingHandler;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class DownloadMediaActivity extends Activity {

    public static Session session = null;
    public static Channel channel = null;
    public static ChannelSftp channelSftp = null;

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
        lvMediaFileList = populateList("/var/lib/transmission-daemon/downloads/");
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
                try {
                    channelSftp.get(filename, Environment.getExternalStorageDirectory().getPath() + "/comslav/media/" + filename);
                    Toast.makeText(getApplicationContext(), "File Download Successful", Toast.LENGTH_SHORT).show();

                } catch (SftpException e) {
                    if (e.id == 4) {
                        recursiveDownload(filename);
                        Toast.makeText(getApplicationContext(), "Folder Download Successful", Toast.LENGTH_SHORT).show();

                    } else {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Download Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }


            public void recursiveDownload(String filename) {
                Vector filesInDirectory = null;
                try {
                    filesInDirectory = channelSftp.ls(filename);
                } catch (SftpException e) {
                    e.printStackTrace();
                }
                if (filesInDirectory != null) {
                    File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/comslav/media/" + filename + "/");
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
                            try {
                                channelSftp.get(filename + "/" + fName, Environment.getExternalStorageDirectory().getPath() + "/comslav/media/" + filename + "/" + fName);
                            } catch (SftpException se) {
                                se.printStackTrace();
                            }
                        }
                        i++;
                    }
                }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_download_media, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
