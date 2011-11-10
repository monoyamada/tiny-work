#!/bin/sh
usage="$0 <tex-file-name-without-ext>"
no_dir="could not find directory"

if [ $# -lt 1 ]; then
	echo $usage
	exit
fi

out_dir="output"
if [ ! -d $out_dir ]; then
	mkdir $out_di#r
fi

opts_encode="-kanji=utf8"
opts="-halt-on-error $opts_encode -output-directory=$out_dir"
platex $opts $1
pbibtex $opts_encode $out_dir/$1
dvipdfmx -o $out_dir/$1.pdf $out_dir/$1.dvi
cp output/note.pdf //NETWALKER/share/doc/.
