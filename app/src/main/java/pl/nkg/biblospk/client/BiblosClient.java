package pl.nkg.biblospk.client;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import pl.nkg.biblospk.data.Account;
import pl.nkg.biblospk.data.Book;

public class BiblosClient {

    private static final String TAG = BiblosClient.class.getSimpleName();

    private static final URL URL_LOGIN;
    private static final String STRING_INVALID_CREDENTIALS = "<p>Podałeś nieprawidłowy login lub hasło. Spróbuj ponownie. Pamiętaj, że system rozróżnia wielkość liter.</p>";

    private static final String OPEN_NAME = "<span class=\"loggedinusername\">";
    private static final String CLOSE_NAME = "</span></a></p></li>";

    private static final String OPEN_DUE = "<a href=\"#opac-user-fines\">Należności (";
    private static final String CLOSE_DUE = ")</a></li>";

    private static final String OPEN_LENDS = "<li><a href=\"#\">Wypożyczenia</a></li>";
    private static final String CLOSE_LENDS = "</table>";

    private static final String OPEN_ROW = " <td class=\"title\">";
    private static final String CLOSE_ROW = "</tr>";

    private static final String OPEN_BORROWER = " <input type=\"hidden\" name=\"borrowernumber\" value=\"";
    private static final String CLOSE_BORROWER = "\">";

    private static final String OPEN_BOOK_TITLE = " <a class=\"title\" href=\"/cgi-bin/koha/opac-detail.pl?biblionumber=";
    private static final String CLOSE_BOOK_TITLE = "</a>";

    private static final String OPEN_BOOK_AUTHOR = " <span class=\"item-details\">";
    private static final String CLOSE_BOOK_AUTHOR = "</span>";

    private static final String OPEN_BOOK_DUE_DATE = " <span class=\"tdlabel\">Termin zwrotu:</span>";
    private static final String CLOSE_BOOK_DUE_DATE = "</span>";

    private static final String OPEN_BOOK_BARCODE = " <span class=\"tdlabel\">Kod kreskowy:</span>";
    private static final String CLOSE_BOOK_BARCODE = "</td>";

    private static final String OPEN_BOOK_SIGNATURE = " <span class=\"tdlabel\">Sygnatura:</span>";
    private static final String CLOSE_BOOK_SIGNATURE = "</td>";

    private static final String OPEN_BOOK_ITEM = " <input type=\"checkbox\" name=\"item\" value=\"";
    private static final String CLOSE_BOOK_ITEM = "\"/>";

    private static final String OPEN_BOOK_RENEWALS = "<span class=\"renewals\">(zostało ";
    private static final String CLOSE_BOOK_RENEWALS = " prolongat)</span>";

    private static final String STRING_CANT_PROLONG = "Brak możliwości prolongaty";

    private static final String OPEN_RESERVED = "<!-- RESERVES TABLE ROWS -->";
    private static final String CLOSE_RESERVED = "</table>";

    private static final String OPEN_BOOK_REQUEST_AUTHOR = "</a>";
    private static final String CLOSE_BOOK_REQUEST_AUTHOR = "</td>";

    private static final String OPEN_BOOK_REQUEST_DATE = "<span class=\"tdlabel\">Data zamówienia:</span>";
    private static final String CLOSE_BOOK_REQUEST_DATE = "</span>";

    private static final String OPEN_BOOK_REQUEST_DUE_DATE = "<span class=\"tdlabel\">Data ważności:</span>";
    private static final String CLOSE_BOOK_REQUEST_DUE_DATE = "</span>";
    private static final String STRING_BOOK_REQUEST_NEVER_EXPIRED = "Nigdy nie wygasa";

    private static final String OPEN_BOOK_QUEUE = "<span title=\"Rezerwacja - brak wolnych egzemplarzy, numer w kolejce: ";
    private static final String CLOSE_BOOK_QUEUE = "\">";

