package com.sch.enums;

import com.sch.util.CamelHashMap;

public enum PushPageType {
	// 프론트 푸쉬 페이지 이동 enum
	HOME("0_home", ""), QUEST("0_quest", "1_info"), QUESTBOOK_EXCHANGE("0_questbook", "1_exchange"),
	QUESTBOOK_MISSION("0_questbook", "1_mission"), PROFILE_BADGE("0_profile", "1_badge"),
	PROFILE_FRIEND("0_profile", "1_friend"), PETROOM("0_petroom", ""), ROGROOM("0_logroom", "1_detail"),
	NOTICE("0_notice", ""), SETTING("0_setting", "1_inquiry");

	public String index;
	public String indexDetail;

	private PushPageType(String index, String indexDetail) {
		this.index = index;
		this.indexDetail = indexDetail;
	}

	public static CamelHashMap getPushPageTypeList() {
		CamelHashMap resultMap = new CamelHashMap();
		CamelHashMap map = new CamelHashMap();

		for (PushPageType pushPageType : PushPageType.values()) {
			map.put("index", pushPageType.index);
			map.put("indexDetail", pushPageType.indexDetail);

			resultMap.put(pushPageType.name(), map);
		}
		return resultMap;
	}
}
