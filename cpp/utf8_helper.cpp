#include "utf8_helper.h"
#include <stdint.h>
#include <string.h>

static const uint8_t utf8_skip_size[256] = {
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,
	2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,
	3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,5,5,5,5,6,6,1,1
};

const char* utf8_helper::next (const char* p) {
	return p + utf8_skip_size[static_cast<const uint8_t>(*p)];
}

const uint8_t* utf8_helper::next (const uint8_t* p) {
	return p + utf8_skip_size[*p];
}

size_t utf8_helper::size (const char* p) {
	size_t n = 0;
	const char* end = p + strlen (p);
	for (; p != end; ++n) {
		p = next (p);
	}
	return n;
}

size_t utf8_helper::size (const uint8_t* p) {
	size_t n = 0;
	const uint8_t* end = p + strlen (reinterpret_cast<const char*> (p));
	for (; p != end; ++n) {
		p = next (p);
	}
	return n;
}
