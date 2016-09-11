package com.ihs.homeconnect.helpers;

public enum services {
    dm(6800, "Download Manager", "ic_dm"),
    backup(80, "Backup", "ic_backup"),
    homebase(80, "Dashboard", "ic_hb"),
    vs(8081, "WebCam", "ic_vs"),
    xmpp(5222, "Messaging", "ic_messaging");
    public final int port;
    public final String name;
    public final int lport;
    public final String icon;

    services(int port, String name, String icon) {
        this.port = port;
        this.name = name;
        this.lport = 9000 + port;
        this.icon = icon;
    }
}