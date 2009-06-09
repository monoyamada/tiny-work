#!/bin/sh
usage="$0 <tex-file-name-without-ext>"
no_dir="could not find directory"

if [ $# -lt 1 ]; then
	echo $usage
	exit
fi

out_dir="output"
opts="-draftmod -halt-on-error"
opts="-encoding=utf8 -draftmod -halt-on-error"
if [ -d $out_dir ]; then
	a=""
else
	mkdir $out_dir
fi

opt_dir="-output-directory=$out_dir"
latex $opts $opt_dir $1
