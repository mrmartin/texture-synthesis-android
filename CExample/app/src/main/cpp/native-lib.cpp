#include <jni.h>
#include <string.h>

extern "C"
jstring
Java_com_example_martin_cexample_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    char hello[4] = "abc";
    return env->NewStringUTF(hello);
}

