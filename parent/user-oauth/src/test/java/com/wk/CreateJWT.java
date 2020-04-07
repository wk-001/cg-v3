package com.wk;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 令牌的创建和解析
 * 公钥加密，私钥解密，私钥作为秘钥给JWT加密
 */
public class CreateJWT {

    /**
     * 创建令牌
     */
    @Test
    public void createCreate(){
        //从证书中获取私钥
        //1、读取类路径中的文件 加载证书
        ClassPathResource resource = new ClassPathResource("changgou.jks");

        //2、加载读取证书数据；参数1：读取到的资源；2：读取证书所需要的密码
        KeyStoreKeyFactory keyFactory = new KeyStoreKeyFactory(resource,"changgou".toCharArray());

        //获取证书中的公钥私钥 参数1：证书的别名，2：证书的密码
        KeyPair keyPair = keyFactory.getKeyPair("changgou","changgou".toCharArray());

        //3、获取RSA算法的私钥  父接口转子接口
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        //设置载荷
        Map<String,Object> map = new HashMap<>();
        map.put("name","tom");
        map.put("age","22");
        map.put("address","bj");
        map.put("role","user,admin");

        //创建令牌需要私钥加盐[RSA算法] 1、需要加密的字符串；2、指定签名算法的盐(私钥) RSA算法的私钥
        //RSA加密(内容+私钥)
        Jwt jwt = JwtHelper.encode(JSON.toJSONString(map), new RsaSigner(privateKey));

        //获取令牌数据
        String token = jwt.getEncoded();
        System.out.println("token = " + token);
    }

    /**
     * 公钥解析私钥加密的令牌
     */
    @Test
    public void parseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhZGRyZXNzIjoiYmoiLCJyb2xlIjoidXNlcixhZG1pbiIsIm5hbWUiOiJ0b20iLCJhZ2UiOiIyMiJ9.P8euKES1-Qn3sASwMZMZSxdKSGA-ZIw_E7zGDwT3pRT55OiLf3sDWnHHajDdIx9P56iA-8RrPvfQX_nu_lZ06kgd_GIaHhGDVnX7BmoONykWoE3JzXrUEtJhD1MsmINKTxREvugC-Cd7rkORWM4j9WanvhfCuk_S5ByjJtu8iwSo2qG9fmP33aBOo8fZgvZhNkkqzacpB8Gu2nbeGR3AuQOSBYMUy1Ydu0pC-lAm2pvYMWO2neofb5f5ZM8GAKGUdEM19QvrizP82FvzerM-xDxiOhzLhkpfr_zkzH60i83FDH4vIhZi1S92jAzXgCl7IBafT-XzB0OPVgaJYSVm9g";
        //公钥
        String publicKey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAglULkpKVFyt3paKQXSmRIUOT6XxkMwamcYJChRy5zjh1O217zrlr+s9SE+no4Yo5Z/EjwQ25pT0uEQVfCjNtWrVB6AsgVz8xtmTI7J/IJ39BK86qGpaporF7nlh0m3DDKtkjMRWcmDkLC2UTAZVazvmk2BmMlLgd49PzFV8jCaozzwsOe9uHh0T8q9H5HtsD+WGdVvo+IIsonQC/03xqnszpxgMFZ8Ke5iP5sQ7e2/mgpqdlhzUc2fttDSCSflTYWrSWwxzgUgG4kp5VkLvCp1Fz7xF2Lvm54GW1+d/xKKln+KBSPwo2j8tcpcxvdNR7OGojylh0ZobJzGj9T5VVswIDAQAB-----END PUBLIC KEY-----";

        //解析令牌
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publicKey));
        String result = jwt.getClaims();
        System.out.println("result = " + result);
    }

}
