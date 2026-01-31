package ru.ztrixdev.projects.passhavenapp;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {
    public static Boolean IntegerToBoolean(Integer val) {
        return val != 0;
    }

    public static Integer BooleanToInteger(Boolean val) {
        if (val == true) return 1;
        else return 0;
    }

    // I politely stole ts from SO but it's very useful.
    public static Map<String, List<String>> getQueryParams(String url) {
        Map<String, List<String>> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                }

                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }

        return params;
    }


    public static final String FILE_NOT_FOUND_SIGNAL = "file_not_found";
    public static final String IO_EXCEPTION_SIGNAL = "io_exception";

    public static String readFile(Uri uri, ContentResolver resolver) {
        try {
            InputStream stream = resolver.openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder total = new StringBuilder();

            for (String line; (line = reader.readLine()) != null; ) {
                total.append(line).append('\n');
            }

            return total.toString().trim();
        } catch (FileNotFoundException e) {
            return FILE_NOT_FOUND_SIGNAL;
        } catch (IOException e) {
            return IO_EXCEPTION_SIGNAL;
        }
    }


    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    public static final int UUID_ALPHANUMERIC_STRING_LENGTH = 36;
}
