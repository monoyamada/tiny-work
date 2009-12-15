if !exists('loaded_snippet') || &cp
    finish
endif

let st = g:snip_start_tag
let et = g:snip_end_tag
let cd = g:snip_elem_delim

"
" this is not for tex but latex, but plugin snippets_emu looks takes convension
" to look snippet definition file:
" <file>.<ext> |-> <ext>_<any>.vim
" . The custom is that both tex and latex files have the extension .tex.

" default section snippets are clever, make more easy to remember
"
exec "Snippet sec1 \\section{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec2 \\subsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec3 \\subsubsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
exec "Snippet sec4 \\subsubsubsection{".st."name".et."}\\label{sec:".st."name:substitute(@z,'.','\\l&','g')".et."}<CR>".st.et
"
" environments
"
exec "Snippet def \\begin{definition}<CR>".st.et."<CR>\\end{definition}"
exec "Snippet obs \\begin{observe}<CR>".st.et."<CR>\\end{observe}"
exec "Snippet todo \\begin{todo}<CR>".st.et."<CR>\\end{todo}"
exec "Snippet prop \\begin{proposition}<CR>".st.et."<CR>\\end{proposition}"
exec "Snippet proof \\begin{proof}<CR>".st.et."<CR>\\end{proof}"
exec "Snippet eqs \\begin{equation}\\begin{split}<CR><Tab>".st.et."<CR>\\end{split}\\end{equation}"
exec "Snippet graph \\begin{equation}\\xymatrix{<CR>".st.et."<CR>}\\end{equation}"
exec "Snippet def.map \\begin{equation}\\begin{split}<CR><Tab>".st.et.":&\to<CR><Tab>&\\mapsto<CR>\\end{split}\\end{equation}"
exec "Snippet item \\begin{itemize}<CR><Tab>\\item ".st.et."<CR>\\end{itemize}"
exec "Snippet table \\begin{table}[!htbp]<CR><Tab>\\begin{center}\\begin{tabular}{cc}\\hline<CR>& \\\\ \\hline<CR>\\end{tabular}\\end{center}<CR>\\caption{}<CR>\\end{table}"
exec "Snippet figure \\begin{figure}[htbp]<CR><Tab>\\begin{center}<CR>\\hincludegraphics[width=1.0\\textwidth]{figure.png}<CR>\\end{center}<CR>\\caption{}\\label{}<CR>\\end{figure}"
