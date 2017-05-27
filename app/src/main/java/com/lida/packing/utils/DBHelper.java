package com.lida.packing.utils;

import android.content.Context;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/05/26.
 */

public class DBHelper {
    public static Connection conn = null;
    static {
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConn(Context context){
        String serverName = (String) SharedPreferencesUtils.getParam(context,"serverName","");
        String serverPort = (String) SharedPreferencesUtils.getParam(context,"serverPort","");
        String databaseName = (String) SharedPreferencesUtils.getParam(context,"databaseName","");
        String userName = (String) SharedPreferencesUtils.getParam(context,"userName","");
        String password = (String) SharedPreferencesUtils.getParam(context,"password","");
        if(conn==null){
            try {
                conn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+serverName+":"+serverPort+";"+
                        "DatabaseName="+databaseName, userName, password);
                LogUtils.e("连接成功!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }
}
