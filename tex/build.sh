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

opts="-halt-on-error"
opt_enc="-kanji=utf8"
opt_dir="-output-directory=$out_dir"
if [ $OS="Windows_NT" ]; then
	opts="$opts $opt_enc $opt_dir"
	platex $opts $opt_dir $1
else
	opts="$opts $opt_enc $opt_dir -draftmod"
	latex $opts $opt_dir $1
fi
