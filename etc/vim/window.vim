let s:fixed = 1
if s:fixed
	if &cp || exists("loaded_directory")
		finish
	endif
	let loaded_directory = 1
endif

let s:cpo_old = &cpo
set cpo&vim

let &cpo = s:cpo_old
unlet s:cpo_old
