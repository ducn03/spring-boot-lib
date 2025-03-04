package com.springboot.prj.service.user;

import com.springboot.jpa.domain.User;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserDataService {
    /**
     * Lấy danh sách người dùng theo page
     * @return
     */
    List<UserDTO> getUsers(Page<User> page);

    /**
     * @param pageIndex Trang - bắt đầu với 0
     * @param pageSize kích thước phần tử trong trang
     */
    Page<User> getUsers(int pageIndex, int pageSize);

    /**
     * Lấy thông tin chi tiết của người dùng
     * @param userId Mã định danh của người dùng
     * @return Trả về 1 obj DTO
     */
    UserDTO getUser(long userId);

    /**
     * Tạo người dùng
     * @param userRequest - payload
     * @return
     */
    UserDTO create(UserRequest userRequest);

    /**
     * Sửa thông tin người dùng
     * @param userRequest - payload
     * @return
     */
    UserDTO update(UserRequest userRequest, long userId);

    /**
     * Xóa thông tin người dùng
     * @param userId - Mã định danh của người dùng
     */
    UserDTO delete(long userId);
}
