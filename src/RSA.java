//import java.math.BigInteger;
//import java.security.SecureRandom;
//
//public class RSA {
//    private BigInteger n, d, e;
//
//    public RSA(int bitLength) {
//        SecureRandom secureRandom = new SecureRandom();
//        BigInteger p = BigInteger.probablePrime(bitLength / 2, secureRandom);
//        BigInteger q = BigInteger.probablePrime(bitLength / 2, secureRandom);
//        System.out.println("p: " + p + "\nq: " + q);
//        n = p.multiply(q);
//
//        BigInteger phin = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
//        e = BigInteger.probablePrime(bitLength / 2, secureRandom);
//
//        // Ensure that e is co-prime with phin(gcd compareTo 1 will return +ve) and smaller than phin
//        while (phin.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phin) < 0) {
//            e.add(BigInteger.ONE);
//        }
//
//        d = e.modInverse(phin);
//    }
//
//    public BigInteger encrypt(BigInteger message) {
//        return message.modPow(e, n);
//    }
//
//    public BigInteger decrypt(BigInteger ciphertext) {
//        return ciphertext.modPow(d, n);
//    }
//
//    public static void main(String[] args) {
//        int bitLength = 256;
//        RSA rsa = new RSA(bitLength);
//
//        String plaintext = "CS Lab RSA assignment";
//        System.out.println("Plaintext: " + plaintext);
//
//        BigInteger plaintextBigInt = new BigInteger(plaintext.getBytes());
//        System.out.println("Plain text in bytes: " + plaintextBigInt);
//
//        BigInteger ciphertext = rsa.encrypt(plaintextBigInt);
//        System.out.println("Encrypted: " + ciphertext);
//
//        BigInteger decryptedBigInt = rsa.decrypt(ciphertext);
//        String decryptedText = new String(decryptedBigInt.toByteArray());
//        System.out.println("Decrypted: " + decryptedText);
//    }
//}


import java.util.Scanner;

public class RSA {
    private long n, d, e;

    public RSA(long p, long q) {
        n = p * q;
        System.out.println("p: " + p + "\nq: " + q + "\nn: " + n);

        long phi_n = (p - 1) * (q - 1);
        e = 3;
        while (gcd(e, phi_n) != 1) {
            e++;
        }
        d = modInverse(e, phi_n);
    }

    private long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    private long modInverse(long a, long m) {
        a %= m;

        for (long i = 1; i < m; i++) {
            if ((a * i) % m == 1) {
                return i;
            }
        }
        return 1;
    }

    private long modPow(long base, long exp, long mod) {
        long result = 1;
        base %= mod;

        while (exp > 0) {
            if ((exp % 2) == 1) {
                result = (result * base) % mod;
            }
            exp = exp >> 1;
            base = (base * base) % mod;
        }

        return result;
    }

    public long encrypt(long message) {
        return modPow(message, e, n);
    }

    public long decrypt(long cipherText) {
        return modPow(cipherText, d, n);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter prime number p:");
        long p = scanner.nextLong();

        System.out.println("Enter prime number q:");
        long q = scanner.nextLong();

        RSA rsa = new RSA(p, q);

        System.out.println("Enter the plaintext message (as a number):");
        long message = scanner.nextLong();

        long ciphertext = rsa.encrypt(message);
        System.out.println("Encrypted: " + ciphertext);

        long decryptedMessage = rsa.decrypt(ciphertext);
        System.out.println("Decrypted: " + decryptedMessage);

        scanner.close();
    }
}
