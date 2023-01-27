package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

	private final ItemRepository itemRepository;

	@GetMapping
	public String items(Model model) {
		List<Item> items = itemRepository.findAll();
		model.addAttribute("items", items);

		return "basic/items";
	}

	@GetMapping("/{itemId}")
	public String item(@PathVariable long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);
		return "basic/item";
	}

	@GetMapping("/add")
	public String addForm() {
		return "basic/addForm";
	}

//	@PostMapping("/add")
	public String addItemV1(@RequestParam String itemName,
			@RequestParam int price,
			@RequestParam Integer quantity,
			Model model) {

		Item item = new Item(itemName, price, quantity);
		itemRepository.save(item);

		model.addAttribute("item", item);

		return "basic/item";
	}

//	@PostMapping("/add")
	public String addItemV2(@ModelAttribute("item") Item item) {

		itemRepository.save(item);

		/**
		 * @ModelAttribute(이름)을 사용하면, 자동으로 Model 객체에 담겨서 전달된다.
		 * RequestMappingHandlerAdapter에서 Model 객체를 생성해준다.
		 */
		// model.addAttribute("item", item);

		return "basic/item";
	}

//	@PostMapping("/add")
	public String addItemV3(@ModelAttribute Item item) {

		itemRepository.save(item);

		/**
		 * 이름을 생략하면, 해당 클래스명의 첫글자만 소문자로 바꿔서 모델에 담아준다.
		 * Item -> item
		 */
		// model.addAttribute("item", item);

		return "basic/item";
	}

//	@PostMapping("/add")
	public String addItemV4(Item item) {

		itemRepository.save(item);

		/**
		 * @ModelAttribute를 생략하면,
		 * 임의로 만든 객체가 파라미터인 경우, 자동으로 @ModelAttribute가 적용된다.
		 */
		// model.addAttribute("item", item);

		return "basic/item";
	}

//	@PostMapping("/add")
	public String addItemV5(Item item) {

		itemRepository.save(item);

		/**
		 * PRG 적용
		 */
		// model.addAttribute("item", item);

		return "redirect:/basic/items/" + item.getId();
	}

	@PostMapping("/add")
	public String addItemV6(Item item, RedirectAttributes redirectAttributes) {

		itemRepository.save(item);
		redirectAttributes.addAttribute("itemId", item.getId());
		/**
		 * redirect에 사용되지 않은 attribute들은 쿼리파라미터로 사용된다.
		 */
		redirectAttributes.addAttribute("status", true);

		return "redirect:/basic/items/{itemId}";
	}

	@GetMapping("/{itemId}/edit")
	public String editForm(@PathVariable Long itemId, Model model) {
		Item item = itemRepository.findById(itemId);
		model.addAttribute("item", item);

		return "basic/editForm";
	}

	@PostMapping("/{itemId}/edit")
	public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
		itemRepository.update(itemId, item);

		return "redirect:/basic/items/{itemId}";
	}

	/**
	 * 테스트용 데이터 추가
	 */
	@PostConstruct
	public void init() {
		itemRepository.save(new Item("itemA", 10000, 10));
		itemRepository.save(new Item("itemB", 20000, 20));
	}
}
