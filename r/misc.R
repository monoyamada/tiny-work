gaussian <- function(t, x) {
	(2*pi*t)^(1/2)*exp(-x^2/t/2);
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

test.boxcar <- function() {
	old.par <- par(mfrow=c(1,1));
	on.exit(par(old.par));
	xs <- (-200:200) / 100;
	col <- c("red", "blue", "black");
	lty = c(1, 1, 3);
	r1 <- 1.0;
	r2 <- 0.3;
	plot(xs, boxcar(r1, xs), type="l", col=col[1], lty=lty[1]);
	lines(xs, boxcar(r2, xs), type="l", col=col[2], lty=lty[2]);
	lines(xs, boxcar.convolve(r1, r2, xs), type="l", col=col[3], lty=lty[3]);
}

test.boxcar.1 <- function() {
	xs <- (-2000:2000) / 100;
	r1 <- 1.0;
	r2 <- 0.3;
	ts <- 1:10000 / 100;
	ys0 <- sapply(ts, function(t) {
		f2 <- integrate(lower=0, upper=r1+r2, function(x) {
			d <- 2 * (r1 + r2) - 2 * boxcar.convolve(r1, r2, x);
			exp(-d / 2 / t);
		});
		f2$value;
	});
	ys.1 <- sapply(ts, function(t) {
		t * (exp(-abs(r1 - r2) / t) - exp(-(r1 + r2) / t));
	});
	ys.2 <- sapply(ts, function(t) {
		abs(r1 - r2) * exp(-abs(r1 - r2) / t);
	});
	ys <- ys.1 + ys.2;

	old.par <- par(mfrow=c(5,1));
	on.exit(par(old.par));

	col <- c("red", "black", "green", "blue");
	lty = c(1, 1, 3, 3);
	plot(ts, ys0, ylim=range(ys, ys.1, ys.2, ys), log="x"
		, type="l", col=col[1], lty=lty[1]);
	lines(ts, ys, type="l", col=col[2], lty=lty[2]);
	#lines(ts, ys.1.1, type="l", col=col[3], lty=lty[3]);
	#lines(ts, ys.1.2, type="l", col=col[4], lty=lty[4]);

	ys.g <- ys / ts^(1/2);
	plot(ts, ys.g, log="x", type="l");

	ys.p <- ys.g * exp(-ts);
	plot(ts, ys.p, log="x", type="l");

	alp <- function(x, t) {
		t.sqrt <- sqrt(t);
		(x / t.sqrt + t.sqrt) * exp(- x / t);
	}

	ys.a <- alp(abs(r1-r2), ts) - alp(r1+r2, ts);
	plot(ts, ys.a, log="x", type="l");

	ys.b <- ys.a * exp(-ts);
	plot(ts, ys.b, log="x", type="l");
}

if (F) {
	test.boxcar.1();
}

test.misc.1 <- function() {
	fnc <- function(x, y) {
		(x / sqrt(y) + sqrt(y)) * exp(-(x / y + y));
	}
	xs <- 0:500 / 100;
	ys <- 1:500 / 100;
	zs <- outer(xs, ys, fnc);
	persp(xs, ys, zs, theta=30, phi=30, expand=0.5, col=rainbow(50), border=NA);
}

if (T) {
	test.misc.1();
}

test.gaussian <- function() {
	old.par <- par(mfrow=c(2,3));
	on.exit(par(old.par));
	xs <- (-200:200) / 20;
	t <- 1;
	plot(xs, gaussian(t, xs), type="l");
	plot(xs, dx1.gaussian(t, xs), type="l");
	plot(xs, dx2.gaussian(t, xs), type="l");
	#plot(xs, gaussian(t, 0) + (1/2)*xs^2*dx2.gaussian(t, 0), type="l");
	t <- 10;
	plot(xs, gaussian(t, xs), type="l");
	plot(xs, dx1.gaussian(t, xs), type="l");
	plot(xs, dx2.gaussian(t, xs), type="l");
	#plot(xs, gaussian(t, 0) + (1/2)*xs^2*dx2.gaussian(t, 0), type="l");
}

if (F) {
	test.gaussian();
}

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
	x <-   c(-2, -1, -0.5, -0.5, -0.25, -0.25, 0, 0.25, 0.25, 0.5, 0.5, 1, 2);
	f.y <- c( 0,  0,    0,    1,     1,     1, 1,    1,    1,   1,   0, 0, 0);
	g.y <- c( 0,  0,    0,    0,     0,     1, 1,    1,    0,   0,   0, 0, 0);
	fg.x <- divide.seq(x, 2);
	fg.y <- (abs(fg.x)<(1/2+1/4))*(pmin(1/2-fg.x, 1/4) + pmin(1/2+fg.x, 1/4));
	lty <- c(1,1,3);
	col <- c("red", "blue", "black");
	plot(x, f.y, type="l", lty=lty[1], col=col[1]
		, main="h(r,x)=|x|<=r", xlab="x", ylab="y");
	lines(x, g.y, type="l", lty=lty[2], col=col[2]);
	lines(fg.x, fg.y, type="l", lty=lty[3], col=col[3]);
	legend(0.3 * max(x), 0.9 * max(f.y, g.y), c("y=h(1/2)", "y=h(1/4)", "y=h(1/2)*h(1/4)"), lty=lty, col=col);
}

if (F) {
	test.convolution();
}

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
