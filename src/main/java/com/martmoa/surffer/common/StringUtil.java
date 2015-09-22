package com.martmoa.surffer.common;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.ko.utils.MorphUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StringUtil {

	/** 복자음/복모음 변환 맵			*/ private final static Map<String, String[]> trans = new HashMap<String, String[]>();
	/** 오타떄문에 영어한글 변환 맵	*/ private final static Map<Character, Character> languageConvertMap = new HashMap<Character, Character>();
	
	final static char[] koreaCharArray = {
		'ㄱ', 'ㄴ', 'ㄷ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅅ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ',
		'ㄲ', 'ㄸ', 'ㅃ', 'ㅆ', 'ㅉ', 'ㅏ', 'ㅑ', 'ㅓ', 'ㅕ', 'ㅗ', 'ㅛ', 'ㅜ', 'ㅠ', 'ㅡ', 
		'ㅣ', 'ㅐ', 'ㅒ', 'ㅔ', 'ㅖ'
	};
	
	final static char[] englishCharArray = {
		'r', 's', 'e', 'f', 'a', 'q', 't', 'd', 'w', 'c', 'z', 'x', 'v', 'g',
		'R', 'E', 'Q', 'T', 'W', 'k', 'i', 'j', 'u', 'h', 'y', 'n', 'b', 'm', 
		'l', 'o', 'O', 'p', 'P'
	};
	 
	private final static String EMPTY_STRING = "";
	
	static{
		
		for(int i = 0 ; i < koreaCharArray.length ; i++ ){
			languageConvertMap.put(koreaCharArray[i], englishCharArray[i]);
			languageConvertMap.put(englishCharArray[i], koreaCharArray[i]);
		}
		
		//		복자음 
		trans.put("ㄳ", new String[]{"ㄱ", "ㅅ"});
		trans.put("ㄵ", new String[]{"ㄴ", "ㅈ"});
		trans.put("ㄶ", new String[]{"ㄴ", "ㅎ"});
		trans.put("ㄺ", new String[]{"ㄹ", "ㄱ"});
		trans.put("ㄻ", new String[]{"ㄹ", "ㅁ"});
		trans.put("ㄼ", new String[]{"ㄹ", "ㅂ"});
		trans.put("ㄽ", new String[]{"ㄹ", "ㅅ"});
		trans.put("ㄾ", new String[]{"ㄹ", "ㅌ"});
		trans.put("ㄿ", new String[]{"ㄹ", "ㅍ"});
		trans.put("ㅀ", new String[]{"ㄹ", "ㅎ"});
		trans.put("ㅄ", new String[]{"ㅂ", "ㅅ"});
		
		//		복모음
		trans.put("ㅘ", new String[]{"ㅗ", "ㅏ"});
		trans.put("ㅙ", new String[]{"ㅗ", "ㅐ"});
		trans.put("ㅚ", new String[]{"ㅗ", "ㅣ"});
		trans.put("ㅝ", new String[]{"ㅜ", "ㅓ"});
		trans.put("ㅞ", new String[]{"ㅜ", "ㅔ"});
		trans.put("ㅟ", new String[]{"ㅜ", "ㅣ"});
		trans.put("ㅢ", new String[]{"ㅡ", "ㅣ"});
		
	}
	
	/**
	 * 입력된 키워드를 초.중.종 성 분리된 키워드를 추출 한다.
	 * @param	keyword
	 * @return	String
	 */
	public String convertKeyByKeyword(String keyword){
		
		List<String> result = new ArrayList<String>();
		int keywordLength = keyword.length();
		for( int i = 0 ; i < keywordLength ; i++ ){
			
			char stem = keyword.charAt(i);								// 한 글자 추출
			char[] chars = MorphUtil.decompose(stem);					// 글자 초.중.종 성 분리
			
			for(char ch : chars){
				
				String charToStringValue = String.valueOf(ch);		
				String[] charArray = trans.get(charToStringValue);		// 초,중,종 성이 분리된 것 가지고 다시 키워드 입력으로 분리
				
				if(charArray == null){
					result.add(charToStringValue);
				} else {
					for(String str: charArray){
						result.add(str);
					}
				}
			}
		}
		
		return StringUtils.join(result, EMPTY_STRING);
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public String convertKeyByLanguage(String key){
		
		String returnString = null;
		
		try {
			StringBuilder returnOutput = new StringBuilder();
			
			for(int i = 0 ; i < key.length() ; i++){
				char oneChar = key.charAt(i);
				char returnChar = languageConvertMap.get(oneChar);
				returnOutput.append(returnChar);
			}
			returnString = returnOutput.toString();
		} catch (Exception e) {
			returnString = "";
		}
		
		return returnString;
		
	}
}
