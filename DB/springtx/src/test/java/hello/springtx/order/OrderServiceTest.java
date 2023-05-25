package hello.springtx.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderServiceTest {

	@Autowired
	OrderService orderService;

	@Autowired
	OrderRepository orderRepository;

	@Test
	void order() throws NotEnoughMoneyException {
		// given
		Order order1 = new Order();
		order1.setUsername("정상");

		Order order2 = new Order();
		order2.setUsername("예외");

		Order order3 = new Order();
		order3.setUsername("잔고부족");

		// when
		orderService.order(order1);

		//then
		// 정상
		Order findOrder1 = orderRepository.findById(order1.getId()).get();
		assertThat(findOrder1.getPayStatus()).isEqualTo("완료");

		// 예외
		assertThatThrownBy(() -> orderService.order(order2))
				.isInstanceOf(RuntimeException.class);
		Optional<Order> orderOptional = orderRepository.findById(order2.getId());
		assertThat(orderOptional).isEmpty();

		// 잔고부족
		assertThatThrownBy(() -> orderService.order(order3))
				.isInstanceOf(NotEnoughMoneyException.class);
		Order findOrder3 = orderRepository.findById(order3.getId()).get();
		assertThat(findOrder3.getPayStatus()).isEqualTo("대기");
	}

	@Test
	void orderTryCatchUncheckedException() throws NotEnoughMoneyException {
		// given
		Order order1 = new Order();
		order1.setUsername("정상");

		Order order2 = new Order();
		order2.setUsername("예외");

		Order order3 = new Order();
		order3.setUsername("잔고부족");

		// when
		orderService.orderTryCatchUncheckedException(order1);

		//then
		// 정상
		Order findOrder1 = orderRepository.findById(order1.getId()).get();
		assertThat(findOrder1.getPayStatus()).isEqualTo("완료");

		// 예외
		orderService.orderTryCatchUncheckedException(order2);
		Optional<Order> orderOptional = orderRepository.findById(order2.getId());
		assertThat(orderOptional.get().getPayStatus()).isNull();
		assertThat(orderOptional.get().getId()).isEqualTo(2);

		// 잔고부족
		assertThatThrownBy(() -> orderService.orderTryCatchUncheckedException(order3))
				.isInstanceOf(NotEnoughMoneyException.class);
		Order findOrder3 = orderRepository.findById(order3.getId()).get();
		assertThat(findOrder3.getPayStatus()).isEqualTo("대기");
	}

	@Test
	void orderTryCatchCheckedException() {
		// given
		Order order1 = new Order();
		order1.setUsername("정상");

		Order order2 = new Order();
		order2.setUsername("예외");

		Order order3 = new Order();
		order3.setUsername("잔고부족");

		// when
		orderService.orderTryCatchCheckedException(order1);

		//then
		// 정상
		Order findOrder1 = orderRepository.findById(order1.getId()).get();
		assertThat(findOrder1.getPayStatus()).isEqualTo("완료");

		// 예외
		assertThatThrownBy(() -> orderService.orderTryCatchCheckedException(order2))
				.isInstanceOf(RuntimeException.class);
		Optional<Order> orderOptional = orderRepository.findById(order2.getId());
		assertThat(orderOptional).isEmpty();

		// 잔고부족
		orderService.orderTryCatchCheckedException(order3);
		Order findOrder3 = orderRepository.findById(order3.getId()).get();
		assertThat(findOrder3.getPayStatus()).isEqualTo("대기");
	}

	@Test
	void runtimeException() throws NotEnoughMoneyException {
		// given
		Order order2 = new Order();
		order2.setUsername("예외");

		// when

		//then
		assertThatThrownBy(() -> orderService.order(order2))
				.isInstanceOf(RuntimeException.class);
		Optional<Order> orderOptional = orderRepository.findById(order2.getId());
		assertThat(orderOptional).isEmpty();
	}

	@Test
	void notEnoughMoneyException() {
		// given
		Order order3 = new Order();
		order3.setUsername("잔고부족");

		// when

		//then
		assertThatThrownBy(() -> orderService.order(order3))
				.isInstanceOf(NotEnoughMoneyException.class);
		Order findOrder3 = orderRepository.findById(order3.getId()).get();
		assertThat(findOrder3.getPayStatus()).isEqualTo("대기");
	}
}