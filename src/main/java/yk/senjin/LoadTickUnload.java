package yk.senjin;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 17/11/15
 * Time: 21:42
 */
public interface LoadTickUnload<T> {
    void onLoad(T watch);
    void onTick(T watch, float dt);
    void onUnload();
}
