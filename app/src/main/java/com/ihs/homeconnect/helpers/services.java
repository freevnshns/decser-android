package com.ihs.homeconnect.helpers;

public enum services {
    Torrent(6800),
    MediaServer(32400),
    Backup(42000),
    HomeBase(10000),
    VideoSurveillance(8081),
    Printing(631);
    public final int port;

    services(int port) {
        this.port = port;
    }
}