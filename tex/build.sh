#!/bin/sh
usage="$0 <tex-file-name-without-ext>"
no_dir="could not find directory"

if [ $# -lt 1 ]; then
	echo $usage
	exit
fi

out_dir="output"
if [ ! -d $out_dir ]; then
	mkdir $out_dir
fi

opts="-halt-on-error -kanji=utf8 -output-directory=$out_dir"
platex $opts $opt_dir $1
dvipdfmx -o $out_dir/$1.pdf $out_dir/$1.dvi
