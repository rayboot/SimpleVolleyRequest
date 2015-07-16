package com.github.rayboot.svr;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author rayboot
 * @from 14-3-17 19:26
 * @TODO gzip压缩，解压算法
 */
public class GzipUtil
{
    // 压缩算法
    public static byte[] compress(String string) throws IOException
    {
        byte[] blockcopy = ByteBuffer.allocate(4)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
                .putInt(string.length()).array();
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        os.close();
        byte[] compressed = new byte[4 + os.toByteArray().length];
        System.arraycopy(blockcopy, 0, compressed, 0, 4);
        System.arraycopy(os.toByteArray(), 0, compressed, 4,
                os.toByteArray().length);
        return compressed;
    }

    // 解压算法
    public static String decompress(byte[] compressed) throws IOException
    {
        byte[] h = new byte[2];
        h[0] = (compressed)[0];
        h[1] = (compressed)[1];
        int head = getShort(h);
        boolean t = head == 0x1f8b;
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        try
        {
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            if (t)
            {
                in = new GZIPInputStream(bis);
            }
            else
            {
                in = bis;
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(in),
                    1000);
            for (String line = r.readLine(); line != null; line = r.readLine())
            {
                sb.append(line);
            }
            r.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (in != null)
            {
                in.close();
            }
        }
        return sb.toString();
    }

    private static int getShort(byte[] data)
    {
        return (int) ((data[0] << 8) | data[1] & 0xFF);
    }
}
