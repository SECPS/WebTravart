package com.example.application.data;

public  class Tuple<S, T> {
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

	}

