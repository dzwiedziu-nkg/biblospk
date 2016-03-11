package pl.nkg.biblospk.client;

import org.apache.commons.io.IOUtils;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

public class WebClient {

    public static void initCookieHandler() {
        CookieHandler.setDefault(new CookieManager());
    }

    public static boolean checkCookieHandlerInitialized() {
        return CookieHandler.getDefault() != null;
    }

    public static FullPageResponse fetchPage(URL url) throws IOException, ParseException {
        return fetchPage(url, null);
    }

    public static FullPageResponse fetchPage(URL url, String postContent) throws IOException, ParseException {
        return fetchPage(url, postContent, FetchFullPage.getInstance());
    }

    public static<T> T fetchPage(URL url, String postContent, OnWebDataReceived<T> onWebDataReceived) throws IOException, ParseException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        if (postContent != null) {
            connection.setDoOutput(true);
            connection.setFixedLengthStreamingMode(postContent.length());

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(postContent);
            writer.flush();
            writer.close();
            os.close();
        }

        connection.connect();

        try {
            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            T t = onWebDataReceived.performWebData(connection.getResponseCode(), connection.getResponseMessage(), rd);
            rd.close();
            return t;
        } finally {
            connection.disconnect();
        }
    }

    public interface OnWebDataReceived<T> {
        T performWebData(int responseCode, String responseMessage, BufferedReader webData) throws IOException;
    }

    public static class FetchFullPage implements OnWebDataReceived<FullPageResponse> {
        private static FetchFullPage INSTANCE;

        public static FetchFullPage getInstance() {
            if (INSTANCE == null) {
                INSTANCE = new FetchFullPage();
            }
            return INSTANCE;
        }

        @Override
        public FullPageResponse performWebData(int responseCode, String responseMessage, BufferedReader webData) throws IOException {
            return new FullPageResponse(responseCode, responseMessage, IOUtils.toString(webData));
        }
    }

    public static class FullPageResponse {
        final private int mResponseCode;
        final private String mResponseMessage;
        final private String mContent;

        public FullPageResponse(int responseCode, String responseMessage, String content) {
            mResponseCode = responseCode;
            mResponseMessage = responseMessage;
            mContent = content;
        }

        public int getResponseCode() {
            return mResponseCode;
        }

        public String getResponseMessage() {
            return mResponseMessage;
        }

        public String getContent() {
            return mContent;
        }
    }
}
