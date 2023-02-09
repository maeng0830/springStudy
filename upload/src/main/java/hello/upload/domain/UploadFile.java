package hello.upload.domain;

import lombok.Data;

@Data
public class UploadFile {

	private String uploadFileName;
	private String storeFileName; // 파일 업로드 명이 같을 수 있으므로

	public UploadFile(String uploadFileName, String storeFileName) {
		this.uploadFileName = uploadFileName;
		this.storeFileName = storeFileName;
	}
}
