#ifndef FATVM_6IUKZK40
#define FATVM_6IUKZK40

#include <stdint.h>
#include <cstddef>
#include <stdexcept>
#include <vector>
#include <ostream>

namespace fatvm {
	struct stack_element;

	struct fatvm {
		/**
		 * push const etc.
		 */
		typedef void (*operator_type_1_0) (stack_element& dst);
		/**
		 * copy etc.
		 */
		typedef void (*operator_type_1_1) (stack_element& dst
			, const stack_element& src);
		/**
		 * binary operators.
		 */
		typedef void (*operator_type_1_2) (stack_element& dst
			, const stack_element& src_0, const stack_element& src_1);
		/**
		 * cobinary operators.
		 */
		typedef void (*operator_type_2_1) (
			stack_element& dst_0, stack_element& dst_1
			, const stack_element& src);
		/**
		 * swap etc.
		 */
		typedef void (*operator_type_2_2) (
			stack_element& dst_0, stack_element& dst_1
			, const stack_element& src_0, const stack_element& src_1);
	};

	struct stack_element : fatvm {
		static const uint8_t uint64_type = 0;
		static const uint8_t int64_type = uint64_type + 1;
		static const uint8_t float64_type = int64_type + 1;
		static const uint8_t const_pointer_type = float64_type + 1;
		static const uint8_t pointer_type = const_pointer_type + 1;

		uint8_t type;
		union {
			uint64_t uint64;
			int64_t int64;
			double float64;
			const void* const_pointer;
			void* pointer;
		};

		stack_element ();
		uint64_t get_uint64 (uint64_t none) const;
		stack_element& set_uint64 (uint64_t value);
		int64_t get_int64 (int64_t none) const;
		stack_element& set_int64 (int64_t value);
		double get_float64 (double none) const;
		stack_element& set_float64 (double value);
		const void* get_const_pointer (const void* none) const;
		stack_element& set_const_pointer (const void* value);
		void* get_pointer (void* none) const;
		stack_element& set_pointer (void* value);

		std::ostream& write (std::ostream& out) const;
	};

	class machine_error : public std::runtime_error {
		public: machine_error (const std::string& what);
	};

	struct stack_frame : fatvm {
		/**
		 * start point of local frame
		 */
		size_t stack_index;

		stack_frame ();
	};

	class machine_base : fatvm {
		public:
		typedef std::vector<stack_element> data_array_type;
		typedef std::vector<stack_frame> frame_array_type;
		private:
		data_array_type _data_array;
		frame_array_type _frame_array;
		stack_frame _root_frame;
		stack_frame* _current_frame;
		std::string _msg_buffer;
		protected:
		data_array_type& data_array ();
		const data_array_type& get_data_array () const;
		frame_array_type& frame_array ();
		const frame_array_type& get_frame_array () const;
		stack_frame& current_frame ();
		const stack_frame& get_current_frame () const;
		size_t to_global_index (size_t index) const;
		size_t to_local_index (size_t index) const;
		public:
		machine_base ();
		size_t frame_size () const;
		size_t size () const;
		size_t global_size () const;
		machine_base& push (const stack_element& value)
			throw (machine_error);
		machine_base& push (const stack_element& value, size_t n)
			throw (machine_error);
		machine_base& pop ()
			throw (machine_error);
		machine_base& pop (size_t n)
			throw (machine_error);
		const stack_element& get (size_t index) const 
			throw (machine_error);
		/**
		 * push if need.
		 */
		machine_base& set (size_t index, const stack_element& value)
			throw (machine_error);
		const stack_element& get_global (size_t index) const
			throw (machine_error);
		/**
		 * push if need.
		 */
		machine_base& set_global (size_t index, const stack_element& value)
			throw (machine_error);

		machine_base& apply_operator_type_1_0 (
			operator_type_1_0 fnc, size_t dst) throw (machine_error);
		machine_base& apply_operator_type_1_1 (
			operator_type_1_1 fnc, size_t dst, size_t src) throw (machine_error);
		machine_base& apply_operator_type_1_2 (
			operator_type_1_2 fnc, size_t dst, size_t src_0, size_t src_1)
			throw (machine_error);
		machine_base& apply_operator_type_2_1 (
			operator_type_2_1 fnc, size_t dst_0, size_t dst_1, size_t src)
			throw (machine_error);
		machine_base& apply_operator_type_2_2 (
			operator_type_2_2 fnc, size_t dst_0, size_t dst_1
			, size_t src_0, size_t src_1) throw (machine_error);
	};
};

std::ostream& operator << (std::ostream& out
	, const fatvm::stack_element& x);

#endif /* end of include guard: FATVM_6IUKZK40 */
