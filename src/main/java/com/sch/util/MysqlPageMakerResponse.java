package com.sch.util; 

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/** 반환 처리시 */
@Setter
@Getter
@AllArgsConstructor
@ToString
@Builder
public class MysqlPageMakerResponse {
    
   private int page;
    
   private int perPageNum;
  
   private int pageStart;
     
   private int totalCount; //전체 개수
    
   private int startPage; // 시작 페이지
    
   private int endPage;   // 끝페이지
    
   private boolean prev;  // 이전 여부
    
   private boolean next;  // 다음 여부
    
   private int displayPageNum;
    
   private int tempEndPage; //마지막 페이지     
    
   private String searchType;
    
   private String keyword;
     
       
}
