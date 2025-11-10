package com.springboot.prj.features.user;

import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.service.controller.ControllerService;
import com.springboot.prj.route.RouteConstant;
import com.springboot.prj.service.user.UserService;
import com.springboot.prj.service.user.cache.UsersCache;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import com.springboot.prj.service.user.request.UserSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {
    private final ControllerService controllerService;
    private final UserService userService;
    private final UsersCache usersCache;

    public UserController(ControllerService controllerService, UserService userService, UsersCache usersCache) {
        this.controllerService = controllerService;
        this.userService = userService;
        this.usersCache = usersCache;
    }

    @GetMapping(RouteConstant.APP.USER.USERS)
    public ResponseEntity<?> getUsers(UserSearchRequest searchRequest){
        PagingData pagingData = new PagingData();
        pagingData.setPageIndex(searchRequest.getPageIndex());
        pagingData.setPageSize(searchRequest.getPageSize());

        List<UserDTO> users = new ArrayList<>();

        if (searchRequest.isHaveCache()) {
            users = usersCache.get();
            return controllerService.success(users);
        }

        users = userService.getUsers(pagingData);
        return controllerService.success(users, pagingData);
    }

    @GetMapping(RouteConstant.APP.USER.USER)
    public ResponseEntity<?> getUser(@PathVariable long userId){
        return controllerService.success(userService.getUser(userId));
    }

    @PostMapping(RouteConstant.APP.USER.CREATE)
    public ResponseEntity<?> create(@RequestBody UserRequest userRequest){
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return controllerService.success(userService.create(userRequest));
    }

    @PostMapping(RouteConstant.APP.USER.UPDATE)
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest,
                                    @PathVariable(RestConstant.USER.USER_ID) long userId){
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return controllerService.success(userService.update(userRequest, userId));
    }

    @PostMapping(RouteConstant.APP.USER.STATUS_CHANGE)
    public ResponseEntity<?> delete(@PathVariable long userId){
        return controllerService.success(userService.delete(userId));
    }
}
