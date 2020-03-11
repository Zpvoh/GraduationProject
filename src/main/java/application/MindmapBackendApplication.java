package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import javax.servlet.MultipartConfigElement;

@Configuration
@SpringBootApplication
public class MindmapBackendApplication extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(MindmapBackendApplication.class, args);
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        ///最大文件
        factory.setMaxFileSize("2048MB"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("2048MB");
        return factory.createMultipartConfig();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
