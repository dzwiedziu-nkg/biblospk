package pl.nkg.biblospk.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

public class UrlQuery {

    private List<ValuePair> mValuePairList = new LinkedList<>();

    public UrlQuery add(String name, String value) {
        mValuePairList.add(new ValuePair(name, value));
        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (ValuePair pair : mValuePairList)
        {
            if (first)
                first = false;
            else
                result.append("&");

            try {
                result.append(URLEncoder.encode(pair.name, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.value, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        return result.toString();
    }

    public static class ValuePair {
        public final String name;
        public final String value;

        public ValuePair(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
