package com.vave.getbike.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import com.vave.getbike.syncher.BaseSyncher;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPUtils {

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.NO_WRAP | Base64.NO_CLOSE | Base64.NO_PADDING);
        System.out.println(" JPEG Bitmap size : " + temp.length() + " JPEG Byte array length :" + b.length);
        return temp;
    }

    public static Bitmap getBitmapFromCameraData(Intent data, Context context) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inJustDecodeBounds = true;
        int sample = 32;
        BitmapFactory.decodeFile(picturePath, option);
        if (option.outHeight > 960 || option.outWidth > 200) {
            if (option.outHeight > option.outWidth) {
                sample = option.outHeight / 200;
            } else {
                sample = option.outWidth / 200;
            }
        }
        option.inSampleSize = sample;
        option.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(picturePath, option);
    }

    public static void ignoreHttpsChecking() throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
            }
        }};

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static String getDataFromServer(String urlData, String requestedMethod)
            throws MalformedURLException, IOException, ProtocolException {

        return getDataFromServer(urlData, requestedMethod, null);
    }

    public static String getDataFromServer(String urlData, String requestedMethod, String content)
            throws MalformedURLException, IOException, ProtocolException {
        URL url = new URL(urlData);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(requestedMethod);
        con.setConnectTimeout(60000);
        con.setReadTimeout(60000);
        if (BaseSyncher.getAccessToken() != null) {
            con.setRequestProperty("Authorization", BaseSyncher.getAccessToken());
            System.out.println("BaseSyncher AccessToken = " + BaseSyncher.getAccessToken());
        }
        if (content != null) {
            byte[] postDataBytes = content.getBytes("UTF-8");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);
            con.setDoInput(true);
            OutputStream outputStream = con.getOutputStream();
            outputStream.write(postDataBytes);
            outputStream.flush();
            outputStream.close();
        }
        con.connect();
        StringBuffer response = new StringBuffer();
        if (con.getResponseCode() != 200 && con.getResponseCode() != 201) {
            readContent(response, con.getErrorStream());
        } else {
            readContent(response, con.getInputStream());
        }
        System.out.println("HTTP Response received for " + urlData + "\n" + response.toString());
        return response.toString();
    }

    public static void readContent(StringBuffer response, InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine).append("\n");
        }
        reader.close();
    }

    public static JSONObject getJSONFromUrl(String url) throws IOException {
        InputStream inputStream = null;
        JSONObject jsonObject = null;
        String json = "";
        HttpURLConnection urlConnection = null;
        try {
            URL url1 = new URL(url);
            urlConnection = (HttpURLConnection) url1.openConnection();
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(60000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            inputStream.close();
            json = sb.toString();
            jsonObject = new JSONObject(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
