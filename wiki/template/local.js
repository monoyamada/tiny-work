// MathJax
MathJax.Hub.Config({
	TeX: {
		 Macros: {
			op: ['\\mathinner{\\operatorname{#1}}', 1]
			, cat: ['\\mathcal{#1}', 1]
			, obj: ['\\mathfrak{O}', 0]
			, arr: ['\\mathfrak{A}', 0]
			, trn: ['\\intercal', 0]
			, dfn: ['\\op{def}', 0]
			, bou: ['\\mathinner{|}', 1]
			, is: ['\\mathinner{[\\![{#1}]\\!]}', 1]
			//
			, seta: ['\\left\\{{#1}\\right\\}', 1]
			, set: ['\\{{#1}\\}', 1]
			//
			, bool: ['\\mathbf{B}', 0]
			, fukuso: ['\\mathbf{C}', 0]
			, sizen: ['\\mathbf{N}', 0]
			, bun: ['\\mathbf{Q}', 0]
			, jitu: ['\\mathbf{R}', 0]
			, sei: ['\\mathbf{Z}', 0]
			//
			, bfB: ['\\mathbf{B}', 0]
			, bfC: ['\\mathbf{C}', 0]
			, bfN: ['\\mathbf{N}', 0]
			, bfQ: ['\\mathbf{Q}', 0]
			, bfR: ['\\mathbf{R}', 0]
			, bfZ: ['\\mathbf{Z}', 0]
			//
			, clP: ['\\mathcal{P}', 0]
			, clS: ['\\mathcal{S}', 0]
			, clU: ['\\mathcal{U}', 0]
			, clW: ['\\mathcal{W}', 0]
			//
			, xiff: ['\\overset{{#1}}{\\iff}', 1]
			, xto: ['\\overset{{#1}}{\\rightarrow}', 1]
			, dar: ['\\downarrow', 0]
			//
			, plra: ['\\left({#1}\\right)', 1]
			, plr: ['({#1})', 1]
			, plrg: ['\\bigl({#1}\\bigr)', 1]
			, plrgg: ['\\biggl({#1}\\biggr)', 1]
			//
			, blra: ['\\left[{#1}\\right]', 1]
			, blr: ['[{#1}]', 1]
			, blrg: ['\\bigl[{#1}\\bigr]', 1]
			, blrgg: ['\\biggl[{#1}\\biggr]', 1]
			//
			, bblra: ['\\left[\\!\\left[{#1}\\right]\\!\\right]', 1]
			, bblr: ['[\\![{#1}]\\!]', 1]
			, bblrg: ['\\bigl[\\!\\bigl[{#1}\\bigr]\\!\\bigr]', 1]
			, bblrgg: ['\\biggl[\\!\\biggl[{#1}\\biggr]\\!\\biggr]', 1]
			//
			, clra: ['\\left\\{{#1}\\right\\}', 1]
			, clr: ['\\{{#1}\\}', 1]
			, clrg: ['\\bigl\\{{#1}\\bigr\\}', 1]
			, clrgg: ['\\biggl\\{{#1}\\biggr\\}', 1]
			//
			, cclra: ['\\left\\{\\!\\left\\{{#1}\\right\\}\\!\\right\\}', 1]
			, cclr: ['\\{\\!\\{{#1}\\}\\!\\}', 1]
			, cclrg: ['\\bigl\\{\\!\\bigl\\{{#1}\\bigr\\}\\!\\bigr\\}', 1]
			, cclrgg: ['\\biggl\\{\\!\\biggl\\{{#1}\\biggr\\}\\!\\biggr\\}', 1]
			//
			, dlra: ['\\left\\langle{#1}\\right\\rangle', 1]
			, dlr: ['\\langle{#1}\\rangle', 1]
			, dlrg: ['\\bigl\\langle{#1}\\bigr\\rangle', 1]
			, dlrgg: ['\\biggl\\langle{#1}\\biggr\\rangle', 1]
			//
			, ddlra: ['\\left\\langle\\!\\left\\langle{#1}\\right\\rangle\\!\\right\\rangle', 1]
			, ddlr: ['\\langle\\!\\langle{#1}\\rangle\\!\\rangle', 1]
			, ddlrg: ['\\bigl\\langle\\!\\bigl\\langle{#1}\\bigr\\rangle\\!\\bigr\\rangle', 1]
			, ddlrgg: ['\\biggl\\langle\\!\\biggl\\langle{#1}\\biggr\\rangle\\!\\biggr\\rangle', 1]
			//
			, braa: ['\\left\\langle{#1}\\right|', 1]
			, bra: ['\\langle{#1}|', 1]
			, brag: ['\\bigl\\langle{#1}\\bigr|', 1]
			, bragg: ['\\biggl\\langle{#1}\\biggr|', 1]
			//
			, keta: ['\\left|{#1}\\right\\rangle', 1]
			, ket: ['|{#1}\\rangle', 1]
			, ketg: ['\\bigl|{#1}\\bigr\\rangle', 1]
			, ketgg: ['\\biggl|{#1}\\biggr\\rangle', 1]
		}
	}, 'HTML-CSS': {scale: 80}
});

// SyntaxHighlighter
SyntaxHighlighter.all();
