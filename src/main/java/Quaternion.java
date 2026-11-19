import java.util.*;

/** Quaternions. Basic operations. */
public class Quaternion {

   private static final double DELTA = 0.000001;
   private double rpart;
   private double ipart;
   private double jpart;
   private double kpart;

   /** Constructor from four double values.
    * @param a real part
    * @param b imaginary part i
    * @param c imaginary part j
    * @param d imaginary part k
    */
   public Quaternion (double a, double b, double c, double d) {
      rpart = a;
      ipart = b;
      jpart = c;
      kpart = d;
   }

   /** Real part of the quaternion.
    * @return real part
    */
   public double getRpart() {
      return rpart;
   }

   /** Imaginary part i of the quaternion.
    * @return imaginary part i
    */
   public double getIpart() {
      return ipart;
   }

   /** Imaginary part j of the quaternion.
    * @return imaginary part j
    */
   public double getJpart() {
      return jpart;
   }

   /** Imaginary part k of the quaternion.
    * @return imaginary part k
    */
   public double getKpart() {
      return kpart;
   }

   /** Conversion of the quaternion to the string.
    * @return a string form of this quaternion:
    * "a+bi+cj+dk"
    * (without any brackets)
    */
   @Override
   public String toString() {
      return Double.toString(rpart)
              + (ipart < 0 ? Double.toString(ipart) : "+" + Double.toString(ipart)) + "i"
              + (jpart < 0 ? Double.toString(jpart) : "+" + Double.toString(jpart)) + "j"
              + (kpart < 0 ? Double.toString(kpart) : "+" + Double.toString(kpart)) + "k";
   }

   /** Conversion from the string to the quaternion.
    * Reverse to <code>toString</code> method.
    * @throws IllegalArgumentException if string s does not represent
    *     a quaternion (defined by the <code>toString</code> method)
    * @param s string of form produced by the <code>toString</code> method
    * @return a quaternion represented by string s
    */
   public static Quaternion valueOf (String s) {
      if (s == null) {
         throw new IllegalArgumentException(
                 "Cannot parse quaternion: input string is null"
         );
      }

      String number =
              "[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?";
      String signedNumber =
              "[+-](?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?";

      String regex =
              "^(" + number + ")"
                      + "(" + signedNumber + ")i"
                      + "(" + signedNumber + ")j"
                      + "(" + signedNumber + ")k$";

      java.util.regex.Matcher matcher =
              java.util.regex.Pattern.compile(regex).matcher(s);

      if (!matcher.matches()) {
         throw new IllegalArgumentException(
                 "Cannot parse quaternion: invalid value '" + s + "'"
         );
      }

      try {
         double a = Double.parseDouble(matcher.group(1));
         double b = Double.parseDouble(matcher.group(2));
         double c = Double.parseDouble(matcher.group(3));
         double d = Double.parseDouble(matcher.group(4));

         return new Quaternion(a, b, c, d);
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException(
                 "Cannot parse quaternion: invalid numeric value in '" + s + "'",
                 e
         );
      }
   }

   /** Clone of the quaternion.
    * @return independent clone of <code>this</code>
    */
   @Override
   public Object clone() throws CloneNotSupportedException {
      return new Quaternion(rpart, ipart, jpart, kpart);
   }

   /** Test whether the quaternion is zero.
    * @return true, if the real part and all the imaginary parts are (close to) zero
    */
   public boolean isZero() {
      return Math.abs(rpart) < DELTA
              && Math.abs(ipart) < DELTA
              && Math.abs(jpart) < DELTA
              && Math.abs(kpart) < DELTA;
   }

   /** Conjugate of the quaternion. Expressed by the formula
    *     conjugate(a+bi+cj+dk) = a-bi-cj-dk
    * @return conjugate of <code>this</code>
    */
   public Quaternion conjugate() {
      return new Quaternion(rpart, -ipart, -jpart, -kpart);
   }

   /** Opposite of the quaternion. Expressed by the formula
    *    opposite(a+bi+cj+dk) = -a-bi-cj-dk
    * @return quaternion <code>-this</code>
    */
   public Quaternion opposite() {
      return new Quaternion(-rpart, -ipart, -jpart, -kpart);
   }

