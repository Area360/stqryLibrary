package nz.co.stqry.library.android.maths.geometry;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Marc Giovannoni on 30/09/14.
 */
public class Vector2f implements Parcelable {

    private float mX;

    private float mY;

    public Vector2f() {
        mX = .0f;
        mY = .0f;
    }

    public Vector2f(float x, float y) {
        mX = x;
        mY = y;
    }

    public void set(float x, float y) {
        mX = x;
        mY = y;
    }

    public float getX() {
        return mX;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getY() {
        return mY;
    }

    public void setY(float y) {
        mY = y;
    }

    public Vector2f add(Vector2f vector2) {
        return new Vector2f(mX + vector2.getX(), mY + vector2.getY());
    }

    public Vector2f sub(Vector2f vector2) {
        return new Vector2f(mX - vector2.getX(), mY - vector2.getY());
    }

    public double length() {
        return Math.sqrt((mX * mX) + (mY * mY));
    }

    public Vector2f mult(float i) {
        return new Vector2f(mX * i, mY * i);
    }

    public double dot(Vector2f vector2) {
        return (mX * vector2.getX()) + (mY * vector2.getY());
    }

    public void rotate(double angle) {
        float tx = mX;
        float ty = mY;
        float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);

        mX = ((cos * tx) - (sin * ty));
        mY = ((sin * tx) + (cos * ty));
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(mX);
        out.writeFloat(mY);
    }

    public static final Creator<Vector2f> CREATOR = new Creator<Vector2f>() {
        public Vector2f createFromParcel(Parcel in) {
            return new Vector2f(in);
        }

        public Vector2f[] newArray(int size) {
            return new Vector2f[size];
        }
    };

    private Vector2f(Parcel in) {
        mX = in.readFloat();
        mY = in.readFloat();
    }
    @Override
    public int describeContents() {
        return 0;
    }
}
