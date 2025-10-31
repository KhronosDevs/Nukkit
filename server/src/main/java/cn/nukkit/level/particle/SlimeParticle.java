package cn.nukkit.level.particle;

import cn.nukkit.math.Vector3;

public final class SlimeParticle extends GenericParticle {
    public SlimeParticle(Vector3 pos) {
        this(pos, 0);
    }

    public SlimeParticle(Vector3 pos, int scale) {
        super(pos, Particle.TYPE_SLIME, scale);
    }
}
