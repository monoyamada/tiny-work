#
#CCACHE = ccache
#CC = $(CCACHE) gcc
#CPP = $(CCACHE) g++
#CXX = $(CCACHE) g++
#
ADD_SO_PATH =
ADD_CFLAGS =-g -DDEBUG -Wall $(ADD_SO_PATH)
PKGS =
UBUNTU_VERSION = $(shell lsb_release -rs)
ifdef $(PKGS)
	PKG_CFLAGS = $(shell pkg-config --cflags $(PKGS))
	PKG_LDLIBS = $(shell pkg-config --libs $(PKGS))
endif
CFLAGS += ${ADD_CFLAGS} $(PKG_CFLAGS)
CPPFLAGS += -std=c++0x ${ADD_CFLAGS} $(PKG_CFLAGS)
LDLIBS += $(PKG_LDLIBS)

LIB_INCS = debug.h utf8_helper.h fatvm.h
LIB_SRCS = debug.cpp utf8_helper.cpp fatvm.cpp
LIB_FILES = $(LIB_INCS) $(LIB_SRCS)

APP_INCS = 
APP_SRCS = 
APP_EXES = 

TEST_INCS =
TEST_SRCS = test-stack.cpp
TEST_EXES = test-stack

all: $(APP_EXES) $(TEST_EXES)
.PHONY: all

lesson-union: lesson-union.cpp $(LIB_SRCS) $(LIB_INCS)
lesson-utf8: lesson-utf8.cpp $(LIB_SRCS) $(LIB_INCS)
test-stack: $(LIB_SRCS) $(LIB_INCS)

.PHONY: clean
clean:
	rm -rf *.o *.so $(APP_EXES) $(TEST_EXES)
