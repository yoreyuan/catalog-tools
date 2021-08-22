package yore.common.util;

import com.baomidou.dynamic.datasource.toolkit.CryptoUtils;

/**
 * 明文转密文(如果未设置默认使用全局的)
 *
 * Created by Yore
 */
public class ENC {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please enter the string to be encrypted!");
            System.exit(0);
        }
        try {
            String encryptStr = CryptoUtils.encrypt( args[0]);
            System.out.println("Plaintext：" + args[0]);
            System.out.println("Ciphertext：" + encryptStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
