package com.ihs.homeconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ihs.homeconnect.helpers.dbHandler;
import com.ihs.homeconnect.helpers.verticalSpaceDecorationHelper;
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
import com.owncloud.android.lib.resources.files.RemoteFile;
import com.owncloud.android.lib.resources.files.UploadRemoteFileOperation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class BackupActivity extends AppCompatActivity implements OnRemoteOperationListener, OnDatatransferProgressListener {

    final static ArrayList<uploadJob> uploadJobs = new ArrayList<>();
    private OwnCloudClient mClient;
    private Handler mHandler;
    private dbHandler dbHandler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHandler = new dbHandler(this, null);

        mHandler = new Handler();
        setContentView(R.layout.activity_backup);

        mClient = OwnCloudClientFactory.createOwnCloudClient(Uri.parse("http://127.0.0.1:9080/owncloud"), this, true);
        String user_email = dbHandler.getUserEmail();

        mClient.setCredentials(OwnCloudCredentialsFactory.newBasicCredentials(user_email.substring(0, user_email.lastIndexOf("@")), dbHandler.getUserPassword()));

        RecyclerView mRecyclerView;
        RecyclerView.Adapter mAdapter;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) findViewById(R.id.rvBackedUpFiles);
        mLayoutManager = new LinearLayoutManager(this);

        assert mRecyclerView != null;
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);


        mAdapter = new fileViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new verticalSpaceDecorationHelper(this));
        }

        Button bShowBkpFiles = (Button) findViewById(R.id.bShowBkpFiles);
        assert bShowBkpFiles != null;
        bShowBkpFiles.setVisibility(View.GONE);
        bShowBkpFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        FloatingActionButton floatingActionUploadButton = (FloatingActionButton) findViewById(R.id.fabAddBackupDirectory);
        if (floatingActionUploadButton != null) {
            floatingActionUploadButton.setImageResource(R.drawable.ic_add);
            floatingActionUploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(BackupActivity.this, BackupSetupActivity.class);
                    startActivity(intent);
                }
            });
        }
        readFilesOnServer();
    }

    private void auto_backup(ArrayList<Object> remoteList) {
        ArrayList<String> auto_bkp_paths = dbHandler.getBackupPaths(1);
        HashSet<String> bkd_paths = new HashSet<>();
        for (Object o :
                remoteList) {
            RemoteFile remoteFile = (RemoteFile) o;
            String remote_path;
            remote_path = remoteFile.getRemotePath();
            remote_path = remote_path.substring(remote_path.lastIndexOf(FileUtils.PATH_SEPARATOR) + 1);
            bkd_paths.add(remote_path);
        }
        for (String path : auto_bkp_paths) {
            File dir = new File(path);
            File[] list = dir.listFiles();
            String local_path;
            for (File aList : list) {
                local_path = aList.getAbsolutePath();
                local_path = local_path.substring(local_path.lastIndexOf(FileUtils.PATH_SEPARATOR) + 1);
                if (!bkd_paths.contains(local_path)) {
                    String mime;
                    String ext = MimeTypeMap.getFileExtensionFromUrl(aList.getAbsolutePath());
                    if (ext != null && !ext.equals("")) {
                        mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                        String remoteBackUpFolderPath = FileUtils.PATH_SEPARATOR + "auto_bkp" + FileUtils.PATH_SEPARATOR;
                        uploadJobs.add(new uploadJob(aList.getAbsolutePath(), remoteBackUpFolderPath + local_path, mime));
                    }
                }
            }
        }
        if (!uploadJobs.isEmpty()) {
            progressDialog = new ProgressDialog(BackupActivity.this);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setTitle("Backing up");
            progressDialog.setProgress(0);
            progressDialog.show();
            uploadFilesToServer();

        }
    }

    private void readFilesOnServer() {
        ReadRemoteFolderOperation readRemoteFolderOperation = new ReadRemoteFolderOperation(FileUtils.PATH_SEPARATOR + "auto_bkp" + FileUtils.PATH_SEPARATOR);
        readRemoteFolderOperation.execute(mClient, this, mHandler);
    }

    private void uploadFilesToServer() {
        uploadJob upJb = uploadJobs.remove(0);
        String filepath = upJb.getPath();
        String remotePath = upJb.getRemotePath();
        String mime = upJb.getMime();
        progressDialog.setMessage(remotePath);
        progressDialog.setProgress(0);
        UploadRemoteFileOperation uploadOperation = new UploadRemoteFileOperation(filepath, remotePath, mime);
        uploadOperation.addDatatransferProgressListener(this);
        uploadOperation.execute(mClient, this, mHandler);
    }

    @Override
    public void onRemoteOperationFinish(RemoteOperation operation, RemoteOperationResult result) {
        if (operation instanceof UploadRemoteFileOperation) {
            if (result.isSuccess()) {
                if (!uploadJobs.isEmpty())
                    uploadFilesToServer();
                else {
                    progressDialog.dismiss();
                    Toast.makeText(BackupActivity.this, "Backup Completed", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            if (operation instanceof ReadRemoteFolderOperation) {
                if (result.isSuccess()) {
                    auto_backup(result.getData());
                } else {
                    if (result.getCode().toString().equalsIgnoreCase("FILE_NOT_FOUND")) {
                        CreateRemoteFolderOperation createOperation = new CreateRemoteFolderOperation(FileUtils.PATH_SEPARATOR + "auto_bkp", false);
                        createOperation.execute(mClient, this, mHandler);
                    }
                }
            } else {
                if (operation instanceof CreateRemoteFolderOperation) {
                    if (result.isSuccess()) {
                        readFilesOnServer();
                    }
                }
            }
        }
    }

    @Override
    public void onTransferProgress(final long progressRate, final long totalTransferredSoFar, final long totalToTransfer, String fileName) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setProgress((int) (totalTransferredSoFar * 100 / totalToTransfer));
            }
        });
    }

    class uploadJob {
        String path;
        String remotePath;
        String mime;

        public uploadJob(String path, String remotePath, String mime) {
            this.path = path;
            this.remotePath = remotePath;
            this.mime = mime;
        }

        public String getPath() {
            return path;
        }

        public String getRemotePath() {
            return remotePath;
        }

        public String getMime() {
            return mime;
        }
    }

    private class fileViewAdapter extends RecyclerView.Adapter<fileViewAdapter.ViewHolder> {
        private ArrayList<String> fileList;

        public fileViewAdapter() {
            super();
            fileList = new ArrayList<>();
            fileList = dbHandler.getBackupPaths(1);
        }

        @Override
        public fileViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_row_files, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(fileViewAdapter.ViewHolder holder, int position) {
            holder.tvDirPath.setText(fileList.get(position));
        }

        @Override
        public int getItemCount() {
            return fileList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvDirPath;

            public ViewHolder(View view) {
                super(view);
                this.tvDirPath = (TextView) view.findViewById(R.id.tvFileNameOrPath);
            }
        }
    }
}
