package item17_20211207;

import java.math.BigInteger;
import java.util.Objects;

// 복소수 클래스
public class Complex {

    private final double realPart;
    private final double imaginaryPart;

    public Complex(double realPart, double imaginaryPart) {
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
    }

    public Complex plus(Complex c) {
        return new Complex(this.realPart + c.realPart, this.imaginaryPart + c.imaginaryPart);
    }

    public Complex minus(Complex c) {
        return new Complex(this.realPart - c.realPart, this.imaginaryPart - c.imaginaryPart);
    }

    public Complex times(Complex c) {
        return new Complex(this.realPart * c.realPart - this.imaginaryPart * c.imaginaryPart,
                this.realPart * c.imaginaryPart + this.imaginaryPart * c.realPart);
    }

    public Complex dividedBy(Complex c) {
        double tmp = c.realPart * c.realPart + c.imaginaryPart * c.imaginaryPart;
        return new Complex((this.realPart * c.realPart + this.imaginaryPart * c.imaginaryPart) / tmp,
                (this.imaginaryPart * c.realPart - this.realPart * c.imaginaryPart) / tmp);
    }

    public double getRealPart() {
        return realPart;
    }

    public double getImaginaryPart() {
        return imaginaryPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Complex complex = (Complex) o;
        return Double.compare(complex.realPart, realPart) == 0 && Double.compare(complex.imaginaryPart, imaginaryPart) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(realPart, imaginaryPart);
    }

    @Override
    public String toString() {
        return "Complex{" +
                "realPart=" + realPart +
                ", imaginaryPart=" + imaginaryPart +
                '}';
    }
}
