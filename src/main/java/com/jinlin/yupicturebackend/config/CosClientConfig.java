package com.jinlin.yupicturebackend.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.region.Region;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration    //表示它是一个配置文件
@ConfigurationProperties(prefix = "cos.client")
@Data
public class CosClientConfig {
    /**
     * 域名
     */
    private String host;
    /**
     * secretId
     */
    private String secretId;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * 区域
     */
    private String region;
    /**
     * 桶名
     */
    private String bucket;
    @Bean
    public COSClient cosClient() {
        // 设置用户身份信息。
        // SECRETID 和 SECRETKEY 请登录访问管理控制台 https://console.cloud.tencent.com/cam/capi 进行查看和管理
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // ClientConfig 中包含了后续请求 COS 的客户端设置：
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        // 设置 bucket 的地域
        // COS_REGION 请参见 https://cloud.tencent.com/document/product/436/6224
        // 以下的设置，是可选的：
        // 设置 socket 读取超时，默认 30s
        // clientConfig.setSocketTimeout(30*1000);
        // 设置建立连接超时，默认 30s
        // clientConfig.setConnectionTimeout(30*1000);
        // 如果需要的话，设置 http 代理，ip 以及 port
        // clientConfig.setHttpProxyIp("httpProxyIp");
        // clientConfig.setHttpProxyPort(80);
        //可以不用写，一般都是HTTPS协议
        clientConfig.setHttpProtocol(HttpProtocol.https);
        // 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }
}
