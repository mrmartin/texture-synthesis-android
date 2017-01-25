#include <jni.h>
#include <string>
#include <errno.h> // for argument parsing with strtol (kyaa.h)
#include <math.h> // for log (neglog_cauchy) used in pixel diff. calculations
#include <stdbool.h> // we're targetting C11 anyway, may as well use it
#include <stdint.h> // for uint8_t for pixel data
#include <stdio.h>
#include <stdlib.h>
#include <string.h> // for file extension mangling
#include <time.h> // for time(0) as a random seed (srand)

// decide which features we want from stb_image.
// this should cover the most common formats.
#define STB_IMAGE_IMPLEMENTATION
#define STB_IMAGE_STATIC
#define STBI_ONLY_JPEG
#define STBI_ONLY_PNG
#define STBI_ONLY_BMP
#define STBI_ONLY_GIF
#include "stb_image.h"


// likewise for stb_image_write. by using the static keyword,
// any unused formats and functions can be stripped from the resulting binary.
// we only use png (stbi_write_png) in this case.
#define STB_IMAGE_WRITE_IMPLEMENTATION
#define STB_IMAGE_WRITE_STATIC
#include "stb_image_write.h"

// this isn't the prettiest way of handling memory errors,
// but it should suffice for our one-thing one-shot program.
#define STRETCHY_BUFFER_OUT_OF_MEMORY \
    fprintf(stderr, "fatal error: ran out of memory in stb__sbgrowf\n"); \
    exit(1);
// provides vector<>-like arrays of variable size.
#include "stretchy_buffer.h"

// for command-line argument parsing
#include "kyaa.h"


extern "C"
jstring
Java_com_example_martin_capturetexture_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
