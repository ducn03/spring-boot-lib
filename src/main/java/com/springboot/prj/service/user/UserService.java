package com.springboot.prj.service.user;

import com.springboot.lib.dto.PagingData;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;

import java.util.List;

public interface UserService {

    /**
     * @param pagingData - thông tin phân trang
     */
    List<UserDTO> getUsers(PagingData pagingData);

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
