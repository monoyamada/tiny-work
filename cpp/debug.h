#ifndef DEBUG_J0HH2B6P
#define DEBUG_J0HH2B6P

#include <string>

#ifndef STD_DEBUG
#define STD_DEBUG std::cout << "[debug " << __FILE__ << ":" << __LINE__ << "] "
#endif // STD_DEBUG

#ifndef STD_ERROR
#define STD_ERROR std::cerr << "[error " << __FILE__ << ":" << __LINE__ << "] "
#endif // STD_ERROR

namespace debug {
	extern bool on;
	std::string message_null (const char* what);
	std::string message_unexpected (const char* what
		, const char* expected, const char* actual);
};

#endif /* end of include guard: DEBUG_J0HH2B6P */
