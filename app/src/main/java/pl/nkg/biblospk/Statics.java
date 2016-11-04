package pl.nkg.biblospk;


import org.acra.ACRA;

public class Statics {
    public static GlobalState sGlobalState;

    public static void sendParseErrorReport(String message, String fragment, String parentFragment) {
        if (sGlobalState == null || !sGlobalState.getPreferencesProvider().isParseBugReport()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Message: ").append(message).append("\n\n");
        sb.append("Fragment: |||").append(fragment).append("|||\n\n");
        sb.append("Parent fragment: |!|").append(parentFragment).append("|!|");

        Exception e = new Exception(sb.toString());
        ACRA.getErrorReporter().handleSilentException(e);
    }
}
