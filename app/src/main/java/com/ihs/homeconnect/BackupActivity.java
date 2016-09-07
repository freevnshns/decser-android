package com.ihs.homeconnect;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;

import com.ihs.homeconnect.helpers.dbHandler;
import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.network.OnDatatransferProgressListener;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.CreateRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;
import java.util.ArrayList;

public class BackupActivity extends AppCompatActivity implements OnRemoteOperationListener, OnDatatransferProgressListener {

    private OwnCloudClient mClient;
    private Handler mHandler;
    private dbHandler dbHandler;
//    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        setContentView(R.layout.activity_backup);
//        progressDialog = new ProgressDialog(this);

        dbHandler = new dbHandler(this, null);
        if (dbHandler.isBackupSet() == 0) {
            Intent intent = new Intent(this, BackupSetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            mClient = OwnCloudClientFactory.createOwnCloudClient(Uri.parse("http://127.0.0.1:9080/owncloud"), this, true);
            mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials("sidzi", "qazxsw"));

//            ReadRemoteFolderOperation readRemoteFolderOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR);
//            readRemoteFolderOperation.execute(mClient, this, mHandler);

            UploadRemoteFileOperation uploadOperation = new UploadRemoteFileOperation(Environment.getExternalStorageDirectory() + "/Download/03_Late_Nights.mp3", "/LOL/", "audio/mp3");
            uploadOperation.addDatatransferProgressListener(this);
            uploadOperation.execute(mClient, this, mHandler);

//            CreateRemoteFolderOperation createOperation = new CreateRemoteFolderOperation(FileUtils.PATH_SEPARATOR + "LOL", false);
//            createOperation.execute(mClient, this, mHandler);
//        auto_backup();
        }
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
            }
        }

    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (operation instanceof UploadRemoteFileOperation) {
            if (result.isSuccess()) {
                finish();
            }
        }
        if (operation instanceof ReadRemoteFolderOperation) {
            if (result.isSuccess()) {
                finish();
            }
        }
        if (operation instanceof CreateRemoteFolderOperation) {
            if (result.isSuccess()) {
                finish();
            }
        }
    }

    @Override
    public void onTransferProgress(final long progressRate, final long totalTransferredSoFar, long totalToTransfer, String fileName) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                progressDialog.setProgress((int) totalTransferredSoFar);
            }
        });
    }
}
