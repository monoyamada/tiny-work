let s:fixed = 0
if s:fixed
	if &cp || exists("loaded_encoding")
		finish
	endif
	let loaded_encoding = 1
endif

let s:cpo_old = &cpo
set cpo&vim

command! -nargs=? -complete=customlist,s:listEncoding Encoding call s:changeEncoding('<args>') 
"
" frequent used encodings
"
function! s:listEncoding(ArgLead, CmdLine, CusorPos)
	return [
\		'utf-8'
\		, 'euc-jp'
\		, 'sjis'
\		, 'iso-2022-jp'
\	]
endfunction
"
" read file with the specified encoding
"
function! s:changeEncoding(enc)
	if a:enc == ''
		execute ':set enc?'
	else
		execute ':e ++enc=' . a:enc
	endif
	return 1
endfunction

let &cpo = s:cpo_old
unlet s:cpo_old
