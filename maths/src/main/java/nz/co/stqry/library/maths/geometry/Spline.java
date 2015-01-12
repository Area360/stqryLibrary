package nz.co.stqry.library.maths.geometry;

import java.util.Arrays;

class Spline {

	private double[] mXx;
	private double[] mYy;

	private double[] mA;
	private double[] mB;
	private double[] mC;
	private double[] mD;

	/**
	 * tracks the last index found since that is mostly commonly the next one
	 * used
	 */
	private int storageIndex = 0;

	/**
	 * Creates a new Spline.
	 * 
	 * @param xx
	 * @param yy
	 */
	public Spline(double[] xx, double[] yy) {
		setValues(xx, yy);
	}

	/**
	 * Set values for this Spline.
	 * 
	 * @param xx
	 * @param yy
	 */
	public void setValues(double[] xx, double[] yy) {
		mXx = xx;
		mYy = yy;
		if (xx.length > 1) {
			calculateCoefficients();
		}
	}

	/**
	 * Returns an interpolated value.
	 * 
	 * @param x
	 * @return the interpolated value
	 */
	public double getValue(double x) {
		if (mXx.length == 0) {
			return Double.NaN;
		}

		if (mXx.length == 1) {
			if (mXx[0] == x) {
				return mYy[0];
			} else {
				return Double.NaN;
			}
		}

		int index = Arrays.binarySearch(mXx, x);
		if (index > 0) {
			return mYy[index];
		}

		index = -(index + 1) - 1;
		// TODO linear interpolation or extrapolation
		if (index < 0) {
			return mYy[0];
		}

		return mA[index] + mB[index] * (x - mXx[index]) + mC[index]
				* Math.pow(x - mXx[index], 2) + mD[index]
				* Math.pow(x - mXx[index], 3);
	}

	/**
	 * Returns an interpolated value. To be used when a long sequence of values
	 * are required in order, but ensure checkValues() is called beforehand to
	 * ensure the boundary checks from getValue() are made
	 * 
	 * @param x
	 * @return the interpolated value
	 */
	public double getFastValue(double x) {
		// Fast check to see if previous index is still valid
		if (storageIndex > -1 && storageIndex < mXx.length - 1
				&& x > mXx[storageIndex] && x < mXx[storageIndex + 1]) {

		} else {
			int index = Arrays.binarySearch(mXx, x);
			if (index > 0) {
				return mYy[index];
			}
			index = -(index + 1) - 1;
			storageIndex = index;
		}

		// TODO linear interpolation or extrapolation
		if (storageIndex < 0) {
			return mYy[0];
		}
		double value = x - mXx[storageIndex];
		return mA[storageIndex] + mB[storageIndex] * value + mC[storageIndex]
				* (value * value) + mD[storageIndex] * (value * value * value);
	}

	/**
	 * Used to check the correctness of this spline
	 */
	public boolean checkValues() {
		if (mXx.length < 2) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns the first derivation at x.
	 * 
	 * @param x
	 * @return the first derivation at x
	 */
	public double getDx(double x) {
		if (mXx.length == 0 || mXx.length == 1) {
			return 0;
		}

		int index = Arrays.binarySearch(mXx, x);
		if (index < 0) {
			index = -(index + 1) - 1;
		}

		return mB[index] + 2 * mC[index] * (x - mXx[index]) + 3 * mD[index]
				* Math.pow(x - mXx[index], 2);
	}

	/**
	 * Calculates the Spline coefficients.
	 */
	private void calculateCoefficients() {
		int N = mYy.length;
		mA = new double[N];
		mB = new double[N];
		mC = new double[N];
		mD = new double[N];

		if (N == 2) {
			mA[0] = mYy[0];
			mB[0] = mYy[1] - mYy[0];
			return;
		}

		double[] h = new double[N - 1];
		for (int i = 0; i < N - 1; i++) {
			mA[i] = mYy[i];
			h[i] = mXx[i + 1] - mXx[i];
			// h[i] is used for division later, avoid a NaN
			if (h[i] == 0.0) {
				h[i] = 0.01;
			}
		}
		mA[N - 1] = mYy[N - 1];

		double[][] A = new double[N - 2][N - 2];
		double[] y = new double[N - 2];
		for (int i = 0; i < N - 2; i++) {
			y[i] = 3 * ((mYy[i + 2] - mYy[i + 1]) / h[i + 1] - (mYy[i + 1] - mYy[i])
					/ h[i]);

			A[i][i] = 2 * (h[i] + h[i + 1]);

			if (i > 0) {
				A[i][i - 1] = h[i];
			}

			if (i < N - 3) {
				A[i][i + 1] = h[i + 1];
			}
		}
		solve(A, y);

		for (int i = 0; i < N - 2; i++) {
			mC[i + 1] = y[i];
			mB[i] = (mA[i + 1] - mA[i]) / h[i] - (2 * mC[i] + mC[i + 1]) / 3 * h[i];
			mD[i] = (mC[i + 1] - mC[i]) / (3 * h[i]);
		}
		mB[N - 2] = (mA[N - 1] - mA[N - 2]) / h[N - 2] - (2 * mC[N - 2] + mC[N - 1])
				/ 3 * h[N - 2];
		mD[N - 2] = (mC[N - 1] - mC[N - 2]) / (3 * h[N - 2]);
	}

	/**
	 * Solves Ax=b and stores the solution in b.
	 */
	public void solve(double[][] A, double[] b) {
		int n = b.length;
		for (int i = 1; i < n; i++) {
			A[i][i - 1] = A[i][i - 1] / A[i - 1][i - 1];
			A[i][i] = A[i][i] - A[i - 1][i] * A[i][i - 1];
			b[i] = b[i] - A[i][i - 1] * b[i - 1];
		}

		b[n - 1] = b[n - 1] / A[n - 1][n - 1];
		for (int i = b.length - 2; i >= 0; i--) {
			b[i] = (b[i] - A[i][i + 1] * b[i + 1]) / A[i][i];
		}
	}
}