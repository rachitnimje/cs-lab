//import java.util.Random;
//
//public class ECC {
//    // y^2 = x^3 + A*x + B
//    // G is the base point on curve
//    static final int P = 23;
//    static final int A = 1;
//    static final int B = 1;
//    static final int Gx = 3;
//    static final int Gy = 10;
//    static final int N = 19;  // Order of the group generated by G (simplified)
//
//    public static class Point {
//        int x, y;
//
//        public Point(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//
//        public boolean isInfinity() {
//            return x == -1 && y == -1;
//        }
//
//        @Override
//        public String toString() {
//            return "(" + x + ", " + y + ")";
//        }
//    }
//
//    public static class KeyPair {
//        private final int privateKey;
//        private final Point publicKey;
//
//        public KeyPair(int privateKey, Point publicKey) {
//            this.privateKey = privateKey;
//            this.publicKey = publicKey;
//        }
//
//        public int getPrivateKey() {
//            return privateKey;
//        }
//
//        public Point getPublicKey() {
//            return publicKey;
//        }
//    }
//
//    // Point addition and doubling on the elliptic curve
//    public static Point pointAdd(Point p1, Point p2) {
//        if (p1.isInfinity()) return p2;
//        if (p2.isInfinity()) return p1;
//
//        int lambda;
//        if (p1.x == p2.x && p1.y == p2.y) {
//            // Point doubling
//            lambda = (3 * p1.x * p1.x + A) * modInverse(2 * p1.y, P) % P;
//        } else {
//            // Point addition
//            lambda = (p2.y - p1.y) * modInverse(p2.x - p1.x, P) % P;
//        }
//
//        int xr = (lambda * lambda - p1.x - p2.x) % P;
//        int yr = (lambda * (p1.x - xr) - p1.y) % P;
//
//        xr = (xr + P) % P;
//        yr = (yr + P) % P;
//
//        return new Point(xr, yr);
//    }
//
//    // Scalar multiplication on the elliptic curve using double-and-add algorithm
//    public static Point scalarMultiply(Point p, int k) {
//        Point result = new Point(-1, -1); // Point at infinity
//        Point addend = p;
//
//        while (k > 0) {
//            if ((k & 1) == 1) {
//                result = pointAdd(result, addend); // Add when the bit is 1
//            }
//            addend = pointAdd(addend, addend); // Double the point
//            k >>= 1;
//        }
//
//        return result;
//    }
//
//    public static int modInverse(int a, int mod) {
//        a %= mod;
//        for (int i = 1; i < mod; i++) {
//            if ((a * i) % mod == 1)
//                return i;
//        }
//        throw new IllegalArgumentException("No modular inverse exists");
//    }
//
//    public static KeyPair generateKeyPair() {
//        Random random = new Random();
//
//        int privateKey = random.nextInt(N);
//        Point publicKey = scalarMultiply(new Point(Gx, Gy), privateKey);
//
//        return new KeyPair(privateKey, publicKey);
//    }
//
//    public static void main(String[] args) {
//        KeyPair keyPair = generateKeyPair();
//
//        System.out.println("Private Key: " + keyPair.getPrivateKey());
//        System.out.println("Public Key: " + keyPair.getPublicKey());
//    }
//}

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class ECC {

    // Define curve parameters: y^2 = x^3 + ax + b
    private static final BigInteger a = new BigInteger("2");
    private static final BigInteger b = new BigInteger("3");
    private static final BigInteger p = new BigInteger("97"); // A small prime for simplicity
    private static final Point G = new Point(BigInteger.valueOf(3), BigInteger.valueOf(6)); // Generator point
    private static final BigInteger n = new BigInteger("5"); // Small order of G for simplicity
    private static final SecureRandom random = new SecureRandom();

    public static class Point {
        BigInteger x, y;

        public Point(BigInteger x, BigInteger y) {
            this.x = x;
            this.y = y;
        }

        public boolean isAtInfinity() {
            return x == null && y == null;
        }

        public static Point infinity() {
            return new Point(null, null);
        }
    }

    // Point addition: P + Q (mod p)
    public static Point pointAdd(Point P, Point Q) {
        if (P.isAtInfinity()) return Q;
        if (Q.isAtInfinity()) return P;

        BigInteger slope;
        if (P.x.equals(Q.x)) {
            if (P.y.equals(Q.y)) {
                // Point doubling
                slope = (P.x.pow(2).multiply(BigInteger.valueOf(3)).add(a))
                        .multiply(P.y.multiply(BigInteger.TWO).modInverse(p)).mod(p);
            } else {
                return Point.infinity(); // P + (-P) = 0 (point at infinity)
            }
        } else {
            slope = (Q.y.subtract(P.y)).multiply(Q.x.subtract(P.x).modInverse(p)).mod(p);
        }

        BigInteger xR = slope.pow(2).subtract(P.x).subtract(Q.x).mod(p);
        BigInteger yR = slope.multiply(P.x.subtract(xR)).subtract(P.y).mod(p);

        return new Point(xR.mod(p), yR.mod(p));
    }

    // Scalar multiplication: k * P (mod p)
    public static Point scalarMultiply(Point P, BigInteger k) {
        Point result = Point.infinity();
        Point addend = P;

        while (k.compareTo(BigInteger.ZERO) > 0) {
            if (k.testBit(0)) {
                result = pointAdd(result, addend);
            }
            addend = pointAdd(addend, addend);
            k = k.shiftRight(1);
        }

        return result;
    }

    // Key pair generation with non-zero private key
    public static BigInteger[] generateKeyPair() {
        BigInteger privateKey;
        do {
            privateKey = new BigInteger(n.bitLength(), random).mod(n);
        } while (privateKey.equals(BigInteger.ZERO)); // Ensure private key is non-zero

        Point publicKey = scalarMultiply(G, privateKey); // public key = privateKey * G
        return new BigInteger[]{privateKey, publicKey.x, publicKey.y}; // (privateKey, publicKey.x, publicKey.y)
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Generate key pair
        BigInteger[] keyPair = generateKeyPair();
        BigInteger privateKey = keyPair[0];
        Point publicKey = new Point(keyPair[1], keyPair[2]);

        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key: (" + publicKey.x + ", " + publicKey.y + ")");

        if (!publicKey.isAtInfinity()) {
            // Get message point from user
            System.out.print("Enter x coordinate of the message: ");
            BigInteger messageX = new BigInteger(scanner.nextLine());
            System.out.print("Enter y coordinate of the message: ");
            BigInteger messageY = new BigInteger(scanner.nextLine());

            Point message = new Point(messageX, messageY);

            // Step 1: Encryption
            // k is a random scalar
            BigInteger k;
            do {
                k = new BigInteger(n.bitLength(), random).mod(n);
            } while (k.equals(BigInteger.ZERO)); // Ensure non-zero k

            // Ephemeral key = k * G (sent to receiver)
            Point ephemeralKey = scalarMultiply(G, k);
            System.out.println("Ephemeral Key (to be shared with receiver): (" + ephemeralKey.x + ", " + ephemeralKey.y + ")");

            // Shared secret = k * publicKey
            Point sharedSecret = scalarMultiply(publicKey, k);
            // Encrypt message: encryptedMessage = message + sharedSecret
            Point encryptedMessage = pointAdd(message, sharedSecret);
            System.out.println("Encrypted Message: (" + encryptedMessage.x + ", " + encryptedMessage.y + ")");

            // Step 2: Decryption
            // Decrypt: sharedSecret = ephemeralKey * privateKey
            Point decryptionSharedSecret = scalarMultiply(ephemeralKey, privateKey);
            // decryptedMessage = encryptedMessage - sharedSecret
            Point negSharedSecret = new Point(decryptionSharedSecret.x.negate().mod(p), decryptionSharedSecret.y.negate().mod(p));
            Point decryptedMessage = pointAdd(encryptedMessage, negSharedSecret);
            System.out.println("Decrypted Message: (" + decryptedMessage.x + ", " + decryptedMessage.y + ")");
        } else {
            System.out.println("Invalid public key, check curve parameters.");
        }

        scanner.close();
    }
}
