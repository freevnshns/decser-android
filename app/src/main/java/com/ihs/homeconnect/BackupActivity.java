package com.ihs.homeconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;

import com.ihs.homeconnect.helpers.dbHandler;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;
import java.util.ArrayList;

public class BackupActivity extends AppCompatActivity {

    private OwnCloudClient mClient;
    private dbHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        dbHandler = new dbHandler(this, null);
        if (dbHandler.isBackupSet() == 0) {
            Intent intent = new Intent(this, SetupBackupActivity.class);
            startActivity(intent);
        } else {
            mClient = OwnCloudClientFactory.createOwnCloudClient(Uri.parse("http://127.0.0.1:9080/owncloud"), this, true);
            mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials("sidzi", "qazxsw"));
            auto_backup();
        }

//        ReadRemoteFolderOperation readRemoteFolderOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR);
//        readRemoteFolderOperation.execute(mClient, new OnRemoteOperationListener() {
//            @Override
//            public void onRemoteOperationFinish(RemoteOperation caller, RemoteOperationResult result) {
//
//            }
//        }, mHandler);
    }

    private void auto_backup() {
        ArrayList<String> auto_bkp_paths = dbHandler.getBackupPaths(1);
        for (String path : auto_bkp_paths) {
            File dir = new File(path);
            File[] list = dir.listFiles();
            String remoteUploadFolder = FileUtils.PATH_SEPARATOR;
            UploadRemoteFileOperation uploadRemoteFileOperation;
            for (File aList : list) {
                String mime = null;
                String ext = MimeTypeMap.getFileExtensionFromUrl(aList.getAbsolutePath());
                if (ext != null)
                    mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                uploadRemoteFileOperation = new UploadRemoteFileOperation(aList.getAbsolutePath(), remoteUploadFolder, mime);
                uploadRemoteFileOperation.addDatatransferProgressListener(new OnDatatransferProgressListener() {
                    @Override
                    public void onTransferProgress(long progressRate, long totalTransferredSoFar, long totalToTransfer, String fileAbsoluteName) {
                        System.out.println(totalTransferredSoFar);
                    }
                });
                uploadRemoteFileOperation.execute(mClient);
            }

        }

    }
}
