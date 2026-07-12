#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

void* av_frame_alloc(void) { return NULL; }
void av_frame_free(void** frame) {}
void av_free(void* ptr) {}
void av_log_set_callback(void* callback) {}
void* av_packet_alloc(void) { return NULL; }
void* avcodec_alloc_context3(const void* codec) { return NULL; }
int avcodec_close(void* avctx) { return 0; }
void* avcodec_find_decoder(int id) { return NULL; }
int avcodec_is_open(void* s) { return 0; }
int avcodec_open2(void* avctx, const void* codec, void** options) { return 0; }
int avcodec_receive_frame(void* avctx, void* frame) { return 0; }
int avcodec_send_packet(void* avctx, const void* avpkt) { return 0; }

#ifdef __cplusplus
}
#endif
