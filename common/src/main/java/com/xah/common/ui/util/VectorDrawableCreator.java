package com.xah.common.ui.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Xml;
import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import com.xah.shared.LogUtil;
import org.xmlpull.v1.XmlPullParser;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorDrawableCreator {
    private static final byte[][] BIN_XML_STRINGS = {
            "width".getBytes(),
            "height".getBytes(),
            "viewportWidth".getBytes(),
            "viewportHeight".getBytes(),
            "fillColor".getBytes(),
            "pathData".getBytes(),
            "fillType".getBytes(),
            "path".getBytes(),
            "vector".getBytes(),
            "http://schemas.android.com/apk/res/android".getBytes()
    };

    private static final int[] BIN_XML_ATTRS = {
            android.R.attr.height,
            android.R.attr.width,
            android.R.attr.viewportWidth,
            android.R.attr.viewportHeight,
            android.R.attr.fillColor,
            android.R.attr.pathData,
            android.R.attr.fillType
    };

    private static final short CHUNK_TYPE_XML = 0x0003;
    private static final short CHUNK_TYPE_STR_POOL = 0x0001;
    private static final short CHUNK_TYPE_START_TAG = 0x0102;
    private static final short CHUNK_TYPE_END_TAG = 0x0103;
    private static final short CHUNK_TYPE_RES_MAP = 0x0180;

    private static final short VALUE_TYPE_DIMENSION = 0x0500;
    private static final short VALUE_TYPE_STRING = 0x0300;
    private static final short VALUE_TYPE_COLOR = 0x1D00;
    private static final short VALUE_TYPE_FLOAT = 0x0400;
    private static final short VALUE_TYPE_INT = 0x1000;

    public static Drawable getVectorDrawable(
            @NonNull Context context,
            int width,
            int height,
            float viewportWidth,
            float viewportHeight,
            List<PathData> paths
    ) {
        byte[] binXml = createBinaryDrawableXml(
                width,
                height,
                viewportWidth,
                viewportHeight,
                paths
        );

        try {
            @SuppressLint("PrivateApi")
            Class<?> xmlBlock = Class.forName("android.content.res.XmlBlock");
            Constructor<?> constructor = xmlBlock.getConstructor(byte[].class);
            Method newParser = xmlBlock.getDeclaredMethod("newParser");
            constructor.setAccessible(true);
            newParser.setAccessible(true);

            XmlPullParser parser = (XmlPullParser)
                    newParser.invoke(constructor.newInstance((Object) binXml));

            if (Build.VERSION.SDK_INT >= 24) {
                return Drawable.createFromXml(context.getResources(), parser);
            } else {
                AttributeSet attrs = Xml.asAttributeSet(parser);
                int type;
                do {
                    type = parser.next();
                } while (type != XmlPullParser.START_TAG);
                return VectorDrawableCompat.createFromXmlInner(
                        context.getResources(),
                        parser,
                        attrs,
                        null
                );
            }
        } catch (Exception e) {
            LogUtil.INSTANCE.error(e, "");
        }
        return null;
    }

    private static byte[] createBinaryDrawableXml(
            int width,
            int height,
            float viewportWidth,
            float viewportHeight,
            List<PathData> paths
    ) {
        List<byte[]> stringPool = new ArrayList<>(Arrays.asList(BIN_XML_STRINGS));
        for (PathData p : paths) {
            stringPool.add(p.data);
        }

        ByteBuffer bb = ByteBuffer.allocate(8192);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        // ==== XML CHUNK ====
        bb.putShort(CHUNK_TYPE_XML);
        bb.putShort((short) 8);
        int xmlSizePos = bb.position();
        bb.putInt(0);

        // ==== STRING POOL ====
        int spStart = bb.position();
        bb.putShort(CHUNK_TYPE_STR_POOL);
        bb.putShort((short) 28);
        int spSizePos = bb.position();
        bb.putInt(0);
        bb.putInt(stringPool.size());
        bb.putInt(0);
        bb.putInt(1 << 8);
        int spStringStartPos = bb.position();
        bb.putInt(0);
        bb.putInt(0);

        int offset = 0;
        for (byte[] s : stringPool) {
            bb.putInt(offset);
            offset += s.length + (s.length > 127 ? 5 : 3);
        }

        int pos = bb.position();
        bb.putInt(spStringStartPos, pos - spStart);
        bb.position(pos);

        for (byte[] s : stringPool) {
            int len = s.length;
            if (len > 127) {
                byte high = (byte) ((len >> 8) | 0x80);
                byte low = (byte) len;
                bb.put(high).put(low).put(high).put(low);
            } else {
                bb.put((byte) len).put((byte) len);
            }
            bb.put(s).put((byte) 0);
        }

        while (bb.position() % 4 != 0) bb.put((byte) 0);
        bb.putInt(spSizePos, bb.position() - spStart);

        // ==== RESOURCE MAP ====
        bb.putShort(CHUNK_TYPE_RES_MAP);
        bb.putShort((short) 8);
        bb.putInt(8 + BIN_XML_ATTRS.length * 4);
        for (int a : BIN_XML_ATTRS) bb.putInt(a);

        // ==== VECTOR START ====
        int vsStart = bb.position();
        int vsSizePos = putStartTag(bb, 8, 4);

        putAttribute(bb, 0, -1, VALUE_TYPE_DIMENSION, (width << 8) + 1);
        putAttribute(bb, 1, -1, VALUE_TYPE_DIMENSION, (height << 8) + 1);
        putAttribute(bb, 2, -1, VALUE_TYPE_FLOAT, Float.floatToRawIntBits(viewportWidth));
        putAttribute(bb, 3, -1, VALUE_TYPE_FLOAT, Float.floatToRawIntBits(viewportHeight));

        bb.putInt(vsSizePos, bb.position() - vsStart);

        // ==== PATHS ====
        for (int i = 0; i < paths.size(); i++) {
            PathData p = paths.get(i);
            int psStart = bb.position();
            int psSizePos = putStartTag(bb, 7, 3);

            putAttribute(bb, 4, -1, VALUE_TYPE_COLOR, p.color);
            putAttribute(bb, 5, 10 + i, VALUE_TYPE_STRING, 10 + i);
            putAttribute(bb, 6, -1, VALUE_TYPE_INT, p.fillType);

            bb.putInt(psSizePos, bb.position() - psStart);
            putEndTag(bb, 7);
        }

        putEndTag(bb, 8);
        bb.putInt(xmlSizePos, bb.position());

        byte[] out = new byte[bb.position()];
        bb.rewind();
        bb.get(out);
        return out;
    }

    private static int putStartTag(ByteBuffer bb, int name, int attrCount) {
        bb.putShort(CHUNK_TYPE_START_TAG);
        bb.putShort((short) 16);
        int sizePos = bb.position();
        bb.putInt(0);
        bb.putInt(0);
        bb.putInt(-1);
        bb.putInt(-1);
        bb.putInt(name);
        bb.putShort((short) 0x14);
        bb.putShort((short) 0x14);
        bb.putShort((short) attrCount);
        bb.putShort((short) 0);
        bb.putShort((short) 0);
        bb.putShort((short) 0);
        return sizePos;
    }

    private static void putEndTag(ByteBuffer bb, int name) {
        bb.putShort(CHUNK_TYPE_END_TAG);
        bb.putShort((short) 16);
        bb.putInt(24);
        bb.putInt(0);
        bb.putInt(-1);
        bb.putInt(-1);
        bb.putInt(name);
    }

    private static void putAttribute(
            ByteBuffer bb,
            int name,
            int rawValue,
            short valueType,
            int valueData
    ) {
        bb.putInt(9); // android namespace
        bb.putInt(name);
        bb.putInt(rawValue);
        bb.putShort((short) 8);
        bb.putShort(valueType);
        bb.putInt(valueData);
    }

    public static class PathData {
        public byte[] data;
        public int color;
        public int fillType;

        public static final int FILL_TYPE_NON_ZERO = 0;
        public static final int FILL_TYPE_EVEN_ODD = 1;

        public PathData(byte[] data, int color, int fillType) {
            this.data = data;
            this.color = color;
            this.fillType = fillType == FILL_TYPE_EVEN_ODD ? FILL_TYPE_EVEN_ODD : FILL_TYPE_NON_ZERO;
        }

        public PathData(byte[] data, int color) {
            this(data, color, FILL_TYPE_NON_ZERO);
        }

        public PathData(String data, int color, int fillType) {
            this(data.getBytes(StandardCharsets.UTF_8), color, fillType);
        }

        public PathData(String data, int color) {
            this(data.getBytes(StandardCharsets.UTF_8), color);
        }
    }
}
