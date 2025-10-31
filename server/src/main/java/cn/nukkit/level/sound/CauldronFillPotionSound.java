package cn.nukkit.level.sound;

import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.LevelEventPacket;

public final class CauldronFillPotionSound extends Sound {

    private final int id;
    private final int color;

    public CauldronFillPotionSound(Vector3 pos, int r, int g, int b) {
        super(pos.x, pos.y, pos.z);
        this.id = LevelEventPacket.EVENT_SOUND_CAULDRON_FILL_POTION;
        this.color = (r << 16 | g << 8 | b) & 0xffffff;
    }

    @Override
    public DataPacket[] encode() {
        LevelEventPacket dataPacket = new LevelEventPacket();
        dataPacket.evid = this.id;
        dataPacket.x = (float) this.x;
        dataPacket.y = (float) this.y;
        dataPacket.z = (float) this.z;
        dataPacket.data = this.color;

        return new LevelEventPacket[]{dataPacket};
    }
}
