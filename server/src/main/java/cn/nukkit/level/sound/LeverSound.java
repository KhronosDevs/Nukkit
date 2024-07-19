package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class LeverSound extends GenericSound {

    public LeverSound(Vector3 pos, boolean isPowerOn) {
        super(pos, LevelEventPacket.EVENT_SOUND_BUTTON_CLICK, isPowerOn ? 0.7f : 0.5f);
    }

}
