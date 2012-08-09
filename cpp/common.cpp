#include "common.h"

fatvm::fatvm_error::fatvm_error (const std::string& what) : std::runtime_error (what) {
}

std::ostream& header_msg (std::ostream& out, const char* file, int line) {
	if (file == 0) {
		return out << "[] ";
	}
	return out << "[" << file << ":" << line << "] ";
}

std::ostream& fatvm::out_of_range_msg (std::ostream& out
	, int64_t begin, int64_t end, int64_t index 
	, const char* what, const char* file, int line) {
	if (what == 0) {
		what = "";
	}
	return header_msg (out, file, line) << what << " " << index 
		<< " not in [" << begin << ", " << end << ")";
}

std::ostream& fatvm::no_such_msg (std::ostream& out
	, const char* what, const char* file, int line) {
	if (what == 0) {
		what = "object";
	}
	return header_msg (out, file, line) << "no such " << what << " exists";
}

std::ostream& fatvm::out_of_limit_msg (std::ostream& out
	, int64_t limit, int64_t required
	, const char* what, const char* file, int line) {
	if (what == 0) {
		what = "";
	}
	return header_msg (out, file, line) << what << " required " << required
		<< " exceeding limit " << limit;
}
