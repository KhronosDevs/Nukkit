package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.LevelEventPacket;

public final class BlockPlaceSound extends GenericSound {

    public BlockPlaceSound(Vector3 pos) {
        super(pos, 0);
    }

    public BlockPlaceSound(Vector3 pos, float pitch) {
        super(pos, LevelEventPacket.EVENT_SOUND_BLOCK_PLACE, pitch);
    }

}
