package com.ihs.homeconnect.helpers;

public enum services {
    dm(6800, "Download Manager"),
    backup(80, "Backup"),
    homebase(80, "Dashboard"),
    vs(8081, "WebCam"),
    xmpp(5222, "Chat"),
    voip(64738, "Audio Call"),
    printer(631, "Print");
    public final int port;
    public final String name;
    public final int lport;

    services(int port, String name) {
        this.port = port;
        this.name = name;
        this.lport = 9000 + port;
    }
}