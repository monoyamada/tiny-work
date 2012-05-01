#include "debug.h"
#include <sstream>

const char* default_delim = ", ";

const char* message_unknown = "unknown error was occured";

#ifdef DEBUG
	bool debug::on = true;
#else
	bool debug::on = false;
#endif

std::string debug::message_unexpected (const char* what
	, const char* expected, const char* actual) {
	std::ostringstream buffer;
	if (what != 0) {
		buffer << "unexpected " << what << ": ";
	}
	if (expected == 0) {
		expected = "non-null";
	}
	if (actual == 0) {
		expected = "null";
	}
	buffer << "expected=" << expected << ", but actual=" << actual;
	return buffer.str ();
}
