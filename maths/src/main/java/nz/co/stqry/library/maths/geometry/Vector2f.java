package nz.co.stqry.library.maths.geometry;

/**
 * Created by Marc Giovannoni on 30/09/14.
 */
public class Vector2f {

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
}
