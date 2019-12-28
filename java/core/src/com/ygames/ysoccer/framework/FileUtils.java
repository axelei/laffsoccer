package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;
import com.sun.istack.internal.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class FileUtils {

    public static final char[] BAD_CHARS = new char[] {'.', '\\', '/', '\'', ',', ':', ';', ' '};

    public static String getTeamFromFile(@NotNull String path) {
        return path.substring(path.indexOf('.') + 1, path.lastIndexOf('.'));
    }

    public static String normalizeName(@NotNull String name) {
        String normalized = StringUtils.stripAccents(name).toLowerCase();
        for (char badChar : BAD_CHARS) {
            normalized = normalized.replace(String.valueOf(badChar), "");
        }
        return normalized;
    }

    public static byte[] inputStreamToBytes(ByteArrayInputStream byteArrayInputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int read = byteArrayInputStream.read();
            while (read != -1) {
                byteArrayOutputStream.write(read);
                read = byteArrayInputStream.read();
            }
        } catch (Exception e) {
            Gdx.app.error("Error converting inputStream", e.toString());
        }
        return byteArrayOutputStream.toByteArray();
    }
}
