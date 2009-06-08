let st = g:snip_start_tag
let et = g:snip_end_tag
let cd = g:snip_elem_delim

"
" wiki {
exec "Snippet wiki.h1 = ".st.et." ="
exec "Snippet wiki.h2 == ".st.et." =="
exec "Snippet wiki.h3 === ".st.et." ==="
exec "Snippet wiki.h4 ==== ".st.et." ===="
exec "Snippet wiki.h5 ===== ".st.et." ====="
exec "Snippet wiki.h5 ===== ".st.et." ====="

exec "Snippet wiki.h5 ===== ".st.et." ====="
"}
"
" tex {
exec "Snippet tex.def \\begin{definition}<CR>".st.et."<CR>\\end{definition}"
exec "Snippet tex.obs \\begin{observation}<CR>".st.et."<CR>\\end{observation}"
exec "Snippet tex.todo \\begin{todo}<CR>".st.et."<CR>\\end{todo}"
exec "Snippet tex.prob \\begin{problem}<CR>".st.et."<CR>\\end{problem}"
exec "Snippet tex.prop \\begin{proposition}<CR>".st.et."<CR>\\end{proposition}"
exec "Snippet tex.proof \\begin{proof}<CR>".st.et."<CR>\\end{proof}"
exec "Snippet tex.eqs \\begin{equation}\\begin{split}<CR>".st.et."<CR>\\end{split}\\end{equation}"
exec "Snippet tex.graph \\begin{equation}\\xymatrix{<CR>".st.et."<CR>}\\end{equation}"
"}
