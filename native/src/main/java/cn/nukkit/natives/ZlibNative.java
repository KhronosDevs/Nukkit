package cn.nukkit.natives;

public class ZlibNative {

  public static native byte[] deflate(byte[] input, int compressionLevel);
  public static native byte[] inflate(byte[] input);
}
