package com.ihs.homeconnect.helpers;

import android.os.AsyncTask;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class downloadManagerHandler extends AsyncTask<String, Void, Object> {

    @Override
    protected Object doInBackground(String... params) {
        Object result = null;
        try {
            JSONRPC2Session dmSession = new JSONRPC2Session(new URL("http://127.0.0.1:" + String.valueOf(services.dm.lport) + "/jsonrpc"));
            JSONRPC2Request request;
            result = new net.minidev.json.JSONArray();
            request = new JSONRPC2Request(params[0], "aria2c");
            if (params.length == 1) {


            }
            if (params.length == 2) {
                ArrayList<Object> uris = new ArrayList<>();
                ArrayList<String> uri = new ArrayList<>();
                uri.add(params[1]);
                uris.add(uri);
                request.setPositionalParams(uris);
            }
            if (params.length == 3) {
                ArrayList<Object> parm = new ArrayList<>();
                parm.add(Integer.valueOf(params[1]));
                parm.add(Integer.valueOf(params[2]));
                request.setPositionalParams(parm);
            }
            try {
                Object tempResult = dmSession.send(request);
                result = ((JSONRPC2Response) tempResult).getResult();
            } catch (JSONRPC2SessionException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
