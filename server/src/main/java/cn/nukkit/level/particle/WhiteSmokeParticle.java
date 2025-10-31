package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

public final class WhiteSmokeParticle extends GenericParticle {

    public WhiteSmokeParticle(Vector3 pos) {
        this(pos, 0);
    }

    public WhiteSmokeParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_WHITE_SMOKE, scale);
    }
}
