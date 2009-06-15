if (F) {
	#sample of approx
	x <- 1:10
	y <- rnorm(10)
	plot(x, y, main = "approx(.) and approxfun(.)")
	points(approx(x, y), col = 2, pch = "*")
}

if (F){
#	lty <- c(0=blank, 1=solid (default), 2=dashed, 3=dotted, 4=dotdash, 5=longdash, 6=twodash)
}

divide.seq <- function(xs, n) {
	usage.xs <- "xs is a vector of numeric"
	usage.n <- "n is a integer and 0 < n"
	if (is.numeric(xs) & 1 < length(xs) ) {
		#valid
	} else {
		stop(simpleError(usage.xs));
	}
	if (is.numeric(n) & length(n) == 1 & 0 < n) {
		#valid
	} else {
		stop(simpleError(usage.n));
	}
	xn <- length(xs);
	yn <- n * (xn - 1) + 1;
	ys <- rep(NA, yn);
	ys[1] <- xs[1];
	for (i in 1:(xn-1)) {
		x1 <- xs[i];
		dx <- (xs[i+1] - x1) / n;
		ys[1 + n*(i-1) + 1:n] <- x1 + dx * (1:n);
	}
	ys;
}

test.divide.seq <- function() {
	xs <- c(0, 1, 3);
	ys <- divide.seq(xs, 3);
	print(list(x=xs, y=ys));
}

if (F) {
	test.divide.seq();
}

test.convolution <- function() {
	x <-         c(-2, -1, -0.5, -0.5, -0.25, -0.25, 0, 0.25, 0.25, 0.5, 0.5, 1, 2);
	f.y <- 1.0 * c( 0,  0,    0,    1,     1,     1, 1,    1,    1,   1,   0, 0, 0);
	g.y <- 0.6 * c( 0,  0,    0,    0,     0,     1, 1,    1,    0,   0,   0, 0, 0);
	c.x <- divide.seq(x, 10);
	c.y <- pmax(0, 1 - abs(c.x));
	plot(x, f.y, type="l", lty=1, main="f(h,r,x)=h*(|x|<=r)", xlab="x", ylab="y");
	lines(x, g.y, type="l", lty=2);
	lines(c.x, c.y, type="l", lty=3);
	legend(0.3 * max(x), 0.9 * max(f.y, g.y), c("y=f(1, 1/2)", "y=f(3/5, 1/4)", "y=f(1,1/2)*f(3/5,1/4)"), lty=c(1,2,3));
}

test.convolution();

string.trim <- function(x) {
	usage <- "x must be a character or null";
	if (is.null(x)) {
		return(x);
	} else if (is.character(x)) {
		x <- gsub('^[[:space:]]+', '', x);
		x <- gsub('[[:space:]]+$', '', x);
		return(x);
	}
	stop(usage);
}

to.hz <- function(length, unit=NULL) {
	unit.usage <- "unit must be (km|m|cm|mm|nm)";
	if (!is.numeric(length)) {
		stop("length must be a numeric");
	}
	if (is.character(unit)) {
		lo.unit <- tolower(string.trim(unit));
		if (lo.unit == "km") {
			unit <- 1000;
		} else if (lo.unit == "m") {
			unit <- 1;
		} else if (lo.unit == "cm") {
			unit <- 0.01;
		} else if (lo.unit == "mm") {
			unit <- 0.001;
		} else if (lo.unit == "nm") {
			unit <- 0.000001;
		} else {
			stop(unit.usage);
		}
	} else {
		if (is.null(unit)) {
			unit <- 1;
		} else {
			stop(unit.usage);
		}
	}
	hz <- 299792458.0 / (length * unit);
	value <- hz;
	unit <- "hz";
	if (value < 10^3) {
	} else if (value < 10^6) {
		value <- round(value / 10^3);
		unit <- "khz";
	} else if (value < 10^9) {
		value <- round(value / 10^6);
		unit <- "mhz";
	} else if (value < 10^12) {
		value <- round(value / 10^9);
		unit <- "ghz";
	} else {
		value <- round(value / 10^12);
		unit <- "thz";
	}
	c(hz=hz, value=value, unit=unit);
}
