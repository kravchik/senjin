package yk.senjin.examples.ds;

import yk.jcommon.fastgeom.Vec2f;
import yk.jcommon.fastgeom.Vec3f;
import yk.jcommon.fastgeom.Vec4f;

/**
 * Created with IntelliJ IDEA.
 * User: yuri
 * Date: 22/10/15
 * Time: 20:55
 */

//posnorcoluv
//poscolnoruv
//colposnoruv
//coponouv
public class PoconuvVi {//TODO rename

    public Vec3f normal = Vec3f.ZERO;
    public Vec4f color = Vec4f.ZERO;
    public Vec3f pos = Vec3f.ZERO;
    public Vec2f uv = Vec2f.ZERO;

    public PoconuvVi(Vec3f pos) {
        this.pos = pos;
    }

    public PoconuvVi(Vec3f pos, Vec2f uv) {
        this.pos = pos;
        this.uv = uv;
    }

    public PoconuvVi(Vec3f pos, Vec3f normal) {
        this.pos = pos;
        this.normal = normal;
    }

    public PoconuvVi(Vec3f pos, Vec3f normal, Vec4f color, Vec2f uv) {
        this.pos = pos;
        this.normal = normal;
        this.color = color;
        this.uv = uv;
    }

    public PoconuvVi(Vec3f pos, Vec3f normal, Vec2f uv) {
        this.pos = pos;
        this.normal = normal;
        this.uv = uv;
    }

    public PoconuvVi(Vec3f pos, Vec3f normal, Vec4f color) {
        this.pos = pos;
        this.normal = normal;
        this.color = color;
    }

    public PoconuvVi(Vec3f pos, Vec4f color) {
        this.pos = pos;
        this.color = color;
    }
}