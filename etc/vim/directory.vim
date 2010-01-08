let s:fixed = 1
if s:fixed
	if &cp || exists("loaded_directory")
		finish
	endif
	let loaded_directory = 1
endif

let s:cpo_old = &cpo
set cpo&vim

command! -nargs=0 FilePaste call s:pasteCurrentFile()
command! -nargs=0 DirGoToHere call  s:changeCurrentDirectory(expand("%:p:h"))
command! -nargs=? -complete=dir DirGoTo call s:changeCurrentDirectory('<args>') 
command! -nargs=0 DirPaste call s:pasteCurrentDirectory() 

"
" paste current file.
"
function! s:pasteCurrentFile()
	call append(expand('.'), expand('%:p'))
endfunction
"
" paste current directory.
"
function! s:pasteCurrentDirectory()
	call append(expand('.'), getcwd())
endfunction
"
" change directory if the specified directory is existing.
" return 1 iff successed to change current directory, 0 otherwise.
"
function! s:changeCurrentDirectory(directory)
	let s:path = '.'
	let s:todo = 0
	if a:directory == ''
		let s:path = getcwd()
	elseif isdirectory(a:directory)
		let s:path = fnamemodify(a:directory, ":p")
		let s:todo = 1
	elseif filereadable(a:directory)
		let s:path = fnamemodify(a:directory, ":p:h")
		let s:todo = 1
	else
		echo "failed"
		return 0
	endif
	if s:todo
		execute 'lcd' . s:path
	endif
	echo getcwd()
	return 1
endfunction

let &cpo = s:cpo_old
unlet s:cpo_old
