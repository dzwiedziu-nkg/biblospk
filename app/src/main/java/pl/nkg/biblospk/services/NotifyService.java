package pl.nkg.biblospk.services;

import org.apache.commons.lang3.StringUtils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.ParseException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import pl.nkg.biblospk.GlobalState;
import pl.nkg.biblospk.MyApplication;
import pl.nkg.biblospk.R;
import pl.nkg.biblospk.data.Book;
import pl.nkg.biblospk.events.AccountDownloadedEvent;
import pl.nkg.biblospk.ui.MainActivity;

public class NotifyService extends Service {

    private static final int NOTIFY_ID = 1;

    public static void startService(Context context) {
        context.startService(new Intent(context, NotifyService.class));
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, NotifyService.class));
    }

    private GlobalState mGlobalState;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGlobalState = ((MyApplication)getApplication()).getGlobalState();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateNotify();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventBackgroundThread(AccountDownloadedEvent event) {
        updateNotify();
    }

    private void updateNotify() {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mGlobalState.isBookListDownloaded()) {

            Date toDay = new Date();

            int expired = mGlobalState.getAccount().countOfExpired(toDay);
            int ready = mGlobalState.getAccount().countOfReady();

            CharSequence title;
            CharSequence content;

            StringBuilder bigText = new StringBuilder();

            if (expired > 0 && ready > 0) {
                title = getText(R.string.notify_title_expired_and_ready);
                content = getResources().getQuantityString(R.plurals.notify_content_expired_and_ready_p1, expired, expired)
                        + " "
                        + getResources().getQuantityString(R.plurals.notify_content_expired_and_ready_p2, ready, ready);
            } else if (expired > 0) {
                title = getText(R.string.notify_title_expired);
                content = getResources().getQuantityString(R.plurals.notify_content_expired, expired, expired);
            } else if (ready > 0) {
                title = getText(R.string.notify_title_ready);
                content = getResources().getQuantityString(R.plurals.notify_content_ready, ready, ready);
            } else {
                mNotificationManager.cancel(NOTIFY_ID);
                return;
            }

            if (expired > 0) {
                bigText.append(getText(R.string.notify_section_expired));

                Book[] books = mGlobalState.getAccount().getSortedBookArray(toDay);
                for (Book book : books) {
                    if (book.checkBookPriority(toDay) == 0) {
                        continue;
                    }

                    bigText.append("\n - ")
                        .append(Book.DUE_DATE_FORMAT.format(book.getDueDate()))
                        .append(" - ")
                        .append(StringUtils.abbreviate(book.getTitle(), 20));
                }
            }

            if (ready > 0) {
                if (expired > 0) {
                    bigText.append("\n\n");
                }
                bigText.append(getText(R.string.notify_section_ready));
                // TODO: list of books ready to lend
            }

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(android.R.drawable.stat_notify_sync) // TODO: make icon
                            .setContentTitle(title)
                            .setContentText(content)
                            .setAutoCancel(false)
                            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                            .setOngoing(true);

            Intent resultIntent = new Intent(this, MainActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(mBuilder);
            style.setBigContentTitle(title);
            style.setSummaryText(content);
            style.bigText(bigText.toString());
            mBuilder.setStyle(style);

            mNotificationManager.notify(NOTIFY_ID, mBuilder.build());
        } else {
            mNotificationManager.cancel(NOTIFY_ID);
        }
    }
}