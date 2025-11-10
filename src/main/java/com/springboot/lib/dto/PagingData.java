package com.springboot.lib.dto;

import com.springboot.lib.constant.RestConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PagingData {
    /**
     * Trang hiện tại trừ đi 1
     */
    private int pageIndex = RestConstant.PAGE.PAGE_INDEX_DEFAULT;
    /**
     * Số phần tử của 1 trang
     */
    private int pageSize = RestConstant.PAGE.PAGE_SIZE_DEFAULT;


    /**
     * Trang hiện tại <br>
     */
    private int currentPage;
    /**
     *
     */
    private int currentElements;
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

    public <T> void update(Page<T> items) {
        this.setCurrentPage(items.getNumber());
        this.setTotalPages(items.getTotalPages());
        this.setTotalElements(items.getTotalElements());
        this.setCurrentElements(items.getNumberOfElements());
        this.setFirstPage(items.isFirst());
        this.setLastPage(items.isLast());
        this.setPageSize(items.getSize());
    }
}
