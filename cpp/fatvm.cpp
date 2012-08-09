#include "fatvm.h"
#include "debug.h"
#include <sstream>
#include <algorithm>

using namespace fatvm;

const char* stack_element_type_name (uint8_t type) {
	switch (type) {
		case stack_element::uint64_type:
		return "uint64";
		case stack_element::int64_type:
		return "int64";
		case stack_element::float64_type:
		return "float64";
		case stack_element::const_pointer_type:
		return "const_pointer";
		case stack_element::pointer_type:
		return "pointer";
		default:
		return "unknown";
	}
}

stack_element::stack_element () : type (uint64_type), uint64 (0) {
}

uint64_t stack_element::get_uint64 (uint64_t none) const {
	return this->type == uint64_type ? this->uint64 : none;
}

stack_element& stack_element::set_uint64 (uint64_t value) {
	this->type = uint64_type;
	this->uint64 = value;
	return *this;
}

int64_t stack_element::get_int64 (int64_t none) const {
	return this->type == int64_type ? this->int64 : none;
}

stack_element& stack_element::set_int64 (int64_t value) {
	this->type = int64_type;
	this->int64 = value;
	return *this;
}

double stack_element::get_float64 (double none) const {
	return this->type == float64_type ? this->float64 : none;
}

stack_element& stack_element::set_float64 (double value) {
	this->type = float64_type;
	this->float64 = value;
	return *this;
}

std::ostream& stack_element::write (std::ostream& out) const {
	const char* name = stack_element_type_name (this->type);
	switch (this->type) {
		case stack_element::uint64_type:
		return out << this->uint64 << ':' << name;
		case stack_element::int64_type:
		return out << this->int64 << ':' << name;
		case stack_element::float64_type:
		return out << this->float64 << ':' << name;
		case stack_element::const_pointer_type:
		return out << this->const_pointer << ':' << name;
		case stack_element::pointer_type:
		return out << this->pointer << ':' << name;
		default:
		return out << this->uint64 << ':' << name;
	}
}

stack_frame::stack_frame () : stack_index (0) {
}

machine_config::machine_config ()
	: max_data_array_size (1024)
	, min_data_array_size (128)
	, max_frame_array_size (256)
	, min_frame_array_size (4) {
}

const bool _fatvm_direct_edit = true;
const bool _fatvm_set_anyway = true;

const machine_config default_config;

machine_base::machine_base () 
	: _data_array (default_config.max_data_array_size, default_config.min_data_array_size)
	, _frame_array (default_config.max_frame_array_size, default_config.min_frame_array_size) {
	this->_current_frame = &(this->_root_frame);
	this->_msg_buffer.reserve (256);
}

machine_base::machine_base (const machine_config& config) 
	: _data_array (config.max_data_array_size, config.min_data_array_size)
	, _frame_array (config.max_frame_array_size, config.min_frame_array_size) {
	this->_current_frame = &(this->_root_frame);
	this->_msg_buffer.reserve (256);
}

machine_base::data_array_type& machine_base::data_array () {
	return this->_data_array;
}

const machine_base::data_array_type& machine_base::get_data_array () const {
	return this->_data_array;
}

machine_base::frame_array_type& machine_base::frame_array () {
	return this->_frame_array;
}

const machine_base::frame_array_type& machine_base::get_frame_array () const {
	return this->_frame_array;
}

stack_frame& machine_base::current_frame () {
	return *(this->_current_frame);
}

const stack_frame& machine_base::get_current_frame () const {
	return *(this->_current_frame);
}

size_t machine_base::to_global_index (size_t index) const {
	return this->get_current_frame ().stack_index + index;
}

size_t machine_base::to_local_index (size_t index) const {
	return index - this->get_current_frame ().stack_index;
}

size_t machine_base::frame_size () const {
	return this->get_frame_array().size ();
}

size_t machine_base::data_size () const {
	return this->global_data_size () - this->get_current_frame ().stack_index;
}

size_t machine_base::global_data_size () const {
	return this->get_data_array ().size ();
}

