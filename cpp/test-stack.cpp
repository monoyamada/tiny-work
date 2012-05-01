#include "fatvm.h"
#include "debug.h"
#include <iostream>
#include <iterator> 

using namespace fatvm;

struct machine : public machine_base {
	const data_array_type& get_data_array () const {
		return machine_base::get_data_array ();
	}
};

int main (int argc, char* argv[]) {
	if (true) {
		STD_DEBUG << sizeof (stack_element) << " bytes" << std::endl;
	}
	if (true) {
		machine vm;
		stack_element value;
		vm.push (value.set_uint64 (123), 1);
		vm.push (value.set_int64 (-456), 1);
		vm.push (value.set_float64 (7.89), 1);
		/* bug in gcc, cannot find operator <<
		std::copy (vm.data_array ().begin (), vm.data_array ().end ()
			, std::ostream_iterator<stack_element> (std::cout, ", "));
		*/
		for (size_t i = 0, n = vm.global_size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << vm.get_global (i);
		}
		std::cout << std::endl;
	}
	if (true) {
		machine vm;
		stack_element value;
		vm.set (0, value.set_uint64 (123));
		vm.set (1, value.set_int64 (-456));
		vm.set (2, value.set_float64 (7.89));
		try {
			vm.pop (4);
		} catch (const machine_error& ex) {
			STD_DEBUG << ex.what () << std::endl;
		}
		for (size_t i = 0, n = vm.global_size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << vm.get_global (i);
		}
		std::cout << std::endl;
	}
	if (true) {
		struct set_123 {
			static void doit (stack_element& dst) {
				dst.set_uint64 (123);
			}
		};
		struct plus_333 {
			static void doit (stack_element& dst, const stack_element& src) {
				dst.set_uint64 (src.get_uint64 (0) + 333);
			}
		};
		struct plus {
			static void doit (stack_element& dst
				, const stack_element& src_0, const stack_element& src_1) {
				dst.set_uint64 (src_0.get_uint64 (0) + src_1.get_uint64 (0));
			}
		};
		machine vm;
		vm.apply_operator_type_1_0 (set_123::doit, 0);
		vm.apply_operator_type_1_1 (plus_333::doit, 1, 0);
		vm.apply_operator_type_1_2 (plus::doit, 2, 0, 1);
		for (size_t i = 0, n = vm.global_size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << vm.get_global (i);
		}
		std::cout << std::endl;
	}
	return 0;
}
