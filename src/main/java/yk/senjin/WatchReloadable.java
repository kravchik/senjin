package yk.senjin;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 17/11/15
 * Time: 03:11
 */
public class WatchReloadable extends Simple3DWatch {
    public TickingWatcher<Simple3DWatch> newWatcher;
    private LoadTickUnload<Simple3DWatch> first;
    private String path;
    private Class[] classes;

    public WatchReloadable(LoadTickUnload<? extends Simple3DWatch> first, Class... classes) {
        this.first = (LoadTickUnload<Simple3DWatch>) first;
        this.path = "target/classes/";
        this.classes = classes;
    }

    public WatchReloadable(LoadTickUnload<? extends Simple3DWatch> first, String path, Class... classes) {
        this.first = (LoadTickUnload<Simple3DWatch>) first;
        this.path = path;
        this.classes = classes;
    }

    @Override
    public void firstFrame() {
        super.firstFrame();
        newWatcher = new TickingWatcher<>(path, this, first, classes);
    }

    @Override
    public void tick(float dt) {
        newWatcher.tick(this, dt);
    }
}
