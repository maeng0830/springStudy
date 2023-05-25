package hello.springtx.propagation;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Slf4j
@SpringBootTest
public class BasicTxTest {

	@Autowired
	PlatformTransactionManager txManager;

	@TestConfiguration
	static class Config {
		@Bean
		public PlatformTransactionManager transactionManager(DataSource dataSource) {
			return new DataSourceTransactionManager(dataSource);
		}
	}

	// 트랜잭션이 시작되면 커넥션을 획득한다.
	// 커밋 후 트랜잭션이 종료되고 커넥션을 반환한다.
	@Test
	void commit() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		** Acquired Connection [HikariProxyConnection@2003467974 wrapping conn0: url=jdbc:h2:mem:28ee51fd-2882-4e0b-84f3-7b52a693210c user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@2003467974 wrapping conn0: url=jdbc:h2:mem:28ee51fd-2882-4e0b-84f3-7b52a693210c user=SA] to manual commit

		log.info("트랜잭션 커밋 시작");
		txManager.commit(status);
//		Initiating transaction commit
//		Committing JDBC transaction on Connection [HikariProxyConnection@2003467974 wrapping conn0: url=jdbc:h2:mem:28ee51fd-2882-4e0b-84f3-7b52a693210c user=SA]
//		** Releasing JDBC Connection [HikariProxyConnection@2003467974 wrapping conn0: url=jdbc:h2:mem:28ee51fd-2882-4e0b-84f3-7b52a693210c user=SA] after transaction

		log.info("트랜잭션 커밋 완료");
	}

	// 트랜잭션이 시작되면 커넥션을 획득한다.
	// 롤백 후 트랜잭션이 종료되고 커넥션을 반환한다.
	@Test
	void rollback() {
		log.info("트랜잭션 시작");
		TransactionStatus status = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		** Acquired Connection [HikariProxyConnection@1876630105 wrapping conn0: url=jdbc:h2:mem:d9c06514-a42f-41dc-9b0a-8986a99cb652 user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@1876630105 wrapping conn0: url=jdbc:h2:mem:d9c06514-a42f-41dc-9b0a-8986a99cb652 user=SA] to manual commit

		log.info("트랜잭션 롤백 시작");
		txManager.rollback(status);
//		Initiating transaction rollback
//		Rolling back JDBC transaction on Connection [HikariProxyConnection@1876630105 wrapping conn0: url=jdbc:h2:mem:d9c06514-a42f-41dc-9b0a-8986a99cb652 user=SA]
//		** Releasing JDBC Connection [HikariProxyConnection@1876630105 wrapping conn0: url=jdbc:h2:mem:d9c06514-a42f-41dc-9b0a-8986a99cb652 user=SA] after transaction

		log.info("트랜잭션 커밋 완료");
	}

	// 트랜잭션1이 시작하면서 커넥션을 획득하고, 커밋 후 커넥션을 반환한다.
	// 트랜잭션2가 시작하면서 커넥션을 획득하고, 커밋 후 커넥션을 반환한다.
	// 로그를 보면 conn0이라는 동일한 물리 커넥션을 사용하고 있지만, 해당 커넥션을 감싸는 프록시 객체의 주소는 다르다.
	// 즉, 본 예제에서는 한 번에 1개의 트랜잭션만 순차적으로 진행되고있기 때문에 같은 커넥션을 사용하는 것 처럼 보이지만,
	// 각 트랜잭션은 각각 다른 커넥션을 사용한 것이다.
	@Test
	void doubleCommit() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Acquired Connection [HikariProxyConnection@677726975 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@677726975 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] to manual commit

		log.info("트랜잭션1 커밋 시작");
		txManager.commit(tx1);
//		Initiating transaction commit
//		Committing JDBC transaction on Connection [HikariProxyConnection@677726975 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA]
//		Releasing JDBC Connection [HikariProxyConnection@677726975 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] after transaction


		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Acquired Connection [HikariProxyConnection@746876245 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@746876245 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] to manual commit

		log.info("트랜잭션2 커밋 시작");
		txManager.commit(tx2);
