package com.ihs.homeconnect.helpers;

import android.os.AsyncTask;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NoCache;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class downloadManagerHandler extends AsyncTask<String, Void, Object> {
    File cacheDir;

    public downloadManagerHandler(File cacheDir) {
        this.cacheDir = cacheDir;
    }

    @Override
    protected Object doInBackground(String... params) {
        final Object result;
        Cache cache = new NoCache();
        Network network = new BasicNetwork(new HurlStack());
        RequestQueue rpcQueue = new RequestQueue(cache, network);
        rpcQueue.start();
        JSONObject rpcRequest = new JSONObject();
        try {
            rpcRequest.accumulate("method", params[0]);
            if (params.length == 2) {
                if (params[0].equals("aria2.addUri")) {
                    JSONArray uris = new JSONArray();
                    ArrayList<Object> uri = new ArrayList<>();
                    uri.add(params[1]);
                    uris.put(uri);
                    rpcRequest.accumulate("parameters", uris);
                } else {
                    ArrayList<Object> par = new ArrayList<>();
                    par.add(params[1]);
                    rpcRequest.accumulate("parameters", par);
                }
            }
            if (params.length == 3) {
                ArrayList<Object> parm = new ArrayList<>();
                parm.add(Integer.valueOf(params[1]));
                parm.add(Integer.valueOf(params[2]));
                rpcRequest.accumulate("parameters", parm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, "http://127.0.0.1:" + String.valueOf(services.dm.lport) + "/jsonrpc", rpcRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.print(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        return null;
//        try {
//            JSONRPC2Session dmSession = new JSONRPC2Session(new URL("http://127.0.0.1:" + String.valueOf(services.dm.lport) + "/jsonrpc"));
//            JSONRPC2Request request;
//            result = new net.minidev.json.JSONArray();
//            request = new JSONRPC2Request(params[0], "aria2c");
//            if (params.length == 2) {
//                if (params[0].equals("aria2.addUri")) {
//                    ArrayList<Object> uris = new ArrayList<>();
//                    ArrayList<String> uri = new ArrayList<>();
//                    uri.add(params[1]);
//                    uris.add(uri);
//                    request.setPositionalParams(uris);
//                } else {
//                    ArrayList<Object> par = new ArrayList<>();
//                    par.add(params[1]);
//                    request.setPositionalParams(par);
//                }
//            }
//            if (params.length == 3) {
//                ArrayList<Object> parm = new ArrayList<>();
//                parm.add(Integer.valueOf(params[1]));
//                parm.add(Integer.valueOf(params[2]));
//                request.setPositionalParams(parm);
//            }
//            try {
//                Object tempResult = dmSession.send(request);
//                result = ((JSONRPC2Response) tempResult).getResult();
//            } catch (JSONRPC2SessionException e) {
//                e.printStackTrace();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
    }
}
