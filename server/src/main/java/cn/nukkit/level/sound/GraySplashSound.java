package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

public final class GraySplashSound extends GenericSound {

    public GraySplashSound(Vector3 pos) {
        this(pos, 0);
    }

    public GraySplashSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_GRAY_SPLASH, pitch);
    }
}
