package hello.itemservice;

import hello.itemservice.config.JdbcTemplateV3Config;
import hello.itemservice.repository.ItemRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


//@Import(MemoryConfig.class) // 아래에서 컴포넌트 스캔 범위를 지정하지 않을 경우, @Configuration도 자동으로 컴포넌트 스캔된다.
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
@Import(JdbcTemplateV3Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web") // 컴포넌트 스캔 범위 지정, MemoryConfig는 범위에 해당안된다.
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local") // 스프링 구동 profile이 local일 때만 빈으로 등록
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

}
