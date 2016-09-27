package com.ihs.homeconnect.helpers;

public enum services {
    dm(6800, "Download Manager", "ic_dm"),
    backup(80, "Backup", "ic_backup"),
    vs(80, "WebCam", "ic_vs"),
    xmpp(5222, "Messaging", "ic_messaging"),
    print(631, "Printing", "ic_print"),
    power(80, "Power Button", "ic_power"),
    sftp(22, "Download files", "ic_download");
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