package com.lxl.essence.paging.custom;


public class PageQueryRequest {

    private int pageNo;
    private int pageSize;
    private String query;

    public PageQueryRequest(int pageNo, int pageSize, String query) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.query = query;
    }

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

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


}