stack_element& machine_base::local_data (size_t index)
	throw (fatvm_error) {
	if (this->data_size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->data_size (), index
			, "stack element", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	return this->data_array ()[this->to_global_index (index)];
}

const stack_element& machine_base::get_local_data (size_t index) const 
	throw (fatvm_error) {
	if (this->data_size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->data_size (), index
			, "stack element", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	return this->get_data_array ()[this->to_global_index (index)];
}

stack_element& machine_base::global_data (size_t index) throw (fatvm_error) {
	if (this->global_data_size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->global_data_size (), index
			, "stack element", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	return this->data_array ()[index];
}

const stack_element& machine_base::get_global_data (size_t index) const 
	throw (fatvm_error) {
	if (this->global_data_size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->global_data_size (), index
			, "stack element", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	return this->get_data_array ()[index];
}

machine_base& machine_base::push_data ()
	throw (fatvm_error) {
	return this->push_data (stack_element ());
}

machine_base& machine_base::push_data (size_t n)
	throw (fatvm_error) {
	return this->push_data (stack_element (), n);
}


machine_base& machine_base::push_data (const stack_element& value)
	throw (fatvm_error) {
	data_array_type& array = this->data_array ();
	array.push_back (value);
	return *this;
}

machine_base& machine_base::push_data (const stack_element& value, size_t n)
	throw (fatvm_error) {
	data_array_type& array = this->data_array ();
	//array.insert (array.end(), n, value);
	array.push_back (value, n);
	return *this;
}

machine_base& machine_base::pop_data () throw (fatvm_error) {
	if (this->data_size () < 1) {
		std::stringstream buffer (this->_msg_buffer);
		no_such_msg (buffer, "stack element", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	data_array_type& array = this->data_array ();
	array.pop_back ();
	return *this;
}

machine_base& machine_base::pop_data (size_t n) throw (fatvm_error) {
	data_array_type& array = this->data_array ();
	if (this->data_size () < n) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->data_size (), n
			, "pop number", __FILE__, __LINE__);
		throw fatvm_error (buffer.str ());
	}
	//array.erase (array.end () - n, array.end ());
	array.pop_back (n);
	return *this;
}

const stack_element& machine_base::get_data (size_t index) const 
	throw (fatvm_error) {
	return this->get_local_data (index);
}

machine_base& machine_base::set_data (size_t index, const stack_element& value) 
	throw (fatvm_error) {
	this->local_data (index) = value;
	return *this;
}

machine_base& machine_base::apply_operator_type_1_0 (
	operator_type_1_0 fnc, size_t dst) throw (fatvm_error) {
	if (_fatvm_set_anyway && this->data_size () <= dst) {
		this->push_data (dst - this->data_size () + 1);
	}
	if (_fatvm_direct_edit) {
		fnc (this->local_data (dst));
	} else {
		stack_element x_dst = this->get_data (dst);
		fnc (x_dst);
		this->set_data (dst, x_dst);
	}
	return *this;
}

machine_base& machine_base::apply_operator_type_1_1 (
	operator_type_1_1 fnc, size_t dst, size_t src) throw (fatvm_error) {
	if (_fatvm_set_anyway && this->data_size () <= dst) {
		this->push_data (dst - this->data_size () + 1);
	}
	const stack_element& x_src = this->get_data (src);
	if (_fatvm_direct_edit) {
		fnc (this->local_data (dst), x_src);
	} else {
		stack_element x_dst = this->get_data (dst);
		fnc (x_dst, x_src);
		this->set_data (dst, x_dst);
	}
	return *this;
}

machine_base& machine_base::apply_operator_type_1_2 (
	operator_type_1_2 fnc, size_t dst, size_t src_0, size_t src_1)
	throw (fatvm_error) {
	if (_fatvm_set_anyway && this->data_size () <= dst) {
		this->push_data (dst - this->data_size () + 1);
	}
	const stack_element& x_src_0 = this->get_data (src_0);
	const stack_element& x_src_1 = this->get_data (src_1);
	if (_fatvm_direct_edit) {
		fnc (this->local_data (dst), x_src_0, x_src_1);
	} else {
		stack_element x_dst = this->get_data (dst);
		fnc (x_dst, x_src_0, x_src_1);
		this->set_data (dst, x_dst);
	}
	return *this;
}

machine_base& machine_base::apply_operator_type_2_1 (
	operator_type_2_1 fnc, size_t dst_0, size_t dst_1, size_t src)
	throw (fatvm_error) {
	if (_fatvm_set_anyway 
		&& (this->data_size () <= dst_0 || this->data_size () <= dst_1)) {
		this->push_data (std::max (dst_0, dst_1) - this->data_size () + 1);
	}
	const stack_element& x_src = this->get_data (src);
	if (_fatvm_direct_edit) {
		fnc (this->local_data (dst_0), this->local_data (dst_1), x_src);
	} else {
		stack_element x_dst_0 = this->get_data (dst_0);
		stack_element x_dst_1 = this->get_data (dst_1);
		fnc (x_dst_0, x_dst_1, x_src);
		this->set_data (dst_0, x_dst_0);
		this->set_data (dst_1, x_dst_1);
	}
	return *this;
}

machine_base& machine_base::apply_operator_type_2_2 (
	operator_type_2_2 fnc, size_t dst_0, size_t dst_1
	, size_t src_0, size_t src_1) throw (fatvm_error) {
	if (_fatvm_set_anyway 
		&& (this->data_size () <= dst_0 || this->data_size () <= dst_1)) {
		this->push_data (std::max (dst_0, dst_1) - this->data_size () + 1);
	}
	const stack_element& x_src_0 = this->get_data (src_0);
	const stack_element& x_src_1 = this->get_data (src_1);
	if (_fatvm_direct_edit) {
		fnc (this->local_data (dst_0), this->local_data (dst_1), x_src_0, x_src_1);
	} else {
		stack_element x_dst_0 = this->get_data (dst_0);
		stack_element x_dst_1 = this->get_data (dst_1);
		fnc (x_dst_0, x_dst_1, x_src_0, x_src_1);
		this->set_data (dst_0, x_dst_0);
		this->set_data (dst_1, x_dst_1);
	}
	return *this;
}
