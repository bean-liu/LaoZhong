package com.qixiao.laozhong;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.net.HttpURLConnection;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvResult;
    EditText etUrl;
    EditText etCookies;
    EditText etDomain;
    HashMap<String, String> mapCookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppClient.initPersistentCookieStore(this);
        initView();
        addListener();
    }

    private void initView() {
        tvResult = (TextView) this.findViewById(R.id.result);
        etUrl = (EditText) this.findViewById(R.id.url);
        etCookies = (EditText) this.findViewById(R.id.cookies);
        etCookies.setHint("cookies eg k1=v1&k2=v2...");
        etDomain = (EditText) this.findViewById(R.id.domain);
    }

    private void addListener() {
        this.findViewById(R.id.reset).setOnClickListener(this);
        this.findViewById(R.id.submit).setOnClickListener(this);
        tvResult.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyText(tvResult.getText().toString());
                return true;
            }
        });
    }

    public void copyText(String str) {
        // TODO Auto-generated method stub
        ClipboardManager cm = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(str);
        toast("内容已经复制到剪贴板");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.reset:
                reset();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void reset() {
        if(mapCookie != null && mapCookie.keySet().size() != 0){
            String domain = etDomain.getText().toString();
            if(!domain.isEmpty()){
                AppClient.deleteCookie(mapCookie, domain);
            } else {
                toast("domain is null");
            }
        }

        etUrl.setText("");
        etCookies.setText("");
        etDomain.setText("");
        tvResult.setText("");
    }

    private void submit() {
        mapCookie = getCookies();
        if(mapCookie != null && mapCookie.keySet().size() != 0){
            String domain = etDomain.getText().toString();
            if(!domain.isEmpty()){
                AppClient.addCookie(mapCookie, domain);
            } else {
                toast("domain is null");
            }
        }

        String url = etUrl.getText().toString();

        if(!url.isEmpty()){
            AppClient.get(url, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(statusCode == HttpURLConnection.HTTP_OK){
                        tvResult.setText(new String(responseBody));
                    } else {
                        toast("请求失败");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    toast("Failure " + error.toString());
                }
            });
        } else {
            toast("url is null");
        }
    }

    private HashMap<String, String> getCookies() {

        HashMap<String, String> map = new HashMap<>();
        String cookies = etCookies.getText().toString();

        if(cookies.isEmpty()) {
            return null;
        }

        String arrayCookies[] = cookies.split("&");

        for(int i = 0; i < arrayCookies.length; i++){
            String kv[] = arrayCookies[i].trim().split("=");
            if(kv.length == 2){
                map.put(kv[0].trim(),kv[1].trim());
            }
        }

        return map;
    }

    private void toast(String str){
        Toast.makeText(this, str,  Toast.LENGTH_SHORT).show();
    }
}
