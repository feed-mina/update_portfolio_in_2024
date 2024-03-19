package com.sch.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sch.service.CommonService;
import com.sch.util.CommonUtil;

@RestController
@RequestMapping("/download")
public class DownloadController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonService commonService;

	@PostMapping(value = "/fileDownload.api")
	public void fileDowndload(@RequestBody Map<String, Object> paramMap, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		List<String> requiredList = Arrays.asList("fileSeq", "fileDetailSn");
		if (!CommonUtil.validation(paramMap, requiredList)) {
			return;
		}

		Map<String, Object> fileMap = commonService.selectMap("file.selectFileDetail", paramMap);

		String fileCours = (String) fileMap.get("fileCours");
		File uFile = new File(fileCours);

		int fSize = (int) uFile.length();

		if (fSize > 0) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(uFile));
			String mimetype = "application/octet-stream; charset=utf-8";
			response.setBufferSize(fSize);
			response.setContentType(mimetype);
			// 브라우저 별 한글 인코딩
			String orginlFileNm = (String) fileMap.get("orignlFileNm");
			String name;
			String header = request.getHeader("User-Agent");
			if (header.contains("Edge")) {
				name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment;filename=" + name + "");
			} else if (header.contains("MSIE") || header.contains("Trident")) { // IE 11버전부터 Trident로 변경되었기때문에 추가해준다.
				name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment;filename=" + name + "");
			} else if (header.contains("Chrome")) {
				// name = new String(orginlFileNm.getBytes("UTF-8"), "ISO-8859-1");
				name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment; filename=" + name + "");
			} else if (header.contains("Opera")) {
				// name = new String(orginlFileNm.getBytes("UTF-8"), "ISO-8859-1");
				name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment; filename=" + name + "");
			} else if (header.contains("Firefox")) {
				// name = new String(orginlFileNm.getBytes("UTF-8"), "ISO-8859-1");
				name = URLEncoder.encode(orginlFileNm, "UTF-8").replaceAll("\\+", "%20");
				response.setHeader("Content-Disposition", "attachment; filename=" + name + "");
			}
			response.setContentLength(fSize);

			FileCopyUtils.copy(in, response.getOutputStream());
			in.close();
			response.getOutputStream().flush();
			response.getOutputStream().close();
		}
	}
}