t.1 <- 1 / 4
f.1 <- function (xs) {
	1 + t.1 * xs^2
}
df.1 <- function (xs) {
	2 * t.1 * xs
}
kleene.1 <- function (n) {
	xs <- numeric (n)
	x <- f.1 (0)
	xs[1] <- x
	for (i in 2:n) {
		x <- f.1 (x)
		xs[i] <- x
	}
	xs
}
newton.1 <- function (n) {
	xs <- numeric (n)
	x <- f.1 (0)
	xs[1] <- x
	for (i in 2:n) {
		y <- f.1 (x) - x; 
		z <- 1 - df.1 (x)
		x <- x + y / z
		xs[i] <- x
	}
	xs
}
n <- 20
xs <- cbind (kleene.1 (n), newton.1 (n))
colnames (xs) <- c("Kleene", "Newton")
x <- 2
ds <- abs(x - xs) / x
print (data.frame (xs))
print (data.frame (ds))
do.png <- T
tryCatch({
	if (do.png) {
		png (file="k-n.png")
	}
	plot (c(1, n), range (ds), type="n", xlab="", ylab="|2 - x| / 2"
		, main="convergence of Kleene vs Newton")
	cols <- c ("blue", "red")
	ltys <- c (2, 1)
	lines (1:n, ds[,1], col=cols[1], lty=ltys[1])
	lines (1:n, ds[,2], col=cols[2], lty=ltys[2])
	legend (x=0.5 * n, y=0.8 * max (ds), leg=colnames (ds)
		, col=cols, lty=ltys)
}, finnaly={
	if (do.png) {
		dev.off ()
	}
})
