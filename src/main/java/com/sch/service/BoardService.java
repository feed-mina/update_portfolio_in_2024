package com.sch.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sch.dao.CommonDao;
import com.sch.util.CommonUtil;

@Service
public class BoardService {

	@Autowired
	CommonService commonService;

	@Autowired
	FileService fileService;
 
	@Autowired
	private CommonDao commonDao;

	public void upsertBbs(Map<String, Object> paramMap, MultipartFile[] bbsFiles) throws Exception {

		if ("COMMUNITY".equals(paramMap.get("bbsCode"))) {
			if (CommonUtil.isEmpty(paramMap.get("bbsSeq"))) {
				fileService.fileRegist(bbsFiles, paramMap, "file");
				commonService.insert("commonBbs.insertBbs", paramMap);

				// 공지사항 insert시 알림 보내기 Arrays.asList("P")
				// 	alarmService.insertBbsAlarm((List<String>) paramMap.get("alarmFormList"), // Arrays.asList("A", "P",
																							// "M"),
				// 	(String) paramMap.get("bbsSj"), (String) paramMap.get("bbsCn"));
			} else {

				/* 있으면 update */
				if (!CommonUtil.isEmpty(paramMap.get("fileSeq"))) {
					commonService.delete("file.deleteFileDetail", paramMap);
				}
				;

				List<Map<String, Object>> filesList = (List<Map<String, Object>>) paramMap.get("filesList");

				if (!CommonUtil.isEmpty(filesList)) {
					int[] i = { 0 };
					filesList.forEach(x -> {
						if (!CommonUtil.isEmpty(x.get("fileSeq"))) {
							commonDao.insert("file.insertFileDetail", x);
						} else {
							x.put("fileSeq", paramMap.get("fileSeq"));
							MultipartFile updtFile = bbsFiles[i[0]];
							try {
								fileService.fileRegistOne(updtFile, x, "file");
								i[0]++;
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
				commonService.update("commonBbs.updateBbsOne", paramMap);
			}
			return;
		} else if ("QNA".equals(paramMap.get("bbsCode"))) {
			/* 카테고리 변경 시 업데이트 */
			if (!CommonUtil.isEmpty(paramMap.get("cnBbsSe"))) {
				Map<String, Object> qnaMap = new HashMap<>();
				/* 질문 bbsSeq */
				qnaMap.put("bbsSeq", paramMap.get("q_bbsSeq"));
				/* 변경할 bbsCode */
				qnaMap.put("bbsSe", paramMap.get("cnBbsSe"));
				qnaMap.put("useAt", paramMap.get("useAt"));
				commonService.update("commonBbs.updateBbsOne", qnaMap);
			}

			/* cnt = 0 이면 insert, 1이상이면 update */
			if ((int) paramMap.get("cnt") == 0) {
				commonService.insert("commonBbs.insertBbs", paramMap);

				// 첫 답변 insert시 알림 보내기 Arrays.asList("P")
				// alarmService.insertQnaAlarm((List<String>) paramMap.get("alarmFormList"),
				// 		(String) paramMap.get("qstRegistId"), (String) paramMap.get("registSj"),
				// 			(String) paramMap.get("bbsCn"));

			} else {
				commonService.update("commonBbs.updateBbsOne", paramMap);
			}
			return;
		} else if ("FAQ".equals(paramMap.get("bbsCode"))) {

			if (CommonUtil.isEmpty(paramMap.get("bbsSeq"))) {
				fileService.fileRegist(bbsFiles, paramMap, "file");
				/* bbsSeq값이 없으면 save */
				commonService.insert("commonBbs.insertBbs", paramMap);
			} else {
				/* bbsSeq값이 있으면 update */

				if (!CommonUtil.isEmpty(paramMap.get("delFileSeq"))) {
					fileService.fileDelete(paramMap);
					paramMap.put("fileSeq", "");
					commonService.update("commonBbs.updateBbsOne", paramMap);
					commonService.delete("file.deleteFileMastr", paramMap);
				} else {
					if (!CommonUtil.isEmpty(paramMap.get("fileSeq"))) {
						commonService.delete("file.deleteFileDetail", paramMap);
					}
					;
					List<Map<String, Object>> filesList = (List<Map<String, Object>>) paramMap.get("filesList");

					if (!CommonUtil.isEmpty(filesList)) {
						// 신규 마스터 채번
						if (CommonUtil.isEmpty(paramMap.get("fileSeq"))) {
							commonDao.insert("file.insertFileMastr", paramMap);
						}
						int[] i = { 0 };
						filesList.forEach(x -> {
							if (!CommonUtil.isEmpty(x.get("fileSeq"))) {
								commonDao.insert("file.insertFileDetail", x);
							} else {
								x.put("fileSeq", paramMap.get("fileSeq"));
								MultipartFile updtFile = bbsFiles[i[0]];
								try {
									fileService.fileRegistOne(updtFile, x, "file");
									i[0]++;
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
					commonService.update("commonBbs.updateBbsOne", paramMap);
				}
			}
			return;
		}

	}

	public void deleteBbsOne(Map<String, Object> paramMap) throws Exception {
		commonService.delete("commonBbs.deleteBbsOne", paramMap);

		if (!CommonUtil.isEmpty(paramMap.get("fileSeq"))) {
			commonService.delete("file.deleteFileDetail", paramMap);
			commonService.delete("file.deleteFileMastr", paramMap);
		}
		return;
	}

	public void upsertBanner(Map<String, Object> paramMap, MultipartFile[] bbsFiles) throws Exception {
		fileService.bannerFileRegist(bbsFiles, paramMap);
		return;
	}
}
