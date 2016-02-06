package com.ssa.transact.transact;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;

/**
 * Created by bogdan on 16/02/05.
 */
public class Singleton {

    private static final Singleton s = new Singleton();

    public String getUsername() {
        return username;
    }

    private String username = null;
    private String password = null;

    private CookieManager cookieManager = null;

    private ServiceConnection serviceConnection = null;

    private TransactBinder binder = null;

    private Singleton() {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (TransactBinder)service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                binder = null;
            }
        };
    };

    public static Singleton getInstance() {
        return s;
    }

    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    public TransactBinder getBinder() {
        return binder;
    }

    public void saveSession(List<String> cookiesHeader, String username, String password) {
        for (String cookie : cookiesHeader) {
            cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
        }
        this.username = username;
        this.password = password;
    }

    public String getSession() {
        if (cookieManager.getCookieStore().getCookies().size() > 0) {
            return TextUtils.join(";", cookieManager.getCookieStore().getCookies());
        }
        return "";
    }

}
