
test_2 = function () {
	f_0 = function (t) {
		x = 1 - sqrt (1 - 4 * t)
		x = x / 2 / t
	}
	f_1 = function (t) {
		1 / (1 - t)
	}
	ts = seq (1 / 100, 1 / 4, len=100)
	xs_0 = f_0 (ts)
	xs_1 = f_1 (ts)
	plot (range (ts), range (xs_0, xs_1), type="n", xlab="t", ylab="x")
	lines (ts, xs_0, col="blue")
	lines (ts, xs_1, col="red")
}

test_2 ()

test_1 <- function () {
	f <- function (n1, n2, n3) {
		a <- factorial (n1 + n2 + n3 + 2);
		b <- factorial (n1) * factorial (n2) * factorial (n3);
		c <- (n2 + n3 + 2) * (n3 * 1);
		a / b / c;
	}

	print (f (2, 3, 5))
	print (f (3, 5, 2))
	print (f (5, 2, 3))
}
