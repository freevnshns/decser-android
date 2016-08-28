package com.ihs.homeconnect.helpers;

public enum services {
    downloadmanager(6800, "DownloadManager"),
    backup(80, "Backup"),
    homebase(80, "Dashboard"),
    videosurveillance(8081, "WebCam"),
    xmpp(5222, "Chat"),
    voip(64738, "Audio Call"),
    printer(631, "Print");
    public final int port;
    public final String name;

    services(int port, String name) {
        this.port = port;
        this.name = name;
    }
}