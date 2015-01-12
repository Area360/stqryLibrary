package nz.co.stqry.library.android.maths.geometry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marc Giovannoni on 30/09/14.
 */
public class Vector2d implements Parcelable {

    private double mX;

    private double mY;

    public Vector2d() {
        mX = .0;
        mY = .0;
    }

    public Vector2d(double x, double y) {
        mX = x;
        mY = y;
    }

    public void set(double x, double y) {
        mX = x;
        mY = y;
    }

    public double getX() {
        return mX;
    }

    public void setX(double x) {
        mX = x;
    }

    public double getY() {
        return mY;
    }

    public void setY(double y) {
        mY = y;
    }

    public Vector2d add(Vector2d vector2) {
        return new Vector2d(mX + vector2.getX(), mY + vector2.getY());
    }

    public Vector2d sub(Vector2d vector2) {
        return new Vector2d(mX - vector2.getX(), mY - vector2.getY());
    }

    public double length() {
        return Math.sqrt((mX * mX) + (mY * mY));
    }

    public Vector2d mult(float i) {
        return new Vector2d(mX * i, mY * i);
    }

    public double dot(Vector2d vector2) {
        return (mX * vector2.getX()) + (mY * vector2.getY());
    }

    public void rotate(double angle) {
        double tx = mX;
        double ty = mY;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        mX = ((cos * tx) - (sin * ty));
        mY = ((sin * tx) + (cos * ty));
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeDouble(mX);
        out.writeDouble(mY);
    }

    public static final Parcelable.Creator<Vector2d> CREATOR = new Parcelable.Creator<Vector2d>() {
        public Vector2d createFromParcel(Parcel in) {
            return new Vector2d(in);
        }

        public Vector2d[] newArray(int size) {
            return new Vector2d[size];
        }
    };

    private Vector2d(Parcel in) {
        mX = in.readDouble();
        mY = in.readDouble();
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
