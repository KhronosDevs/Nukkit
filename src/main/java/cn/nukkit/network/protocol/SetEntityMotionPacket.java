package cn.nukkit.network.protocol;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class SetEntityMotionPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.SET_ENTITY_MOTION_PACKET;

    // eid, motX, motY, motZ
    public Entry[] entities = new Entry[0];

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public DataPacket clean() {
        this.entities = new Entry[0];
        return super.clean();
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        for (Entry entry : this.entities) {
            this.putLong(entry.entityId);
            this.putFloat((float) entry.motionX);
            this.putFloat((float) entry.motionY);
            this.putFloat((float) entry.motionZ);
        }
    }

    public static class Entry {
        public final long entityId;
        public final double motionX;
        public final double motionY;
        public final double motionZ;

        public Entry(long entityId, double motionX, double motionY, double motionZ) {
            this.entityId = entityId;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
        }
    }

}
