// MathJax
MathJax.Hub.Config({
	TeX: {
		 Macros: {
			op: ['\\mathinner{\\operatorname{#1}}', 1]
			, cat: ['\\mathcal{#1}', 1]
			, obj: ['\\mathfrak{O}', 0]
			, arr: ['\\mathfrak{A}', 0]
			, set: ['\\{{#1}\\}', 1]
			, bfB: ['\\mathbf{B}', 0]
			, bfC: ['\\mathbf{C}', 0]
			, bfN: ['\\mathbf{N}', 0]
			, bfQ: ['\\mathbf{Q}', 0]
			, bfR: ['\\mathbf{R}', 0]
			, bfZ: ['\\mathbf{Z}', 0]
			//
			, clS: ['\\mathcal{S}', 0]
			, clU: ['\\mathcal{U}', 0]
			, clW: ['\\mathcal{W}', 0]
			//
			, xiff: ['\\overset{{#1}}{\\iff}', 1]
			, xto: ['\\overset{{#1}}{\\rightarrow}', 1]
			, dar: ['\\downarrow', 0]
			, plr: ['({#1})', 1]
			, gplr: ['\\bigl({#1}\\bigr)', 1]
			, ggplr: ['\\biggl({#1}\\biggr)', 1]
			, blr: ['\\langle{#1}\\rangle', 1]
			, gblr: ['\\bigl\\langle{#1}\\bigr\\rangle', 1]
			, ggblr: ['\\biggl\\langle{#1}\\biggr\\rangle', 1]
		}
	}
	, 'HTML-CSS': {scale: 80}
});

// SyntaxHighlighter
SyntaxHighlighter.all();