//		Initiating transaction commit
//		Committing JDBC transaction on Connection [HikariProxyConnection@746876245 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA]
//		Releasing JDBC Connection [HikariProxyConnection@746876245 wrapping conn0: url=jdbc:h2:mem:5b0eefba-0ffa-4e2a-b404-a3b6b3758b55 user=SA] after transaction
	}

	// 트랜잭션1이 시작하면서 커넥션을 획득하고, 롤백 후 커넥션을 반환한다.
	// 트랜잭션2가 시작하면서 커넥션을 획득하고, 롤백 후 커넥션을 반환한다.
	// 로그를 보면 conn0이라는 동일한 물리 커넥션을 사용하고 있지만, 해당 커넥션을 감싸는 프록시 객체의 주소는 다르다.
	// 즉, 본 예제에서는 한 번에 1개의 트랜잭션만 순차적으로 진행되고있기 때문에 같은 커넥션을 사용하는 것 처럼 보이지만,
	// 각 트랜잭션은 각각 다른 커넥션을 사용한 것이다.
	@Test
	void doubleRollback() {
		log.info("트랜잭션1 시작");
		TransactionStatus tx1 = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Acquired Connection [HikariProxyConnection@1051130608 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@1051130608 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] to manual commit

		log.info("트랜잭션1 롤백 시작");
		txManager.rollback(tx1);
//		Initiating transaction rollback
//		Rolling back JDBC transaction on Connection [HikariProxyConnection@1051130608 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA]
//		Releasing JDBC Connection [HikariProxyConnection@1051130608 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] after transaction


		log.info("트랜잭션2 시작");
		TransactionStatus tx2 = txManager.getTransaction(
				new DefaultTransactionDefinition());
//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
//		Acquired Connection [HikariProxyConnection@1388081103 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] for JDBC transaction
//		Switching JDBC Connection [HikariProxyConnection@1388081103 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] to manual commit

		log.info("트랜잭션2 롤백 시작");
		txManager.rollback(tx2);
//		Initiating transaction rollback
//		Rolling back JDBC transaction on Connection [HikariProxyConnection@1388081103 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA]
//		Releasing JDBC Connection [HikariProxyConnection@1388081103 wrapping conn0: url=jdbc:h2:mem:9b4c41c9-fdc6-46fd-b2c3-4142c1cbc171 user=SA] after transaction
	}

	// 외부 트랜잭션이 시작하고, 커넥션을 획득한다.
	// 내부 트랜잭션이 시작된다. 이 때 별도의 커넥션을 획득하지 않고, 기존 외부 트랜잭션에 합류한다.
	// 내부 트랜잭션이 커밋된다. 아무 로직도 일어나지 않는다. 단순히 내부 트랜잭션이 정상 커밋됐다는 것만 기록하는 것이다.
	// 외부 트랜잭션이 커밋된다. 내부 트랜잭션도 커밋되었고, 외부 트랜잭션도 커밋되었으므로 물리 트랜잭션이 커밋되고 커넥션을 반환한다.
	// 즉 물리 트랜잭션은 외부 트랜잭션이 관리한다.
	@Test
	void innerCommit() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
	//		외부 트랜잭션 시작
	//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
	//		Acquired Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:d0070460-f86d-499a-840e-1c10993082cb user=SA] for JDBC transaction
	//		Switching JDBC Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:d0070460-f86d-499a-840e-1c10993082cb user=SA] to manual commit
	//		outer.isNewTransaction()=true

		log.info("내부 트랜잭션 시작");
		TransactionStatus inner = txManager.getTransaction(
				new DefaultTransactionDefinition());
		log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
	//		내부 트랜잭션 시작
	//		Participating in existing transaction
	//		inner.isNewTransaction()=false

		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);
	//		내부 트랜잭션 커밋

		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);
	//		외부 트랜잭션 커밋
	//		Initiating transaction commit
	//		Committing JDBC transaction on Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:d0070460-f86d-499a-840e-1c10993082cb user=SA]
	//		Releasing JDBC Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:d0070460-f86d-499a-840e-1c10993082cb user=SA] after transaction
	}

	@Test
	void outerRollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
	//		외부 트랜잭션 시작
	//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
	//		Acquired Connection [HikariProxyConnection@1438859728 wrapping conn0: url=jdbc:h2:mem:efc24126-94de-4b3f-acf4-cb474ae64e58 user=SA] for JDBC transaction
	//		Switching JDBC Connection [HikariProxyConnection@1438859728 wrapping conn0: url=jdbc:h2:mem:efc24126-94de-4b3f-acf4-cb474ae64e58 user=SA] to manual commit
	//		outer.isNewTransaction()=true

		log.info("내부 트랜잭션 시작");
		TransactionStatus inner = txManager.getTransaction(
				new DefaultTransactionDefinition());
		log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
	//		내부 트랜잭션 시작
	//		Participating in existing transaction
	//		inner.isNewTransaction()=false

		log.info("내부 트랜잭션 커밋");
		txManager.commit(inner);
	//		내부 트랜잭션 커밋

		log.info("외부 트랜잭션 롤백");
		txManager.rollback(outer);
	//		외부 트랜잭션 롤백
	//		Initiating transaction rollback
	//		Rolling back JDBC transaction on Connection [HikariProxyConnection@1438859728 wrapping conn0: url=jdbc:h2:mem:efc24126-94de-4b3f-acf4-cb474ae64e58 user=SA]
	//		Releasing JDBC Connection [HikariProxyConnection@1438859728 wrapping conn0: url=jdbc:h2:mem:efc24126-94de-4b3f-acf4-cb474ae64e58 user=SA] after transaction
	}

	@Test
	void innerRollback() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
	//		외부 트랜잭션 시작
	//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
	//		Acquired Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:e5f32eb9-84e3-4dde-aaeb-4865d1a11d80 user=SA] for JDBC transaction
	//		Switching JDBC Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:e5f32eb9-84e3-4dde-aaeb-4865d1a11d80 user=SA] to manual commit
	//		outer.isNewTransaction()=true

		log.info("내부 트랜잭션 시작");
		TransactionStatus inner = txManager.getTransaction(
				new DefaultTransactionDefinition());
		log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
	//		내부 트랜잭션 시작
	//		Participating in existing transaction
	//		inner.isNewTransaction()=false

		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);
	//		내부 트랜잭션 커밋
	//		** Participating transaction failed - marking existing transaction as rollback-only **
	//		Setting JDBC transaction [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:e5f32eb9-84e3-4dde-aaeb-4865d1a11d80 user=SA] rollback-only

		log.info("외부 트랜잭션 커밋");
		Assertions.assertThatThrownBy(() -> txManager.commit(outer))
				.isInstanceOf(UnexpectedRollbackException.class);
	//		외부 트랜잭션 롤백
	//		** Global transaction is marked as rollback-only but transactional code requested commit **
	//		Initiating transaction rollback
	//		Rolling back JDBC transaction on Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:e5f32eb9-84e3-4dde-aaeb-4865d1a11d80 user=SA]
	//		Releasing JDBC Connection [HikariProxyConnection@881578083 wrapping conn0: url=jdbc:h2:mem:e5f32eb9-84e3-4dde-aaeb-4865d1a11d80 user=SA] after transaction
	//		UnexpectedRollbackException 발생
	}

	@Test
	void innerRollbackRequiresNew() {
		log.info("외부 트랜잭션 시작");
		TransactionStatus outer = txManager.getTransaction(new DefaultTransactionDefinition());
		log.info("outer.isNewTransaction()={}", outer.isNewTransaction());
	//		외부 트랜잭션 시작
	//		Creating new transaction with name [null]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT
	//		Acquired Connection [HikariProxyConnection@1171196949 wrapping conn0: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] for JDBC transaction
	//		Switching JDBC Connection [HikariProxyConnection@1171196949 wrapping conn0: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] to manual commit
	//		outer.isNewTransaction()=true

		log.info("내부 트랜잭션 시작");
		DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW); // 새로운 외부 트랜잭션으로 시작
		TransactionStatus inner = txManager.getTransaction(definition);
		log.info("inner.isNewTransaction()={}", inner.isNewTransaction());
	//		내부 트랜잭션 시작
	//		** Suspending current transaction, creating new transaction with name [null] **
	//		Acquired Connection [HikariProxyConnection@839348439 wrapping conn1: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] for JDBC transaction
	//		Switching JDBC Connection [HikariProxyConnection@839348439 wrapping conn1: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] to manual commit
	//		** inner.isNewTransaction()=true **

		log.info("내부 트랜잭션 롤백");
		txManager.rollback(inner);
	//		내부 트랜잭션 롤백
	//		Initiating transaction rollback
	//		Rolling back JDBC transaction on Connection [HikariProxyConnection@839348439 wrapping conn1: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA]
	//		Releasing JDBC Connection [HikariProxyConnection@839348439 wrapping conn1: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] after transaction
	//		** Resuming suspended transaction after completion of inner transaction **

		log.info("외부 트랜잭션 커밋");
		txManager.commit(outer);
	//		외부 트랜잭션 커밋
	//		Initiating transaction commit
	//		Committing JDBC transaction on Connection [HikariProxyConnection@1171196949 wrapping conn0: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA]
	//		Releasing JDBC Connection [HikariProxyConnection@1171196949 wrapping conn0: url=jdbc:h2:mem:a63669fb-962b-48a0-a448-c45ad011c3f0 user=SA] after transaction
	}
}
