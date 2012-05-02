#include "fatvm.h"
#include "debug.h"
#include "containers.h"
#include <iostream>
#include <iterator> 
#include <typeinfo> 

using namespace fatvm;

std::ostream& operator << (std::ostream& out, const stack_element& x) {
	return x.write (out);
}

struct op_index_2 {
	uint8_t index_0;
	uint8_t index_1;
};

struct op_index_3 {
	uint8_t index_0;
	uint8_t index_1;
	uint8_t index_2;
};

struct op_index_value {
	uint8_t index;
	union {
		uint8_t uint8;
		int8_t int8;
	};
};

struct opcode {
	static const uint8_t op_none = 0;
	static const uint8_t op_apply_large = op_none + 1;
	static const uint8_t op_push_uint16 = op_apply_large + 1;
	static const uint8_t op_push_int16 = op_push_uint16 + 1;
	static const uint8_t op_set_uint8 = op_push_int16 + 1;
	static const uint8_t op_set_int8 = op_set_uint8 + 1;
	static opcode encode_none () {
		return opcode (op_none);
	}
	static opcode encode_apply_large (uint16_t index) {
		opcode code = opcode (op_apply_large);
		code.uint16 = index;
		return code;
	}
	static opcode encode_push_uint16 (uint16_t value) {
		opcode code = opcode (op_push_uint16);
		code.uint16 = value;
		return code;
	}
	static opcode encode_push_int16 (int16_t value) {
		opcode code = opcode (op_push_int16);
		code.int16 = value;
		return code;
	}
	static opcode encode_set_uint8 (uint8_t index, uint8_t value) {
		opcode code = opcode (op_set_uint8);
		code.index_value.index = index;
		code.index_value.uint8 = value;
		return code;
	}
	static opcode encode_set_int8 (uint8_t index, int8_t value) {
		opcode code = opcode (op_set_int8);
		code.index_value.index = index;
		code.index_value.int8 = value;
		return code;
	}

	uint8_t id;
	union {
		op_index_2 index_2;
		op_index_3 index_3;
		op_index_value index_value;
		uint16_t uint16;
		int16_t int16;
	};
	opcode () : id (op_none) {
	}
	opcode (uint8_t _id) : id (_id) {
	}
};

struct op_long_index_4 {
	uint16_t index_0;
	uint16_t index_1;
	uint16_t index_2;
	uint16_t index_3;
};

struct opcode_large {
	static const uint8_t op_none = 0;
	static const uint8_t op_push_uint64 = op_none + 1;
	static const uint8_t op_push_int64 = op_push_uint64 + 1;
	static const uint8_t op_push_float64 = op_push_int64 + 1;
	static const uint8_t op_push_const_pointer = op_push_float64 + 1;
	static const uint8_t op_push_pointer = op_push_const_pointer + 1;
	static const uint8_t op_apply_operator_1_0 = op_push_pointer + 1;
	static const uint8_t op_apply_operator_1_1 = op_apply_operator_1_0 + 1;
	static const uint8_t op_apply_operator_1_2 = op_apply_operator_1_1 + 1;
	static const uint8_t op_apply_operator_2_1 = op_apply_operator_1_2 + 1;

	static opcode_large encode_apply_operator_1_2 (uint16_t fnc, uint16_t dst
		, uint16_t src_0, uint16_t src_1) {
		opcode_large code = opcode_large (op_apply_operator_1_2);
		STD_DEBUG << int(code.id) << std::endl;
		code.index_4.index_0 = fnc;
		code.index_4.index_1 = dst;
		code.index_4.index_2 = src_0;
		code.index_4.index_3 = src_1;
		STD_DEBUG << int(code.id) << std::endl;
		return code;
	}
	uint8_t id;
	union {
		op_long_index_4 index_4;
		uint64_t uint64;
		int64_t int64;
		double float64;
		const void* const_pointer;
		void* pointer;
	};
	opcode_large () : id (op_none) {
	}
	opcode_large (uint8_t _id) : id (_id) {
	}
	/*
	opcode_large (const opcode_large& x) : id (x.id), as (x.as) {
	}
	opcode_large& operator= (const opcode_large& x) {
		this->id = x.id;
		this->as = x.as;
		return *this;
	}
	*/
};

struct code_set {
	simple_array <opcode> opcode_array;
	simple_array <opcode_large> opcode_large_array;
};

struct machine : public machine_base {
	simple_vector <operator_type_1_2> operator_type_1_2_array;
	machine () : machine_base (), operator_type_1_2_array (256, 16) {
	}
	const data_array_type& get_data_array () const {
		return machine_base::get_data_array ();
	}
	size_t execute (const code_set& codes, size_t index) {
		opcode code = codes.opcode_array[index];
		if (code.id != opcode::op_apply_large) {
			return this->execute (code, index);
		} else {
			return this->execute (codes.opcode_large_array[code.uint16], index);
		}
	}
	size_t execute (opcode code, size_t index) {
		switch (code.id) {
			case opcode::op_none:
			break;
			case opcode::op_push_uint16: {
				stack_element x;
				this->push_data (x.set_uint64 (code.uint16));
			}
			break;
			default:
			throw fatvm_error ("not yet");
		}
		return ++index;
	}
	size_t execute (const opcode_large& code, size_t index) {
		switch (code.id) {
			case opcode_large::op_none:
			break;
			case opcode_large::op_apply_operator_1_2: {
				const op_long_index_4& indices = code.index_4;
				operator_type_1_2 fnc = this->operator_type_1_2_array.get (indices.index_0);
				this->apply_operator_type_1_2 (fnc, indices.index_1, indices.index_2, indices.index_3);
			}
			break;
			default:
			throw fatvm_error ("not yet");
		}
		return ++index;
	}
};

