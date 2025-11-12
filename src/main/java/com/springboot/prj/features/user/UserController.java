package com.springboot.prj.features.user;

import com.springboot.lib.aop.LogActivity;
import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.dto.PagingData;
import com.springboot.lib.exception.AppException;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.ControllerHelper;
import com.springboot.prj.route.RouteConstant;
import com.springboot.prj.service.user.UserService;
import com.springboot.prj.service.user.cache.UsersCache;
import com.springboot.prj.service.user.dto.UserDTO;
import com.springboot.prj.service.user.request.UserRequest;
import com.springboot.prj.service.user.request.UserSearchRequest;
import lombok.CustomLog;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CustomLog
public class UserController {
    private final UserService userService;
    private final UsersCache usersCache;

    public UserController(UserService userService, UsersCache usersCache) {
        this.userService = userService;
        this.usersCache = usersCache;
    }

    @LogActivity
    @GetMapping(RouteConstant.APP.USER.USERS)
    public ResponseEntity<?> getUsers(UserSearchRequest searchRequest){
        PagingData pagingData = new PagingData();
        pagingData.setPageIndex(searchRequest.getPageIndex());
        pagingData.setPageSize(searchRequest.getPageSize());

        String lang = LocaleContextHolder.getLocale().getLanguage();
        log.info("Lang: " + lang);

        List<UserDTO> users;
        if (searchRequest.isHaveCache()) {
            users = usersCache.get();
            return ControllerHelper.success(users);
        }

        users = userService.getUsers(pagingData);
        return ControllerHelper.success(users, pagingData);
    }

    @LogActivity
    @GetMapping(RouteConstant.APP.USER.USER)
    public ResponseEntity<?> getUser(@PathVariable long userId){
        return ControllerHelper.success(userService.getUser(userId));
    }

    @LogActivity
    @PostMapping(RouteConstant.APP.USER.CREATE)
    public ResponseEntity<?> create(@RequestBody UserRequest userRequest){
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return ControllerHelper.success(userService.create(userRequest));
    }

    @LogActivity
    @PostMapping(RouteConstant.APP.USER.UPDATE)
    public ResponseEntity<?> update(@RequestBody UserRequest userRequest,
                                    @PathVariable(RestConstant.USER.USER_ID) long userId){
        if (userRequest == null) {
            throw new AppException(ErrorCodes.SYSTEM.BAD_REQUEST);
        }
        return ControllerHelper.success(userService.update(userRequest, userId));
    }

    @PostMapping(RouteConstant.APP.USER.STATUS_CHANGE)
    public ResponseEntity<?> delete(@PathVariable long userId){
        return ControllerHelper.success(userService.delete(userId));
    }
}
