# include <jni.h>

JNIEXPORT jstring
JNICALL
Java_com_mahmutalperenunal_passwordbook_security_EncryptionDecryption_getKey(JNIEnv
* env,
jobject thiz
) {
return (*env)->NewStringUTF(env, "gST/s74ylq3PVtffExulAA==:ASrYWQ7W0+d0Hw9ENTiEZzRiThTpKqaaeW4paguIxuM=");
}
