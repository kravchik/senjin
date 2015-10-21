package myengine.optiseq.states.arraystructure;

/**
 * Created by: Yuri Kravchik Date: 2/11/2007 Time: 10:28:41
 */
public abstract class StrideOffset extends Stride {
    protected int offset;

    public StrideOffset(final int stride, final int offset) {
        super(stride);
        this.offset = offset;
    }
}
