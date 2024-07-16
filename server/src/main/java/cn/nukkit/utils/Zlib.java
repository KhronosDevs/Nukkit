package cn.nukkit.utils;

import cn.nukkit.natives.ZlibNative;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.InflaterInputStream;


public abstract class Zlib {

    public static byte[] deflate(byte[] data, int level) { return ZlibNative.deflate(data, level); }

    public static byte[] inflate(byte[] data) { return ZlibNative.inflate(data); }

    public static byte[] inflate(byte[] data, int maxSize) {
        var bytes = inflate(data);

        if (bytes.length > maxSize) {
            var returnBytes = new byte[maxSize];

            System.arraycopy(bytes, 0, returnBytes, 0, maxSize);

            return returnBytes;
        }

        return bytes;
    }

}

