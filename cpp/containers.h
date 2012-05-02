#ifndef CONTAINERS_2K6YWPZ1
#define CONTAINERS_2K6YWPZ1

#include "debug.h"
#include "common.h"
#include <sstream>
#include <cstring>

namespace fatvm {
	template <typename T>
	class simple_array {
		typedef T value_type;
		private:
		value_type* _array;
		size_t _size;
		public:
		simple_array () : _array (0), _size (0) {
		}
		simple_array (value_type* array, size_t size) : _array (array), _size (size) {
		}
		simple_array& set_array (value_type* array, size_t size) {
			this->_array = array;
			this->_size = size;
			return *this;
		}
		value_type* array () {
			return this->_array;
		}
		const value_type* get_array () const {
			return this->_array;
		}
		size_t size () const {
			return this->_size;
		}
		value_type& operator[] (size_t index) {
			return *(this->_array + index);
		}
		const value_type& operator[] (size_t index) const {
			return *(this->_array + index);
		}
		const value_type& at (size_t index) const throw (fatvm_error) {
			return this->get (index);
		}
		const value_type& get (size_t index) const throw (fatvm_error) {
			if (this->_size <= index) {
				std::string str;
				str.reserve (128);
				std::stringstream buffer (str);
				out_of_range_msg (buffer, 0, this->_size, index, "get", __FILE__, __LINE__);
				throw fatvm_error (buffer.str ());
			}
			return *(this->_array + index);
		}
		simple_array& set (size_t index, const value_type& value) throw (fatvm_error) {
			if (this->_size <= index) {
				std::string str;
				str.reserve (128);
				std::stringstream buffer (str);
				out_of_range_msg (buffer, 0, this->_size, index, "set", __FILE__, __LINE__);
				throw fatvm_error (buffer.str ());
			}
			*(this->_array + index) = value;
			return *this;
		}
	};

	template <typename T>
	class simple_vector {
		public:
		typedef T value_type;
		private:
		const size_t _max_array_size;
		const size_t _min_array_size;
		size_t _array_size;
		size_t _size;
		value_type* _array;
		protected:
		void ensure_array (size_t size) {
			if (size <= this->_array_size) {
				return;
			} else if (this->_max_array_size < size) {
				std::string str;
				str.reserve (128);
				std::stringstream buffer (str);
				out_of_limit_msg (buffer, this->_max_array_size, size
					, "array size", __FILE__, __LINE__);
				throw fatvm_error (buffer.str ());
			}
			size_t new_size = 0 < this->_size ? (this->_size << 1) : this->_min_array_size;
			for (; new_size < size; new_size = (new_size << 1)) {
			}
			if (this->_max_array_size < new_size) {
				new_size = this->_max_array_size;
			}
			value_type* new_array = (value_type*) malloc (new_size * sizeof (value_type));
			if (this->_array != 0) {
				if (0 < this->_size) {
					memcpy (new_array, this->_array, (this->_size) * sizeof (value_type));
				}
				this->delete_array (this->_array);
			}
			this->_array = new_array;
			this->_array_size = new_size;
		}
		public:
		value_type* new_array (size_t n) const {
			return (value_type*) malloc (n * sizeof (value_type));
		}
		void delete_array (value_type* array) const {
			if (array == 0) {
				return;
			}
			free (array);
		}
		simple_vector (size_t max_array_size, size_t min_array_size)
			: _max_array_size (max_array_size), _min_array_size (min_array_size)
			, _array_size (0), _size (0), _array (0) {
		}
		~simple_vector () {
			if (this->_array != 0) {
				this->delete_array (this->_array);
				this->_array = 0;
			}
		}
		size_t max_array_size () const {
			return this->_max_array_size;
		}
		size_t min_array_size () const {
			return this->_min_array_size;
		}
		size_t array_size () const {
			return this->_array_size;
		}
		size_t size () const {
			return this->_size;
		}
		value_type& operator[] (size_t index) {
			return *(this->_array + index);
		}
		const value_type& operator[] (size_t index) const {
			return *(this->_array + index);
		}
		const value_type& at (size_t index) const throw (fatvm_error) {
			return this->get (index);
		}
		const value_type& get (size_t index) const throw (fatvm_error) {
			if (this->size () <= index) {
				std::string str;
				str.reserve (128);
				std::stringstream buffer (str);
				out_of_range_msg (buffer, 0, this->size (), index, "get", __FILE__, __LINE__);
				throw fatvm_error (buffer.str ());
			}
			return *(this->_array + index);
		}
		simple_vector& set (size_t index, const value_type& value) throw (fatvm_error) {
			if (this->size () <= index) {
				std::string str;
				str.reserve (128);
				std::stringstream buffer (str);
				out_of_range_msg (buffer, 0, this->size (), index, "get", __FILE__, __LINE__);
				throw fatvm_error (buffer.str ());
			}
			*(this->_array + index) = value;
			return *this;
		}
		const value_type& front (const value_type& none) const {
			if (this.size () < 1) {
				return none;
			}
			return *(this->_array);
		}
		const value_type& back (const value_type& none) const {
			if (this.size () < 1) {
				return none;
			}
			return *(this->_array + (this->_size - 1));
		}
		simple_vector& push_back (const value_type& value) throw (fatvm_error) {
			if (this->array_size () <= this->_size) {
				this->ensure_array (this->_size + 1);
			}
			*(this->_array + ((this->_size)++)) = value;
			return *this;
		}
		simple_vector& push_back (const value_type& value, size_t n) throw (fatvm_error) {
			if (n < 1) {
				return *this;
			} else if (this->array_size () < this->_size + n) {
				this->ensure_array (this->_size + n);
			}
			while (0 < n--) {
				*(this->_array + ((this->_size)++)) = value;
			}
			return *this;
		}
		simple_vector& pop_back () {
			if (this->_size < 1) {
				return *this;
			}
			--(this->_size);
			return *this;
		}
		simple_vector& pop_back (size_t n) {
			if (this->_size < n) {
				this->_size = 0;
			} else {
				this->_size -= n;
			}
			return *this;
		}
		size_t copy_array (value_type* out, size_t n) const {
			n = std::min (n, this._size);
			if (n < 1) {
				return 0;
			}
			memcpy (out, this->_array, n);
			return n;
		}
	};
};

#endif /* end of include guard: CONTAINERS_2K6YWPZ1 */
