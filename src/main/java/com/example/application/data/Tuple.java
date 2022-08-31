package com.example.application.data;

public class Tuple<S, T> {
	private S source;
	private T target;

	public Tuple(S sour, T tar) {
		source = sour;
		target = tar;
	}

	public S getSource() {
		return source;
	}

	public void setSource(S source) {
		this.source = source;
	}

	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	@Override
	public boolean equals(Object o1) {
		if (o1 == null) {
			return false;
		}
		if(this.hashCode()==o1.hashCode()) return true;
		if (!(o1 instanceof Tuple)) {
			return false;
		}
		Tuple<S, T> other = (Tuple<S, T>) o1;
		return this.source.equals(other.getSource()) && this.target.equals(other.getTarget());
	}
	
	@Override
	public int hashCode() {
		int hash=7;
		hash=31*hash+source.hashCode();
		hash=31*hash+target.hashCode();
		return hash;
	}

}
