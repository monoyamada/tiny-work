#ifndef COMMON_LASY3GL7
#define COMMON_LASY3GL7

#include <stdexcept>
#include <ostream>
#include <stdint.h>

namespace fatvm {
	class fatvm_error : public std::runtime_error {
		public: fatvm_error (const std::string& what);
	};
	std::ostream& out_of_range_msg (std::ostream& out
		, int64_t begin, int64_t end, int64_t index 
		, const char* what, const char* file, int line);
	std::ostream& no_such_msg (std::ostream& out
		, const char* what, const char* file, int line);
	std::ostream& out_of_limit_msg (std::ostream& out
		, int64_t limit, int64_t required
		, const char* what, const char* file, int line);
};

#endif /* end of include guard: COMMON_LASY3GL7 */
