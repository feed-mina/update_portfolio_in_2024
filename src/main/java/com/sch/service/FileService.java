package com.sch.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sch.dao.CommonDao;
import com.sch.util.CamelHashMap;

@Service("fileService")
public class FileService {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private CommonDao commonDao;

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Value("${file.banner-dir}")
	private String bannerDir;

	public boolean fileRegist(MultipartFile[] uploadfile, Map<String, Object> paramMap) throws Exception {
		return fileRegist(uploadfile, paramMap, "");
	}

	public boolean filesRegist(MultipartFile[] uploadfile, Map<String, Object> paramMap, String dirSe) throws Exception {
		int count = 1;
		System.out.println("uploadfile : "+ uploadfile);
		System.out.println("paramMap : "+ paramMap);
		System.out.println("dirSe : "+ dirSe);
		if (uploadfile == null || uploadfile.length == 0) {
			return false;
		}

		// dir 없으면 생성
		File fileDir = new File(uploadDir + File.separator + dirSe);

		System.out.println("fileDir : "+ fileDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		// 신규 마스터 채번
		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			commonDao.insert("file.insertFileMastr", paramMap);
		}
		int fileNum = 0;
		// 파일 저장
		for (MultipartFile mpFile : uploadfile) {
			paramMap.put("orignlFileNm", mpFile.getOriginalFilename());
			paramMap.put("fileMg", mpFile.getSize());
			String uuid = UUID.randomUUID().toString();

			Path path = Paths.get(fileDir + File.separator + uuid).toAbsolutePath();
			mpFile.transferTo(path.toFile());

			String rootPath = System.getProperty("user.dir");

			paramMap.put("fileCours", path.toFile().getAbsolutePath());
			if (paramMap.get("playTime") != null) {
				ArrayList<String> playTimeArr = (ArrayList<String>) paramMap.get("playTime");
				String playTimeStr = playTimeArr.get(fileNum++);
				paramMap.put("playTimeStr", playTimeStr);
			}
			System.out.println("mpFile : " + mpFile);
			System.out.println("paramMap : " + paramMap);
			commonDao.insert("file.insertFileDetail", paramMap);
			paramMap.put("fileSeq", paramMap.get("fileSeq"));
		//	paramMap.put("fileDetailSn" + count, count);
			paramMap.put("orignlFileNm" + count, mpFile.getOriginalFilename());
			paramMap.put("fileMg" + count, mpFile.getSize());
			paramMap.put("fileCours" + count, path.toFile().getAbsolutePath());

			System.out.println("paramMap : "+ paramMap);
			count++;
		}

		return true;
	}

	public boolean fileRegist(MultipartFile[] uploadfile, Map<String, Object> paramMap, String dirSe) throws Exception {

		if (uploadfile == null || uploadfile.length == 0) {
			return false;
		}

		// dir 없으면 생성
		File fileDir = new File(uploadDir + File.separator + dirSe);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		// 신규 마스터 채번
		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			commonDao.insert("file.insertFileMastr", paramMap);
		}
		int fileNum = 0;
		// 파일 저장
		for (MultipartFile mpFile : uploadfile) {
			paramMap.put("orignlFileNm", mpFile.getOriginalFilename());
			paramMap.put("fileMg", mpFile.getSize());
			String uuid = UUID.randomUUID().toString();

			Path path = Paths.get(fileDir + File.separator + uuid).toAbsolutePath();
			mpFile.transferTo(path.toFile());

			String rootPath = System.getProperty("user.dir");

			paramMap.put("fileCours", path.toFile().getAbsolutePath());
			if (paramMap.get("playTime") != null) {
				ArrayList<String> playTimeArr = (ArrayList<String>) paramMap.get("playTime");
				String playTimeStr = playTimeArr.get(fileNum++);
				paramMap.put("playTimeStr", playTimeStr);
			}
			commonDao.insert("file.insertFileDetail", paramMap);
		}

