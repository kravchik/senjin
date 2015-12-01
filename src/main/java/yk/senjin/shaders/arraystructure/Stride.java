package yk.senjin.shaders.arraystructure;

/**
 * Created by: Yuri Kravchik
 * Date: 2/11/2007
 * Time: 10:38:13
 */
public abstract class Stride extends AbstractArrayStructure {
    /**
     * Specifies the byte stride from one attribute to the next, allowing
     * attribute values to be intermixed with other attribute values or stored
     * in a separate array.
     * <p/>
     * A value of 0 for stride means that the values are stored sequentially in
     * memory with no gaps between successive elements.
     */
    protected int stride;

    public Stride(int stride) {
        this.stride = stride;
    }
}
