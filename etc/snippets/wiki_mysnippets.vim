if !exists('loaded_snippet') || &cp
    finish
endif

let st = g:snip_start_tag
let et = g:snip_end_tag
let cd = g:snip_elem_delim

exec "Snippet h1 = ".st.et." ="
exec "Snippet h2 == ".st.et." =="
exec "Snippet h3 === ".st.et." ==="
exec "Snippet h4 ==== ".st.et." ===="
exec "Snippet h5 ===== ".st.et." ====="
exec "Snippet h6 ====== ".st.et." ======"
