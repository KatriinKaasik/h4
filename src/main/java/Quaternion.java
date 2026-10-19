/**
 * Lahenduse loomisel kasutasin tehisintellekti abi (Claude Sonnet 5.5).
 */

import java.util.*;

/** Quaternions. Basic operations. */
public class Quaternion {

   private static final double EPSILON = 0.000001;

   private final double a;
   private final double b;
   private final double c;
   private final double d;

   /** Constructor from four double values.
    * @param a real part
    * @param b imaginary part i
    * @param c imaginary part j
    * @param d imaginary part k
    */
   public Quaternion (double a, double b, double c, double d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
   }

   /** Real part of the quaternion.
    * @return real part
    */
   public double getRpart() {
      return a;
   }

   /** Imaginary part i of the quaternion.
    * @return imaginary part i
    */
   public double getIpart() {
      return b;
   }

   /** Imaginary part j of the quaternion.
    * @return imaginary part j
    */
   public double getJpart() {
      return c;
   }

   /** Imaginary part k of the quaternion.
    * @return imaginary part k
    */
   public double getKpart() {
      return d;
   }

   /** Conversion of the quaternion to the string.
    * @return a string form of this quaternion:
    * "a+bi+cj+dk"
    * (without any brackets)
    */
   @Override
   public String toString() {
      return a + sign (b) + "i" + sign (c) + "j" + sign (d) + "k";
   }

   private static String sign (double x) {
      return (x < 0 || 1 / x < 0) ? "" + x : "+" + x;
   }

   /** Conversion from the string to the quaternion.
    * Reverse to <code>toString</code> method.
    * @throws IllegalArgumentException if string s does not represent
    *     a quaternion (defined by the <code>toString</code> method)
    * @param s string of form produced by the <code>toString</code> method
    * @return a quaternion represented by string s
    */
   public static Quaternion valueOf (String s) {
      if (s == null)
         throw new IllegalArgumentException ("Cannot convert null to a quaternion");
      try {
         for (char ch : s.toCharArray())
            if ("0123456789.+-eEijk".indexOf (ch) < 0)
               throw new NumberFormatException();
         int i = s.indexOf ('i');
         int j = s.indexOf ('j');
         int k = s.indexOf ('k');
         if (i < 0 || j < i || k < j || k != s.length() - 1)
            throw new NumberFormatException();
         int split = i - 1;
         while (split > 0 && ("+-".indexOf (s.charAt (split)) < 0 || "eE".indexOf (s.charAt (split - 1)) >= 0))
            split--;
         if (split <= 0)
            throw new NumberFormatException();
         return new Quaternion (number (s.substring (0, split), false), number (s.substring (split, i), true),
                 number (s.substring (i + 1, j), true), number (s.substring (j + 1, k), true));
      } catch (NumberFormatException e) {
         throw new IllegalArgumentException ("Not a quaternion (expected a+bi+cj+dk): \"" + s + "\"");
      }
   }

   /** Parse one part of the quaternion string. */
   private static double number (String part, boolean signRequired) {
      if (part.isEmpty() || (signRequired && "+-".indexOf (part.charAt (0)) < 0))
         throw new NumberFormatException();
      return Double.parseDouble (part);
   }

   /** Clone of the quaternion.
    * @return independent clone of <code>this</code>
    */
   @Override
   public Object clone() throws CloneNotSupportedException {
      return new Quaternion (a, b, c, d);
   }

   /** Test whether the quaternion is zero.
    * @return true, if the real part and all the imaginary parts are (close to) zero
    */
   public boolean isZero() {
      return Math.abs (a) < EPSILON && Math.abs (b) < EPSILON
              && Math.abs (c) < EPSILON && Math.abs (d) < EPSILON;
   }

   /** Conjugate of the quaternion. Expressed by the formula
    *     conjugate(a+bi+cj+dk) = a-bi-cj-dk
    * @return conjugate of <code>this</code>
    */
   public Quaternion conjugate() {
      return new Quaternion (a, -b, -c, -d);
   }

   /** Opposite of the quaternion. Expressed by the formula
    *    opposite(a+bi+cj+dk) = -a-bi-cj-dk
    * @return quaternion <code>-this</code>
    */
   public Quaternion opposite() {
      return new Quaternion (-a, -b, -c, -d);
   }

   /** Sum of quaternions. Expressed by the formula
    *    (a1+b1i+c1j+d1k) + (a2+b2i+c2j+d2k) = (a1+a2) + (b1+b2)i + (c1+c2)j + (d1+d2)k
    * @param q addend
    * @return quaternion <code>this+q</code>
    */
   public Quaternion plus (Quaternion q) {
      return new Quaternion (a + q.a, b + q.b, c + q.c, d + q.d);
   }

