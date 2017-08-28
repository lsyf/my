package com.loushuiyifan.shiro.tool;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.util.ByteSource;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

/**
 * @author 漏水亦凡
 * @date 2017/7/14
 */
public class MySimpleByteSource implements ByteSource, Serializable {

    private static final long serialVersionUID = 1L;


    private byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public MySimpleByteSource() {
    }

    public MySimpleByteSource(byte[] bytes) {
        this.bytes = bytes;
    }


    public MySimpleByteSource(char[] chars) {
        this.bytes = CodecSupport.toBytes(chars);
    }


    public MySimpleByteSource(String string) {
        this.bytes = CodecSupport.toBytes(string);
    }


    public MySimpleByteSource(ByteSource source) {
        this.bytes = source.getBytes();
    }


    public MySimpleByteSource(File file) {
        this.bytes = new MySimpleByteSource.BytesHelper().getBytes(file);
    }


    public MySimpleByteSource(InputStream stream) {
        this.bytes = new MySimpleByteSource.BytesHelper().getBytes(stream);
    }

    public static boolean isCompatible(Object o) {
        return o instanceof byte[] || o instanceof char[] || o instanceof String ||
                o instanceof ByteSource || o instanceof File || o instanceof InputStream;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    public String toHex() {
        if (this.cachedHex == null) {
            this.cachedHex = Hex.encodeToString(getBytes());
        }
        return this.cachedHex;
    }

    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.encodeToString(getBytes());
        }
        return this.cachedBase64;
    }

    public String toString() {
        return toBase64();
    }

    public int hashCode() {
        if (this.bytes == null || this.bytes.length == 0) {
            return 0;
        }
        return Arrays.hashCode(this.bytes);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ByteSource) {
            ByteSource bs = (ByteSource) o;
            return Arrays.equals(getBytes(), bs.getBytes());
        }
        return false;
    }

    //will probably be removed in Shiro 2.0.  See SHIRO-203:
    //https://issues.apache.org/jira/browse/SHIRO-203
    private static final class BytesHelper extends CodecSupport {
        public byte[] getBytes(File file) {
            return toBytes(file);
        }

        public byte[] getBytes(InputStream stream) {
            return toBytes(stream);
        }
    }

}
