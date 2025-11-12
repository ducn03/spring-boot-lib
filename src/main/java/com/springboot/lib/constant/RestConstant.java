package com.springboot.lib.constant;

public interface RestConstant {
    interface PAGE {
        String PAGE_INDEX = "pageIndex";
        String PAGE_SIZE = "pageSize";
        int PAGE_SIZE_DEFAULT = 20;
        int PAGE_INDEX_DEFAULT = 0;
    }

    interface LANG {
        String EN = "en";
        String VI = "vi";
    }

    interface USER {
        String USER_ID = "userId";
    }
}
