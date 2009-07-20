concatenate <- function(x1, x2) {
	f1 <- function(x, xs) {
		ind <- which(xs <= x);
		c(xs[ind], x, xs[-ind]);
	}
	n1 <- length(x1);
	n2 <- length(x2);
	if (n1 < 2) {
		f1(x1, x2);
	} else if (n2 < 2) {
		f1(x2, x1);
	} else if (x1[n1] <= x2[1]) {
		c(x1, x2);
	} else if (x2[n2] <= x1[1]) {
		c(x2, x1);
	} else {
	}
}

length <- 10;
length.1 <- length / 2;
xs <- sample(1:length, length);
xs1 <- sort(xs[1:length.1]);
xs2 <- sort(xs[-(1:length.1)]);
print(list(xs1, xs2));
