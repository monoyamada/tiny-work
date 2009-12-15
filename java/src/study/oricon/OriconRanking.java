package study.oricon;

import java.io.IOException;

public class OriconRanking extends OriconData {
	private OriconName brand;
	private OriconName product;
	private int sales;

	public OriconRanking() {
		this.sales = -1;
	}
	public OriconRanking(OriconName brand, OriconName product, int sales) {
		this.brand = brand;
		this.product = product;
		this.sales = sales;
	}
	public OriconName getBrand() {
		return this.getBrand(true);
	}
	public OriconName getBrand(boolean anyway) {
		if (this.brand == null && anyway) {
			this.brand = this.newOriconName();
		}
		return this.brand;
	}
	protected void setBrand(OriconName brand) {
		this.brand = brand;
	}
	public void setBrand(int id, String name) {
		this.getBrand(true).set(id, name);
	}
	public OriconName getProduct() {
		return this.getProduct(true);
	}
	public OriconName getProduct(boolean anyway) {
		if (this.product == null && anyway) {
			this.product = this.newOriconName();
		}
		return this.product;
	}
	protected OriconName newOriconName() {
		return new OriconName();
	}
	protected void setProduct(OriconName product) {
		this.product = product;
	}
	public void setProduct(int id, String name) {
		this.getProduct(true).set(id, name);
	}
	public int getSales() {
		return this.sales;
	}
	public void setSales(int sales) {
		this.sales = sales;
	}

	public void toString(Appendable output) throws IOException {
		output.append("brand=(");
		this.getBrand().toString(output);
		output.append("), product=(");
		this.getProduct().toString(output);
		output.append("), sales=");
		output.append(Integer.toString(this.getSales()));
	}
	public OriconRanking clone() {
		final OriconRanking that = (OriconRanking) super.clone();
		if (this.brand != null) {
			that.brand = this.brand.clone();
		}
		if (this.product != null) {
			that.product = this.product.clone();
		}
		return that;
	}
	public OriconRanking copy(OriconRanking x) {
		if (x.brand != null) {
			if (this.brand != null) {
				this.brand.copy(x.brand);
			} else {
				this.brand = x.brand.clone();
			}
		} else if (this.brand != null) {
			this.brand.clear();
		}
		if (x.product != null) {
			if (this.product != null) {
				this.product.copy(x.product);
			} else {
				this.product = x.product.clone();
			}
		} else if (this.product != null) {
			this.product.clear();
		}
		return this;
	}
	public OriconRanking clear() {
		this.sales = -1;
		if (this.getBrand(false) != null) {
			this.getBrand(false).clear();
		}
		if (this.getProduct(false) != null) {
			this.getProduct(false).clear();
		}
		return this;
	}
}
