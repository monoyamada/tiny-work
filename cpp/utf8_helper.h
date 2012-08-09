#ifndef UTF8_HELPER_B7TKUA86
#define UTF8_HELPER_B7TKUA86

#include <stdio.h>
#include <stdint.h>

namespace utf8_helper {
	const char* next (const char* p);
	const uint8_t* next (const uint8_t* p);
	size_t size (const char* p);
	size_t size (const uint8_t* p);
};

#endif /* end of include guard: UTF8_HELPER_B7TKUA86 */
