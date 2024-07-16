package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockEntityDataPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_ENTITY_DATA_PACKET;

    public int x;
    public int y;
    public int z;
    public byte[] namedTag;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.x = this.getInt();
        this.y = this.getInt();
        this.z = this.getInt();
        this.namedTag = this.get();
    }

    @Override
    public void encode() {
        this.reset();
        this.putInt(this.x);
        this.putInt(this.y);
        this.putInt(this.z);
        this.put(this.namedTag);
    }
}