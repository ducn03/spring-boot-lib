package com.springboot.prj.features.user;

import com.springboot.jpa.domain.User;
import com.springboot.lib.constant.RestConstant;
import com.springboot.lib.exception.AppThrower;
import com.springboot.lib.exception.ErrorCodes;
import com.springboot.lib.helper.HttpHelper;
import com.springboot.lib.service.controller.ControllerService;
import com.springboot.lib.utils.NumberUtils;
import com.springboot.prj.service.user.UserDataService;
import com.springboot.prj.service.user.request.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class UserController {
    private final ControllerService controllerService;
    private final UserDataService userDataService;

    public UserController(ControllerService controllerService, UserDataService userDataService) {
        this.controllerService = controllerService;
        this.userDataService = userDataService;
    }

    public ServerResponse getUsers(ServerRequest request){
        String pageIndexStr = request.param(RestConstant.PAGE.PAGE_INDEX).toString();
        String pageSizeStr = request.param(RestConstant.PAGE.PAGE_SIZE).toString();

        int pageIndex = NumberUtils.tryToGetInteger(pageIndexStr);
        int pageSize = NumberUtils.tryToGetInteger(pageSizeStr);

        if (pageSize == 0) pageSize = RestConstant.PAGE.PAGE_SIZE_DEFAULT;
        Page<User> userPage = userDataService.getUsers(pageIndex, pageSize);
        return controllerService.success(userDataService.getUsers(userPage), userPage);
    }

    public ServerResponse getUser(ServerRequest request){
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        return controllerService.success(userDataService.getUser(userId));
    }

    public ServerResponse create(ServerRequest request){
        UserRequest userRequest = HttpHelper.body(request, UserRequest.class);
        if (userRequest == null) AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
        return controllerService.success(userDataService.create(userRequest));
    }

    public ServerResponse update(ServerRequest request){
        UserRequest userRequest = HttpHelper.body(request, UserRequest.class);
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        if (userRequest == null) AppThrower.ep(ErrorCodes.SYSTEM.BAD_REQUEST);
        return controllerService.success(userDataService.update(userRequest, userId));
    }

    public ServerResponse delete(ServerRequest request){
        String userIdStr = request.pathVariable(RestConstant.USER.USER_ID);
        long userId = NumberUtils.tryToGetLong(userIdStr);
        return controllerService.success(userDataService.delete(userId));
    }
}
