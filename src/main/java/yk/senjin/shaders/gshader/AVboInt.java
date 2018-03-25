package yk.senjin.shaders.gshader;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Yuri Kravchik on 25.03.18.
 */
public class AVboInt extends AVbo {
    public AVboInt(int elementsCount) {
        super(short.class, elementsCount);
    }

    @Override public void serializeData(ByteBuffer buffer, List data) {
        ReflectionVBO.setDataShort(data, buffer);
    }
}
