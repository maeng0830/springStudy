package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

/**
 * NamedParameterJdbcTemplate
 *
 * SqlParameterSource - 이름 지정 파라미터
 * - BeanPropertySqlParameterSource
 * - MapSqlParameterSource
 *
 * Map - 이름 지정 파라미터
 *
 * BeanPropertyRowMapper - rs -> 객체
 */
@Slf4j
public class JdbcTemplateItemRepositoryV2 implements ItemRepository {

	// NameParameterJdbcTemplate 사용
	private final NamedParameterJdbcTemplate template;

	public JdbcTemplateItemRepositoryV2(DataSource dataSource) {
		this.template = new NamedParameterJdbcTemplate(dataSource);
	}

	// 저장
	@Override
	public Item save(Item item) {
		// NameParameterJdbcTemplate에서 사용하는 sql
		String sql = "insert into item(item_name, price, quantity) "
				+ "values (:itemName, :price, :quantity)";

		// SqlParameterSource 생성 방법 1
		SqlParameterSource param = new BeanPropertySqlParameterSource(item);

		// DB에서 생성된 Key를 사용하기 위함
		KeyHolder keyHolder = new GeneratedKeyHolder();

		template.update(sql, param, keyHolder);

		long key = keyHolder.getKey().longValue();
		item.setId(key);

		return item;
	}

	// 수정
	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		String sql = "update item "
				+ "set item_name=:itemName, price=:price, quantity=:quantity "
				+ "where id=:id";

		// SqlParameterSource 생성 방법 2
		SqlParameterSource param = new MapSqlParameterSource()
				.addValue("itemName", updateParam.getItemName())
				.addValue("price", updateParam.getPrice())
				.addValue("quantity", updateParam.getQuantity())
				.addValue("id", itemId);

		template.update(sql, param);
	}

	// 조회(단일)
	@Override
	public Optional<Item> findById(Long id) {
		String sql = "select id, item_name, price, quantity from item where id=:id";

		try {
			// Map도 이름 지정 파라미터로 사용 가능하다. 방법 3
			Map<String, Long> param = Map.of("id", id);
			// queryForObject 하나의 row만 가져올 때
			Item item = template.queryForObject(sql, param, itemRowMapper());
			return Optional.of(item);
		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	// 조회(다수)
	@Override
	public List<Item> findAll(ItemSearchCond cond) {
		String itemName = cond.getItemName();
		Integer maxPrice = cond.getMaxPrice();

		SqlParameterSource param = new BeanPropertySqlParameterSource(
				cond);

		String sql = "select id, item_name, price, quantity from item";

		// 동적 쿼리 시작!
		if (StringUtils.hasText(itemName) || maxPrice != null) {
			sql += " where";
		}

		boolean andFlag = false;

		if (StringUtils.hasText(itemName)) {
			sql += " item_name like concat('%', :itemName,'%')";
			andFlag = true;
		}

		if (maxPrice != null) {
			if (andFlag) {
				sql += " and";
			}
			sql += " price <= :maxPrice";
		}
		// 동적 쿼리 끝!

		log.info("sql={}", sql);
		// query는 여러개의 row를 가져 올 때
		return template.query(sql, param, itemRowMapper());
	}

	// rs -> Item 객체
	private RowMapper<Item> itemRowMapper() {
		// rs -> Item 객체(camel 변환 지원)
		return BeanPropertyRowMapper.newInstance(Item.class);
	}
}
