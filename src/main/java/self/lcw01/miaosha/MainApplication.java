package self.lcw01.miaosha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
//继承SpringBootServletInitializer类，重写configure方法，实现既可以以jar包方式跑，也可以以war包方式跑功能
public class MainApplication extends SpringBootServletInitializer{
    public static void main(String[] args) throws Exception{
        SpringApplication.run(MainApplication.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(MainApplication.class);
    }
}
