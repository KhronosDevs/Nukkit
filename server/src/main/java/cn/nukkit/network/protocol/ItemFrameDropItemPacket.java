package cn.nukkit.network.protocol;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    @Override
    public void decode() {
        this.z = getInt();
        this.y = getInt();
        this.x = getInt();
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }
}