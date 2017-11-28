package koala.administration;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bjama on 4/25/2017.
 */
public class MD5 {
    private String code;

    public MD5(String md5) {
        Passe(md5);

    }

    public void Passe(String pass){
        byte[] passBytes = pass.getBytes();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(passBytes);
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(passBytes);
            BigInteger number = new BigInteger(1, messageDigest);
            this.code= number.toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new Error("invalid JRE: have not 'MD5' impl.", e);
        }
    }
    public String codeGet(){
        return code;
    }
    @Override // pour l'essei
    public String toString(){
        return this.code;}
    // pour l'essei
//    public static void main(String[] args) {
//
//        while (true) {
//            System.out.println(new MD5(new Scanner(System.in).nextLine()));
//        }
//    }

}