   /** Product of quaternions. Expressed by the formula
    *  (a1+b1i+c1j+d1k) * (a2+b2i+c2j+d2k) = (a1a2-b1b2-c1c2-d1d2) + (a1b2+b1a2+c1d2-d1c2)i +
    *  (a1c2-b1d2+c1a2+d1b2)j + (a1d2+b1c2-c1b2+d1a2)k
    * @param q factor
    * @return quaternion <code>this*q</code>
    */
   public Quaternion times (Quaternion q) {
      return new Quaternion (
              a * q.a - b * q.b - c * q.c - d * q.d,
              a * q.b + b * q.a + c * q.d - d * q.c,
              a * q.c - b * q.d + c * q.a + d * q.b,
              a * q.d + b * q.c - c * q.b + d * q.a);
   }

   /** Multiplication by a coefficient.
    * @param r coefficient
    * @return quaternion <code>this*r</code>
    */
   public Quaternion times (double r) {
      return new Quaternion (a * r, b * r, c * r, d * r);
   }

   /** Inverse of the quaternion. Expressed by the formula
    *     1/(a+bi+cj+dk) = a/(a*a+b*b+c*c+d*d) +
    *     ((-b)/(a*a+b*b+c*c+d*d))i + ((-c)/(a*a+b*b+c*c+d*d))j + ((-d)/(a*a+b*b+c*c+d*d))k
    * @return quaternion <code>1/this</code>
    */
   public Quaternion inverse() {
      if (isZero())
         throw new RuntimeException ("Division by zero: cannot invert " + this);
      double n2 = a * a + b * b + c * c + d * d;
      return new Quaternion (a / n2, -b / n2, -c / n2, -d / n2);
   }

   /** Difference of quaternions. Expressed as addition to the opposite.
    * @param q subtrahend
    * @return quaternion <code>this-q</code>
    */
   public Quaternion minus (Quaternion q) {
      return plus (q.opposite());
   }

   /** Right quotient of quaternions. Expressed as multiplication to the inverse.
    * @param q (right) divisor
    * @return quaternion <code>this*inverse(q)</code>
    */
   public Quaternion divideByRight (Quaternion q) {
      if (q.isZero())
         throw new RuntimeException ("Division by zero: " + this + " / " + q);
      return times (q.inverse());
   }

   /** Left quotient of quaternions.
    * @param q (left) divisor
    * @return quaternion <code>inverse(q)*this</code>
    */
   public Quaternion divideByLeft (Quaternion q) {
      if (q.isZero())
         throw new RuntimeException ("Division by zero: " + q + " \\ " + this);
      return q.inverse().times (this);
   }

   /** Equality test of quaternions. Difference of equal numbers
    *     is (close to) zero.
    * @param qo second quaternion
    * @return logical value of the expression <code>this.equals(qo)</code>
    */
   @Override
   public boolean equals (Object qo) {
      return qo instanceof Quaternion && minus ((Quaternion) qo).isZero();
   }

   /** Dot product of quaternions. (p*conjugate(q) + q*conjugate(p))/2
    * @param q factor
    * @return dot product of this and q
    */
   public Quaternion dotMult (Quaternion q) {
      return times (q.conjugate()).plus (q.times (conjugate())).times (0.5);
   }

   /** Integer hashCode has to be the same for equal objects.
    * @return hashcode
    */
   @Override
   public int hashCode() {
      return Objects.hash (Math.round (a / EPSILON), Math.round (b / EPSILON),
              Math.round (c / EPSILON), Math.round (d / EPSILON));
   }

   /** Norm of the quaternion. Expressed by the formula
    *     norm(a+bi+cj+dk) = Math.sqrt(a*a+b*b+c*c+d*d)
    * @return norm of <code>this</code> (norm is a real number)
    */
   public double norm() {
      return Math.sqrt (a * a + b * b + c * c + d * d);
   }

   /** Main method for testing purposes.
    * @param args command line parameters
    */
   public static void main (String[] args) {
      Quaternion p = new Quaternion (1., 2., 3., 4.);
      Quaternion q = new Quaternion (-2., 1., 0.5, -3.);
      System.out.println (p + " | " + q);
      System.out.println ("p+q = " + p.plus (q) + ", p-q = " + p.minus (q));
      System.out.println ("p*q = " + p.times (q) + ", q*p = " + q.times (p));
      System.out.println ("p*inverse(p) = " + p.times (p.inverse()));
      System.out.println ("p/q right = " + p.divideByRight (q) + ", left = " + p.divideByLeft (q));
      System.out.println ("dot = " + p.dotMult (q) + ", norm(p) = " + p.norm());
      System.out.println ("round trip: " + valueOf (q.toString()).equals (q));
      System.out.println (valueOf ("-1-2i-3j-4k") + " " + valueOf ("-2.0e-3-4.0e-1i-5.0e-2j-6.0e-3k"));
      for (String bad : new String[] {"abc", "1+-2i+3j+4k", "1+2i+3j", "", "1 2i 3j 4k", "NaN+1i+2j+3k"}) {
         try {
            valueOf (bad);
            System.out.println ("ERROR: accepted \"" + bad + "\"");
         } catch (IllegalArgumentException e) {
            System.out.println (e.getMessage());
         }
      }
      try {
         p.divideByRight (new Quaternion (0., 0., 0., 0.));
      } catch (RuntimeException e) {
         System.out.println (e.getMessage());
      }
   }

}
