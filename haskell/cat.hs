{-
 - runghc <this.file> < USA-states.txt
 -}
main = do {
	cs <- getContents;
	putStr cs;
}
