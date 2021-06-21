package cn.chenjy.yums.http.config;

import cn.chenjy.yums.http.template.OkHttpTemplate;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * @author ChenJY
 * @create 2021/6/18 10:43 上午
 * @DESCRIPTION
 */
@Configuration
@EnableConfigurationProperties(HttpProperties.class)
@ConditionalOnProperty(value = "yums.http.type", havingValue = "okhttp",matchIfMissing=true)
public class OkHttpConfig {
    private static final Logger LOG = LoggerFactory.getLogger(OkHttpConfig.class);
    private static final String TAG = "HttpConfig";

    private final HttpProperties httpProperties;

    public OkHttpConfig(HttpProperties httpProperties) {
        this.httpProperties = httpProperties;
    }

    @Bean
    @ConditionalOnMissingBean(X509TrustManager.class)
    public X509TrustManager x509TrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(SSLSocketFactory.class)
    public SSLSocketFactory sslSocketFactory() {
        try {
            //信任任何链接
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{x509TrustManager()}, new SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a new connection pool with tuning parameters appropriate for a single-user application.
     * The tuning parameters in this pool are subject to change in future OkHttp releases. Currently
     */
    @Bean
    @ConditionalOnMissingBean(ConnectionPool.class)
    public ConnectionPool pool() {
        return new ConnectionPool(200, 5, TimeUnit.MINUTES);
    }

    @Bean
    @ConditionalOnMissingBean(OkHttpClient.class)
    @ConditionalOnBean({SSLSocketFactory.class, X509TrustManager.class, ConnectionPool.class})
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory(), x509TrustManager())
                //是否开启缓存
                .retryOnConnectionFailure(false)
                //连接池
                .connectionPool(pool())
                .connectTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(10L, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean(OkHttpTemplate.class)
    @ConditionalOnBean({SSLSocketFactory.class, X509TrustManager.class, ConnectionPool.class, OkHttpClient.class})
    public OkHttpTemplate okHttpTemplate() {
        return new OkHttpTemplate();
    }
}
