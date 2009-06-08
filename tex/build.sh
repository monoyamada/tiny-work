#!/bin/sh
usage="$0 <tex-file-name-without-ext>"
if [ $# -lt 1 ]; then
	echo $usage
	exit
fi
out_dir="output"
opts="-draftmod -halt-on-error"
opts="-encoding=utf8 -draftmod -halt-on-error"
opt_dir="-output-directory=$out_dir"
latex $opts $opt_dir $1
