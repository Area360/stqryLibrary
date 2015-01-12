package nz.co.stqry.library.maths.geometry;

public class Spline2D {

  /** 
   *  Array representing the relative proportion of the total distance
   *  of each point in the line ( i.e. first point is 0.0, end point is
   *  1.0, a point halfway on line is 0.5 ).
   */
  private double[] mT;
  private Spline mSplineX;
  private Spline mSplineY;
  /**
   * Total length tracing the points on the spline
   */
  private double mLength;
  
  /**
   * Creates a new Spline2D.
   * @param points
   */
  public Spline2D(Vector2d[] points) {
    double[] x = new double[points.length];
    double[] y = new double[points.length];
    
    for(int i = 0; i< points.length; i++) {
      x[i] = points[i].getX();
      y[i] = points[i].getY();
    }
    
    init(x, y);
  }

  /**
   * Creates a new Spline2D.
   * @param x
   * @param y
   */
  public Spline2D(double[] x, double[] y) {
    init(x, y);
  }

  private void init(double[] x, double[] y) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("Arrays must have the same length.");
    }
    
    if (x.length < 2) {
      throw new IllegalArgumentException("Spline edges must have at least two points.");
    }

    mT = new double[x.length];
    mT[0] = 0.0; // start point is always 0.0
    
    // Calculate the partial proportions of each section between each set
    // of points and the total length of sum of all sections
    for (int i = 1; i < mT.length; i++) {
      double lx = x[i] - x[i-1];
      double ly = y[i] - y[i-1];
      // If either diff is zero there is no point performing the square root
      if ( 0.0 == lx ) {
        mT[i] = Math.abs(ly);
      } else if ( 0.0 == ly ) {
        mT[i] = Math.abs(lx);
      } else {
        mT[i] = Math.sqrt(lx*lx+ly*ly);
      }
      
      mLength += mT[i];
      mT[i] += mT[i-1];
    }
    
    for(int i = 1; i< (mT.length)-1; i++) {
      mT[i] = mT[i] / mLength;
    }
    
    mT[(mT.length)-1] = 1.0; // end point is always 1.0
    
    mSplineX = new Spline(mT, x);
    mSplineY = new Spline(mT, y);
  }

  /**
   * @param t 0 <= t <= 1
   */
  public double[] getPoint(double t) {
    double[] result = new double[2];
    result[0] = mSplineX.getValue(t);
    result[1] = mSplineY.getValue(t);

    return result;
  }
  
  /**
   * Used to check the correctness of this spline
   */
  public boolean checkValues() {
    return (mSplineX.checkValues() && mSplineY.checkValues());
  }

  public double getDx(double t) {
    return mSplineX.getDx(t);
  }
  
  public double getDy(double t) {
    return mSplineY.getDx(t);
  }
  
  public Spline getSplineX() {
    return mSplineX;
  }
  
  public Spline getSplineY() {
    return mSplineY;
  }
  
  public double getLength() {
    return mLength;
  }

}
