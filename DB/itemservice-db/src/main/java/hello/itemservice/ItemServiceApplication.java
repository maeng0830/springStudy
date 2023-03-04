package hello.itemservice;

import com.zaxxer.hikari.HikariDataSource;
import hello.itemservice.config.JdbcTemplateV3Config;
import hello.itemservice.config.MyBatisConfig;
import hello.itemservice.repository.ItemRepository;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


//@Import(MemoryConfig.class) // 아래에서 컴포넌트 스캔 범위를 지정하지 않을 경우, @Configuration도 자동으로 컴포넌트 스캔된다.
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
//@Import(JdbcTemplateV3Config.class)
@Import(MyBatisConfig.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web") // 컴포넌트 스캔 범위 지정, MemoryConfig는 범위에 해당안된다.
@Slf4j
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local") // 스프링 구동 profile이 local일 때만 빈으로 등록
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

	/*@Bean
	@Profile("test")
	public DataSource dataSource() {
		// test 프로필로 실행될 경우, 여기서 생성한 dataSource를 빈으로 사용한다.
		log.info("메모리 데이터베이스 초기화");
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setPoolName("org.h2.Driver");
		*//**
		 * 임베디드 모드(메모리 모드)
		 * DB_ClOSE_DELAY=-1는 임베디드 모드에서 데이터베이스 커넥션 연결이 모두 끊어지면 데이터베이스도 종료되는데 그것을 방지하는 설정
		 *//*
		dataSource.setJdbcUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}*/
}