int main (int argc, char* argv[]) {
	if (true) {
		STD_DEBUG << sizeof (opcode) << " bytes" << std::endl;
		STD_DEBUG << sizeof (opcode_large) << " bytes" << std::endl;
	}
	if (true) {
		/*
		 * looked like output value is determined by limits<T>::max () of operands.
		 * uint64_t + int64_t |-> uint64_t
		 *   cas limits<uint64_t>::max () > limits<int64_t>::max ()
		 * double + uint64_t |-> double
		 *   cas limits<double>::max () > limits<uint64_t>::max ()
		 */
		STD_DEBUG << typeid(uint64_t (123) + uint64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(uint64_t (123) + int64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(int64_t (123) + uint64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(int64_t (123) + int64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(uint64_t (123) + double (456)).name () << std::endl;
		STD_DEBUG << typeid(double (123) + uint64_t (456)).name () << std::endl;

		STD_DEBUG << typeid(uint64_t (123) - uint64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(uint64_t (123) - int64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(int64_t (123) - uint64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(int64_t (123) - int64_t (456)).name () << std::endl;
		STD_DEBUG << typeid(uint64_t (123) - double (456)).name () << std::endl;
		STD_DEBUG << typeid(double (123) - uint64_t (456)).name () << std::endl;
	}
	if (true) {
		opcode* opcodes = new opcode[32];
		size_t n_opcodes = 0;
		opcodes[n_opcodes++] = opcode::encode_push_uint16 (123);
		opcodes[n_opcodes++] = opcode::encode_push_uint16 (456);
		opcodes[n_opcodes++] = opcode::encode_apply_large (0);

		opcode_large* larges = new opcode_large[32];
		size_t n_larges = 0;
		larges[n_larges++] = opcode_large::encode_apply_operator_1_2 (0, 0, 0, 1);

		code_set fnc;
		fnc.opcode_array.set_array (opcodes, n_opcodes);
		fnc.opcode_large_array.set_array (larges, n_larges);

		struct fncs {
			static void plus (stack_element& dst
				, const stack_element& src_0, const stack_element& src_1) {
				dst.set_uint64 (src_0.get_uint64 (0) + src_1.get_uint64 (0));
			}
		};

		machine vm;
		vm.operator_type_1_2_array.push_back (
			fncs::plus
		);

		size_t index = 0;
		while (index < n_opcodes) {
			index = vm.execute (fnc, index);
		}
		const machine::data_array_type& array = vm.get_data_array ();
		for (size_t i = 0, n = array.size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << array.at (i);
		}
		std::cout << std::endl;
	}
	if (true) {
		simple_vector<stack_element> vec (6, 2);
		stack_element value;
		vec.push_back (value.set_uint64 (123));
		vec.push_back (value.set_uint64 (456));
		vec.push_back (value.set_int64 (-123), 2);
		vec.push_back (value.set_int64 (-123));
		for (size_t i = 0, n = vec.size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << vec.get (i);
		}
		std::cout << std::endl;
		try {
			vec.push_back (value.set_float64 (4.56), 2);
		} catch (const fatvm_error& ex) {
			STD_DEBUG << ex.what () << std::endl;
		}
		//vec.pop_back (3);
	}
	if (true) {
		STD_DEBUG << sizeof (stack_element) << " bytes" << std::endl;
	}
	if (true) {
		machine vm;
		stack_element value;
		vm.push_data (value.set_uint64 (123), 1);
		vm.push_data (value.set_int64 (-456), 1);
		vm.push_data (value.set_float64 (7.89), 1);
		const machine::data_array_type& array = vm.get_data_array ();
		/* bug in gcc, cannot find operator <<
		std::copy (vm.data_array ().begin (), vm.data_array ().end ()
			, std::ostream_iterator<stack_element> (std::cout, ", "));
		*/
		for (size_t i = 0, n = array.size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << array.at (i);
		}
		std::cout << std::endl;
	}
	if (true) {
		machine vm;
		stack_element value;
		try {
			vm.set_data (0, value.set_uint64 (123));
			vm.set_data (1, value.set_int64 (-456));
			vm.set_data (2, value.set_float64 (7.89));
		} catch (const fatvm_error& ex) {
			STD_DEBUG << ex.what () << std::endl;
		}
		vm.push_data (value.set_uint64 (123));
		try {
			vm.pop_data (2);
		} catch (const fatvm_error& ex) {
			STD_DEBUG << ex.what () << std::endl;
		}
		const machine::data_array_type& array = vm.get_data_array ();
		for (size_t i = 0, n = array.size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << array.at (i);
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
		const machine::data_array_type& array = vm.get_data_array ();
		for (size_t i = 0, n = array.size (); i < n; ++i) {
			if (i != 0) {
				std::cout << ", ";
			}
			std::cout << array.at (i);
		}
		std::cout << std::endl;
	}
	return 0;
}