    private static final String OPEN_BOOK_RENTAL = "<span class=\"tdlabel\">Miejsce odbioru:</span>";
    private static final String CLOSE_BOOK_RENTAL = "</td>";

    static {
        try {
            URL_LOGIN = new URL("http://koha.biblos.pk.edu.pl/cgi-bin/koha/opac-user.pl");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Account loginAndFetchAccount(String login, String password) throws IOException, ParseException, InvalidCredentialsException, ServerErrorException {
        if (!WebClient.checkCookieHandlerInitialized()) {
            throw new RuntimeException("Please init CookieHandler via WebClient.initCookieHandler()");
        }

        UrlQuery urlQuery = new UrlQuery()
                .add("koha_login_context", "opac")
                .add("userid", login)
                .add("password", password);

        WebClient.FullPageResponse response = WebClient.fetchPage(URL_LOGIN, urlQuery.toString());

        if (response.getResponseCode() != 200) {
            throw new ServerErrorException(response.getResponseCode(), response.getResponseMessage(), response.getContent());
        }

        String content = response.getContent();
        if (content.contains(STRING_INVALID_CREDENTIALS)) {
            throw new InvalidCredentialsException();
        }

        Account account = parseAccount(content);
        account.setCardNumber(login);
        return account;
    }

    public static Account parseAccount(String webPage) {
        Account account = new Account();

        try {
            account.setName(StringUtils.substringBetween(webPage, OPEN_NAME, CLOSE_NAME).trim());
        } catch (Exception e) {
            account.setName("!@#$%^&*");
            Log.e(TAG, "Unrecognized name");
        }

        try {
            account.setDebts(Float.parseFloat(StringUtils.substringBetween(webPage, OPEN_DUE, CLOSE_DUE).trim()));
        } catch (Exception e) {
            account.setDebts(-1);
            Log.e(TAG, "Unrecognized due");
        }

        try {
            account.setBorrowerNumber(Integer.parseInt(StringUtils.substringBetween(webPage, OPEN_BORROWER, CLOSE_BORROWER).trim()));
        } catch (Exception e) {
            account.setBorrowerNumber(-1);
            Log.e(TAG, "Invalid borrower number");
        }


        String lends = StringUtils.substringBetween(webPage, OPEN_LENDS, CLOSE_LENDS);
        for (String row : StringUtils.substringsBetween(lends, OPEN_ROW, CLOSE_ROW)) {
            account.getBookList().add(parseBook(row, true));
        }

        String reserved = StringUtils.substringBetween(webPage, OPEN_RESERVED, CLOSE_RESERVED);
        for (String row : StringUtils.substringsBetween(reserved, OPEN_ROW, CLOSE_ROW)) {
            account.getBookList().add(parseBook(row, false));
        }

        account.updateStats();

        return account;
    }

    private static Book parseBook(String row, boolean lend) {
        Book book = new Book();
        book.setCategory(lend ? Book.CATEGORY_LEND : Book.CATEGORY_BOOKED);

        try {
            String title = StringUtils.substringBetween(row, OPEN_BOOK_TITLE, CLOSE_BOOK_TITLE);
            String[] titleSplit = title.split("\">");
            book.setBiblioNumber(Integer.parseInt(titleSplit[0]));
            book.setTitle(titleSplit[1].replace(" / ", "").replace(" : ", "").trim());
        } catch (Exception e) {
            book.setTitle("*&^%$#@!");
            book.setBiblioNumber(-1);
            Log.e(TAG, "Invalid book title and biblionumber");
        }

        try {
            if (lend) {
                book.setAuthors(StringUtils.substringBetween(row, OPEN_BOOK_AUTHOR, CLOSE_BOOK_AUTHOR).trim());
            } else {
                book.setAuthors(StringUtils.substringBetween(row, OPEN_BOOK_REQUEST_AUTHOR, CLOSE_BOOK_REQUEST_AUTHOR).trim());
            }
        } catch (Exception e) {
            book.setAuthors("*&^%$#@!");
            Log.e(TAG, "Invalid book authors");
        }

        if (lend) {
            try {
                book.setDueDate(Book.DUE_DATE_FORMAT.parse(StringUtils.substringBetween(row, OPEN_BOOK_DUE_DATE, CLOSE_BOOK_DUE_DATE).trim()));
            } catch (Exception e) {
                book.setDueDate(new Date(0));
                Log.e(TAG, "Invalid book due date");
            }
        } else {
            try {
                String due = StringUtils.substringBetween(row, OPEN_BOOK_REQUEST_DUE_DATE, CLOSE_BOOK_REQUEST_DUE_DATE);
                if (due.contains(STRING_BOOK_REQUEST_NEVER_EXPIRED)) {
                    book.setDueDate(null);
                } else {
                    book.setDueDate(Book.DUE_DATE_FORMAT.parse(due.trim()));
                    book.setCategory(Book.CATEGORY_WAITING);
                }
            } catch (Exception e) {
                book.setDueDate(new Date(0));
                Log.e(TAG, "Invalid book due date");
            }

            try {
                book.setRequestDate(Book.DUE_DATE_FORMAT.parse(StringUtils.substringBetween(row, OPEN_BOOK_REQUEST_DATE, CLOSE_BOOK_REQUEST_DATE).trim()));
            } catch (Exception e) {
                book.setRequestDate(new Date(0));
                Log.e(TAG, "Invalid book request date");
            }
        }

        if (lend) {
            try {
                book.setBarCode(Long.parseLong(StringUtils.substringBetween(row, OPEN_BOOK_BARCODE, CLOSE_BOOK_BARCODE).trim()));
            } catch (Exception e) {
                book.setBarCode(-1);
                Log.e(TAG, "Invalid book barcode");
            }

            try {
                book.setSignature(StringUtils.substringBetween(row, OPEN_BOOK_SIGNATURE, CLOSE_BOOK_SIGNATURE).trim());
            } catch (Exception e) {
                book.setSignature("*&^%$#@!");
                Log.e(TAG, "Invalid book signature");
            }

            try {
                if (row.contains(STRING_CANT_PROLONG)) {
                    book.setAllProlongs(5);
                    book.setAvailableProlongs(0);
                    book.setItem(-1);
                } else {
                    String renewals = StringUtils.substringBetween(row, OPEN_BOOK_RENEWALS, CLOSE_BOOK_RENEWALS).trim();
                    String[] fromTo = renewals.split(" z ");
                    book.setAvailableProlongs(Integer.parseInt(fromTo[0].trim()));
                    book.setAllProlongs(Integer.parseInt(fromTo[1].trim()));
                    book.setItem(Integer.parseInt(StringUtils.substringBetween(row, OPEN_BOOK_ITEM, CLOSE_BOOK_ITEM).trim()));
                }
            } catch (Exception e) {
                book.setAvailableProlongs(-1);
                book.setAllProlongs(-1);
                Log.e(TAG, "Invalid book prolongs count");
            }
        } else {
            if (book.getCategory() == Book.CATEGORY_BOOKED) {
                try {
                    book.setQueue(Integer.parseInt(StringUtils.substringBetween(row, OPEN_BOOK_QUEUE, CLOSE_BOOK_QUEUE).trim()));
                } catch (Exception e) {
                    book.setBarCode(-1);
                    Log.e(TAG, "Invalid book queue");
                }
            } else {
                book.setQueue(0);
            }

            try {
                book.setRental(StringUtils.substringBetween(row, OPEN_BOOK_RENTAL, CLOSE_BOOK_RENTAL).trim());
            } catch (Exception e) {
                book.setRental("*&^%$#@!");
                Log.e(TAG, "Invalid book rental office");
            }
        }

        return book;
    }
}
