package hello.itemservice.repository.jdbctemplate;

import hello.itemservice.domain.Item;
import hello.itemservice.repository.ItemRepository;
import hello.itemservice.repository.ItemSearchCond;
import hello.itemservice.repository.ItemUpdateDto;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.util.StringUtils;

/**
 * JdbcTemplate
 */
@Slf4j
public class JdbcTemplateItemRepositoryV1 implements ItemRepository {

	private final JdbcTemplate template;

	public JdbcTemplateItemRepositoryV1(DataSource dataSource) {
		this.template = new JdbcTemplate(dataSource);
	}

	// 저장
	@Override
	public Item save(Item item) {
		String sql = "insert into item(item_name, price, quantity) values (?, ?, ?)";

		// DB에서 생성된 Key를 사용하기 위함
		KeyHolder keyHolder = new GeneratedKeyHolder();

		// 데이터 변경(저장)
		template.update(connection -> {
			//자동 증가 키
			PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
			// 밸류 바인딩
			ps.setString(1, item.getItemName());
			ps.setInt(2, item.getPrice());
			ps.setInt(3, item.getQuantity());

			return ps;
		}, keyHolder);

		long key = keyHolder.getKey().longValue();
		item.setId(key);

		return item;
	}

	// 수정
	@Override
	public void update(Long itemId, ItemUpdateDto updateParam) {
		String sql = "update item set item_name=?, price=?, quantity=? where id=?";

		// 데이터 변경(수정)
		template.update(sql, updateParam.getItemName(), updateParam.getPrice(),
				updateParam.getQuantity(), itemId);
	}

	// 조회(단일)
	@Override
	public Optional<Item> findById(Long id) {
		String sql = "select id, item_name, price, quantity from item where id=?";

		try {
			// queryForObject 하나의 row만 가져올 때
			Item item = template.queryForObject(sql, itemRowMapper(), id);
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

		String sql = "select id, item_name, price, quantity from item";

		// 동적 쿼리 시작!
		if (StringUtils.hasText(itemName) || maxPrice != null) {
			sql += " where";
		}

		boolean andFlag = false;
		List<Object> param = new ArrayList<>();

		if (StringUtils.hasText(itemName)) {
			sql += " item_name like concat('%',?,'%')";
			param.add(itemName);
			andFlag = true;
		}

		if (maxPrice != null) {
			if (andFlag) {
				sql += " and";
			}
			sql += " price <= ?";
			param.add(maxPrice);
		}
		// 동적 쿼리 끝!

		log.info("sql={}", sql);
		// query는 여러개의 row를 가져 올 때
		return template.query(sql, itemRowMapper(), param.toArray());
	}

	// rs -> Item 객체
	private RowMapper<Item> itemRowMapper() {
		return ((rs, rowNum) -> {
			Item item = new Item();
			item.setId(rs.getLong("id"));
			item.setItemName(rs.getString("item_name"));
			item.setPrice(rs.getInt("price"));
			item.setQuantity(rs.getInt("quantity"));
			return item;
		});
	}
}
