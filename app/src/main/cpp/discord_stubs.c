#ifdef __cplusplus
extern "C" {
#endif

void Discord_Initialize(const char* applicationId, void* handlers, int autoRegister, const char* optionalSteamId) {}
void Discord_UpdatePresence(const void* presence) {}
void Discord_Shutdown(void) {}

#ifdef __cplusplus
}
#endif
