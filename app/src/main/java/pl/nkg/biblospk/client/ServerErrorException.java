package pl.nkg.biblospk.client;

public class ServerErrorException extends Exception {
    final private int mResponseCode;
    final private String mResponseMessage;
    final private String mContent;

    public ServerErrorException(int responseCode, String responseMessage, String content) {
        mResponseCode = responseCode;
        mResponseMessage = responseMessage;
        mContent = content;
    }

    public ServerErrorException(String detailMessage, int responseCode, String responseMessage, String content) {
        super(detailMessage);
        mResponseCode = responseCode;
        mResponseMessage = responseMessage;
        mContent = content;
    }

    public ServerErrorException(String detailMessage, Throwable throwable, int responseCode, String responseMessage, String content) {
        super(detailMessage, throwable);
        mResponseCode = responseCode;
        mResponseMessage = responseMessage;
        mContent = content;
    }

    public ServerErrorException(Throwable throwable, int responseCode, String responseMessage, String content) {
        super(throwable);
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
