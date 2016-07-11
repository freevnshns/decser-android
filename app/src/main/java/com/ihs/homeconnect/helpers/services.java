package com.ihs.homeconnect.helpers;

public enum services {
    DownloadsManager(6800),
    Backup(42000),
    HomeBase(10000),
    VideoSurveillance(8081),
    Printing(631);
    public final int port;

    services(int port) {
        this.port = port;
    }
}