package yk.senjin;

import yk.jcommon.utils.ClassChangeWatcher;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 18/11/15
 * Time: 10:03
 */
public class TickingWatcher<T> {
    public ClassChangeWatcher<LoadTickUnload<T>> watcher;

    public TickingWatcher(String path, T t, LoadTickUnload<T> o, Class... otherClasses) {
        watcher = ClassChangeWatcher.watch(path, o, otherClasses);
        watcher.dst.onLoad(t);
    }

    public void tick(T t, float dt) {
        LoadTickUnload<T> old = watcher.dst;
        if (watcher.reload()) {
            old.onUnload();
            watcher.dst.onLoad(t);
        }
        watcher.dst.onTick(t, dt);
    }

}
