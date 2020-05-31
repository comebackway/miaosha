package self.lcw01.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * 封装了MD5方法的工具类
 */
public class MD5Util {
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }


    private static final String salt = "1a2b3c4d";
    //客户端提交form表单前，与固定的salt拼接后做的一次md5加密，以防被截取信息
    public static String inputPassFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(3) + salt.charAt(5);
        return md5(str);
    }


    //服务端使用md5加密 用户输入的经salt拼接后的md5密文 + 随机salt（该随机salt存在数据库中）
    public static String formPassToDBPass(String formPass,String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(3) + salt.charAt(5);
        return md5(str);
    }


    //调用上述两个方法，直接获得用户输入密码经两次md5和salt拼接后的密文
    public static String inputPassToDbPass(String input,String saltDB){
        String formPass = inputPassFormPass(input);
        String dbPass = formPassToDBPass(formPass,saltDB);
        return dbPass;
    }


    public static void main(String[] args) {
        System.out.println(inputPassFormPass("123456"));
        System.out.println(inputPassToDbPass("123456","1a2b3c4d"));
        //System.out.println(inputPassToDbPass("123456","2djhfd9"));
    }
}
