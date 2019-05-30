package cn.swiftpass.wftpay;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {
    public static final String ENCRYPTION_MANNER = "DESede";
    /**
     * 3DES加密
     *
     * @param data 加密数据
     * @param key  加密密码
     * @return
     * @throws Exception
     */
    public static String encrypt3DES(byte[] data, byte[] key) throws Exception {
        // 恢复密钥
        SecretKey secretKey = new SecretKeySpec(key, ENCRYPTION_MANNER);
        // Cipher完成加密
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MANNER);
        // cipher初始化
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypt = cipher.doFinal(data);
        //转码
        return new String(Base64.encode(encrypt, Base64.DEFAULT), "UTF-8");
    }

    /**
     * 3DS解密
     * @param data 加密后的字符串
     * @param key 加密密码
     * @return
     * @throws Exception
     */
    public static String decrypt3DES(String data, byte[] key) throws Exception {
        // 恢复密钥
        SecretKey secretKey = new SecretKeySpec(key, ENCRYPTION_MANNER);
        // Cipher完成解密
        Cipher cipher = Cipher.getInstance(ENCRYPTION_MANNER);
        // 初始化cipher
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        //转码
        byte[] bytes = Base64.decode(data.getBytes("UTF-8"), Base64.DEFAULT);
        //解密
        byte[] plain = cipher.doFinal(bytes);
        //解密结果转码
        return  new String(plain, "utf-8");
    }

    /**
     * MD5 加密
     */
    public static String encryptMd5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
