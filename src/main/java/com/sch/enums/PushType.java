package com.sch.enums;

public enum PushType {
	// S = 서비스알림 , A = 활동알림
//	NEW_QUEST("NEW_QUEST", 10, "S"), // 10시
//	CONTINEW_QUEST("CONTINEW_QUEST", 13, "S"), // 13시
//	CHANGE_MONEY("CHANGE_MONEY", -1, "S"), // 09시
//	NEAR_MISSION("NEAR_MISSION", -1, "S"), // 즉시
//	HURRYUP_REWORD("HURRYUP_REWORD", 14, "S"), // 14시
//	JOIN_MYROOM("JOIN_MYROOM", -1, "S"), // 즉시
//	GET_BADGE("GET_BADGE", 11, "S"), // 11시
//	PET("PET", 12, "S"), // 12시
//	LIKE_LOG("LIKE_LOG", -1, "A"), // 즉시
//	COMMENT("COMMENT", -1, "A"), // 즉시
//	FOLLOW("FOLLOW", -1, "A"), // 즉시
//	NEW_NOTIFICATION("NEW_NOTIFICATION", 9, "S"), // 9시
//	QNA("QNA", -1, "S"); // 즉시

	NEW_QUEST("NEW_QUEST", "퀘스트"), SERVICE("SERVICE", "서비스"), LIKE("LIKE", "좋아요"), ANSWER("ANSWER", "답글"),
	FOLLOW("FOLLOW", "팔로우"), BADGE("BADGE", "뱃지");

	public String enumStr;
	public String enumKorStr;

	private PushType(String enumStr, String enumKorStr) {
		this.enumStr = enumStr;
		this.enumKorStr = enumKorStr;
	}
}
