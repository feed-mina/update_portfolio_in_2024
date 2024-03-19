package com.sch.service;

import com.sch.util.CamelHashMap;
import com.sch.util.CommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GalleryService {
    @Autowired
    FileService fileService;

    @Autowired
    CommonService commonService;

    public void upsert(Map<String, Object> paramMap, MultipartFile[] fileList) throws Exception {
        List spotList = (List) paramMap.get("spotList");
        Map<String, Object> parameter = new CamelHashMap();

        for (int i = 0; i < spotList.size(); i++) {
            parameter.put("spotKey", spotList.get(i));
            Map<String, Object> GalleryMap = commonService.selectMap("Gallery.selectGalleryOne", parameter);
            if (CommonUtil.isEmpty(GalleryMap.get("spotKey"))) {
                // 없으면 insert
                try {
                    fileService.fileRegist(fileList[i], GalleryMap, "file");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                GalleryMap.put("spotKey", spotList.get(i));
                commonService.insert("Gallery.insertGallery", GalleryMap);
            } else {
                // 있으면 update
                if (!CommonUtil.isEmpty(GalleryMap.get("fileSeq")) && !CommonUtil.isEmpty(fileList[i])) {
                    try {
                        fileService.fileDelete(GalleryMap);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                try {
                    fileService.fileRegist(fileList[i], GalleryMap, "file");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                commonService.update("Gallery.updateGallery", GalleryMap);

                if (!CommonUtil.isEmpty(paramMap.get("delFileSeq"))) {
                    commonService.delete("file.deleteFileMastr", GalleryMap);
                }
            }
        }
        List<Map<String, Object>> contentsList = (List<Map<String, Object>>) paramMap.get("commentList");
        usertContents(contentsList);
    }

    public void usertContents(Map<String, Object> paramMap) {
        Map<String, Object> GalleryMap = commonService.selectMap("Gallery.selectGalleryOne", paramMap);
        if (CommonUtil.isEmpty(GalleryMap.get("spotKey"))) {
            // 없으니 insert
            commonService.insert("Gallery.insertGallery", paramMap);
        } else {
            // 있으면 update
            commonService.update("Gallery.updateGallery", paramMap);
        }
    }

    public void usertContents(List<Map<String, Object>> paramMap) {
        paramMap.forEach(x -> {
            Map<String, Object> GalleryMap = commonService.selectMap("Gallery.selectGalleryOne", x);
            if (CommonUtil.isEmpty(GalleryMap.get("spotKey"))) {
                // 없으니 insert
                commonService.insert("Gallery.insertGallery", x);
            } else {
                // 있으면 update
                commonService.update("Gallery.updateGallery", x);
            }
        });
    }
}
