euclid <- function(a0, a1) {
	stopifnot(a0 >= a1)
	r <- a0 %% a1
	while (0 < r) {
		a0 <- a1
		a1 <- r
		r <- a0 %% a1
	}
	return(a1)
}

crnames <- function(x, row, col) {
	rownames(x) <- row
	colnames(x) <- col
	x
}

if (0) {
	print(euclid(12, 8))
	print(euclid(387, 109))
}

if (1) {
	x6 <- 1:5
	xs <- matrix(x6 %x% x6, 5) %% 6
	xs <- crnames(xs, x6, x6)
	print(xs)
	x3 <- c(2, 4)
	xs <- matrix(x6 %x% x3, 2) %% 6
	xs <- crnames(t(xs), x6, x3)
	print(xs)
	x2 <- c(3)
	xs <- matrix(x6 %x% x2, 1) %% 6
	xs <- crnames(t(xs), x6, x2)
	print(xs)
}
