if !exists('loaded_snippet') || &cp
    finish
endif

let st = g:snip_start_tag
let et = g:snip_end_tag
let cd = g:snip_elem_delim

"this is not for tex but latex syntax.

" default section snippets are clever, make more easy to remember
exec "Snippet sec1 \\section{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec2 \\subsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec3 \\subsubsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec4 \\subsubsubsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et

exec "Snippet def \\begin{definition}<CR>".st.et."<CR>\\end{definition}"
exec "Snippet obs \\begin{observation}<CR>".st.et."<CR>\\end{observation}"
exec "Snippet todo \\begin{todo}<CR>".st.et."<CR>\\end{todo}"
exec "Snippet prop \\begin{proposition}<CR>".st.et."<CR>\\end{proposition}"
exec "Snippet proof \\begin{proof}<CR>".st.et."<CR>\\end{proof}"
exec "Snippet eqs \\begin{equation}\\begin{split}<CR><tab>".st.et."<CR>\\end{split}\\end{equation}"
exec "Snippet graph \\begin{equation}\\xymatrix{<CR>".st.et."<CR>}\\end{equation}"
