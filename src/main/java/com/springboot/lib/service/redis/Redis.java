package com.springboot.lib.service.redis;

import java.util.Set;

/**
 * Redis cache hoạt động ở mức độ server và truy cập thông qua network. <br>
 * 📌 **Ưu điểm:** <br>
 * ✅ Hiệu suất cao và nhanh hơn so với truy vấn database, nhưng vẫn có độ trễ do network. <br>
 * ✅ Dễ dàng mở rộng (scalable), có thể hoạt động trên nhiều server. <br>
 * ✅ Dữ liệu được lưu trữ bền vững hơn, ngay cả khi server ứng dụng bị restart. <br>
 * ✅ Hỗ trợ TTL tự động, giúp kiểm soát bộ nhớ hiệu quả. <br>
 *
 * ⚠ **Nhược điểm:** <br>
 * ❌ Vẫn có độ trễ do phải truyền tải dữ liệu qua mạng. <br>
 * ❌ Cần thiết lập Redis server, có thể yêu cầu thêm tài nguyên hệ thống. <br>
 */

public interface Redis {
    void set(String key, String value);
    void set(String key, String value, long ttl);
    String get(String key);
    void delete(String key);
    Long increment(String key);
    void hSet(String key, String hash, String value);
    Set<Object> hGetAll(String key);
    boolean singleRequest(String key, long ttl);
    boolean hashExists(String key, String hashKey);
    String hashGet(String key, String hashKey);
    void hashDelete(String key, Object... hashKey);
}
