package com.ygames.ysoccer.framework;

import com.badlogic.gdx.Gdx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

class FileUtils {

    static byte[] inputStreamToBytes(ByteArrayInputStream byteArrayInputStream) {
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
