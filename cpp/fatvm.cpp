#include "fatvm.h"
#include "debug.h"
#include <iostream>
#include <sstream>
#include <algorithm>

using namespace fatvm;

std::ostream& header_msg (std::ostream& out, const char* file, int line) {
	if (file == 0) {
		return out << "[] ";
	}
	return out << "[" << file << ":" << line << "] ";
}

std::ostream& out_of_range_msg (std::ostream& out
	, int64_t begin, int64_t end, int64_t index 
	, const char* what, const char* file, int line) {
	if (what == 0) {
		what = "";
	}
	return header_msg (out, file, line) << what << " " << index 
		<< " not in [" << begin << ", " << end << ")";
}

std::ostream& no_such_msg (std::ostream& out
	, const char* what, const char* file, int line) {
	if (what == 0) {
		what = "object";
	}
	return header_msg (out, file, line) << "no such " << what << " exists";
}

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

std::ostream& operator << (std::ostream& out, const stack_element& x) {
	return x.write (out);
}

stack_frame::stack_frame () : stack_index (0) {
}

machine_error::machine_error (const std::string& what)
	: std::runtime_error (what) {
}

machine_base::machine_base () {
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

size_t machine_base::frame_size () const {
	return this->get_frame_array().size ();
}

size_t machine_base::size () const {
	return this->global_size () - this->get_current_frame ().stack_index;
}

size_t machine_base::global_size () const {
	return this->get_data_array ().size ();
}

machine_base& machine_base::push (const stack_element& value)
	throw (machine_error) {
	data_array_type& array = this->data_array ();
	array.push_back (value);
	return *this;
}

machine_base& machine_base::push (const stack_element& value, size_t n)
	throw (machine_error) {
	data_array_type& array = this->data_array ();
	array.insert (array.end(), n, value);
	return *this;
}

machine_base& machine_base::pop () throw (machine_error) {
	if (this->size () < 1) {
		std::stringstream buffer (this->_msg_buffer);
		no_such_msg (buffer, "stack element", __FILE__, __LINE__);
		throw machine_error (buffer.str ());
	}
	data_array_type& array = this->data_array ();
	array.pop_back ();
	return *this;
}

machine_base& machine_base::pop (size_t n) throw (machine_error) {
	data_array_type& array = this->data_array ();
	if (this->size () < n) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->size (), n
			, "pop number", __FILE__, __LINE__);
		throw machine_error (buffer.str ());
	}
	array.erase (array.end () - n, array.end ());
	return *this;
}

size_t machine_base::to_global_index (size_t index) const {
	return this->get_current_frame ().stack_index + index;
}

size_t machine_base::to_local_index (size_t index) const {
	return index - this->get_current_frame ().stack_index;
}

const stack_element& machine_base::get (size_t index) const throw (machine_error) {
	if (this->size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->size (), index
			, "stack element", __FILE__, __LINE__);
		throw machine_error (buffer.str ());
	}
	return this->get_data_array ()[this->to_global_index (index)];
}

machine_base& machine_base::set (size_t index, const stack_element& value) throw (machine_error) {
	return this->set_global (this->to_global_index (index), value);
}

const stack_element& machine_base::get_global (size_t index) const throw (machine_error) {
	if (this->global_size () <= index) {
		std::stringstream buffer (this->_msg_buffer);
		out_of_range_msg (buffer, 0, this->global_size (), index
			, "stack element", __FILE__, __LINE__);
		throw machine_error (buffer.str ());
	}
	return this->get_data_array ()[index];
}

machine_base& machine_base::set_global (size_t index, const stack_element& value) throw (machine_error) {
	if (this->global_size () <= index) {
		this->push (stack_element (), index - this->global_size () + 1);
	}
	this->data_array ()[index] = value;
	return *this;
}

machine_base& machine_base::apply_operator_type_1_0 (
	operator_type_1_0 fnc, size_t dst) throw (machine_error) {
	if (this->size () <= dst) {
		this->push (stack_element (), dst - this->size () + 1);
	}
	stack_element x_dst = this->get (dst);
	fnc (x_dst);
	return this->set (dst, x_dst);
}

machine_base& machine_base::apply_operator_type_1_1 (
	operator_type_1_1 fnc, size_t dst, size_t src) throw (machine_error) {
	if (this->size () <= dst) {
		this->push (stack_element (), dst - this->size () + 1);
	}
	const stack_element& x_src = this->get (src);
	stack_element x_dst = this->get (dst);
	fnc (x_dst, x_src);
	return this->set (dst, x_dst);
}

machine_base& machine_base::apply_operator_type_1_2 (
	operator_type_1_2 fnc, size_t dst, size_t src_0, size_t src_1)
	throw (machine_error) {
	if (this->size () <= dst) {
		this->push (stack_element (), dst - this->size () + 1);
	}
	const stack_element& x_src_0 = this->get (src_0);
	const stack_element& x_src_1 = this->get (src_1);
	stack_element x_dst = this->get (dst);
	fnc (x_dst, x_src_0, x_src_1);
	return this->set (dst, x_dst);
}

machine_base& machine_base::apply_operator_type_2_1 (
	operator_type_2_1 fnc, size_t dst_0, size_t dst_1, size_t src)
	throw (machine_error) {
	if (this->size () <= dst_0 || this->size () <= dst_1) {
		this->push (stack_element ()
			, std::max (dst_0, dst_1) - this->size () + 1);
	}
	const stack_element& x_src = this->get (src);
	stack_element x_dst_0 = this->get (dst_0);
	stack_element x_dst_1 = this->get (dst_1);
	fnc (x_dst_0, x_dst_1, x_src);
	this->set (dst_0, x_dst_0);
	return this->set (dst_1, x_dst_1);
}

machine_base& machine_base::apply_operator_type_2_2 (
	operator_type_2_2 fnc, size_t dst_0, size_t dst_1
	, size_t src_0, size_t src_1) throw (machine_error) {
	if (this->size () <= dst_0 || this->size () <= dst_1) {
		this->push (stack_element ()
			, std::max (dst_0, dst_1) - this->size () + 1);
	}
	const stack_element& x_src_0 = this->get (src_0);
	const stack_element& x_src_1 = this->get (src_1);
	stack_element x_dst_0 = this->get (dst_0);
	stack_element x_dst_1 = this->get (dst_1);
	fnc (x_dst_0, x_dst_1, x_src_0, x_src_1);
	this->set (dst_0, x_dst_0);
	return this->set (dst_1, x_dst_1);

}
