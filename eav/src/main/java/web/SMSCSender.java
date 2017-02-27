package web;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Lawrence on 27.02.2017.
 */
public class SMSCSender {

    private String SMSC_LOGIN    = "Netcracker";     // логин клиента
    private String SMSC_PASSWORD = "q7Sq2O_VqLhh";  // пароль или MD5-хеш пароля в нижнем регистре
    private boolean SMSC_HTTPS   = false;         // использовать HTTPS протокол
    private String SMSC_CHARSET  = "utf-8";       // кодировка сообщения: koi8-r, windows-1251 или utf-8 (по умолчанию)
    private boolean SMSC_DEBUG   = false;         // флаг отладки
    private boolean SMSC_POST    = false;         // Использовать метод POST



    public SMSCSender() {
    }

    public SMSCSender(String login, String password) {
        SMSC_LOGIN    = login;
        SMSC_PASSWORD = password;
    }

    public SMSCSender(String login, String password, String charset) {
        SMSC_LOGIN    = login;
        SMSC_PASSWORD = password;
        SMSC_CHARSET  = charset;
    }

    public SMSCSender(String login, String password, String charset, boolean debug) {
        SMSC_LOGIN    = login;
        SMSC_PASSWORD = password;
        SMSC_CHARSET  = charset;
        SMSC_DEBUG    = debug;
    }

    public String[] sendSms(String phones, String message, int translit, String time, String id, int format, String sender, String query) {
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] m = {};

        try {
            m = SmscSendCmd("send", "cost=3&phones=" + URLEncoder.encode(phones, SMSC_CHARSET)
                    + "&mes=" + URLEncoder.encode(message, SMSC_CHARSET)
                    + "&translit=" + translit + "&id=" + id + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, SMSC_CHARSET))
                    + (time == "" ? "" : "&time=" + URLEncoder.encode(time, SMSC_CHARSET))
                    + (query == "" ? "" : "&" + query));
        }
        catch (UnsupportedEncodingException e) {

        }

        if (SMSC_DEBUG) {
            if (Integer.parseInt(m[1]) > 0) {
                System.out.println("Сообщение отправлено успешно. ID: " + m[0] + ", всего SMS: " + m[1] + ", стоимость: " + m[2] + ", баланс: " + m[3]);
            }
            else {
                System.out.print("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
                System.out.println(Integer.parseInt(m[0])>0 ? (", ID: " + m[0]) : "");
            }
        }

        return m;
    }

    public String[] getSmsCost(String phones, String message, int translit, int format, String sender, String query){
        String[] formats = {"", "flash=1", "push=1", "hlr=1", "bin=1", "bin=2", "ping=1"};
        String[] m = {};

        try {
            m = SmscSendCmd("send", "cost=1&phones=" + URLEncoder.encode(phones, SMSC_CHARSET)
                    + "&mes=" + URLEncoder.encode(message, SMSC_CHARSET)
                    + "&translit=" + translit + (format > 0 ? "&" + formats[format] : "")
                    + (sender == "" ? "" : "&sender=" + URLEncoder.encode(sender, SMSC_CHARSET))
                    + (query == "" ? "" : "&" + query));
        }
        catch (UnsupportedEncodingException e) {

        }
        // (cost, cnt) или (0, -error)

        if (SMSC_DEBUG) {
            if (Integer.parseInt(m[1]) > 0) {
                System.out.println("Стоимость рассылки: " + m[0] + ", Всего SMS: " + m[1]);
            }
            else {
                System.out.print("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
            }
        }

        return m;
    }

    public String[] getStatus(int id, String phone, int all){
        String[] m = {};
        String tmp;

        try {
            m = SmscSendCmd("status", "phone=" + URLEncoder.encode(phone, SMSC_CHARSET) + "&id=" + id + "&all=" + all);

            if (SMSC_DEBUG) {
                if (m[1] != "" && Integer.parseInt(m[1]) >= 0) {
                    java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(Integer.parseInt(m[1]));
                    System.out.println("Статус SMS = " + m[0]);
                }
                else
                    System.out.println("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
            }

            if (all == 1 && m.length > 9 && (m.length < 14 || m[14] != "HLR")) {
                tmp = implode(m, ",");
                m = tmp.split(",", 9);
            }
        }
        catch (UnsupportedEncodingException e) {

        }

        return m;
    }

    public String getBalance() {
        String[] m = {};

        m = SmscSendCmd("balance", ""); // (balance) или (0, -error)

        if (SMSC_DEBUG) {
            if (m.length == 1)
                System.out.println("Сумма на счете: " + m[0]);
            else
                System.out.println("Ошибка №" + Math.abs(Integer.parseInt(m[1])));
        }

        return m.length == 2 ?  "" : m[0];
    }

    private String[] SmscSendCmd(String cmd, String arg){
        String[] m = {};
        String ret = ",";

        try {
            String url = (SMSC_HTTPS ? "https" : "http") + "://smsc.ru/sys/" + cmd +".php?login=" + URLEncoder.encode(SMSC_LOGIN, SMSC_CHARSET)
                    + "&psw=" + URLEncoder.encode(SMSC_PASSWORD, SMSC_CHARSET)
                    + "&fmt=1&charset=" + SMSC_CHARSET + "&" + arg;

            int i = 0;
            do {
                if (i > 0)
                    Thread.sleep(2000);
                ret = SmscReadUrl(url);
            }
            while (ret == "" && ++i < 3);
        }
        catch (UnsupportedEncodingException e) {

        }
        catch (InterruptedException e) {

        }

        return ret.split(",");
    }

    private String SmscReadUrl(String url) {

        String line = "", real_url = url;
        String[] param = {};
        boolean is_post = (SMSC_POST || url.length() > 2000);

        if (is_post) {
            param = url.split("\\?",2);
            real_url = param[0];
        }

        try {
            URL u = new URL(real_url);
            InputStream is;

            if (is_post){
                URLConnection conn = u.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), SMSC_CHARSET);
                os.write(param[1]);
                os.flush();
                os.close();
                System.out.println("post");
                is = conn.getInputStream();
            }
            else {
                is = u.openStream();
            }

            InputStreamReader reader = new InputStreamReader(is, SMSC_CHARSET);

            int ch;
            while ((ch = reader.read()) != -1) {
                line += (char)ch;
            }

            reader.close();
        } catch (IOException e) {

        }

        return line;
    }

    private static String implode(String[] ary, String delim) {
        String out = "";

        for (int i = 0; i < ary.length; i++) {
            if (i != 0)
                out += delim;
            out += ary[i];
        }

        return out;
    }
}
