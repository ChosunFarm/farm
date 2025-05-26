package farm.farmshop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.file.upload.directory}")
    private String uploadRootDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. static 폴더의 기본 리소스들 (메인 페이지 이미지, CSS, JS 등)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/")
                .setCachePeriod(0);

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // 2. 업로드된 이미지들 (별도 경로 사용)
        String absolutePath = Paths.get(uploadRootDir).toAbsolutePath().toString() + "/";

        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:" + absolutePath)
                .setCachePeriod(0);
    }
}