		return true;
	}

	/**
	 * 이미지 낱개로 저장 - 공자루에서 사용
	 * @param uploadfile
	 * @param paramMap
	 * @param dirSe
	 * @return
	 * @throws Exception
	 */
	public boolean fileRegist(MultipartFile uploadfile, Map<String, Object> paramMap, String dirSe) throws Exception {

		if (uploadfile == null) {
			return false;
		}

		// dir 없으면 생성
		File fileDir = new File(uploadDir + File.separator + dirSe);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		// 신규 마스터 채번
		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			commonDao.insert("file.insertFileMastr", paramMap);
		}
		int fileNum = 0;
		// 파일 저장
		paramMap.put("orignlFileNm", uploadfile.getOriginalFilename());
		paramMap.put("fileMg", uploadfile.getSize());
		String uuid = UUID.randomUUID().toString();

		Path path = Paths.get(fileDir + File.separator + uuid).toAbsolutePath();
		uploadfile.transferTo(path.toFile());

		String rootPath = System.getProperty("user.dir");

		paramMap.put("fileCours", path.toFile().getAbsolutePath());
		if (paramMap.get("playTime") != null) {
			ArrayList<String> playTimeArr = (ArrayList<String>) paramMap.get("playTime");
			String playTimeStr = playTimeArr.get(fileNum++);
			paramMap.put("playTimeStr", playTimeStr);
		}
		commonDao.insert("file.insertFileDetail", paramMap);

		return true;
	}

	public boolean fileRegistOne(MultipartFile bbsFiles, Map<String, Object> x, String dirSe) throws Exception {
		if (bbsFiles == null) {
			return false;
		}

		// dir 없으면 생성
		File fileDir = new File(uploadDir + File.separator + dirSe);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}

		int fileNum = 0;
		// 파일 저장
		x.put("orignlFileNm", bbsFiles.getOriginalFilename());
		x.put("fileMg", bbsFiles.getSize());
		String uuid = UUID.randomUUID().toString();

		Path path = Paths.get(fileDir + File.separator + uuid).toAbsolutePath();
		bbsFiles.transferTo(path.toFile());

		String rootPath = System.getProperty("user.dir");

		x.put("fileCours", path.toFile().getAbsolutePath());
		if (x.get("playTime") != null) {
			ArrayList<String> playTimeArr = (ArrayList<String>) x.get("playTime");
			String playTimeStr = playTimeArr.get(fileNum++);
			x.put("playTimeStr", playTimeStr);
		}
		commonDao.insert("file.insertFileDetail", x);
		return true;
	}

	public boolean fileDelete(Map<String, Object> paramMap) throws Exception {

		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			return false;
		}

		Map<String, Object> fileMap = commonDao.selectMap("file.selectFileDetail", paramMap);

		if (ObjectUtils.isEmpty(fileMap)) {
			return false;
		}

		// 물리파일도 삭제 - 삭제 실패해도 에러 아님...
		try {
			File file = new File((String) fileMap.get("fileCours"));
			if (file.exists()) {
				if (file.delete()) {
				}
			}
		} catch (Exception e) {
			log.debug("파일 삭제 실패");
		}
		// detail 삭제
		commonDao.delete("file.deleteFileDetail", paramMap);

		return true;
	}

	public boolean fileDeleteAll(Map<String, Object> paramMap) throws Exception {

		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			return false;
		}
		Map<String, Object> map = new CamelHashMap();
		map.put("fileSeq", paramMap.get("fileSeq"));

		List<Map<String, Object>> list = commonDao.selectList("file.selectFileDetail", map);

		for (Map<String, Object> file : list) {
			this.fileDelete(file);
		}
		map.put("delFileSeq", paramMap.get("fileSeq"));

		// commonDao.delete("file.deleteFileMastr", map);
		return true;
	}

	public List<Map<String, Object>> fileList(Map<String, Object> paramMap) throws Exception {

		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			return null;
		}

		// get fileList
		return commonDao.selectList("file.selectFileDetail", paramMap);
	}

	// file이 단건인 경우
	public Map<String, Object> fileMap(Map<String, Object> paramMap) throws Exception {

		if (ObjectUtils.isEmpty(paramMap.get("fileSeq"))) {
			return null;
		}

		// get fileMap
		return commonDao.selectMap("file.selectFileDetail", paramMap);
	}

	public boolean bannerFileRegist(MultipartFile[] uploadfile, Map<String, Object> paramMap) throws Exception {
		if (uploadfile == null || uploadfile.length == 0) {
			if (!ObjectUtils.isEmpty(paramMap)) {
				List<Map<String, Object>> fileList = (List<Map<String, Object>>) paramMap.get("fileList");
				File orgFile = new File((String) fileList.get(0).get("fileCours"));
				String bannerNm = (String) paramMap.get("bannerNm");
				File copyFile = new File(bannerDir + File.separator + bannerNm);
				try {
					FileUtils.copyFile(orgFile, copyFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			} else {
				return false;
			}
		}

		// dir 없으면 생성
		File fileDir = new File(bannerDir);

		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		// 파일 저장
		for (MultipartFile mpFile : uploadfile) {

			String bannerNm = (String) paramMap.get("bannerNm");

			Path path = Paths.get(fileDir + File.separator + bannerNm).toAbsolutePath();
			mpFile.transferTo(path.toFile());

		}

		return true;
	}

	@Cacheable(value = "fileMapCache", key = "#fileSeq")
	public Map<String, Object> fileMapCache(Map<String, Object> paramMap, String fileSeq) throws Exception {
		return this.fileMap(paramMap);
	}

	@CacheEvict(value = "fileMapCache", key = "#fileSeq")
	public void fileMapRefresh(String fileSeq) throws Exception {
		log.info("fileMapCache 캐시해제 >> " + fileSeq);
	}
}
