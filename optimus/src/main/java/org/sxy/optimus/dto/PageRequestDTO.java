package org.sxy.optimus.dto;

import java.util.List;

public class PageRequestDTO {
    private int pageNo;
    private int pageSize;
    private List<String> ascSortBy;
    private List<String> descSortBy;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getAscSortBy() {
        return ascSortBy;
    }

    public void setAscSortBy(List<String> ascSortBy) {
        this.ascSortBy = ascSortBy;
    }

    public List<String> getDescSortBy() {
        return descSortBy;
    }

    public void setDescSortBy(List<String> descSortBy) {
        this.descSortBy = descSortBy;
    }
}
