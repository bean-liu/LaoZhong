package com.qixiao.laozhong;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.impl.cookie.BasicClientCookie;

import java.util.Map;
import java.util.Set;

/**
 * Created by 刘军 on 2015/6/16.
 */
public class AppClient {

    private static AsyncHttpClient asyncClient = new AsyncHttpClient();
    private static PersistentCookieStore cookieStore;

    // 持久化cookies
    public static void initPersistentCookieStore(Context context) {
        setCookieStore(context, asyncClient);
    }

    public static void setCookieStore(Context context, AsyncHttpClient client){
        cookieStore = new PersistentCookieStore(context);
        client.setCookieStore(cookieStore);

    }

    public static void addCookie(Map<String, String> map, String domain){

        if(cookieStore == null){
            return ;
        }

        for(String str : map.keySet()){
            BasicClientCookie newCookie = new BasicClientCookie(str, map.get(str));
            //newCookie.setVersion(1);
            newCookie.setDomain(domain);
            newCookie.setPath("/");
            cookieStore.addCookie(newCookie);
        }

        asyncClient.setUserAgent("laozhong");

    }

    public static void deleteCookie(Map<String, String> map, String domain){

        if(cookieStore == null){
            return ;
        }

        for(String str : map.keySet()){
            BasicClientCookie newCookie = new BasicClientCookie(str, map.get(str));
            //newCookie.setVersion(1);
            newCookie.setDomain(domain);
            newCookie.setPath("/");
            cookieStore.deleteCookie(newCookie);
        }

    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        asyncClient.post(url, params, responseHandler);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
        // asyncClient.setUserAgent("hello i'm laozhong");
        asyncClient.get(url, params, responseHandler);
    }

    public static AsyncHttpClient createClient(Context context){
        AsyncHttpClient client = new AsyncHttpClient();
        setCookieStore(context, client);
        return client;
    }

}
