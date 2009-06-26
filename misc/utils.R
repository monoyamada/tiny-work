divide <- function(xs, n) {
	usage <- "xs is an array, n is a strict positive integer";
	xn <- length(xs);
	if (!is.numeric(n)) {
		stop(usage);
	}
	n <- as.integer(n);
	if (n < 1 | xn < n) {
	} else if (n == 1) {
		list(xs)
	} else {
		m <- floor(xn / n);
		lapply(1:n, function(i) {
			if (i < n) {
				xs[(1:m) + m*(i-1)];
			} else {
				xs[(m*(i-1)+1):xn];
			}
		});
	}
}

gaussian <- function(t, x) {
	1/(2*pi*t)^(1/2)*exp(-x^2/t/2);
}

dx1.gaussian <- function(t, x) {
	-(2*pi*t)^(1/2)*x/t*exp(-x^2/t/2);
}

dx2.gaussian <- function(t, x) {
	(2*pi*t)^(1/2)*((x/t)^2-1/t)*exp(-x^2/t/2);
}

dx.gaussian <- dx1.gaussian 

boxcar <- function(radius, x) {
	(-radius <= x) & (x < radius);
}

boxcar.convolve <- function(r1, r2, x) {
	ind.0 <- which(r1 + r2 <= abs(x));
	ind.1 <- which(abs(r1 - r2) <= abs(x) & abs(x) < r1 + r2);
	ind.2 <- which(abs(x) < abs(r1 - r2));
	x[ind.0] <- 0;
	x[ind.1] <- r1 + r2 - abs(x[ind.1]);
	x[ind.2] <- 2 * min(r1, r2);
	x;
}
