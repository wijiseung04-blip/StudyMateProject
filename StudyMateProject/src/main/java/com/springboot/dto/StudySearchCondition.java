package com.springboot.dto;

public class StudySearchCondition {

    private String keyword = "";   // 검색어
    private String type = "all";   // 검색 대상: all(제목+설명), title, description
    private String size = "all";   // 모집 인원: all, small(2~4), medium(5~10), large(11~50)

    // 검색 중인지 (결과 없음 메시지 구분용)
    public boolean isSearching() {
        return !keyword.isBlank() || !"all".equals(size);
    }

    public int getMinMembers() {
        return switch (size) {
            case "medium" -> 5;
            case "large" -> 11;
            default -> 2;
        };
    }

    public int getMaxMembers() {
        return switch (size) {
            case "small" -> 4;
            case "medium" -> 10;
            default -> 50;
        };
    }

    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
}