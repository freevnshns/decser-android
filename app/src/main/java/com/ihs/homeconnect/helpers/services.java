package com.ihs.homeconnect.helpers;

public enum services {
    downloadmanager(6800),
    backup(80),
    homebase(80),
    videosurveillance(8081),
    xmpp(5222),
    printer(631);
    public final int port;

    services(int port) {
        this.port = port;
    }
}