package hello.upload.controller;

import hello.upload.domain.Item;
import hello.upload.domain.ItemRepository;
import hello.upload.domain.UploadFile;
import hello.upload.file.FIleStore;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

	private final ItemRepository itemRepository;
	private final FIleStore fIleStore;

	@GetMapping("/items/new")
	public String newItem(@ModelAttribute ItemForm itemForm) {
		return "item-form";
	}

	@PostMapping("/items/new")
	public String saveItem(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes)
			throws IOException {

		// 파일은 스토리지에 저장
		UploadFile uploadFile = fIleStore.storeFile(form.getAttachFile());
		List<UploadFile> storeImageFiles = fIleStore.storeFiles(form.getImageFiles());

		// 파일 정보(경로)는 데이터 베이스에 저장
		Item item = new Item();
		item.setItemName(form.getItemName());
		item.setAttachFile(uploadFile);
		item.setImageFiles(storeImageFiles);
		itemRepository.save(item);

		redirectAttributes.addAttribute("itemId", item.getId());

		return "redirect:/items/{itemId}";
	}

	@GetMapping("/items/{id}")
	public String items(@PathVariable Long id, Model model) {
		Item item = itemRepository.findById(id);
		model.addAttribute("item", item);
		return "item-view";
	}

	@ResponseBody
	@GetMapping("/images/{filename}")
	public Resource downloadImage(@PathVariable String filename) throws MalformedURLException {
		// "file:..."
		return new UrlResource("file:" + fIleStore.getFullPath(filename));
	}

	@GetMapping("/attach/{itemId}")
	public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId)
			throws MalformedURLException, UnsupportedEncodingException {
		Item item = itemRepository.findById(itemId);
		String storeFileName = item.getAttachFile().getStoreFileName();
		String uploadFileName = item.getAttachFile().getUploadFileName();

		UrlResource resource = new UrlResource("file:" + fIleStore.getFullPath(storeFileName));

		log.info("uploadFileName={}", uploadFileName);

		String encodedUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);
		String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\"";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
				.body(resource);
	}
}
