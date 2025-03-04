package com.springboot.lib.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class Pagination<T> {
    /**
     * Trang hiện tại <br>
     */
    private int currentPage;
    /**
     * Tổng trang
     */
    private int totalPages;
    /**
     * Tổng phần tử trong trang
     */
    private long totalElements;
    /**
     * Trang đầu
     */
    private boolean isFirstPage;
    /**
     * Trang cuối
     */
    private boolean isLastPage;
    /**
     * Số phần tử của 1 trang
     */
    private int pageSize;

    public Pagination(Page<T> page) {
        this.currentPage = page.getNumber();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.isFirstPage = page.isFirst();
        this.isFirstPage = page.isLast();
        this.pageSize = page.getSize();
    }
}