   /** Sum of quaternions. Expressed by the formula
    *    (a1+b1i+c1j+d1k) + (a2+b2i+c2j+d2k) = (a1+a2) + (b1+b2)i + (c1+c2)j + (d1+d2)k
    * @param q addend
    * @return quaternion <code>this+q</code>
    */
   public Quaternion plus (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot add quaternion: the second operand is null"
         );
      }

      return new Quaternion(
              rpart + q.rpart,
              ipart + q.ipart,
              jpart + q.jpart,
              kpart + q.kpart
      );
   }

   /** Product of quaternions. Expressed by the formula
    *  (a1+b1i+c1j+d1k) * (a2+b2i+c2j+d2k) = (a1a2-b1b2-c1c2-d1d2) + (a1b2+b1a2+c1d2-d1c2)i +
    *  (a1c2-b1d2+c1a2+d1b2)j + (a1d2+b1c2-c1b2+d1a2)k
    * @param q factor
    * @return quaternion <code>this*q</code>
    */
   public Quaternion times (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot multiply quaternions: the second operand is null"
         );
      }

      double a = rpart * q.rpart
              - ipart * q.ipart
              - jpart * q.jpart
              - kpart * q.kpart;

      double b = rpart * q.ipart
              + ipart * q.rpart
              + jpart * q.kpart
              - kpart * q.jpart;

      double c = rpart * q.jpart
              - ipart * q.kpart
              + jpart * q.rpart
              + kpart * q.ipart;

      double d = rpart * q.kpart
              + ipart * q.jpart
              - jpart * q.ipart
              + kpart * q.rpart;

      return new Quaternion(a, b, c, d);
   }

   /** Multiplication by a coefficient.
    * @param r coefficient
    * @return quaternion <code>this*r</code>
    */
   public Quaternion times (double r) {
      return new Quaternion(
              rpart * r,
              ipart * r,
              jpart * r,
              kpart * r
      );
   }

   /** Inverse of the quaternion. Expressed by the formula
    *     1/(a+bi+cj+dk) = a/(a*a+b*b+c*c+d*d) +
    *     ((-b)/(a*a+b*b+c*c+d*d))i + ((-c)/(a*a+b*b+c*c+d*d))j + ((-d)/(a*a+b*b+c*c+d*d))k
    * @return quaternion <code>1/this</code>
    */
   public Quaternion inverse() {
      double denominator =
              rpart * rpart
                      + ipart * ipart
                      + jpart * jpart
                      + kpart * kpart;

      if (denominator < DELTA) {
         throw new ArithmeticException(
                 "Cannot calculate inverse: quaternion is zero"
         );
      }

      return new Quaternion(
              rpart / denominator,
              -ipart / denominator,
              -jpart / denominator,
              -kpart / denominator
      );
   }

   public Quaternion pow(int n) {
      if (n == 0) {
         return new Quaternion(1, 0, 0, 0);
      }

      if (n == 1) {
         return new Quaternion(rpart, ipart, jpart, kpart);
      }

      if (n == -1) {
         return inverse();
      }

      if (n > 1) {
         return times(pow(n - 1));
      }

      return pow(-n).inverse();
   }

   /** Difference of quaternions. Expressed as addition to the opposite.
    * @param q subtrahend
    * @return quaternion <code>this-q</code>
    */
   public Quaternion minus (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot subtract quaternion: the second operand is null"
         );
      }

      return new Quaternion(
              rpart - q.rpart,
              ipart - q.ipart,
              jpart - q.jpart,
              kpart - q.kpart
      );
   }

   /** Right quotient of quaternions. Expressed as multiplication to the inverse.
    * @param q (right) divisor
    * @return quaternion <code>this*inverse(q)</code>
    */
   public Quaternion divideByRight (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot divide by right quaternion: divisor is null"
         );
      }

      if (q.isZero()) {
         throw new ArithmeticException(
                 "Cannot divide by right quaternion: divisor is zero"
         );
      }

      return times(q.inverse());
   }

   /** Left quotient of quaternions.
    * @param q (left) divisor
    * @return quaternion <code>inverse(q)*this</code>
    */
   public Quaternion divideByLeft (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot divide by left quaternion: divisor is null"
         );
      }

      if (q.isZero()) {
         throw new ArithmeticException(
                 "Cannot divide by left quaternion: divisor is zero"
         );
      }

      return q.inverse().times(this);
   }

   /** Equality test of quaternions. Difference of equal numbers
    *     is (close to) zero.
    * @param qo second quaternion
    * @return logical value of the expression <code>this.equals(qo)</code>
    */
   @Override
   public boolean equals (Object qo) {
      if (this == qo) {
         return true;
      }

      if (!(qo instanceof Quaternion)) {
         return false;
      }

      Quaternion q = (Quaternion) qo;

      return Math.abs(rpart - q.rpart) < DELTA
              && Math.abs(ipart - q.ipart) < DELTA
              && Math.abs(jpart - q.jpart) < DELTA
              && Math.abs(kpart - q.kpart) < DELTA;
   }

   /** Dot product of quaternions. (p*conjugate(q) + q*conjugate(p))/2
    * @param q factor
    * @return dot product of this and q
    */
   public Quaternion dotMult (Quaternion q) {
      if (q == null) {
         throw new IllegalArgumentException(
                 "Cannot calculate dot product: the second operand is null"
         );
      }

      return this.times(q.conjugate())
              .plus(q.times(this.conjugate()))
              .times(0.5);
   }

   /** Integer hashCode has to be the same for equal objects.
    * @return hashcode
    */
   @Override
   public int hashCode() {
      long r = Math.round(rpart / DELTA);
      long i = Math.round(ipart / DELTA);
      long j = Math.round(jpart / DELTA);
      long k = Math.round(kpart / DELTA);

      int result = 17;
      result = 31 * result + (int)(r ^ (r >>> 32));
      result = 31 * result + (int)(i ^ (i >>> 32));
      result = 31 * result + (int)(j ^ (j >>> 32));
      result = 31 * result + (int)(k ^ (k >>> 32));

      return result;
   }

   /** Norm of the quaternion. Expressed by the formula
    *     norm(a+bi+cj+dk) = Math.sqrt(a*a+b*b+c*c+d*d)
    * @return norm of <code>this</code> (norm is a real number)
    */
   public double norm() {
      return Math.sqrt(
              rpart * rpart
                      + ipart * ipart
                      + jpart * jpart
                      + kpart * kpart
      );
   }

   /** Main method for testing purposes.
    * @param args command line parameters
    */
   public static void main (String[] args) {
      Quaternion q1 = new Quaternion(1, 2, 3, 4);
      Quaternion q2 = new Quaternion(5, 6, 7, 8);

      System.out.println("q1 = " + q1);
      System.out.println("q2 = " + q2);
      System.out.println("q1 + q2 = " + q1.plus(q2));
      System.out.println("q1 - q2 = " + q1.minus(q2));
      System.out.println("q1 * q2 = " + q1.times(q2));
      System.out.println("conjugate(q1) = " + q1.conjugate());
      System.out.println("opposite(q1) = " + q1.opposite());
      System.out.println("norm(q1) = " + q1.norm());
      System.out.println("inverse(q1) = " + q1.inverse());
   }

}
