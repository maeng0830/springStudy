package hello.itemservice.repository.mybatis;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 마이바티스 스프링 연동 모듈이 @Mapper를 조회한다.
 * 마이바티스 스프링 연동 모듈은 동적 프록시 객체 생성을 통해 @Mapper가 적용된 인터페이스의 구현체를 생성한다.
 * 마이바티스 스프링 연동 모듈은 스프링 컨테이너에 생성된 동적 프록시 객체를 빈으로 등록한다.
 * @Mapper 구현체는 마이바티스에서 발생한 스프링 데이터 예외로 변환까지 해준다. JDBC 템플릿이 그랬던것 처럼..
 */
@Mapper
public interface ItemMapper {

	// 메소드명은 xml 쿼리문의 id 값으로 사용된다.
	void save(Item item);

	// 파라미터가 두개 이상 넘어가는 경우, @Param 사용해야한다.
	void update(@Param("id") Long id, @Param("updateParam") ItemUpdateDto updateParam);

	Optional<Item> findById(Long id);

	List<Item> findAll(ItemSearchCond itemSearch);
}
