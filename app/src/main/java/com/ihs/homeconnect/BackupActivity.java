package com.ihs.homeconnect;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.owncloud.android.lib.common.OwnCloudClient;
import com.owncloud.android.lib.common.OwnCloudClientFactory;
import com.owncloud.android.lib.common.OwnCloudCredentialsFactory;
import com.owncloud.android.lib.common.operations.OnRemoteOperationListener;
import com.owncloud.android.lib.common.operations.RemoteOperation;
import com.owncloud.android.lib.common.operations.RemoteOperationResult;
import com.owncloud.android.lib.resources.files.FileUtils;
import com.owncloud.android.lib.resources.files.ReadRemoteFolderOperation;

public class BackupActivity extends AppCompatActivity {

    private OwnCloudClient mClient;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);
        mHandler = new Handler();
        mClient = OwnCloudClientFactory.createOwnCloudClient(Uri.parse("http://127.0.0.1:9080/owncloud"), this, true);
        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials("sidzi", "qazxsw"));
        ReadRemoteFolderOperation readRemoteFolderOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR);
        readRemoteFolderOperation.execute(mClient, new OnRemoteOperationListener() {
            @Override
            public void onRemoteOperationFinish(RemoteOperation caller, RemoteOperationResult result) {

            }
        }, mHandler);
    }
}
