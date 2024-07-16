#include <jni.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <zlib.h>

#define MIN(a, b)  ((a) < (b) ? (a) : (b))
#define CHUNK_SIZE 512000  // 64K -- 256K

static bool native_deflate(
    uint8_t *in, size_t in_len, uint8_t **out, size_t *out_len, int compression_level
);
static bool native_inflate(uint8_t *in, size_t in_len, uint8_t **out, size_t *out_len);

JNIEXPORT jbyteArray JNICALL Java_cn_nukkit_natives_ZlibNative_deflate(
    JNIEnv *env, jobject this, jbyteArray input, jint compression_level
)
{
  jbyteArray output_arr = NULL;
  size_t input_len, output_len;
  uint8_t *input_data, *output_data;

  input_len = (*env)->GetArrayLength(env, input);
  input_data = (uint8_t *)(*env)->GetByteArrayElements(env, input, NULL);

  if (!native_deflate(input_data, input_len, &output_data, &output_len, compression_level)) {
    return NULL;
  }

  output_arr = (*env)->NewByteArray(env, output_len);
  (*env)->SetByteArrayRegion(env, output_arr, 0, output_len, (const jbyte *)output_data);

  free(output_data);

  return output_arr;
}

JNIEXPORT jbyteArray
 JNICALL
Java_cn_nukkit_natives_ZlibNative_inflate(JNIEnv *env, jobject this, jbyteArray input)
{
  jbyteArray output_arr = NULL;
  size_t input_len, output_len;
  uint8_t *input_data, *output_data;

  input_len = (*env)->GetArrayLength(env, input);
  input_data = (uint8_t *)(*env)->GetByteArrayElements(env, input, NULL);

  if (!native_inflate(input_data, input_len, &output_data, &output_len)) {
    return NULL;
  }

  output_arr = (*env)->NewByteArray(env, output_len);
  (*env)->SetByteArrayRegion(env, output_arr, 0, output_len, (const jbyte *)output_data);

  free(output_data);

  return output_arr;
}

bool native_deflate(
    uint8_t *in, size_t in_len, uint8_t **out, size_t *out_len, int compression_level
)
{
  static uint8_t chunk[CHUNK_SIZE] = {0};
  uint8_t *in_ptr = NULL, *out_ptr = NULL;
  int32_t result = 0;
  size_t len = 0, written = 0, flush = 0, offset = 0;
  z_stream stream = {0};

  stream.zalloc = NULL;
  stream.zfree = NULL;
  stream.opaque = NULL;

  result = deflateInit(&stream, compression_level);

  if (result != Z_OK) {
    return false;
  }

  do {
    in_ptr = in + offset;

    stream.avail_in = MIN(in_len - offset, CHUNK_SIZE);
    stream.next_in = in_ptr;

    offset += stream.avail_in;
    flush = stream.avail_in <= 0 ? Z_FINISH : Z_NO_FLUSH;

    do {
      stream.avail_out = CHUNK_SIZE;
      stream.next_out = chunk;

      result = deflate(&stream, flush);

      if (result == Z_STREAM_ERROR) {
        fprintf(stderr, "CRITICAL: could not deflate buffer: zlib error %d\n", result);

        if (out_ptr) free(out_ptr);

        deflateEnd(&stream);

        return false;
      }

      written = CHUNK_SIZE - stream.avail_out;

      if (written > 0) {
        out_ptr = realloc(out_ptr, sizeof(uint8_t) * (len + written));
        memcpy(out_ptr + len, chunk, written);

        len += written;
      }
    } while (stream.avail_out == 0);

    if (stream.avail_in != 0) {
      // Didn't consume all input, smth went wrong
      deflateEnd(&stream);
      return false;
    }

  } while (flush != Z_FINISH);

  result = deflateEnd(&stream);

  if (result != Z_OK) {
    return false;
  }

  *out = out_ptr;
  *out_len = len;

  return true;
}

bool native_inflate(uint8_t *in, size_t in_len, uint8_t **out, size_t *out_len)
{
  static uint8_t chunk[CHUNK_SIZE] = {0};
  int32_t result = 0;
  uint8_t *in_ptr = NULL, *out_ptr = NULL;
  size_t written = 0, len = 0, offset = 0;
  z_stream stream = {0};

  stream.zalloc = NULL;
  stream.zfree = NULL;
  stream.opaque = NULL;

  result = inflateInit(&stream);

  if (result != Z_OK) {
    return false;
  }

  do {
    in_ptr = in + offset;

    stream.avail_in = MIN(in_len - offset, CHUNK_SIZE);
    stream.next_in = in_ptr;

    offset += stream.avail_in;

    do {
      stream.avail_out = CHUNK_SIZE;
      stream.next_out = chunk;

      result = inflate(&stream, Z_NO_FLUSH);

      if (result == Z_STREAM_ERROR) {
        fprintf(stderr, "CRITICAL: could not inflate buffer: zlib error %d\n", result);

        if (out_ptr) free(out_ptr);

        inflateEnd(&stream);

        return false;
      }

      switch (result) {
        case Z_NEED_DICT:
        case Z_DATA_ERROR:
        case Z_MEM_ERROR:
          fprintf(stderr, "CRITICAL: could not inflate buffer: zlib error %d\n", result);

          if (out_ptr) free(out_ptr);

          inflateEnd(&stream);
          return false;
      }

      written = CHUNK_SIZE - stream.avail_out;

      if (written > 0) {
        out_ptr = realloc(out_ptr, sizeof(uint8_t) * (len + written));
        memcpy(out_ptr + len, chunk, written);

        len += written;
      }
    } while (stream.avail_out == 0);
  } while (result != Z_STREAM_END);

  result = inflateEnd(&stream);

  if (result != Z_OK) {
    return false;
  }

  *out = out_ptr;
  *out_len = len;

  return true;
}
