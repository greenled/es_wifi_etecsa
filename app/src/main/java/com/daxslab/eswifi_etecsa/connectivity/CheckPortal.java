package com.daxslab.eswifi_etecsa.connectivity;

import android.content.Context;
import android.os.AsyncTask;

import com.daxslab.eswifi_etecsa.R;
import com.daxslab.eswifi_etecsa.utils.NotificationUtils;
import com.daxslab.eswifi_etecsa.utils.WifiUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

/**
 * Created by cccaballero on 20/09/18.
 */
class CheckPortal extends AsyncTask<String, Void, Boolean> {


    private Context mContext;
    private boolean isWifiOk;

    CheckPortal(Context mContext){
        super();
        this.mContext = mContext;
        this.isWifiOk = true;

    }

    protected Boolean doInBackground(String... params) {

        String ssid = WifiUtils.getCurrentSSID(mContext);

        if (Objects.equals(WifiReceiver.WIFI_ETECSA_SSID, ssid)) {
            boolean isConnectionOk =true;
            try {
                isConnectionOk = checkConnection(params[0]);
            }catch (SSLHandshakeException ex){
                NotificationUtils.createNotification(mContext, WifiReceiver.SSL_WARNING_NOTIFICATION_ID, mContext.getString(R.string.ssl_warning), mContext.getString(R.string.not_valid_ssl));
                this.isWifiOk = false;
            }catch (Exception ex){
                NotificationUtils.createNotification(mContext, WifiReceiver.CANT_VERIFY_SSL_WARNING_NOTIFICATION_ID, mContext.getString(R.string.cant_verify_ssl_warning), mContext.getString(R.string.cant_verify_ssl));
                this.isWifiOk = false;
            }

            return isConnectionOk;
        }
        return null;
    }

    private boolean checkConnection(String checkUrl) throws InterruptedException, IOException {
        HttpURLConnection response = null;
            TimeUnit.SECONDS.sleep(3);
            URL url = new URL(checkUrl);
            response = (HttpURLConnection) url.openConnection();
            response.setRequestMethod("GET");
            response.connect();
            checkResponse(response);
            return true;
    }

    private void checkResponse(HttpURLConnection response) throws IOException {
        int statusCode = response.getResponseCode();
        if (HttpURLConnection.HTTP_OK != statusCode) {
            throw new IOException("HttpStatus: " + statusCode);
        }
    }

}