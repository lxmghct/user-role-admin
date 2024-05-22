package com.example.user.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class DataUtils {

    /**
     * 检查时间字符串是否合法 yyyy-MM-dd HH:mm:ss
     *
     * @param time 时间字符串
     */
    public static boolean isTimeFormatInvalid(String time) {
        if (time == null) {
            return true;
        }
        return !time.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
    }

    // 排序方法
    public static final String ORDER_METHODS = "asc,desc";

    /**
     * 检查空字符串
     *
     * @param str 字符串
     */
    public static boolean checkEmptyString(String str) {
        return str == null || str.trim().length() == 0;
    }


    /**
     * 获取分页对象，默认采用1开始的页码，所以页码需要减1
     *
     * @param page     页码
     * @param pageSize 每页大小
     */
    public static Pageable getPageable(int page, int pageSize) {
        return PageRequest.of(page - 1, pageSize);
    }

    /**
     * 获取Page对象
     *
     * @param data 数据
     * @param total 总数
     * @param page 页码
     * @param pageSize 每页大小
     */
    public static <T> Page<T> getPage(List<T> data, long total, int page, int pageSize) {
        return new PageImpl<>(data, DataUtils.getPageable(page, pageSize), total);
    }

}
