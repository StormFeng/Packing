package com.lida.packing.utils;

import com.apkfuns.logutils.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/05/26.
 */

public class DBHelper {
    private static Connection conn = null;
    private static final String ServerName = "192.168.0.122";
    private static final String ServerPort = "1433";
    private static final String DatabaseName = "c6";
    private static final String ServerUserName = "sa";
    private static final String ServerPassword = "123";
    static {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn(){
        if(conn==null){
            try {
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+ServerName+":"+ServerPort+";"+
                        "DatabaseName="+DatabaseName, ServerUserName, ServerPassword);
                LogUtils.e("连接成功!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
